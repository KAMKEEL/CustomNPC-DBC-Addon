package kamkeel.npcdbc.api.form;

public interface IFormMasteryLinkData {

    boolean hasLinkData(int race);

    boolean isCustomFormLink(int race);

    int getFormID(int race);

    IForm getForm(int race);

    void setCustomFormLink(int formID, int race);

    void setCustomFormLink(IForm form, int race);

    void setDBCFormLink(int formID, int race);

    void setMasteryLink(int formID, int race, boolean isCustomFormLink);

    void removeLinkData(int race);

    void removeAllLinkData();
}
