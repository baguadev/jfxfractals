package com.gdi.jfxfractals.renderimpl;

import com.gdi.jfxfractals.renderer.IFractalRender;

import static org.lwjgl.opengl.GL11.*;

public class MandelbrotRenderer2 extends  AbstractFractalRenderer implements IFractalRender {

    public static final int g = 10;
    public static final int f = 8;

    public static final float it = 100;
    public static final int limit = 300;

    public static float xoffset = .5f;
    public static float yoffset = .5f;

    static float gridSize = 0.0003f;

    static  int Width = 1920;
    static  int Height = 1080;

    static Col[][] pix;

    @Override
    public void render() {
        Width = width;
        Height = height;

        glEnable(GL_DEPTH_TEST);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        pix = new Col[Width][Height];
        generate();
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, Width, 0, Height, 1, -1);
        glMatrixMode(GL_MODELVIEW);

        for (int i = 0; i < Width; i++) {

            for (int j = 0; j < Height; j++) {

                glColor3f(pix[i][j].r,pix[i][j].g,pix[i][j].b);
                glBegin(GL_QUADS);

                glVertex2f(i, j);
                glVertex2f((i + 1), j);
                glVertex2f((i + 1), (j + 1));
                glVertex2f(i, (j +1));

                glEnd();

            }
        }
    }

    public static void generate() {

        setupPix();
        for (int i = 0; i < g; i++) {

            aplyFxaa();
        }


    }

    public static void fxaa (Col a,Col ... cols) {

        float b = 0;
        for (int i = 0; i < cols.length; i++) {

            b+= cols[i].r;
        }

        a.r = a.r + b/(g*f);
        a.g = a.g + b/(g*f);
        a.b = a.b + b/(g*f);
    }

    public static void aplyFxaa() {

        for (int i = 0; i < Width; i++) {

            for (int j = 0; j < Height; j++) {

                int mi = i-1;
                int mj = j-1;

                int li = (i+1) % Width;
                int lj = (j+1) % Height;

                if(mi < 0) mi= Width -1;
                if(mj < 0) mj = Height -1;

                fxaa(pix[i][j], pix[mi] [j],
                        pix[i] [mj],
                        pix[mi][mj],
                        pix[li] [j],
                        pix[i] [lj],
                        pix[li] [lj]);

            }
        }
    }
    private static void setupPix() {

        for (int i = 0; i < Width; i++) {

            for (int j = 0; j < Height; j++) {

                pix[i][j] = new Col();

                float x = (i - Width/2)*gridSize + xoffset;
                float y = (j - Height/2)*gridSize + yoffset;

                float cx = x;
                float cy = y;

                int n = 0;

                while(n < it) {

                    float xx = x*x - y*y;
                    float yy = 2 * x * y;

                    x = xx + cx;
                    y = yy + cy;
                    n++;

                    if(x + y > limit) {

                        break;
                    }

                }
                pix[i] [j].r =n/it;
                pix[i] [j].g =n/it;
                pix[i] [j].b =n/it;

            }
        }
    }

    static class Col{float r,g,b;}

}
