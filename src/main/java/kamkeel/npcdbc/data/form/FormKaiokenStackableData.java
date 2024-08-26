package kamkeel.npcdbc.data.form;

import kamkeel.npcdbc.api.form.IFormKaiokenStackables;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Arrays;

public class FormKaiokenStackableData implements IFormKaiokenStackables {

    public float kaiokenDrainMulti = 1;
    public float[] kaiokenBalanceValue = new float[6];
    public float[] kaiokenStrainedBalanceValue = new float[6];

    public boolean kaiokenMultipliesCurrentFormDrain;

    public boolean isUsingGlobalMultis;

    private final Form parent;
    private final FormStackable stackableParent;

    public FormKaiokenStackableData(Form parent, FormStackable formStackable) {
        this.parent = parent;
        this.stackableParent = formStackable;

        Arrays.fill(kaiokenBalanceValue, 1);
        Arrays.fill(kaiokenStrainedBalanceValue, 1);
    }

    public void readFromNBT(NBTTagCompound stack){
        if(stack.hasKey("kaiokenDrainData")){
            NBTTagCompound kaioDrain = stack.getCompoundTag("kaiokenDrainData");
            kaiokenDrainMulti = kaioDrain.getFloat("multi");

            NBTTagCompound balanceNormal = kaioDrain.getCompoundTag("balanceNormal");
            for(int i = 0; i < 6; i++){
                kaiokenBalanceValue[i] = balanceNormal.getFloat(i+"");
            }
            NBTTagCompound balanceStrained = kaioDrain.getCompoundTag("balanceStrained");
            for(int i = 0; i < 6; i++){
                kaiokenStrainedBalanceValue[i] = balanceStrained.getFloat(i+"");
            }
            kaiokenMultipliesCurrentFormDrain = kaioDrain.getBoolean("multipliesCurrentFormDrain");
        }else{
            kaiokenMultipliesCurrentFormDrain = true;
        }
    }

    public void saveToNBT(NBTTagCompound stack){
        NBTTagCompound kaioDrainData = new NBTTagCompound();
        kaioDrainData.setFloat("multi", kaiokenDrainMulti);
        NBTTagCompound kaioBalanceNormal = new NBTTagCompound();
        for(int i = 0; i < 6; i++){
            kaioBalanceNormal.setFloat(i+"", kaiokenBalanceValue[i]);
        }
        NBTTagCompound kaioBalanceStrained = new NBTTagCompound();
        for(int i = 0; i < 6; i++){
            kaioBalanceStrained.setFloat(i+"", kaiokenStrainedBalanceValue[i]);
        }

        kaioDrainData.setTag("balanceNormal", kaioBalanceNormal);
        kaioDrainData.setTag("balanceStrained", kaioBalanceStrained);
        kaioDrainData.setBoolean("multipliesCurrentFormDrain", kaiokenMultipliesCurrentFormDrain);
        stack.setTag("kaiokenDrainData", kaioDrainData);
    }


    @Override
    public boolean isUsingGlobalKaiokenMultis() {
        return this.isUsingGlobalMultis;
    }

    @Override
    public void toggleGlobalKaiokenMulti(boolean useGlobal) {
        this.isUsingGlobalMultis = useGlobal;
    }

    @Override
    public void setKaioStateMulti(int state2, float multi) {

    }

    @Override
    public float getKaioStateMulti(int state2) {
        return 0;
    }

    @Override
    public void setKaioDrain(float drain) {
        this.kaiokenDrainMulti = drain;
    }

    @Override
    public float getKaioDrain() {
        return this.kaiokenDrainMulti;
    }

    @Override
    public void toggleAdvanedDrain(boolean isOn) {
        this.kaiokenMultipliesCurrentFormDrain = true;
    }

    @Override
    public boolean usingAdvancedDrain() {
        return this.kaiokenMultipliesCurrentFormDrain;
    }

    @Override
    public void setKaioState2Balance(int state2, boolean strained, float value){
        if(state2 < 0)
            state2 = 0;
        if(state2 > 5)
            state2 = 5;

        if(strained)
            this.kaiokenStrainedBalanceValue[state2] = value;
        else
            this.kaiokenBalanceValue[state2] = value;
    }

    @Override
    public float getKaioState2Balance(int state2, boolean strained) {
        if(state2 < 0)
            state2 = 0;
        if(state2 > 5)
            state2 = 5;

        return (strained ? kaiokenStrainedBalanceValue[state2] : kaiokenBalanceValue[state2]);
    }
}
