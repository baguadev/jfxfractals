package com.gdi.jfxfractals.renderer;

import org.eclipse.fx.drift.Renderer;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;

public class SceneRendererThread {

    private IFractalRender fractalRender;

    private boolean alive = false;

    private Thread thread;

    public SceneRendererThread(IFractalRender fractalRender) {
        this.fractalRender = fractalRender;
    }

    public void start() {
        if (!alive) {
            alive = true;
            thread = new Thread(this::run);
            thread.setDaemon(true);
            thread.start();
        }
    }

    public void stop() {
        if (alive) {
            alive = false;
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            thread = null;
        }
    }

    GLCapabilities caps;

    long ctx;

    private void run() {
        try {

            ctx = org.eclipse.fx.drift.internal.GL.createSharedCompatContext(0);
            //ctx = org.eclipse.fx.drift.internal.GL.createContext(0, 3, 0);
            org.eclipse.fx.drift.internal.GL.makeContextCurrent(ctx);

            System.err.println("Context is " + ctx);


            caps = GL.createCapabilities();

            System.err.println("CAPS: " + caps.OpenGL32);
            System.err.println("ARB_shader_objects = " + caps.GL_ARB_shader_objects
                    + ", ARB_vertex_shader = " + caps.GL_ARB_vertex_shader
                    + ", ARB_fragment_shader = " + caps.GL_ARB_fragment_shader);


            fractalRender.initialize();


            while (alive) {
                fractalRender.update();
                fractalRender.beforeFrame();
                fractalRender.render();
                fractalRender.afterFrame();
            }


            fractalRender.dispose();
            org.eclipse.fx.drift.internal.GL.destroyContext(ctx);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
