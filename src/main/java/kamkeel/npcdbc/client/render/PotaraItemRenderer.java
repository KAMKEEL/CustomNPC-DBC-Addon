package kamkeel.npcdbc.client.render;

import kamkeel.npcdbc.items.Potara;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import noppes.npcs.client.ClientCacheHandler;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class PotaraItemRenderer implements IItemRenderer {
    private static final ResourceLocation enchant = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    private static final ResourceLocation unsplit = new ResourceLocation("textures/items/diamond.png");


    public PotaraItemRenderer() {}

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
       return type == ItemRenderType.ENTITY || type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON || type == ItemRenderType.INVENTORY;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return type == ItemRenderType.ENTITY && (helper == ItemRendererHelper.ENTITY_ROTATION || helper == ItemRendererHelper.ENTITY_BOBBING);
    }

    // Only Run if Custom Size
    public void customRender(ItemStack par2ItemStack) {
        if (!(par2ItemStack.getItem() instanceof Potara))
            return;
    }

    // Only Run if Custom Size
    public void customRenderPart2(ItemStack par2ItemStack) {
        if (!(par2ItemStack.getItem() instanceof Potara))
            return;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        if (type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
            IIcon iicon = ((EntityLivingBase)data[1]).getItemIcon(item, 0);
            renderItemEquipped(iicon, item);
        } else if (type == ItemRenderType.EQUIPPED) {
            IIcon iicon = ((EntityLivingBase)data[1]).getItemIcon(item, 0);
            renderItemEquipped(iicon, item);
        } else if (type == ItemRenderType.ENTITY) {
            EntityItem entityItem = (EntityItem)data[1];
            this.renderDroppedItem(entityItem, item);
        } else if (type == ItemRenderType.INVENTORY) {
            this.renderInventoryItem(item, (RenderBlocks)data[0]);
        }
    }

    public void renderItemEquipped(IIcon iicon, ItemStack par2ItemStack) {
        int par3 = 0;

        Minecraft mc = Minecraft.getMinecraft();
        TextureManager texturemanager = mc.getTextureManager();

        if (iicon == null) {
            return;
        }

        Tessellator tessellator = Tessellator.instance;
        if (bindPotaraTexture(texturemanager, par2ItemStack)) {
            texturemanager.bindTexture(texturemanager.getResourceLocation(par2ItemStack.getItemSpriteNumber()));
        } else {
            texturemanager.bindTexture(unsplit);
        }

        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        if (bindPotaraTexture(texturemanager, par2ItemStack)) {
            float f = iicon.getMinU();
            float f1 = iicon.getMaxU();
            float f2 = iicon.getMinV();
            float f3 = iicon.getMaxV();
            ItemRenderer.renderItemIn2D(tessellator, f1, f2, f, f3, iicon.getIconWidth(), iicon.getIconHeight(), 0.0625F);
        } else {
            ItemRenderer.renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
        }

        if (par2ItemStack.hasEffect(par3)) {
            GL11.glDepthFunc(GL11.GL_EQUAL);
            GL11.glDisable(GL11.GL_LIGHTING);
            texturemanager.bindTexture(enchant);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
            float f7 = 0.76F;
            GL11.glColor4f(0.5F * f7, 0.25F * f7, 0.8F * f7, 1.0F);
            GL11.glMatrixMode(GL11.GL_TEXTURE);
            GL11.glPushMatrix();
            float f8 = 0.125F;
            GL11.glScalef(f8, f8, f8);
            float f9 = (float) (Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
            GL11.glTranslatef(f9, 0.0F, 0.0F);
            GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
            ItemRenderer.renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(f8, f8, f8);
            f9 = (float) (Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
            GL11.glTranslatef(-f9, 0.0F, 0.0F);
            GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
            ItemRenderer.renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
            GL11.glPopMatrix();
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glDepthFunc(GL11.GL_LEQUAL);
        }

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
    }

    public void renderDroppedItem(EntityItem entityItem, ItemStack item) {
        Tessellator tessellator = Tessellator.instance;
        float f9 = 0.5F;
        float f10 = 0.25F;
        GL11.glPushMatrix();
        float f12 = 0.0625F;
        float f11 = 0.021875F;

        GL11.glTranslatef(-f9, -f10, -(f12 + f11));
        GL11.glTranslatef(0f, 0f, f12 + f11);

        Minecraft mc = Minecraft.getMinecraft();
        TextureManager texturemanager = mc.getTextureManager();

        if (bindPotaraTexture(texturemanager, item)) {
            texturemanager.bindTexture(TextureMap.locationItemsTexture);
            IIcon par2Icon = item.getIconIndex();
            ItemRenderer.renderItemIn2D(tessellator, par2Icon.getMaxU(), par2Icon.getMinV(), par2Icon.getMinU(), par2Icon.getMaxV(), par2Icon.getIconWidth(), par2Icon.getIconHeight(), f12);
        } else {
            texturemanager.bindTexture(unsplit);
            ItemRenderer.renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
        }
        GL11.glPopMatrix();
    }


    public void renderInventoryItem(ItemStack itemStack, RenderBlocks renderBlocks) {
        IIcon iicon = itemStack.getItem().getIcon(itemStack, -1);

        GL11.glDisable(GL11.GL_LIGHTING); //Forge: Make sure that render states are reset, a renderEffect can derp them up.
        GL11.glEnable(GL11.GL_ALPHA_TEST);

        Minecraft mc = Minecraft.getMinecraft();
        TextureManager texturemanager = mc.getTextureManager();
        if(itemStack.getTagCompound() != null && itemStack.getTagCompound().hasKey("Side")){
            RenderItem.getInstance().renderIcon(0, 0, iicon, 16, 16);
        } else {
            texturemanager.bindTexture(unsplit);
            int posX = 0;
            int posY = 0;
            int imageHeight = 16;
            int imageWidth = 16;
            Tessellator tessellator = Tessellator.instance;
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV((double)(posX), (double)(posY + imageHeight), 0, 0, 1);
            tessellator.addVertexWithUV((double)(posX + imageWidth), (double)(posY + imageHeight), 0, 1, 1);
            tessellator.addVertexWithUV((double)(posX + imageWidth), (double)(posY), 0, 1, 0);
            tessellator.addVertexWithUV((double)(posX), (double)(posY), 0, 0, 0);
            tessellator.draw();
        }

        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_LIGHTING);

        if (itemStack.hasEffect(0))
        {
            RenderItem.getInstance().renderEffect(texturemanager, 0, 0);
        }
        GL11.glEnable(GL11.GL_LIGHTING);
    }


    public boolean bindPotaraTexture(TextureManager textureManager, ItemStack itemStack){
        if(itemStack.getTagCompound() != null && itemStack.getTagCompound().hasKey("Side")){
            return true;
        }
        return false;
    }

}
