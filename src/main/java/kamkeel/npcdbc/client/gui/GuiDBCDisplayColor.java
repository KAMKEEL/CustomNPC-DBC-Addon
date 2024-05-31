package kamkeel.npcdbc.client.gui;

import kamkeel.npcdbc.data.npc.DBCDisplay;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.gui.util.GuiModelInterface;
import noppes.npcs.client.gui.util.GuiNpcTextField;
import noppes.npcs.client.gui.util.ITextfieldListener;
import noppes.npcs.entity.EntityCustomNpc;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class GuiDBCDisplayColor extends GuiModelInterface implements ITextfieldListener {
    private GuiScreen parent;
    private static final ResourceLocation color = new ResourceLocation("customnpcs:textures/gui/color.png");
    private int colorX;
    private int colorY;
    private GuiNpcTextField textfield;
    private final DBCDisplay data;
    private int type = 0; // CM, C1, C2, C3, HairColor, EyeColor

    public GuiDBCDisplayColor(GuiScreen parent, DBCDisplay data, EntityCustomNpc npc, int type) {
        super(npc);
        this.parent = parent;
        this.data = data;
        this.xOffset = 60;
        this.ySize = 230;
        this.type = type;
    }

    public void initGui() {
        super.initGui();
        this.colorX = this.guiLeft + 4;
        this.colorY = this.guiTop + 50;

        if(type == 1){
            this.addTextField(this.textfield = new GuiNpcTextField(0, this, this.guiLeft + 25, this.guiTop + 20, 70, 20, getColor(this.data.bodyC1)));
            this.textfield.setTextColor(this.data.bodyC1);
        } else if(type == 2){
            this.addTextField(this.textfield = new GuiNpcTextField(0, this, this.guiLeft + 25, this.guiTop + 20, 70, 20, getColor(this.data.bodyC2)));
            this.textfield.setTextColor(this.data.bodyC2);
        } else if(type == 3){
            this.addTextField(this.textfield = new GuiNpcTextField(0, this, this.guiLeft + 25, this.guiTop + 20, 70, 20, getColor(this.data.bodyC3)));
            this.textfield.setTextColor(this.data.bodyC3);
        } else if(type == 4){
            this.addTextField(this.textfield = new GuiNpcTextField(0, this, this.guiLeft + 25, this.guiTop + 20, 70, 20, getColor(this.data.hairColor)));
            this.textfield.setTextColor(this.data.hairColor);
        } else if(type == 5){
            this.addTextField(this.textfield = new GuiNpcTextField(0, this, this.guiLeft + 25, this.guiTop + 20, 70, 20, getColor(this.data.eyeColor)));
            this.textfield.setTextColor(this.data.eyeColor);
        } else if(type == 6){
            this.addTextField(this.textfield = new GuiNpcTextField(0, this, this.guiLeft + 25, this.guiTop + 20, 70, 20, getColor(this.data.furColor)));
            this.textfield.setTextColor(this.data.furColor);
        }else {
            this.addTextField(this.textfield = new GuiNpcTextField(0, this, this.guiLeft + 25, this.guiTop + 20, 70, 20, getColor(this.data.bodyCM)));
            this.textfield.setTextColor(this.data.bodyCM);
        }
    }

    public void keyTyped(char c, int i) {
        String prev = this.textfield.getText();
        super.keyTyped(c, i);
        String newText = this.textfield.getText();
        if (!newText.equals(prev)) {
            try {
                int color = Integer.parseInt(this.textfield.getText(), 16);
                switch (type){
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
                    default:
                        this.data.bodyCM = color;
                        break;
                }
                this.textfield.setTextColor(color);
            } catch (NumberFormatException var6) {
                this.textfield.setText(prev);
            }
        }
    }

    protected void actionPerformed(GuiButton btn) {
        super.actionPerformed(btn);
    }

    public void close() {
        this.mc.displayGuiScreen(this.parent);
    }

    public void drawScreen(int par1, int par2, float par3) {
        super.drawScreen(par1, par2, par3);
        this.mc.getTextureManager().bindTexture(color);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.drawTexturedModalRect(this.colorX, this.colorY, 0, 0, 120, 120);
    }

    public void mouseClicked(int i, int j, int k) {
        super.mouseClicked(i, j, k);
        if (i >= this.colorX && i <= this.colorX + 120 && j >= this.colorY && j <= this.colorY + 120) {
            InputStream stream = null;

            try {
                IResource resource = this.mc.getResourceManager().getResource(GuiDBCDisplayColor.color);
                BufferedImage bufferedimage = ImageIO.read(stream = resource.getInputStream());
                int color = bufferedimage.getRGB((i - this.guiLeft - 4) * 4, (j - this.guiTop - 50) * 4) & 16777215;
                if (color != 0) {
                    switch (type){
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
                        default:
                            this.data.bodyCM = color;
                            break;
                    }
                    this.textfield.setTextColor(color);
                    this.textfield.setText(getColor(color));
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
        int color = 0;
        try {
            color = Integer.parseInt(textfield.getText(), 16);
        } catch (NumberFormatException var4) {
            color = 0;
        }

        switch (type){
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
            default:
                this.data.bodyCM = color;
                break;
        }
        textfield.setTextColor(color);
    }

    public String getColor(int input) {
        String str;
        for(str = Integer.toHexString(input); str.length() < 6; str = "0" + str) {
        }

        return str;
    }
}
