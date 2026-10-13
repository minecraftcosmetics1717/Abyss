package com.ghosts.jujutsureborn.mixin;

import net.mcreator.jujutsucraft.init.JujutsucraftModEntities;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.mcreator.jujutsucraft.init.JujutsucraftModParticleTypes;
import net.mcreator.jujutsucraft.procedures.RangeAttackProcedure;
import net.mcreator.jujutsucraft.procedures.ReturnEntitySizeProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "net.mcreator.jujutsucraft.procedures.DivergentFistProcedure", remap = false)
public class DivergentFistBlackFlashMixin {

    private static final int WINDOW_END_TICK = 8; // 0.40s resolve
    private static final int BF_COMBAT_COOLDOWN_TICKS = 50;

    @Inject(method = "execute", at = @At("HEAD"), cancellable = true, remap = false)
    private static void jreborn$divergentFist(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci) {
        if (entity == null) return;

        CompoundTag nbt = entity.getPersistentData();

        // Phase 1 = vanilla
        if (nbt.getDouble("cnt7") == 0.0D) {
            return;
        }

        // Handle phase 2 ourselves
        ci.cancel();

        nbt.putBoolean("jreborn_df_active", true);

        // Open timing window once
        if (!nbt.getBoolean("jreborn_df_window_open")) {
            nbt.putBoolean("jreborn_df_window_open", true);
            nbt.putBoolean("jreborn_df_bf", false);
            nbt.putBoolean("jreborn_df_early", false);
            nbt.putInt("jreborn_df_ticks", 0);

            // Allow re-press during the real BF window
            if (entity instanceof LivingEntity living) {
                MobEffect combatCd = ForgeRegistries.MOB_EFFECTS.getValue(
                        new ResourceLocation("jujutsucraft", "cooldown_time_combat")
                );
                if (combatCd != null && living.hasEffect(combatCd)) {
                    living.removeEffect(combatCd);
                }
            }
        }

        int ticks = nbt.getInt("jreborn_df_ticks") + 1;
        nbt.putInt("jreborn_df_ticks", ticks);
        nbt.putDouble("cnt1", (double) ticks);

        // Same cancel as original
        if (entity instanceof LivingEntity living) {
            MobEffect physicalGifted = JujutsucraftModMobEffects.PHYSICAL_GIFTED_EFFECT.get();
            if (physicalGifted != null && living.hasEffect(physicalGifted)) {
                clearDfState(nbt);
                nbt.putDouble("skill", 0.0D);
                return;
            }
        }

        // Wait until 0.40s
        if (ticks < WINDOW_END_TICK) {
            return;
        }

        boolean blackFlash = nbt.getBoolean("jreborn_df_bf") && !nbt.getBoolean("jreborn_df_early");

        double cnt6 = nbt.getDouble("cnt6");
        double cnt6Multiplier = 1.0D + cnt6 * 0.2D;
        double sizeSquared = Math.pow(ReturnEntitySizeProcedure.execute(entity), 2.0D);

        if (blackFlash) {
            doForcedBlackFlash(world, entity, nbt, cnt6Multiplier, sizeSquared);
        } else {
            doNormalSecondHit(world, entity, nbt, cnt6Multiplier, sizeSquared);
        }

        clearDfState(nbt);
        nbt.putDouble("skill", 0.0D);
    }

    private static void doNormalSecondHit(LevelAccessor world, Entity entity, CompoundTag nbt,
                                          double cnt6Multiplier, double sizeSquared) {
        double damage = 12.0D * cnt6Multiplier;
        double knockback = 0.25D * cnt6Multiplier;
        double range = 4.0D * sizeSquared;

        for (int i = 0; i < 11; ++i) {
            nbt.putDouble("x_pos", nbt.getDouble("x_pos") + nbt.getDouble("x_power"));
            nbt.putDouble("y_pos", nbt.getDouble("y_pos") + nbt.getDouble("y_power"));
            nbt.putDouble("z_pos", nbt.getDouble("z_pos") + nbt.getDouble("z_power"));

            nbt.putDouble("Damage", damage);
            nbt.putDouble("knockback", knockback);
            nbt.putDouble("Range", range);
            nbt.putDouble("projectile_type", 1.0D);
            nbt.putBoolean("attack", true);

            RangeAttackProcedure.execute(
                    world,
                    nbt.getDouble("x_pos"),
                    nbt.getDouble("y_pos"),
                    nbt.getDouble("z_pos"),
                    entity
            );
        }
    }

