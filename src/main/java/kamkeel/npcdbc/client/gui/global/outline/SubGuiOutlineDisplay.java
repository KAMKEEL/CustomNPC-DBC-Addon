package kamkeel.npcdbc.client.gui.global.outline;

import kamkeel.npcdbc.client.gui.component.SubGuiSelectOutline;
import kamkeel.npcdbc.client.model.part.hair.DBCHair;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.controllers.OutlineController;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.data.outline.Outline;
import kamkeel.npcdbc.mixins.late.INPCDisplay;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.SubGuiColorSelector;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.entity.EntityNPCInterface;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;


public class SubGuiOutlineDisplay extends GuiNPCInterface implements ISubGuiListener, GuiSelectionListener, ITextfieldListener {
    public static boolean useGUIOutline;
    public static Outline outline;
    private final GuiNPCManageOutlines parent;
    private final DBCDisplay visualDisplay;
    public int lastColorClicked = 0;
    public int xOffset = 0;
    public int yOffset = 0;
    public GuiScrollWindow scrollWindow;
    DBCDisplay origDisplay = null;
    private float rotation = 0.0F;
    private GuiNpcButton left, right, zoom, unzoom;
    private float zoomed = 60.0F;


    public SubGuiOutlineDisplay(GuiNPCManageOutlines parent, EntityNPCInterface npc, Outline outline) {
        super(npc);
        SubGuiOutlineDisplay.outline = outline;
        this.parent = parent;


        setBackground("menubg.png");
        xSize = 360;
        ySize = 216;

        this.xOffset = 140;

        npc.display.name = "outline man";
        npc.height = 1.62f;
        npc.width = 0.43f;
        visualDisplay = ((INPCDisplay) npc.display).getDBCDisplay();


    }

    public void updateDisplay() {
        DBCData data = DBCData.getClient();
        boolean isSaiyan = DBCRace.isSaiyan(visualDisplay.race);
        if (isSaiyan) {
            visualDisplay.tailState = (data.Tail == 0 || data.Tail == 1) ? data.Tail : (byte) (data.Tail == -1 ? 0 : 1);
        }

        if (visualDisplay.race == DBCRace.ARCOSIAN || visualDisplay.race == DBCRace.NAMEKIAN) {
            visualDisplay.hairCode = "";
        } else if (visualDisplay.race == DBCRace.MAJIN) {
            visualDisplay.hairCode = DBCHair.MAJIN_HAIR;
            visualDisplay.hairColor = visualDisplay.bodyCM;
        }

    }

