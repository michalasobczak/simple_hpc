__kernel void sampleKernel(__global const float *a,
                           __global float *d)
{
    __private int gid = get_global_id(0);
    __private int group_id = get_group_id(0);
    d[group_id] = d[group_id] + 1;
    printf("%u %u %f \n", group_id, gid, d[group_id]);
}
