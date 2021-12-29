package com.michalasobczak.picture;

import com.michalasobczak.opencl.ExecutionStatistics;
import com.michalasobczak.opencl.RuntimeConfigurationSet;
import org.jocl.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import static org.jocl.CL.*;
import static org.jocl.CL.CL_MEM_READ_WRITE;


class KernelConfigurationSet {
    public cl_context_properties contextProperties;
    public cl_context context;
    public cl_command_queue commandQueue;

    public String content;
    public cl_program program;
    public cl_kernel kernel;

    long[] globalWorkSize;
    private BufferedImage inputImage;
    public BufferedImage outputImage;
    private int imageSizeX;
    private int imageSizeY;
    private cl_mem inputImageMem;
    private cl_mem outputImageMem;
    JLabel outputLabel;


    public KernelConfigurationSet() {
        System.out.println(" - KernelConfigurationSet");
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


    public void readKernelFile() {
        this.content = new String("");
        try {
            this.content = Files.readString(Path.of("picture/src/com/michalasobczak/picture/kernel.c"));
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
        //
        globalWorkSize = new long[2];
        globalWorkSize[0] = imageSizeX;
        globalWorkSize[1] = imageSizeY;
    }


    public void setKernelArgs(float factor) {
        clSetKernelArg(kernel, 0, Sizeof.cl_mem, Pointer.to(inputImageMem));
        clSetKernelArg(kernel, 1, Sizeof.cl_mem, Pointer.to(outputImageMem));
        clSetKernelArg(kernel, 2, Sizeof.cl_float, Pointer.to(new float[] { factor } ));
    }


    public void runKernel(int iterations) {
        long sumRun = 0;
        for (int i = 0; i<iterations; i++) {
            long aTime = ZonedDateTime.now().toInstant().toEpochMilli();
            //
                System.out.println("Start.....");
                // Write input
                cl_event writeEvent0 = new cl_event();
                // Execute the kernel
                cl_event kernelEvent0 = new cl_event();
                    clEnqueueNDRangeKernel(this.commandQueue, this.kernel, 2, null, this.globalWorkSize, null, 0, null, kernelEvent0);
                    clFinish(this.commandQueue);
                System.out.println("Waiting for kernel events...");
                CL.clWaitForEvents(1, new cl_event[]{kernelEvent0});
                // Read output
                cl_event readEvent0 = new cl_event();
                DataBufferInt dataBufferDst = (DataBufferInt)outputImage.getRaster().getDataBuffer();
                int dataDst[] = dataBufferDst.getData();
                    clEnqueueReadImage(this.commandQueue, outputImageMem, true, new long[3],
                            new long[]{imageSizeX, imageSizeY, 1}, imageSizeX * Sizeof.cl_uint,
                            0, Pointer.to(dataDst), 0, null, readEvent0);
                System.out.println("Waiting for read events...");
                CL.clWaitForEvents(1, new cl_event[]{readEvent0});
            //
            long bTime = ZonedDateTime.now().toInstant().toEpochMilli();
            sumRun = sumRun + (bTime - aTime);
            //
            // Print the timing information for the commands
            ExecutionStatistics executionStatistics = new ExecutionStatistics();
            executionStatistics.addEntry("kernel0", kernelEvent0);
            executionStatistics.addEntry("read0",   readEvent0);
            executionStatistics.print();
            System.out.println("Took OpenCL calc&read result: " + String.valueOf(bTime - aTime) + "ms\n");
            System.out.println("\n");
        }
        System.out.println("Calc&Read AVG: " + sumRun/iterations);
    }


    public void releaseResources() {
        clReleaseKernel(this.kernel);
        clReleaseProgram(this.program);
        clReleaseCommandQueue(this.commandQueue);
        clReleaseContext(this.context);
    }

    public static BufferedImage createBufferedImage(String fileName) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(fileName));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        int sizeX = image.getWidth();
        int sizeY = image.getHeight();
        BufferedImage result = new BufferedImage(sizeX, sizeY, BufferedImage.TYPE_INT_RGB);
        Graphics g = result.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return result;
    }


    public void initImages(String path) {
        String fileName = path;
        inputImage = createBufferedImage(fileName);
        imageSizeX = inputImage.getWidth();
        imageSizeY = inputImage.getHeight();
        System.out.println("x=" + imageSizeX + ", y=" + imageSizeY);
        outputImage = new BufferedImage(imageSizeX, imageSizeY, BufferedImage.TYPE_INT_RGB);
    }


    public void initPanel() {
        // Create the panel showing the input and output images
        JPanel mainPanel = new JPanel(new GridLayout(1,0));
        JLabel inputLabel = new JLabel(new ImageIcon(inputImage));
        mainPanel.add(inputLabel, BorderLayout.CENTER);
        outputLabel = new JLabel(new ImageIcon(outputImage));
        mainPanel.add(outputLabel, BorderLayout.CENTER);
        // Create the main frame
        JFrame frame = new JFrame("OpenCL picture test no 1");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        //frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        //frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }

    public void checkforImageSupport() {
        // Check if images are supported
        int imageSupport[] = new int[1];
        clGetDeviceInfo (RuntimeConfigurationSet.device, CL.CL_DEVICE_IMAGE_SUPPORT, Sizeof.cl_int, Pointer.to(imageSupport), null);
        System.out.println("Images supported: " + (imageSupport[0]==1));
        if (imageSupport[0] == 0) {
            System.out.println("Images are not supported");
            System.exit(1);
            return;
        }
    }

    public void initImageMem() {
        // Create the memory object for the input- and output image
        DataBufferInt dataBufferSrc = (DataBufferInt)inputImage.getRaster().getDataBuffer();
        int dataSrc[] = dataBufferSrc.getData();
        cl_image_format imageFormat = new cl_image_format();
        imageFormat.image_channel_order = CL_RGBA;
        imageFormat.image_channel_data_type = CL_UNSIGNED_INT8;
        inputImageMem = clCreateImage2D(
                context, CL_MEM_READ_ONLY | CL_MEM_USE_HOST_PTR,
                new cl_image_format[]{imageFormat}, imageSizeX, imageSizeY,
                imageSizeX * Sizeof.cl_uint, Pointer.to(dataSrc), null);
        outputImageMem = clCreateImage2D(
                context, CL_MEM_WRITE_ONLY,
                new cl_image_format[]{imageFormat}, imageSizeX, imageSizeY,
                0, null, null);
    }
} // class Configuration
