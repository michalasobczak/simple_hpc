package com.michalasobczak.c64_3d;

import com.michalasobczak.opencl.RuntimeConfigurationSet;
import org.jocl.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import static org.jocl.CL.*;
import static org.jocl.CL.CL_MEM_READ_WRITE;



class KernelConfigurationSet {
    private final int n;

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
        this.n = Main.n;
        System.out.println(" - KernelConfigurationSet");
    }


    public void initializeSrcArrayA() {
        srcA = Pointer.to(Main.vertices_3d);
    }


    public void initializeDstArray() {
        dst = Pointer.to(Main.vertices_2d);
    }


    public void generateSampleRandomData() {
        System.out.println(" - NOP");
    }


    public void printSrcArray() {
        if (Main.n <= 1024) {
            System.out.println(java.util.Arrays.toString(Main.vertices_3d));
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
        // Allocate the memory objects for the input - and output data
        this.memObjects[0] = clCreateBuffer(this.context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, (long) Sizeof.cl_int * Main.n, KernelConfigurationSet.srcA, null);
        this.memObjects[1] = clCreateBuffer(this.context, CL_MEM_READ_WRITE, (long) Sizeof.cl_int * Main.n, null, null);
    }


    public void readKernelFile() {
        this.content = new String("");
        try {
            this.content = Files.readString(Path.of("opencl/c64_3d/src/com/michalasobczak/c64_3d/kernel.c"));
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
        this.global_work_size = new long[] { Main.n } ;
        this.local_work_size  = new long[] { 3 };
    }


    public void runKernel(int iterations) {
        long sumRun = 0;
        for (int i = 0; i<iterations; i++) {
            // Execute the kernel & Read the output data
            long aTime = ZonedDateTime.now().toInstant().toEpochMilli();
            clEnqueueNDRangeKernel(this.commandQueue, this.kernel, 1, null, this.global_work_size, this.local_work_size, 0, null, null);
            clEnqueueReadBuffer(this.commandQueue, this.memObjects[1], CL_TRUE, 0, (long) Main.n * Sizeof.cl_int, KernelConfigurationSet.dst, 0, null, null);
            long bTime = ZonedDateTime.now().toInstant().toEpochMilli();
            System.out.println("Took OpenCL read result: " + String.valueOf(bTime - aTime) + "ms");
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
        if (Main.n <= 1024) {
            System.out.println("Result: " + java.util.Arrays.toString(Main.vertices_2d));
        }
    }

} // class Configuration
