package com.michalasobczak.opencl;

import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_device_id;
import org.jocl.cl_platform_id;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.jocl.CL.*;


public class PlatformParametersSet {
    private List<cl_device_id> devices = new ArrayList<cl_device_id>();


    public PlatformParametersSet() {
        System.out.println(" - PlatformParametersSet");
    }


    public void getPlatformsAndDevices() {
        System.out.println(" - PlatformParametersSet::getPlatforms");
        // Obtain the number of platforms
        int[] numPlatforms = new int[1];
        clGetPlatformIDs(0, null, numPlatforms);
        System.out.println("  - Number of platforms: " + numPlatforms[0]);
        // Obtain the platform IDs
        cl_platform_id[] platforms = new cl_platform_id[numPlatforms[0]];
        clGetPlatformIDs(platforms.length, platforms, null);
        RuntimeConfigurationSet.platforms2 = platforms;
        // Collect all devices of all platforms
        for (int i = 0; i < platforms.length; i++) {
            String platformName = getString(platforms[i], CL_PLATFORM_NAME);
            // Obtain the number of devices for the current platform
            int[] numDevices = new int[1];
            clGetDeviceIDs(platforms[i], CL_DEVICE_TYPE_ALL, 0, null, numDevices);
            System.out.println("  - #" + i + ": Number of devices in platform " + platformName + ": " + numDevices[0] + ", " + platforms[i]);
            cl_device_id[] devicesArray = new cl_device_id[numDevices[0]];
            clGetDeviceIDs(platforms[i], CL_DEVICE_TYPE_ALL, numDevices[0], devicesArray, null);
            this.devices.addAll(Arrays.asList(devicesArray));
        }
        RuntimeConfigurationSet.devices2 = this.devices;
    } // getPlatforms


