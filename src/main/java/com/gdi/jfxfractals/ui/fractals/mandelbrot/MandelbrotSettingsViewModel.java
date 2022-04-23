package com.gdi.jfxfractals.ui.fractals.mandelbrot;

import com.gdi.jfxfractals.renderer.IFractalRender;
import com.gdi.jfxfractals.renderimpl.MandelbrotShaderRenderer;
import com.gdi.jfxfractals.ui.fractals.FractalSettingsViewModel;
import com.gdi.jfxfractals.ui.maincontent.MainContentViewModel;
import com.gdi.jfxfractals.ui.scope.FractalSettingsScope;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MandelbrotSettingsViewModel implements ViewModel, FractalSettingsViewModel {

    private static final Logger log = LoggerFactory.getLogger(MandelbrotSettingsViewModel.class);

    @InjectScope
    private FractalSettingsScope fractalSettingsScope;

    private MandelbrotShaderRenderer fractalRenderer;

    public void initialize() {

    }

    @Override
    public void setFractalRenderer(IFractalRender fractalRender) {
        fractalRenderer = (MandelbrotShaderRenderer) fractalRender;
    }

    public void setMaxIter(Number newValue) {
        fractalRenderer.setMaxIter(newValue.floatValue());
    }
}
