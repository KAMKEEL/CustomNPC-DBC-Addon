package kamkeel.npcdbc.client.gui.global.ability;

import kamkeel.npcdbc.data.ability.Ability;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.packets.request.ability.DBCSaveAbility;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.ClientCacheHandler;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.select.GuiAnimationSelection;
import noppes.npcs.client.gui.select.GuiTextureSelection;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.client.renderer.ImageData;
import org.lwjgl.opengl.GL11;

import static noppes.npcs.client.gui.player.inventory.GuiCNPCInventory.specialIcons;

public class SubGuiAbilityGeneral extends GuiNPCInterface implements ISubGuiListener, GuiSelectionListener, ITextfieldListener {
    private final GuiNPCManageAbilities parent;
    public GuiScrollWindow scrollWindow;
    public static Ability ability;
    private final String originalName;

    public SubGuiAbilityGeneral(GuiNPCManageAbilities parent) {
        super(parent.npc);
        this.parent = parent;
        SubGuiAbilityGeneral.ability = parent.ability;
        this.originalName = ability.name;

        xSize = 360;
        ySize = 216;
    }

    @Override
    public void initGui() {
        super.initGui();
        int x = guiLeft - 27;
        int y = guiTop - 30;
        int rightOffset = 25;

        if (scrollWindow == null) {
            scrollWindow = new GuiScrollWindow(this, x, y, 230 + rightOffset, 270, 0);
        } else {
            scrollWindow.xPos = x;
            scrollWindow.yPos = y;
            scrollWindow.clipWidth = 230 + rightOffset;
            scrollWindow.clipHeight = 270;
        }
        scrollWindow.scrollSpeed = 3.5f;

        addScrollableGui(0, scrollWindow);
        int maxScroll = -20;

        int guiX = -5;
        y = 7;

        scrollWindow.addLabel(new GuiNpcLabel(101, "gui.name", 3, y + 5));
        scrollWindow.getLabel(101).color = 0xffffff;
        scrollWindow.addTextField(new GuiNpcTextField(101, this, this.fontRendererObj, guiX + 100 + rightOffset, y, 120, 20, ability.name));
        scrollWindow.getTextField(101).setMaxStringLength(40);

        y += 26;
        scrollWindow.addLabel(new GuiNpcLabel(102, "general.menuName", 3, y + 5));
        scrollWindow.getLabel(102).color = 0xffffff;
        scrollWindow.addTextField(new GuiNpcTextField(102, this, this.fontRendererObj, guiX + 100 + rightOffset, y, 120, 20, ability.menuName.replaceAll("§", "&")));
        scrollWindow.getTextField(102).setMaxStringLength(40);

        y += 26;
        scrollWindow.addLabel(new GuiNpcLabel(103, "ability.editor.kiCost", 3, y + 5));
        scrollWindow.getLabel(103).color = 0xffffff;
        scrollWindow.addTextField(new GuiNpcTextField(103, this, this.fontRendererObj, guiX + 180 + rightOffset, y, 40, 20, ability.kiCost + ""));
        scrollWindow.getTextField(103).setIntegersOnly();
        scrollWindow.getTextField(103).min = 0;

        y += 26;
        scrollWindow.addLabel(new GuiNpcLabel(104, "ability.editor.cooldown", 3, y + 5));
        scrollWindow.getLabel(104).color = 0xffffff;
        scrollWindow.addTextField(new GuiNpcTextField(104, this, this.fontRendererObj, guiX + 180 + rightOffset, y, 40, 20, ability.cooldown + ""));
        scrollWindow.getTextField(104).setIntegersOnly();
        scrollWindow.getTextField(104).min = 0;

        y += 26;
//        scrollWindow.addLabel(new GuiNpcLabel(105, "ability.editor.maxCharge", 3, y + 5));
//        scrollWindow.getLabel(105).color = 0xffffff;
//        scrollWindow.addTextField(new GuiNpcTextField(105, this, this.fontRendererObj, guiX + 100 + rightOffset, y, 40, 20, ability.maxCharge + ""));
//        scrollWindow.getTextField(105).setIntegersOnly();
//        scrollWindow.getTextField(105).min = 0;

        y += 26;
        scrollWindow.addLabel(new GuiNpcLabel(106, "ability.editor.multiUse", 3, y + 5));
        scrollWindow.getLabel(106).color = 0xffffff;
        scrollWindow.addButton(new GuiNpcButtonYesNo(106, guiX + 160 + rightOffset, y, 60, 20, ability.multiUse));

        if (ability.multiUse) {
            maxScroll += 26;
            y += 26;
            scrollWindow.addLabel(new GuiNpcLabel(1061, "ability.editor.maxUses", 3, y + 5));
            scrollWindow.getLabel(1061).color = 0xffffff;
            scrollWindow.addTextField(new GuiNpcTextField(106, this, this.fontRendererObj, guiX + 180 + rightOffset, y, 40, 20, ability.maxUses + ""));
            scrollWindow.getTextField(106).setIntegersOnly();
            scrollWindow.getTextField(106).min = 0;
        }

        y += 26;
        scrollWindow.addLabel(new GuiNpcLabel(107, "ability.editor.type", 3, y + 5));
        scrollWindow.getLabel(107).color = 0xffffff;
        scrollWindow.addButton(new GuiNpcButton(107, guiX + 160 + rightOffset, y, 60, 20, new String[]{"Cast", "Toggle", "Animated"}, ability.type.ordinal()));

        if (ability.type == Ability.Type.Animated) {
            maxScroll += 26;
            y += 26;
            scrollWindow.addLabel(new GuiNpcLabel(1071, "animation.animation", 3, y + 5));
            scrollWindow.getLabel(1071).color = 0xffffff;
            scrollWindow.addButton(new GuiNpcButton(1071, guiX + 160 + rightOffset, y, 60, 20, "gui.select"));

            if (ability.animation != null) {
                scrollWindow.getButton(1071).setDisplayText(ability.animation.getName());
            }
        }

        y += 52;
        scrollWindow.addLabel(new GuiNpcLabel(108, "display.texture", 3, y + 5));
        scrollWindow.getLabel(108).color = 0xffffff;
        scrollWindow.addTextField(new GuiNpcTextField(108, this, this.fontRendererObj, guiX + 100 + rightOffset, y, 120, 20, ability.icon));
        scrollWindow.getTextField(108).setMaxStringLength(100);

        maxScroll += 26;
        y += 26;
        scrollWindow.addLabel(new GuiNpcLabel(109, "ability.editor.width", 3, y + 5));
        scrollWindow.getLabel(109).color = 0xffffff;
        scrollWindow.addTextField(new GuiNpcTextField(109, this, this.fontRendererObj, guiX + 180 + rightOffset, y, 40, 20, ability.width + ""));
        scrollWindow.getTextField(109).setIntegersOnly();

        maxScroll += 26;
        y += 26;
        scrollWindow.addLabel(new GuiNpcLabel(110, "ability.editor.height", 3, y + 5));
        scrollWindow.getLabel(110).color = 0xffffff;
        scrollWindow.addTextField(new GuiNpcTextField(110, this, this.fontRendererObj, guiX + 180 + rightOffset, y, 40, 20, ability.height + ""));
        scrollWindow.getTextField(110).setIntegersOnly();

        maxScroll += 26;
        y += 26;
        scrollWindow.addLabel(new GuiNpcLabel(111, "ability.editor.xPos", 3, y + 5));
        scrollWindow.getLabel(111).color = 0xffffff;
        scrollWindow.addTextField(new GuiNpcTextField(111, this, this.fontRendererObj, guiX + 180 + rightOffset, y, 40, 20, ability.iconX + ""));
        scrollWindow.getTextField(111).setIntegersOnly();

        maxScroll += 26;
        y += 26;
        scrollWindow.addLabel(new GuiNpcLabel(112, "ability.editor.yPos", 3, y + 5));
        scrollWindow.getLabel(112).color = 0xffffff;
        scrollWindow.addTextField(new GuiNpcTextField(112, this, this.fontRendererObj, guiX + 180 + rightOffset, y, 40, 20, ability.iconY + ""));
        scrollWindow.getTextField(112).setIntegersOnly();

        maxScroll += 39;
        y += 39;
        scrollWindow.addButton(new GuiNpcButton(113, 3, y, 80, 20, "script.scripts"));

        scrollWindow.maxScrollY = maxScroll;
    }