    public void getDevicesInfo()  {
        System.out.println(" - PlatformParametersSet::getDevices");
        int counter = 0;
        for (cl_device_id device : this.devices) {
            String deviceName = getString(device, CL_DEVICE_NAME);
            System.out.println("--- #" + counter + ": Info for device " + deviceName + ", " + device + ": ---");
            System.out.printf("CL_DEVICE_NAME: \t\t\t\t\t%s\n", deviceName);
            String deviceVendor = getString(device, CL_DEVICE_VENDOR);
            System.out.printf("CL_DEVICE_VENDOR: \t\t\t\t\t%s\n", deviceVendor);
            String driverVersion = getString(device, CL_DRIVER_VERSION);
            System.out.printf("CL_DRIVER_VERSION: \t\t\t\t\t%s\n", driverVersion);
            long deviceType = getLong(device, CL_DEVICE_TYPE);
            if ((deviceType & CL_DEVICE_TYPE_CPU) != 0)
                System.out.printf("CL_DEVICE_TYPE:\t\t\t\t\t\t%s\n", "CL_DEVICE_TYPE_CPU");
            if ((deviceType & CL_DEVICE_TYPE_GPU) != 0)
                System.out.printf("CL_DEVICE_TYPE:\t\t\t\t\t\t%s\n", "CL_DEVICE_TYPE_GPU");
            if ((deviceType & CL_DEVICE_TYPE_ACCELERATOR) != 0)
                System.out.printf("CL_DEVICE_TYPE:\t\t\t\t\t\t%s\n", "CL_DEVICE_TYPE_ACCELERATOR");
            if ((deviceType & CL_DEVICE_TYPE_DEFAULT) != 0)
                System.out.printf("CL_DEVICE_TYPE:\t\t\t\t\t\t%s\n", "CL_DEVICE_TYPE_DEFAULT");
            int maxComputeUnits = getInt(device, CL_DEVICE_MAX_COMPUTE_UNITS);
            System.out.printf("CL_DEVICE_MAX_COMPUTE_UNITS:\t\t%d\n", maxComputeUnits);
            long maxWorkItemDimensions = getLong(device, CL_DEVICE_MAX_WORK_ITEM_DIMENSIONS);
            System.out.printf("CL_DEVICE_MAX_WORK_ITEM_DIMENSIONS:\t%d\n", maxWorkItemDimensions);
            long[] maxWorkItemSizes = getSizes(device, CL_DEVICE_MAX_WORK_ITEM_SIZES, 3);
            System.out.printf("CL_DEVICE_MAX_WORK_ITEM_SIZES:\t\t%d / %d / %d \n",
                    maxWorkItemSizes[0], maxWorkItemSizes[1], maxWorkItemSizes[2]);
            long maxWorkGroupSize = getSize(device, CL_DEVICE_MAX_WORK_GROUP_SIZE);
            System.out.printf("CL_DEVICE_MAX_WORK_GROUP_SIZE:\t\t%d\n", maxWorkGroupSize);
            long maxClockFrequency = getLong(device, CL_DEVICE_MAX_CLOCK_FREQUENCY);
            System.out.printf("CL_DEVICE_MAX_CLOCK_FREQUENCY:\t\t%d MHz\n", maxClockFrequency);
            int addressBits = getInt(device, CL_DEVICE_ADDRESS_BITS);
            System.out.printf("CL_DEVICE_ADDRESS_BITS:\t\t\t\t%d\n", addressBits);
            long maxMemAllocSize = getLong(device, CL_DEVICE_MAX_MEM_ALLOC_SIZE);
            System.out.printf("CL_DEVICE_MAX_MEM_ALLOC_SIZE:\t\t%d MByte\n", (int) (maxMemAllocSize / (1024 * 1024)));
            long globalMemSize = getLong(device, CL_DEVICE_GLOBAL_MEM_SIZE);
            System.out.printf("CL_DEVICE_GLOBAL_MEM_SIZE:\t\t\t%d MByte\n", (int) (globalMemSize / (1024 * 1024)));
            int errorCorrectionSupport = getInt(device, CL_DEVICE_ERROR_CORRECTION_SUPPORT);
            System.out.printf("CL_DEVICE_ERROR_CORRECTION_SUPPORT:\t%s\n", errorCorrectionSupport != 0 ? "yes" : "no");
            int localMemType = getInt(device, CL_DEVICE_LOCAL_MEM_TYPE);
            System.out.printf("CL_DEVICE_LOCAL_MEM_TYPE:\t\t\t%s\n", localMemType == 1 ? "local" : "global");
            long localMemSize = getLong(device, CL_DEVICE_LOCAL_MEM_SIZE);
            System.out.printf("CL_DEVICE_LOCAL_MEM_SIZE:\t\t\t%d KByte\n", (int) (localMemSize / 1024));
            long maxConstantBufferSize = getLong(device, CL_DEVICE_MAX_CONSTANT_BUFFER_SIZE);
            System.out.printf("CL_DEVICE_MAX_CONSTANT_BUFFER_SIZE:\t%d KByte\n", (int) (maxConstantBufferSize / 1024));
            long queueProperties = getLong(device, CL_DEVICE_QUEUE_PROPERTIES);
            if ((queueProperties & CL_QUEUE_OUT_OF_ORDER_EXEC_MODE_ENABLE) != 0)
                System.out.printf("CL_DEVICE_QUEUE_PROPERTIES:\t\t\t%s\n", "CL_QUEUE_OUT_OF_ORDER_EXEC_MODE_ENABLE");
            if ((queueProperties & CL_QUEUE_PROFILING_ENABLE) != 0)
                System.out.printf("CL_DEVICE_QUEUE_PROPERTIES:\t\t\t%s\n", "CL_QUEUE_PROFILING_ENABLE");
            int imageSupport = getInt(device, CL_DEVICE_IMAGE_SUPPORT);
            System.out.printf("CL_DEVICE_IMAGE_SUPPORT:\t\t\t%d\n", imageSupport);
            int maxReadImageArgs = getInt(device, CL_DEVICE_MAX_READ_IMAGE_ARGS);
            System.out.printf("CL_DEVICE_MAX_READ_IMAGE_ARGS:\t\t%d\n", maxReadImageArgs);
            int maxWriteImageArgs = getInt(device, CL_DEVICE_MAX_WRITE_IMAGE_ARGS);
            System.out.printf("CL_DEVICE_MAX_WRITE_IMAGE_ARGS:\t\t%d\n", maxWriteImageArgs);
            long singleFpConfig = getLong(device, CL_DEVICE_SINGLE_FP_CONFIG);
            System.out.printf("CL_DEVICE_SINGLE_FP_CONFIG:\t\t\t%s\n", stringFor_cl_device_fp_config(singleFpConfig));
            long image2dMaxWidth = getSize(device, CL_DEVICE_IMAGE2D_MAX_WIDTH);
            System.out.printf("CL_DEVICE_2D_MAX_WIDTH\t\t\t\t%d\n", image2dMaxWidth);
            long image2dMaxHeight = getSize(device, CL_DEVICE_IMAGE2D_MAX_HEIGHT);
            System.out.printf("CL_DEVICE_2D_MAX_HEIGHT\t\t\t\t%d\n", image2dMaxHeight);
            long image3dMaxWidth = getSize(device, CL_DEVICE_IMAGE3D_MAX_WIDTH);
            System.out.printf("CL_DEVICE_3D_MAX_WIDTH\t\t\t\t%d\n", image3dMaxWidth);
            long image3dMaxHeight = getSize(device, CL_DEVICE_IMAGE3D_MAX_HEIGHT);
            System.out.printf("CL_DEVICE_3D_MAX_HEIGHT\t\t\t\t%d\n", image3dMaxHeight);
            long image3dMaxDepth = getSize(device, CL_DEVICE_IMAGE3D_MAX_DEPTH);
            System.out.printf("CL_DEVICE_3D_MAX_DEPTH\t\t\t\t%d\n", image3dMaxDepth);
            System.out.print("CL_DEVICE_PREFERRED_VECTOR_WIDTH_\t");
            int preferredVectorWidthChar = getInt(device, CL_DEVICE_PREFERRED_VECTOR_WIDTH_CHAR);
            int preferredVectorWidthShort = getInt(device, CL_DEVICE_PREFERRED_VECTOR_WIDTH_SHORT);
            int preferredVectorWidthInt = getInt(device, CL_DEVICE_PREFERRED_VECTOR_WIDTH_INT);
            int preferredVectorWidthLong = getInt(device, CL_DEVICE_PREFERRED_VECTOR_WIDTH_LONG);
            int preferredVectorWidthFloat = getInt(device, CL_DEVICE_PREFERRED_VECTOR_WIDTH_FLOAT);
            int preferredVectorWidthDouble = getInt(device, CL_DEVICE_PREFERRED_VECTOR_WIDTH_DOUBLE);
            System.out.printf("CHAR %d, SHORT %d, INT %d, LONG %d, FLOAT %d, DOUBLE %d\n",
                    preferredVectorWidthChar, preferredVectorWidthShort,
                    preferredVectorWidthInt, preferredVectorWidthLong,
                    preferredVectorWidthFloat, preferredVectorWidthDouble);
            counter = counter + 1;
        }
    } // getDevices


