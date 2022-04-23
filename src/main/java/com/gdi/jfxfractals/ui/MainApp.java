package com.gdi.jfxfractals.ui;

import com.gdi.jfxfractals.AppLauncher;
import com.gdi.jfxfractals.renderer.RenderManager;
import com.gdi.jfxfractals.service.*;
import com.gdi.jfxfractals.ui.container.MainContainerView;
import com.gdi.jfxfractals.ui.container.MainContainerViewModel;
import com.gdi.jfxfractals.ui.fractals.SettingsManager;
import com.google.common.eventbus.EventBus;
import com.google.inject.*;
import com.google.inject.Module;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.svg.SVGGlyph;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.MvvmFX;
import de.saxsys.mvvmfx.ViewTuple;
import de.saxsys.mvvmfx.internal.MvvmfxApplication;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;

public class MainApp extends Application implements MvvmfxApplication {

    private static final Logger log = LoggerFactory.getLogger(MainApp.class);
    private Stage primaryStage;

    // EventBus using Guava
    private static EventBus eventBus = new EventBus("Key_Pressed_event_bus");

    public static EventBus getKeyEventBus() {
        return eventBus;
    }


    @Override
    public void initMvvmfx() throws Exception {

    }

    @Override
    public void startMvvmfx(Stage stage) throws Exception {
        log.debug("Starting application");
        Thread.currentThread().setUncaughtExceptionHandler((t, e) -> logError(e));
        try {
            log.debug("Loading resource bundle");
            ResourceBundle resourceBundle = ResourceBundle.getBundle("properties/resources");
            MvvmFX.setGlobalResourceBundle(resourceBundle);
            log.debug("Setting title");
            stage.setTitle(resourceBundle.getString("window.title"));
            log.debug("loading main container");
            //ContextManager.context.getInstance(Run.class).start();
            final ViewTuple<MainContainerView, MainContainerViewModel> main = FluentViewLoader.fxmlView(MainContainerView.class).load();
            log.debug("Creating custom decorator");
            JFXDecorator jfxDecorator = new JFXDecorator(stage, main.getView());
            jfxDecorator.setCustomMaximize(true);
            jfxDecorator.setGraphic(new SVGGlyph());
            log.debug("creating root scene");
            double width = 1700;
            double height = 900;
            Scene rootScene = new Scene(jfxDecorator, width, height);

            rootScene.getStylesheets().add("/css/material-ui.css");
            rootScene.getStylesheets().add("/css/main.css");
            HostServices hostServices = this.getHostServices();
            log.debug("Applying global styles");
            //    StyleManager.applyGlobalStylesheets(rootScene);

            log.debug("Setting scene and showing");
            stage.setScene(rootScene);
            stage.show();
            log.debug("Starting key listener");

            startKeyListener();
        } catch (final RuntimeException e) {
            e.printStackTrace();
            logError(e);
            Platform.exit();
        } catch (Exception ex) {
            log.error("Got exception:", ex);
            throw ex;
        }
    }

    public void stopMvvmfx() throws Exception {
        //AppLauncher.closeRenderLoop();
        ContextManager.context.getInstance(RenderManager.class).close();
        System.exit(1);
    }

    public final void init() throws Exception {
        List<Module> modules = new ArrayList<>();
        modules.add(new MvvmfxModule());
        modules.add(new AbstractModule() {
            @Override
            protected void configure() {
                bind(HostServices.class).toProvider(MainApp.this::getHostServices);
                bind(Stage.class).toProvider(() -> primaryStage);
                bind(Parameters.class).toProvider(MainApp.this::getParameters);
                bind(RendererContext.class).in(Singleton.class);
                bind(RenderManager.class).in(Singleton.class);
                bind(SettingsManager.class).in(Singleton.class);
            }
        });

        this.initGuiceModules(modules);

        final Injector injector = Guice.createInjector(modules);
        MvvmFX.setCustomDependencyInjector(injector::getInstance);

        log.debug("Injecting members in current app");
        injector.injectMembers(this);
        ContextManager.context = new GuiceContext(injector);
        AppLauncher.startRenderLoop();
        this.initMvvmfx();
        log.debug("Initialising guice modules");
    }

    public final void start(Stage stage) throws Exception {
        this.primaryStage = stage;
        this.startMvvmfx(stage);
    }

    @Override
    public final void stop() throws Exception {
        stopMvvmfx();
        log.debug("Stopping application");
    }

    public void initGuiceModules(List<Module> modules) throws Exception {
    }

    private static void startKeyListener() {
        // Get the logger for "org.jnativehook" and set the level to off.
        java.util.logging.Logger logger = java.util.logging.Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            log.error("The key listener could not be initialized.");
            log.error(ex.getMessage());

            System.exit(1);
        }

        GlobalScreen.addNativeKeyListener(new KeyListener());
    }

    public static void main(final String[] args) {
        launch(args);
    }

    public static void logError(final Throwable e) {
        log.error("Application error", e);
    }
}
