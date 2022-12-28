package org.example;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL45.*;
import static org.lwjgl.opengl.GL45.GL_STATIC_DRAW;

public class Mesh {
    private float[] vertices;
    private int[] indices;

    private int myVbo;
    public int myEbo;

    public Mesh(float[] vertices, int[] indices) {
        this.vertices = vertices;
        this.indices = indices;

        build();
    }

    private void build() {



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


    }

}
