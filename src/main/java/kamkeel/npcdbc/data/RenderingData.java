package kamkeel.npcdbc.data;

import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.entity.EntityNPCInterface;

public class RenderingData {
    public boolean isNPC;
    public EntityNPCInterface npc;
    public DBCDisplay display; //for NPCs

    public EntityPlayer player;
    public DBCData dbcData; //for players

    public byte race = 0, gender = 0;
    public int dbcState, formID;
    public int eyeType = 0, noseType = 0, mouthType = 0;
    public int hairColor = 0, eyeColor = 0, furColor;
    public int bodyCM = 0xffffff, bodyC1 = 0xffffff, bodyC2 = 0xffffff, bodyC3 = 0xffffff;
    public int bodyType = 0;

    public static RenderingData from(DBCDisplay display) {
        RenderingData data = new RenderingData();
        data.isNPC = true;
        data.display = display;

        //        data.npc = display.npc;
        //        data.race = display.race;
        //        data.formID = display.formID;
        //        data.eyeType = display.eyeType;
        //        data.noseType = display.noseType;
        //        data.mouthType = display.mouthType;
        //        data.hairColor = display.hairColor;
        //        data.eyeColor = display.eyeColor;
        //        data.furColor = display.furColor;
        //        data.bodyCM = display.bodyCM;
        //        data.bodyC1 = display.bodyC1;
        //        data.bodyC2 = display.bodyC2;
        //        data.bodyC3 = display.bodyC3;
        //        data.bodyType = display.bodyType;

        return data;
    }

    public static RenderingData from(DBCData dbcData) {
        RenderingData data = new RenderingData();
        data.isNPC = false;
        data.player = dbcData.player;
        data.dbcData = dbcData;
        //        data.race = display.Race;
        //  data.eyeType = JRMCoreH.dnsEyes() display.eyeType;
        //        data.noseType = display.noseType;
        //        data.mouthType = display.mouthType;
        //        data.hairColor = display.hairColor;
        //        data.eyeColor = display.eyeColor;
        //        data.furColor = display.furColor;
        //        data.bodyCM = display.;
        //        data.bodyC1 = display.bodyC1;
        //        data.bodyC2 = display.bodyC2;
        //        data.bodyC3 = display.bodyC3;
        //        data.bodyType = display.bodyType;

        return data;
    }

    public int eyeType() {
        return isNPC ? display.eyeType : JRMCoreH.dnsEyes(dbcData.DNS);
    }
}
