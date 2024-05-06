package kamkeel.npcdbc.client.model;


import JinRyuu.JRMCore.entity.ModelBipedBody;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Matrix4f;

public class ModelPotara extends ModelBipedBody {

    private final ModelRenderer Head;
    private final ModelRenderer earringRight;
    private final ModelRenderer dangle2_r1;
    private final ModelRenderer dangle1_r1;
    private final ModelRenderer earringLeft;
    private final ModelRenderer dangle2_r2;
    private final ModelRenderer dangle1_r2;

    /**
     * Creates a model for the Potara.
     * @param side 0 - right, 1 - left
     */
    public ModelPotara(int side){
        this();
        if(side == 0)
            earringLeft.isHidden = true;
        if(side == 1)
            earringRight.isHidden = true;

    }



    public ModelPotara() {
        textureWidth = 16;
        textureHeight = 16;

        Head = new ModelRenderer(this);
        Head.setRotationPoint(0.0F, 0.0F, 0.0F);
        bipedHead.addChild(Head);

        earringRight = new ModelRenderer(this);
        earringRight.setRotationPoint(-11.0F, -0.5F, 0.0F);
        Head.addChild(earringRight);
        earringRight.cubeList.add(new ModelBox(earringRight, 0, 4, 5.0F, -2.0F, -1.0F, 2, 2, 2, 0.0F));

        dangle2_r1 = new ModelRenderer(this);
        dangle2_r1.setRotationPoint(6.0F, -3.0F, 0.0F);
        earringRight.addChild(dangle2_r1);
        setRotationAngle(dangle2_r1, 0.0F, 0.7854F, 0.0F);
        dangle2_r1.cubeList.add(new ModelBox(dangle2_r1, 0, 0, -1.0F, -1.0F, 0.0F, 2, 2, 0, 0.0F));

        dangle1_r1 = new ModelRenderer(this);
        dangle1_r1.setRotationPoint(6.0F, -3.0F, 0.0F);
        earringRight.addChild(dangle1_r1);
        setRotationAngle(dangle1_r1, 0.0F, -0.7854F, 0.0F);
        dangle1_r1.cubeList.add(new ModelBox(dangle1_r1, 0, 0, -1.0F, -1.0F, 0.0F, 2, 2, 0, 0.0F));

        earringLeft = new ModelRenderer(this);
        earringLeft.setRotationPoint(-1.0F, -0.5F, 0.0F);
        Head.addChild(earringLeft);
        earringLeft.cubeList.add(new ModelBox(earringLeft, 0, 4, 5.0F, -2.0F, -1.0F, 2, 2, 2, 0.0F));

        dangle2_r2 = new ModelRenderer(this);
        dangle2_r2.setRotationPoint(6.0F, -3.0F, 0.0F);
        earringLeft.addChild(dangle2_r2);
        setRotationAngle(dangle2_r2, 0.0F, 0.7854F, 0.0F);
        dangle2_r2.cubeList.add(new ModelBox(dangle2_r2, 0, 0, -1.0F, -1.0F, 0.0F, 2, 2, 0, 0.0F));

        dangle1_r2 = new ModelRenderer(this);
        dangle1_r2.setRotationPoint(6.0F, -3.0F, 0.0F);
        earringLeft.addChild(dangle1_r2);
        setRotationAngle(dangle1_r2, 0.0F, -0.7854F, 0.0F);
        dangle1_r2.cubeList.add(new ModelBox(dangle1_r2, 0, 0, -1.0F, -1.0F, 0.0F, 2, 2, 0, 0.0F));
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
        copyRotationData(Head, bipedHead); //Copies rotation & position of bipedHead

        GL11.glPushMatrix();

        //Cannot render to bipedHead directly because DBC makes the parent render the texture on top of itself
        //@TODO Scale models to keep their original positions, Might have to render them individually instead of rendering the "parent" object?
        Head.render(f5);
        GL11.glPopMatrix();

    }
}
