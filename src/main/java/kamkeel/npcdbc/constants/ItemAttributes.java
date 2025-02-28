package kamkeel.npcdbc.constants;

import kamkeel.npcs.controllers.AttributeController;
import kamkeel.npcs.controllers.data.attribute.AttributeDefinition;
import kamkeel.npcs.controllers.data.attribute.AttributeValueType;

public class ItemAttributes {

    public String strengthKey = "dbc.Strength";
    public String dexterityKey = "dbc.Dexterity";
    public String constitutionKey = "dbc.Constitution";
    public String willpowerKey = "jinryuujrmcore.WillPower";
    public String spiritKey = "jinryuujrmcore.Spirit";

    public String strengthMultiKey = "dbc.Strength.Multi";
    public String dexterityMultiKey = "dbc.Dexterity.Multi";
    public String constitutionMultiKey = "dbc.Constitution.Multi";
    public String willpowerMultiKey = "jinryuujrmcore.WillPower.Multi";
    public String spiritMultiKey = "jinryuujrmcore.Spirit.Multi";

    public static AttributeDefinition STRENGTH;
    public static AttributeDefinition DEXTERITY;
    public static AttributeDefinition CONSTITUTION;
    public static AttributeDefinition WILLPOWER;
    public static AttributeDefinition SPIRIT;

    public static AttributeDefinition STRENGTH_BOOST;
    public static AttributeDefinition DEXTERITY_BOOST;
    public static AttributeDefinition CONSTITUTION_BOOST;
    public static AttributeDefinition WILLPOWER_BOOST;
    public static AttributeDefinition SPIRIT_BOOST;

    public ItemAttributes(){
        // Flat Attributes
        STRENGTH = new AttributeDefinition(strengthKey, "Strength", '4', AttributeValueType.FLAT, AttributeDefinition.AttributeSection.STATS);
        STRENGTH.setTranslationKey("jinryuujrmcore.Strength");
        DEXTERITY = new AttributeDefinition(dexterityKey, "Dexterity", '6', AttributeValueType.FLAT, AttributeDefinition.AttributeSection.STATS);
        DEXTERITY.setTranslationKey("jinryuujrmcore.Dexterity");
        CONSTITUTION = new AttributeDefinition(constitutionKey, "Constitution", 'b', AttributeValueType.FLAT, AttributeDefinition.AttributeSection.STATS);
        CONSTITUTION.setTranslationKey("jinryuujrmcore.Constitution");
        WILLPOWER = new AttributeDefinition(willpowerKey, "Willpower", 'd', AttributeValueType.FLAT, AttributeDefinition.AttributeSection.STATS);
        WILLPOWER.setTranslationKey("jinryuujrmcore.WillPower");
        SPIRIT = new AttributeDefinition(spiritKey, "Spirit", '3', AttributeValueType.FLAT, AttributeDefinition.AttributeSection.STATS);
        SPIRIT.setTranslationKey("jinryuujrmcore.Spirit");

        // Multi Attributes
        STRENGTH_BOOST = new AttributeDefinition(strengthMultiKey, "Strength Multi", '4', AttributeValueType.PERCENT, AttributeDefinition.AttributeSection.STATS);
        STRENGTH_BOOST.setTranslationKey("jinryuujrmcore.Strength");
        DEXTERITY_BOOST = new AttributeDefinition(dexterityMultiKey, "Dexterity Multi", '6', AttributeValueType.PERCENT, AttributeDefinition.AttributeSection.STATS);
        DEXTERITY_BOOST.setTranslationKey("jinryuujrmcore.Dexterity");
        CONSTITUTION_BOOST = new AttributeDefinition(constitutionMultiKey, "Constitution Multi", 'b', AttributeValueType.PERCENT, AttributeDefinition.AttributeSection.STATS);
        CONSTITUTION_BOOST.setTranslationKey("jinryuujrmcore.Constitution");
        WILLPOWER_BOOST = new AttributeDefinition(willpowerMultiKey, "Willpower Multi", 'd', AttributeValueType.PERCENT, AttributeDefinition.AttributeSection.STATS);
        WILLPOWER_BOOST.setTranslationKey("jinryuujrmcore.WillPower");
        SPIRIT_BOOST = new AttributeDefinition(spiritMultiKey, "Spirit Multi", '3', AttributeValueType.PERCENT, AttributeDefinition.AttributeSection.STATS);
        SPIRIT_BOOST.setTranslationKey("jinryuujrmcore.Spirit");
    }
}
