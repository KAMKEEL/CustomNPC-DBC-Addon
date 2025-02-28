package kamkeel.npcdbc.constants;

import kamkeel.npcs.controllers.AttributeController;
import kamkeel.npcs.controllers.data.attribute.AttributeDefinition;
import kamkeel.npcs.controllers.data.attribute.AttributeValueType;

public class ItemAttributes {

    public String strengthKey = "jinryuujrmcore.Strength";
    public String dexterityKey = "jinryuujrmcore.Dexterity";
    public String constitutionKey = "jinryuujrmcore.Constitution";
    public String willpowerKey = "jinryuujrmcore.WillPower";
    public String spiritKey = "jinryuujrmcore.Spirit";

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

    public ItemAttributes(){}
}
