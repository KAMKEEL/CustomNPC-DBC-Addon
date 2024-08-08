package kamkeel.npcdbc.config;

import JinRyuu.JRMCore.JRMCoreH;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import kamkeel.npcdbc.client.ClientCache;
import net.minecraftforge.common.config.Configuration;
import noppes.npcs.util.ValueUtil;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.util.HashMap;

public class ConfigDBCEffects
{
    public static Configuration config;

    public final static String REGEN = "Regen";

    public static int HealthRegenPercent = 5;
    public static int KiRegenPercent = 5;
    public static int StaminaRegenPercent = 5;

    public final static String NAMEK_REGEN = "Namekian_Regen";
    public static int NamekRegenPercent = 3;

    public final static String FRUITOFMIGHT = "Fruit_of_Might";
    public static double FOM_Strength = 1.2;
    public static double FOM_Dex = 0.5;
    public static double FOM_Will = 0.5;
    public static int FOM_EffectLength = 90;
    public static double FOM_KiDrain = -0.8;


    public final static String ZENKAI = "Zenkai";
    public static double ZenkaiSaiyanStr = 1.0;
    public static double ZenkaiSaiyanDex = 1.0;
    public static double ZenkaiSaiyanWil = 1.0;
    public static int ZenkaiSaiyanLength = 360;

    public static double ZenkaiHALFStr = 1.0f;
    public static double ZenkaiHALFDex = 1.0f;
    public static double ZenkaiHALFWil = 1.0f;
    public static int ZenkaiHALFLength = 180;

    public final static String Meditation = "Meditation";
    public static int MeditationSpiBoostPercent = 20;

    public final static String Potara = "Potara";
    public static double TierOneMulti = 0.5;
    public static double TierTwoMulti = 0.7;
    public static double TierThreeMulti = 1.0;

    public final static String OVERPOWER = "OVERPOWER";
    public static int OVERPOWER_AMOUNT = 25;

    public final static String EXHAUST = "EXHAUST";
    public static int EXHAUST_TIME = 15;
    public static boolean EXHAUST_ZENAKI = true;
    public static boolean EXHAUST_OVERPOWER = true;

    public final static String DIVINE = "DIVINE";
    public final static String DIVINE_RACES = "DIVINE RACES";
    private static float divineMulti = 1;
    private static final HashMap<Integer, HashMap<String, Boolean>> divineApplicableForms = new HashMap<>();

    /**
     * Ugly, roundabout way of persisting configs between multiplayer and singleplayer.
     *
     * @return Divine multiplier that should be used in the current world (singleplayer or multiplayer).
     */
    public static float getDivineMulti(){
        if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
            return divineMulti;
        else
            return ClientCache.divineMulti;
    }

    public static HashMap<Integer, HashMap<String, Boolean>> getDivineApplicableForms(){
        if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
            return divineApplicableForms;
        return ClientCache.divineApplicableForms;
    }

    public static boolean canDivineBeApplied(int race, int state){
        HashMap<Integer, HashMap<String, Boolean>> raceMap = getDivineApplicableForms();
        HashMap<String, Boolean> formMap = raceMap.get(race);
        if(formMap == null || formMap.isEmpty())
            return false;
        if(race >= JRMCoreH.trans.length)
            return false;
        if(state >= JRMCoreH.trans[race].length)
            return false;
        return formMap.getOrDefault(JRMCoreH.trans[race][state], false);
    }


