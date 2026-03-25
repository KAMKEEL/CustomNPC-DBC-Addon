package kamkeel.npcdbc.data.race.progression;

import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormRace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FormTree {
    private final String raceNamespace;
    private final Map<Integer, List<Form>> levels = new LinkedHashMap<>();

    public FormTree(String raceNamespace) {
        this.raceNamespace = raceNamespace;
    }

    public void add(int level, Form form) {
        levels.computeIfAbsent(level, k -> new ArrayList<>()).add(form);
    }

    public List<Form> getFormsAtLevel(int level) {
        return Collections.unmodifiableList(levels.getOrDefault(level, Collections.emptyList()));
    }

    public Map<Integer, List<Form>> getLevels() {
        return Collections.unmodifiableMap(levels);
    }

    public void register(FormRace race) {
        resolveLinks();
        for (List<Form> forms : levels.values()) {
            for (Form form : forms) {
                form.race = race;
                int scopedId = getScopedId(form.id);
                form.id = scopedId;
                form.name = "builtin_" + scopedId;
                form.builtIn = true;
                FormController.getInstance().registerBuiltIn(scopedId, form);
            }
        }
    }

    private void resolveLinks() {
        for (List<Form> forms : levels.values()) {
            for (Form form : forms) {
                if (form.childID != -1)
                    form.childID = getScopedId(form.childID);
                if (form.parentID != -1)
                    form.parentID = getScopedId(form.parentID);
            }
        }
    }

    public int getScopedId(int localId) {
        return (raceNamespace.hashCode() & 0x7FFFFFFF) * 1000 + localId;
    }

    public List<Form> getAllForms() {
        List<Form> all = new ArrayList<>();
        for (List<Form> forms : levels.values()) {
            all.addAll(forms);
        }
        return Collections.unmodifiableList(all);
    }
}
