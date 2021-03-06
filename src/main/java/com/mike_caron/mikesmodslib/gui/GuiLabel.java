package com.mike_caron.mikesmodslib.gui;

import java.awt.*;

public class GuiLabel
    extends GuiControl
{
    protected String stringLabel;

    protected Color color;

    protected Alignment alignment;
    protected VerticalAlignment verticalAlignment;

    public GuiLabel(int x, int y, String key)
    {
        this(x,y, null, key, Alignment.LEFT, VerticalAlignment.BOTTOM);
    }

    public GuiLabel(int x, int y, Color color, String key)
    {
        this(x, y, color, key, Alignment.LEFT, VerticalAlignment.BOTTOM);
    }

    public GuiLabel(int x, int y, Color color, String key, Alignment alignment)
    {
        this(x, y, color, key, alignment, VerticalAlignment.BOTTOM);
    }

    public GuiLabel(int x, int y, Color color, String key, Alignment alignment, VerticalAlignment verticalAlignment)
    {
        super(x, y);

        this.color = color;
        this.stringLabel = key;
        this.alignment = alignment;
        this.verticalAlignment = verticalAlignment;
    }

    @Override
    public int getWidth()
    {
        return this.parent.getFontRenderer().getStringWidth(stringLabel);
    }

    @Override
    public int getHeight()
    {
        return 10;
    }

    public void setColor(Color color)
    {
        this.color = color;
    }

    @Override
    public void draw()
    {
        if(!this.visible) return;

        String l = getLabel();

        int dx = 0;
        int dy = 0;
        int sw = this.parent.getFontRenderer().getStringWidth(l);
        int sh = this.parent.getFontRenderer().FONT_HEIGHT;

        if(alignment == Alignment.CENTER)
        {
            dx = - (sw / 2);
        }
        else if(alignment == Alignment.RIGHT)
        {
            dx = -sw;
        }

        if(verticalAlignment == VerticalAlignment.TOP)
        {
            dy = -sh;
        }
        else if(verticalAlignment == VerticalAlignment.MIDDLE)
        {
            dy = -(sh / 2);
        }

        Color col = this.color != null ? this.color : parent.getForeColor();
        this.parent.getFontRenderer().drawString(l, dx, dy, col.getRGB());
    }

    public String getLabel()
    {
        return stringLabel;
    }

    public enum Alignment
    {
        LEFT,
        CENTER,
        RIGHT
    }

    public enum VerticalAlignment
    {
        TOP,
        MIDDLE,
        BOTTOM
    }
}
