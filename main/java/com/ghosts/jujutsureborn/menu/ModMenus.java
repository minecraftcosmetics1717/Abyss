package com.ghosts.jujutsureborn.menu;

import com.ghosts.jujutsureborn.JujutsuReborn;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, JujutsuReborn.MODID);

    public static final RegistryObject<MenuType<ShadowInventoryMenu>> SHADOW_INVENTORY =
            MENUS.register("shadow_inventory",
                    () -> IForgeMenuType.create(ShadowInventoryMenu::new));
    public static final RegistryObject<MenuType<LifeCharmsMenu>> LIFE_CHARMS =
            MENUS.register("life_charms", () -> IForgeMenuType.create(LifeCharmsMenu::new));
    public static final RegistryObject<MenuType<InnerselfInventoryMenu>> INNERSELF_INVENTORY =
            MENUS.register("innerself_inventory",
                    () -> IForgeMenuType.create(InnerselfInventoryMenu::new));
}