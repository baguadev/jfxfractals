package com.gdi.jfxfractals.renderimpl.lorenz;

import com.gdi.jfxfractals.renderimpl.lorenz.math.Series;
import javafx.scene.paint.Color;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import static org.lwjgl.opengl.GL11.*;
import java.nio.FloatBuffer;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class ScatterChart {
    public final List<Series> data = new CopyOnWriteArrayList<>();

    private static final int TEX_COORD_SIZE = 2;
    private static final int VECTOR3_SIZE = 3;
    private static final int STRIDE = 24;
    private static final int NORMAL_OFFSET = VECTOR3_SIZE * 4;

    public void render() {
        GL11.glLineWidth(2);
        GL11.glBegin(GL_LINES);

        GL11.glColor3f(1, 0, 0);
        GL11.glVertex3f(0, 0, 0);
        GL11.glVertex3f(1, 0, 0);

        GL11.glColor4f(1, 0, 0, 0.4f);
        GL11.glVertex3f(1, 0, 0);
        GL11.glVertex3f(10, 0, 0);

        GL11.glColor3f(0, 1, 0);
        GL11.glVertex3f(0, 0, 0);
        GL11.glVertex3f(0, 1, 0);

        GL11.glColor4f(0, 1, 0, 0.4f);
        GL11.glVertex3f(0, 1, 0);
        GL11.glVertex3f(0, 10, 0);

        GL11.glColor3f(0, 0, 1);
        GL11.glVertex3f(0, 0, 0);
        GL11.glVertex3f(0, 0, 1);

        GL11.glColor4f(0, 0, 1, 0.4f);
        GL11.glVertex3f(0, 0, 1);
        GL11.glVertex3f(0, 0, 10);

        GL11.glEnd();

        GL11.glLineWidth(1);

        for (Series s : this.data) {
            int size = s.size();
            if (size == 0) {
                continue;
            }

            FloatBuffer buffer = s.getBuffer();

            Color c = s.getColor();
            GL11.glColor4f((float) c.getRed(), (float) c.getGreen(), (float) c.getBlue(), (float) c.getOpacity());
            GL11.glPointSize(1);

            GL11.glEnableClientState(GL_VERTEX_ARRAY);

            GL11.glVertexPointer(3, GL_FLOAT, 0, buffer);

            GL11.glDrawArrays(s.connect ? GL_LINE_STRIP : GL_POINTS, 0, size);
            GL11.glDisableClientState(GL_VERTEX_ARRAY);

            GL11.glPointSize(10);
            GL11.glBegin(GL_POINTS);
            int pos = (size - 1) * 3;
            GL11.glVertex3f(buffer.get(pos), buffer.get(pos + 1), buffer.get(pos + 2));
            GL11.glEnd();
        }

    }

}
