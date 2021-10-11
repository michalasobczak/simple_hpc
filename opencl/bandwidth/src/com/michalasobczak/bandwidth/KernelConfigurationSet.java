package com.michalasobczak.bandwidth;

import com.michalasobczak.opencl.RuntimeConfigurationSet;
import com.michalasobczak.opencl.Utils;
import org.jocl.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.Random;
import static org.jocl.CL.*;
import static org.jocl.CL.CL_MEM_READ_WRITE;


class KernelConfigurationSet {
    private final int n;

    public static float[] srcArrayA;
    public static float[] dstArray;
    public static Pointer srcA;
    public static Pointer dst;

    public cl_context_properties contextProperties;
    public cl_context context;
    public cl_command_queue commandQueue;
    public cl_mem[] memObjects = new cl_mem[4];

    public String content;
    public cl_program program;
    public cl_kernel kernel;

    public long[] global_work_size;
    public long[] local_work_size;


    public KernelConfigurationSet(int n) {
        this.n = n;
        System.out.println(" - KernelConfigurationSet");
    }


    public float[] getSrcArrayA() {
        System.out.println(" - Allocating sample data");
        return new float[this.n];
    }


    public float[] getDstArrayA() {
        System.out.println(" - Allocating return buffer");
        return new float[this.n];
    }


    public void initializeSrcArrayA() {
        srcArrayA = this.getSrcArrayA();
        srcA      = Pointer.to(srcArrayA);
    }


    public void initializeDstArray() {
        dstArray  = this.getDstArrayA();
        dst       = Pointer.to(dstArray);
    }


    public void generateSampleRandomData() {
        System.out.println(" - Started randomizing");
        Random rd = new Random();
        for (int i = 0; i <= n - 1; i++) {
            srcArrayA[i] = rd.nextFloat();
        }
        System.out.println(" - Finished randomizing");
    }


    public void printSrcArray() {
        if (this.n <= 1024) {
            System.out.println(java.util.Arrays.toString(srcArrayA));
        }
    }


    public void createContext() {
        // Initialize the context properties
        this.contextProperties = new cl_context_properties();
        this.contextProperties.addProperty(CL_CONTEXT_PLATFORM, RuntimeConfigurationSet.platform);
        // Create a context for the selected device
        this.context = clCreateContext(this.contextProperties, 1, new cl_device_id[]{RuntimeConfigurationSet.device}, null, null, null);
    }


    public void createCommandQueue() {
        // Create a command-queue for the selected device
        this.commandQueue = clCreateCommandQueue(this.context, RuntimeConfigurationSet.device, 0, null);
    }


    public void createBuffers() {
        // Allocate the memory objects for the input- and output data
        this.memObjects[0] = clCreateBuffer(this.context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, (long) Sizeof.cl_float * this.n, KernelConfigurationSet.srcA, null);
        this.memObjects[1] = clCreateBuffer(this.context, CL_MEM_READ_WRITE, (long) Sizeof.cl_float * this.n, null, null);
    }


    public void readKernelFile() {
        this.content = new String("");
        try {
            this.content = Files.readString(Path.of("bandwidth/src/com/michalasobczak/bandwidth/kernel.c"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void initializeKernel() {
        // Create the program from the source code
        this.program = clCreateProgramWithSource(this.context, 1, new String[]{ this.content }, null, null);
        // Build the program
        clBuildProgram(this.program, 0, null, null, null, null);
        // Create the kernel
        this.kernel = clCreateKernel(this.program, "sampleKernel", null);
        // Set the arguments for the kernel
        clSetKernelArg(this.kernel, 0, Sizeof.cl_mem, Pointer.to(this.memObjects[0]));
        clSetKernelArg(this.kernel, 1, Sizeof.cl_mem, Pointer.to(this.memObjects[1]));
    }


    public void configureWork() {
        this.global_work_size = new long[] { this.n } ;
        this.local_work_size  = new long[] { 8192 };
    }


    public void runKernel(int iterations) {
        long sumRun = 0;
        for (int i = 0; i<iterations; i++) {
            // Execute the kernel & Read the output data
            long aTime = ZonedDateTime.now().toInstant().toEpochMilli();
            clEnqueueNDRangeKernel(this.commandQueue, this.kernel, 1, null, this.global_work_size, this.local_work_size, 0, null, null);
            clEnqueueReadBuffer(this.commandQueue, this.memObjects[1], CL_TRUE, 0, (long) n * Sizeof.cl_float, KernelConfigurationSet.dst, 0, null, null);
            long bTime = ZonedDateTime.now().toInstant().toEpochMilli();
            System.out.println("Took OpenCL calc&read result: " + String.valueOf(bTime - aTime) + "ms");
            sumRun = sumRun + (bTime - aTime);
        }

        System.out.println("Calc&Read AVG: " + sumRun/iterations);
    }


    public void releaseResources() {
        clReleaseMemObject(this.memObjects[0]);
        clReleaseMemObject(this.memObjects[1]);
        clReleaseKernel(this.kernel);
        clReleaseProgram(this.program);
        clReleaseCommandQueue(this.commandQueue);
        clReleaseContext(this.context);
    }


    public void printResults() {
        if (this.n <= 1024) {
            System.out.println("Result: " + java.util.Arrays.toString(KernelConfigurationSet.dstArray));
        }

        System.out.println("Last result: " + KernelConfigurationSet.srcArrayA[n-1] + " giving " + KernelConfigurationSet.dstArray[n-1]);
    }

} // class Configuration
