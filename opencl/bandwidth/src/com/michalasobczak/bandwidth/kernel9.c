void first(int *ptr) {
  ptr = 1;
}



__kernel void sampleKernel(__global const float *a,
                           __global float *d)
{
    __private int gid = get_global_id(0);
    __private int group_id = get_group_id(0);

    __local  int *ptr1;
    __global int *ptr2;

    //first(ptr1);
    //first(ptr2);

    printf("%u\n");
}