    private static int getInt(cl_device_id device, int paramName) {
        return getInts(device, paramName, 1)[0];
    }


    private static int[] getInts(cl_device_id device, int paramName, int numValues) {
        int[] values = new int[numValues];
        clGetDeviceInfo(device, paramName, (long) Sizeof.cl_int * numValues, Pointer.to(values), null);
        return values;
    }


    private static long getLong(cl_device_id device, int paramName) {
        return getLongs(device, paramName, 1)[0];
    }


    private static long[] getLongs(cl_device_id device, int paramName, int numValues) {
        long[] values = new long[numValues];
        clGetDeviceInfo(device, paramName, (long) Sizeof.cl_long * numValues, Pointer.to(values), null);
        return values;
    }


    private static String getString(cl_device_id device, int paramName) {
        // Obtain the length of the string that will be queried
        long[] size = new long[1];
        clGetDeviceInfo(device, paramName, 0, null, size);
        // Create a buffer of the appropriate size and fill it with the info
        byte[] buffer = new byte[(int)size[0]];
        clGetDeviceInfo(device, paramName, buffer.length, Pointer.to(buffer), null);
        // Create a string from the buffer (excluding the trailing \0 byte)
        return new String(buffer, 0, buffer.length-1);
    }


    private static String getString(cl_platform_id platform, int paramName) {
        // Obtain the length of the string that will be queried
        long[] size = new long[1];
        clGetPlatformInfo(platform, paramName, 0, null, size);
        // Create a buffer of the appropriate size and fill it with the info
        byte[] buffer = new byte[(int)size[0]];
        clGetPlatformInfo(platform, paramName, buffer.length, Pointer.to(buffer), null);
        // Create a string from the buffer (excluding the trailing \0 byte)
        return new String(buffer, 0, buffer.length-1);
    }


    private static long getSize(cl_device_id device, int paramName) {
        return getSizes(device, paramName, 1)[0];
    }


    static long[] getSizes(cl_device_id device, int paramName, int numValues) {
        // The size of the returned data has to depend on
        // the size of a size_t, which is handled here
        ByteBuffer buffer = ByteBuffer.allocate(numValues * Sizeof.size_t).order(ByteOrder.nativeOrder());
        clGetDeviceInfo(device, paramName, (long) Sizeof.size_t * numValues, Pointer.to(buffer), null);
        long[] values = new long[numValues];
        if (Sizeof.size_t == 4) {
            for (int i=0; i<numValues; i++) {
                values[i] = buffer.getInt(i * Sizeof.size_t);
            }
        }
        else {
            for (int i=0; i<numValues; i++) {
                values[i] = buffer.getLong(i * Sizeof.size_t);
            }
        }
        return values;
    }

} // class PlatformParametersSet
