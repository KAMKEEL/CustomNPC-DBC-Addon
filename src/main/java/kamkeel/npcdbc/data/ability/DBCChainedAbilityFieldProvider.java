package kamkeel.npcdbc.data.ability;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcs.controllers.data.ability.ChainedAbility;
import kamkeel.npcs.controllers.data.ability.IChainedAbilityFieldProvider;
import noppes.npcs.client.gui.builder.FieldDef;

import java.util.List;

/**
 * Injects DBC-specific tabs into chained ability configuration GUI:
 * - "Icon" tab for icon texture and UV settings
 */
@SideOnly(Side.CLIENT)
public class DBCChainedAbilityFieldProvider implements IChainedAbilityFieldProvider {
    private static final String TAB_ICON = "Icon";

    @Override
    public void addFieldDefinitions(ChainedAbility chain, List<FieldDef> defs) {
        addIconFields(chain, defs);
    }

    private void addIconFields(ChainedAbility chain, List<FieldDef> defs) {
        AbilityIconData icon = AbilityIconData.fromChainedAbility(chain);

        // Texture path (URL or resource location)
        defs.add(FieldDef.stringField("gui.texture", icon::getTexture, icon::setTexture)
            .tab(TAB_ICON));

        // UV coordinates section
        defs.add(FieldDef.section("ability.icon.section.uv")
            .tab(TAB_ICON));
        defs.add(FieldDef.intField("ability.icon.x", icon::getIconX, icon::setIconX)
            .tab(TAB_ICON).range(0, 4096));
        defs.add(FieldDef.intField("ability.icon.y", icon::getIconY, icon::setIconY)
            .tab(TAB_ICON).range(0, 4096));

        // Dimensions section
        defs.add(FieldDef.section("gui.size")
            .tab(TAB_ICON));
        defs.add(FieldDef.intField("gui.width", icon::getWidth, icon::setWidth)
            .tab(TAB_ICON).range(1, 256));
        defs.add(FieldDef.intField("gui.height", icon::getHeight, icon::setHeight)
            .tab(TAB_ICON).range(1, 256));
        defs.add(FieldDef.floatField("gui.scale", icon::getScale, icon::setScale)
            .tab(TAB_ICON).range(0.1f, 10.0f));
    }
}
