package com.mike_caron.mikesmodslib.gui;

import com.mike_caron.mikesmodslib.Mod;
import net.minecraft.util.ResourceLocation;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Stack;

public class GuiGuide
    extends GuiBase
    implements GuiButton.ClickedListener,
               GuiGuidePage.NavigationListener
{
    private GuiButton backButton = GuiUtil.translatedButton(1, 7, 147, 55, 14, "gui.mikesmodslib:guide.back");
    private GuiButton closeButton = GuiUtil.translatedButton(2, 194, 147, 55, 14, "gui.mikesmodslib:guide.close");
    private GuiButton indexButton = GuiUtil.translatedButton(3, 104, 147, 55, 14, "gui.mikesmodslib:guide.index");

    private GuiGuidePage guidePage = new GuiGuidePage(7, 18, 242, 126);

    private final Stack<String> pageNav = new Stack<>();
    private final Stack<Integer> scrolls = new Stack<>();

    private String currentPage = "/index";
    public GuiGuide(int width, int height, ResourceLocation background)
    {
        super(width, height, background);

        this.background = background;

        initControls();

        loadPage();
    }

    @Override
    protected String getTitleKey()
    {
        return "gui.mikesmodslib:guide.title";
    }

    @Override
    protected void addControls()
    {
        super.addControls();

        this.addControl(backButton);
        this.addControl(guidePage);
        this.addControl(closeButton);
        this.addControl(indexButton);
        backButton.setEnabled(false);
        backButton.addListener(this);
        closeButton.addListener(this);
        guidePage.addListener(this);
        indexButton.addListener(this);

    }


    @Override
    public void clicked(GuiButton.ClickedEvent event)
    {
        if(event.id == 1)
        {
            currentPage = pageNav.pop();
            backButton.setEnabled(!pageNav.isEmpty());

            loadPage();

            guidePage.setScrollY(scrolls.pop());
        }
        else if(event.id == 2)
        {
            this.mc.player.closeScreen();
        }
        else if(event.id == 3)
        {
            navigated("/index");
        }
    }

    private void loadPage()
    {
        guidePage.loadPage(currentPage);
    }

    @Override
    public void navigated(String newUri)
    {
        pageNav.push(currentPage);
        scrolls.push(guidePage.getScrollY());
        currentPage = newUri;
        backButton.setEnabled(true);
        loadPage();
    }

    @Override
    public void navigatedExternal(String uri)
    {
        try
        {
            URI url = new URI(uri);

            openWebLink(url);
        }
        catch(URISyntaxException ex)
        {
            Mod.logger.error("Unable to parse Uri", ex);
        }


    }
}
