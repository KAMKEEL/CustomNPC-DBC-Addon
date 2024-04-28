package kamkeel.npcdbc.api.aura;

public interface IAuraHandler {
    IAura saveAura(IAura var1);

    void delete(String var1);

    void delete(int var1);

    boolean has(String var1);

    IAura get(String var1);

    IAura get(int var1);

    IAura[] getAuras();
}
