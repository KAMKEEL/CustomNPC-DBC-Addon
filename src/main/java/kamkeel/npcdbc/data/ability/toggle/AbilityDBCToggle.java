package kamkeel.npcdbc.data.ability.toggle;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.constants.DBCSettings;
import kamkeel.npcdbc.data.ability.AbilityIconData;
import kamkeel.npcdbc.util.DBCSettingsUtil;
import kamkeel.npcs.controllers.data.ability.Ability;
import kamkeel.npcs.controllers.data.ability.AbilityVariant;
import kamkeel.npcs.controllers.data.ability.LockMovementType;
import kamkeel.npcs.controllers.data.ability.UserType;
import kamkeel.npcs.controllers.data.telegraph.TelegraphType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import noppes.npcs.client.gui.builder.FieldDef;
import noppes.npcs.util.ValueUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Base class for DBC toggle abilities (Ki Fist, Swoop, etc.).
 * Pure on/off toggles for players — no execution, no ticking, no phases.
 */
public class AbilityDBCToggle extends Ability {

    private SettingType settingType = SettingType.NONE;
    private int mode = 0;

    public AbilityDBCToggle() {
        this.typeId = "ability.npcdbc.dbc_toggle";
        this.name = "DBC Toggle";
        this.toggleable = true;
        this.allowedBy = UserType.PLAYER_ONLY;
        this.telegraphType = TelegraphType.NONE;
        this.windUpTicks = 0;
        this.setIconTexture(CustomNpcPlusDBC.ID + ":textures/gui/ability_icons.png");
        this.setIconWidth(48);
        this.setIconHeight(48);
        this.setIconScale(1.5f);
    }

    public enum SettingType {
        NONE(-1, "None"),
        KAIOKEN(DBCSettings.KAIOKEN_ENABLED, "Kaioken"),
        FORM_SELECTION(DBCSettings.FORM_SELECTION, "Form Selection"),
        SWOOP(DBCSettings.DODGE_ENABLED, "Swoop"),
        FUSION(DBCSettings.FUSION_ENABLED, "Fusion"),
        POTENTIAL_UNLEASHED(DBCSettings.POTENTIAL_UNLEASHED, "Potential Unleashed"),
        KI_FIST(DBCSettings.KI_FIST, "Ki Fist"),
        KI_PROTECTION(DBCSettings.KI_PROTECTION, "Ki Protection"),
        ULTRA_INSTINCT(DBCSettings.ULTRA_INSTINCT, "Ultra Instinct"),
        FRIENDLY_FIST(DBCSettings.FRIENDLY_FIST, "Friendly Fist"),
        KI_WEAPON(DBCSettings.KI_WEAPON_TOGGLE, "Ki Weapon"),
        INSTANT_TRANSMISSION_RANGE(DBCSettings.INSTANT_TRANSMISSION_SHORT_RANGE, "IT Range Mode"),
        INSTANT_TRANSMISSION_SURROUND(DBCSettings.INSTANT_TRANSMISSION_LONG_RANGE, "IT Surround Mode"),
        GOD_OF_DESTRUCTION(DBCSettings.GOD_OF_DESTRUCTION, "God Of Destruction");

        public final int id;
        public final String displayKey;

        SettingType(int id, String displayKey) {
            this.id = id;
            this.displayKey = displayKey;
        }

        public String getDisplayKey() {
            return displayKey;
        }

        public static String[] getDisplayKeys() {
            String[] keys = new String[SettingType.values().length];
            for (int i = 0; i < SettingType.values().length; i++) {
                keys[i] = SettingType.values()[i].getDisplayKey();
            }
            return keys;
        }

        public static SettingType fromId(int id) {
            if (!DBCSettingsUtil.isSetting(id))
                return NONE;

            for (SettingType setting : SettingType.values()) {
                if (setting.id == id) {
                    return setting;
                }
            }

            return NONE;
        }
    }

