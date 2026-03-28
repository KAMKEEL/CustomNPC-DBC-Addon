package kamkeel.npcdbc.data.race.serial;

public interface DataSerializable {
    DataCompound serialize(DataCompound data);
    void deserialize(DataCompound data);
}
