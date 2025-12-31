package kamkeel.npcdbc.client.gui.hud.abilityWheel;

import kamkeel.npcdbc.client.gui.component.SubGuiSelectAbility;
import kamkeel.npcdbc.config.ConfigDBCClient;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.client.gui.util.ISubGuiListener;
import noppes.npcs.client.gui.util.SubGuiInterface;

import java.util.HashMap;

import static org.lwjgl.opengl.GL11.glTranslatef;

public class HUDAbilityWheel extends GuiNPCInterface implements ISubGuiListener {
    public static float BLUR_INTENSITY = 0;
    public static float MAX_BLUR = 4;
    public static boolean BLUR_ENABLED = true;

    ScaledResolution scaledResolution;
    public AbilityWheelSegment[] wheelSlot = new AbilityWheelSegment[6];

    private float zoomed = 70, rotation;
    float guiAnimationScale = 0, animationScaleFactor, undoMCScaling = 1;
    long timeOpened, timeClosedSubGui, timeSinceM1, timeClosed;

    public int hoveredSlot = -1;
    boolean keyDown, unpressedAllKeys = false;
    boolean configureEnabled;
    // public HashMap<Integer, String> dbcForms; // TODO REPLACE WITH DBC ABILITIES
    public DBCData dbcData;
    public PlayerDBCInfo dbcInfo;

    public boolean isClosing;

    public static final int CLOSE_TIME = 600;
    public static final int OPEN_TIME = 1500;

    public double easeOutExpo(double x) {
        return x == 1 ? 1 : 1 - Math.pow(2, -10 * x);
    }

    public HUDAbilityWheel() {
        mc = Minecraft.getMinecraft();
        dbcData = DBCData.getClient();
        //dbcForms = dbcData.getUnlockedDBCFormsMap();
        dbcInfo = PlayerDataUtil.getClientDBCInfo();

        for (int i = 0; i < 6; i++) {
            wheelSlot[i] = new AbilityWheelSegment(this, i);
            // TODO REPLACE WITH ABILITY WHEEL
            //wheelSlot[i].setForm(dbcInfo.formWheel[i], false);
        }
        // TODO REPLACE WITH REQUEST ABILITY WHEEL
        //DBCPacketHandler.Instance.sendToServer(new DBCRequestFormWheel());

        // Stops the GUI from un-pressing all keys for you.
        mc.inGameHasFocus = false;
        mc.mouseHelper.ungrabMouseCursor();

        BLUR_INTENSITY = 0;
    }

    @Override
    public void initGui() {
        super.initGui();
        // Prevents replaying the open animation on screen resize
        if (timeOpened == 0)
            timeOpened = Minecraft.getSystemTime();

        scaledResolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);


        int x = (this.width / 2) + 94;
        int y = this.height - 22;
        addButton(new GuiNpcButton(6, x, y, 60, 20, new String[]{"Configure", "Done"}, !configureEnabled ? 0 : 1));

        float factor = scaledResolution.getScaleFactor();
        undoMCScaling = 1f / factor * 2f;
        if (factor == 1) {
            if (mc.displayHeight < 260)
                undoMCScaling = 0.3f;
            else if (mc.displayHeight < 350)
                undoMCScaling = 0.45f;
            else if (mc.displayHeight < 720)
                undoMCScaling = 0.7f;
            else if (mc.displayWidth < 650)
                undoMCScaling = 1f;
        } else if (factor == 2) {
            if (mc.displayHeight < 530)
                undoMCScaling = 0.413f;
            else if (mc.displayHeight < 600)
                undoMCScaling = 0.475f;
            else if (mc.displayHeight < 720)
                undoMCScaling = 1f / scaledResolution.getScaleFactor() * 1;
            else
                undoMCScaling = 1;
        } else if (factor == 3) {
            if (mc.displayHeight < 730)
                undoMCScaling = 1f / 3;
            else if (mc.displayHeight < 930)
                undoMCScaling = 0.425f;
            else if (mc.displayHeight < 1000)
                undoMCScaling = 0.6f;
            else if (mc.displayWidth < 1300)
                undoMCScaling = 0.8f;
            else if (mc.displayHeight < 1250) {
                undoMCScaling = 0.75f;
                glTranslatef(0, -15, 0);
            } else
                undoMCScaling = 0.99f;
        }

        if (configureEnabled) {
            addButton(new GuiNpcButton(8, x - 94 - 75, y - 25, 150, 20, "Edit"));
            addButton(new GuiNpcButton(7, x += 62, y, 80, 20, "Switch Wheel"));


            x = (int) ((this.width / 2) * undoMCScaling + 190);
            y = (this.height / 2) - 100;
            if (undoMCScaling < 1) {
                x += 20;
                y -= 25;
            }
        }
    }

    public void buttonEvent(GuiButton guibutton) {
        GuiNpcButton button = (GuiNpcButton) guibutton;
        if (button.id <= 5) {
            this.setSubGui(new SubGuiSelectAbility(button.id, true, true).displayDBCAbilities(PlayerDataUtil.getClientDBCInfo()));
//        } else if (button.id == 100) {
//            wheelSlot[0].removeForm();
//        } else if (button.id == 11) {
//            wheelSlot[1].removeForm();
//        } else if (button.id == 22) {
//            wheelSlot[2].removeForm();
//        } else if (button.id == 33) {
//            wheelSlot[3].removeForm();
//        } else if (button.id == 44) {
//            wheelSlot[4].removeForm();
//        } else if (button.id == 55) {
//            wheelSlot[5].removeForm();
//        } else if (button.id == 6) {
//            configureEnabled = button.getValue() == 1;
//            if (configureEnabled) {
//                selectSlot(-1);
//                timeClosedSubGui = Minecraft.getSystemTime();
//            }
        } else if (button.id == 7) {
            ConfigDBCClient.AlteranteSelectionWheelTexture = !ConfigDBCClient.AlteranteSelectionWheelTexture;
            ConfigDBCClient.AlternateSelectionWheelTextureProperty.set((ConfigDBCClient.AlteranteSelectionWheelTexture));
            timeClosedSubGui = Minecraft.getSystemTime();
        }
        if (button.id == 8) {
            this.setSubGui(new SubGuiSelectAbility(button.id, true, true).displayDBCAbilities(PlayerDataUtil.getClientDBCInfo()));
        }
        initGui();
    }

    @Override
    public void save() {

    }

    @Override
    public void subGuiClosed(SubGuiInterface subGuiInterface) {

    }
}
