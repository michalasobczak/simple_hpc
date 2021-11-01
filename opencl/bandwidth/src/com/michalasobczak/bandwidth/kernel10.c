void foo(int *ptr, int _gid) {
  *ptr = *ptr + _gid;
}

__kernel void sampleKernel(__global const float *a, __global float *d) {
  __private int gid = get_global_id(0);
  __private int group_id = get_group_id(0);
  local int *ptr = 0;
  local int var;
  var = 2;
  ptr = &var;
  foo(ptr, gid);
  printf("%i, %p, %i, %i\n", gid, ptr, (int)*ptr, var);
  d[gid] = 0.1;
}
