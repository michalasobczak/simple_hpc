package com.michalasobczak.opencl;

import org.jocl.cl_event;
import java.util.ArrayList;
import java.util.List;

public class ExecutionStatistics {
    private List<Entry> entries = new ArrayList<Entry>();
    public void addEntry(String name, cl_event event) {
        entries.add(new Entry(name, event));
    }
    public void clear()
    {
        entries.clear();
    }
    private void normalize() {
        long minQueuedTime = Long.MAX_VALUE;
        for (Entry entry : entries) {
            minQueuedTime = Math.min(minQueuedTime, entry.getQueuedTime());
        }
        for (Entry entry : entries) {
            entry.normalize(minQueuedTime);
        }
    }
    public void print() {
        normalize();
        for (Entry entry : entries) {
            entry.print();
        }
    }
}  // ExecutionStatistics
