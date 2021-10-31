__kernel void sampleKernel(__global const float *a,
                           __global float *d)
{
    __private int gid = get_global_id(0);
    __private int group_id = get_group_id(0);
    __local int shared;
    shared = 0.0;
    //barrier(CLK_LOCAL_MEM_FENCE);
    atomic_inc(&shared);
    d[group_id] = shared;
    printf("%u %u %f %i\n", group_id, gid, d[group_id], shared);
}