    public void initGui() {
        super.initGui();
        int y = guiTop - 20;

        if (scrollWindow == null) {
            scrollWindow = new GuiScrollWindow(this, guiLeft - 40, y, 235, 250, 0);
        } else {
            scrollWindow.xPos = guiLeft - 40;
            scrollWindow.yPos = y;
            scrollWindow.clipWidth = 235;
            scrollWindow.clipHeight = 250;
        }

        addScrollableGui(0, scrollWindow);
        int maxScroll = 0;


        int guiX = 0;
        y = 6;

        OutlineController.getInstance().customOutlines.replace(outline.id, outline);

        scrollWindow.addLabel(new GuiNpcLabel(1, "gui.name", 3, y + 5));
        scrollWindow.getLabel(1).color = 0xffffff;
        scrollWindow.addTextField(new GuiNpcTextField(1, this, this.fontRendererObj, guiX + 120, y, 100, 20, outline.name));
        scrollWindow.getTextField(1).setMaxStringLength(20);

        y += 26;
        scrollWindow.addLabel(new GuiNpcLabel(2, "general.menuName", 3, y + 5));
        scrollWindow.getLabel(2).color = 0xffffff;
        scrollWindow.addTextField(new GuiNpcTextField(2, this, this.fontRendererObj, guiX + 120, y, 100, 20, outline.menuName.replaceAll("§", "&")));
        scrollWindow.getTextField(2).setMaxStringLength(20);

        y += 50;
        scrollWindow.addLabel(new GuiNpcLabel(3, "display.color1", 3, y + 5));
        scrollWindow.getLabel(3).color = 0xffffff;
        scrollWindow.addButton(new GuiNpcButton(3, guiX + 140, y, 60, 20, getColor(outline.innerColor.color)));
        // scrollWindow.getButton(3).enabled = t != None || (t2 == EnumAuraTypes2D.Default || t2 == EnumAuraTypes2D.Base);

        scrollWindow.getButton(3).packedFGColour = outline.innerColor.color;
        scrollWindow.addButton(new GuiNpcButton(31, guiX + 200, y, 20, 20, "X"));
        scrollWindow.getButton(31).enabled = outline.innerColor.color != 0x00ffff;

        y += 23;
        scrollWindow.addLabel(new GuiNpcLabel(4, "display.alpha", 3, y + 5));
        scrollWindow.getLabel(4).color = 0xffffff;
        scrollWindow.addTextField(new GuiNpcTextField(4, this, guiX + 165, y, 33, 18, String.valueOf(outline.innerColor.alpha)));
        scrollWindow.getTextField(4).setMaxStringLength(4);
        scrollWindow.getTextField(4).integersOnly = true;
        scrollWindow.getTextField(4).setMinMaxDefault(-1, 255, -1);
        scrollWindow.addButton(new GuiNpcButton(41, guiX + 200, y - 1, 20, 20, "X"));
        scrollWindow.getButton(41).enabled = outline.innerColor.alpha != -1;

        y += 50;
        scrollWindow.addLabel(new GuiNpcLabel(5, "display.color2", 3, y + 5));
        scrollWindow.getLabel(5).color = 0xffffff;
        scrollWindow.addButton(new GuiNpcButton(5, guiX + 140, y, 60, 20, getColor(outline.outerColor.color)));
        //  scrollWindow.getButton(5).enabled = t == SaiyanGod || t == SaiyanRose || t == UltimateArco;

        scrollWindow.getButton(5).packedFGColour = outline.outerColor.color;
        scrollWindow.addButton(new GuiNpcButton(51, guiX + 200, y, 20, 20, "X"));
        scrollWindow.getButton(51).enabled = outline.outerColor.color != 0xffffff;

        y += 23;
        scrollWindow.addLabel(new GuiNpcLabel(6, "display.alpha", 3, y + 5));
        scrollWindow.getLabel(6).color = 0xffffff;
        scrollWindow.addTextField(new GuiNpcTextField(6, this, guiX + 165, y, 33, 18, String.valueOf(outline.outerColor.alpha)));
        scrollWindow.getTextField(6).setMaxStringLength(4);
        scrollWindow.getTextField(6).integersOnly = true;
        scrollWindow.getTextField(6).setMinMaxDefault(-1, 255, -1);
        scrollWindow.addButton(new GuiNpcButton(61, guiX + 200, y - 1, 20, 20, "X"));
        scrollWindow.getButton(61).enabled = outline.outerColor.alpha != -1;

        y += 50;
        scrollWindow.addLabel(new GuiNpcLabel(7, "display.size", 3, y + 5));
        scrollWindow.getLabel(7).color = 0xffffff;
        scrollWindow.addTextField(new GuiNpcTextField(7, this, guiX + 165, y, 33, 18, String.valueOf(outline.size)));
        scrollWindow.getTextField(7).setMaxStringLength(4);
        scrollWindow.getTextField(7).floatsOnly = true;
        scrollWindow.getTextField(7).setMinMaxDefaultFloat(0, 10, 1);
        scrollWindow.addButton(new GuiNpcButton(71, guiX + 200, y - 1, 20, 20, "X"));
        scrollWindow.getButton(71).enabled = outline.size != 1;

        y += 23;
        maxScroll += 23;
        scrollWindow.addLabel(new GuiNpcLabel(8, "display.speed", 3, y + 5));
        scrollWindow.getLabel(8).color = 0xffffff;
        scrollWindow.addTextField(new GuiNpcTextField(8, this, guiX + 165, y, 33, 18, String.valueOf(outline.speed)));
        scrollWindow.getTextField(8).setMaxStringLength(4);
        scrollWindow.getTextField(8).floatsOnly = true;
        scrollWindow.getTextField(8).setMinMaxDefaultFloat(0, 50, 1);
        scrollWindow.addButton(new GuiNpcButton(81, guiX + 200, y - 1, 20, 20, "X"));
        scrollWindow.getButton(81).enabled = outline.speed != 1;

        y += 23;
        maxScroll += 23;
        scrollWindow.addLabel(new GuiNpcLabel(9, "outline.noiseSize", 3, y + 5));
        scrollWindow.getLabel(9).color = 0xffffff;
        scrollWindow.addTextField(new GuiNpcTextField(9, this, guiX + 165, y, 33, 19, String.valueOf(outline.noiseSize)));
        scrollWindow.getTextField(9).setMaxStringLength(4);
        scrollWindow.getTextField(9).floatsOnly = true;
        scrollWindow.getTextField(9).setMinMaxDefaultFloat(0, 50, 1);
        scrollWindow.addButton(new GuiNpcButton(91, guiX + 200, y - 1, 20, 20, "X"));
        scrollWindow.getButton(91).enabled = outline.noiseSize != 1;

        y += 23;
        maxScroll += 23;
        scrollWindow.addLabel(new GuiNpcLabel(10, "outline.colorSmoothness", 3, y + 5));
        scrollWindow.getLabel(10).color = 0xffffff;
        scrollWindow.addTextField(new GuiNpcTextField(10, this, guiX + 165, y, 33, 18, String.valueOf(outline.colorSmoothness)));
        scrollWindow.getTextField(10).setMaxStringLength(4);
        scrollWindow.getTextField(10).floatsOnly = true;
        scrollWindow.getTextField(10).setMinMaxDefaultFloat(0, 1, 0.2f);
        scrollWindow.addButton(new GuiNpcButton(101, guiX + 200, y - 1, 20, 20, "X"));
        scrollWindow.getButton(101).enabled = outline.colorSmoothness != 0.2f;

        y += 23;
        maxScroll += 23;
        scrollWindow.addLabel(new GuiNpcLabel(11, "outline.colorInterpolation", 3, y + 5));
        scrollWindow.getLabel(11).color = 0xffffff;
        scrollWindow.addTextField(new GuiNpcTextField(11, this, guiX + 165, y, 33, 18, String.valueOf(outline.colorInterpolation)));
        scrollWindow.getTextField(11).setMaxStringLength(4);
        scrollWindow.getTextField(11).floatsOnly = true;
        scrollWindow.getTextField(11).setMinMaxDefaultFloat(0, 1, 0.55f);
        scrollWindow.addButton(new GuiNpcButton(111, guiX + 200, y - 1, 20, 20, "X"));
        scrollWindow.getButton(111).enabled = outline.colorInterpolation != 0.55f;
        y += 23;
        maxScroll += 23;
        scrollWindow.addLabel(new GuiNpcLabel(12, "outline.pulsingSpeed", 3, y + 5));
        scrollWindow.getLabel(12).color = 0xffffff;
        scrollWindow.addTextField(new GuiNpcTextField(12, this, guiX + 165, y, 33, 18, String.valueOf(outline.pulsingSpeed)));
        scrollWindow.getTextField(12).setMaxStringLength(4);
        scrollWindow.getTextField(12).floatsOnly = true;
        scrollWindow.getTextField(12).setMinMaxDefaultFloat(0, 50, 0);
        scrollWindow.addButton(new GuiNpcButton(121, guiX + 200, y - 1, 20, 20, "X"));
        scrollWindow.getButton(121).enabled = outline.pulsingSpeed != 0;

        maxScroll += 10;

        int yOffset = this.yOffset;
        this.addButton(this.unzoom = new GuiNpcButton(666, this.guiLeft + 148 + this.xOffset, this.guiTop + 200 + yOffset, 20, 20, "-"));
        this.addButton(this.zoom = new GuiNpcButton(667, this.guiLeft + 214 + this.xOffset, this.guiTop + 200 + yOffset, 20, 20, "+"));
        this.addButton(this.left = new GuiNpcButton(668, this.guiLeft + 170 + this.xOffset, this.guiTop + 200 + yOffset, 20, 20, "<"));
        this.addButton(this.right = new GuiNpcButton(669, this.guiLeft + 192 + this.xOffset, this.guiTop + 200 + yOffset, 20, 20, ">"));
        scrollWindow.maxScrollY = maxScroll;

    }

