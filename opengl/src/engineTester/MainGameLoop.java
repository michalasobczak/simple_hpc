package engineTester;

import org.lwjgl.opengl.Display;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.RawModel;
import renderEngine.Renderer;

public class MainGameLoop {

    public static void main (String[] args) {
        DisplayManager.createDisplay();
        Loader loader = new Loader();
        Renderer renderer = new Renderer();
        Display.setTitle("simple_hpc.OpenGL");
        float[] vertices = {
                -0.5f,   0.5f,  0f,
                -0.5f,  -0.5f,  0f,
                 0.5f,  -0.5f,  0f,
                 0.5f,   0.5f,  0f,
        };
        int[] indices = {
                0,1,3,
                3,1,2
        };
        RawModel model = loader.loadtoVAO(vertices, indices);
        while (!Display.isCloseRequested()) {
            renderer.prepare();
            renderer.render(model);
            DisplayManager.updateDisplay();
        }
        DisplayManager.closeDisplay();
    }

}
