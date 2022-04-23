package com.gdi.jfxfractals.renderimpl;

import com.gdi.jfxfractals.renderer.IFractalRender;
import com.gdi.jfxfractals.renderer.Shader;
import com.gdi.jfxfractals.renderer.Vertex;
import com.gdi.jfxfractals.service.ContextManager;
import com.gdi.jfxfractals.service.RendererContext;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import org.joml.Vector2d;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL40;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.Callable;

import static com.gdi.jfxfractals.io.IOUtil.ioResourceToByteBuffer;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.opengl.ARBShaderObjects.*;
import static org.lwjgl.opengl.ARBVertexShader.GL_VERTEX_SHADER_ARB;
import static org.lwjgl.opengl.GL11.*;


public class MandelbrotShaderRenderer extends  AbstractFractalRenderer implements IFractalRender {

    private Vector2d cameraTranslation = new Vector2d();
    private double basePanningSpeed = 0.0125f;
    private double currentPanningSpeed = 0.0125f;
    private double cameraZoom = 1.0f;
    private double zoomSpeed = 1.05f;

    private final int PIXEL_WIDTH = 480 * 2;
    private final int PIXEL_HEIGHT = 480 * 2;
    private final int WIDTH = 4;
    private final int HEIGHT = 4;
    private final String TITLE = "Mandelbrot Viewer";
    private final Vector4f BACKGROUND_COLOUR = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);

    private int screenshotCount = 1;
    private boolean moveRight = false;
    private boolean moveLeft = false;
    private boolean moveUp = false;
    private boolean moveDown = false;
    private volatile boolean zoomIn = false;
    private volatile boolean zoomOut = false;

    private final int[] winWidth = new int[1];
    private final int[] winHeight = new int[1];
    private final int[] fbWidth = new int[1];
    private final int[] fbHeight = new int[1];

    private final double[] mousePosX = new double[1];
    private final double[] mousePosY = new double[1];
    private Shader shaderHandler;
    private int vaoID;
    float[] color = new float[]{0.0f};
    float[] maxIter = new float[]{100.0f};
    double time = 0;
    int iboID;
    // IBO (Index Buffer Object)
    int[] indices = {
            0, 1, 2,
            2, 3, 0
    };

    private float   mouseDownX;
    private float   mouseX;
    private boolean mouseDown;
    private long firstTime;
    private int  frameNumber;
    private int bounceCount = 1;
    Callable<Void> listenerCallback;
    int program;
    private int uCameraZoom;
    private int uCameraPos;
    private int uColor;
    private int uMaxIter;

    @Override
    public void initialize() {
        super.initialize();

        // Vendor
        System.out.println("OpenGL Info:");
        System.out.println("  Vendor: " + glGetString(GL_VENDOR));
        System.out.println("  Renderer: " + glGetString(GL_RENDERER));
        System.out.println("  Version: " + glGetString(GL_VERSION));

        // Background Colour
        glClearColor(BACKGROUND_COLOUR.x, BACKGROUND_COLOUR.y, BACKGROUND_COLOUR.z, BACKGROUND_COLOUR.w);

        // Vertices
        Vertex v0 = new Vertex(); v0.setXYZ(-2f, 2f, 0f);
        Vertex v1 = new Vertex(); v1.setXYZ(-2f, -2f, 0f);
        Vertex v2 = new Vertex(); v2.setXYZ( 2f,-2f, 0f);
        Vertex v3 = new Vertex(); v3.setXYZ( 2f, 2f, 0f);
        Vertex[] vertices = new Vertex[] {v0, v1, v2, v3};

        // VBO (Vertex Buffer Object)
        FloatBuffer vboBuffer = BufferUtils.createFloatBuffer(vertices.length * Vertex.positionElementCount);
        for(int vertex = 0; vertex < vertices.length; vertex++) {
            vboBuffer.put(vertices[vertex].getXYZW());
        }
        vboBuffer.flip();


        IntBuffer iboBuffer = BufferUtils.createIntBuffer(indices.length);
        iboBuffer.put(indices);
        iboBuffer.flip();

        // VAO (Vertex Array Object)
        vaoID = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoID);

        // Buffer Bind and Data TODO: Change GL_STATIC_DRAW
        int vboID = GL30.glGenBuffers();
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vboID);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, vboBuffer, GL30.GL_STATIC_DRAW);
        GL30.glVertexAttribPointer(0, Vertex.positionElementCount, Vertex.type, false, Vertex.positionSize, 0);
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);

        GL30.glBindVertexArray(0);

        iboID = GL30.glGenBuffers();
        GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, iboID);
        GL30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, iboBuffer, GL30.GL_STATIC_DRAW);
        GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, 0);

        // Shaders
        /*shaderHandler = new Shader();
        shaderHandler.addShader("/shaders/vert.shader", GL30.GL_VERTEX_SHADER);
        shaderHandler.addShader("/shaders/frag.shader", GL30.GL_FRAGMENT_SHADER);
        shaderHandler.bindShaderAttribute(0, "in_Position");
        shaderHandler.validateProgram();
        */
        try {
            program = createProgram();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Camera (Both axis from -2 to 2)
        float zoomAmount = 1.05f;
    }

    @Override
    public void installListeners()
    {
        System.out.println("Attaching event listeners");
        EventHandler<? super MouseEvent> onMouseMoved = event -> {
            double x = event.getX();
            boolean mouseDown = event.isPrimaryButtonDown();
            MandelbrotShaderRenderer.this.mouseX = (float)x;
            if ( mouseDown ) {
                MandelbrotShaderRenderer.this.frameNumber = 0;
            }
        };
        EventHandler<? super ScrollEvent> onScroll = event -> {
            if (event.getDeltaY() < 0) {
                fov *= 1.05f;
                zoomOut = true;
                zoomIn = false;
                cameraZoom = cameraZoom * (1 / zoomSpeed); // Update the zoomAmount
                currentPanningSpeed = basePanningSpeed / cameraZoom; // Update camera panning speed
            } else {
                fov *= 1f / 1.05f;
                zoomOut = false;
                zoomIn = true;
                cameraZoom = cameraZoom * zoomSpeed; // Update the zoomAmount
                currentPanningSpeed = basePanningSpeed / cameraZoom;
            }
        };

        EventHandler<? super KeyEvent> onKeyPress = event-> {

            System.out.println("Processing key event:"+event.getEventType());
            if (event.getCode() == KeyCode.D && event.getEventType() == KeyEvent.KEY_PRESSED)
            {
                moveRight = true;
                cameraTranslation.add(currentPanningSpeed, 0.0f);
            }
            if (event.getCode() == KeyCode.D && event.getEventType() == KeyEvent.KEY_RELEASED) {
                moveRight = false;
            }

            if (event.getCode() == KeyCode.A && event.getEventType() == KeyEvent.KEY_PRESSED)
            {
                moveLeft = true;
                cameraTranslation.add(-currentPanningSpeed, 0.0f);
            }
            if (event.getCode() == KeyCode.A && event.getEventType() == KeyEvent.KEY_RELEASED) {
                moveLeft = false;
            }
            if (event.getCode() == KeyCode.W && event.getEventType() == KeyEvent.KEY_PRESSED)
            {
                moveUp = true;
                cameraTranslation.add(0.0f, currentPanningSpeed);
            }
            if (event.getCode() == KeyCode.W && event.getEventType() == KeyEvent.KEY_RELEASED) {
                moveUp = false;
            }
            if (event.getCode() == KeyCode.S && event.getEventType() == KeyEvent.KEY_PRESSED)
            {
                moveDown = true;
                cameraTranslation.add(0.0f, -currentPanningSpeed);
            }
            if (event.getCode() == KeyCode.S && event.getEventType() == KeyEvent.KEY_RELEASED) {
                moveDown = false;
            }
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


    static int createShader(String resource, int type) throws IOException {
        int shader = glCreateShaderObjectARB(type);
        ByteBuffer source = ioResourceToByteBuffer(resource, 1024);
        PointerBuffer strings = BufferUtils.createPointerBuffer(1);
        IntBuffer lengths = BufferUtils.createIntBuffer(1);
        strings.put(0, source);
        lengths.put(0, source.remaining());
        glShaderSourceARB(shader, strings, lengths);
        glCompileShaderARB(shader);
        int compiled = glGetObjectParameteriARB(shader, GL_OBJECT_COMPILE_STATUS_ARB);
        String shaderLog = glGetInfoLogARB(shader);
        if (shaderLog.trim().length() > 0) {
            System.err.println(shaderLog);
        }
        if (compiled == 0) {
            throw new AssertionError("Could not compile shader");
        }
        return shader;
    }

    int createProgram() throws IOException {

        int program = glCreateProgramObjectARB();
        int vertexShader = createShader("/shaders/vert.shader",
                GL_VERTEX_SHADER_ARB);
        int fragmentShader = createShader("/shaders/frag.shader",
                ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);
        glAttachObjectARB(program, vertexShader);
        glAttachObjectARB(program, fragmentShader);
        glLinkProgramARB(program);
        int linkStatus = glGetObjectParameteriARB(program, GL_OBJECT_LINK_STATUS_ARB);
        String programLog = glGetInfoLogARB(program);
        if (programLog.trim().length() > 0) {
            System.err.println(programLog);
        }
        if (linkStatus == 0) {
            throw new AssertionError("Could not link program");
        }

        glUseProgramObjectARB(program);
        GL20.glBindAttribLocation(program,0,"in_Position");

        uCameraZoom = glGetUniformLocationARB(program, "u_CameraZoom");
        uCameraPos = glGetUniformLocationARB(program, "u_CameraPos");
        uColor = glGetUniformLocationARB(program, "u_Color");
        uMaxIter = glGetUniformLocationARB(program, "u_maxIter");
        return program;
    }

    public void setMaxIter(float maxIter)
    {
        this.maxIter[0] = maxIter;
    }

    @Override
    public void dispose() {
        super.dispose();
        System.out.println("Disposing renderer:");
        if(listenerCallback != null) {
            try {
                System.out.println("Disposing listeners");
                listenerCallback.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("Disposing shaders");
        if(shaderHandler !=null) {
            shaderHandler.unBindProgram();
        }
    }

    @Override
    public void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
        //glEnable(GL_DEPTH_TEST);
        // Camera Movement
            /*if(moveRight) {
                cameraTranslation.add(currentPanningSpeed, 0.0f);
            }
            if(moveLeft) {
                cameraTranslation.add(-currentPanningSpeed, 0.0f);
            }
            if(moveUp) {
                cameraTranslation.add(0.0f, currentPanningSpeed);
            }
            if(moveDown) {
                cameraTranslation.add(0.0f, -currentPanningSpeed);
            }
            if(zoomIn) {
                cameraZoom = cameraZoom * zoomSpeed; // Update the zoomAmount
                currentPanningSpeed = basePanningSpeed / cameraZoom; // Update camera panning speed
            }
            if(zoomOut) {
                cameraZoom = cameraZoom * (1 / zoomSpeed); // Update the zoomAmount
                currentPanningSpeed = basePanningSpeed / cameraZoom; // Update camera panning speed
            }
            */
        // Bind Shader
            /*shaderHandler.bindProgram();
            shaderHandler.setUniVec1d("u_CameraZoom", 1 / cameraZoom);

            try (MemoryStack stack = MemoryStack.stackPush()) {
                DoubleBuffer cameraBuffer = cameraTranslation.get(stack.mallocDouble(2));
                shaderHandler.setUniVec2d("u_CameraPos", cameraBuffer);
            }

            shaderHandler.setUniVec1f("u_Color", color);
            shaderHandler.setUniVec1f("u_maxIter", maxIter);
            */

        glUseProgramObjectARB(program);
        GL40.glUniform1dv(uCameraZoom, new double[]{1 / cameraZoom});

        try (MemoryStack stack = MemoryStack.stackPush()) {
            DoubleBuffer cameraBuffer = cameraTranslation.get(stack.mallocDouble(2));
            //shaderHandler.setUniVec2d(uCameraPos, cameraBuffer);
            GL40.glUniform2dv(uCameraPos, cameraBuffer);

        }

        GL40.glUniform1fv(uColor, color);
        GL40.glUniform1fv(uMaxIter, maxIter);
        // Bind VAO
        GL30.glBindVertexArray(vaoID);
        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);
        GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, iboID);

        // Draw the vertices
        GL30.glDrawElements(GL30.GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);

        // Unbind VAO
        GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, 0);
        GL30.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);

        // GUI
        final double currentTime = glfwGetTime();
        final double deltaTime = (time > 0) ? (currentTime - time) : 1f / 60f;
        time = currentTime;
        //glfwSwapInterval(1);

    }
}
