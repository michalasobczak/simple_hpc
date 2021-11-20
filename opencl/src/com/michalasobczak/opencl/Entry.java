package com.michalasobczak.opencl;

import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_event;

public class Entry {
    private String name;
    private long submitTime[] = new long[1];
    private long queuedTime[] = new long[1];
    private long startTime[] = new long[1];
    private long endTime[] = new long[1];
    Entry(String name, cl_event event) {
        this.name = name;
        CL.clGetEventProfilingInfo(event, CL.CL_PROFILING_COMMAND_QUEUED, Sizeof.cl_ulong, Pointer.to(queuedTime), null);
        CL.clGetEventProfilingInfo(event, CL.CL_PROFILING_COMMAND_SUBMIT, Sizeof.cl_ulong, Pointer.to(submitTime), null);
        CL.clGetEventProfilingInfo(event, CL.CL_PROFILING_COMMAND_START, Sizeof.cl_ulong, Pointer.to(startTime), null);
        CL.clGetEventProfilingInfo(event, CL.CL_PROFILING_COMMAND_END, Sizeof.cl_ulong, Pointer.to(endTime), null);
    }
    void normalize(long baseTime) {
        submitTime[0] -= baseTime;
        queuedTime[0] -= baseTime;
        startTime[0] -= baseTime;
        endTime[0] -= baseTime;
    }
    long getQueuedTime()
    {
        return queuedTime[0];
    }

    void print() {
        System.out.println("Event "+name+": ");
        System.out.println("Queued : "+ String.format("%8.3f", queuedTime[0]/1e6)+" ms");
        System.out.println("Submit : "+ String.format("%8.3f", submitTime[0]/1e6)+" ms");
        System.out.println("Start  : "+ String.format("%8.3f", startTime[0]/1e6)+" ms");
        System.out.println("End    : "+ String.format("%8.3f", endTime[0]/1e6)+" ms");
        long duration = endTime[0]-startTime[0];
        System.out.println("Time   : "+ String.format("%8.3f", duration / 1e6)+" ms");
    }

}
