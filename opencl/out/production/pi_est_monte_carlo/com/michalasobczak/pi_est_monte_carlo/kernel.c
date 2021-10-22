
__kernel void sampleKernel(__global const float *a,
                           __global const float *b,
                           __global int *d)
{
    const float r = 1.0f;
    const int idx = get_global_id(0);
    if (a[idx]*a[idx] + b[idx]*b[idx] < r) {
      d[idx] = 1;
    }
    else {
      d[idx] = 0;
    }
}
