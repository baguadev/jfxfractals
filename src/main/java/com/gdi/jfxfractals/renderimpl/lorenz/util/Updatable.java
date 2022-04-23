package com.gdi.jfxfractals.renderimpl.lorenz.util;

public interface Updatable {
    default void init() {}

    void update();

    default void dispose() {}
}
