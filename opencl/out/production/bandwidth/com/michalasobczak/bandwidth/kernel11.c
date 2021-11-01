void first(int *ptr) {
  *ptr = 2;
}

__kernel void sampleKernel(__global const float *a, __global float *d) {
    __private int gid = get_global_id(0);
    __private int group_id = get_group_id(0);
    __local int *ptr1;
    ptr1 = 0;
    first(&ptr1);
    printf("ptr1: %i, %u\n", gid, ptr1);
}