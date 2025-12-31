package kamkeel.npcdbc.client.gui.hud.abilityWheel;

import kamkeel.npcdbc.client.gui.hud.WheelSegment;
import kamkeel.npcdbc.config.ConfigDBCClient;
import kamkeel.npcdbc.controllers.AbilityController;
import kamkeel.npcdbc.data.AbilityWheelData;
import kamkeel.npcdbc.data.ability.Ability;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.packets.player.ability.DBCSaveAbilityWheel;
import kamkeel.npcdbc.network.packets.player.ability.DBCSelectAbility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import static org.lwjgl.opengl.GL11.glScaled;
import static org.lwjgl.opengl.GL11.glTranslatef;

// TODO CREATE THE DBC ABILITY HANDLER URGENTLY LMAO
public class AbilityWheelSegment extends WheelSegment {
    public HUDAbilityWheel parent;
    public Ability ability;
    public AbilityWheelData data = new AbilityWheelData();
    //private FormIcon icon = null;

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
        ability = !data.isDBC ? (Ability) AbilityController.getInstance().get(data.abilityID) : null;
        if (updateServer)
            DBCPacketHandler.Instance.sendToServer(new DBCSaveAbilityWheel(index, data));
        //icon = form != null ? new FormIcon(parent, form) : new FormIcon(parent, data.formID);
    }

    public void setAbility(AbilityWheelData data, boolean updateServer) {
        this.data = data;
        ability = !data.isDBC ? (Ability) AbilityController.getInstance().get(data.abilityID) : null;
        if (updateServer)
            DBCPacketHandler.Instance.sendToServer(new DBCSaveAbilityWheel(index, data));
        //icon = form != null ? new FormIcon(parent, form) : new FormIcon(parent, data.formID);
    }

    public void removeAbility() {
        data.reset();
        ability = null;
        DBCPacketHandler.Instance.sendToServer(new DBCSaveAbilityWheel(index, data));
        if (parent.hoveredSlot == index)
            parent.selectSlot(-1);

        parent.timeClosedSubGui = Minecraft.getSystemTime();
        //icon = null;
    }


    @Override
    protected void drawWheelItem(FontRenderer fontRenderer) {
        if (data.abilityID != -1) {
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
            if (data.isDBC) {
                if (!parent.dbcAbilities.containsKey(data.abilityID))
                    removeAbility();
            } else {
                if (!parent.dbcInfo.customAbilityData.hasAbilityUnlocked(data.abilityID))
                    removeAbility();
            }
//            if (icon != null) {
//                glTranslatef(0, (float) -icon.height / 2, 0);
//                icon.draw();
//                glTranslatef(0, icon.height, 0);
//            }
            drawCenteredString(fontRenderer, getAbilityName(), 0, 0, 0xFFFFFFFF);
        }
    }

    public String getAbilityName() {
        DBCData dbcData = DBCData.get(parent.dbcInfo.parent.player);

        if (!data.isDBC)
            return ability != null ? ability.menuName : "";

        // TODO RETURN DBC ABILITIES NAMES
        //return DBCForm.getMenuName(dbcData.Race, data.abilityID, dbcData.isForm(DBCForm.Divine));
        return "";
    }
}
