
__kernel void sampleKernel(__global const float *a,
                           __global float *d)
{
    __private int gid = get_global_id(0);
    __private int n = 10;
    __private int myArray[10];
    for (int i=0; i<n; i++) {
      myArray[i] = i;
    }
    // explicit out of bounds error
    d[gid] = (a[gid] * 2.0) - myArray[gid];
}
