package kamkeel.npcdbc.client.gui.hud.abilityWheel;

import kamkeel.npcdbc.client.gui.hud.WheelSegment;
import kamkeel.npcdbc.client.utils.Color;
import kamkeel.npcdbc.controllers.AbilityController;
import kamkeel.npcdbc.data.AbilityWheelData;
import kamkeel.npcdbc.data.ability.Ability;
import kamkeel.npcdbc.data.ability.AddonAbility;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.packets.player.ability.DBCSaveAbilityWheel;
import kamkeel.npcdbc.network.packets.player.ability.DBCSelectAbility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import noppes.npcs.NoppesStringUtils;
import noppes.npcs.client.ClientCacheHandler;
import noppes.npcs.client.renderer.ImageData;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.glTranslatef;

public class AbilityWheelSegment extends WheelSegment {
    public HUDAbilityWheel parent;
    public Ability ability;
    public AbilityWheelData data = new AbilityWheelData();

    AbilityWheelSegment(HUDAbilityWheel parent, int index) {
        this(parent, 0, 0, index);

    }

    AbilityWheelSegment(HUDAbilityWheel parent, int posX, int posY, int index) {
        super(index);
        this.parent = parent;
        this.posX = posX;
        this.posY = posY;
        this.index = data.slot = index;
    }

    public void selectAbility() {
        DBCPacketHandler.Instance.sendToServer(new DBCSelectAbility(data.abilityID, data.isDBC));
    }

    public void setAbility(int abilityID, boolean isDBC, boolean updateServer) {
        data.abilityID = abilityID;
        data.isDBC = isDBC;
        ability = (Ability) AbilityController.getInstance().get(data.abilityID, data.isDBC);
        if (updateServer)
            DBCPacketHandler.Instance.sendToServer(new DBCSaveAbilityWheel(index, data));
    }

    public void setAbility(AbilityWheelData data, boolean updateServer) {
        this.data = data;
        ability = (Ability) AbilityController.getInstance().get(data.abilityID, data.isDBC);
        if (updateServer)
            DBCPacketHandler.Instance.sendToServer(new DBCSaveAbilityWheel(index, data));
    }

    public void removeAbility() {
        data.reset();
        ability = null;
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
        if (data.abilityID != -1) {
            glTranslatef(0, 10, 0);

            switch (index) {
                case 0:
                case 1:
                    glTranslatef(3, 0, 0);
                    break;
                case 2:
                case 3:
                    glTranslatef(6, 0, 0);
                    break;
                case 4:
                case 5:
                    glTranslatef(11, 0, 0);
                    break;
                default:
                    break;
            }

            if (data.isDBC) {
                if (!parent.dbcAbilities.containsKey(data.abilityID))
                    removeAbility();
            } else {
                if (!parent.dbcInfo.customAbilityData.hasAbilityUnlocked(data.abilityID))
                    removeAbility();
            }

            drawCenteredString(fontRenderer, getAbilityName(), 0, 0, 0xFFFFFFFF);
        }
    }

    public String getAbilityName() {
        if (ability instanceof AddonAbility)
            return NoppesStringUtils.translate(((AddonAbility) ability).langName);

        return ability != null ? ability.menuName : "";
    }
}
