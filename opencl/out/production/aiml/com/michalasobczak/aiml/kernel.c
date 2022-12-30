#pragma OPENCL EXTENSION cl_khr_fp64 : enable

// ---------------------------------------------------------------------------------------------------------------------
// FP01
__kernel void sampleKernel(__global const uchar* srcA,
                           __global const int2*   srcB,
                           __global       uchar*  dst)
{
    // --- DATA ---

    // FP02
    int gc, ls, gs, gid, lid;
    int c, d;
    int res = 0;
    __private uchar current[130000]    = { '\0' };
    __private uchar word_buffer[30] = { '\0' };

    // -- PROCEDURE
    gid = get_global_id(0);
    int2 val = srcB[gid];
    d = 0;
    for (c=val.s0; c<val.s0+val.s1; c++) {
        //printf("%c", srcA[c]);
        current[d] = srcA[c];
        d++;
    }
    //if (gid == 1 00) {
        //printf("START:");
        //for (c=0; c<250; c++) {
        //    if (current[c] != NULL)
        //        printf("%c", current[c]);
        //}
        //printf(":END\n");

        uchar tmp[10] = "LORD\0";
        set_string(word_buffer, tmp);
        res = find_string_in_string(current, word_buffer);

        //print_string(tmp);
        //set_string_and_print(word_buffer, tmp2);
        //printf("\n%c %c\n", word_buffer[0], word_buffer[1]);
        //printf("word_buffer = ");
        //find_in_string(current, 10, word_buffer, 10);


    //}
    //printf("\nsize for me: %i: %i %i\n", gid, val.s0, val.s1);

    // write to output buffer
    dst[gid] = res;
} // kernel
