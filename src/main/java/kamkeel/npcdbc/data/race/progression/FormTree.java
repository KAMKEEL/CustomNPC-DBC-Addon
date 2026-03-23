package kamkeel.npcdbc.data.race.progression;

import kamkeel.npcdbc.data.form.BuiltInForm;
import kamkeel.npcdbc.data.form.FormRace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FormTree {
    private final String raceNamespace;
    private final Map<Integer, List<BuiltInForm>> levels = new LinkedHashMap<>();

    public FormTree(String raceNamespace) {
        this.raceNamespace = raceNamespace;
    }

    public void add(int level, BuiltInForm form) {
        levels.computeIfAbsent(level, k -> new ArrayList<>()).add(form);
    }

    public List<BuiltInForm> getFormsAtLevel(int level) {
        return Collections.unmodifiableList(levels.getOrDefault(level, Collections.emptyList()));
    }

    public Map<Integer, List<BuiltInForm>> getLevels() {
        return Collections.unmodifiableMap(levels);
    }

    public void register(FormRace race) {
        for (List<BuiltInForm> forms : levels.values()) {
            for (BuiltInForm form : forms) {
                form.race = race;
                int scopedId = getScopedId(form.id);
//                FormController.Instance.registerBuiltIn(scopedId, raceNamespace, form);
            }
        }
        resolveLinks();
    }

    private void resolveLinks() {
        for (List<BuiltInForm> forms : levels.values()) {
            for (BuiltInForm form : forms) {
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
}
