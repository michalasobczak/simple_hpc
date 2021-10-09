package com.michalasobczak.opencl;

import org.jocl.cl_device_id;
import org.jocl.cl_platform_id;
import java.util.ArrayList;
import java.util.List;

public class RuntimeConfigurationSet {

    public static cl_platform_id[] platforms2  = new cl_platform_id[10];
    public static List<cl_device_id> devices2  = new ArrayList<cl_device_id>();
    public int platformIndex;
    public int deviceIndex;
    public static cl_platform_id platform;
    public static cl_device_id device;


    public RuntimeConfigurationSet() {
        System.out.println(" - RuntimeConfigurationSet");
    }


    public void selectPlatform(int platformIndex) {
        this.platformIndex = platformIndex;
        platform           = platforms2[this.platformIndex];
        System.out.println("selectPlatform: " + platform);
    }


    public void selectDevice(int deviceIndex) {
        this.deviceIndex   = deviceIndex;
        device             = devices2.get(this.deviceIndex);
        System.out.println("selectDevice: " + device);
    }

} // class RuntimeConfigurationSet
