#define MAX 1024*2

__kernel void sampleKernel(__global const float *a,
                           __global       float *d,
                           __local        float *local_data) {
    //__private int ls  = get_local_size(0);
    __private int gid = get_global_id(0);
    __private int group_id = get_group_id(0);
    //__private int elements = get_num_groups(0) * ls;
    __private float tmp = 0.0f;
    //
    for (int i=0; i<MAX; i++) {
      local_data[i] = a[i];
    }
    //
    barrier(CLK_LOCAL_MEM_FENCE);
    //
    if (gid == 0) {
      for (int t=0;t<MAX-1;t++) {
        for (int u=0;u<MAX-t-1;u++) {
          if (local_data[u] > local_data[u+1]) {
            tmp = local_data[u];
            local_data[u] = local_data[u+1];
            local_data[u+1] = tmp;
          } // bubble if and swap
        } // bubble B
      } // bubble A
    }
    //
    barrier(CLK_LOCAL_MEM_FENCE);
    //
    for (int g=0; g<MAX; g++) {
      d[(group_id*MAX)+g] = local_data[g];
    }
}
