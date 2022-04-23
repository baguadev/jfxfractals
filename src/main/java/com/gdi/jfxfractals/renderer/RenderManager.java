package com.gdi.jfxfractals.renderer;

import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.eclipse.fx.drift.Renderer;
import org.eclipse.fx.drift.StandardTransferTypes;
import org.eclipse.fx.drift.TransferType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RenderManager {

    private RenderLoop currentLoop;
    private SceneRendererThread rendererThread;
    private static final Logger log = LoggerFactory.getLogger(RenderManager.class);

    public RenderLoop getCurrentLoop() {
        return currentLoop;
    }

    public SceneRendererThread getRendererThread() { return rendererThread;}
    public void setCurrentLoop(RenderLoop currentLoop) {
        this.currentLoop = currentLoop;
    }

    public void applyRenderer(IFractalRender fractalRender)
    {
        close();
        //fractalRender.setDriftFxRendered(driftFxRenderer);
        fractalRender.setType(StandardTransferTypes.NVDXInterop);
        rendererThread = new SceneRendererThread(fractalRender);
        //currentLoop = new RenderLoop(render);
        //currentLoop.start();
        rendererThread.start();

    }

    public void close() {
       /* if(currentLoop != null)
        {
            currentLoop.close();
            try {
                currentLoop.join(3000);
            } catch (InterruptedException e) {
                log.error("Got interrupted",e);
            }
        }
        */
        if(rendererThread != null)
        {
            rendererThread.stop();
        }
    }
}
