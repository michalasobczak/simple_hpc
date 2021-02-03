
void orthographic_projection(unsigned char * rx, unsigned char * ry, int ax,int ay,int az, int sx,int sz, int cx,int cz) {
  int d = az/30;
  int d2 = az/20;
  int bx = (ax*1) + d;
  int by = (ay*1) + d2 - 150;
  *rx = bx;
  *ry = by;
}


__kernel void sampleKernel(__global const float3 *a,
                           __global float2 *d)
{
    // gets
    __private int gid = get_global_id(0);
    __private int gr  = get_group_id(0);
    __private int gc  = get_num_groups(0);
    __private int lid = get_local_id(0);
    __private int ls  = get_local_size(0);
    __private int eid = (gr*ls)+lid;
    // ids
    __private int gs  = gc*ls;
    __private int pr = a[eid]*(gc*ls)*100;

    // calc
    //orthographic_projection(&rxA,&ryA, x2,y2,z2, 4,4, 4,4);

    //d[pr] = 1;
}
