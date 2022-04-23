package com.gdi.jfxfractals.renderimpl.lorenz.math.vector;

public final class Vector3f {
    public float x, y, z;

    public Vector3f() {}

    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3f copy() {
        return new Vector3f(this.x, this.y, this.z);
    }

    public interface Consumer {

        default void accept(Vector3f v) {
            accept(v.x, v.y, v.z);
        }

        void accept(float x, float y, float z);
    }
}