    @Override
    public void unFocused(GuiNpcTextField guiNpcTextField) {
        if (guiNpcTextField.id == 1) {
            String name = guiNpcTextField.getText();
            if (!name.isEmpty() && !parent.data.containsKey(name)) {
                String old = parent.outline.name;
                parent.data.remove(parent.outline.name);
                parent.outline.name = name;
                parent.data.put(parent.outline.name, parent.outline.id);
                parent.selected = name;
                parent.scrollOutlines.replace(old, parent.outline.name);
            } else
                guiNpcTextField.setText(parent.outline.name);
        } else if (guiNpcTextField.id == 2) {
            String menuName = guiNpcTextField.getText();
            if (!menuName.isEmpty()) {
                parent.outline.menuName = menuName.replaceAll("&", "§");
            }
        } else if (guiNpcTextField.id == 4) {
            outline.innerColor.alpha = guiNpcTextField.getInteger();
            scrollWindow.getButton(4).enabled = outline.innerColor.alpha != -1;
        } else if (guiNpcTextField.id == 6) {
            outline.outerColor.alpha = guiNpcTextField.getInteger();
            scrollWindow.getButton(61).enabled = outline.outerColor.alpha != -1;

        } else if (guiNpcTextField.id == 7) {
            outline.size = guiNpcTextField.getFloat();
            scrollWindow.getButton(71).enabled = outline.size != 1;

        } else if (guiNpcTextField.id == 8) {
            outline.speed = guiNpcTextField.getFloat();
            scrollWindow.getButton(81).enabled = outline.speed != 1;

        } else if (guiNpcTextField.id == 9) {
            outline.noiseSize = guiNpcTextField.getFloat();
            scrollWindow.getButton(91).enabled = outline.noiseSize != 1;

        } else if (guiNpcTextField.id == 10) {
            outline.colorSmoothness = guiNpcTextField.getFloat();
            scrollWindow.getButton(101).enabled = outline.colorSmoothness != 0.2;

        } else if (guiNpcTextField.id == 11) {
            outline.colorInterpolation = guiNpcTextField.getFloat();
            scrollWindow.getButton(111).enabled = outline.colorInterpolation != 0.55;

        } else if (guiNpcTextField.id == 12) {
            outline.pulsingSpeed = guiNpcTextField.getFloat();
            scrollWindow.getButton(121).enabled = outline.pulsingSpeed != 0;

        }

    }

