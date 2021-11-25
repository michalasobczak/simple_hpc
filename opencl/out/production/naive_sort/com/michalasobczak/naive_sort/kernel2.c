void sort_local(__local float *tab) {
  if (tab[0] > tab[1]) {
    float tmp = tab[0];
    tab[0] = tab[1];
    tab[1] = tmp;
  }
}

int find_power_of_2(int number) {
  int tmp = number;
  int count = 0;
  while (tmp > 1) {
    tmp = tmp/2;
    count = count + 1;
  }
  return count;
}

void preview_output(__global float *d) {
  __private int gc  = get_num_groups(0);
  __private int ls  = get_local_size(0);
  __private int gs  = gc*ls;
  for (int i=0; i<gs; i++) {
    printf("%f ", d[i]);
  }
  printf(".\n");
}

__kernel void sampleKernel(__global const float *a, __global float *d, __local float *partial_sums) {
    __private int gc  = get_num_groups(0);
    __private int ls  = get_local_size(0);
    __private int gs  = gc*ls;
    __private int gid = get_global_id(0);
    __private int lid = get_local_id(0);
    __private int group_id = get_group_id(0);

    int power_count = find_power_of_2(gs);

    //printf("%u, %u\n", gc, ls);
    //printf("%u\n", power_count);

    partial_sums[lid] = a[gid];

    barrier(CLK_LOCAL_MEM_FENCE);

    //__local float sub[LOCAL];
    //sub[0] = a[(group_id*LOCAL)+0];
    //sub[1] = a[(group_id*LOCAL)+1];
    //sort_local(sub);
    //printf("1.  %u, %f %f\n", gid, sub[0], sub[1]);

    //for (int i=0; i<power_count; i++) {
    for (int i=gs/2; i>0; i >>= 1) {
      //preview_output(d);
      if (lid < i) {
        //printf("step %u -> %u %u %f\n", i, gid, lid, a[gid]);
        partial_sums[lid] += partial_sums[lid + i];
      }
      barrier(CLK_LOCAL_MEM_FENCE);
    }

    if (lid == 0) {
      d[group_id] = partial_sums[0];
    }
}
