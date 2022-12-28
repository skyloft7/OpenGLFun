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


public class Main extends App {




    Mesh mesh;


    Shader shader;

    public void init() {





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

    @Override
    public void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
    }

    public static void main(String[] args) {

        Window win = new Window("OpenGL!", 640, 480);

        win.show(new Main());

    }

}