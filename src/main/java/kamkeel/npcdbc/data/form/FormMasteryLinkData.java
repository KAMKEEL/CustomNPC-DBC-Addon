package kamkeel.npcdbc.data.form;

import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.api.form.IForm;
import kamkeel.npcdbc.api.form.IFormMasteryLinkData;
import kamkeel.npcdbc.controllers.FormController;
import net.minecraft.nbt.NBTTagCompound;

import java.util.HashMap;

public class FormMasteryLinkData implements IFormMasteryLinkData {

    public FormMastery parent;

    public HashMap<Integer, LinkData> masteryLinks = new HashMap<>();

    public FormMasteryLinkData(FormMastery formMastery) {
        this.parent = formMastery;
    }

    public void saveToNBT(NBTTagCompound compound) {
        NBTTagCompound masteryLinksNBT = new NBTTagCompound();

        for(int i = 0; i < JRMCoreH.Races.length; i++) {
            LinkData data = masteryLinks.get(i);
            if(data == null)
                continue;

            masteryLinksNBT.setTag(""+i, data.writeCompound());
        }

        compound.setTag("masteryLinkData", masteryLinksNBT);

    }

    public void loadFromNBT(NBTTagCompound compound) {
        if(!compound.hasKey("masteryLinkData"))
            return;

        NBTTagCompound masteryLinksNBT = compound.getCompoundTag("masteryLinkData");
        for(int i = 0; i < JRMCoreH.Races.length; i++) {
            if(!masteryLinksNBT.hasKey(""+i))
                continue;
            LinkData data = LinkData.loadFromNBT(masteryLinksNBT.getCompoundTag(""+i));
            masteryLinks.put(i, data);
        }
    }


    @Override
    public boolean hasLinkData(int race) {
        return masteryLinks.get(race) != null;
    }

    @Override
    public boolean isCustomFormLink(int race) {
        return hasLinkData(race) && masteryLinks.get(race).isCustomLink;
    }

    @Override
    public int getFormID(int race) {
        LinkData linkData = masteryLinks.get(race);
        return linkData != null ? masteryLinks.get(race).formID : -1;
    }

    @Override
    public IForm getForm(int race) {
        LinkData linkData = masteryLinks.get(race);
        return linkData != null && linkData.isCustomLink ? FormController.getInstance().get(linkData.formID) : null;
    }

    @Override
    public void setCustomFormLink(int formID, int race) {
        masteryLinks.put(race, new LinkData(formID, true));
    }

    @Override
    public void setCustomFormLink(IForm form, int race) {
        if(form == null) {
            removeLinkData(race);
            return;
        }

        masteryLinks.put(race, new LinkData(form.getID(), true));
    }

    @Override
    public void setDBCFormLink(int formID, int race) {
        if(formID == -1){
            removeLinkData(race);
            return;
        }
        masteryLinks.put(race, new LinkData(formID, false));
    }

    @Override
    public void setMasteryLink(int formID, int race, boolean isCustomFormLink) {
        if(formID == -1){
            removeLinkData(race);
            return;
        }

        masteryLinks.put(race, new LinkData(formID, isCustomFormLink));
    }

    @Override
    public void removeLinkData(int race) {
        masteryLinks.remove(race);
    }

    @Override
    public void removeAllLinkData() {
        masteryLinks.clear();
    }


    public static class LinkData {
        public int formID;
        public boolean isCustomLink;

        public LinkData(int formID, boolean isCustomLink) {
            this.formID = formID;
            this.isCustomLink = isCustomLink;
        }

        public NBTTagCompound writeCompound() {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setInteger("formID", formID);
            compound.setBoolean("isCustom", isCustomLink);
            return compound;
        }

        public static LinkData loadFromNBT(NBTTagCompound compound) {
            int formID = compound.getInteger("formID");
            boolean isCustom = compound.getBoolean("isCustom");

            return new LinkData(formID, isCustom);
        }
    }
}
