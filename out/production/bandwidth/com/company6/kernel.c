
__kernel void sampleKernel(__global const float *a,
                           __global float *d)
{
    // gets
    __private int gid = get_global_id(0);
    //__private int gr  = get_group_id(0);
    //__private int gc  = get_num_groups(0);
    //__private int lid = get_local_id(0);
    //__private int ls  = get_local_size(0);
    //__private int eid = (gr*ls)+lid;
    // ids
    //__private int gs  = gc*ls;
    //__private int pr = a[eid]*(gc*ls)*100;
    // calc
    d[gid] = a[gid];
}
