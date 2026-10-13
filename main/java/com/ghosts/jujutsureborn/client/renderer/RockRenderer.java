package com.ghosts.jujutsureborn.client.renderer;

import com.ghosts.jujutsureborn.entity.RockEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

public class RockRenderer extends ThrownItemRenderer<RockEntity> {

   public RockRenderer(EntityRendererProvider.Context context) {
      super(context, 1.0F, true); // 1.0 = normal size, true = fullbright
   }
}