    @Override
    public void drawScreen(int i, int j, float f) {
        super.drawScreen(i, j, f);

        int iconRenderSize = 96;

        int screenWidth = this.width;
        int screenHeight = this.height;

        int margin = 100;

        int x = screenWidth - iconRenderSize - margin;
        int y = (screenHeight - iconRenderSize) / 2;

        TextureManager textureManager = mc.getTextureManager();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        textureManager.bindTexture(specialIcons);
        func_152125_a(x, y, 0, 224, 16, 16, iconRenderSize, iconRenderSize, 256, 256);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        ImageData data = ClientCacheHandler.getImageData(ability.icon);
        if (data.imageLoaded()) {
            data.bindTexture();

            int iconX = ability.iconX;
            int iconY = ability.iconY;
            int iconWidth = ability.getWidth();
            int iconHeight = ability.getHeight();
            int width = data.getTotalWidth();
            int height = data.getTotalWidth();

            func_152125_a(
                x, y,
                iconX, iconY,
                iconWidth, iconHeight,
                iconRenderSize, iconRenderSize,
                width, height
            );
        } else {
            textureManager.bindTexture(new ResourceLocation("customnpcs", "textures/marks/question.png"));
            func_152125_a(x, y, 0, 0, 1, 1, iconRenderSize, iconRenderSize, 1, 1);
        }
    }

