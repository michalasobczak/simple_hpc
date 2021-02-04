
__kernel void sampleKernel(__global const int *a,
                           __global int *d)
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

    if (lid == 0) {
        /* ******** */
        /* DATA     */
        /* ******** */
        int camera_x = 200;
        int camera_y = 335;
        int camera_z = -250;
        int th_x = 0;
        int th_y = 0;
        int th_z = 0;
        int start_x = 700;
        int start_y = 700;
        int start_z = 500;
        int rxA = 0;
        int ryA = 0;
        /* ********* */
        /* FUNCTIONS */
        /* ********* */
        int x2, y2, z2 = 0;                 // 3D base + point
        unsigned char x3, y3 = 0;           // 2D equivalent
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
