package com.gdi.jfxfractals.ui.fractals.lorenz.controls;

import com.gdi.jfxfractals.common.fx.task.ObservableProgressListener;
import com.gdi.jfxfractals.common.task.IncrementalListener;
import com.gdi.jfxfractals.renderimpl.lorenz.math.Function;
import com.gdi.jfxfractals.renderimpl.lorenz.math.RungeKutta4;
import com.gdi.jfxfractals.renderimpl.lorenz.math.Series;
import com.gdi.jfxfractals.renderimpl.lorenz.math.vector.Vector3d;
import com.gdi.jfxfractals.renderimpl.lorenz.util.Updatable;

public class LorenzConfig implements Updatable {
    public double sigma = Function.DEFAULT_SIGMA, rho = Function.DEFAULT_RHO, beta = Function.DEFAULT_BETA, x0 = 1, y0 = 1, z0 = 1, h = 0.001;
    public int points = 100000, speed = 100;

    public final ObservableProgressListener progressListener = new ObservableProgressListener();
    public final Series series = new Series();

    private IncrementalListener listener;
    private Updatable solver;

    public void start() {
        stop();

        this.progressListener.setCancelled(false);
        this.listener = this.progressListener.limit(this.points);
        this.series.ensureCapacity(this.points, true);
        this.solver = new RungeKutta4(new Vector3d(this.x0, this.y0, this.z0), this.h, Function.lorenz(this.sigma, this.rho, this.beta), this.series, this.listener, this.speed);
        this.solver.init();
    }

    public void stop() {
        if (this.listener != null) {
            this.listener.cancel();
            this.listener = null;
        }
        if (this.solver != null) {
            this.solver.dispose();
            this.solver = null;
        }
        this.series.clear();
    }

    @Override
    public void update() {
        if (this.solver != null)
            this.solver.update();
    }

    @Override
    public void dispose() {
        stop();
    }
}
