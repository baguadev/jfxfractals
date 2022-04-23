package com.gdi.jfxfractals.service;

import com.gdi.jfxfractals.ui.MainApp;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

public class KeyListener implements NativeKeyListener {

    public KeyListener() { }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) { }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
        if (nativeKeyEvent.getKeyCode()==NativeKeyEvent.VC_ALT) {
            MainApp.getKeyEventBus().post("AltKeyPressed");
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) { }
}
