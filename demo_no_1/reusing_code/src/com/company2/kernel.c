
__kernel void sampleKernel(__global const float *a,
                           __global float *d)
{
    __private int gid = get_global_id(0);
    __private int lid = get_local_id(0);
    d[gid] = lid;
}
