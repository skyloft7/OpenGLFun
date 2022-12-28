package org.example;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL43.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;


public class Main {

    // The window handle
    private long window;

    public void run() {

        initAndShowWindow();
        loop();




        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void initAndShowWindow() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create the window
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

        window = glfwCreateWindow(640, 480, "OpenGL Fun!", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        });

        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);



        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
    }




    Mesh mesh;


    Shader shader;

    private void init() {

        glDebugMessageCallback((source, type, id, severity, length, message, userParam) -> {
            String messageStr = GLDebugMessageCallback.getMessage(length, message);

            System.out.println(messageStr);
        }, 0);






        MeshBatch meshBatch = new MeshBatch();
        meshBatch.bind();

        mesh = new Mesh(
                new float[]{
                        // positions          // colors           // texture coords
                        0.5f,  0.5f, 0.0f,   1.0f, 1.0f,   // top right
                        0.5f, -0.5f, 0.0f,   1.0f, 0.0f,   // bottom right
                        -0.5f, -0.5f, 0.0f,   0.0f, 0.0f,   // bottom left
                        -0.5f,  0.5f, 0.0f,   0.0f, 1.0f    // top left
                },

                new int[]{  // note that we start from 0!
                        0, 1, 3,   // first triangle
                        1, 2, 3    // second triangle
                }
        );

        meshBatch.addMesh(mesh);
        meshBatch.build();


        Texture tex = new Texture("texture.png");
        tex.bind();

        shader = new Shader(
                FileReader.readFile("vertexshader.glsl"),
                FileReader.readFile("fragmentshader.glsl")
        );




        shader.compile();
        shader.bind();




    }

    private void loop() {

        GL.createCapabilities();

        init();

        glClearColor(0.2f, 0.3f, 0.3f, 1.0f);

        float dark = 0.5f;

        boolean phaseUp = true;

        while ( !glfwWindowShouldClose(window) ) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer




            if(phaseUp){

                dark *= 1.05f;

                if(dark > 2){
                    phaseUp = false;
                }
            }

            if(!phaseUp){
                dark /= 1.05f;

                if(dark < 2){
                    phaseUp = true;
                }
            }



            shader.setFloat("dark", dark);



            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);



            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    public static void main(String[] args) {
        new Main().run();
    }

}