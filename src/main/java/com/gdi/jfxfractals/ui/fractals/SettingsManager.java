package com.gdi.jfxfractals.ui.fractals;

import com.gdi.jfxfractals.renderer.IFractalRender;
import com.gdi.jfxfractals.renderimpl.MandelbrotShaderRenderer;
import com.gdi.jfxfractals.ui.fractals.lorenz.LorenzSettingsView;
import com.gdi.jfxfractals.ui.fractals.lorenz.LorenzSettingsViewModel;
import com.gdi.jfxfractals.ui.fractals.mandelbrot.MandelbrotSettingsView;
import com.gdi.jfxfractals.ui.fractals.mandelbrot.MandelbrotSettingsViewModel;
import de.saxsys.mvvmfx.Context;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.scene.Node;
import javafx.scene.Parent;

import java.util.HashMap;

public class SettingsManager {

    HashMap<Class, Node> settingMap;

    public SettingsManager()
    {

    }

    public Parent getSettingsContent(IFractalRender fractalRender, Context context)
    {
        Parent view;
        System.out.println("Processing renderer class:"+fractalRender.getClass().getName());
        switch (fractalRender.getClass().getName())
        {
            case "com.gdi.jfxfractals.renderimpl.MandelbrotShaderRenderer":
                ViewTuple<MandelbrotSettingsView, MandelbrotSettingsViewModel> load = FluentViewLoader
                        .fxmlView(MandelbrotSettingsView.class)
                        .context(context)
                        .load();

                view = load.getView();
                load.getViewModel().setFractalRenderer(fractalRender);
                break;
            case "com.gdi.jfxfractals.renderimpl.LorenzAttractorRenderer":
                ViewTuple<LorenzSettingsView, LorenzSettingsViewModel> lorenz = FluentViewLoader
                        .fxmlView(LorenzSettingsView.class)
                        .context(context)
                        .load();

                view = lorenz.getView();
                lorenz.getViewModel().setFractalRenderer(fractalRender);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + fractalRender.getClass().getName());
        }
        return view;
    }

}
