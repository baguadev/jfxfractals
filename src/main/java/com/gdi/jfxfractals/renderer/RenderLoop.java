package com.gdi.jfxfractals.renderer;

import com.gdi.jfxfractals.renderer.utils.GLFunctions;
import com.gdi.jfxfractals.renderer.utils.MovingAverage;
import com.gdi.jfxfractals.service.ContextManager;
import com.gdi.jfxfractals.service.RendererContext;
import org.eclipse.fx.drift.*;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GLCapabilities;

import java.nio.ByteBuffer;

public class RenderLoop extends Thread {

    public static boolean enableFPS = false;
    public static boolean sendToDFX = true;

    private DriftFXSurface surface;
    private Swapchain chain;
    private Renderer hook;

    private int width;
    private int height;


    private TransferType transfer = StandardTransferTypes.NVDXInterop;
    private long contextID = -1;
    private GLCapabilities glCaps;

    private boolean shouldClose = false;
    private final MovingAverage FPS = new MovingAverage(30);

    private Framebuffer msaaBuffer;
    private Framebuffer intermediate;
   // public static final int MAX_FPS = 60;
   // public static final int MILLIS_PER_FRAME = 1000/MAX_FPS;
    public static final String PROGRAM_TITLE = "Fractals Explorer";
    private IFractalRender currentRenderer;

    //This value would probably be stored elsewhere.
    final double GAME_HERTZ = 30.0;
    //Calculate how many ns each frame should take for our target game hertz.
    final double TIME_BETWEEN_UPDATES = 1000000000 / GAME_HERTZ;
    //At the very most we will update the game this many times before a new render.
    //If you're worried about visual hitches more than perfect timing, set this to 1.
    final int MAX_UPDATES_BEFORE_RENDER = 5;
    //We will need the last update time.
    double lastUpdateTime = System.nanoTime();
    //Store the last time we rendered.
    double lastRenderTime = System.nanoTime();

    //If we are able to get as high as this FPS, don't render again.
    final double TARGET_FPS = 60;
    final double TARGET_TIME_BETWEEN_RENDERS = 1000000000 / TARGET_FPS;

    //Simple way of finding FPS.
    int lastSecondTime = (int) (lastUpdateTime / 1000000000);

    private int fps = 60;
    private int frameCount = 0;

    public RenderLoop(IFractalRender render) {
        currentRenderer = render;
    }

    private void load() {

        if (this.loadFrom(ContextManager.context.getInstance(RendererContext.class).getRenderSurface())) {

            GLFW.glfwInit();
            GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GL11.GL_FALSE);
            GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
            GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
            GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_COMPAT_PROFILE);

