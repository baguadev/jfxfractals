package com.gdi.jfxfractals.renderer;

import org.eclipse.fx.drift.DriftFXSurface;
import org.eclipse.fx.drift.Renderer;
import org.eclipse.fx.drift.TransferType;

public interface IFractalRender {
    void initialize();
    void dispose();

    void update();
    void beforeFrame() throws Exception;
    void afterFrame();
    void render();

    void setType(TransferType mode);

    void setDriftFxRendered(Renderer driftFxRenderer);

    void setDriftFxSurface(DriftFXSurface driftFXSurface);

}
