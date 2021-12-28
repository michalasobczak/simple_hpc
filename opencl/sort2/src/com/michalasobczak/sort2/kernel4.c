__kernel void sampleKernel(__global const float *a, __global float *d, __local float *local_data) {
    __private int ls  = get_local_size(0);
    __private int gid = get_global_id(0);
    __private int lid = get_local_id(0);
    __private int group_id = get_group_id(0);
    float tmp = 0.0f;
    local_data[lid] = a[gid];
    barrier(CLK_LOCAL_MEM_FENCE);
    // step -> ls, 32, 16, 8, 4, 2
    for (int step=ls; step>=2; step=step/2) {
      // UWAGA:
      // Tylko dla elementów zerowych w danych podgrupach grup roboczych
      if (lid % step == 0) {
        //printf("group_id=%u, lid=%u, step=%u\n", group_id, lid, step);
        for (int e=lid; e<lid+step; e++) {
          if (step == ls && (e-lid)<step/2) {
            if (local_data[e] > local_data[e+(step/2)]) {
              tmp = local_data[e];
              local_data[e] = local_data[e+(step/2)];
              local_data[e+(step/2)] = tmp;
            }
            //printf(">>gid=%u,e=%u -> %f\n", gid, e, local_data[e]);
          }
        } // e/lid for
      } // zero element
      // UWAGA:
      // Nie ma potrzeby aby elementy inne niż 0 czekały,
      // i tak nic więcej nie będą robiły
      // chyba, że chcemy używać printf
      barrier(CLK_LOCAL_MEM_FENCE);
    } // step for
    //
    // Uwaga:
    // Na koniec dla 0 elementu
    if (lid == 0) {
      for (int g=0; g<ls; g++) {
        d[(group_id*ls)+g] = local_data[g];
      }
    }
}
