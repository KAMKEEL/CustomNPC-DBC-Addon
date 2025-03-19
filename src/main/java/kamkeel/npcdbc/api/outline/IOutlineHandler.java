package kamkeel.npcdbc.api.outline;

public interface IOutlineHandler {

    public IOutline createOutline(String name);

    public IOutline saveOutline(IOutline outline);

    void deleteOutlineFile(String name);

    boolean hasName(String newName);

    IOutline get(String name);

    IOutline get(int id);

    boolean has(String name);

    boolean has(int id);

    void delete(int id);

    void delete(String name);

    String[] getNames();

    IOutline[] getOutlines();

    IOutline getOutlineFromName(String outlineName);
}
