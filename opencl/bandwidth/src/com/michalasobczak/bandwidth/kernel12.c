#define N (8*1024)+1

__kernel void sampleKernel(__global const float *a, __global float *d) {
  __private int gid = get_global_id(0);
  __private int lid = get_local_id(0);
  __local int myArray[N];
  if (lid == 0) {
    for (int i=0; i<N; i++) {
      myArray[i] = i+1;
    }
    printf("%i, %i\n", gid, myArray[0]);
  }
}