    public static void init(File configFile)
    {
        config = new Configuration(configFile);

        try
        {
            config.load();

            HealthRegenPercent = config.get(REGEN, "Health Regen", 5, "Amount of Percent to restore per Level of Regen").getInt(5);
            KiRegenPercent = config.get(REGEN, "Ki Regen", 5, "Amount of Percent to restore per Level of Regen").getInt(5);
            StaminaRegenPercent = config.get(REGEN, "Stamina Regen", 5, "Amount of Percent to restore per Level of Regen").getInt(5);

            NamekRegenPercent = config.get(NAMEK_REGEN, "Namek Regen", 3,
                "Namekian Regen is removed once the player reaches the specified amount in Gameplay Config." +
                "\nThis value is percent health restored per second.").getInt(3);

            FOM_Strength = config.get(FRUITOFMIGHT, "Strength Multi", 1.2, "Amount added to Strength Multi [EX: Form is 1.5x and FOM is x0.5. Total is: x2.0]").getDouble(1.2);
            FOM_Dex = config.get(FRUITOFMIGHT, "Dex Multi", 0.5, "Amount added to Dex Multi").getDouble(0.5);
            FOM_Will = config.get(FRUITOFMIGHT, "Will Multi", 0.5, "Amount added to Will Multi").getDouble(0.5);
            FOM_EffectLength = config.get(FRUITOFMIGHT, "Effect Time", 90, "Amount of time in seconds the Fruit of Might Effect is applied for").getInt(90);
            FOM_KiDrain = config.get(FRUITOFMIGHT, "Effect Drain", -0.8, "Ki Drain Percent per Second").getDouble(-0.8);

            config.addCustomCategoryComment(ZENKAI,
                "Zenkai will occur when a Saiyan or Half Saiyan dies. This can be disabled in\n" +
                        "the DBC Gameplay Config. If the Zenaki effect is given to a none Saiyan. It will\n" +
                         "utilitze the Half Saiyan Config values. All multis are ADDED multis.");
            ZenkaiSaiyanStr = config.get(ZENKAI, "Saiyan Strength Multi", 1.2f, "Amount added to Strength Multi").getDouble(1.0f);
            ZenkaiSaiyanDex = config.get(ZENKAI, "Saiyan Dex Multi", 1.2f, "Amount added to Dex Multi").getDouble(1.0f);
            ZenkaiSaiyanWil = config.get(ZENKAI, "Saiyan Will Multi", 1.2f, "Amount added to Will Multi").getDouble(1.0f);
            ZenkaiSaiyanLength = config.get(ZENKAI, "Saiyan Zenkai Length", 360, "Time of Zenkai in Seconds").getInt(360);

            ZenkaiHALFStr = config.get(ZENKAI, "Half Saiyan Strength Multi", 1.2f, "Amount added to Strength Multi").getDouble(1.0f);
            ZenkaiHALFDex = config.get(ZENKAI, "Half Saiyan Dex Multi", 1.2f, "Amount added to Dex Multi").getDouble(1.0f);
            ZenkaiHALFWil = config.get(ZENKAI, "Half Saiyan Will Multi", 1.2f, "Amount added to Will Multi").getDouble(1.0f);
            ZenkaiHALFLength = config.get(ZENKAI, "Half Saiyan Zenkai Length", 180, "Time of Zenkai in Seconds").getInt(180);

            config.addCustomCategoryComment(Meditation,
                "Meditation Spirit Bonus is always added AFTER all other calculations");
            MeditationSpiBoostPercent = config.get(Meditation, "Meditation Boost", 20, "Amount of percent to multiply Base Spirit Stats by").getInt(20);

            config.addCustomCategoryComment(Potara,
                "If the Potara Status Effect Multi is set to 0 or below, then the Status Effect will not be applied" +
                    "\nThis mutli is added (not multiplied) to [Str, Dex, Wil]");
            TierOneMulti = config.get(Potara, "Tier 1 Multi", 0.5f).getDouble(0.5f);
            TierTwoMulti = config.get(Potara, "Tier 2 Multi", 0.7f).getDouble(0.7f);
            TierThreeMulti = config.get(Potara, "Tier 3 Multi", 1.0f).getDouble(1.0f);

            OVERPOWER_AMOUNT = config.get(OVERPOWER, "Overpower Amount", 25,
                "Overpower allows a player to increase their release above their natural limit. Potential Unlock Level \n" +
                    "is included in this calculation. So if their limit is 50 percent then their new limit is 50 + Overpower Amount." +
                    "The range of accepted values are [0 to 25].").getInt(25);

            OVERPOWER_AMOUNT = ValueUtil.clamp(OVERPOWER_AMOUNT, 0, 25);


            config.addCustomCategoryComment(EXHAUST,
                "Exhausted prevents specific effects from being applied to the player." +
                    "\nSimilar to Pain or NoFuse, it acts as a Cooldown");
            EXHAUST_TIME = config.get(Potara, "Exhaust Time", 15, "Amount of Time in Minutes for Exhaust").getInt(15);
            EXHAUST_ZENAKI = config.get(EXHAUST, "Exhaust Zenkai", true).getBoolean(true);
            EXHAUST_OVERPOWER = config.get(EXHAUST, "Exhaust Overpower", true).getBoolean(true);


            config.addCustomCategoryComment(DIVINE,
                "Forms can now benefit from an additional multi" +
                    "\n" +
                    "\nDivine is applied in a similar manner as Majin and Legendary" +
                    "\n" +
                    "\nFormula:" +
                    "\n formMulti = ( multi x racialBoost ) x masteryMultiModifier" +
                    "\n result = baseStat x [(formMulti x kaiokenMulti) + (formMulti x majin) + (formMulti x legendary) + (formMulti x divine)" +
                    "\n" +
                    "\nWHERE:" +
                    "\n - racialBoost is the multi gained from Arcosian PowerPoints or Majin Absorption" +
                    "\n - kaiokenMulti is the multi gained from kaioken. If you're not in kaioken, the multi is 1.0"

            );

            divineMulti = (float) config.get(DIVINE, "Divine status effect multi", 1.0, "Put the boost in multiplier form. 1.0 is no boost, 1.15 = 15% boost").getDouble(1.0);

            for(int i = 0; i < JRMCoreH.Races.length; i++){
                HashMap<String, Boolean> formsAffected = new HashMap<>();
                String legalValues = "";
                for(int y = 0; y < JRMCoreH.trans[i].length; y++){
                    if(y != 0)
                        legalValues += ",";
                    legalValues += JRMCoreH.trans[i][y];
                }
                String[] formNames = config.getStringList(JRMCoreH.Races[i]+" - Divine affected forms", DIVINE_RACES, new String[0], "Forms affected by divine multi.\nLegal values: "+legalValues, JRMCoreH.trans[i]);
                for(String name : formNames){
                    formsAffected.put(name, true);
                }
                divineApplicableForms.put(i, formsAffected);
            }
        }
        catch (Exception e)
        {
            FMLLog.log(Level.ERROR, e, "DBC Addon has had a problem loading its status effect configuration");
        }
        finally
        {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }
}
