void first(int *ptr) {
  (*ptr)++;
}

__kernel void sampleKernel(__global const float *a, __global float *d) {
    __private int gid = get_global_id(0);
    __private int group_id = get_group_id(0);
    int var;
    int *ptr = 0;
    printf("%i, %p\n", gid, ptr);
    var = 4;
    ptr = &var;
    printf("%i, %p, %i\n", gid, ptr, (int)*ptr);
    *ptr = *ptr + gid;
    printf("%i, %p, %i\n", gid, ptr, (int)*ptr);
    first(ptr);
    printf("%i, %p, %i\n", gid, ptr, (int)*ptr);
    d[gid] = (float)*ptr;
}
