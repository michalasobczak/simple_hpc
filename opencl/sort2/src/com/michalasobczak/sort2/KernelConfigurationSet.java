package com.michalasobczak.sort2;

import com.michalasobczak.opencl.ExecutionStatistics;
import com.michalasobczak.opencl.RuntimeConfigurationSet;
import org.jocl.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import static org.jocl.CL.*;
import static org.jocl.CL.CL_MEM_READ_WRITE;


class KernelConfigurationSet {
    private final int n;
    private final int loc;

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


    public KernelConfigurationSet(int n, int loc) {
        this.n = n;
        this.loc = loc;
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
        long properties = 0;
        properties |= CL.CL_QUEUE_PROFILING_ENABLE;
        this.commandQueue = clCreateCommandQueue(this.context, RuntimeConfigurationSet.device, properties, null);
    }


    public void createBuffers() {
        // Allocate the memory objects for the input- and output data
        this.memObjects[0] = clCreateBuffer(this.context, CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR, (long) Sizeof.cl_float * this.n, KernelConfigurationSet.srcA, null);
        this.memObjects[1] = clCreateBuffer(this.context, CL_MEM_READ_WRITE, (long) Sizeof.cl_float * this.n, null, null);
    }


    public void readKernelFile() {
        this.content = new String("");
        try {
            this.content = Files.readString(Path.of("sort2/src/com/michalasobczak/sort2/kernel4.c"));
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
        clSetKernelArg(this.kernel, 2, Sizeof.cl_float * this.loc, null);
    }


    public void configureWork() {
        this.global_work_size = new long[] { this.n };  // dla kernel2 ustaw 1
        this.local_work_size  = new long[] { this.loc }; // dla kernel2 ustaw 1
    }


    public void runKernel(int iterations) {
        boolean withWriteEvent = true;
        long sumRun = 0;
        long[] off0 = new long[] { 0 };
        long[] off1 = new long[] { 1 };
        for (int i = 0; i<iterations; i++) {
            long aTime = ZonedDateTime.now().toInstant().toEpochMilli();
            //
                System.out.println("Start.....");
                // Write input
                cl_event writeEvent0 = new cl_event();
                if (withWriteEvent) {
                    long beforeWrite = 0;
                    long afterWrite = 0;
                    long writeDiff = 0;
                    beforeWrite = ZonedDateTime.now().toInstant().toEpochMilli();
                        clEnqueueWriteBuffer(this.commandQueue, this.memObjects[0], true, 0, 0, KernelConfigurationSet.srcA, 0, null, writeEvent0);
                        clFinish(this.commandQueue);
                    afterWrite = ZonedDateTime.now().toInstant().toEpochMilli();
                    writeDiff = afterWrite - beforeWrite;
                    System.out.println("write, writeDiff=" + String.valueOf(writeDiff));
                    System.out.println("Waiting for write events...");
                    CL.clWaitForEvents(1, new cl_event[]{writeEvent0});
                }
                // Execute the kernel
                // Uwaga: wielokrotne uruchamianie tylko dla kernel3.
                // w pozostałych przykładach wskazać offset jako null i usunąć pętlę
                cl_event kernelEvent0 = new cl_event();
                //for (int j=0; j<=(this.n/2); j++) {
                    //System.out.println(j);
                    clEnqueueNDRangeKernel(this.commandQueue, this.kernel, 1, off0, this.global_work_size, this.local_work_size, 0, null, kernelEvent0);
                    clFinish(this.commandQueue);
                    //clEnqueueNDRangeKernel(this.commandQueue, this.kernel, 1, off1, this.global_work_size, this.local_work_size, 0, null, kernelEvent0);
                    //clFinish(this.commandQueue);
                //}
                System.out.println("Waiting for kernel events...");
                CL.clWaitForEvents(1, new cl_event[]{kernelEvent0});
                // Read output
                cl_event readEvent0 = new cl_event();
                clEnqueueReadBuffer(this.commandQueue, this.memObjects[1], CL_TRUE, 0, (long) n * Sizeof.cl_float, KernelConfigurationSet.dst, 0, null, readEvent0);
                System.out.println("Waiting for read events...");
                CL.clWaitForEvents(1, new cl_event[]{readEvent0});
            //
            long bTime = ZonedDateTime.now().toInstant().toEpochMilli();
            sumRun = sumRun + (bTime - aTime);
            //
            // Print the timing information for the commands
            ExecutionStatistics executionStatistics = new ExecutionStatistics();
            if (withWriteEvent) {
                executionStatistics.addEntry("write0", writeEvent0);
            }
            executionStatistics.addEntry("kernel0", kernelEvent0);
            executionStatistics.addEntry("read0",   readEvent0);
            executionStatistics.print();
            System.out.println("Took OpenCL calc&read result: " + String.valueOf(bTime - aTime) + "ms\n");
            System.out.println("\n");
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
        float[] copiedArray = Arrays.copyOfRange(dstArray, 0, this.n);
          //System.out.println(Arrays.toString(copiedArray));
        long aTime = ZonedDateTime.now().toInstant().toEpochMilli();
//        float tmp = 0;
//        for (int i=0;i<this.n-1;i++) {
//            for (int j=0;j<this.n-i-1;j++) {
//              if (copiedArray[j] > copiedArray[j+1]) {
//                  tmp = copiedArray[j];
//                  copiedArray[j] = copiedArray[j+1];
//                  copiedArray[j+1] = tmp;
//              }
//            }
//        }
        Arrays.sort(copiedArray);
        long bTime = ZonedDateTime.now().toInstant().toEpochMilli();
        System.out.println("Java sort result: " + String.valueOf(bTime - aTime) + "ms");
          //System.out.println(Arrays.toString(copiedArray));
        //
        System.out.println(Arrays.compare(copiedArray, dstArray));
    }


} // class Configuration
