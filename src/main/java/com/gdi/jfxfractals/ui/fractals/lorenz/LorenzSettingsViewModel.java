package com.gdi.jfxfractals.ui.fractals.lorenz;

import com.gdi.jfxfractals.renderer.IFractalRender;
import com.gdi.jfxfractals.renderimpl.LorenzAttractorRenderer;
import com.gdi.jfxfractals.ui.fractals.FractalSettingsViewModel;
import com.gdi.jfxfractals.ui.fractals.lorenz.controls.LorenzConfig;
import com.gdi.jfxfractals.ui.scope.FractalSettingsScope;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;

public class LorenzSettingsViewModel implements ViewModel, FractalSettingsViewModel {

    @InjectScope
    private FractalSettingsScope fractalSettingsScope;

    private LorenzAttractorRenderer fractalRenderer;

    public void initialize() {

    }


    @Override
    public void setFractalRenderer(IFractalRender fractalRender) {
        fractalRenderer = (LorenzAttractorRenderer) fractalRender;
    }

    public void removeConfig(LorenzConfig cfg) {
        fractalRenderer.removeConfig(cfg);
    }

    public void addConfig(LorenzConfig cfg) {
        fractalRenderer.addConfig(cfg);
    }
}
