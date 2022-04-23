package com.gdi.jfxfractals.renderimpl.lorenz.util;

public class FloatUtil {
    public static final float E = 2.7182818284590452354f;

    /** The value PI, i.e. 180 degrees in radians. */
    public static final float PI = 3.14159265358979323846f;

    /** The value 2PI, i.e. 360 degrees in radians. */
    public static final float TWO_PI = 2f * PI;

    /** The value PI/2, i.e. 90 degrees in radians. */
    public static final float HALF_PI = PI / 2f;

    /** The value PI/4, i.e. 45 degrees in radians. */
    public static final float QUARTER_PI = PI / 4f;

    /** The value PI^2. */
    public final static float SQUARED_PI = PI * PI;

    public static final float EPSILON = 1.1920929E-7f; // Float.MIN_VALUE == 1.4e-45f ; double EPSILON 2.220446049250313E-16d

    /**
     * Inversion Epsilon, used with equals method to determine if two inverted matrices are close enough to be considered equal.
     * <p>
     * Using {@value}, which is ~100 times {@link FloatUtil#EPSILON}.
     * </p>
     */
    public static final float INV_DEVIANCE = 1.0E-5f; // FloatUtil.EPSILON == 1.1920929E-7f; double ALLOWED_DEVIANCE: 1.0E-8f

}
