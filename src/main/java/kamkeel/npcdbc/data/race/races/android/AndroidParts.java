package kamkeel.npcdbc.data.race.races.android;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.LocalizationHelper;

public class AndroidParts {

    public static final AndroidPartData RED_RIBBON_CORE = create("rr_core", AndroidPartSlot.CORE)

        .build();

    private static AndroidPartData.Builder create(String name, AndroidPartSlot slot) {
        return AndroidPartData.create(CustomNpcPlusDBC.ID, name, slot)
            .unlocalizedName(LocalizationHelper.ITEM_PREFIX + name);
    }
}
