package com.ghosts.jujutsureborn.client;

import com.ghosts.jujutsureborn.JujutsuReborn;
import com.ghosts.jujutsureborn.client.renderer.RockRenderer;
import com.ghosts.jujutsureborn.init.JujutsuRebornEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = JujutsuReborn.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

   @SubscribeEvent
   public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
      event.registerEntityRenderer(JujutsuRebornEntities.ROCK.get(), RockRenderer::new);
   }
}