package com.gdi.jfxfractals.renderimpl.lorenz.math;

import com.gdi.jfxfractals.renderimpl.lorenz.math.vector.Vector3d;

public interface Function {
    double DEFAULT_SIGMA = 10, DEFAULT_RHO = 28, DEFAULT_BETA = 8 / 3D;

    default void apply(Vector3d input, Vector3d output) {
        apply(input.x, input.y, input.z, output);
    }

    void apply(double x, double y, double z, Vector3d output);

    static Function lorenz() {
        return lorenz(DEFAULT_SIGMA, DEFAULT_RHO, DEFAULT_BETA);
    }

    static Function lorenz(double sigma, double rho, double beta) {
        return (x, y, z, output) -> {
            output.x = sigma * (y - x);
            output.y = rho * x - y - x * z;
            output.z = x * y - beta * z;
        };
    }
}

