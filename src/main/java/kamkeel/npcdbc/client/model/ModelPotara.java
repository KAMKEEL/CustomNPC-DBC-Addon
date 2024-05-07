package kamkeel.npcdbc.client.model;


import JinRyuu.JRMCore.entity.ModelBipedBody;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Matrix4f;

public class ModelPotara extends ModelBipedBody {

    public static final ModelPotara RIGHT_EAR = new ModelPotara(0);
    public static final ModelPotara LEFT_EAR = new ModelPotara(1);
    public static final ModelPotara BOTH_EARS = new ModelPotara();

    private final ModelRenderer Head;
    private final ModelRenderer earringRight;
    private final ModelRenderer earringLeft;

    /**
     * Creates a model for the Potara.
     * @param side 0 - right, 1 - left
     */
    private ModelPotara(int side){
        this();
        if(side == 0)
            earringLeft.isHidden = true;
        if(side == 1)
            earringRight.isHidden = true;

    }



    private ModelPotara() {
        textureWidth = 8;
        textureHeight = 8;

        Head = new ModelRenderer(this);
        Head.setRotationPoint(0.0F, 0.0F, 0.0F);


        earringRight = new ModelRenderer(this);
        earringRight.setRotationPoint(2.0F, -0.40F, -0.5F);
        Head.addChild(earringRight);
        earringRight.cubeList.add(new ModelBox(earringRight, 0, 2, -7.0F, -2.0F, -1.0F, 1, 1, 1, 0.0F));

        ModelRenderer dangle2_r1 = new ModelRenderer(this);
        dangle2_r1.setRotationPoint(-6.5F, -2.5F, -0.5F);
        earringRight.addChild(dangle2_r1);
        setRotationAngle(dangle2_r1, 0.0F, 0.7854F, 0.0F);
        dangle2_r1.cubeList.add(new ModelBox(dangle2_r1, 0, 0, -0.5F, -0.5F, 0.0F, 1, 1, 0, 0.0F));

        ModelRenderer dangle1_r1 = new ModelRenderer(this);
        dangle1_r1.setRotationPoint(-6.5F, -2.5F, -0.5F);
        earringRight.addChild(dangle1_r1);
        setRotationAngle(dangle1_r1, 0.0F, -0.7854F, 0.0F);
        dangle1_r1.cubeList.add(new ModelBox(dangle1_r1, 0, 0, -0.5F, -0.5F, 0.0F, 1, 1, 0, 0.0F));

        earringLeft = new ModelRenderer(this);
        earringLeft.setRotationPoint(-2.0F, -0.40F, -0.5F);
        Head.addChild(earringLeft);
        earringLeft.cubeList.add(new ModelBox(earringLeft, 0, 2, 6.0F, -2.0F, -1.0F, 1, 1, 1, 0.0F));

        ModelRenderer dangle2_r2 = new ModelRenderer(this);
        dangle2_r2.setRotationPoint(6.5F, -2.5F, -0.5F);
        earringLeft.addChild(dangle2_r2);
        setRotationAngle(dangle2_r2, 0.0F, 0.7854F, 0.0F);
        dangle2_r2.cubeList.add(new ModelBox(dangle2_r2, 0, 0, -0.5F, -0.5F, 0.0F, 1, 1, 0, 0.0F));

        ModelRenderer dangle1_r2 = new ModelRenderer(this);
        dangle1_r2.setRotationPoint(6.5F, -2.5F, -0.5F);
        earringLeft.addChild(dangle1_r2);
        setRotationAngle(dangle1_r2, 0.0F, -0.7854F, 0.0F);
        dangle1_r2.cubeList.add(new ModelBox(dangle1_r2, 0, 0, -0.5F, -0.5F, 0.0F, 1, 1, 0, 0.0F));
    }

    private void setRotationAngle(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    private void copyRotationData(ModelRenderer model, ModelRenderer modelToCopy){
        model.rotationPointX = modelToCopy.rotationPointX;
        model.rotationPointY = modelToCopy.rotationPointY;
        model.rotationPointZ = modelToCopy.rotationPointZ;
        rot(model, modelToCopy);
    }


    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        copyRotationData(Head, bipedHead);
        Head.render(f5);
    }
}
