package com.gdi.jfxfractals.service;

import com.google.inject.Injector;

public interface Context {

    /**
     * Injects members into given instance
     *
     * @param instance instance to inject members into
     */
    void injectMembers(Object instance);

    /**
     * Create instance of given class
     *
     * @param cls
     * @param <T>
     * @return resulting instance
     */
    <T> T getInstance(Class<T> cls);

    /**
     * Context initialization
     */
    default void init() {
        // no-op
    }

    /**
     * Context disposal
     */
    default void dispose() {
        // no-op
    }

    Injector getInjector();

}