    @Override
    public List<AbilityVariant> getVariants() {
        return Arrays.asList(
            new AbilityVariant("ability.npcdbc.friendly_fist", a -> {
                AbilityDBCToggle toggle = (AbilityDBCToggle) a;
                a.setName("Friendly Fist");
                toggle.setSettingType(SettingType.FRIENDLY_FIST);
                toggle.setIconX(0);
                toggle.setIconY(0);
            }),
            new AbilityVariant("ability.npcdbc.ki_blade", a -> {
                AbilityDBCToggle toggle = (AbilityDBCToggle) a;
                a.setName("Ki Blade");
                toggle.setSettingType(SettingType.KI_WEAPON);
                toggle.setMode(0);
                toggle.setIconX(288);
                toggle.setIconY(0);
            }),
            new AbilityVariant("ability.npcdbc.ki_fist", a -> {
                AbilityDBCToggle toggle = (AbilityDBCToggle) a;
                a.setName("Ki Fist");
                toggle.setSettingType(SettingType.KI_FIST);
                toggle.setIconX(192);
                toggle.setIconY(0);
            }),
            new AbilityVariant("ability.npcdbc.ki_protection", a -> {
                AbilityDBCToggle toggle = (AbilityDBCToggle) a;
                a.setName("Ki Protection");
                toggle.setSettingType(SettingType.KI_PROTECTION);
                toggle.setIconX(240);
                toggle.setIconY(0);
            }),
            new AbilityVariant("ability.npcdbc.ki_scythe", a -> {
                AbilityDBCToggle toggle = (AbilityDBCToggle) a;
                a.setName("Ki Scythe");
                toggle.setSettingType(SettingType.KI_WEAPON);
                toggle.setMode(1);
                toggle.setIconX(336);
                toggle.setIconY(0);
            }),
            new AbilityVariant("ability.npcdbc.swoop", a -> {
                AbilityDBCToggle toggle = (AbilityDBCToggle) a;
                a.setName("Swoop");
                toggle.setSettingType(SettingType.SWOOP);
                toggle.setIconX(336);
                toggle.setIconY(0);
            })
        );
    }

    @Override
    public boolean hasDamage() {
        return false;
    }

    protected SettingType[] getIllegalSettings() {
        return new SettingType[]{
            SettingType.FORM_SELECTION,
            SettingType.INSTANT_TRANSMISSION_RANGE,
            SettingType.INSTANT_TRANSMISSION_SURROUND
        };
    }

    protected boolean isIllegalSetting(int setting) {
        for (SettingType s : getIllegalSettings()) {
            if (s.id == setting) return true;
        }

        return false;
    }

    // ═══════════════════════════════════════════════════════════════════
    // TOGGLE
    // ═══════════════════════════════════════════════════════════════════

    @Override
    public void onToggleOn(EntityLivingBase caster) {
        if (caster instanceof EntityPlayer)
            onToggle((EntityPlayer) caster, true);
    }

    @Override
    public void onToggleOff(EntityLivingBase caster) {
        if (caster instanceof EntityPlayer)
            onToggle((EntityPlayer) caster, false);
    }

    protected void onToggle(EntityPlayer player, boolean newState) {
        if (getSettingType() == SettingType.NONE)
            return;

        if (getSettingType() == SettingType.KI_WEAPON) {
            DBCSettingsUtil.setKiWeapon(player, newState, mode);
            return;
        }

        DBCSettingsUtil.setEnabled(player, getSetting(), newState);
    }

    protected boolean showModeWhen() {
        return getSettingType() == SettingType.KI_WEAPON ||
            getSettingType() == SettingType.INSTANT_TRANSMISSION_RANGE ||
            getSettingType() == SettingType.INSTANT_TRANSMISSION_SURROUND;
    }

    protected int getMaxMode() {
        return 1;
    }

    // ═══════════════════════════════════════════════════════════════════
    // GETTERS AND SETTERS
    // ═══════════════════════════════════════════════════════════════════

