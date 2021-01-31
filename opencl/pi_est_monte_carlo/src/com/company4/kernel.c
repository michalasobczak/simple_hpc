
__kernel void sampleKernel(__global const float *a,
                           __global const float *b,
                           __global int *d)
{
    //__private int gid = get_global_id(0);
    //__private int lid = get_local_id(0);
    //__private int ls = get_local_size(0);
    //d[gid] = a[gid] + b[gid];
    const float r = 1.0f;
    const int idx = get_global_id(0);
    if (a[idx]*a[idx] + b[idx]*b[idx] < r) {
      d[idx] = 1;
    }
    else {
      d[idx] = 0;
    }
}
