
__kernel void sampleKernel(__global const float *a,
                           __global float *d)
{
    __private int gid = get_global_id(0);
    //__private int lid = get_local_id(0);
    d[gid] = (sin(a[gid]) + cos(a[gid]) * 3.14);
    //d[gid] = lid;
}
