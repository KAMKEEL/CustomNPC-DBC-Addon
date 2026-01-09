package kamkeel.npcdbc.client.gui.global.form;

import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.api.form.IForm;
import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormMastery;
import kamkeel.npcdbc.data.form.FormMasteryLinkData;
import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.gui.util.GuiCustomScroll;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.client.gui.util.GuiNpcTextField;
import noppes.npcs.client.gui.util.ICustomScrollListener;
import noppes.npcs.client.gui.util.SubGuiInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SubGuiFormMasteryLink extends SubGuiInterface implements ICustomScrollListener {
    private GuiCustomScroll scrollRaces;
    private GuiCustomScroll scrollForms;

    private boolean checkCustomForms;
    private String formSearch = "";
    private String selectedFormString;

    public SubGuiFormMastery parent;
    public Form form;
    public FormMastery formMastery;

    private List<String> customFormList;

    private HashMap<Integer, ArrayList<String>> raceForms = new HashMap<>();
    private HashMap<Integer, HashMap<String, Integer>> raceFormIDLookUp = new HashMap<>();

    private int selectedRace = -1;
    private ArrayList<String> raceNames = new ArrayList<>();

    private static int[] PROPER_NON_RACIAL_IDS = {
        DBCForm.Kaioken,
        DBCForm.Mystic,
        DBCForm.UltraInstinct,
        DBCForm.GodOfDestruction
    };

    public SubGuiFormMasteryLink(SubGuiFormMastery subGuiFormMastery) {
        this.parent = subGuiFormMastery;
        this.form = parent.form;
        this.formMastery = parent.mastery;

        this.drawDefaultBackground = true;
        this.title = "";
        this.setBackground("menubg.png");
        this.xSize = 366;
        this.ySize = 226;

//        customFormList = ((List<String>) (Object) Arrays.asList(((GuiNPCManageForms) parent.getParent()).data.keySet().toArray()));

        customFormList = new ArrayList<>();
        for (Map.Entry<Integer, Form> entry : FormController.getInstance().customForms.entrySet()) {
            customFormList.add(entry.getValue().name);
        }

//        List<String> test = Arrays.asList("Test", "TestForm2");
        for (int i = 0; i < JRMCoreH.Races.length; i++) {
            raceNames.add(JRMCoreH.trl("jrmc", JRMCoreH.Races[i]));
            ArrayList<String> forms = new ArrayList<>();

            HashMap<String, Integer> formNameID = new HashMap<>();
            for (int y = 0; y < JRMCoreH.TransNms[i].length; y++) {
                if (y == 12 || y == 13)
                    continue;
                String formName = JRMCoreH.trl("jrmc", JRMCoreH.TransNms[i][y]);
                forms.add(formName);
                formNameID.put(formName, y);
            }

            for (int x = 0; x < 4; x++) {
                String nonRacialName = JRMCoreH.transNonRacial[x];
                forms.add(nonRacialName);
                formNameID.put(nonRacialName, PROPER_NON_RACIAL_IDS[x]);
            }


            raceFormIDLookUp.put(i, formNameID);
            raceForms.put(i, forms);
        }
    }

    public void initGui() {
        super.initGui();
        this.addButton(new GuiNpcButton(1, this.guiLeft + this.xSize - 70, this.guiTop + 4, 60, 20, "gui.done"));
        this.addButton(new GuiNpcButton(2, this.guiLeft + this.xSize - 70, this.guiTop + 4 + 23, 60, 20, new String[]{"DBC", "Custom"}, this.checkCustomForms ? 1 : 0));
        this.addButton(new GuiNpcButton(3, this.guiLeft + this.xSize - 70, this.guiTop + 4 + 23 * 3 - 12, 60, 20, "Remove"));
        this.addButton(new GuiNpcButton(4, this.guiLeft + this.xSize - 70, this.guiTop + 4 + 23 * 4 - 12, 60, 20, "Remove All"));

        if (this.scrollRaces == null) {
            this.scrollRaces = new GuiCustomScroll(this, 0, 0);
            this.scrollRaces.setSize(90, 183);
            this.scrollRaces.setUnsortedList(raceNames);
        }

        if (this.selectedRace >= 0) {
            this.scrollRaces.selected = this.selectedRace;
        }

        this.scrollRaces.guiLeft = this.guiLeft + 4;
        this.scrollRaces.guiTop = this.guiTop + 4;
        this.addScroll(this.scrollRaces);

        if (this.scrollForms == null) {
            this.scrollForms = new GuiCustomScroll(this, 1, 0);
            this.scrollForms.setSize(194, 183);
        }


        this.scrollForms.guiLeft = this.guiLeft + 95;
        this.scrollForms.guiTop = this.guiTop + 4;
        this.addScroll(this.scrollForms);
        this.addTextField(new GuiNpcTextField(1, this, this.fontRendererObj, this.guiLeft + 4, this.guiTop + 189, 285, 20, this.formSearch));
    }

    protected void actionPerformed(GuiButton guibutton) {
        super.actionPerformed(guibutton);

        if (guibutton.id == 1) {
            this.close();
        }

        if (guibutton.id == 2) {
            checkCustomForms = !checkCustomForms;

            this.scrollForms.selected = -1;
            this.scrollForms.resetScroll();
            if (this.selectedRace >= 0) {
                this.scrollForms.setUnsortedList(getCorrectFormList());
                this.scrollForms.setSelected(getNameFromLinkData());
            }
            this.getTextField(1).setText("");
            this.formSearch = "";
        }

        if (guibutton.id == 3) {
            formMastery.masteryLink.removeLinkData(selectedRace);
            formSearch = "";
            selectedFormString = null;
            this.scrollForms.setSelected(null);
        }

        if (guibutton.id == 4) {
            formMastery.masteryLink.removeAllLinkData();
            this.selectedRace = -1;
            formSearch = "";
            selectedFormString = null;
            this.scrollForms = null;
            this.scrollRaces = null;
        }

        initGui();
    }

    public void customScrollDoubleClicked(String selection, GuiCustomScroll scroll) {
//        if (this.selectedResource != null) {
//            this.close();
//        }
    }

    @Override
    public void customScrollClicked(int i, int i1, int i2, GuiCustomScroll guiCustomScroll) {
        if (guiCustomScroll.id == 0) {
//            this.selectedRace = guiCustomScroll.getSelected();
//            this.selectedResource = null;
            this.selectedRace = guiCustomScroll.selected;
            this.scrollForms.selected = -1;
            this.scrollForms.resetScroll();
            if (this.selectedRace >= 0) {
                this.scrollForms.setUnsortedList(getCorrectFormList());
                this.scrollForms.setSelected(getNameFromLinkData());
            }
            this.getTextField(1).setText("");
            this.formSearch = "";
        }

        if (guiCustomScroll.id == 1 && selectedRace >= 0) {
//            mc.thePlayer.addChatComponentMessage(new ChatComponentText(""+guiCustomScroll.selected));
            this.selectedFormString = guiCustomScroll.getSelected();
            int formID = -1;
            if (checkCustomForms) {
                Form form = (Form) FormController.getInstance().get(guiCustomScroll.getSelected());
                if (form != null)
                    formID = form.id;
            } else {
                formID = raceFormIDLookUp.get(selectedRace).getOrDefault(guiCustomScroll.getSelected(), -1);
            }
            if (formID >= 0)
                formMastery.masteryLink.setMasteryLink(formID, selectedRace, checkCustomForms);
            else
                formMastery.masteryLink.removeLinkData(selectedRace);
        }

        this.initGui();
    }

    private String getNameFromLinkData() {
        if (!formMastery.masteryLink.hasLinkData(this.selectedRace))
            return null;

//        if(selectedRace == -1)
//            return null;

        FormMasteryLinkData.LinkData linkData = formMastery.masteryLink.masteryLinks.get(this.selectedRace);
        if (linkData.isCustomLink) {
            IForm form = FormController.Instance.get(linkData.formID);
            if (form != null)
                return form.getName();
            else
                return null;
        } else {
            int formID = DBCForm.getJRMCFormID(linkData.formID, this.selectedRace);
            if (formID < 0)
                return null;
            if (formID < JRMCoreH.trans[this.selectedRace].length) {
                return JRMCoreH.trl("jrmc", JRMCoreH.TransNms[selectedRace][formID]);
            } else {
                return JRMCoreH.transNonRacial[formID - JRMCoreH.trans[this.selectedRace].length];
            }
        }
    }

    public void keyTyped(char c, int i) {
        super.keyTyped(c, i);

        if (this.getTextField(1) != null && this.getTextField(1).isFocused()) {
            if (this.formSearch.equals(this.getTextField(1).getText())) {
                return;
            }

            this.formSearch = this.getTextField(1).getText().toLowerCase();
            this.scrollForms.resetScroll();
            this.scrollForms.setUnsortedList(this.getFormSearch());
            this.scrollForms.setSelected(this.selectedFormString);
        }

    }

    private List<String> getFormSearch() {
        if (this.selectedRace == -1) {
            return new ArrayList();
        } else if (this.formSearch.isEmpty()) {
            return this.getCorrectFormList();
        } else {
            List<String> list = new ArrayList();
            Iterator var2 = (this.getCorrectFormList()).iterator();

            while (var2.hasNext()) {
                String name = (String) var2.next();
                if (name.toLowerCase().contains(this.formSearch)) {
                    list.add(name);
                }
            }

            return list;
        }
    }

    private List<String> getCorrectFormList() {
        if (checkCustomForms)
            return customFormList;
        return raceForms.get(selectedRace);
    }

}
