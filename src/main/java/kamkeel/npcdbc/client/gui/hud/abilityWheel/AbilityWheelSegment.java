package kamkeel.npcdbc.client.gui.hud.abilityWheel;

import kamkeel.npcdbc.client.gui.hud.WheelSegment;
import kamkeel.npcdbc.config.ConfigDBCClient;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.packets.player.ability.DBCSelectAbility;
import kamkeel.npcs.controllers.data.ability.Ability;
import kamkeel.npcs.controllers.data.ability.AbilityController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import static org.lwjgl.opengl.GL11.glScaled;
import static org.lwjgl.opengl.GL11.glTranslatef;

/**
 * A segment in the Ability Wheel that represents a single ability slot.
 */
public class AbilityWheelSegment extends WheelSegment {
    public HUDAbilityWheel parent;
    public String abilityKey = null;
    public Ability ability = null;

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
    }

    public void clearAbility() {
        this.abilityKey = null;
        this.ability = null;
        if (parent.hoveredSlot == index) {
            parent.selectSlot(-1);
        }
        parent.timeClosedSubGui = Minecraft.getSystemTime();
    }

    @Override
    protected void drawWheelItem(FontRenderer fontRenderer) {
        if (abilityKey != null && !abilityKey.isEmpty()) {
            if (index == 1 || index == 5) {
                glTranslatef(0, 10, 0);
            } else if (index == 2 || index == 4) {
                glTranslatef(0, -10, 0);
            }
            if (ConfigDBCClient.AlteranteSelectionWheelTexture) {
                glScaled(0.7, 0.7, 1);
                switch (index) {
                    case 0:
                        glTranslatef(0, -15f, 0);
                        break;
                    case 1:
                        glTranslatef(-12, -5, 0);
                        break;
                    case 2:
                        glTranslatef(-11, 3, 0);
                        break;
                    case 3:
                        glTranslatef(0, 12f, 0);
                        break;
                    case 4:
                        glTranslatef(10, 3, 0);
                        break;
                    default:
                        glTranslatef(13, -5, 0);
                }
            }

            // Draw ability name
            String displayName = getAbilityName();
            drawCenteredString(fontRenderer, displayName, 0, 0, 0xFFFFFFFF);
        }
    }

    public String getAbilityName() {
        if (ability != null) {
            return ability.getName();
        }
        return abilityKey != null ? abilityKey : "";
    }
}
