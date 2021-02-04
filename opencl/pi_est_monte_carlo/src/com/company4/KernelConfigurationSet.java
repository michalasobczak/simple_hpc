package com.company4;

import com.company.RuntimeConfigurationSet;
import org.jocl.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Random;
import static org.jocl.CL.*;
import static org.jocl.CL.CL_MEM_READ_WRITE;


class KernelConfigurationSet {
    private final int n;

    public static float[] srcArrayA;
    public static float[] srcArrayB;
    public static int[] dstArray;
    public static Pointer srcA;
    public static Pointer srcB;
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
    public float[] getSrcArrayB() {
        System.out.println(" - Allocating sample data");
        return new float[this.n];
    }
    public int[] getDstArrayA() {
        System.out.println(" - Allocating return buffer");
        return new int[this.n];
    }


    public void initializeSrcArrayA() {
        srcArrayA = this.getSrcArrayA();
        srcA      = Pointer.to(srcArrayA);
    }
    public void initializeSrcArrayB() {
        srcArrayB = this.getSrcArrayB();
        srcB      = Pointer.to(srcArrayB);
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
            srcArrayB[i] = rd.nextFloat();
        }
        System.out.println(" - Finished randomizing");
    }


    public void printSrcArray() {
        if (this.n <= 1024) {
            System.out.println(java.util.Arrays.toString(srcArrayA));
            System.out.println(java.util.Arrays.toString(srcArrayB));
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
        this.memObjects[1] = clCreateBuffer(this.context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, (long) Sizeof.cl_float * this.n, KernelConfigurationSet.srcB, null);
        this.memObjects[2] = clCreateBuffer(this.context, CL_MEM_READ_WRITE, (long) Sizeof.cl_int * this.n, null, null);
    }


    public void readKernelFile() {
        this.content = new String("");
        try {
            this.content = Files.readString(Path.of("opencl/pi_est_monte_carlo/src/com/company4/kernel.c"));
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
        clSetKernelArg(this.kernel, 2, Sizeof.cl_mem, Pointer.to(this.memObjects[2]));
    }


    public void configureWork() {
        this.global_work_size = new long[] { this.n } ;
        this.local_work_size  = new long[] { 1 };
    }


    public void runKernel(int iterations) {
        long sumCalc = 0;
        long sumRead = 0;
        for (int i = 0; i<=iterations; i++) {
            long aTime = ZonedDateTime.now().toInstant().toEpochMilli();
            // Execute the kernel
            clEnqueueNDRangeKernel(this.commandQueue, this.kernel, 1, null, this.global_work_size, this.local_work_size, 0, null, null);
            long bTime = ZonedDateTime.now().toInstant().toEpochMilli();
            System.out.println("Took OpenCL calculate: " + String.valueOf(bTime - aTime) + "ms");
            sumCalc = sumCalc + (bTime - aTime);
            // Read the output data
            clEnqueueReadBuffer(this.commandQueue, this.memObjects[2], CL_TRUE, 0, (long) n * Sizeof.cl_int, KernelConfigurationSet.dst, 0, null, null);
            bTime = ZonedDateTime.now().toInstant().toEpochMilli();
            System.out.println("Took OpenCL read result: " + String.valueOf(bTime - aTime) + "ms");
            sumRead = sumRead + (bTime - aTime);
        }

        System.out.println("Calc AVG: " + sumCalc/iterations);
        System.out.println("Read AVG: " + sumRead/iterations);
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

        float nf     = n*1.0F;
        int sum      = Arrays.stream(dstArray).sum();
        float result = (4.0F * sum) / nf;
        System.out.println("PI EST MC: " + result);
    }

} // class Configuration
