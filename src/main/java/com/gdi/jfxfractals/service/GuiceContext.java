package com.gdi.jfxfractals.service;

import com.google.inject.Injector;

public class GuiceContext implements Context{

    protected Injector injector;
    public GuiceContext( Injector injector) {

        this.injector = injector;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void injectMembers(Object obj) {
        injector.injectMembers(obj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T getInstance(Class<T> cls) {
        return injector.getInstance(cls);
    }

    public Injector getInjector() {
        return injector;
    }
}

