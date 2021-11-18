
__kernel void sampleKernel(__global const int *a,
                           __global int *d)
{
    // gets
    __private int gid = get_global_id(0);
    __private int gr  = get_group_id(0);
    __private int lid = get_local_id(0);
    // calc
    if (lid == 0) {
        int start_x  = 100;
        int start_y  = 700;
        int start_z  = 500;
        int x2, y2, z2 = 0;
        int d1, d2, bx, by = 0;
        x2 = start_x + a[gid];
        y2 = start_y + a[gid+1];
        z2 = start_z + a[gid+2];
        d1 = z2/30;
        d2 = z2/20;
        bx = (x2*1) + d1;
        by = (y2*1) + d2 - 150;
        d[(gr*2)+0]  =  bx;
        d[(gr*2)+1]  =  by;
    } // lid == 0
}
