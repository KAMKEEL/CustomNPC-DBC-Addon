package kamkeel.npcdbc.client.gui.hud.abilityWheel;

import kamkeel.npcdbc.client.gui.hud.WheelSegment;
import kamkeel.npcdbc.client.gui.hud.abilityWheel.icon.AbilityIcon;
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
import noppes.npcs.NoppesStringUtils;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.glTranslatef;

public class AbilityWheelSegment extends WheelSegment {
    public HUDAbilityWheel parent;
    public Ability ability;
    public AbilityWheelData data = new AbilityWheelData();
    private AbilityIcon icon = null;

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
        icon = ability != null ? new AbilityIcon(ability) : null;
    }

    public void setAbility(AbilityWheelData data, boolean updateServer) {
        this.data = data;
        ability = (Ability) AbilityController.getInstance().get(data.abilityID, data.isDBC);
        if (updateServer)
            DBCPacketHandler.Instance.sendToServer(new DBCSaveAbilityWheel(index, data));
        icon = ability != null ? new AbilityIcon(ability) : null;
    }

    public void removeAbility() {
        data.reset();
        ability = null;
        DBCPacketHandler.Instance.sendToServer(new DBCSaveAbilityWheel(index, data));
        if (parent.hoveredSlot == index)
            parent.selectSlot(-1);

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
        if (data.abilityID == -1)
            return;

        if (data.isDBC) {
            if (!parent.dbcAbilities.containsKey(data.abilityID)) {
                removeAbility();
                return;
            }
        } else {
            if (!parent.dbcInfo.customAbilityData.hasAbilityUnlocked(data.abilityID)) {
                removeAbility();
                return;
            }
        }

        GL11.glPushMatrix();

        GL11.glTranslatef(0, 5, 0);

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

        drawCenteredString(fontRenderer, getAbilityName(), 0, (icon != null ? icon.height / 2 + 5 : 0), 0xFFFFFFFF);

        GL11.glPopMatrix();
    }

    public String getAbilityName() {
        if (ability instanceof AddonAbility)
            return NoppesStringUtils.translate(((AddonAbility) ability).langName);

        return ability != null ? ability.menuName : "";
    }
}
