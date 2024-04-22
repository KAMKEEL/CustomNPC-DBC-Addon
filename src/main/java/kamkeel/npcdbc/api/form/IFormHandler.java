package kamkeel.npcdbc.api.form;

public interface IFormHandler {
    IForm saveForm(IForm var1);

    void delete(String var1);

    void delete(int var1);

    boolean has(String var1);

    IForm get(String var1);

    IForm get(int var1);

    IForm[] getForms();
}
