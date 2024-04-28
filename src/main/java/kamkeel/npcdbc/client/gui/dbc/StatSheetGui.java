package kamkeel.npcdbc.client.gui.dbc;

import JinRyuu.JRMCore.*;
import cpw.mods.fml.common.FMLCommonHandler;
import kamkeel.npcdbc.client.gui.dbc.constants.GuiInfo;
import kamkeel.npcdbc.data.DBCData;
import kamkeel.npcdbc.mixin.IDBCGuiScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import static JinRyuu.JRMCore.JRMCoreGuiScreen.kqGW3Z;

public class StatSheetGui extends AbstractJRMCGui {

    private static final ResourceLocation icons = new ResourceLocation("jinryuumodscore:icons.png");
    private static final ResourceLocation icons3 = new ResourceLocation("jinryuumodscore:icons3.png");
    public static boolean overrideBaseDBC = false;
    private int upgradeCounter;

    private GuiButton[] buttons = new GuiButton[7];

    public StatSheetGui() {
        super(10);
    }

    protected StatSheetGui(int guiReplacementID){
        super(guiReplacementID);
    }

    @Override
    public void updateScreen(){

        DBCData dbcClient = DBCData.getClient();
        if(!overrideBaseDBC){
            JRMCoreGuiScreen DBCScreen = new JRMCoreGuiScreen(0);
            ((IDBCGuiScreen) DBCScreen).setGuiIDPostInit(10);
            FMLCommonHandler.instance().showGuiScreen(DBCScreen);
            return;
        }
        if(dbcClient.Accept == 0){
            JRMCoreGuiScreen DBCScreen = new JRMCoreGuiScreen(0);
            ((IDBCGuiScreen) DBCScreen).setGuiIDPostInit(0);
            FMLCommonHandler.instance().showGuiScreen(DBCScreen);
            return;
        }

        this.guiWidthOffset = (this.width - menuImageWidth) / 2;
        this.guiHeightOffset = (this.height - menuImageHeight) / 2;

        dynamicElements.clear();

        //this.dynamicLabels.add(new JRMCoreLabel("Testing", DBCData.getClient().Ki+"", guiWidthOffset+10, guiHeightOffset+10, -1));
        boolean max = JRMCoreH.getPlayerLevel(JRMCoreH.PlyrAttrbts) >= JRMCoreH.getPlayerLevel(kqGW3Z(false) * 6);

        int index = 0;

        //Level
        this.dynamicElements.add(new JRMCoreLabel(
            String.format("%s: §8%s", JRMCoreH.trl("jrmc", "Level"), JRMCoreH.numSep(JRMCoreH.getPlayerLevel(JRMCoreH.PlyrAttrbts))),
            (max ? JRMCoreH.trl("jrmc", "LevelMax") : JRMCoreH.trl("jrmc", "LevelNext", JRMCoreH.cllr + JRMCoreH.attrLvlNext(JRMCoreH.PlyrAttrbts) + JRMCoreH.cldgy)),
            this.guiWidthOffset+5,
            this.guiHeightOffset+5+index*10
        ));
        index++;

        //TP
        String requiredTP = JRMCoreH.cct(JRMCoreH.trl("jrmc", "RequiredTP"), JRMCoreH.cllr + JRMCoreH.numSep(JRMCoreH.attrCst(JRMCoreH.PlyrAttrbts, 0)) + JRMCoreH.cldgy, "");
        this.dynamicElements.add(new JRMCoreLabel(
            String.format("%s: §8%s", JRMCoreH.trl("jrmc", "TP"), JRMCoreH.numSep(JRMCoreH.curTP)),
            JRMCoreH.trl("jrmc", "TrainingPoints") + ",\n "+requiredTP,
            this.guiWidthOffset+5,
            this.guiHeightOffset+5+index*10
        ));
        index++;


        String raceText = String.format("%s: §8%s", JRMCoreH.trl("jrmc", "Race"), JRMCoreH.Races[JRMCoreH.Race]);
        this.dynamicElements.add(new JRMCoreLabel(
            raceText,
            null,
            this.guiWidthOffset+5,
            this.guiHeightOffset+5+index*10
        ));

        this.dynamicElements.add(new GuiIcon(
            icons3,
            guiWidthOffset+5+Minecraft.getMinecraft().fontRenderer.getStringWidth(raceText),
            guiHeightOffset+2+index*10,
            0,
            (JRMCoreH.dnsGender(JRMCoreH.dns) < 1 ? 128 : 112),
            16,
            16
        ));

        index++;



        //@TODO Add proper hovers
        //@TODO Add proper translation
        //@TODO Add custom form support
        this.dynamicElements.add(new JRMCoreLabel(
            String.format("%s: §8%s", JRMCoreH.trl("jrmc", "TRState"), JRMCoreH.TransNms[JRMCoreH.Race][JRMCoreH.State]),
            null,
            this.guiWidthOffset+5,
            this.guiHeightOffset+5+index*10
        ));
        index++;

        this.dynamicElements.add(new JRMCoreLabel(
            String.format("%s: §8%s", JRMCoreH.trl("jrmc", "Class"), JRMCoreH.ClassesDBC[JRMCoreH.Class]),
            JRMCoreH.trl("jrmc", JRMCoreH.ClassesDBCDesc[JRMCoreH.Class]),
            this.guiWidthOffset+5,
            this.guiHeightOffset+5+index*10,
            200
        ));
        index++;

        this.dynamicElements.add(new JRMCoreLabel(
            String.format("%s: §8%s", JRMCoreH.trl("jrmc", "Alignment"), JRMCoreH.algnCur(JRMCoreH.align)),
            JRMCoreH.trl("jrmc", "AlignmentDesc", JRMCoreH.align+"%"),
            this.guiWidthOffset+5,
            this.guiHeightOffset+5+index*10
        ));
        index++;

        //Line break between race info and stats
        index++;


        this.dynamicElements.add(new JRMCoreLabel(
            JRMCoreH.trl("jrmc", "Attributes"),
            null,
            this.guiWidthOffset+5,
            this.guiHeightOffset+5+index*10
        ));
        index++;

        String[] attrNames = new String[]{
            "STR", "DEX", "CON", "WIL", "MND", "SPI"
        };

        int upgradeCost = JRMCoreH.attrCst(JRMCoreH.PlyrAttrbts, this.upgradeCounter);
        boolean isFused = false; //@TODO add logic for this

        for(int i = 0; i < 6; i++){
            boolean isMaxed = !(JRMCoreGuiScreen.kqGW3Z(isFused) > JRMCoreH.PlyrAttrbts[i]);
            boolean canAfford = JRMCoreH.curTP >= upgradeCost;
            boolean isButtonEnabled = !isFused && !isMaxed && canAfford;
            String upgradeTooltip = null;

            int yPos = guiHeightOffset+i*10+index*10;

            if(isFused){
                upgradeTooltip = JRMCoreH.trl("dbc", "cantupgradef");
            }else if(isMaxed){
                upgradeTooltip = JRMCoreH.trl("jrmc", "AttributeMaxed");
            }

            GuiButton button = buttons[i];
            if(button == null){
                button = new JRMCoreGuiButtonsA3(i, guiWidthOffset + 3, yPos+3, 10, 2, isButtonEnabled);
                buttons[i] = button;
                this.buttonList.add(button);
            }else{
                button.enabled = isButtonEnabled;
            }
            if(upgradeTooltip != null){
                this.dynamicElements.add(new JRMCoreLabel(button, upgradeTooltip));
            }

            int originalStatVal = JRMCoreH.PlyrAttrbts[i];
            int modifiedStatVal = originalStatVal; //@TODO Replace with multi

            String attributeDesc = JRMCoreH.attrNms(1, i) + ", "+ JRMCoreH.trl("jrmc", JRMCoreH.attrDsc[1][i]);

            if(originalStatVal != modifiedStatVal){
                attributeDesc = "Modified: \n" +attributeDesc;
            }

            this.dynamicElements.add(new JRMCoreLabel(
                String.format("§8%s: %s%s", attrNames[i], "§4", JRMCoreH.numSep(modifiedStatVal)),
                attributeDesc,
                guiWidthOffset+15,
                yPos+5
            ));
        }
        index+=6;

        boolean allMaxed = JRMCoreH.acm(JRMCoreH.PlyrAttrbts);

        GuiButton upgradeButton = buttons[6];
        if(upgradeButton == null){
            upgradeButton = new JRMCoreGuiButtonsA3(
                6,
                guiWidthOffset+7,
                guiHeightOffset + index*10 + 3,
                10,
                2,
                (!allMaxed && !isFused)
            );
            buttons[6] = upgradeButton;
            this.buttonList.add(upgradeButton);
        }else{
            upgradeButton.enabled = (!allMaxed && !isFused);
        }


        this.dynamicElements.add(new JRMCoreLabel(
            " §8UC: " + JRMCoreH.cldb + JRMCoreH.numSep(upgradeCost)+" TP "+(upgradeCounter > 0 ? "x"+JRMCoreH.attributeMultiplier(this.upgradeCounter) : ""),
            "lololol",
            guiWidthOffset+15,
            guiHeightOffset+5+index*10
            )
        );



    }


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks){
        this.drawBackground();

        drawStatusEffects();
        drawAlignmentBar();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void initGui(){
        super.initGui();
        updateScreen();

        //Change screens
        String s = "Switch to "+(overrideBaseDBC ? "Normal" : "§aEnhanced") +" GUI";
        int i = this.fontRendererObj.getStringWidth(s)+10;
        this.buttonList.add(new JRMCoreGuiButtons00(303030303, (this.width -i)/2, guiHeightOffset - 30, i + 8, 20, s, 0));



        this.guiWidthOffset = (this.width - menuImageWidth) / 2;
        this.guiHeightOffset = (this.height - menuImageHeight) / 2;

        addServerButtons();
        addDifficultyButton();
    }

    @Override
    protected void actionPerformed(GuiButton button){
        super.actionPerformed(button);
        int id = button.id;
        if(id == 303030303)
            overrideBaseDBC = false;

        if(id >= 0 && id <= 5){
            if(!JRMCoreH.isFused()){
                JRMCoreH.Upg((byte) (id+upgradeCounter*6));
            }
        }
        if(id == 6){
            this.upgradeCounter++;
            if(this.upgradeCounter >= 3)
                this.upgradeCounter = 0;
        }

    }

    protected void drawAlignmentBar(){
        int imgHeight = this.menuImageHeight-10;
        this.guiHeightOffset = (this.height - imgHeight) / 2;
        this.guiWidthOffset = (this.width - this.menuImageWidth) / 2;

        mc.getTextureManager().bindTexture(icons);
        GL11.glPushMatrix();
        int alignment1;
        int alignment2;

        if(JRMCoreH.Algnmnt_Good(JRMCoreH.align)){
            alignment1 = 654591;
            alignment2 = 6028287;
        } else if (JRMCoreH.Algnmnt_Neut(JRMCoreH.align)) {
            alignment1 = 9127101;
            alignment2 = 11042302;
        } else {
            alignment1 = 16726090;
            alignment2 = 16544131;
        }

        float h2 = (float)(alignment1 >> 16 & 255) / 255.0F;
        float h3 = (float)(alignment1 >> 8 & 255) / 255.0F;
        float h4 = (float)(alignment1 & 255) / 255.0F;
        float h1 = 1.0F;
        GL11.glColor4f(h1 * h2, h1 * h3, h1 * h4, 0.5F);
        this.drawTexturedModalRect(guiWidthOffset + 8, guiHeightOffset - 11, 8, 174, 241, 7);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
        this.drawTexturedModalRect(guiWidthOffset, guiHeightOffset - 10, 0, 169, menuImageWidth, 5);
        int max = menuImageWidth - 20;
        if (max < 1) {
            max = 1;
        }

        double maxperc = (double)max * 0.01D;
        int alignmentAdjusted = (int)(maxperc * (double)JRMCoreH.align);
        if (alignmentAdjusted > menuImageWidth) {
            alignmentAdjusted = menuImageWidth;
        }

        h2 = (float)(alignment2 >> 16 & 255) / 255.0F;
        h3 = (float)(alignment2 >> 8 & 255) / 255.0F;
        h4 = (float)(alignment2 & 255) / 255.0F;
        GL11.glColor4f(h1 * h2, h1 * h3, h1 * h4, 1.0F);
        this.drawTexturedModalRect((this.width - 5) / 2 - max / 2 + alignmentAdjusted - 4, guiHeightOffset-14, 0, 182, 11, 13);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    protected void drawStatusEffects(){
        this.guiWidthOffset = (this.width - menuImageWidth) / 2;
        this.guiHeightOffset = (this.height - menuImageHeight) / 2;
        JRMCoreClient.bars.showSE(this.width/4, guiHeightOffset - 35, 0, 0);
    }
    private void addDifficultyButton() {
        GuiInfo.ReferenceIDs ref = GuiInfo.ReferenceIDs.DIFFICULTY;
        String translation = ref.getTranslation();
        int stringWidth = fontRendererObj.getStringWidth(translation)+8;
        this.buttonList.add(new JRMCoreGuiButtons00(ref.getButtonId(), width/2 + 90 - stringWidth / 2, height/2 + 55, stringWidth, 20, translation, 8046079));
    }
}
