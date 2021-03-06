package com.mike_caron.mikesmodslib.gui;

import net.minecraft.client.renderer.GlStateManager;

public class GuiImageToggleButton
    extends GuiToggleButton
{
    //ResourceLocation texture;
    //int texWidth, texHeight, sourceX, sourceY;
    GuiImage image;
    int id;


    public GuiImageToggleButton(int id, int x, int y, int width, int height, GuiImage image)
    {
        super(id, x, y, width, height);

        this.image = image;
    }

    @Override
    public void draw()
    {
        super.draw();

        float dx = this.width / 2f - image.getWidth() / 2f;
        float dy = this.height / 2f - image.getHeight() / 2f;

        GlStateManager.pushMatrix();
        GlStateManager.translate(dx, dy, 0);
        image.draw();
        GlStateManager.popMatrix();
    }
}
