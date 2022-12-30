package com.michalasobczak.aiml;

import com.michalasobczak.opencl.ExecutionStatistics;
import com.michalasobczak.opencl.RuntimeConfigurationSet;
import org.jocl.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;
import java.util.stream.IntStream;

import static org.jocl.CL.*;
import static org.jocl.CL.CL_MEM_READ_WRITE;


class KernelConfigurationSet {
    private int n;

    public static byte[] srcArrayA;
    public static int[] srcArrayB;
    public static byte[] dstArray;
    public static Pointer srcA;
    public static Pointer srcB;
    public static Pointer dst;

    public cl_context_properties contextProperties;
    public cl_context context;
    public cl_command_queue commandQueue;
    public cl_mem[] memObjects = new cl_mem[3];

    public String content;
    public cl_program program;
    public cl_kernel kernel;

    public long[] global_work_size;
    public long[] local_work_size;

    public Vector<String> vector = new Vector<>();
    public Vector<Integer> line_sizes = new Vector<Integer>();
    public int counter = 0;
    public int pointer = 0;
    public StringBuffer allstring = new StringBuffer();


    public KernelConfigurationSet(int n) {
        this.n = n;
        System.out.println(" - KernelConfigurationSet");
    }


    public byte[] getSrcArrayA() {
        System.out.println(" - Allocating sample data");
        return new byte[allstring.length()];
    }

    public int[] getSrcArrayB() {
        System.out.println(" - Allocating sample data");
        return new int[this.n*2];
    }


    public byte[] getDstArrayA() {
        System.out.println(" - Allocating return buffer");
        return new byte[this.n];
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

    public void readFile() {
        System.out.println(" - Read file");
        File file = new File("aiml/src/com/michalasobczak/aiml/bible.txt");
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st = null;
            while ((st = br.readLine()) != null) {
                allstring.append(st);
                vector.add(st);
                line_sizes.add(pointer);
                pointer = pointer + st.length();
                counter++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Read no of lines: " + counter);
        System.out.println("sample line: " + vector.get(100));
        System.out.println("sample line size in chars: " + line_sizes.get(5));

        n = counter;
    }

    public void generateSampleRandomData() {
        System.out.println(" - Started sampling data");
        for (int i=0; i<allstring.length(); i++) {
            srcArrayA[i] = (byte)allstring.charAt(i);
        }
        System.out.println("allstring size: " + allstring.length());
        for (int i=0; i<n; i++) {
            srcArrayB[i*2]  = line_sizes.get(i);
            srcArrayB[(i*2)+1] = vector.get(i).length();
        }
        System.out.println(" - Finished sampling data");
    }


    public void printSrcArray() {
        if (this.n <= 1024) {
            //System.out.println(java.util.Arrays.toString(srcArrayB));
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
        this.memObjects[0] = clCreateBuffer(this.context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, (long) Sizeof.cl_uchar * this.allstring.length(), KernelConfigurationSet.srcA, null);
        this.memObjects[1] = clCreateBuffer(this.context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, (long) Sizeof.cl_int * this.n * 2, KernelConfigurationSet.srcB, null);
        this.memObjects[2] = clCreateBuffer(this.context, CL_MEM_READ_WRITE, (long) Sizeof.cl_uchar * this.n, null, null);
    }


    public void readKernelFile() {
        this.content = new String("");
        try {
            this.content = Files.readString(Path.of("aiml/src/com/michalasobczak/aiml/utils.c"));
            this.content += "\n";
            this.content += Files.readString(Path.of("aiml/src/com/michalasobczak/aiml/kernel.c"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void initializeKernel() {
        // Create the program from the source code
        this.program = clCreateProgramWithSource(this.context, 1, new String[] { this.content }, null, null);
        // Build the program
        clBuildProgram(this.program, 0, null, "-cl-std=CL2.0", null, null);
        // Create the kernel
        this.kernel = clCreateKernel(this.program, "sampleKernel", null);
        // Set the arguments for the kernel
        clSetKernelArg(this.kernel, 0, Sizeof.cl_mem, Pointer.to(this.memObjects[0]));
        clSetKernelArg(this.kernel, 1, Sizeof.cl_mem, Pointer.to(this.memObjects[1]));
        clSetKernelArg(this.kernel, 2, Sizeof.cl_mem, Pointer.to(this.memObjects[2]));
    }


    public void configureWork() {
        this.global_work_size = new long[] { this.n } ;
        this.local_work_size  = new long[] { 32 };
    }


    public void runKernel(int iterations) {
        for (int i = 0; i<iterations; i++) {
            // Execute the kernel & Read the output data
            long aTime = ZonedDateTime.now().toInstant().toEpochMilli();
            //System.out.println("Start.....");
                // Execute the kernel
                cl_event kernelEvent0 = new cl_event();
                    clEnqueueNDRangeKernel(this.commandQueue, this.kernel, 1, null, this.global_work_size, this.local_work_size, 0, null, kernelEvent0);
                    //clFinish(this.commandQueue);
                //System.out.println("Waiting for kernel events...");
                CL.clWaitForEvents(1, new cl_event[]{kernelEvent0});
                // Read output
                cl_event readEvent0 = new cl_event();
                    clEnqueueReadBuffer(this.commandQueue, this.memObjects[2], CL_TRUE, 0, (long) n * Sizeof.cl_uchar, KernelConfigurationSet.dst, 0, null, readEvent0);
                //System.out.println("Waiting for read events...");
                CL.clWaitForEvents(1, new cl_event[]{readEvent0});
            long bTime = ZonedDateTime.now().toInstant().toEpochMilli();
            // Print the timing information for the commands
            ExecutionStatistics executionStatistics = new ExecutionStatistics();
            executionStatistics.addEntry("kernel0", kernelEvent0);
            executionStatistics.addEntry("read0",   readEvent0);
            //executionStatistics.print();
            //
            System.out.println("Took OpenCL read result: " + String.valueOf(bTime - aTime) + "ms");
        }
    }


    public void releaseResources() {
        clReleaseMemObject(this.memObjects[0]);
        clReleaseMemObject(this.memObjects[1]);
        clReleaseMemObject(this.memObjects[2]);
        clReleaseKernel(this.kernel);
        clReleaseProgram(this.program);
        clReleaseCommandQueue(this.commandQueue);
        clReleaseContext(this.context);
    }


    public void printResults() {
        System.out.println("Elements: " + dstArray.length);
        int i = 0;
        int counter = 0;
        int res = 0;
        for (byte tmp : dstArray) {
            res = (Byte.toUnsignedInt(tmp));
            if (res == 1) {
                counter++;
            }
            //System.out.println(String.valueOf(i) + " : " +  res);
            i++;
        }
        System.out.println("counter for search = " + counter);
    }

} // class Configuration
