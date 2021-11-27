__kernel void sampleKernel(__global const float *a, __global float *d, __local float *local_data) {
    __private int ls  = get_local_size(0);
    __private int gid = get_global_id(0);
    __private int lid = get_local_id(0);
    __private int group_id = get_group_id(0);
    float tmp = 0.0f;
    float per_element = 0.0f;
    int counter = 0;
    local_data[lid] = a[gid];
    barrier(CLK_LOCAL_MEM_FENCE);
    // step 2, 4, 8, 16, 32 itd.
    for (int step=2; step<=ls; step=step*2) {
      // UWAGA:
      // Tylko dla elementów zerowych w danych podgrupach grup roboczych
      if (lid % step == 0) {
        counter = 1;
        //printf("group_id=%u, lid=%u, step=%u\n", group_id, lid, step);
        for (int e=lid; e<lid+step; e++) {
          // Uwaga:
          // Dla podgrup 2 elementowych wykonujemy sort-swap
          if (step == 2 && e % 2 == 0) {
            if (local_data[e] > local_data[e+1]) {
              tmp = local_data[e];
              local_data[e] = local_data[e+1];
              local_data[e+1] = tmp;
            }
            //printf(">gid=%u,e=%u -> %f (%f,%f,%u,%u,%u)\n", gid, e, local_data[e], per_element, per_element*counter, counter, step, lid);
          }
          else if (step == 2) {
            //printf(".gid=%u,e=%u -> %f (%f,%f,%u,%u,%u)\n", gid, e, local_data[e], per_element, per_element*counter, counter, step, lid);
          }
          // Uwaga:
          // Dla podgrup o większym rozmiarze od 4 do rozmiaru lokalnego
          // wykonujemy inny typ szeregowania danych
          else if (step >= 4 && step < ls && (e-lid)<step/2) {
            if (local_data[e] > local_data[e+(step/2)]) {
              tmp = local_data[e];
              local_data[e] = local_data[e+(step/2)];
              local_data[e+(step/2)] = tmp;
            }
            per_element = 1.0/step;
            //printf(">>gid=%u,e=%u -> %f (%f,%f,%u,%u,%u)\n", gid, e, local_data[e], per_element, per_element*counter, counter, step, lid);
          }
          else if (step >= 4) {
            //printf("..gid=%u,e=%u -> %f (%f,%f,%u,%u,%u)\n", gid, e, local_data[e], per_element, per_element*counter, counter, step, lid);
          }
          counter = counter + 1;
        } // e/lid for
        // BUBBLE
        if (step == ls) {
          for (int t=0;t<step-1;t++) {
            for (int u=0;u<step-t-1;u++) {
              if (local_data[u] > local_data[u+1]) {
                tmp = local_data[u];
                local_data[u] = local_data[u+1];
                local_data[u+1] = tmp;
              } // bubble if and swap
            } // bubble B
          } // bubble A
        } // final step
      } // zero element
      // UWAGA:
      // Nie ma potrzeby aby elementy inne niż 0 czekały,
      // i tak nic więcej nie będą robiły
      // chyba, że chcemy używać printf
      barrier(CLK_LOCAL_MEM_FENCE);
    } // step for
    // Uwaga:
    // Na koniec dla 0 elementu
    if (lid == 0) {
      for (int g=0; g<ls; g++) {
        d[(group_id*ls)+g] = local_data[g];
      }
    }
}
