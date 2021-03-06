package com.mike_caron.mikesmodslib.gui;

import com.mike_caron.mikesmodslib.Mod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import javax.annotation.Nullable;
import java.awt.Color;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class GuiBase
    extends GuiScreen
    implements IGuiGroup
{
    protected int guiLeft, guiTop, xSize, ySize;

    protected ResourceLocation background;

    protected final List<GuiControl> controls = new ArrayList<>();
    private GuiLabel titleLabel;

    private Color foreColor = GuiUtil.FONT_COLOUR;

    private GuiControl mouseOverControl = null;
    private boolean leftDown = false, rightDown = false, middleDown = false;
    private List<List<GuiControl>> waitingForButton = new ArrayList<List<GuiControl>>();

    public GuiBase(int width, int height, ResourceLocation background)
    {
        super();

        this.xSize = width;
        this.ySize = height;
        this.background = background;

        waitingForButton.add(new ArrayList<>());
        waitingForButton.add(new ArrayList<>());
        waitingForButton.add(new ArrayList<>());
    }

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height)
    {
        super.setWorldAndResolution(mc, width, height);

        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
    }

    @Override
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();

        int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;

        GuiControl newMouseControl = null;

        newMouseControl = this.hitTest(mouseX - guiLeft, mouseY - guiTop);

        if(mouseOverControl != newMouseControl)
        {
            if(mouseOverControl != null)
            {
                mouseOverControl.onMouseExit();
            }
            mouseOverControl = newMouseControl;
            if(mouseOverControl != null)
            {
                mouseOverControl.onMouseEnter();
            }
        }

        if(mouseOverControl != null)
        {
            int transX = mouseOverControl.parent.translateFromScreenX(mouseX) - guiLeft - mouseOverControl.getX();
            int transY = mouseOverControl.parent.translateFromScreenY(mouseY) - guiTop - mouseOverControl.getY();

            mouseOverControl.onMouseOver(transX, transY);

            int dWheel = Mouse.getEventDWheel() / 120;
            if(dWheel != 0)
            {
                mouseOverControl.onMouseWheel(transX, transY, dWheel);
            }
        }

        Stream<GuiControl> allWaiting = waitingForButton.stream().map(Collection::stream).reduce(Stream.empty(), Stream::concat).distinct();

        allWaiting.forEach(c -> c.onMouseMove(
            c.parent.translateFromScreenX(mouseX) - c.getX(),
            c.parent.translateFromScreenY(mouseY) - c.getY()
        ));


        int button = Mouse.getEventButton();
        if(button >= 0 && button <= 2)
        {
            boolean newState = Mouse.getEventButtonState();

            if(getStateForButton(button) && !newState)
            {
                setStateForButton(button, false);

                //mouseOverControl.onMouseUp(mouseX, mouseY, button);
                for(GuiControl waiting : waitingForButton.get(button))
                {
                    int transX = waiting.parent.translateFromScreenX(mouseX) - waiting.getX();
                    int transY = waiting.parent.translateFromScreenY(mouseY) - waiting.getY();
                    waiting.onMouseUp(transX, transY, button);
                }
                waitingForButton.get(button).clear();
            }
            else if(!getStateForButton(button) && newState)
            {
                setStateForButton(button, true);
                if(mouseOverControl != null)
                {
                    int transX = mouseOverControl.parent.translateFromScreenX(mouseX) - mouseOverControl.getX();
                    int transY = mouseOverControl.parent.translateFromScreenY(mouseY) - mouseOverControl.getY();
                    mouseOverControl.onMouseDown(transX, transY, button);
                    waitingForButton.get(button).add(mouseOverControl);
                }

                if(mouseOverControl == null || !mouseOverControl.canHaveFocus())
                {
                    for(GuiControl control : controls)
                    {
                        if (control.hasFocus())
                        {
                            control.setFocused(false);
                            break;
                        }
                    }
                }
            }
        }


    }

    private boolean getStateForButton(int button)
    {
        if(button == 0) return leftDown;
        if(button == 1) return rightDown;
        if(button == 2) return middleDown;
        return false;
    }

    private void setStateForButton(int button, boolean state)
    {
        if(button == 0) leftDown = state;
        if(button == 1) rightDown = state;
        if(button == 2) middleDown = state;
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();

        for(GuiControl control : this.controls)
        {
            control.update();
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        boolean textFocused = false;

        for (GuiControl control : this.controls) {
            if(control.hasFocus())
            {
                textFocused = true;
                control.onKeyTyped(typedChar, keyCode);

                if(keyCode == Keyboard.KEY_ESCAPE)
                {
                    control.setFocused(false);
                    this.controlLostFocus(control);
                }

                break;
            }
        }

        if (!textFocused)
        {
            if(keyCode == Keyboard.KEY_ESCAPE || this.mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode))
            {
                this.mc.player.closeScreen();
                return;
            }

            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    public final void initGui()
    {
        super.initGui();
    }

    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate(guiLeft, guiTop, 0);

        for (GuiControl control : this.controls) {
            if(control.isVisible())
            {
                GlStateManager.pushMatrix();
                GlStateManager.translate(control.getX(), control.getY(), 0);
                control.preDraw();
                control.draw();
                control.postDraw();
                GlStateManager.popMatrix();
            }
        }

        GlStateManager.popMatrix();
    }

    public void drawGuiContainerBackgroundLayer(int mouseX, int mouseY)
    {
        GlStateManager.color(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(background);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        drawGuiContainerBackgroundLayer(mouseX, mouseY);
        //super.drawScreen(mouseX, mouseY, partialTicks);
        drawGuiContainerForegroundLayer(mouseX, mouseY);
        this.renderHoveredToolTip(this.mc.player, this.mc.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL, mouseX, mouseY);
    }

    @Override
    public void addControl(GuiControl control)
    {
        this.controls.add(control);
        control.setParent(this);
        this.sort();
    }

    @Override
    public int translateToScreenX(int x)
    {
        return guiLeft + x;
    }

    @Override
    public int translateToScreenY(int y)
    {
        return guiTop + y;
    }

    @Override
    public int translateFromScreenX(int x)
    {
        return x - guiLeft;
    }

    @Override
    public int translateFromScreenY(int y)
    {
        return y - guiTop;
    }

    @Override
    public void removeControl(GuiControl control)
    {
        this.controls.remove(control);
    }

    @Override
    public void clearControls()
    {
        this.controls.clear();
    }

    protected String getTitleKey()
    {
        return null;
    }

    protected void drawCenteredWrappedString(String message, int x, int y, int wrapWidth)
    {
        List<String> lines = this.fontRenderer.listFormattedStringToWidth(message, wrapWidth);
        int height = 10 * lines.size();
        int ty = y - height / 2;

        for(int i = 0; i < lines.size(); i++)
        {
            int w = this.fontRenderer.getStringWidth(lines.get(i));
            this.fontRenderer.drawString(lines.get(i), x - w / 2, ty, GuiUtil.FONT_COLOUR.getRGB());
            ty += 10;
        }
    }

    protected void controlLostFocus(Gui textField)
    {

    }

    protected void initControls()
    {
        addControls();
    }

    protected void addControls()
    {
        String key = getTitleKey();
        if(key == null) key = "title not set";
        titleLabel = GuiUtil.staticLabelFromTranslationKey(6, 6, key);
        this.addControl(titleLabel);
    }

    public FontRenderer getFontRenderer()
    {
        return this.fontRenderer;
    }

    protected void onActionPerformed(GuiControl control)
    {

    }

    protected void renderHoveredToolTip(EntityPlayer player, ITooltipFlag tooltipFlag, int mouseX, int mouseY)
    {
        if(mouseOverControl != null)
        {
            int goodX = mouseOverControl.parent.translateFromScreenX(mouseX);
            int goodY = mouseOverControl.parent.translateFromScreenY(mouseY);

            List<String> toolTip = mouseOverControl.getTooltip(player, tooltipFlag, goodX, goodY);
            if(toolTip != null)
            {
                this.drawHoveringText(toolTip, mouseX, mouseY);
            }
        }
    }

    @Override
    public void onMouseEnter()
    {

    }

    @Override
    public void onMouseExit()
    {

    }

    @Override
    public void onMouseOver(int mouseX, int mouseY)
    {

    }

    @Override
    public void onMouseDown(int mouseX, int mouseY, int button)
    {

    }

    @Override
    public void onMouseUp(int mouseX, int mouseY, int button)
    {

    }

    @Override
    public void onMouseMove(int mouseX, int mouseY)
    {

    }

    @Override
    public void onMouseWheel(int mouseX, int mouseY, int deltaWheel)
    {

    }

    @Nullable
    @Override
    public GuiControl hitTest(int x, int y)
    {
        for(GuiControl control : controls)
        {
            if(!control.isVisible() || !control.isEnabled())
                continue;

            int transX = x - control.getX();
            int transY = y - control.getY();

            GuiControl ret = control.hitTest(transX, transY);
            if(ret != null)
                return ret;
        }
        return null;
    }

    @Override
    public void sort()
    {
        this.controls.sort(Comparator.comparingInt(a -> a.zIndex));
    }

    @Override
    public boolean notifyTakeFocus(GuiControl taker)
    {
        //first, look for objections
        /*
        for(Gui control : controls)
        {
            if(control instanceof GuiTextField)
            {
                //hum, how to determine if a text field can lose focus?

                //if(((GuiTextField)control).get) {
                //    return false;
                //}
            }
        }*/

        //no objections, lady
        for(GuiControl control : controls)
        {
            if (control != taker && ((GuiControl)control).hasFocus())
            {
                ((GuiControl)control).setFocused(false);
            }
        }

        return true;
    }

    protected void openWebLink(URI url)
    {
        try {
            Class<?> oclass = Class.forName("java.awt.Desktop");
            Object object = oclass.getMethod("getDesktop").invoke((Object)null);
            oclass.getMethod("browse", URI.class).invoke(object, url);
        } catch (Throwable var4) {
            Throwable throwable = var4.getCause();
            Mod.logger.error("Couldn't open link: {}", throwable == null ? "<UNKNOWN>" : throwable.getMessage());
        }
    }

    @Override
    public Color getForeColor()
    {
        return foreColor;
    }

    @Override
    public void setForeColor(Color color)
    {
        this.foreColor = color;
    }
}
