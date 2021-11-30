#define MAX 1024*16

__kernel void sampleKernel(__global float *a, __global float *d, __local float *local_data) {
    __private int ls  = get_local_size(0);
    __private int gid = get_global_id(0);
    __private int lid = get_local_id(0);
    __private int group_id = get_group_id(0);
    float tmp = 0.0;

    if (d[gid] == 0L) {
      d[gid] = a[gid];
      barrier(CLK_GLOBAL_MEM_FENCE);
    }

    if (gid < MAX) {
      //printf("lid=%u, gid=%u, gro=%u, ele=%f\n", lid, gid, group_id, d[gid]);
      //barrier(CLK_GLOBAL_MEM_FENCE);
      if (lid % 2 == 0) {
        if (d[gid] > d[gid+1]) {
          tmp = d[gid];
          d[gid] = d[gid+1];
          d[gid+1] = tmp;
          barrier(CLK_GLOBAL_MEM_FENCE);
        }
      }
    }

    //if (gid == 0) {
    //  for (int i=0;i<MAX;i++) {
    //    d[i] = a[i];
    //  }
    //}
}
