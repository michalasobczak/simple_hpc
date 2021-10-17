package com.michalasobczak.bandwidth;

import com.michalasobczak.opencl.RuntimeConfigurationSet;
import com.michalasobczak.opencl.Utils;
import org.jocl.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
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
        long properties = 0;
        properties |= CL.CL_QUEUE_PROFILING_ENABLE;
        this.commandQueue = clCreateCommandQueue(this.context, RuntimeConfigurationSet.device, properties, null);
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
        this.local_work_size  = new long[] { 32 };
    }


    public void runKernel(int iterations) {
        long sumRun = 0;
        for (int i = 0; i<iterations; i++) {
            // Execute the kernel & Read the output data
            long aTime = ZonedDateTime.now().toInstant().toEpochMilli();
            //
                cl_event kernelEvent0 = new cl_event();
                clEnqueueNDRangeKernel(this.commandQueue, this.kernel, 1, null, this.global_work_size, this.local_work_size, 0, null, kernelEvent0);
                //
                System.out.println("Waiting for kernel events...");
                CL.clWaitForEvents(1, new cl_event[]{kernelEvent0});
                //
                cl_event readEvent0 = new cl_event();
                clEnqueueReadBuffer(this.commandQueue, this.memObjects[1], CL_TRUE, 0, (long) n * Sizeof.cl_float, KernelConfigurationSet.dst, 0, null, readEvent0);
                //
                System.out.println("Waiting for read events...");
                CL.clWaitForEvents(1, new cl_event[]{readEvent0});
            //
            long bTime = ZonedDateTime.now().toInstant().toEpochMilli();
            sumRun = sumRun + (bTime - aTime);

            // Print the timing information for the commands
            ExecutionStatistics executionStatistics = new ExecutionStatistics();
            executionStatistics.addEntry("kernel0", kernelEvent0);
            executionStatistics.addEntry("  read0", readEvent0);
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
        if (this.n <= 1024) {
            System.out.println("Result: " + java.util.Arrays.toString(KernelConfigurationSet.dstArray));
        }

        System.out.println("Last result: " + KernelConfigurationSet.srcArrayA[n-1] + " giving " + KernelConfigurationSet.dstArray[n-1]);
    }


    /**
     * A simple helper class for tracking cl_events and printing
     * timing information for the execution of the commands that
     * are associated with the events.
     */
    static class ExecutionStatistics
    {
        /**
         * A single entry of the ExecutionStatistics
         */
        private static class Entry
        {
            private String name;
            private long submitTime[] = new long[1];
            private long queuedTime[] = new long[1];
            private long startTime[] = new long[1];
            private long endTime[] = new long[1];

            Entry(String name, cl_event event)
            {
                this.name = name;
                CL.clGetEventProfilingInfo(
                        event, CL.CL_PROFILING_COMMAND_QUEUED,
                        Sizeof.cl_ulong, Pointer.to(queuedTime), null);
                CL.clGetEventProfilingInfo(
                        event, CL.CL_PROFILING_COMMAND_SUBMIT,
                        Sizeof.cl_ulong, Pointer.to(submitTime), null);
                CL.clGetEventProfilingInfo(
                        event, CL.CL_PROFILING_COMMAND_START,
                        Sizeof.cl_ulong, Pointer.to(startTime), null);
                CL.clGetEventProfilingInfo(
                        event, CL.CL_PROFILING_COMMAND_END,
                        Sizeof.cl_ulong, Pointer.to(endTime), null);
            }

            void normalize(long baseTime)
            {
                submitTime[0] -= baseTime;
                queuedTime[0] -= baseTime;
                startTime[0] -= baseTime;
                endTime[0] -= baseTime;
            }

            long getQueuedTime()
            {
                return queuedTime[0];
            }

            void print()
            {
                System.out.println("Event "+name+": ");
                System.out.println("Queued : "+
                        String.format("%8.3f", queuedTime[0]/1e6)+" ms");
                System.out.println("Submit : "+
                        String.format("%8.3f", submitTime[0]/1e6)+" ms");
                System.out.println("Start  : "+
                        String.format("%8.3f", startTime[0]/1e6)+" ms");
                System.out.println("End    : "+
                        String.format("%8.3f", endTime[0]/1e6)+" ms");

                long duration = endTime[0]-startTime[0];
                System.out.println("Time   : "+
                        String.format("%8.3f", duration / 1e6)+" ms");
            }
        }

        /**
         * The list of entries in this instance
         */
        private List<Entry> entries = new ArrayList<Entry>();

        /**
         * Adds the specified entry to this instance
         *
         * @param name A name for the event
         * @param event The event
         */
        public void addEntry(String name, cl_event event)
        {
            entries.add(new Entry(name, event));
        }

        /**
         * Removes all entries
         */
        public void clear()
        {
            entries.clear();
        }

        /**
         * Normalize the entries, so that the times are relative
         * to the time when the first event was queued
         */
        private void normalize()
        {
            long minQueuedTime = Long.MAX_VALUE;
            for (Entry entry : entries)
            {
                minQueuedTime = Math.min(minQueuedTime, entry.getQueuedTime());
            }
            for (Entry entry : entries)
            {
                entry.normalize(minQueuedTime);
            }
        }

        /**
         * Print the statistics
         */
        public void print()
        {
            normalize();
            for (Entry entry : entries)
            {
                entry.print();
            }
        }


    }  // ExecutionStatistics

} // class Configuration
