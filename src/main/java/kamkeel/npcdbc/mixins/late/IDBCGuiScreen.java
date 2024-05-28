package kamkeel.npcdbc.mixins.late;

public interface IDBCGuiScreen {

    /**
     * Sets the DBC GUI screen ID and makes it persist after initial display
     * @param id id of the GUI to display;
     */
    void setGuiIDPostInit(int id);
}