            contextID = GLFW.glfwCreateWindow(800, 800, PROGRAM_TITLE, 0, 0);
            if (contextID == 0) {
                throw new RuntimeException("Failed to create window");
            }
            GLFW.glfwHideWindow(contextID);
            GLFW.glfwMakeContextCurrent(contextID);
            glCaps = GL.createCapabilities();
            currentRenderer.initialize();
           // StatusHandler.postStatus("Renderer initialized.", 10000);
        }
        else {
            //not feasible StatusHandler.postStatus("Renderer waiting for DFX surface...", 10000);
        }
    }

    private boolean loadFrom(DriftFXSurface surf) {
        if (surf == null)
            return false;
        surface = surf;
        hook = GLRenderer.getRenderer(surface);
        return true;
    }

    @Override
    public void run() {
        while (!shouldClose) {
            try {
                double now = System.nanoTime();
                int updateCount = 0;
                while( now - lastUpdateTime > TIME_BETWEEN_UPDATES && updateCount < MAX_UPDATES_BEFORE_RENDER )
                {
                   // updateGame();
                    lastUpdateTime += TIME_BETWEEN_UPDATES;
                    updateCount++;
                }

                //If for some reason an update takes forever, we don't want to do an insane number of catchups.
                //If you were doing some sort of game that needed to keep EXACT time, you would get rid of this.
                if ( now - lastUpdateTime > TIME_BETWEEN_UPDATES)
                {
                    lastUpdateTime = now - TIME_BETWEEN_UPDATES;
                }

                //Render. To do so, we need to calculate interpolation for a smooth render.
                float interpolation = Math.min(1.0f, (float) ((now - lastUpdateTime) / TIME_BETWEEN_UPDATES) );
                this.renderLoop();
                lastRenderTime = now;

                //Update the frames we got.
                int thisSecond = (int) (lastUpdateTime / 1000000000);
                if (thisSecond > lastSecondTime)
                {
                    //System.out.println("NEW SECOND " + thisSecond + " " + frameCount);
                    FPS.addValue(frameCount);
                    fps = frameCount;
                    frameCount = 0;
                    lastSecondTime = thisSecond;
                }

                //Yield until it has been at least the target time between renders. This saves the CPU from hogging.
                while ( now - lastRenderTime < TARGET_TIME_BETWEEN_RENDERS && now - lastUpdateTime < TIME_BETWEEN_UPDATES)
                {
                    Thread.yield();
                     try {Thread.sleep(1);} catch(Exception e) {}

                    now = System.nanoTime();
                }



                //long pre = System.currentTimeMillis();

                //long post = System.currentTimeMillis();

                /*
                if (enableFPS) {
                    long dur = Math.max(1, post - pre);
                    FPS.addValue(1000 / dur);
                }
                long sleep = MILLIS_PER_FRAME - (post - pre);
                if (sleep > 0) {
                    Thread.sleep(sleep);
                }
                */

            }
            catch (InterruptedException e) {
                e.printStackTrace();
                shouldClose = true;
            }
        }
        if (chain != null) {
            chain.dispose();
        }
        currentRenderer.dispose();
        chain = null;
        GLFW.glfwDestroyWindow(contextID);
        GLFW.glfwTerminate();
    }

    private void renderLoop() throws InterruptedException {
        if (surface == null) {
            this.load();
            return;
        }
        Vec2i size = hook.getSize();

        if (size.x == 0 || size.y == 0) {
            shouldClose = true;
            throw new RuntimeException("Render box is size zero!");
        }

        if (chain == null || size.x != width || size.y != height) {
            System.out.println("Recreating swapchain");
            if (chain != null) {
                chain.dispose();
            }

            chain = hook.createSwapchain(new SwapchainConfig(size, 2, PresentationMode.MAILBOX, transfer));

            width = size.x;
            height = size.y;

            msaaBuffer = null;
            intermediate = null;
            //StatusHandler.postStatus("DriftFX interface loaded", 750, false);
        }
        GLFunctions.printGLErrors("Main loop");
        if (contextID <= 0)
            return;

        if (msaaBuffer == null)
            msaaBuffer = new Framebuffer(width, height, true);

        if (intermediate == null)
            intermediate = new Framebuffer(width, height).setClear(1, 1, 1);

        GLFunctions.printGLErrors("Pre-render");
        msaaBuffer.bind(false);
        GLFunctions.printGLErrors("Framebuffer bind");
        this.render(width, height);
        GLFunctions.printGLErrors("Draw");
        msaaBuffer.unbind();
        GLFunctions.printGLErrors("Framebuffer unbind");
        msaaBuffer.sendTo(intermediate);
        GLFunctions.printGLErrors("Framebuffer copy to intermediate");

        GL11.glFlush();

        if (sendToDFX) {
            RenderTarget target = chain.acquire();

            int tex = GLRenderer.getGLTextureId(target);
            int depthTex = GL11.glGenTextures();
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTex);
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL32.GL_DEPTH_COMPONENT32F, width, height, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (ByteBuffer)null);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

            int fb = GL32.glGenFramebuffers();

            GL32.glBindFramebuffer(GL32.GL_FRAMEBUFFER, fb);
            GL32.glFramebufferTexture(GL32.GL_FRAMEBUFFER, GL32.GL_COLOR_ATTACHMENT0, tex, 0);
            GL32.glFramebufferTexture(GL32.GL_FRAMEBUFFER, GL32.GL_DEPTH_ATTACHMENT, depthTex, 0);

            int status = GL32.glCheckFramebufferStatus(GL32.GL_FRAMEBUFFER);
            switch (status) {
                case GL32.GL_FRAMEBUFFER_COMPLETE:
                    break;
                case GL32.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:
                    System.err.println("INCOMPLETE_ATTACHMENT!");
                    break;
            }

            GLFunctions.printGLErrors("DFX Framebuffer bind");
            //intermediate.sendTo(fb);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            intermediate.draw();
            GLFunctions.printGLErrors("Framebuffer render to DFX");
            //this.render(width, height);

            GL32.glBindFramebuffer(GL32.GL_FRAMEBUFFER, 0);
            GL32.glDeleteFramebuffers(fb);
            GL11.glDeleteTextures(depthTex);
            GLFunctions.printGLErrors("DFX Framebuffer unbind");

            chain.present(target);
        }
    }

    private void render(int x, int y) throws InterruptedException {
        GL11.glClearColor(1, 1, 1, 1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glViewport(0, 0, x, y);

        if(currentRenderer != null) {
           // currentRenderer.draw(x, y);
        }
    }

    public void close() {
        shouldClose = true;
    }

    public long getFPS() {
        if (!enableFPS) {
            throw new IllegalStateException("FPS is not enabled!");
        }
        return Math.round(FPS.getAverage());
    }
}
