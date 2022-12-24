package com.michalasobczak.des;

import com.michalasobczak.opencl.RuntimeConfigurationSet;
import org.jocl.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.IntStream;

import static org.jocl.CL.*;
import static org.jocl.CL.CL_MEM_READ_WRITE;


class KernelConfigurationSet {
    private final int n;

    public static byte[] srcArrayA;
    public static byte[] dstArray;
    public static Pointer srcA;
    public static Pointer dst;

    public cl_context_properties contextProperties;
    public cl_context context;
    public cl_command_queue commandQueue;
    public cl_mem[] memObjects = new cl_mem[2];

    public String content;
    public cl_program program;
    public cl_kernel kernel;

    public long[] global_work_size;
    public long[] local_work_size;


    public KernelConfigurationSet(int n) {
        this.n = n;
        System.out.println(" - KernelConfigurationSet");
    }


    public byte[] getSrcArrayA() {
        System.out.println(" - Allocating sample data");
        return new byte[this.n*8];
    }


    public byte[] getDstArrayA() {
        System.out.println(" - Allocating return buffer");
        return new byte[this.n*8];
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
        for (int i = 0; i <= (n*8) - 1; i++) {
            byte c = (byte)('a' + rd.nextInt(10));
            //System.out.println(c);
            srcArrayA[i] = c;
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
        this.memObjects[0] = clCreateBuffer(this.context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, (long) Sizeof.cl_uchar8 * this.n, KernelConfigurationSet.srcA, null);
        this.memObjects[1] = clCreateBuffer(this.context, CL_MEM_READ_WRITE, (long) Sizeof.cl_uchar8 * this.n, null, null);
    }


    public void readKernelFile() {
        this.content = new String("");
        try {
            this.content = Files.readString(Path.of("des/src/com/michalasobczak/des/kernel.c"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void initializeKernel() {
        // Create the program from the source code
        this.program = clCreateProgramWithSource(this.context, 1, new String[] { this.content }, null, null);
        // Build the program
        clBuildProgram(this.program, 0, null, "-cl-std=CL3.0", null, null);
        // Create the kernel
        this.kernel = clCreateKernel(this.program, "sampleKernel", null);
        // Set the arguments for the kernel
        clSetKernelArg(this.kernel, 0, Sizeof.cl_mem, Pointer.to(this.memObjects[0]));
        clSetKernelArg(this.kernel, 1, Sizeof.cl_mem, Pointer.to(this.memObjects[1]));
    }


    public void configureWork() {
        this.global_work_size = new long[] { this.n } ;
        this.local_work_size  = new long[] { 32 };
    }


    public void runKernel(int iterations) {
        for (int i = 0; i<iterations; i++) {
            // Execute the kernel & Read the output data
            long aTime = ZonedDateTime.now().toInstant().toEpochMilli();
            System.out.println("Start.....");
            // Write input
            //cl_event writeEvent0 = new cl_event();
            // Execute the kernel
            //cl_event kernelEvent0 = new cl_event();
                clEnqueueNDRangeKernel(this.commandQueue, this.kernel, 1, null, this.global_work_size, this.local_work_size, 0, null, null);
                clEnqueueReadBuffer(this.commandQueue, this.memObjects[1], CL_TRUE, 0, (long) n * Sizeof.cl_uchar8, KernelConfigurationSet.dst, 0, null, null);
            long bTime = ZonedDateTime.now().toInstant().toEpochMilli();
            System.out.println("Took OpenCL read result: " + String.valueOf(bTime - aTime) + "ms");
        }
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
        System.out.println("Elements: " + dstArray.length);
        int i = 0;
        for (byte tmp : dstArray) {
            //System.out.println(String.valueOf(i) + " : " + (Byte.toUnsignedInt(tmp)) );
            i++;
        }
    }

} // class Configuration
