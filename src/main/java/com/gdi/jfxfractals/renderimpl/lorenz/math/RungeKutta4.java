package com.gdi.jfxfractals.renderimpl.lorenz.math;

import com.gdi.jfxfractals.common.task.IncrementalListener;
import com.gdi.jfxfractals.renderimpl.lorenz.math.vector.Vector3d;
import com.gdi.jfxfractals.renderimpl.lorenz.util.Updatable;

public final class RungeKutta4 implements Updatable {
    private final Vector3d v0;
    private final double h;
    private final Function function;
    private final Series series;
    private final IncrementalListener listener;
    private final int iterationsPerUpdate;

    private Vector3d current;

    public RungeKutta4(Vector3d v0, double h, Function function, Series series, IncrementalListener listener, int iterationsPerUpdate) {
        this.v0 = v0.copy();
        this.h = h;
        this.function = function;
        this.series = series;
        this.listener = listener;
        this.iterationsPerUpdate = iterationsPerUpdate;

        this.current = this.v0;
    }

    @Override
    public void update() {
        if (!this.listener.isCancelled())
            this.current = apply(this.current, this.h, this.function, this.series, this.listener, this.iterationsPerUpdate);
    }

    public static Vector3d apply(Vector3d v0, double h, Function function, Series series, IncrementalListener listener, int maxIterations) {
        double h2 = h / 2D, h6 = h / 6D;
        Vector3d v = v0.copy(), k1 = new Vector3d(), k2 = new Vector3d(), k3 = new Vector3d(), k4 = new Vector3d();

        int i = 0;
        while (!listener.isCancelled() && i < maxIterations) {
            function.apply(v, k1);
            function.apply(v.x + h2 * k1.x, v.y + h2 * k1.y, v.z + h2 * k1.z, k2);
            function.apply(v.x + h2 * k2.x, v.y + h2 * k2.y, v.z + h2 * k2.z, k3);
            function.apply(v.x + h * k3.x, v.y + h * k3.y, v.z + h * k3.z, k4);

            v.x += h6 * (k1.x + 2 * k2.x + 2 * k3.x + k4.x);
            v.y += h6 * (k1.y + 2 * k2.y + 2 * k3.y + k4.y);
            v.z += h6 * (k1.z + 2 * k2.z + 2 * k3.z + k4.z);

            listener.increment(1);
            series.add(v);
            i++;
        }

        return v;
    }
}
