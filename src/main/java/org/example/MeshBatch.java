package org.example;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class MeshBatch {
    private int myVao;

    private List<Mesh> myMeshes = new ArrayList<>();


    public MeshBatch() {
        myVao = glGenVertexArrays();
    }

    public void bind(){
        glBindVertexArray(myVao);
    }

    public void addMesh(Mesh mesh){
        myMeshes.add(mesh);
    }

    public void removeMesh(Mesh mesh){
        myMeshes.remove(mesh);
    }

    public void build() {
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 20,  0);
        glEnableVertexAttribArray(0);

        //20 and 12
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 20, 12);
        glEnableVertexAttribArray(1);
    }
}
