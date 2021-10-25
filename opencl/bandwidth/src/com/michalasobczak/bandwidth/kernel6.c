__kernel void sampleKernel(__global const float *a,
                           __global float *d)
{
    __private int gid = get_global_id(0);
    int j = 0;
    while (1) {
      j = j + 1;
    }
    d[gid] = j;
}
