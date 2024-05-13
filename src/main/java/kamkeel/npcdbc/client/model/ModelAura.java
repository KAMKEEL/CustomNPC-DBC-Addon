package kamkeel.npcdbc.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelAura extends ModelBase {
    public ModelRenderer auraModel;

    public ModelAura() {
        textureWidth = 64;
        textureHeight = 32;
        auraModel = new ModelRenderer(this, 0, 0);
        auraModel.addBox(-10.0F, -17.0F, -8.0F, 20, 20, 0);
        auraModel.setRotationPoint(0.0F, 20.0F, 0.0F);
    }
    

}