    private static void doForcedBlackFlash(LevelAccessor world, Entity entity, CompoundTag nbt,
                                           double cnt6Multiplier, double sizeSquared) {
        double cnt6 = nbt.getDouble("cnt6");

        // Strong-punch style base (multiplier applied after DamageFix in RangeAttack mixin)
        double giftFactor = 0.2D;
        if (entity instanceof LivingEntity living
                && living.hasEffect(JujutsucraftModMobEffects.PHYSICAL_GIFTED_EFFECT.get())) {
            giftFactor = 0.3D;
        }
        double strongMultiplier = 1.0D + cnt6 * giftFactor;

        nbt.putDouble("Damage", 11.0D * strongMultiplier);
        nbt.putDouble("knockback", 0.35D * (1.0D + cnt6 * 0.1D));
        nbt.putDouble("Range", Math.max(2.5D, 2.0D * Math.sqrt(sizeSquared)));
        nbt.putDouble("effect", 5.0D);
        nbt.putDouble("projectile_type", 1.0D);
        nbt.putBoolean("attack", true);

        // Mark for BF mult after DamageFix
        nbt.putBoolean("jreborn_force_bf", true);

        // Block natural BF roll (prevents a second ×4)
        double oldSkill = nbt.getDouble("skill");
        nbt.putDouble("skill", 2105.0D);

        // Aim at enemy hit by first DF punch, if still valid
        double ix;
        double iy;
        double iz;

        Entity tracked = null;
        if (nbt.hasUUID("jreborn_df_last_target") && world instanceof ServerLevel server) {
            tracked = server.getEntity(nbt.getUUID("jreborn_df_last_target"));
        }

        if (tracked instanceof LivingEntity livingTarget && livingTarget.isAlive()) {
            ix = livingTarget.getX();
            iy = livingTarget.getY() + livingTarget.getBbHeight() * 0.5D;
            iz = livingTarget.getZ();
        } else {
            Vec3 look = entity.getLookAngle();
            double dist = 2.0D;
            ix = entity.getX() + look.x * dist;
            iy = entity.getY() + entity.getBbHeight() * 0.6D + look.y * dist;
            iz = entity.getZ() + look.z * dist;
        }

        RangeAttackProcedure.execute(world, ix, iy, iz, entity);

        nbt.putDouble("skill", oldSkill);
        nbt.putBoolean("jreborn_force_bf", false);

        spawnBlackFlashEntity(world, ix, iy, iz);
        applyZone(entity);
        playBlackFlashEffects(world, ix, iy, iz);
        applyCombatCooldown(entity);
    }

    private static void spawnBlackFlashEntity(LevelAccessor world, double x, double y, double z) {
        if (!(world instanceof ServerLevel server)) return;

        EntityType<?> type = JujutsucraftModEntities.ENTITY_BLACK_FLASH.get();
        if (type == null) return;

        Entity spawned = type.spawn(server, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
        if (spawned != null) {
            spawned.setYRot(server.random.nextFloat() * 360.0F);
        }
    }

    private static void applyZone(Entity entity) {
        if (!(entity instanceof LivingEntity living)) return;
        MobEffect zone = JujutsucraftModMobEffects.ZONE.get();
        if (zone != null) {
            living.addEffect(new MobEffectInstance(zone, 6000, 0, true, true));
        }
    }

    private static void applyCombatCooldown(Entity entity) {
        if (!(entity instanceof LivingEntity living)) return;

        MobEffect combatCd = ForgeRegistries.MOB_EFFECTS.getValue(
                new ResourceLocation("jujutsucraft", "cooldown_time_combat")
        );
        if (combatCd == null) {
            combatCd = JujutsucraftModMobEffects.COOLDOWN_TIME_COMBAT.get();
        }
        if (combatCd != null) {
            living.addEffect(new MobEffectInstance(
                    combatCd,
                    BF_COMBAT_COOLDOWN_TICKS,
                    0,
                    false,
                    false
            ));
        }
    }

    private static void playBlackFlashEffects(LevelAccessor world, double x, double y, double z) {
        BlockPos pos = BlockPos.containing(x, y, z);

        if (world instanceof Level level && !level.isClientSide()) {
            SoundEvent shock = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("jujutsucraft", "electric_shock"));
            SoundEvent thunder = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.lightning_bolt.thunder"));
            SoundEvent impact = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.lightning_bolt.impact"));

            if (shock != null) {
                level.playSound((Player) null, pos, shock, SoundSource.NEUTRAL, 1.0F, 0.75F);
                level.playSound((Player) null, pos, shock, SoundSource.NEUTRAL, 1.0F, 1.0F);
                level.playSound((Player) null, pos, shock, SoundSource.NEUTRAL, 1.0F, 1.25F);
            }
            if (thunder != null) {
                level.playSound((Player) null, pos, thunder, SoundSource.NEUTRAL, 1.0F, 1.0F);
            }
            if (impact != null) {
                level.playSound((Player) null, pos, impact, SoundSource.NEUTRAL, 1.0F, 1.0F);
            }
        }

        if (world instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.ENCHANTED_HIT, x, y, z, 15, 0.5D, 0.5D, 0.5D, 0.0D);

            var bf = JujutsucraftModParticleTypes.PARTICLE_BLACK_FLASH_1.get();
            server.sendParticles(bf, x, y, z, 35, 0.8D, 1.0D, 0.8D, 0.0D);

            for (int i = 0; i < 16; ++i) {
                double ox = (server.random.nextDouble() - 0.5D) * 3.0D;
                double oy = server.random.nextDouble() * 2.0D;
                double oz = (server.random.nextDouble() - 0.5D) * 3.0D;
                server.sendParticles(bf, x + ox, y + oy, z + oz, 3, 0.15D, 0.15D, 0.15D, 0.0D);
            }
        }
    }

    private static void clearDfState(CompoundTag nbt) {
        nbt.putBoolean("jreborn_df_active", false);
        nbt.putBoolean("jreborn_df_window_open", false);
        nbt.putBoolean("jreborn_df_bf", false);
        nbt.putBoolean("jreborn_df_early", false);
        nbt.putBoolean("jreborn_force_bf", false);
        nbt.putInt("jreborn_df_ticks", 0);
        nbt.putDouble("cnt7", 0.0D);
        nbt.remove("jreborn_df_last_target");
    }
}