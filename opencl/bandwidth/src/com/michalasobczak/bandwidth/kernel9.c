void first(int *ptr) {
  (*ptr)++;
}

__kernel void sampleKernel(__global const float *a, __global float *d) {
    __private int gid = get_global_id(0);
    __private int group_id = get_group_id(0);

    int var;
    int *ptr1 = 0;
    printf("ptr1/0: %i\n", ptr1);
    var = 4;
    ptr1 = &var;
    printf("ptr1/a: %i\n", ptr1);
    printf("ptr1/a: %i\n", *ptr1);
    *ptr1 = *ptr1 + 2;
    printf("ptr1/b: %i\n", ptr1);
    printf("ptr1/b: %i\n", *ptr1);
    first(ptr1);
    printf("ptr1/c: %i\n", ptr1);
    printf("ptr1/c: %i\n", *ptr1);
    //d[gid] = *ptr1;
}
