package kamkeel.npcdbc.data.dbcdata;

import kamkeel.npcdbc.CommonProxy;
import kamkeel.npcdbc.config.ConfigDBCGameplay;

import java.util.Arrays;

public class DBCDataKiAttackValidator {

    public final DBCData dbcData;

    private long[] arrayCastStartTime = new long[8];

    public DBCDataKiAttackValidator(DBCData dbcData) {
        this.dbcData = dbcData;
        Arrays.fill(arrayCastStartTime, -1);
    }

    private int calculateExpectedCastTimeTicks(int kiAttackSelection, int chargePercent) {
        // TODO: ADD THE TIME MATH FOR CHARGE PERCENT
        return 0;
    }

    public boolean isCastTimeValid(int kiAttackSelection, int chargePercent, long worldTicks) {
        if (kiAttackSelection >= arrayCastStartTime.length)
            return false;

        if (!ConfigDBCGameplay.ValidateKiCasting)
            return true;

        long startTime = arrayCastStartTime[kiAttackSelection];
        if (startTime == -1) {
            WARN("Player attempted launching an attack without casting it!!!");
            return false;
        }
        long totalCastTime = worldTicks - startTime;
        int expectedCastTime = calculateExpectedCastTimeTicks(kiAttackSelection, chargePercent);

        if (totalCastTime >= expectedCastTime)
            return true;

        long missingTicks = expectedCastTime - totalCastTime;
        float deviationFactor = (float) missingTicks / (float) expectedCastTime;

        if (deviationFactor >= ConfigDBCGameplay.InvalidateWhenAboveDeviationFactor) {
            WARN(String.format("Cancelled attack due to major cast time deviation - (%f%%, %d ticks too fast)", deviationFactor*100, missingTicks));
            return false;
        }

        if (deviationFactor >= ConfigDBCGameplay.WarnWhenAboveDeviationFactor) {
            WARN(String.format("Player had slight cast time deviation - (%f%%, %d ticks too fast)", deviationFactor*100, missingTicks));
        }

        return true;
    }


    public void startCast(int kiAttackSelection, long worldTicks) {
        if (kiAttackSelection >= arrayCastStartTime.length)
            return;
        arrayCastStartTime[kiAttackSelection] = worldTicks;
    }

    public void cancelCast(int kiAttackSelection) {
        this.startCast(kiAttackSelection, -1);
    }

    private void WARN(String warnMessage) {
        String playerName = dbcData.player.getDisplayName();

        String message = String.format("[KI CAST WARN] - Player %s - %s", playerName, warnMessage);
        CommonProxy.LOGGER.warn(message);
    }
}
