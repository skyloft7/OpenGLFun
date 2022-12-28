package org.example;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class Texture {
    private String path;

    private int myTex;

    public Texture(String path) {
        IntBuffer width = BufferUtils.createIntBuffer(1);

        IntBuffer height = BufferUtils.createIntBuffer(1);

        IntBuffer channelsInFile = BufferUtils.createIntBuffer(1);

        STBImage.stbi_set_flip_vertically_on_load(true);
        ByteBuffer texture = STBImage.stbi_load(path, width, height, channelsInFile, 4);

        myTex = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, myTex);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, 340, 148, 0, GL_RGBA, GL_UNSIGNED_BYTE, texture);

        glGenerateMipmap(GL_TEXTURE_2D);


        STBImage.stbi_image_free(texture);

    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, myTex);

    }

}
