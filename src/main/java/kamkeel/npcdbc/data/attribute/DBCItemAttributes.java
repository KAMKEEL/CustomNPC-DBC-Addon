package kamkeel.npcdbc.data.attribute;

import kamkeel.npcdbc.controllers.BonusController;
import kamkeel.npcdbc.data.PlayerBonus;
import kamkeel.npcdbc.data.attribute.requirements.DBCClassRequirement;
import kamkeel.npcdbc.data.attribute.requirements.DBCLevelRequirement;
import kamkeel.npcdbc.data.attribute.requirements.DBCRaceRequirement;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormAttributes;
import kamkeel.npcs.CustomAttributes;
import kamkeel.npcs.controllers.AttributeController;
import kamkeel.npcs.controllers.data.attribute.AttributeDefinition;
import kamkeel.npcs.controllers.data.attribute.AttributeValueType;
import kamkeel.npcs.controllers.data.attribute.PlayerAttribute;
import kamkeel.npcs.controllers.data.attribute.requirement.RequirementCheckerRegistry;
import kamkeel.npcs.controllers.data.attribute.tracker.AttributeRecalcEvent;
import kamkeel.npcs.controllers.data.attribute.tracker.PlayerAttributeTracker;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.config.ConfigMain;

import java.util.Map;

public class DBCItemAttributes {

    public static String gearFlat = "GearFlat";
    public static String gearMulti = "GearMulti";

    public String strengthKey = "dbc.Strength";
    public String dexterityKey = "dbc.Dexterity";
    public String constitutionKey = "dbc.Constitution";
    public String willpowerKey = "dbc.WillPower";
    public String spiritKey = "dbc.Spirit";

    public String strengthMultiKey = "dbc.Strength.Multi";
    public String dexterityMultiKey = "dbc.Dexterity.Multi";
    public String constitutionMultiKey = "dbc.Constitution.Multi";
    public String willpowerMultiKey = "dbc.WillPower.Multi";
    public String spiritMultiKey = "dbc.Spirit.Multi";

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

    public DBCItemAttributes() {
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

        // Register Attributes
        AttributeController.registerAttribute(STRENGTH);
        AttributeController.registerAttribute(DEXTERITY);
        AttributeController.registerAttribute(CONSTITUTION);
        AttributeController.registerAttribute(WILLPOWER);
        AttributeController.registerAttribute(SPIRIT);

        AttributeController.registerAttribute(STRENGTH_BOOST);
        AttributeController.registerAttribute(DEXTERITY_BOOST);
        AttributeController.registerAttribute(CONSTITUTION_BOOST);
        AttributeController.registerAttribute(WILLPOWER_BOOST);
        AttributeController.registerAttribute(SPIRIT_BOOST);

        // Add Pre Listener
        AttributeRecalcEvent.registerPreListener((entityPlayer, playerAttributeTracker) -> preListener(entityPlayer, playerAttributeTracker));

        // Add Listener for Bonus
        AttributeRecalcEvent.registerListener((entityPlayer, playerAttributeTracker) -> postListener(entityPlayer, playerAttributeTracker));

        // Requirements
        RequirementCheckerRegistry.registerChecker(new DBCLevelRequirement());
        RequirementCheckerRegistry.registerChecker(new DBCRaceRequirement());
        RequirementCheckerRegistry.registerChecker(new DBCClassRequirement());
    }

    public static void preListener(EntityPlayer player, PlayerAttributeTracker tracker) {
        if (ConfigMain.AttributesEnabled) {
            Form form = DBCData.get(player).getForm();
            if (form == null) return;

            FormAttributes fa = form.customAttributes;

            // — flat attributes —
            for (Map.Entry<String,Float> e : fa.getAllAttributes().entrySet()) {
                AttributeDefinition def = AttributeController.getAttribute(e.getKey());
                if (def == null) continue;
                PlayerAttribute inst = tracker.playerAttributes.getAttributeInstance(def);
                if (inst == null) inst = tracker.playerAttributes.registerAttribute(def, 0f);
                inst.value += e.getValue();
            }

            // — magic attributes —
            for (Map.Entry<String,Map<Integer,Float>> tagEntry : fa.getAllMagic().entrySet()) {
                String tag = tagEntry.getKey();
                tagEntry.getValue().forEach((magicId, v) -> {
                    switch (tag) {
                        case CustomAttributes.MAGIC_DAMAGE_KEY:
                            tracker.magicDamage.merge(magicId, v, Float::sum);
                            break;
                        case CustomAttributes.MAGIC_BOOST_KEY:
                            tracker.magicBoost.merge(magicId, v, Float::sum);
                            break;
                        case CustomAttributes.MAGIC_DEFENSE_KEY:
                            tracker.magicDefense.merge(magicId, v, Float::sum);
                            break;
                        case CustomAttributes.MAGIC_RESISTANCE_KEY:
                            tracker.magicResistance.merge(magicId, v, Float::sum);
                            break;
                    }
                });
            }
        }
    }

    public static void postListener(EntityPlayer entityPlayer, PlayerAttributeTracker playerAttributeTracker) {
        BonusController.getInstance().removeBonus(entityPlayer, gearFlat);
        BonusController.getInstance().removeBonus(entityPlayer, gearMulti);
        if (ConfigMain.AttributesEnabled) {
            PlayerBonus flatBonus = new PlayerBonus(gearFlat, (byte) 1);
            flatBonus.setStrength(playerAttributeTracker.getAttributeValue(STRENGTH));
            flatBonus.setDexterity(playerAttributeTracker.getAttributeValue(DEXTERITY));
            flatBonus.setConstitution(playerAttributeTracker.getAttributeValue(CONSTITUTION));
            flatBonus.setWillpower(playerAttributeTracker.getAttributeValue(WILLPOWER));
            flatBonus.setSpirit(playerAttributeTracker.getAttributeValue(SPIRIT));

            PlayerBonus multiBonus = new PlayerBonus(gearMulti, (byte) 0);
            multiBonus.setStrength(playerAttributeTracker.getAttributeValue(STRENGTH_BOOST) / 100f);
            multiBonus.setDexterity(playerAttributeTracker.getAttributeValue(DEXTERITY_BOOST) / 100f);
            multiBonus.setConstitution(playerAttributeTracker.getAttributeValue(CONSTITUTION_BOOST) / 100f);
            multiBonus.setWillpower(playerAttributeTracker.getAttributeValue(WILLPOWER_BOOST) / 100f);
            multiBonus.setSpirit(playerAttributeTracker.getAttributeValue(SPIRIT_BOOST) / 100f);

            // Check if Bonus Exists
            boolean hasFlatBonus = flatBonus.getStrength() != 0.0f ||
                flatBonus.getDexterity() != 0.0f ||
                flatBonus.getConstitution() != 0.0f ||
                flatBonus.getWillpower() != 0.0f ||
                flatBonus.getSpirit() != 0.0f;
            boolean hasMultiBonus = multiBonus.getStrength() != 0.0f ||
                multiBonus.getDexterity() != 0.0f ||
                multiBonus.getConstitution() != 0.0f ||
                multiBonus.getWillpower() != 0.0f ||
                multiBonus.getSpirit() != 0.0f;
            if (hasFlatBonus) {
                BonusController.getInstance().applyBonus(entityPlayer, flatBonus);
            }
            if (hasMultiBonus) {
                BonusController.getInstance().applyBonus(entityPlayer, multiBonus);
            }
        }
    }
}
