package kamkeel.npcdbc.data.aura;

import kamkeel.npcdbc.api.aura.IAura;
import kamkeel.npcdbc.api.aura.IAuraDisplay;
import kamkeel.npcdbc.controllers.AuraController;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.controllers.AnimationController;
import noppes.npcs.scripted.NpcAPI;

public class Aura implements IAura {
    public int id = -1;
    public String name = "", menuName = "§aNEW";

    public int secondaryAuraID = -1;
    public AuraDisplay display = new AuraDisplay(this);

    public Aura() {
    }

    public Aura(int id, String name) {
        this.id = id;
        this.name = name;
    }

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

        if (!compound.hasKey("secondaryAuraID"))
            compound.setInteger("secondaryAuraID", -1);
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
    public void assignToPlayer(IPlayer player) {

        PlayerDBCInfo formData = PlayerDataUtil.getDBCInfo((EntityPlayer) player.getMCEntity());
        formData.addAura(this);
        formData.updateClient();


    }

    public void assignToPlayer(String playerName) {
        assignToPlayer(NpcAPI.Instance().getPlayer(playerName));
    }


    @Override
    public void removeFromPlayer(IPlayer player) {
        PlayerDBCInfo formData = PlayerDataUtil.getDBCInfo((EntityPlayer) player.getMCEntity());
        formData.removeAura(this);
        if (formData.selectedAura == this.id)
            formData.selectedAura = -1;

        formData.updateClient();
    }

    public void removeFromPlayer(String playerName) {
        removeFromPlayer(NpcAPI.Instance().getPlayer(playerName));
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
        Aura secondaryAura = (Aura) AuraController.Instance.get(id);
        if (secondaryAura != null && secondaryAura.secondaryAuraID != this.id)
            this.secondaryAuraID = id;
        else
            secondaryAuraID = -1;
    }

    @Override
    public void setSecondaryAura(IAura aura) {
        int id = aura == null ? -1 : aura.getID();
        setSecondaryAura(id);
    }

    public boolean hasSecondaryAura() {
        return AuraController.Instance.has(secondaryAuraID);
    }

    @Override
    public IAura clone() {
        Aura aura = new Aura();
        aura.readFromNBT(writeToNBT());
        aura.id = AuraController.Instance.getUnusedId();
        return aura;
    }

    @Override
    public IAura save() {
        return AuraController.Instance.saveAura(this);
    }


}
