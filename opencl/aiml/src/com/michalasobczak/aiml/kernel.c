#pragma OPENCL EXTENSION cl_khr_fp64 : enable

// ---------------------------------------------------------------------------------------------------------------------
// FP01
__kernel void sampleKernel(__global const uchar*  srcA,
                           __global const int2*   srcB,
                           __global       uchar*  dst)
{
    int gc, ls, gs, gid, lid;
    int c, d;
    int res = 0;
    __private uchar current[500]    = { '\0' };
    __private uchar word_buffer[30] = { '\0' };
    // -- PROCEDURE
    gid = get_global_id(0);
    int2 val = srcB[gid];
    d = 0;
    // prepare current local item
    for (c=val.s0; c<val.s0+val.s1; c++) {
        current[d] = srcA[c];
        d++;
    }
    uchar tmp[10] = "Bible\0";
    set_string(word_buffer, tmp);
    res = find_string_in_string(srcA, word_buffer);
    dst[gid] = res;
} // kernel
