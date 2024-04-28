package kamkeel.npcdbc.client.gui.dbc;

import JinRyuu.JRMCore.*;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.client.gui.dbc.constants.GuiInfo;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.mixin.IDBCGuiScreen;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import static JinRyuu.JRMCore.JRMCoreGuiScreen.kqGW3Z;

@SideOnly(Side.CLIENT)
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
        PlayerDBCInfo dataClient = PlayerDataUtil.getClientDBCInfo();


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

        String formColor = "";
        String formName;
        Form customForm = dbcClient.getForm();

        boolean isLegendary = dbcClient.containsSE(14);
        boolean isMajin = dbcClient.containsSE(12);


        if(isMajin && isLegendary){
            formColor = "§5";
        }else if(isMajin){
            formColor = "§c";
        }else if(isLegendary){
            formColor = "§a";
        }

        boolean isFused = dbcClient.containsSE(10) || dbcClient.containsSE(11);
        if(isFused){
            formColor = "§d";
        }

        if(customForm != null){
            formName = customForm.getMenuName();
            formColor = dataClient.getFormColorCode(customForm);
        }else {
            formName = JRMCoreH.trl("jrmc", JRMCoreH.TransNms[dbcClient.Race][dbcClient.State]);


            boolean ascendedAboveBase = (dbcClient.Race == 4 && dbcClient.State > 4) || dbcClient.State > 0;
            if (formColor.isEmpty() && ascendedAboveBase)
                formColor = "§6";
        }
        formName = formColor + formName;
        formColor = (formColor.equals("§4") ? "" : formColor); //Makes stats pop out when your form color is the same as the default stat color

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
            String.format("%s: §8%s", JRMCoreH.trl("jrmc", "TRState"), formColor+formName),
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
            String.format("%s: §4%s", JRMCoreH.trl("jrmc", "Alignment"), JRMCoreH.algnCur(JRMCoreH.align)),
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

        int[] statVals = new int[3];

        int upgradeCost = JRMCoreH.attrCst(JRMCoreH.PlyrAttrbts, this.upgradeCounter);
        boolean canAffordUpgrade = JRMCoreH.curTP >= upgradeCost;
        for(int i = 0; i < 6; i++){
            boolean isMaxed = !(JRMCoreGuiScreen.kqGW3Z(isFused) > JRMCoreH.PlyrAttrbts[i]);
            boolean isButtonEnabled = !isFused && !isMaxed && canAffordUpgrade;

            boolean isSTRDEXWIL = (i < 2 || i == 3);

            String upgradeTooltip = null;

            int yPos = guiHeightOffset+i*10+index*10;

            if(isFused){
                upgradeTooltip = JRMCoreH.trl("dbc", "cantupgradef");
            }else if(isMaxed){
                upgradeTooltip = JRMCoreH.trl("jrmc", "AttributeMaxed");
            }else if(!canAffordUpgrade){
                upgradeTooltip = JRMCoreH.trl("jrmc", "cantupgrade")+ "\n" + JRMCoreH.trl("jrmc", "RequiredTP", "§4"+JRMCoreH.numSep(upgradeCost));
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

            if(isSTRDEXWIL){
                modifiedStatVal = JRMCoreH.getPlayerAttribute(JRMCoreClient.mc.thePlayer, JRMCoreH.PlyrAttrbts, i, JRMCoreH.State, JRMCoreH.State2, JRMCoreH.Race, JRMCoreH.PlyrSkillX, JRMCoreH.curRelease, JRMCoreH.getArcRsrv(), JRMCoreH.StusEfctsMe(14), JRMCoreH.StusEfctsMe(12), JRMCoreH.StusEfctsMe(5), JRMCoreH.StusEfctsMe(13), JRMCoreH.StusEfctsMe(19), JRMCoreH.StusEfctsMe(20), JRMCoreH.Pwrtyp, JRMCoreH.PlyrSkills, isFused, JRMCoreH.getMajinAbsorption());;
                if(i < 2)
                    statVals[i] = modifiedStatVal;
                else if(i == 3)
                    statVals[2] = modifiedStatVal;
            }

            String statDisplay = JRMCoreH.numSep(modifiedStatVal);

            String attributeDesc = JRMCoreH.attrNms(1, i) + ", "+ JRMCoreH.trl("jrmc", JRMCoreH.attrDsc[1][i]);

            if(originalStatVal != modifiedStatVal){
                attributeDesc = JRMCoreH.trl("jrmc", "Modified") +": §4" + formColor+statDisplay+"\n§8"
                    + JRMCoreH.trl("jrmc", "Original") +": §4" + JRMCoreH.numSep(originalStatVal)+"\n§8"
                    +attributeDesc;
            }

            this.dynamicElements.add(new JRMCoreLabel(
                String.format("§8%s: §4%s%s", attrNames[i], (isSTRDEXWIL ? formColor : ""), statDisplay),
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


        String upgradeDescription = JRMCoreH.trl("jrmc", "UCnam");
        int descriptionWidth = this.mc.fontRenderer.getStringWidth(upgradeDescription+" ");

        if (allMaxed) {
            upgradeDescription += "\n§c" + JRMCoreH.cct(JRMCoreH.trl("jrmc", "AttributeAllMaxed"));
        } else if (upgradeCost == 0 || !canAffordUpgrade) {
            upgradeDescription += "\n§c" + JRMCoreH.cct(JRMCoreH.trl("jrmc", "cantupgrade"));
        } else if (isFused) {
            upgradeDescription += "\n§c" + JRMCoreH.cct(JRMCoreH.trl("dbc", "cantupgradef"));
        }else if(upgradeCounter > 0){
            upgradeDescription += ", ";
        }

        this.dynamicElements.add(new JRMCoreLabel(
            " §8UC: " + JRMCoreH.cldb + (allMaxed || upgradeCost <= 0 ? JRMCoreH.trl("jrmc", "LimitReached") : JRMCoreH.numSep(upgradeCost)+" TP "+(upgradeCounter > 0 ? "x"+JRMCoreH.attributeMultiplier(this.upgradeCounter) : "")),
            upgradeDescription,
            guiWidthOffset+15,
            guiHeightOffset+5+index*10,
            descriptionWidth
            )
        );

        index = 0;

        this.dynamicElements.add(new JRMCoreLabel(
           String.format("%s: §8%s%%", JRMCoreH.trl("jrmc", "PowerRelease"), dbcClient.Release),
           null,
            guiWidthOffset+133,
            guiHeightOffset+5+index*10
        ));
        index++;
        index++;


        this.dynamicElements.add(new JRMCoreLabel(
            JRMCoreH.trl("jrmc", "Stats"),
            null,
            guiWidthOffset+133,
            guiHeightOffset+5+index*10
        ));
        index++;

        System.out.println(String.format("%s, %s", JRMCoreH.Class, dbcClient.Class));
        int stat = JRMCoreH.stat(mc.thePlayer, 0, 1, 0, statVals[0], dbcClient.Race, JRMCoreH.Class, 0);
        //float inc = JRMCoreH.statInc(JRMCoreH.Pwrtyp, 0, 1, JRMCoreH.Race, JRMCoreH.Class, 0.0F);
        int curAtr = (int)((double)stat * 0.01D * (double)JRMCoreH.curRelease * (double)JRMCoreH.weightPerc(0));
        long longValue = (long)curAtr + (long)0;
        if (longValue > 2147483647L) {
            longValue = 2147483647L;
        }

        this.dynamicElements.add(new JRMCoreLabel(
            String.format("§8%s: §4%s", JRMCoreH.trl("jrmc", "mleMC"), formColor+JRMCoreH.numSep((long) (stat*(dbcClient.Release / 100.0F)))),
            "description",
            guiWidthOffset+133,
            guiHeightOffset+5+index*10
        ));
        index++;


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
