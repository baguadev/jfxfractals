package com.gdi.jfxfractals.model;

import com.gdi.jfxfractals.renderer.IFractalRender;
import com.gdi.jfxfractals.renderimpl.MandelbrotRenderer;

import java.util.function.Supplier;

public class FractalInfo<T extends IFractalRender> {
    private String name;
    private String description;
    private final Supplier<? extends T> ctor;
    private T renderer;

    public FractalInfo(String name, String description, Supplier<? extends T> ctor) {
        this.name = name;
        this.description = description;
        this.ctor = ctor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public T getRenderer() {
        if(renderer == null)
        {
            renderer = ctor.get();;
        }
        return renderer;
    }
}