    public void buttonEvent(GuiButton guibutton) {
        GuiNpcButton button = (GuiNpcButton) guibutton;
        if (button.id == 3) {
            lastColorClicked = 0;
            setSubGui(new SubGuiColorSelector(outline.innerColor.color));
        } else if (button.id == 31) {
            outline.innerColor.color = 0x00ffff;
            initGui();
        } else if (button.id == 41) {
            outline.innerColor.alpha = -1;
            initGui();
        } else if (button.id == 5) {
            lastColorClicked = 1;
            setSubGui(new SubGuiColorSelector(outline.outerColor.color));
        } else if (button.id == 51) {
            outline.outerColor.color = 0xffffff;
            initGui();
        } else if (button.id == 61) {
            outline.outerColor.alpha = -1;
            initGui();
        } else if (button.id == 71) {
            outline.size = 1;
            initGui();
        } else if (button.id == 81) {
            outline.speed = 1;
            initGui();
        } else if (button.id == 91) {
            outline.noiseSize = 1;
            initGui();
        } else if (button.id == 101) {
            outline.colorSmoothness = 0.2f;
            initGui();
        } else if (button.id == 111) {
            outline.colorInterpolation = 0.55f;
            initGui();
        } else if (button.id == 121) {
            outline.pulsingSpeed = 0;
            initGui();
        }

    }

    @Override
    public void keyTyped(char c, int i) {
        super.keyTyped(c, i);
        if (i == 1) {
            close();
        }

    }


    @Override
    public void mouseClicked(int i, int j, int k) {
        super.mouseClicked(i, j, k);
    }

    @Override
    public void subGuiClosed(SubGuiInterface subgui) {
        if (subgui instanceof SubGuiColorSelector) {
            int color = ((SubGuiColorSelector) subgui).color;
            if (lastColorClicked == 0)
                outline.innerColor.color = color;
            else if (lastColorClicked == 1)
                outline.outerColor.color = color;

            initGui();
        }

        if (subgui instanceof SubGuiSelectOutline) {
            if (outline != null) {
                SubGuiSelectOutline selectOutline = ((SubGuiSelectOutline) subgui);
                if (selectOutline.confirmed) {
                    if (selectOutline.selectedOutlineID == outline.id)
                        return;
                    outline.id = selectOutline.selectedOutlineID;
                    initGui();

                }
            }
        }
    }

