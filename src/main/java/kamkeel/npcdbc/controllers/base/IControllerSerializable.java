package kamkeel.npcdbc.controllers.base;

import kamkeel.npcdbc.data.race.serial.DataSerializable;

public interface IControllerSerializable extends DataSerializable {

    String getKey();

    String getDisplayName();

    void setDisplayName(String name);

    default boolean hasKey() {
        return getKey() != null && !getKey().isEmpty();
    }
}