    @Override
    public void buttonEvent(GuiButton guibutton) {
        GuiNpcButton button = (GuiNpcButton) guibutton;
        int id = button.id;

        if (id == 106) {
            ability.multiUse = !ability.multiUse;
            initGui();
        }

        if (id == 107) {
            ability.type = Ability.Type.values()[button.getValue()];
            initGui();
        }

        if (id == 1071) {
            setSubGui(new GuiAnimationSelection());
        }

        if (id == 1081) {
            setSubGui(new GuiTextureSelection(this.npc, scrollWindow.getTextField(id).getText()));
        }

        if (id == 113) {
            DBCPacketHandler.Instance.sendToServer(new DBCSaveAbility(ability.writeToNBT(false), originalName));
            GuiScriptAbility scriptGUI = new GuiScriptAbility(parent, ability);
            scriptGUI.setWorldAndResolution(mc, width, height);
            scriptGUI.initGui();
            mc.currentScreen = scriptGUI;
        }
    }

    @Override
    public void unFocused(GuiNpcTextField textfield) {
        int id = textfield.id;

        if (id == 101) {
            String name = textfield.getText();
            if (!name.isEmpty() && !parent.data.containsKey(name)) {
                String old = parent.ability.name;
                parent.data.remove(parent.ability.name);
                parent.ability.name = name;
                parent.data.put(parent.ability.name, parent.ability.id);
                parent.selected = name;
                parent.scrollAbilities.replace(old, parent.ability.name);
            } else {
                textfield.setText(ability.name);
            }
        }

        if (id == 102) {
            String menuName = textfield.getText();
            if (!menuName.isEmpty()) {
                parent.ability.menuName = menuName.replaceAll("&", "§");
            }
        }

        if (id == 103) {
            try {
                parent.ability.kiCost = Integer.parseInt(textfield.getText());
            } catch (Exception i) {
                textfield.setText("0");
            }
        }

        if (id == 104) {
            try {
                parent.ability.cooldown = Integer.parseInt(textfield.getText());
            } catch (Exception i) {
                textfield.setText("0");
            }
        }

        if (id == 105) {
        }

        if (id == 108) {
            parent.ability.icon = textfield.getText();
        }

        if (id == 109) {
            try {
                parent.ability.width = Integer.parseInt(textfield.getText());
            } catch (Exception i) {
                textfield.setText("16");
            }
        }

        if (id == 110) {
            try {
                parent.ability.height = Integer.parseInt(textfield.getText());
            } catch (Exception i) {
                textfield.setText("16");
            }
        }

        if (id == 111) {
            try {
                parent.ability.iconX = Integer.parseInt(textfield.getText());
            } catch (Exception i) {
                textfield.setText("0");
            }
        }

        if (id == 112) {
            try {
                parent.ability.iconY = Integer.parseInt(textfield.getText());
            } catch (Exception i) {
                textfield.setText("0");
            }
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
    public void subGuiClosed(SubGuiInterface subgui) {
        if (subgui instanceof GuiAnimationSelection) {
            if (ability != null) {
                GuiAnimationSelection selectAnimation = ((GuiAnimationSelection) subgui);
                if (selectAnimation.confirmed) {
                    if (ability.animation != null && selectAnimation.selectedAnimationID == ability.animation.id)
                        return;
                    ability.setAnimation(selectAnimation.selectedAnimationID);
                    initGui();
                }
            }
        }
    }

    @Override
    protected void drawBackground() {
        super.drawBackground();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void close() {
        NoppesUtil.openGUI(player, parent);
        DBCPacketHandler.Instance.sendToServer(new DBCSaveAbility(ability.writeToNBT(false), originalName));
        ability = null;
    }

    @Override
    public void save() {

    }

    @Override
    public void selected(int id, String name) {

    }
}
