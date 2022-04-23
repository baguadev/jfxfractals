package com.gdi.jfxfractals.ui.mainmenu;

import com.gdi.jfxfractals.ui.MainApp;
import com.gdi.jfxfractals.ui.scope.MenuScope;
import com.google.common.eventbus.Subscribe;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MenuViewModel implements ViewModel {
    private static final Logger log = LoggerFactory.getLogger(MenuViewModel.class);

    public static final String SHOW_HIDE_MENU = "ShowHide_Navigation_Menu";

    @InjectScope
    MenuScope menuScope;

    public void initialize() {

        log.debug("init menu viewmodel");
        MainApp.getKeyEventBus().register(this);
    }

    public void notifyShowHideSidePane() {
        menuScope.publish(MenuScope.SIDE_PANE);
    }

    @Subscribe
    public void altKeyPressedEvent(String event) {
        if (event.equals("AltKeyPressed")) {
            log.debug("Received event Alt key pressed");
            publish(SHOW_HIDE_MENU);
        }
    }
}
