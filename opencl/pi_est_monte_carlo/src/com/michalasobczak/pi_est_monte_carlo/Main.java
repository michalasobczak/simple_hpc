package com.michalasobczak.pi_est_monte_carlo;

import com.michalasobczak.opencl.PlatformParametersSet;
import com.michalasobczak.opencl.RuntimeConfigurationSet;
import com.michalasobczak.opencl.Utils;
import org.jocl.CL;



public class Main {

    public static void main(String[] args) {
        Utils.log("***** PI EST MC                             *****");
        Utils.log("***** com.michalasobczak.pi_est_monte_carlo *****");

        // -----
        Utils.log("0. OpenCL specific configuration");
        Utils.log(" - Enable exceptions and subsequently omit error checks in this sample");
        CL.setExceptionsEnabled(true);

        // -----
        Utils.log("1. Initialize configuration classes");
        int n = 1024*1024*32;
        PlatformParametersSet p = new PlatformParametersSet();
        RuntimeConfigurationSet r = new RuntimeConfigurationSet();
        KernelConfigurationSet c = new KernelConfigurationSet(n);

        // -----
        Utils.log("2. Get and print platform/devices parameters");
        p.getPlatformsAndDevices();
        p.getDevicesInfo();

        // -----
        Utils.log("3. Platform and device selection");
        r.selectPlatform(0);
        r.selectDevice(0);

        // -----
        Utils.log("4. Create input and output data");
        c.initializeSrcArrayA();
        c.initializeSrcArrayB();
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
        c.runKernel(9);

        // -----
        Utils.log("7. Release kernel, program, and memory objects");
        c.releaseResources();

        // -----
        Utils.log("8. Debug results");
        c.printResults();
    } // main

} // class Main
