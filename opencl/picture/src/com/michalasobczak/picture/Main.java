package com.michalasobczak.picture;

import com.michalasobczak.opencl.PlatformParametersSet;
import com.michalasobczak.opencl.RuntimeConfigurationSet;
import com.michalasobczak.opencl.Utils;
import org.jocl.CL;

import javax.swing.*;


public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //new JOCLSimpleImage();
                Utils.log("***** picture                     *****");
                Utils.log("***** com.michalasobczak.picture  *****");
                CL.setExceptionsEnabled(true);
                PlatformParametersSet p = new PlatformParametersSet();
                RuntimeConfigurationSet r = new RuntimeConfigurationSet();
                KernelConfigurationSet c = new KernelConfigurationSet();
                c.initImages("picture/src/com/michalasobczak/picture/input3.png");
                //c.initPanel();
                p.getPlatformsAndDevices();
                p.getDevicesInfo();
                r.selectPlatform(1);
                r.selectDevice(1);
                c.createContext();
                c.createCommandQueue();
                c.checkforImageSupport();
                c.readKernelFile();
                c.initImageMem();
                c.initializeKernel();
                Thread thread = new Thread(new Runnable() {
                    public void run() {
                        float x = 1.0f;
                        while (x < 2.0f) {
                            c.setKernelArgs(x);
                            c.runKernel(1);
                            //c.outputLabel.repaint();
                            x = x + 0.1f;
                        }
                    }
                });
                thread.setDaemon(true);
                thread.start();
                //c.releaseResources();
            }
        });
    } // main

} // class Main
