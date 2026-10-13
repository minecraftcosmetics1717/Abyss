package com.ghosts.jujutsureborn.mixin;

import com.ghosts.jujutsureborn.client.CameraTilt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Camera;
import net.minecraft.client.Options;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraTiltMixin {

    @Shadow
    private Quaternionf rotation;

    // Forge 1.20.1 method name is setup(...)
    @Inject(method = "setup", at = @At("TAIL"))
    private void jreborn$applyCameraTilt(
            BlockGetter level,
            Entity focusedEntity,
            boolean thirdPerson,
            boolean inverseView,
            float partialTick,
            CallbackInfo ci
    ) {
        if (thirdPerson) return;
        if (focusedEntity == null) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.options == null) return;

        Options options = mc.options;
        boolean isSprinting = focusedEntity.isSprinting();

        float maxAngle = isSprinting ? 4.5f : 3.0f;
        float maxPitch = isSprinting ? 2.5f : 1.0f;
        float camSpeed = isSprinting ? 0.07f : 0.03f;

        // Roll (A / D)
        if (options.keyLeft.isDown() && !options.keyRight.isDown()) {
            CameraTilt.targetRoll = maxAngle;
        } else if (options.keyRight.isDown() && !options.keyLeft.isDown()) {
            CameraTilt.targetRoll = -maxAngle;
        } else {
            CameraTilt.targetRoll = 0.0f;
        }

        // Pitch (W / S)
        if (options.keyUp.isDown() && !options.keyDown.isDown()) {
            CameraTilt.targetPitch = -maxPitch;
        } else if (options.keyDown.isDown() && !options.keyUp.isDown()) {
            CameraTilt.targetPitch = maxPitch;
        } else {
            CameraTilt.targetPitch = 0.0f;
        }

        // Smooth interpolation
        CameraTilt.currentPitch += (CameraTilt.targetPitch - CameraTilt.currentPitch) * camSpeed;
        CameraTilt.currentRoll  += (CameraTilt.targetRoll  - CameraTilt.currentRoll)  * camSpeed;

        // Apply rotation
        if (Math.abs(CameraTilt.currentRoll) > 0.001f) {
            this.rotation.rotateZ((float) Math.toRadians(CameraTilt.currentRoll));
        }
        if (Math.abs(CameraTilt.currentPitch) > 0.001f) {
            this.rotation.rotateX((float) Math.toRadians(CameraTilt.currentPitch));
        }
    }
}