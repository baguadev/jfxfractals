package com.gdi.jfxfractals.renderimpl.lorenz.math.vector;

public final class Vector3d {
    public double x, y, z;

    public Vector3d() {}

    public Vector3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3d copy() {
        return new Vector3d(this.x, this.y, this.z);
    }

    public interface Consumer {

        default void accept(Vector3d v) {
            accept(v.x, v.y, v.z);
        }

        void accept(double x, double y, double z);
    }
}
