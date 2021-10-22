__kernel void sampleKernel(__global const float *a,
                           __global float *d)
{
    __private int gid = get_global_id(0);
    __private int gr  = get_group_id(0);
    __private int gc  = get_num_groups(0);
    __private int lid = get_local_id(0);
    __private int ls  = get_local_size(0);
    __private int eid = (gr*ls)+lid;
    __private int gs  = gc*ls;

    //printf(" %i \n", gid);
    d[gid] = ls;
}
