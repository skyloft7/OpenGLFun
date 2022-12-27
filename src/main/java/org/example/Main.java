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

    int myVao;
    int myVbo;
    int myEbo;

    int myTex;
    private void init() {

        glDebugMessageCallback((source, type, id, severity, length, message, userParam) -> {
            String messageStr = GLDebugMessageCallback.getMessage(length, message);

            System.out.println(messageStr);
        }, 0);

        String vertShader = "#version 330 core\n" +
                "layout (location = 0) in vec3 aPos;\n" +
                "layout (location = 1) in vec2 aTexCoord;\n" +
                "\n" +
                "out vec2 TexCoord;\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "    gl_Position = vec4(aPos, 1.0);\n" +
                "    TexCoord = aTexCoord;\n" +
                "}";

        String fragShader = "#version 330 core\n" +
                "out vec4 FragColor;\n" +
                "  \n" +
                "in vec2 TexCoord;\n" +
                "\n" +
                "uniform sampler2D ourTexture;\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "    FragColor = texture(ourTexture, TexCoord);\n" +
                "}";




        float vertices[] = {
                // positions          // colors           // texture coords
                0.5f,  0.5f, 0.0f,   1.0f, 1.0f,   // top right
                0.5f, -0.5f, 0.0f,   1.0f, 0.0f,   // bottom right
                -0.5f, -0.5f, 0.0f,   0.0f, 0.0f,   // bottom left
                -0.5f,  0.5f, 0.0f,   0.0f, 1.0f    // top left
        };

        int indices[] = {  // note that we start from 0!
                0, 1, 3,   // first triangle
                1, 2, 3    // second triangle
        };


        myVao = glGenVertexArrays();
        glBindVertexArray(myVao);

        myVbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, myVbo);



        FloatBuffer verticesAsFloatBuffer = BufferUtils.createFloatBuffer(vertices.length);
        verticesAsFloatBuffer.put(vertices);

        verticesAsFloatBuffer.flip();

        glBufferData(GL_ARRAY_BUFFER, verticesAsFloatBuffer, GL_STATIC_DRAW);


        myEbo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, myEbo);

        IntBuffer indicesAsIntBuffer = BufferUtils.createIntBuffer(indices.length);
        indicesAsIntBuffer.put(indices);
        indicesAsIntBuffer.flip();

        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesAsIntBuffer, GL_STATIC_DRAW);


        IntBuffer width = BufferUtils.createIntBuffer(1);

        IntBuffer height = BufferUtils.createIntBuffer(1);

        IntBuffer channelsInFile = BufferUtils.createIntBuffer(1);

        STBImage.stbi_set_flip_vertically_on_load(true);
        ByteBuffer texture = STBImage.stbi_load("texture.png", width, height, channelsInFile, 4);

        myTex = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, myTex);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, 340, 148, 0, GL_RGBA, GL_UNSIGNED_BYTE, texture);
        glGenerateMipmap(GL_TEXTURE_2D);

        STBImage.stbi_image_free(texture);






        int vertexShaderProg = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShaderProg, vertShader);
        glCompileShader(vertexShaderProg);

        System.out.println(glGetShaderInfoLog(vertexShaderProg));


        int fragmentShaderProg = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShaderProg, fragShader);
        glCompileShader(fragmentShaderProg);

        System.out.println(glGetShaderInfoLog(fragmentShaderProg));


        int shaderProgram = glCreateProgram();



        glAttachShader(shaderProgram, vertexShaderProg);
        glAttachShader(shaderProgram, fragmentShaderProg);


        glDeleteShader(vertexShaderProg);
        glDeleteShader(fragmentShaderProg);

        glLinkProgram(shaderProgram);

        glUseProgram(shaderProgram);


        glVertexAttribPointer(0, 3, GL_FLOAT, false, 20,  0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 2, GL_FLOAT, false, 20, 12);
        glEnableVertexAttribArray(1);




    }

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        init();

        // Set the clear color
        glClearColor(0.2f, 0.3f, 0.3f, 1.0f);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !glfwWindowShouldClose(window) ) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer




            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, myEbo);
            glBindTexture(GL_TEXTURE_2D, myTex);
            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);


            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }
    }

    public static void main(String[] args) {
        new Main().run();
    }

}