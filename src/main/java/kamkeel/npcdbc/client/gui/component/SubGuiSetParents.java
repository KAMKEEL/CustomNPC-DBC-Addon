package kamkeel.npcdbc.client.gui.component;

import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.data.form.Form;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.StatCollector;
import noppes.npcs.client.gui.util.*;

import java.util.ArrayList;
import java.util.HashMap;

public class SubGuiSetParents extends SubGuiInterface implements ICustomScrollListener {

    private HashMap<String, Integer> data = new HashMap<>();

    private final Form form;
    private GuiCustomScroll dbcRaces;
    private GuiCustomScroll dbcForms;
    private final HashMap<Integer, HashMap<Integer, String>> allForms = new HashMap<>();
    private final ArrayList<String> races = new ArrayList<>();
    private int selectedRace = 0;

    public SubGuiSetParents(Form form1){
        this.closeOnEsc = true;
        this.form = form1;
        this.drawDefaultBackground = true;

        setBackground("menubg.png");
        xSize = 360;
        ySize = 216;

        allForms.put(DBCRace.HUMAN, DBCForm.getFormsMap(DBCRace.HUMAN));
        allForms.put(DBCRace.SAIYAN, DBCForm.getFormsMap(DBCRace.SAIYAN));
        allForms.put(DBCRace.HALFSAIYAN, DBCForm.getFormsMap(DBCRace.HALFSAIYAN));
        allForms.put(DBCRace.NAMEKIAN, DBCForm.getFormsMap(DBCRace.NAMEKIAN));
        allForms.put(DBCRace.ARCOSIAN, DBCForm.getFormsMap(DBCRace.ARCOSIAN));
        allForms.put(DBCRace.MAJIN, DBCForm.getFormsMap(DBCRace.MAJIN));

        races.add("Human");
        races.add("Saiyan");
        races.add("Half-Saiyan");
        races.add("Namekian");
        races.add("Arcosian");
        races.add("Majin");
    }

    @Override
    public void initGui(){
        super.initGui();

        addLabel(new GuiNpcLabel(1, StatCollector.translateToLocal("Races"), guiLeft + 22, guiTop + 11));
        if(dbcRaces == null){
            dbcRaces = new GuiCustomScroll(this,0);
            dbcRaces.setSize(150, 180);
        }
        dbcRaces.guiLeft = guiLeft + 20;
        dbcRaces.guiTop = guiTop + 24;
        dbcRaces.setUnsortedList(races);
        dbcRaces.selected = selectedRace;
        this.addScroll(dbcRaces);

        addLabel(new GuiNpcLabel(2, StatCollector.translateToLocal("Forms"), guiLeft + 192, guiTop + 11));
        if(dbcForms == null){
            dbcForms = new GuiCustomScroll(this,1);
            dbcForms.setSize(150, 155);
        }
        dbcForms.guiLeft = guiLeft + 190;
        dbcForms.guiTop = guiTop + 24;
        dbcForms.setUnsortedList(new ArrayList<>(allForms.get(selectedRace).values()));
        dbcForms.selected = form.getFormRequirement(selectedRace);
        this.addScroll(dbcForms);

        addButton(new GuiNpcButton(10, guiLeft + 219, guiTop + 184, 95, 20, "gui.remove"));
        addButton(new GuiNpcButton(1, guiLeft + xSize - 25, guiTop + ySize - 25, 20, 20, "X"));
    }

    @Override
    public void actionPerformed(GuiButton button){
        int id = button.id;

        if(id == 1){
            this.close();
        }
        if(id == 10){
            form.removeFormRequirement(selectedRace);
            dbcForms.selected = -1;
        }
    }

    @Override
    public void customScrollClicked(int i, int i1, int i2, GuiCustomScroll guiCustomScroll) {
        // Do Selection
        if(guiCustomScroll.id == 0){
            selectedRace = guiCustomScroll.selected;
            initGui();
        }
        if(guiCustomScroll.id == 1){
            int selected = guiCustomScroll.selected == 11 ? 14 : guiCustomScroll.selected == 12 ? 15 : guiCustomScroll.selected; //ssj4 and ssbevo fix
            form.addFormRequirement(selectedRace, (byte) selected);
        }
    }

}