    @Override
    protected void drawBackground() {
        //  super.drawBackground();

        int xPosGradient = guiLeft + 5;
        int yPosGradient = guiTop + 5;
        // drawGradientRect(xPosGradient, yPosGradient, 130 + xPosGradient, 180 + yPosGradient, 0xc0101010, 0xd0101010);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public void drawScreen(int i, int j, float f) {
        if (Mouse.isButtonDown(0)) {
            if (this.left.mousePressed(this.mc, i, j)) {
                rotation += 0.2 * 2.0F;
            } else if (this.right.mousePressed(this.mc, i, j)) {
                rotation -= 0.2 * 2.0F;
            } else if (this.zoom.mousePressed(this.mc, i, j) && zoomed < 100.0F) {
                zoomed += 0.05 * 2.0F;
            } else if (this.unzoom.mousePressed(this.mc, i, j) && zoomed > 10.0F) {
                zoomed -= 0.05 * 2.0F;
            }
        }
        super.drawScreen(i, j, f);

        if (hasSubGui())
            return;


        int outlineID = visualDisplay.outlineID;
        if (outlineID != -1)
            visualDisplay.outlineID = outline.id;

        int hideName = npc.display.showName;
        int size = npc.display.modelSize;
        npc.display.showName = 1;
        npc.display.modelSize = 5;
        GL11.glColor4f(1, 1, 1, 1);
        EntityLivingBase entity = this.npc;

        int l = guiLeft + 190 + xOffset;
        int i1 = guiTop + 180 + yOffset - 5;


        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glPushMatrix();
        GL11.glTranslatef(l, i1, 60F);

        GL11.glScalef(-zoomed, zoomed, zoomed);
        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
        float f2 = entity.renderYawOffset;
        float f3 = entity.rotationYaw;
        float f4 = entity.rotationPitch;
        float f7 = entity.rotationYawHead;
        float f5 = (float) (l) - i;
        float f6 = (float) (i1 - 50) - j;
        GL11.glRotatef(135F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GL11.glRotatef(-135F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-(float) Math.atan(f6 / 800F) * 20F, 1.0F, 0.0F, 0.0F);
        entity.prevRenderYawOffset = entity.renderYawOffset = rotation;
        entity.prevRotationYaw = entity.rotationYaw = (float) Math.atan(f5 / 80F) * 40F + rotation;
        entity.rotationPitch = -(float) Math.atan(f6 / 80F) * 20F;
        entity.prevRotationYawHead = entity.rotationYawHead = entity.rotationYaw;
        GL11.glTranslatef(0.0F, entity.yOffset, 1F);
        RenderManager.instance.playerViewY = 180F;


        // Render Entity
        try {
            RenderManager.instance.renderEntityWithPosYaw(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F);
        } catch (Exception ignored) {
        }

        entity.prevRenderYawOffset = entity.renderYawOffset = f2;
        entity.prevRotationYaw = entity.rotationYaw = f3;
        entity.rotationPitch = f4;
        entity.prevRotationYawHead = entity.rotationYawHead = f7;

        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glPopMatrix();
        npc.display.showName = hideName;
        npc.display.modelSize = size;
        visualDisplay.outlineID = outlineID;
    }

    /**
     * FIXME: Remove this function after ScrollWindow gets fixed in main CNPC+.
     */
    @Override
    public void updateScreen() {
        if (scrollWindow != null) {
            if (scrollWindow.scrollY > scrollWindow.maxScrollY || scrollWindow.nextScrollY > scrollWindow.maxScrollY) {
                scrollWindow.nextScrollY = scrollWindow.maxScrollY;
                scrollWindow.scrollY = scrollWindow.maxScrollY;
            }

            if (scrollWindow.nextScrollY < 0.0F) {
                scrollWindow.nextScrollY = 0.0F;
            }
        }

        super.updateScreen();
    }

    @Override
    public void selected(int id, String name) {
    }

    public void save() {
    }

    public void close() {
        NoppesUtil.openGUI(player, parent);
        save();
    }

    public String getColor(int input) {
        String str;
        for (str = Integer.toHexString(input); str.length() < 6; str = "0" + str) {
        }
        return str;
    }
}
