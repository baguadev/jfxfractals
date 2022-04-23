package com.gdi.jfxfractals.service;

import com.gdi.jfxfractals.renderer.IFractalRender;
import javafx.stage.Stage;
import org.eclipse.fx.drift.DriftFXSurface;

public class RendererContext {

    private IFractalRender fractalRender;

    private DriftFXSurface renderSurface;

    private Stage primaryStage;

    public IFractalRender getFractalRender() {
        return fractalRender;
    }

    public void setFractalRender(IFractalRender fractalRender) {
        this.fractalRender = fractalRender;
    }

    public DriftFXSurface getRenderSurface() {
        return renderSurface;
    }

    public void setRenderSurface(DriftFXSurface renderSurface) {
        this.renderSurface = renderSurface;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
