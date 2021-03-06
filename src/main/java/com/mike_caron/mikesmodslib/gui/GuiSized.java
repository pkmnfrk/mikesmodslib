package com.mike_caron.mikesmodslib.gui;

public abstract class GuiSized extends GuiControl
{
    protected int width;
    protected int height;

    public GuiSized(int x, int y, int width, int height)
    {
        super(x, y);
        this.height = height;
        this.width = width;
    }

    public int getWidth()
    {
        return width;
    }

    public void setWidth(int width) { this.width = width; }

    public int getHeight()
    {
        return height;
    }

    public void setHeight(int height) { this.height = height; }

}
