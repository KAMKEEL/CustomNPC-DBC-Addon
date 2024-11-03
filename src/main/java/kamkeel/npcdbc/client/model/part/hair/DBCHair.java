package kamkeel.npcdbc.client.model.part.hair;

import JinRyuu.JBRA.mod_JBRA;
import JinRyuu.JRMCore.JRMCoreClient;
import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.client.ColorMode;
import kamkeel.npcdbc.client.model.ModelDBC;
import kamkeel.npcdbc.client.utils.Color;
import kamkeel.npcdbc.config.ConfigDBCClient;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormDisplay;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.mixins.late.INPCDisplay;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.client.model.ModelMPM;
import noppes.npcs.controllers.data.TintData;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.data.ModelData;
import org.lwjgl.opengl.GL11;

public class DBCHair extends ModelHairRenderer {
    public static final String GOKU_HAIR = "025050545050210250505450501801505045505021025050475050180147507467503248505072675043255250726750360150505667501922475071675038255050716750380152507167503202475071675032025250716750300050507167503000505047655036205250276550362250502765503620475027655036225250306550363150503065503622475030655034015250276550250147502765503000505027655036175050505050803150505050508028505050505080225050505050801750505050508022505050505080255050505050801750505050508000505050505080005050505050800050505050508000505050505080005050505050800050505050508000505050505080005050505050803154508067504931545080615028285450766150472854506561506551525080675038655250806150786052507861503451525069615050625050806950528250508061503485505078615030625050696150585149508069506157495080615080624950786150805149506961504920";
    public static final String SSJ4_HAIR = "373852546750347428545480193462285654801934283647478050340147507467501848505072675018255250726750183760656580501822475071675018255050716750189730327158501802475071675018973225673850189765616160501820414547655019545654216550195754542165501920475027655019943669346576193161503065231900475030655019406534276538199465393460501997654138655019976345453950189760494941501897615252415018976354563850189763494736501897614949395018976152523950189763525234501897584749395018976150493850189760545234501897585250415018885445474550189754475041501897545250435018885454523950185143607861501897415874585018514369196150185147768078391865525680565018974356806150188843567861501868396374615018975056805650189750568056501885582374615018975823726150187149568054501877495680565018774950785650189163236961501820";
    public static final String MAJIN_HAIR = "005050555050000050505550500000505055505000005050455050000050505250500000505052505000005050555050000050505450500000505052505000005050525050000150433450500000505055505000005050525050000054395050500000505045505000005050475050000050504750500000505047505000015043655050000050504750500000505047505000005050475050000050504750500000544545505000005250505050000052505050500000525050505000005250505050000050505050500000505050505000005050505050000052505050500000525050505000005250505050000052505050500000525050505000005245505050000054505050500000525050505000005252505050000070505050500000705050505000007050505050000070505050500000705050505000347050505050003470505050500000705050505000007050505050000069505050500000695050505000007050505050000070505050500000705050505000007050505050000070505050500020";
    public static ResourceLocation hairResource = new ResourceLocation("jinryuumodscore:gui/normall.png");

    public ModelData data;
    public EntityCustomNpc entity;
    public DBCDisplay display;
    public int hairColor = 16777215;
    public ModelMPM base;

    public ModelHairRenderer hairCluster;
    public ModelHairRenderer[] hairall;

    public ModelHairRenderer bipedHeadAll;
    public ModelHairRenderer radlike1;
    public ModelHairRenderer radlike2;
    public ModelHairRenderer radlike3;
    public ModelHairRenderer radlike4;
    public ModelHairRenderer radlike5;
    public ModelHairRenderer radlike7;
    public ModelHairRenderer radlike8;
    public ModelHairRenderer radlike10;
    public ModelHairRenderer radlike11;
    public ModelHairRenderer radlike12;
    public ModelHairRenderer radlike13;
    public ModelHairRenderer radlike14;
    public ModelHairRenderer radlike15;
    public ModelHairRenderer radlike16;
    public ModelHairRenderer radlike17;
    public ModelHairRenderer radlike18;
    public ModelHairRenderer radlike19;
    public ModelHairRenderer radlike20;
    public ModelHairRenderer radlike21;
    public ModelHairRenderer radlike22;
    public ModelHairRenderer radlike23;
    public ModelHairRenderer radlike24;
    public ModelHairRenderer radlike25;
    public ModelHairRenderer radlike26;
    public ModelHairRenderer radlike27;
    public ModelHairRenderer radlike28;
    public ModelHairRenderer radlike29;
    public ModelHairRenderer radlike30;
    public ModelHairRenderer radlike31;
    public ModelHairRenderer radlike32;
    public ModelHairRenderer radlik6;
    public ModelHairRenderer radlik7;
    public ModelHairRenderer radlik15;
    public ModelHairRenderer radlik1;
    public ModelHairRenderer radlik2;
    public ModelHairRenderer radlik3;
    public ModelHairRenderer radlik4;
    public ModelHairRenderer radlik5;
    public ModelHairRenderer radlik8;
    public ModelHairRenderer radlik9;
    public ModelHairRenderer radlik10;
    public ModelHairRenderer radlik11;
    public ModelHairRenderer radlik12;
    public ModelHairRenderer radlik13;
    public ModelHairRenderer radlik14;
    public ModelHairRenderer radlik16;
    public ModelHairRenderer radlik17;
    public ModelHairRenderer radlik18;

    public ModelHairRenderer bipedHeadrad;
    public ModelHairRenderer bipedHeadradl2;
    public ModelHairRenderer bipedHeadradl;
    public ModelHairRenderer ssj3, ssj3strand1;

    public DBCHair(ModelMPM base) {
        super(base);
        this.base = base;
        textureHeight = 32;
        textureWidth = 64;

        this.hairCluster = new ModelHairRenderer(base, 0, 0);
        this.hairall = new ModelHairRenderer[224];
        int hossz;
        int face;
        for (hossz = 0; hossz < 4; ++hossz) {
            for (face = 0; face < 56; ++face) {
                if (this.hairall[hossz + face * 4] == null) {
                    this.hairall[hossz + face * 4] = new ModelHairRenderer(base, 32, 0);
                    this.hairall[hossz + face * 4].addBox(-1.0F, hossz == 0 ? -1.0F : 0.0F, -1.0F, 2, 3, 2);
                    this.hairall[hossz + face * 4].setRotationPoint(0.0F, 0.0F, 0.0F);
                    this.setRotation(this.hairall[hossz + face * 4], 0.0F, 0.0F, 0.0F);
                    this.hairCluster.addChild(this.hairall[hossz + face * 4]);
                }
            }
        }
        for (hossz = 0; hossz < 4; ++hossz) {
            for (face = 0; face < 56; ++face) {
                if (hossz != 3) {
                    this.hairall[hossz + face * 4].addChild(this.hairall[hossz + 1 + face * 4]);
                }
            }
        }

        this.addChild(hairCluster);
        initSSJ3();
    }

