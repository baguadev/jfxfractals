package com.gdi.jfxfractals.renderimpl.lorenz.math;

import com.gdi.jfxfractals.renderimpl.lorenz.math.vector.Vector3d;
import com.gdi.jfxfractals.utils.Buffers;
import javafx.scene.paint.Color;

import java.nio.FloatBuffer;

public final class Series {
    public static final Color DEFAULT_COLOR = Color.color(1, 1, 1, 0.8);

    public boolean connect = true;
    private Color color = DEFAULT_COLOR;

    private FloatBuffer buffer;
    private int size = 0;

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color == null ? Color.WHITE : color;
    }

    public void add(Vector3d v) {
        add((float) v.x, (float) v.y, (float) v.z);
    }

    public void add(float x, float y, float z) {
        ensureCapacity(this.size + 1, false);

        int pos = this.size * 3;
        this.buffer.put(pos, x);
        this.buffer.put(pos + 1, y);
        this.buffer.put(pos + 2, z);

        this.size++;
    }

    public void ensureCapacity(int capacity, boolean exact) {
        if (this.buffer == null) {
            if (capacity < 128)
                capacity = 128;
            this.buffer = Buffers.newDirectFloatBuffer(capacity * 3);
        } else {
            int prevCapacity = this.buffer.capacity() / 3;
            if (capacity <= prevCapacity)
                return;

            if (!exact) {
                if (capacity < prevCapacity * 2)
                    capacity = prevCapacity * 2;
            }

            FloatBuffer buf = Buffers.newDirectFloatBuffer(capacity * 3);
            buf.put(this.buffer);
            buf.rewind();
            this.buffer = buf;
        }
    }

    public void clear() {
        this.size = 0;
    }

    public int size() {
        return this.size;
    }

    public FloatBuffer getBuffer() {
        return this.buffer;
    }
}