    public SettingType getSettingType() {
        return settingType;
    }

    public int getSetting() {
        return settingType.id;
    }

    public void setSettingType(SettingType settingType) {
        this.settingType = settingType;
    }

    public void setSetting(int setting) {
        if (!DBCSettingsUtil.isSetting(setting) || isIllegalSetting(setting)) return;

        this.settingType = SettingType.fromId(setting);
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = ValueUtil.clamp(mode, 0, 1);
    }

    // ═══════════════════════════════════════════════════════════════════
    // FIELD DEFINITIONS
    // ═══════════════════════════════════════════════════════════════════

    @Override
    public void getAbilityDefinitions(List<FieldDef> defs) {
        defs.add(FieldDef.stringEnumField(
            "ability.settingType",
            Arrays.stream(SettingType.values())
                .filter(t -> !isIllegalSetting(t.id))
                .map(SettingType::getDisplayKey)
                .toArray(String[]::new),

            () -> this.getSettingType().getDisplayKey(),

            v -> {
                for (SettingType t : SettingType.values()) {
                    if (t.getDisplayKey().equals(v)) {
                        this.setSettingType(t);
                        break;
                    }
                }
            }
        ));

        defs.add(FieldDef.intField("ability.mode", this::getMode, this::setMode)
            .range(0, this.getMaxMode())
            .visibleWhen(this::showModeWhen));
    }


    // ═══════════════════════════════════════════════════════════════════
    // EXECUTION — blocked; toggle abilities never execute
    // ═══════════════════════════════════════════════════════════════════

    @Override
    public void start(EntityLivingBase target) {
    }

    @Override
    public void onExecute(EntityLivingBase caster, EntityLivingBase target) {
    }

    @Override
    public void onActiveTick(EntityLivingBase caster, EntityLivingBase target, int tick) {
    }

    @Override
    public void writeTypeNBT(NBTTagCompound nbt) {
        nbt.setInteger("setting", settingType.id);
        nbt.setInteger("mode", mode);
    }

    @Override
    public void readTypeNBT(NBTTagCompound nbt) {
        int s = nbt.getInteger("setting");

        if (isIllegalSetting(s)) {
            this.settingType = SettingType.NONE;
        } else {
            this.settingType = SettingType.fromId(s);
        }

        this.mode = nbt.getInteger("mode");
    }

    // ═══════════════════════════════════════════════════════════════════
    // ICON HELPERS
    // ═══════════════════════════════════════════════════════════════════

    private NBTTagCompound iconComp() {
        if (!customData.hasKey(AbilityIconData.NBT_KEY, Constants.NBT.TAG_COMPOUND))
            customData.setTag(AbilityIconData.NBT_KEY, new NBTTagCompound());
        return customData.getCompoundTag(AbilityIconData.NBT_KEY);
    }

    public String getIconTexture() {
        return iconComp().getString("Texture");
    }

    public void setIconTexture(String t) {
        iconComp().setString("Texture", t);
    }

    public int getIconX() {
        return iconComp().getInteger("IconX");
    }

    public void setIconX(int x) {
        iconComp().setInteger("IconX", x);
    }

    public int getIconY() {
        return iconComp().getInteger("IconY");
    }

    public void setIconY(int y) {
        iconComp().setInteger("IconY", y);
    }

    public int getIconWidth() {
        return iconComp().getInteger("Width");
    }

    public void setIconWidth(int w) {
        iconComp().setInteger("Width", Math.max(32, w));
    }

    public int getIconHeight() {
        return iconComp().getInteger("Height");
    }

    public void setIconHeight(int h) {
        iconComp().setInteger("Height", Math.max(32, h));
    }

    public float getIconScale() {
        return iconComp().getFloat("Scale");
    }

    public void setIconScale(float s) {
        iconComp().setFloat("Scale", s <= 0 ? 1.0f : s);
    }
}
