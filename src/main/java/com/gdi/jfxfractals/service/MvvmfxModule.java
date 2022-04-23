package com.gdi.jfxfractals.service;

import com.google.inject.AbstractModule;
import de.saxsys.mvvmfx.MvvmFX;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;

public class MvvmfxModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(NotificationCenter.class).toProvider(MvvmFX::getNotificationCenter);
    }
}

