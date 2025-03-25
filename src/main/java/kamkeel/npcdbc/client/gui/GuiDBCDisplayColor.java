package kamkeel.npcdbc.client.gui;

import kamkeel.npcdbc.data.npc.DBCDisplay;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.data.ModelData;
import org.apache.commons.lang3.mutable.MutableInt;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class GuiDBCDisplayColor extends SubGuiInterface implements ITextfieldListener, ISliderListener {
    private GuiModelDBC parent;
    private ModelData modelData;
    private static final ResourceLocation color = new ResourceLocation("customnpcs:textures/gui/color.png");
    private int colorX;
    private int colorY;
    private GuiNpcTextField textfield;
    private final DBCDisplay data;
    private int type = 0; // CM, C1, C2, C3, HairColor, EyeColor
    public int selectedColor = -1;
    public float defaultAlpha = -1, selectedAlpha = -1;
    public GuiNpcSlider alphaSlider;
    public int buttonID;
    public int xOffset, yOffset;
    public MutableInt col;

    public GuiDBCDisplayColor(GuiModelDBC parent, ModelData modelData, DBCDisplay data, EntityCustomNpc npc, int type, int buttonID) {
        super();
        this.parent = parent;
        this.modelData = modelData;
        this.data = data;
        this.ySize = 200;
        xOffset = 40;
        this.type = type;
        this.buttonID = buttonID;
        drawDefaultBackground = false;
        closeOnEsc = true;
    }

    public void initGui() {
        super.initGui();
        this.colorX = this.guiLeft + xOffset + 4;
        this.colorY = this.guiTop + yOffset + 50;


        if (defaultAlpha != -1) {
            this.addSlider(alphaSlider = new GuiNpcSlider(this, 1, this.colorX - 1, colorY + 120, selectedAlpha = defaultAlpha));
            alphaSlider.width = 118;
        }

        int color = selectedColor = getColor();

        this.addTextField(this.textfield = new GuiNpcTextField(0, this, this.guiLeft + xOffset + 25, this.guiTop + yOffset + 20, 70, 20, getColor(color)));
        this.textfield.setTextColor(color);

        this.addButton(new GuiNpcButton(66, guiLeft + ySize - 25, this.guiTop + 5, 20, 20, "X"));
    }

    public GuiDBCDisplayColor setColor(MutableInt col) {
        this.col = col;
        return this;
    }


    public GuiDBCDisplayColor hasAlphaSlider(float defaultAlpha) {
        this.defaultAlpha = defaultAlpha;
        return this;
    }

    public void keyTyped(char c, int i) {
        String prev = this.textfield.getText();
        super.keyTyped(c, i);
        //  Minecraft.getMinecraft().currentScreen = null;
        String newText = this.textfield.getText();
        if (!newText.equals(prev)) {
            try {
                int color = Integer.parseInt(this.textfield.getText(), 16);
                setColor(color);
            } catch (NumberFormatException var6) {
                this.textfield.setText(prev);
            }
        }
    }

    public int getColor() {
        int color = -1;
        if (type == 0) {
            color = this.data.bodyCM;
        } else if (type == 1) {
            color = this.data.bodyC1;
        } else if (type == 2) {
            color = this.data.bodyC2;
        } else if (type == 3) {
            color = this.data.bodyC3;
        } else if (type == 4) {
            color = this.data.hairColor;
        } else if (type == 5) {
            color = this.data.eyeColor;
        } else if (type == 6) {
            color = this.data.furColor;
        } else if (type == 7) {
            color = this.data.kiWeaponLeft.color.color;
        } else if (type == 8) {
            color = this.data.kiWeaponRight.color.color;
        }

        return color;
    }

    public void setColor(int color) {
        switch (type) {
            case 0:
                this.data.bodyCM = color;
                break;
            case 1:
                this.data.bodyC1 = color;
                break;
            case 2:
                this.data.bodyC2 = color;
                break;
            case 3:
                this.data.bodyC3 = color;
                break;
            case 4:
                this.data.hairColor = color;
                break;
            case 5:
                this.data.eyeColor = color;
                break;
            case 6:
                this.data.furColor = color;
                break;
            case 7:
                this.data.kiWeaponLeft.color.color = color;
                break;
            case 8:
                this.data.kiWeaponRight.color.color = color;
                break;
        }
        this.textfield.setTextColor(color);
        this.textfield.setTextColor(color);
        this.textfield.setText(getColor(color));
        selectedColor = color;


    }

    protected void actionPerformed(GuiButton btn) {
        super.actionPerformed(btn);
        if (btn instanceof GuiNpcButton) {
            if (((GuiNpcButton) btn).id == 66) {
                close();
            }
        }
    }

    public void close() {
        super.close();
    }

    public void drawScreen(int par1, int par2, float par3) {
        drawGradientRect(guiLeft, guiTop, guiLeft + xSize, guiTop + ySize, 0xf1101010, 0xf1101010);
        super.drawScreen(par1, par2, par3);
        this.mc.getTextureManager().bindTexture(color);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, selectedAlpha > -1 ? selectedAlpha : 1);
        this.drawTexturedModalRect(this.colorX, this.colorY, 0, 0, 120, 120);
    }

    public void mouseClicked(int i, int j, int k) {

        super.mouseClicked(i, j, k);
        if (i >= this.colorX && i <= this.colorX + 120 && j >= this.colorY && j <= this.colorY + 120) {
            InputStream stream = null;

            try {
                IResource resource = this.mc.getResourceManager().getResource(GuiDBCDisplayColor.color);
                BufferedImage bufferedimage = ImageIO.read(stream = resource.getInputStream());
                int color = bufferedimage.getRGB((i - this.guiLeft - xOffset - 4) * 4, (j - this.guiTop - yOffset - 50) * 4) & 16777215;
                if (color != 0) {
                    setColor(color);
                }
            } catch (IOException ignored) {
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException var15) {
                    }
                }

            }

        }
    }

    public void unFocused(GuiNpcTextField textfield) {
        int color = -1;
        try {
            color = Integer.parseInt(textfield.getText(), 16);
        } catch (NumberFormatException var4) {
            color = -1;
        }
        setColor(color);
    }

    public String getColor(int input) {
        String str;
        for (str = Integer.toHexString(input); str.length() < 6; str = "0" + str) {
        }

        return str;
    }

    public void mouseDragged(GuiNpcSlider slider) {
        int percent = (int) (slider.sliderValue * 255.0F);
        slider.setString(percent + "");
        if (slider.id == 1) {
            selectedAlpha = slider.sliderValue;

        }

        if (type == 7) {
            this.data.kiWeaponLeft.color.alpha = selectedAlpha;
        } else if (type == 8) {
            this.data.kiWeaponRight.color.alpha = selectedAlpha;
        }

    }

    public void mousePressed(GuiNpcSlider slider) {
    }

    public void mouseReleased(GuiNpcSlider slider) {
    }

}
