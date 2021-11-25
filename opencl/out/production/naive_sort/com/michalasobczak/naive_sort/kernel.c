__kernel void sampleKernel(__global const float *a,
                           __global float *d)
{
    __private int gc  = get_num_groups(0);
    __private int ls  = get_local_size(0);
    __private int gs  = gc*ls;
    float per_item = 1.0/gs;
    __private int gid = get_global_id(0);
    __private int lid = get_local_id(0);
    __private float current = a[gid];
    __private int location = (int)((current/per_item)*100);
    printf("%u, %u, %f -> %u\n", lid, gid, current, location);
    d[location] = current;
}
