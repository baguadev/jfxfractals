open module jfxfractals {
    requires java.base;
    requires transitive javafx.base;
    requires transitive javafx.graphics;
    requires transitive javafx.controls;
    requires transitive javafx.swing;
    requires transitive javafx.media;
    requires transitive javafx.web;
    requires org.slf4j;
    requires de.saxsys.mvvmfx;
    requires com.google.guice;
    requires javafx.fxml;
    requires com.jfoenix;
    requires org.eclipse.fx.drift;
    requires org.lwjgl.opengl;
    requires org.lwjgl.glfw;
    requires org.joml;
    requires jnativehook;
    requires com.google.common;
    requires java.logging;
    requires org.apache.commons.lang3;
    requires org.burningwave.core;



    /*opens com.gdi.jfxfractals.ui;
    opens com.gdi.jfxfractals.ui.container;
    opens com.gdi.jfxfractals.renderer;
    opens com.gdi.jfxfractals.renderimpl;
    opens com.gdi.jfxfractals.service;
    opens com.gdi.jfxfractals;
    opens com.gdi.jfxfractals.common.app;
    opens com.gdi.jfxfractals.ui.scope;
    */

    exports com.gdi.jfxfractals.ui;
}