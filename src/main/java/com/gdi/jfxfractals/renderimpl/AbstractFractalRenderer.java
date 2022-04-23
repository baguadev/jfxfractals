package com.gdi.jfxfractals.renderimpl;

import com.gdi.jfxfractals.renderer.IFractalRender;
import org.eclipse.fx.drift.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.Callback;
import org.lwjgl.system.Configuration;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.glFramebufferTexture;

public abstract class AbstractFractalRenderer implements IFractalRender {

    protected Renderer renderer;
    protected Swapchain swapChain;
    protected RenderTarget target;

    private FloatBuffer normalMatrixBuffer = BufferUtils.createFloatBuffer(3 * 3);
    private FloatBuffer lightPositionBuffer = BufferUtils.createFloatBuffer(3);
    private FloatBuffer viewPositionBuffer = BufferUtils.createFloatBuffer(3);

    GLCapabilities caps;

    long ctx;

    private TransferType txType;
    private TransferType curTxType;

    int fb;
    int depthTex;

    int width = 800;
    int height = 800;

    float fov = 60;
    float rotation;

    Callback debugProc;
    DriftFXSurface driftFxSurface;

    @Override
    public void initialize() {
        Configuration.DEBUG.set(true);
        Configuration.DEBUG_FUNCTIONS.set(false);
        Configuration.DEBUG_STREAM.set(System.err);
        debugProc = GLUtil.setupDebugMessageCallback();
        fb = glGenFramebuffers();
        System.err.println("fb = " + fb);
        depthTex = glGenTextures();
    }

    @Override
    public void setDriftFxRendered(Renderer driftFxRenderer) {
        this.renderer = driftFxRenderer;
    }

    @Override
    public void setDriftFxSurface(DriftFXSurface driftFXSurface) {
        this.driftFxSurface = driftFXSurface;
        this.renderer = GLRenderer.getRenderer(driftFXSurface);
        installListeners();
    }

    public void installListeners()
    {

    }

    @Override
    public void setType(TransferType mode) {
        txType = StandardTransferTypes.MainMemory;
    }

    @Override
    public void update() {

    }

    @Override
    public void dispose() {
        glDeleteFramebuffers(fb);
        glDeleteTextures(depthTex);

        if (swapChain != null) {
            swapChain.dispose();
            swapChain = null;
        }

        debugProc.free();
    }

    @Override
    public void beforeFrame() throws Exception {
        if (target != null) {
            throw new IllegalStateException();
        }
        Vec2i size = renderer.getSize();

        if (swapChain == null || size.x != width || size.y != height || curTxType != txType) {
            System.err.println("(re)create swapchain");
            if (swapChain != null) {
                swapChain.dispose();
            }
            try {
                swapChain = renderer.createSwapchain(new SwapchainConfig(size, 2, PresentationMode.MAILBOX, txType));

                width = size.x;
                height = size.y;

                // update depth tex
                glBindTexture(GL_TEXTURE_2D, depthTex);
                glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT32F, width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, (ByteBuffer)null);
                glBindTexture(GL_TEXTURE_2D, 0);

                curTxType = txType;
            } catch (Exception e) {
                System.err.println("swapchain recreation failed! " + e.getMessage());
                e.printStackTrace(System.err);
            }
        }

        if (swapChain != null) {
            target = swapChain.acquire();

            int tex = GLRenderer.getGLTextureId(target);

            glBindFramebuffer(GL_FRAMEBUFFER, fb);
            glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, tex, 0);
            glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, depthTex, 0);

            int status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
            switch (status) {
                case GL_FRAMEBUFFER_COMPLETE: break;
                case GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT: System.err.println("INCOMPLETE_ATTACHMENT!"); break;
            }

            glViewport(0, 0, width, height);

            /* int tex = GLRenderer.getGLTextureId(target);
            depthTex = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, depthTex);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT32F, width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, (ByteBuffer)null);
            glBindTexture(GL_TEXTURE_2D, 0);

            fb = glGenFramebuffers();


            glBindFramebuffer(GL_FRAMEBUFFER, fb);
            glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, tex, 0);
            glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, depthTex, 0);

            int status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
            switch (status) {
                case GL_FRAMEBUFFER_COMPLETE: break;
                case GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT: System.err.println("INCOMPLETE_ATTACHMENT!"); break;
            }

            glViewport(0, 0, width, height);
             */
        }

    }

    @Override
    public void afterFrame() {

        if (target == null) {
            throw new IllegalStateException();
        }
        if (swapChain != null) {
            glBindFramebuffer(GL_FRAMEBUFFER, 0);
            swapChain.present(target);

            target = null;
        }

    }
}
