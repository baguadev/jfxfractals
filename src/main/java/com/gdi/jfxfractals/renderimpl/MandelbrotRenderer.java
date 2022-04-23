package com.gdi.jfxfractals.renderimpl;

import com.gdi.jfxfractals.renderer.IFractalRender;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;

public class MandelbrotRenderer extends  AbstractFractalRenderer implements IFractalRender{

    public final double arcThickness;
    public final double arcThicknessHalfFraction = 0.35;
    private static final double INNER_RADIUS = 0.2;
    private static final double MAX_THICKNESS = 0.75;

    public MandelbrotRenderer()
    {
        arcThickness = MAX_THICKNESS/3;
    }

    @Override
    public void render() {
        GL11.glLineWidth(2);
        GL11.glDepthMask(false);
        GL11.glColor4f(0, 0, 0, 1);
        for (int i = 0; i < 3; i++) {
            int year = i;
            GL11.glBegin(GL11.GL_LINE_STRIP);
            for (double a = 0; a <= 360; a += 2) {
                double ang = this.getGuiAngle(a);
                double r = this.getArcCenterlineRadiusAt(i, a) - arcThickness * arcThicknessHalfFraction;
                double x = r * Math.cos(ang);
                double y = r * Math.sin(ang);
                GL11.glVertex2d(x, y);
            }
            GL11.glEnd();

        }
    }

    private double getGuiAngle(double a) {
        return Math.toRadians(-a + 90);
    }

    public double getArcCenterlineRadiusAt(int i, double a) {
        double r1 = INNER_RADIUS+i*arcThickness;
        double r2 = r1+arcThickness;
        return (r1+(r2-r1)*(a/360D));
    }
}
