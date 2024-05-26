package kamkeel.npcdbc.data.aura;

import kamkeel.npcdbc.api.aura.IAura;
import kamkeel.npcdbc.api.aura.IAuraDisplay;
import kamkeel.npcdbc.controllers.AuraController;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.controllers.AnimationController;
import noppes.npcs.scripted.NpcAPI;

public class Aura implements IAura {
    public int id = -1;
    public String name = "", menuName = "§aNEW";

    public int secondaryAuraID = -1;
    public AuraDisplay display = new AuraDisplay(this);

    public NBTTagCompound writeToNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("ID", id);
        compound.setString("name", name);
        compound.setString("menuName", menuName);
        compound.setInteger("secondaryAuraID", secondaryAuraID);

        display.writeToNBT(compound);


        return compound;
    }

    public void readFromNBT(NBTTagCompound compound) {
        if (compound.hasKey("ID"))
            id = compound.getInteger("ID");
        else if (AnimationController.Instance != null)
            id = FormController.Instance.getUnusedId();

        name = compound.getString("name");
        menuName = compound.getString("menuName");

        secondaryAuraID = compound.getInteger("secondaryAuraID");

        display.readFromNBT(compound);
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String getMenuName() {
        return menuName;
    }

    @Override
    public void setMenuName(String name) {
        if (name.contains("&"))
            name = name.replace("&", "§");

        this.menuName = name;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public void setID(int newID) {
        id = newID;
    }

    @Override
    public void assignToPlayer(EntityPlayer player) {

        PlayerDBCInfo formData = PlayerDataUtil.getDBCInfo(player);
        formData.addAura(this);
        formData.updateClient();


    }

    public void assignToPlayer(String playerName) {
        assignToPlayer(NpcAPI.Instance().getPlayer(playerName).getMCEntity());
    }


    @Override
    public void removeFromPlayer(EntityPlayer player) {
        PlayerDBCInfo formData = PlayerDataUtil.getDBCInfo(player);
        formData.removeAura(this);
        if (formData.selectedAura == this.id)
            formData.selectedAura = -1;

        formData.updateClient();
    }

    public void removeFromPlayer(String playerName) {
        removeFromPlayer(NpcAPI.Instance().getPlayer(playerName).getMCEntity());
    }

    @Override
    public IAuraDisplay getDisplay() {
        return this.display;
    }


    @Override
    public int getSecondaryAuraID() {
        return secondaryAuraID;
    }

    public Aura getSecondaryAur() {
        if (AuraController.Instance.has(secondaryAuraID))
            return (Aura) AuraController.Instance.get(secondaryAuraID);
        return null;
    }

    @Override
    public IAura getSecondaryAura() {
        if (AuraController.Instance.has(secondaryAuraID))
            return AuraController.Instance.get(id);
        return null;
    }

    @Override
    public void setSecondaryAura(int id) {
        if (AuraController.Instance.has(id))
            this.secondaryAuraID = id;
        else
            secondaryAuraID = -1;
    }

    @Override
    public void setSecondaryAura(Aura aura) {
        setSecondaryAura(aura.id);
    }

    public boolean hasSecondaryAura() {
        return AuraController.Instance.has(secondaryAuraID);
    }

    @Override
    public IAura clone() {
        Aura aura = new Aura();
        aura.readFromNBT(writeToNBT());
        return aura;
    }
    @Override
    public IAura save() {
        return AuraController.Instance.saveAura(this);
    }


}
