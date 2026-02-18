package kamkeel.npcdbc.client.gui.hud.abilityWheel;

import kamkeel.npcdbc.api.Color;
import kamkeel.npcdbc.client.gui.hud.WheelSegment;
import kamkeel.npcdbc.client.gui.hud.abilityWheel.icon.AbilityIcon;
import kamkeel.npcdbc.data.AbilityWheelData;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.packets.player.ability.DBCSaveAbilityWheel;
import kamkeel.npcdbc.network.packets.player.ability.DBCSelectAbility;
import kamkeel.npcdbc.network.packets.player.ability.DBCToggleAbilityAction;
import kamkeel.npcs.controllers.data.ability.Ability;
import kamkeel.npcs.controllers.data.ability.ChainedAbility;
import kamkeel.npcs.controllers.data.ability.IAbilityAction;
import kamkeel.npcs.controllers.AbilityController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import noppes.npcs.controllers.data.PlayerData;
import org.lwjgl.opengl.GL11;

/**
 * A segment in the Ability Wheel that represents a single ability slot.
 * Owns its own AbilityWheelData and handles all state changes atomically
 * (matching FormWheelSegment pattern).
 */
public class AbilityWheelSegment extends WheelSegment {
    public HUDAbilityWheel parent;
    public AbilityWheelData data = new AbilityWheelData();
    public Ability ability = null;
    public IAbilityAction action = null;
    private AbilityIcon icon = null;

    public AbilityWheelSegment(HUDAbilityWheel parent, int index) {
        this(parent, 0, 0, index);
    }

    public AbilityWheelSegment(HUDAbilityWheel parent, int posX, int posY, int index) {
        super(index);
        this.parent = parent;
        this.posX = posX;
        this.posY = posY;
        this.index = data.slot = index;
    }

    public void selectAbility() {
        if (data.isEmpty()) return;
        if (ability != null && ability.isToggleable()) {
            // Toggle abilities perform their action directly - no cooldown, no events
            DBCPacketHandler.Instance.sendToServer(new DBCToggleAbilityAction(data.abilityKey));
            return;
        }
        DBCPacketHandler.Instance.sendToServer(new DBCSelectAbility(data.abilityKey));
    }

    public void setAbility(String abilityKey, boolean updateServer) {
        data.abilityKey = (abilityKey != null) ? abilityKey : "";
        resolveAction();
        if (updateServer)
            DBCPacketHandler.Instance.sendToServer(new DBCSaveAbilityWheel(index, data));
    }

    public void setAbility(AbilityWheelData data, boolean updateServer) {
        this.data = data;
        resolveAction();
        if (updateServer)
            DBCPacketHandler.Instance.sendToServer(new DBCSaveAbilityWheel(index, data));
    }

    private void resolveAction() {
        ability = null;
        action = null;
        icon = null;
        if (data.isEmpty() || AbilityController.Instance == null) return;

        if (data.isChainKey()) {
            action = AbilityController.Instance.resolveChainedAbility(data.getResolveKey());
        } else {
            Ability resolved = AbilityController.Instance.resolveAbility(data.abilityKey);
            ability = resolved;
            action = resolved;
        }
        if (ability != null) {
            icon = new AbilityIcon(ability);
        } else if (action instanceof ChainedAbility) {
            icon = new AbilityIcon((ChainedAbility) action);
        } else {
            icon = null;
        }
    }

    public void updateIndex(int newIndex) {
        this.index = newIndex;
        this.data.slot = newIndex;
    }

    public void removeAbility() {
        data.reset();
        ability = null;
        action = null;
        icon = null;
        DBCPacketHandler.Instance.sendToServer(new DBCSaveAbilityWheel(index, data));
        if (parent.hoveredSlot == index)
            parent.selectSlot(-1);
        parent.timeClosedSubGui = Minecraft.getSystemTime();
    }

    @Override
    public void draw(FontRenderer fontRenderer) {
        float hover = getSegmentScale();
        float open = getOpenScale();

        float finalScale = open * (0.85f + hover * 0.15f);

        currentColor = Color.lerpRGBA(NOT_HOVERED, HOVERED, hover);
        currentColor.glColor();

        GL11.glPushMatrix();
        GL11.glTranslatef(posX, posY, 0);
        GL11.glScalef(finalScale, finalScale, 1);
        GL11.glTranslatef(-posX, -posY, 0);

        drawIndexedTexture();
        drawWheelItem(fontRenderer);

        GL11.glPopMatrix();
    }

    @Override
    protected void drawWheelItem(FontRenderer fontRenderer) {
        if (data.isEmpty())
            return;

        GL11.glPushMatrix();

        if (icon != null) {
            icon.draw();
        }

        GL11.glTranslatef(0, 5, 0);

        drawCenteredString(fontRenderer, getAbilityName(), 0, (icon != null ? icon.height / 2 + 5 : 0), getNameColor());

        GL11.glPopMatrix();
    }

    public String getAbilityName() {
        if (ability != null) return ability.getDisplayName();
        if (action instanceof ChainedAbility) return ((ChainedAbility) action).getDisplayName();
        return !data.isEmpty() ? data.abilityKey : "";
    }

    /**
     * Get the color for the ability name.
     * Toggleable abilities show green if active, red if inactive.
     * Regular abilities show white.
     */
    private int getNameColor() {
        if (ability != null && ability.isToggleable()) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.thePlayer != null) {
                PlayerData playerData = PlayerData.get(mc.thePlayer);
                if (playerData != null && playerData.abilityData != null
                    && playerData.abilityData.isAbilityToggled(data.abilityKey)) {
                    return 0xFF55FF55; // Green - active
                }
            }
            return 0xFFFF5555; // Red - inactive
        }
        return 0xFFFFFFFF; // White - normal
    }
}
