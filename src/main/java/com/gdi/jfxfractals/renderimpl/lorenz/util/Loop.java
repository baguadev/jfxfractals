package com.gdi.jfxfractals.renderimpl.lorenz.util;

import com.gdi.jfxfractals.common.app.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public final class Loop {

    private static final Logger log = LoggerFactory.getLogger(Loop.class);
    private static int tasks = 0;

    public final List<Updatable> updatables = new ArrayList<>();
    private int nanoPeriod;
    private Task task;

    public Loop() {
        State.SHUTDOWN.newListener(e -> stop(), 0).register();
    }

    public void stop() {
        if (this.task == null)
            return;

        this.task.run = false;
        this.task = null;
    }

    public void setPrefFrequency(double f) {
        if (f <= 0)
            throw new IllegalArgumentException();
        this.nanoPeriod = (int) (1E9 / f);
    }

    public void setPrefPeriod(int nano) {
        if (nano < 0)
            throw new IllegalArgumentException();
        this.nanoPeriod = nano;
    }

    public double getPrefFrequency() {
        return 1E9 / (double) getPrefPeriod();
    }

    public int getPrefPeriod() {
        return this.nanoPeriod;
    }

    public double getCurrentFrequency() {
        return 1E9 / (double) getCurrentPeriod();
    }

    public int getCurrentPeriod() {
        if (this.task == null)
            return 0;
        int d = this.task.nanoDelta;
        return d < this.nanoPeriod ? this.nanoPeriod : d;
    }

    public void start() {
        if (this.task != null)
            return;

        this.task = new Task();
        this.task.setName("Loop-" + tasks++);
        this.task.start();
    }

    public boolean isStarted() {
        return this.task != null;
    }

    private class Task extends Thread {
        private boolean run = true;
        private int nanoDelta = 0;

        @Override
        public void run() {
            long t = System.nanoTime();

            for (Updatable u : Loop.this.updatables) {
                try {
                    u.init();
                } catch (Exception e) {
                    log.error("Updatable " + u + " failed to init", e);
                }
            }

            while (this.run) {
                for (Updatable u : Loop.this.updatables) {
                    try {
                        u.update();
                    } catch (Exception e) {
                        log.error("Updatable " + u + " failed to update", e);
                    }
                }

                long t2 = System.nanoTime();
                this.nanoDelta = (int) (t2 - t);
                int remaining = Loop.this.nanoPeriod - this.nanoDelta;
                if (remaining > 0) {
                    try {
                        Thread.sleep(remaining / 1_000_000, remaining % 1_000_000);
                    } catch (InterruptedException e) {
                        log.error("Exception occured:", e);
                    }
                }
                t = t2;
            }

            for (Updatable u : Loop.this.updatables) {
                try {
                    u.dispose();
                } catch (Exception e) {
                    log.error("Updatable " + u + " failed to dispose", e);
                }
            }
        }
    }
}
