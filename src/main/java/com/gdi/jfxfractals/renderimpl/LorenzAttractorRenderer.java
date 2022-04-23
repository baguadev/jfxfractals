package com.gdi.jfxfractals.renderimpl;

import com.gdi.jfxfractals.renderer.IFractalRender;
import com.gdi.jfxfractals.renderimpl.lorenz.Camera;
import com.gdi.jfxfractals.renderimpl.lorenz.ScatterChart;
import com.gdi.jfxfractals.renderimpl.lorenz.util.Updatable;
import com.gdi.jfxfractals.service.ContextManager;
import com.gdi.jfxfractals.service.RendererContext;
import com.gdi.jfxfractals.ui.fractals.lorenz.controls.LorenzConfig;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL21;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static org.lwjgl.opengl.GL11.*;

public class LorenzAttractorRenderer extends  AbstractFractalRenderer implements IFractalRender {

    private static int tasks = 0;

    public final List<Updatable> updatables = new ArrayList<>();
    private int nanoPeriod;
    private static final Logger log = LoggerFactory.getLogger(LorenzAttractorRenderer.class);

    private Camera camera = new Camera();
    private ScatterChart chart = new ScatterChart();
    Callable<Void> listenerCallback;
    private float   mouseX;
    private int frameNumber;
    private volatile boolean zoomIn = false;
    private volatile boolean zoomOut = false;
    float z = 0;
    double lastX,lastY;

    public void removeConfig(LorenzConfig config)
    {
        updatables.remove(config);
        chart.data.remove(config.series);

    }

    public void addConfig(LorenzConfig config)
    {
        chart.data.add(config.series);
        updatables.add(config);
    }


    @Override
    public void initialize() {
        super.initialize();
        long t = System.nanoTime();
        GL11.glShadeModel(GL_SMOOTH);
        GL11.glClearColor(0, 0, 0, 1);
        GL11.glClearDepth(1);

        //GL11.glEnable(GL_DEPTH_TEST);
       // GL21.glDepthFunc(GL_LEQUAL);
       // GL21.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);

        GL11.glEnable(GL_LINE_SMOOTH);
        GL11.glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);

        GL11.glEnable(GL_POINT_SMOOTH);
        GL11.glHint(GL_POINT_SMOOTH_HINT, GL_NICEST);

        GL11.glEnable(GL_BLEND);
        GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        for (Updatable u : updatables) {
            try {
                u.init();
            } catch (Exception e) {
                log.error("Updatable " + u + " failed to init", e);
            }
        }


        reshapeCamera();
    }

    @Override
    public void installListeners()
    {
        System.out.println("Attaching event listeners");
        EventHandler<? super MouseEvent> onMouseMoved = event -> {
            double x = event.getX();
            boolean mouseDown = event.isPrimaryButtonDown();
            this.mouseX = (float)x;
            if ( mouseDown ) {
                this.frameNumber = 0;
                double lastX = this.lastX, lastY = this.lastY;
                this.lastX = event.getX();
                this.lastY = event.getY();

                if (lastX == -1 || lastY == -1) {
                    return;
                }

                double dx = event.getX() - lastX, dy = lastY - event.getY();
                if (dx * dx + dy * dy > 1000) {
                    return;
                }

                this.camera.pitch((float) dy * -0.003f * getZoomFactor());
                this.camera.yaw((float) dx * 0.003f * getZoomFactor());
            }
        };
        EventHandler<? super ScrollEvent> onScroll = event -> {
            if (event.getDeltaY() < 0) {
                fov *= 1.05f;
                zoomOut = true;
                zoomIn = false;
                z++;
            } else {
                fov *= 1f / 1.05f;
                zoomOut = false;
                zoomIn = true;
                z--;
            }
            if(z!= 0) {
                this.camera.zoom(z);
            }
        };

        EventHandler<? super KeyEvent> onKeyPress = event-> {

            System.out.println("Processing key event:"+event.getEventType());

        };

        driftFxSurface.addEventHandler(MouseEvent.MOUSE_DRAGGED, onMouseMoved);
        driftFxSurface.addEventHandler(ScrollEvent.SCROLL, onScroll);
        ContextManager.context.getInstance(RendererContext.class).getPrimaryStage().addEventHandler(KeyEvent.ANY, onKeyPress);

        listenerCallback = () -> {
            driftFxSurface.removeEventHandler(MouseEvent.MOUSE_DRAGGED, onMouseMoved);
            driftFxSurface.removeEventHandler(ScrollEvent.SCROLL, onScroll);
            ContextManager.context.getInstance(RendererContext.class).getPrimaryStage().removeEventHandler(KeyEvent.ANY, onKeyPress);
            return null;
        };
    }


    @Override
    public void render() {

        for (Updatable u : updatables) {
            try {
                u.update();
            } catch (Exception e) {
                log.error("Updatable " + u + " failed to update", e);
            }
        }

        GL21.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // 3D Setup
        GL21.glMatrixMode(GL_PROJECTION);
        GL21.glLoadIdentity();
        this.camera.load3D();

        // 3D Render
        GL21.glMatrixMode(GL_MODELVIEW);
        GL21.glLoadIdentity();
        this.chart.render();


        // 2D Setup
        GL21.glMatrixMode(GL_PROJECTION);
        GL21.glLoadIdentity();
        this.camera.load2D();

        // 2D Render
        GL21.glMatrixMode(GL_MODELVIEW);
        GL21.glLoadIdentity();

    }

    void reshapeCamera()
    {
        this.camera.setWidth(width);
        this.camera.setHeight(height);
    }

    @Override
    public void dispose() {
        super.dispose();
        updatables.clear();
        this.chart.data.clear();
        if(listenerCallback != null) {
            try {
                System.out.println("Disposing listeners");
                listenerCallback.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private float getZoomFactor() {
        return this.camera.getFOV() / Camera.DEFAULT_FOV;
    }

}
