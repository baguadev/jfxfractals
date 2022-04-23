package com.gdi.jfxfractals;

import com.gdi.jfxfractals.renderer.RenderLoop;
import com.gdi.jfxfractals.ui.MainApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppLauncher {

    private static final Logger log = LoggerFactory.getLogger(AppLauncher.class);

    public static void main(final String[] args) {
        log.debug("Starting application");
        MainApp.main(args);
    }
    public static void startRenderLoop()
    {
        //renderer = new RenderLoop(render);
        //renderer.start();
    }

    public static void closeRenderLoop()
    {
//        renderer.close();
    }

}