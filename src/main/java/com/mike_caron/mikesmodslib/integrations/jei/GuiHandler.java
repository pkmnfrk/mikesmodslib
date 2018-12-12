package com.mike_caron.mikesmodslib.integrations.jei;

import com.mike_caron.mikesmodslib.gui.GuiContainerBase;
import com.mike_caron.mikesmodslib.gui.GuiControl;
import com.mike_caron.mikesmodslib.gui.GuiFluid;
import mezz.jei.api.gui.IAdvancedGuiHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GuiHandler<T extends GuiContainerBase>
    implements IAdvancedGuiHandler<T>
{
    private final Class<T> actualGui;

    public GuiHandler(Class<T> clz)
    {
        this.actualGui = clz;
    }

    @Override
    @Nonnull
    public Class<T> getGuiContainerClass()
    {
        return actualGui;
    }

    @Nullable
    @Override
    public Object getIngredientUnderMouse(GuiContainerBase guiContainer, int mouseX, int mouseY)
    {
        GuiControl control = guiContainer.hitTest(mouseX - guiContainer.getGuiLeft(), mouseY - guiContainer.getGuiTop());

        if(control instanceof GuiFluid)
        {
            return ((GuiFluid) control).getFluidStack();
        }

        return null;
    }
}
