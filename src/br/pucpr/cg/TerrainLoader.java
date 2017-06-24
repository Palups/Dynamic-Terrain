package br.pucpr.cg;

import br.pucpr.mage.*;
import br.pucpr.mage.phong.DirectionalLight;
import br.pucpr.mage.phong.Material;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.IOException;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
/**
 * Created by Palups on 21/06/2017.
 */
/*W e S zoom da camera */
public class TerrainLoader implements Scene{
    private Keyboard keys = Keyboard.getInstance();

    private static final String PATH = "C:/img/opengl/textures/";

    //Dados da cena
    private Camera camera = new Camera();
    private DirectionalLight light = new DirectionalLight(
            new Vector3f(1.0f, -3.0f, -1.0f), //direction
            new Vector3f(0.02f, 0.02f, 0.02f),  //ambiente
            new Vector3f(1.0f, 1.0f, 1.0f), //diffuse
            new Vector3f(1.0f, 1.0f, 1.0f)  //specular
    );

    //Dados da malha
    private Mesh mesh;
    private Material material;

    private float angleX = 0.0f;
    private float angleY = 0.5f;

    private PerlinNoise pnoise;
    private int width, height;

    private float heightScale; //altera a altura do terreno

    private boolean wireFrame;

    @Override
    public void init() {
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glPolygonMode(GL_FRONT_FACE, GL_LINE);
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        heightScale = 1.0f;
        width = 1000;
        height = 1000;
        wireFrame = false;
        pnoise = new PerlinNoise(width, height, 0);

        try {
            mesh = MeshFactory.loadTerrain(pnoise.GetNoise(), 200.0f);
        } catch (IOException e){
            e.printStackTrace();
            System.exit(1);
        }

        camera.getPosition().y = 700.0f;
        camera.getPosition().z = 500.0f;

        material = new Material(
                new Vector3f(5.0f, 5.0f, 5.0f), //ambient
                new Vector3f(1.0f, 1.0f, 1.0f), //diffuse
                new Vector3f(0.1f, 0.1f,0.1f), //specular
                1000.0f //specular power
        );
        if(!wireFrame)
            material.setTexture("uTexture", new Texture(PATH + "snow.png"));
    }

    @Override
    public void update(float secs) {

        if (keys.isPressed(GLFW_KEY_ESCAPE)) {
            glfwSetWindowShouldClose(glfwGetCurrentContext(), GLFW_TRUE);
            return;
        }

        if(keys.isDown(GLFW_KEY_A))
        {
            angleY += Math.toRadians(45)*secs;
        }
        if(keys.isDown(GLFW_KEY_D))
        {
            angleY -= Math.toRadians(45)*secs;
        }

        if (keys.isDown(GLFW_KEY_W)) {
            camera.moveFront(100.0f * secs);
        }

        if (keys.isDown(GLFW_KEY_S)) {
            camera.moveFront(-100.0f * secs);
        }

        if (keys.isDown(GLFW_KEY_C)) {
            camera.strafeRight(100.0f * secs);
        }

        if (keys.isDown(GLFW_KEY_Z)) {
            camera.strafeLeft(100.0f * secs);
        }

        if (keys.isDown(GLFW_KEY_LEFT)) {
            camera.rotate((float) Math.toRadians(45) * secs);
        }

        if (keys.isDown(GLFW_KEY_RIGHT)) {
            camera.rotate(-(float) Math.toRadians(45) * secs);
        }

        if (keys.isDown(GLFW_KEY_UP)) {
            camera.rotateX((float) Math.toRadians(45) * secs);
        }

        if (keys.isDown(GLFW_KEY_DOWN)) {
            camera.rotateX(-(float) Math.toRadians(45) * secs);
        }

        //Muda a escala do terreno
        if (keys.isDown(GLFW_KEY_R)) {
            heightScale += 0.01;
        }

        if (keys.isDown(GLFW_KEY_T)) {
            if (heightScale - 0.1f >= 0)
                heightScale -= 0.01f;
        }

        //Wireframe
        if(keys.isPressed(GLFW_KEY_SPACE))
        {
            wireFrame = !wireFrame;
        }

    }

    @Override
    public void draw() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        if (wireFrame)
            glPolygonMode(GL_FRONT_AND_BACK,GL_LINE);
        else
            glPolygonMode(GL_FRONT_AND_BACK,GL_FILL);

        Shader shader = mesh.getShader();
        shader.bind()
                .setUniform("uProjection", camera.getProjectionMatrix())
                .setUniform("uView", camera.getViewMatrix())
                .setUniform("uCameraPosition", camera.getPosition())
                .setUniform("aValue", heightScale);

        light.apply(shader);
        material.apply(shader);
        shader.unbind();

        mesh.setUniform("uWorld", new Matrix4f().rotateX(angleX).rotateY(angleY));
        mesh.draw();
    }

    public void deinit() {}

    public static void main(String[] args) {
        new Window(new TerrainLoader(), "Trabalho + TDE", 800,600).show();
    }

}
