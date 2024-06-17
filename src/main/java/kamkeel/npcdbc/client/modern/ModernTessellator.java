package kamkeel.npcdbc.client.modern;

public class ModernTessellator {
    public static ModernTessellator Instance;

    public ModernTessellator() {
        Instance = this;
    }

    public static ModernTessellator getInstance() {
        if (Instance == null)
            return new ModernTessellator();

        return Instance;
    }
}
