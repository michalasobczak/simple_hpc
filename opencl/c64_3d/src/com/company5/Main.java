package com.company5;

import com.company.PlatformParametersSet;
import com.company.RuntimeConfigurationSet;
import org.jocl.CL;
import com.company.Utils;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferStrategy;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.*;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Main extends JFrame {
    public static int[] vertices_3d;
    public static int vertices_3d_count;
    public static int vertices_2d_count;
    public static int[] vertices_2d;
    public static int n;



    public static void main(String[] args) throws IOException {
        ArrayList<Integer> vector = new ArrayList<Integer>();
        List<String> model = Files.readAllLines(Path.of("opencl/c64_3d/sphere.model"));
        for (String line : model) {
            String[] parsedLine = line.split(",");
            vector.add(Integer.parseInt(parsedLine[0]));
            vector.add(Integer.parseInt(parsedLine[1]));
            vector.add(Integer.parseInt(parsedLine[2]));
        }
        System.out.println("Vector size: " + vector.size());
        int size = vector.size();
        vertices_3d = new int[size];
        for (int i=0; i<=size-1; i++) {
            vertices_3d[i] = vector.get(i);
        }

        vertices_3d_count = vertices_3d.length;
        vertices_2d_count = vertices_3d.length;
        vertices_2d = new int[vertices_3d.length];
        n = vertices_3d.length;



        Utils.log("***** C64 3D       *****");
        Utils.log("***** com.company5 *****");

        // -----
        Utils.log("0. OpenCL specific configuration");
        Utils.log(" - Enable exceptions and subsequently omit error checks in this sample");
        CL.setExceptionsEnabled(true);

        // -----
        Utils.log("1. Initialize configuration classes");

        PlatformParametersSet p = new PlatformParametersSet();
        RuntimeConfigurationSet r = new RuntimeConfigurationSet();
        KernelConfigurationSet c = new KernelConfigurationSet(n);

        // -----
        Utils.log("2. Get and print platform/devices parameters");
        p.getPlatformsAndDevices();
        p.getDevicesInfo();

        // -----
        Utils.log("3. Platform and device selection");
        r.selectPlatform(1);
        r.selectDevice(1);

        // -----
        Utils.log("4. Create input and output data");
        c.initializeSrcArrayA();
        c.initializeDstArray();
        c.generateSampleRandomData();
        c.printSrcArray();

        // -----
        Utils.log("4. Create context and command queue and buffers");
        c.createContext();
        c.createCommandQueue();
        c.createBuffers();

        // -----
        Utils.log("5. Read kernel file, create program, pass arguments");
        c.readKernelFile();
        c.initializeKernel();
        c.configureWork();

        // -----
        Utils.log("6. Run kernel, read buffer");
        c.runKernel(0);

        // -----
        // NOTE: releasing resources prevents from creating UI elements here after
        //Utils.log("7. Release kernel, program, and memory objects");
        //c.releaseResources();

        // -----
        Utils.log("8. Debug results");
        c.printResults();

        Utils.log("9. Initialize UI");
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main().setVisible(true);
            }
        });
    } // main

    public Main() {
        super("Passing OpenCL calculations to UI");
        setSize(1600, 1200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
    void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        for (int i=0; i<=Main.vertices_2d.length-2; i+=2) {
            g2d.drawLine(Main.vertices_2d[i], Main.vertices_2d[i+1], Main.vertices_2d[i], Main.vertices_2d[i+1]);
        }
    }
    public void paint(Graphics g) {
        super.paint(g);
        draw(g);
    }

} // class Main