    public void initSSJ3() {
        this.bipedHeadAll = new ModelHairRenderer(base, 0, 0);
        this.bipedHeadAll.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.01F);
        this.bipedHeadAll.setRotationPoint(0.0F, 0.0F, 0.0F);

        this.radlike1 = new ModelHairRenderer(base, 32, 0);
        this.radlike1.addBox(-1.0F, -10.0F, -6.05F, 4, 4, 4);
        this.radlike1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.radlike1, -0.3141593F, 0.0F, 0.0F);
        this.radlike2 = new ModelHairRenderer(base, 32, 0);
        this.radlike2.addBox(-6.8F, -6.5F, -1.0F, 4, 3, 3);
        this.radlike2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.radlike2, 0.0F, 0.1745329F, -0.1396263F);
        this.radlike3 = new ModelHairRenderer(base, 32, 0);
        this.radlike3.addBox(-6.3F, -4.0F, 0.0F, 3, 2, 2);
        this.radlike3.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.radlike3, 0.0F, (float) (Math.PI / 12), -0.1919862F);
        this.radlike4 = new ModelHairRenderer(base, 32, 0);
        this.radlike4.addBox(2.8F, -7.0F, -1.0F, 4, 3, 3);
        this.radlike4.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.radlike4, 0.0F, -0.1745329F, 0.1919862F);
        this.radlike5 = new ModelHairRenderer(base, 32, 0);
        this.radlike5.addBox(2.8F, -4.0F, 0.7F, 3, 2, 2);
        this.radlike5.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.radlike5, 0.0F, -0.1745329F, 0.1570796F);
        this.radlike7 = new ModelHairRenderer(base, 32, 0);
        this.radlike7.addBox(-1.5F, -11.0F, -8.0F, 3, 3, 3);
        this.radlike7.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.radlike7, -0.5934119F, 0.0F, 0.1047198F);
        this.radlike8 = new ModelHairRenderer(base, 32, 0);
        this.radlike8.addBox(-5.0F, -12.0F, -8.0F, 2, 3, 2);
        this.radlike8.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.radlike8, (float) (-Math.PI * 2.0 / 9.0), 0.0F, 0.4363323F);
        this.radlike10 = new ModelHairRenderer(base, 32, 0);
        this.radlike10.addBox(-1.0F, -10.3F, -6.3F, 4, 6, 4);
        this.radlike10.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.radlike10, -0.4363323F, 0.0F, -0.3665191F);
        this.radlike11 = new ModelHairRenderer(base, 32, 0);
        this.radlike11.addBox(1.0F, -11.3F, -6.0F, 5, 4, 3);
        this.radlike11.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.radlike11, -0.5410521F, 0.0F, -0.4886922F);
        this.radlike12 = new ModelHairRenderer(base, 32, 0);
        this.radlike12.addBox(3.5F, -11.5F, -8.0F, 3, 3, 3);
        this.radlike12.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.radlike12, -0.8552113F, 0.0F, -0.6108652F);
        this.radlike13 = new ModelHairRenderer(base, 32, 0);
        this.radlike13.addBox(6.0F, -12.4F, -8.0F, 2, 3, 2);
        this.radlike13.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.radlike13, -0.9948377F, 0.0F, -0.7679449F);
        this.radlike14 = new ModelHairRenderer(base, 32, 0);
        this.radlike14.addBox(-1.3F, -9.3F, -5.5F, 3, 5, 3);
        this.radlike14.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.radlike14, -0.3665191F, 0.0F, 0.4014257F);
        this.radlike15 = new ModelHairRenderer(base, 32, 0);
        this.radlike15.addBox(-5.5F, -9.8F, -6.0F, 3, 3, 3);
        this.radlike15.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.radlike15, -0.5410521F, 0.0F, 0.837758F);
        this.radlike16 = new ModelHairRenderer(base, 32, 0);
        this.radlike16.addBox(-9.0F, -8.533334F, -6.0F, 2, 3, 2);
        this.radlike16.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.radlike16, -0.837758F, 0.0F, 1.27409F);
        this.radlike17 = new ModelHairRenderer(base, 32, 0);
        this.radlike17.addBox(-2.0F, -2.0F, 4.0F, 4, 5, 4);
        this.radlike17.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.radlike17, 0.4886922F, 0.0F, 0.0F);
        this.radlike18 = new ModelHairRenderer(base, 32, 0);
        this.radlike18.addBox(-1.0F, -5.0F, 5.0F, 4, 5, 4);
        this.radlike18.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.radlike18, 0.5061455F, (float) (Math.PI / 12), 0.0174533F);
        this.radlike19 = new ModelHairRenderer(base, 32, 0);
        this.radlike19.addBox(-4.0F, -6.0F, 5.0F, 4, 6, 4);
        this.radlike19.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.radlike19, (float) (Math.PI / 6), (float) (-Math.PI / 12), 0.0F);
        this.radlike20 = new ModelHairRenderer(base, 32, 0);
        this.radlike20.addBox(-2.4F, -5.2F, 7.0F, 4, 5, 4);
        this.radlike20.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.radlike20, (float) (Math.PI * 2.0 / 9.0), 0.0F, 0.0F);
        this.radlike21 = new ModelHairRenderer(base, 32, 0);
        this.radlike21.addBox(0.1333333F, -6.5F, 7.533333F, 3, 5, 3);
        this.radlike21.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.radlike21, 0.7679449F, 0.1745329F, 0.0F);
        this.radlike22 = new ModelHairRenderer(base, 32, 0);
        this.radlike22.addBox(-2.866667F, -7.2F, 7.333333F, 3, 4, 3);
        this.radlike22.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.radlike22, 0.5934119F, (float) (-Math.PI / 12), 0.0F);
        this.radlike23 = new ModelHairRenderer(base, 32, 0);
        this.radlike23.addBox(1.0F, -9.0F, -4.05F, 3, 4, 4);
        this.radlike23.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.radlike23, -0.3141593F, 0.0F, -0.8726646F);
        this.radlike24 = new ModelHairRenderer(base, 32, 0);
        this.radlike24.addBox(3.533333F, -10.0F, -4.716667F, 3, 4, 3);
        this.radlike24.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.radlike24, -0.5585054F, 0.0F, -1.082104F);
        this.radlike25 = new ModelHairRenderer(base, 32, 0);
        this.radlike25.addBox(3.533333F, -12.0F, -4.716667F, 2, 4, 2);
        this.radlike25.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.radlike25, -0.5934119F, 0.0F, -0.8203047F);
        this.radlike26 = new ModelHairRenderer(base, 32, 0);
        this.radlike26.addBox(3.533333F, -9.666667F, -3.116667F, 3, 4, 3);
        this.radlike26.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.radlike26, -0.5585054F, 0.0F, -1.396263F);
        this.radlike27 = new ModelHairRenderer(base, 32, 0);
        this.radlike27.addBox(-4.5F, -6.8F, -5.0F, 3, 4, 3);
        this.radlike27.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.radlike27, -0.5410521F, 0.0F, 1.047198F);
        this.radlike28 = new ModelHairRenderer(base, 32, 0);
        this.radlike28.addBox(-6.8F, -7.533333F, -5.0F, 3, 4, 3);
        this.radlike28.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.radlike28, -0.837758F, 0.0F, 1.308997F);
        this.radlike29 = new ModelHairRenderer(base, 32, 0);
        this.radlike29.addBox(6.0F, -10.2F, -5.0F, 2, 3, 2);
        this.radlike29.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.radlike29, -0.7679449F, 0.0F, -1.291544F);
        this.radlike30 = new ModelHairRenderer(base, 32, 0);
        this.radlike30.addBox(-2.433333F, -10.6F, -7.666667F, 3, 3, 3);
        this.radlike30.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.radlike30, -0.7330383F, 0.0F, 0.3839724F);
        this.radlike31 = new ModelHairRenderer(base, 32, 0);
        this.radlike31.addBox(-5.466667F, -11.0F, -8.333333F, 2, 3, 2);
        this.radlike31.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.radlike31, (float) (-Math.PI * 3.0 / 10.0), 0.0F, 0.6806784F);
        this.radlike32 = new ModelHairRenderer(base, 32, 0);
        this.radlike32.addBox(-1.4F, -14.0F, -3.0F, 3, 4, 3);
        this.radlike32.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.radlike32, -0.4363323F, 0.0F, -0.0349066F);
        this.radlik6 = new ModelHairRenderer(base, 32, 0);
        this.radlik6.addBox(-6.8F, -1.733333F, 3.2F, 3, 6, 3);
        this.radlik6.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.radlik6, 0.4363323F, 0.0F, 0.3490659F);
        this.radlik7 = new ModelHairRenderer(base, 32, 0);
        this.radlik7.addBox(4.0F, -3.066667F, 2.6F, 3, 6, 3);
        this.radlik7.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.radlik7, 0.4363323F, 0.0F, -0.3490659F);
        this.radlik15 = new ModelHairRenderer(base, 32, 0);
        this.radlik15.addBox(-2.266667F, -3.2F, 5.4F, 4, 4, 4);
        this.radlik15.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.radlik15, 0.4363323F, 0.0F, 0.0F);
        this.radlik1 = new ModelHairRenderer(base, 32, 0);
        this.radlik1.addBox(-4.466667F, 6.2F, 4.0F, 3, 3, 2);
        this.radlik1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.radlik1, 0.0872665F, 0.0F, 0.0698132F);
        this.radlik2 = new ModelHairRenderer(base, 32, 0);
        this.radlik2.addBox(2.533333F, 4.2F, 3.0F, 3, 3, 3);
        this.radlik2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.radlik2, 0.1396263F, 0.0F, -0.0872665F);
        this.radlik3 = new ModelHairRenderer(base, 32, 0);
        this.radlik3.addBox(-5.466667F, 4.2F, 3.0F, 3, 3, 3);
        this.radlik3.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.radlik3, 0.1396263F, 0.0F, 0.0872665F);
        this.radlik4 = new ModelHairRenderer(base, 32, 0);
        this.radlik4.addBox(-6.133333F, 0.7333333F, 3.0F, 3, 5, 3);
        this.radlik4.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.radlik4, 0.2268928F, 0.0F, 0.2094395F);
        this.radlik5 = new ModelHairRenderer(base, 32, 0);
        this.radlik5.addBox(3.266667F, 0.7333333F, 3.0F, 3, 5, 3);
        this.radlik5.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.radlik5, 0.2268928F, 0.0F, -0.2094395F);
        this.radlik8 = new ModelHairRenderer(base, 32, 0);
        this.radlik8.addBox(-1.466667F, 6.0F, 4.0F, 3, 5, 4);
        this.radlik8.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.radlik8, 0.0872665F, (float) (-Math.PI / 12), 0.0F);
        this.radlik9 = new ModelHairRenderer(base, 32, 0);
        this.radlik9.addBox(-2.466667F, 2.0F, 4.0F, 4, 5, 4);
        this.radlik9.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.radlik9, 0.1570796F, (float) (-Math.PI / 12), 0.0F);
        this.radlik10 = new ModelHairRenderer(base, 32, 0);
        this.radlik10.addBox(-2.0F, 7.266667F, 4.0F, 4, 4, 4);
        this.radlik10.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.radlik10, 0.0698132F, (float) (Math.PI / 12), 0.0F);
        this.radlik11 = new ModelHairRenderer(base, 32, 0);
        this.radlik11.addBox(-1.0F, 4.266667F, 4.0F, 4, 4, 4);
        this.radlik11.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.radlik11, 0.1047198F, (float) (Math.PI / 12), 0.0F);
        this.radlik12 = new ModelHairRenderer(base, 32, 0);
        this.radlik12.addBox(-0.9F, 1.266667F, 4.0F, 4, 4, 4);
        this.radlik12.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.radlik12, 0.1745329F, (float) (Math.PI / 12), 0.0F);
        this.radlik13 = new ModelHairRenderer(base, 32, 0);
        this.radlik13.addBox(-1.933333F, 5.0F, 4.0F, 4, 5, 4);
        this.radlik13.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.radlik13, 0.1745329F, 0.0F, 0.0F);
        this.radlik14 = new ModelHairRenderer(base, 32, 0);
        this.radlik14.addBox(-1.4F, 8.0F, 5.6F, 3, 5, 3);
        this.radlik14.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.radlik14, 0.0872665F, 0.0F, 0.0F);
        this.radlik16 = new ModelHairRenderer(base, 32, 0);
        this.radlik16.addBox(-2.533333F, -2.0F, 3.333333F, 4, 6, 4);
        this.radlik16.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.radlik16, 0.3490659F, (float) (-Math.PI / 12), 0.0F);
        this.radlik17 = new ModelHairRenderer(base, 32, 0);
        this.radlik17.addBox(-1.0F, -2.0F, 4.0F, 4, 5, 4);
        this.radlik17.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.radlik17, 0.3316126F, (float) (Math.PI / 12), 0.0F);
        this.radlik18 = new ModelHairRenderer(base, 32, 0);
        this.radlik18.addBox(-2.0F, 1.0F, 4.0F, 4, 5, 4);
        this.radlik18.setRotationPoint(0.0F, 0.0F, 0.0F);

        this.bipedHeadrad = new ModelHairRenderer(base, 0, 0);
        this.bipedHeadrad.addBox(-0.0F, -0.0F, -0.0F, 0, 0, 0, 0.02F);
        this.bipedHeadrad.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.bipedHeadradl = new ModelHairRenderer(base, 0, 0);
        this.bipedHeadradl.addBox(-0.0F, -0.0F, -0.0F, 0, 0, 0, 0.02F);
        this.bipedHeadradl.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.bipedHeadradl2 = new ModelHairRenderer(base, 0, 0);
        this.bipedHeadradl2.addBox(-0.0F, -0.0F, -0.0F, 0, 0, 0, 0.02F);
        this.bipedHeadradl2.setRotationPoint(0.0F, 0.0F, 0.0F);


        this.bipedHeadrad.addChild(this.bipedHeadAll);
        this.bipedHeadrad.addChild(this.radlike1);
        this.bipedHeadrad.addChild(this.radlike2);
        this.bipedHeadrad.addChild(this.radlike3);
        this.bipedHeadrad.addChild(this.radlike4);
        this.bipedHeadrad.addChild(this.radlike5);
        this.bipedHeadrad.addChild(this.radlike7);
        this.bipedHeadrad.addChild(this.radlike8);
        this.bipedHeadrad.addChild(this.radlike10);
        this.bipedHeadrad.addChild(this.radlike11);
        this.bipedHeadrad.addChild(this.radlike12);
        this.bipedHeadrad.addChild(this.radlike13);
        this.bipedHeadrad.addChild(this.radlike14);
        this.bipedHeadrad.addChild(this.radlike15);
        this.bipedHeadrad.addChild(this.radlike16);
        this.bipedHeadrad.addChild(this.radlike18);
        this.bipedHeadrad.addChild(this.radlike19);
        this.bipedHeadrad.addChild(this.radlike20);
        this.bipedHeadrad.addChild(this.radlike21);
        this.bipedHeadrad.addChild(this.radlike22);
        this.bipedHeadrad.addChild(this.radlike23);
        this.bipedHeadrad.addChild(this.radlike24);
        this.bipedHeadrad.addChild(this.radlike25);
        this.bipedHeadrad.addChild(this.radlike26);
        this.bipedHeadrad.addChild(this.radlike27);
        this.bipedHeadrad.addChild(this.radlike28);
        this.bipedHeadrad.addChild(this.radlike29);
        this.bipedHeadrad.addChild(this.radlike30);
        this.bipedHeadrad.addChild(this.radlike31);
        this.bipedHeadrad.addChild(this.radlike32);
        this.bipedHeadradl.addChild(this.radlik1);
        this.bipedHeadradl.addChild(this.radlik2);
        this.bipedHeadradl.addChild(this.radlik3);
        this.bipedHeadradl.addChild(this.radlik4);
        this.bipedHeadradl.addChild(this.radlik5);
        this.bipedHeadradl.addChild(this.radlik8);
        this.bipedHeadradl.addChild(this.radlik9);
        this.bipedHeadradl.addChild(this.radlik10);
        this.bipedHeadradl.addChild(this.radlik11);
        this.bipedHeadradl.addChild(this.radlik12);
        this.bipedHeadradl.addChild(this.radlik13);
        this.bipedHeadradl.addChild(this.radlik14);
        this.bipedHeadradl.addChild(this.radlik18);
        this.bipedHeadradl2.addChild(this.radlik6);
        this.bipedHeadradl2.addChild(this.radlik7);
        this.bipedHeadradl2.addChild(this.radlik15);
        this.bipedHeadradl2.addChild(this.radlike17);
        this.bipedHeadradl2.addChild(this.radlik16);
        this.bipedHeadradl2.addChild(this.radlik17);

        this.ssj3 = new ModelHairRenderer(base, 0, 0);
        this.ssj3.addBox(-0.0F, -0.0F, -0.0F, 0, 0, 0, 0.02F);
        this.ssj3.setRotationPoint(0.0F, 0.0F, 0.0F);

        this.ssj3strand1 = new ModelHairRenderer(base, 32, 0);
        this.ssj3strand1.addBox(2.866667F, -5.533333F, -6.25F, 2, 4, 1);
        this.ssj3strand1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.ssj3strand1, -0.3141593F, 0.0F, -0.4712389F);
        this.ssj3.addChild(this.ssj3strand1);
    }

    @Override
    public void render(float par1) {
        if (isHidden)
            return;

        if (base.isArmor)
            return;
        DBCDisplay display = ((INPCDisplay) entity.display).getDBCDisplay();
        if (!display.enabled)
            return;

        GL11.glPushAttrib(GL11.GL_CURRENT_BIT);
        ClientProxy.bindTexture(hairResource);
        TintData tintData = this.entity.display.tintData;
        boolean showColor = !this.base.isArmor && tintData.processColor(this.entity.hurtTime > 0 || this.entity.deathTime > 0);
        if (showColor) {
            int color = this.hairColor;
            float red = (float) (color >> 16 & 255) / 255.0F;
            float green = (float) (color >> 8 & 255) / 255.0F;
            float blue = (float) (color & 255) / 255.0F;
            GL11.glColor4f(red, green, blue, this.base.alpha);
        } else if (this.entity.hurtTime > 0 || this.entity.deathTime > 0) {
            int color = this.hairColor;
            float red = (float) (color >> 16 & 255) / 255.0F;
            float green = (float) (color >> 8 & 255) / 255.0F;
            float blue = (float) (color & 255) / 255.0F;

            // Increase the red component
            float increasedRed = Math.min(red + 0.3F, 1.0F); // Increase red by 0.1, ensuring it doesn't exceed 1.0

            GL11.glColor4f(increasedRed, green, blue, this.base.alpha);
        }

        this.renderHairs(display);
        this.base.currentlyPlayerTexture = false;
        if (showColor) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, this.base.alpha);
        }
        GL11.glPopAttrib();
    }

    public void setData(ModelData data, EntityCustomNpc entity, DBCDisplay display) {
        this.data = data;
        this.entity = entity;
        this.initData(data, display);
    }

    public void initData(ModelData data, DBCDisplay display) {
        if (display.hairCode.isEmpty()) {
            isHidden = true;
            return;
        }


        hairColor = display.hairColor;
        isHidden = hairCluster.isHidden = false;
    }

    private void setRotation(ModelHairRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    public int dnsHair2(String s, int n) {
        int a = 0;
        try {
            a = Integer.parseInt(s.charAt(n) + "" + s.charAt(n + 1) + "");
        } catch (NumberFormatException ignored) {
        }
        return s.length() > n ? a : 0;
    }

    public void renderSSJ3Hair(boolean renderBang) {
        this.bipedHeadrad.rotateAngleY = base.bipedBody.rotateAngleY;
        this.bipedHeadrad.rotateAngleX = base.bipedBody.rotateAngleX;
        this.bipedHeadrad.rotationPointX = base.bipedBody.rotationPointX;
        this.bipedHeadrad.rotationPointY = base.bipedBody.rotationPointY;
        this.bipedHeadrad.render(0.0625f);
        this.bipedHeadradl.rotateAngleY = base.bipedBody.rotateAngleY;
        this.bipedHeadradl.rotateAngleX = base.bipedBody.rotateAngleX / 4.0F;
        this.bipedHeadradl.rotationPointX = base.bipedBody.rotationPointX;
        this.bipedHeadradl.rotationPointY = base.bipedBody.rotationPointY;
        this.bipedHeadradl.render(0.0625f);
        this.bipedHeadradl2.rotateAngleY = base.bipedBody.rotateAngleY;
        this.bipedHeadradl2.rotateAngleX = base.bipedBody.rotateAngleX / 2.0F;
        this.bipedHeadradl2.rotationPointX = base.bipedBody.rotationPointX;
        this.bipedHeadradl2.rotationPointY = base.bipedBody.rotationPointY;
        this.bipedHeadradl2.render(0.0625f);
        this.bipedHeadradl2.rotateAngleY = base.bipedBody.rotateAngleY;
        this.bipedHeadradl2.rotateAngleX = base.bipedBody.rotateAngleX / 1.2F;
        this.bipedHeadradl2.rotationPointX = base.bipedBody.rotationPointX;
        this.bipedHeadradl2.rotationPointY = base.bipedBody.rotationPointY;
        this.bipedHeadradl2.render(0.0625f);

        if (renderBang) {
            this.ssj3.rotateAngleY = base.bipedBody.rotateAngleY;
            this.ssj3.rotateAngleX = base.bipedBody.rotateAngleX;
            this.ssj3.rotationPointX = base.bipedBody.rotationPointX;
            this.ssj3.rotationPointY = base.bipedBody.rotationPointY;
            this.ssj3.render(0.0625f);
        }
    }

    public void renderHairs(DBCDisplay display) {
        if (display.hairCode.length() < 5) {
            if (display.hairCode.equalsIgnoreCase("bald"))
                return;
            if (display.race == 5)
                display.hairCode = MAJIN_HAIR;
            if (display.hairType.equals("ssj4"))
                display.hairCode = SSJ4_HAIR;

            if (display.hairCode.length() < 5)
                display.hairCode = SSJ4_HAIR;
//                return;
        }
        boolean canUse = mod_JBRA.a6P9H9B;
        boolean superForm = false;//super form selected;JRMCoreH.plyrSttngsClient(1, pl);
        boolean hasAura = display.auraOn;//aura anim JRMCoreH.StusEfctsClient(4, pl);
        boolean isTurbo = false; //turbo anim JRMCoreH.StusEfctsClient(3, pl);
        boolean isKaioken = false;//kaioken anim JRMCoreH.StusEfctsClient(5, pl);
        boolean isTransforming = display.isTransforming; //transforming anim JRMCoreH.StusEfctsClient(1, pl);

        boolean isSaiyan = DBCRace.isSaiyan(display.race);
        boolean isSSJ3 = false, isRaditz = false;
        boolean effectMajinHair = true;

        if ((display.hairCode.equalsIgnoreCase("bald") || display.hairType.equals("oozaru")) && isSaiyan)
            return;


        String hairCode = display.hairCode;
        int state = 0;
        int race = display.race;
        int rage = display.rage;


        if (display.rage > 0 && display.selectedForm == display.formID)
            rage = 0;

        if (display.hairType.equals("raditz"))
            isRaditz = true;
        else if (display.hairType.equals("ssj"))
            state = 1;
        else if (display.hairType.equals("ssj2"))
            state = 5;
        else if (display.hairType.equals("ssj3"))
            isSSJ3 = true;


        else if (display.hairType.equals("ssj4"))
            hairCode = SSJ4_HAIR;

        int hairColor = display.hairColor;
        if (display.hairColor == -1 && display.race == 5)
            hairColor = display.bodyCM;

        //////////////////////////////////////////////////////
        //////////////////////////////////////////////////////
        //Forms
        Form form = display.getForm();
        if (form != null) {
            FormDisplay d = form.display;
            if (race == 5 && !form.display.effectMajinHair) {
                effectMajinHair = false;
                hairCode = MAJIN_HAIR;
                if (form.display.bodyCM != -1)
                    hairColor = form.display.bodyCM;
            } else {
                if ((d.hairCode.equalsIgnoreCase("bald") || d.hairType.equals("oozaru")) && isSaiyan)
                    return;
                else if (d.hairCode.length() > 3)
                    hairCode = d.hairCode;


                if (d.hasColor("hair"))
                    hairColor = d.hairColor;
                else if (display.race == 5 && d.hasColor("bodycm")) {
                    hairColor = d.bodyCM;
                }

                if (d.hairType.equals("base"))
                    state = 0;
                else if (d.hairType.equals("raditz"))
                    isRaditz = true;
                else if (d.hairType.equals("ssj"))
                    state = 1;
                else if (d.hairType.equals("ssj2"))
                    state = 5;
                else if (d.hairType.equals("ssj4"))
                    hairCode = SSJ4_HAIR;

                isSSJ3 = d.hairType.equals("ssj3");
            }
        }
        //////////////////////////////////////////////////////
        //////////////////////////////////////////////////////
        ColorMode.applyModelColor(hairColor, this.base.alpha, ModelDBC.isHurt);
        String HDDir = CustomNpcPlusDBC.ID + ":textures/hd/";
        boolean HD = ConfigDBCClient.EnableHDTextures;
        ClientProxy.bindTexture(new ResourceLocation((HD ? HDDir + "base/" : "jinryuumodscore:gui/") + "normall.png"));
        if ((isRaditz || isSSJ3) && effectMajinHair) {
            renderSSJ3Hair(isSSJ3);
            return;
        }
        int trTime = canUse ? 2 : 200;
        int arTime = canUse ? 2 : 200;
        if (JRMCoreH.HairsT(state, "B") && display.stateChange < 200) {
            display.stateChange += trTime;
        }

        if (JRMCoreH.HairsT(state, "C")) {
            if (display.stateChange < 200) {
                display.stateChange += trTime;
            }

            if (display.state2Change < 200) {
                display.state2Change += trTime;
            }
        }

        if (JRMCoreH.HairsT(display.tempState, "A") && !JRMCoreH.HairsT(state, "A")) {
            if (!JRMCoreH.HairsT(display.tempState, state) && display.stateChange < 200) {
                display.stateChange += trTime;
            }

            if (display.stateChange >= 200) {
                display.stateChange = 200;
                display.tempState = state;
            }
        } else if (!JRMCoreH.HairsT(display.tempState, "A") && JRMCoreH.HairsT(state, "A")) {
            if ((!JRMCoreH.HairsT(display.tempState, state) || rage == 0) && display.stateChange > 0) {
                display.stateChange -= trTime;
            }

            if (display.stateChange <= 0) {
                display.stateChange = 0;
                display.tempState = state;
            }
        } else if (!JRMCoreH.HairsT(display.tempState, state) && JRMCoreH.HairsT(display.tempState, "B") && JRMCoreH.HairsT(state, "B")) {
            display.tempState = state;
        } else if (JRMCoreH.HairsT(display.tempState, "A")) {
            if (!canUse && JRMCoreH.HairsT(display.tempState, state) && rage > 90) {
                display.stateChange += trTime;
                if (display.stateChange > 200) {
                    display.stateChange = 200;
                }
            } else if (canUse && JRMCoreH.HairsT(display.tempState, state) && rage > 0 && display.stateChange < rage * 2) {
                display.stateChange += trTime;
            } else if (JRMCoreH.HairsT(display.tempState, state)) {
                if (display.stateChange > 0) {
                    display.stateChange -= trTime;
                } else {
                    display.stateChange = 0;
                }

                if (display.state2Change > 0) {
                    display.state2Change -= trTime;
                } else {
                    display.state2Change = 0;
                }
            }
        } else if ((!JRMCoreH.HairsT(state, "B") || !superForm) && !JRMCoreH.HairsT(state, "B")) {
            if (!JRMCoreH.HairsT(display.tempState, state) && JRMCoreH.HairsT(state, "C")) {
                if (display.state2Change < 200) {
                    display.state2Change += trTime;
                }

                if (display.state2Change >= 200) {
                    display.state2Change = 200;
                    display.tempState = state;
                }
            }
        } else if (!canUse && JRMCoreH.HairsT(display.tempState, state) && rage > 90) {
            display.state2Change += trTime;
            if (display.state2Change > 200) {
                display.state2Change = 200;
            }
        } else if (canUse && JRMCoreH.HairsT(display.tempState, state) && rage > 0 && display.state2Change < rage * 2) {
            display.state2Change += trTime;
        } else if (display.state2Change > 200) {
            display.state2Change = 200;
            display.tempState = state;
        } else if (display.state2Change > 0) {
            display.state2Change -= trTime;
        } else if (display.state2Change != 0) {
            display.state2Change = 0;
        }

        if (canUse && hasAura) { //turbo/kaioken/charging hair animation
            if (JRMCoreH.HairsT(display.tempState, state) && display.auraTime < 50) {
                if (display.auraTime < 50 && display.auraType == 0) {
                    display.auraTime += arTime;
                }

                if (display.auraTime >= 50) {
                    display.auraType = 1;
                }

                if (display.auraTime < 20 && display.auraType == 1) {
                    display.auraType = 0;
                }

                if (display.auraTime > 0 && display.auraType == 1) {
                    display.auraTime -= arTime;
                }
            } else if (JRMCoreH.HairsT(display.tempState, state) && !JRMCoreH.HairsT(state, "A")) {
                if (display.auraType < 2) {
                    display.auraType = 2;
                }

                if (display.bendTime < 50 && display.auraType == 2) {
                    display.bendTime += arTime;
                }

                if (display.bendTime >= 50) {
                    display.auraType = 3;
                }

                if (display.bendTime < 20 && display.auraType == 3) {
                    display.auraType = 2;
                }

                if (display.bendTime > 0 && display.auraType == 3) {
                    display.bendTime -= arTime;
                }
            }
        } else {
            if (display.auraType > 0) {
                display.auraType = 0;
            }

            if (display.bendTime > 0) {
                display.bendTime -= 1;
            }

            if (display.auraTime > 0) {
                display.auraTime -= 1;
            }
        }

        ////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////
        //Hair fix
        if (display.tempState != state)
            display.tempState = state;

        int hairState = race == 5 && isTransforming ? 0 : state;
        if (!JRMCoreH.HairsT(display.tempState, "A") && JRMCoreH.HairsT(hairState, "A")) {
            if ((!JRMCoreH.HairsT(display.tempState, hairState) || rage == 0) && display.stateChange > 0) {
                display.stateChange -= trTime;
            }

            if (display.stateChange <= 0) {
                display.stateChange = 0;
                display.tempState = hairState;
            }
        }
        ////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////

        GL11.glPushMatrix();
        if (kamkeel.npcdbc.client.ClientProxy.renderingOutline) {
            GL11.glScalef(1.04f, 1.04f, 1.04f);
            GL11.glTranslatef(0, 0.03f, 0);
        }
       // display.hasEyebrows = false;
        int[] hairRightPosZ = new int[]{3, 2, 1, 0, 3, 2, 1, 3, 2, 3};
        int[] hairRightPosY = new int[]{0, 0, 0, 0, 1, 1, 1, 2, 2, 3};
        int[] hairLeftPosZ = new int[]{0, 1, 2, 3, 1, 2, 3, 2, 3, 3};
        int[] hairLeftPosY = new int[]{0, 0, 0, 0, 1, 1, 1, 2, 2, 3};
        int[] hairBackPosX = new int[]{0, 1, 2, 3, 0, 1, 2, 3, 0, 1, 2, 3, 0, 1, 2, 3};
        int[] hairBackPosY = new int[]{0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3};
        int[] hairTopPosX = new int[]{0, 1, 2, 3, 0, 1, 2, 3, 0, 1, 2, 3, 0, 1, 2, 3};
        int[] hairTopPosZ = new int[]{0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3};
        int[] hairPos = new int[]{0, 4, 14, 24, 40, 56};

        for (int face = 0; face < 56; ++face) {
            int l = dnsHair2(hairCode, face * 14);
            if (l != 0) {
                int X = dnsHair2(hairCode, face * 14 + 2);
                int Y = dnsHair2(hairCode, face * 14 + 4);
                int Z = dnsHair2(hairCode, face * 14 + 6);
                int B = dnsHair2(hairCode, face * 14 + 8);
                int P = dnsHair2(hairCode, face * 14 + 10);
                int T = dnsHair2(hairCode, face * 14 + 12);
                X = X > 82 ? 82 : (X < 18 ? 18 : X);
                Y = Y > 82 ? 82 : (Y < 18 ? 18 : Y);
                Z = Z > 82 ? 82 : (Z < 18 ? 18 : Z);
                B = B > 82 ? 82 : (B < 18 ? 18 : B);
                P = P > 82 ? 82 : (P < 18 ? 18 : P);
                T = T > 82 ? 82 : (T < 18 ? 18 : T);
                float x = (float) (X - 50) * 0.1F;
                float y = (float) (Y - 50) * 0.1F;
                float z = (float) (Z - 50) * 0.1F;
                float b = (float) (B - 50) * 0.1F;
                float p = (float) (P - 50) * 0.1F;
                int t = (int) ((float) (T - 18) * 1.62F);
                float Int = (float) t * 0.01F;
                boolean hpFront = face >= hairPos[0] && face < hairPos[1];
                boolean hpTop = face >= hairPos[4] && face < hairPos[5];
                boolean hpRight = face >= hairPos[1] && face < hairPos[2];
                boolean hpLeft = face >= hairPos[2] && face < hairPos[3];
                boolean hpBack = face >= hairPos[3] && face < hairPos[4];
                if (display.stateChange > 0 && l > 0) {
                    if (y > -1.0F && y < 1.0F && z > -1.0F && z < 1.0F && hpBack) {
                        x += (float) display.stateChange * Int * (x < 0.0F ? -0.01F : 0.01F) * (float) l * 0.01F;
                        x = x > 3.0F ? 3.0F : x;
                        x = x < -3.0F ? -3.0F : x;
                    }

                    if (y > -1.0F && y < 1.0F && x > -1.0F && x < 1.0F && !hpBack) {
                        z += (float) display.stateChange * Int * (z < 0.0F ? -0.01F : 0.01F);
                        z = z > 3.2F ? 3.2F : z;
                        z = z < -3.2F ? -3.2F : z;
                        if (!hpFront || x < 0.0F) {
                            x += (float) display.stateChange * Int * 0.01F;
                            x = x > 0.4F ? 0.4F : x;
                            x = x < -0.4F ? -0.4F : x;
                        }

                        if (z > 0.0F) {
                            boolean add = hpTop ? hairTopPosZ[face - hairPos[4]] == 0 || hairTopPosZ[face - hairPos[4]] == 2 : false;
                            boolean add2 = hpTop ? face % 4 == 0 || face % 4 == 3 : false;
                            b += (float) display.stateChange * Int * -0.02F;
                            b = b < (add && add2 ? 0.0F : -0.2F) ? (add && add2 ? 0.0F : -0.2F) : b;
                        } else if (z < 0.0F) {
                            boolean add = hpTop ? hairTopPosZ[face - hairPos[4]] == 0 || hairTopPosZ[face - hairPos[4]] == 2 : false;
                            boolean add2 = hpTop ? face % 4 == 0 || face % 4 == 3 : false;
                            b += (float) display.stateChange * Int * 0.02F;
                            b = b > (add && add2 ? 0.0F : 0.2F) ? (add && add2 ? 0.0F : 0.2F) : b;
                        }
                    } else if (y > -1.0F && y < 1.0F) {
                        x += (float) display.stateChange * Int * (x < 0.0F ? -0.01F : 0.01F);
                        x = x > 2.8F ? 2.8F : x;
                        x = x < -2.8F ? -2.8F : x;
                        if (b > 1.5F) {
                            x = x > 1.5F ? 1.5F : x;
                            x = x < -1.5F ? -1.5F : x;
                            b += (float) display.stateChange * Int * (b < 0.0F ? 0.03F : -0.03F);
                            b = b > 2.8F ? 2.8F : b;
                            b = b < -2.8F ? -2.8F : b;
                        }
                    } else if (x > -1.0F && x < 1.0F) {
                        z += (float) display.stateChange * Int * (z < 0.0F ? -0.01F : 0.01F);
                        z = z > 2.8F ? 2.8F : z;
                        z = z < -2.8F ? -2.8F : z;
                        if (b > 0.0F && z > 0.0F && y < 1.6F) {
                            z = z > 2.2F ? 2.2F : z;
                            z = z < -2.2F ? -2.2F : z;
                            float var91 = b + (float) display.stateChange * Int * -0.02F;
                            float var92 = var91 > b ? b : var91;
                            b = var92 < -b ? -b : var92;
                        } else if (b > 0.0F && z < 0.0F && y > 0.0F) {
                            z = z > 2.2F ? 2.2F : z;
                            z = z < -2.2F ? -2.2F : z;
                            float var89 = b + (float) display.stateChange * Int * -0.02F;
                            float var90 = var89 > b ? b : var89;
                            b = var90 < -b ? -b : var90;
                        } else if (y < -1.3F && b > 0.0F) {
                            z = z > 2.2F ? 2.2F : z;
                            z = z < -2.2F ? -2.2F : z;
                            b += (float) display.stateChange * Int * -0.02F;
                            b = b < 0.5F ? 0.5F : b;
                        }
                    }
                }

                if (display.state2Change > 0) {
                    if (y > -1.0F && y < 1.0F && x > -1.0F && x < 1.0F && hpFront) {
                        float Int2 = Int > 0.02F ? 0.6F : Int;
                        x += (float) display.state2Change * Int2 * 0.01F;
                        x = x > 0.2F ? 0.2F : x;
                        x = x < -0.2F ? -0.2F : x;
                        z += (float) display.state2Change * Int2 * (z < 0.0F ? -0.02F : 0.02F);
                        z = z > 2.8F ? 2.8F : z;
                        z = z < -2.8F ? -2.8F : z;
                    }

                    l = (int) ((float) l + (float) display.state2Change * 0.1F);
                    if (b < 0.0F) {
                        b += (float) display.state2Change * 5.0E-4F;
                        b = b >= 0.0F ? 0.2F : b;
                    }

                    if (b > 0.0F) {
                        b += (float) display.state2Change * -5.0E-4F;
                        b = b <= 0.0F ? -0.2F : b;
                    }
                }

                if (display.bendTime > 0) {
                    z += (float) display.bendTime * (z < 0.0F ? -0.0025F : 0.0025F);
                    b += (float) display.bendTime * (b > 0.0F ? -0.005F : 0.005F);
                    z = z > 3.2F ? 3.2F : z;
                    z = z < -3.2F ? -3.2F : z;
                }

                if (display.auraTime > 0) {
                    z += (float) display.auraTime * (z < 0.0F ? -0.0025F : 0.0025F);
                    b += (float) display.auraTime * (b > 0.0F ? -0.005F : 0.005F);
                    z = z > 3.2F ? 3.2F : z;
                    z = z < -3.2F ? -3.2F : z;
                }

                int lng = 0;
                if (!JRMCoreClient.mc.isGamePaused()) {
                    this.setRotation(this.hairall[lng + face * 4], x, y, z);
                    this.hairall[lng + face * 4].rotationPointX = -2.999F + (float) (face < 4 ? face * 2 : (face >= 14 && face < 24 ? 7 : (face >= 24 && face < 40 ? hairBackPosX[face - 4 - 10 - 10] * 2 : (face >= 40 && face < 56 ? hairTopPosX[face - 4 - 10 - 10 - 16] * 2 : -1))));
                    this.hairall[lng + face * 4].rotationPointZ = -3.999F + (face >= 4 && face < 14 ? (float) (hairRightPosZ[face - 4] * 2 + 1) : (face >= 14 && face < 24 ? (float) (hairLeftPosZ[face - 4 - 10] * 2 + 1) : (face >= 24 && face < 40 ? 8.0F : (face >= 40 && face < 56 ? (float) (hairTopPosZ[face - 4 - 10 - 10 - 16] * 2) + 0.9F : 0.0F))));
                    this.hairall[lng + face * 4].rotationPointY = -7.0F + (face >= 4 && face < 14 ? (float) (hairRightPosY[face - 4] * 2) : (face >= 14 && face < 24 ? (float) (hairLeftPosY[face - 4 - 10] * 2) : (face >= 24 && face < 40 ? (float) (hairBackPosY[face - 4 - 10 - 10] * 2) : -0.5F)));
                    this.hairall[1 + face * 4].rotateAngleY = 0.0F;
                    this.hairall[1 + face * 4].rotateAngleX = -0.0F;
                    this.hairall[2 + face * 4].rotateAngleY = 0.0F;
                    this.hairall[2 + face * 4].rotateAngleX = 0.0F;
                    this.hairall[3 + face * 4].rotateAngleY = 0.0F;
                    this.hairall[3 + face * 4].rotateAngleX = 0.0F;
                    if (!hpTop && !hpRight && !hpLeft) {
                        this.hairall[1 + face * 4].rotateAngleX = b * 0.3F * (p > 0.5F ? 1.0F - p * 0.3F : (p < -0.5F ? 1.0F + -p * 0.1F : 1.0F));
                        this.hairall[2 + face * 4].rotateAngleX = b * 0.3F;
                        this.hairall[3 + face * 4].rotateAngleX = b * 0.3F * (p > 0.5F ? 1.0F + p * 0.1F : (p < -0.5F ? 1.0F - -p * 0.3F : 1.0F));
                    } else {
                        int min = hpLeft ? 1 : -1;
                        this.hairall[1 + face * 4].rotateAngleZ = (float) min * b * 0.3F * (p > 0.5F ? 1.0F - p * 0.3F : (p < -0.5F ? 1.0F + -p * 0.1F : 1.0F));
                        this.hairall[2 + face * 4].rotateAngleZ = (float) min * b * 0.3F;
                        this.hairall[3 + face * 4].rotateAngleZ = (float) min * b * 0.3F * (p > 0.5F ? 1.0F + p * 0.1F : (p < -0.5F ? 1.0F - -p * 0.3F : 1.0F));
                    }
                }

                this.hairall[1 + face * 4].rotationPointX = 0.0F;
                this.hairall[1 + face * 4].rotationPointZ = 0.0F;
                this.hairall[1 + face * 4].rotationPointY = 1.5F;
                this.hairall[2 + face * 4].rotationPointX = 0.0F;
                this.hairall[2 + face * 4].rotationPointZ = 0.0F;
                this.hairall[2 + face * 4].rotationPointY = 2.5F;
                this.hairall[3 + face * 4].rotationPointX = 0.0F;
                this.hairall[3 + face * 4].rotationPointZ = 0.0F;
                this.hairall[3 + face * 4].rotationPointY = 2.5F;

                GL11.glPushMatrix();
                float tincs1 = (float) l < 33.0F ? (float) l / 33.0F : 1.0F;
                float tincs2 = (float) l > 33.0F && (float) l < 66.0F ? ((float) l - 33.0F) / 33.0F : ((float) l < 33.0F ? 0.0F : 1.0F);
                float tincs3 = (float) l > 66.0F ? ((float) l - 66.0F) / 33.0F : ((float) l < 66.0F ? 0.0F : 1.0F);
                this.hairall[lng + face * 4].lengthY = 1.0F;
                this.hairall[1 + face * 4].lengthY = tincs1;
                this.hairall[2 + face * 4].lengthY = tincs2;
                this.hairall[3 + face * 4].lengthY = tincs3;
                this.hairall[0 + face * 4].sizeXZ = 1.1F;
                this.hairall[1 + face * 4].sizeXZ = 1.0F;
                this.hairall[2 + face * 4].sizeXZ = 0.9F;
                this.hairall[3 + face * 4].sizeXZ = 0.8F;
                this.hairall[1 + face * 4].showModel = (float) l > 0.0F;
                this.hairall[2 + face * 4].showModel = (float) l > 33.0F;
                this.hairall[3 + face * 4].showModel = (float) l > 66.0F;
//                if (face > 23)
//                    new Color(0x830099, 1).glColor();
//                else if (face < 4)
//                    new Color(0x830099, 1).glColor();
//                else
//                    new Color(0xedebe9, 1).glColor();
                this.hairall[lng + face * 4].render(0.0625f);
                GL11.glPopMatrix();
            }
        }

        GL11.glScalef(1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }
}
