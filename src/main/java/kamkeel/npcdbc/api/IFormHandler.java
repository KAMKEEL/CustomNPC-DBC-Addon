package kamkeel.npcdbc.api;

public interface IFormHandler {
    ICustomForm saveForm(ICustomForm var1);

    void delete(String var1);

    void delete(int var1);

    boolean has(String var1);

    ICustomForm get(String var1);

    ICustomForm get(int var1);

    ICustomForm[] getForms();
}
