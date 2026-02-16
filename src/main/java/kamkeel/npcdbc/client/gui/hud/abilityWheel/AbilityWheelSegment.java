package kamkeel.npcdbc.client.gui.hud.abilityWheel;

import kamkeel.npcdbc.api.Color;
import kamkeel.npcdbc.client.gui.hud.WheelSegment;
import kamkeel.npcdbc.client.gui.hud.abilityWheel.icon.AbilityIcon;
import kamkeel.npcdbc.config.ConfigDBCClient;
import kamkeel.npcdbc.data.ability.toggle.DBCToggleAbility;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.packets.player.ability.DBCSelectAbility;
import kamkeel.npcs.controllers.data.ability.Ability;
import kamkeel.npcs.controllers.data.ability.AbilityController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.glScaled;
import static org.lwjgl.opengl.GL11.glTranslatef;

/**
 * A segment in the Ability Wheel that represents a single ability slot.
 */
public class AbilityWheelSegment extends WheelSegment {
    public HUDAbilityWheel parent;
    public String abilityKey = null;
    public Ability ability = null;
    private AbilityIcon icon = null;

    public AbilityWheelSegment(HUDAbilityWheel parent, int index) {
        this(parent, 0, 0, index);
    }

    public AbilityWheelSegment(HUDAbilityWheel parent, int posX, int posY, int index) {
        super(index);
        this.parent = parent;
        this.posX = posX;
        this.posY = posY;
        this.index = index;
    }

    public void selectAbility() {
        if (abilityKey != null && !abilityKey.isEmpty()) {
            // Toggleable abilities cannot be selected - they are toggled directly
            if (ability instanceof DBCToggleAbility) {
                return;
            }
            // Send packet to server to select this ability
            DBCPacketHandler.Instance.sendToServer(new DBCSelectAbility(index));
        }
    }

    public void setAbility(String key) {
        this.abilityKey = key;
        if (key != null && !key.isEmpty() && AbilityController.Instance != null) {
            this.ability = AbilityController.Instance.resolveAbility(key);
        } else {
            this.ability = null;
        }
        icon = ability != null ? new AbilityIcon(ability) : null;
    }

    public void clearAbility() {
        this.abilityKey = null;
        this.ability = null;
        if (parent.hoveredSlot == index) {
            parent.selectSlot(-1);
        }
        parent.timeClosedSubGui = Minecraft.getSystemTime();
        icon = null;
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
        if (abilityKey == null || abilityKey.isEmpty())
            return;

        GL11.glPushMatrix();

        //        switch (index) {
        //            case 0: glTranslatef(1, 0, 0); break;
        //            case 1: glTranslatef(4, 0, 0); break;
        //            case 2: glTranslatef(6, 0, 0); break;
        //            case 3: glTranslatef(8, 0, 0); break;
        //            case 4: glTranslatef(10, 0, 0); break;
        //            case 5: glTranslatef(12, 0, 0); break;
        //        }

        if (icon != null) {
            icon.draw();
        }

        GL11.glTranslatef(0, 5, 0);

        drawCenteredString(fontRenderer, getAbilityName(), 0, (icon != null ? icon.height / 2 + 5 : 0), getNameColor());

        GL11.glPopMatrix();
    }

    public String getAbilityName() {
        if (ability != null) {
            return ability.getName();
        }
        return abilityKey != null ? abilityKey : "";
    }

    /**
     * Get the color for the ability name.
     * Toggleable abilities show green if active, red if inactive.
     * Regular abilities show white.
     */
    private int getNameColor() {
        if (ability instanceof DBCToggleAbility) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.thePlayer != null && ((DBCToggleAbility) ability).isActive(mc.thePlayer)) {
                return 0xFF55FF55; // Green - active
            }
            return 0xFFFF5555; // Red - inactive
        }
        return 0xFFFFFFFF; // White - normal
    }
}
