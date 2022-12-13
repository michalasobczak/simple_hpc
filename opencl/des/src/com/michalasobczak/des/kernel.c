uchar* bin_prnt_byte(uchar x) {
   uchar str[8];
   int j=0;
   for (int n=0; n<8; n++) {
      if ((x & 0x80) !=0) {
         str[j] = '1';
      } else {
         str[j] = '0';
      }
      x = x<<1;
      j++;
   }
   //printf("my: %s \n", str);
   return str;
} // bin_prnt_byte


// Takes 8 char values holding consecutive value characters
void print_uchar_arr_as_decimals(uchar arr[]) {
    printf("decimal: %u %u %u %u %u %u %u %u \n", arr[0], arr[1], arr[2], arr[3], arr[4], arr[5], arr[6], arr[7]);
} // print_decimal


void print_uchar_arr_as_binaries(uchar arr[]) {
    for (int i=0; i<=7; i++) {
        uchar* str = bin_prnt_byte(arr[i]);
        printf("binary %u: %c %c %c %c %c %c %c %c\n", i, str[0], str[1], str[2], str[3], str[4], str[5], str[6], str[7]);
    } // for
} // print_binary


uchar* convert_8decimals_into_bs(uchar arr[]) {
    uchar result[64];
    int j = 0;
    for (int i=0; i<=7; i++) {
        uchar* str = bin_prnt_byte(arr[i]);
        for (int k=0; k<=7; k++) {
            result[(i*8)+k] = str[k];
        }
        j++;
    } // for
    //printf("bs: %s\n", result);
    return result;
}

void print_uchar_arr56(uchar arr[]) {
    for (int k=0; k<56; k++) {
        printf("%c", arr[k]);
    }
    printf("\n");
}

void print_uchar_arr48(uchar arr[]) {
    for (int k=0; k<48; k++) {
        printf("%c", arr[k]);
    }
    printf("\n");
}

void print_uchar_arr28(uchar arr[]) {
    for (int k=0; k<28; k++) {
        printf("%c", arr[k]);
    }
    printf("\n");
}

void shift_left_once(uchar arr[]) {
    uchar tmp1 = arr[0];
	for (int i = 1; i < 28; i++) {
		 arr[i-1] = arr[i];
	} // for
	arr[27] = tmp1;
} // shift_left_once


__kernel void sampleKernel(__global const uchar8* src,
                           __global       uchar8* dst)
{
    __private int gc  = get_num_groups(0);
    __private int ls  = get_local_size(0);
    __private int gs  = gc*ls;
    __private int gid = get_global_id(0);
    __private int lid = get_local_id(0);
    __private uchar8 current = src[gid];
    printf("lid:%u, gid:%u, value: %u, %u, %u, %u, %u, %u, %u, %u \n", lid, gid, current.s0, current.s1, current.s2, current.s3,
                                                                                 current.s4, current.s5, current.s6, current.s7);
    //
    // DES key
    //
    printf("KEY:\n");
    const uchar key[8]    =  {0, 255, 255, 255, 4, 5, 100, 255};   // key (decimal)
    const uchar pt_str[8] =  {48, 49, 50, 51, 52, 53, 54, 55};     // plain text (decimal ASCI)
    uchar* pt[64]         =  convert_8decimals_into_bs(key);       //
    print_uchar_arr_as_decimals(key);
    print_uchar_arr_as_binaries(key);
    uchar* key_bs = convert_8decimals_into_bs(key);  // binary string, uchar* arr
    uchar round_keys[16][48];
    //
    // generate keys
    // The PC1 table
    int pc1[56] = {
        57,49,41,33,25,17,9,
        1,58,50,42,34,26,18,
        10,2,59,51,43,35,27,
        19,11,3,60,52,44,36,
        63,55,47,39,31,23,15,
        7,62,54,46,38,30,22,
        14,6,61,53,45,37,29,
        21,13,5,28,20,12,4
    };
    // The PC2 table
    int pc2[48] = {
        14,17,11,24,1,5,
        3,28,15,6,21,10,
        23,19,12,4,26,8,
        16,7,27,20,13,2,
        41,52,31,37,47,55,
        30,40,51,45,33,48,
        44,49,39,56,34,53,
        46,42,50,36,29,32
    };
    //
	// 1. Compressing the key using the PC1 table
	uchar perm_key[56];
	printf("PERM_KEY: ");
	for (int i = 0; i < 56; i++) {
		perm_key[i] = key_bs[pc1[i]-1];
		printf("%c", perm_key[i]);
	} // for
	printf("\n");
    //
	// 2. Dividing the key into two equal halves
	uchar left[28]; for (int i=0; i<28; i++) { left[i]  = perm_key[i]; } //printf("LEFT:  "); print_uchar_arr28(left);
    uchar _right[28]; int c = 0; for (int i=28; i<56; i++) { _right[c] = perm_key[i]; c++; }; //printf("RIGHT: "); print_uchar_arr28(_right);
    //
    for (int i=0; i<16; i++) {
        // 3.1. For rounds 1, 2, 9, 16 the key_chunks
        // are shifted by one.
        if (i == 0 || i == 1 || i==8 || i==15) {
            shift_left_once(left);   //print_uchar_arr28(left);
            shift_left_once(_right); //print_uchar_arr28(_right);
        }
        // 3.2. For other rounds, the key_chunks
        // are shifted by two
        else {
            //left= shift_left_twice(left);
            shift_left_once(left); shift_left_once(left);
            shift_left_once(_right); shift_left_once(_right);
            //print_uchar_arr28(left);
            //print_uchar_arr28(_right);
        }
        // Combining the two chunks
        uchar combined_key[56];
        for (int j=0; j<28; j++) { combined_key[j] = left[j]; }
        int c = 0; for (int j=28; j<56; j++) { combined_key[j] = _right[c]; c++; }
        //printf("combined: %u: ", i); print_uchar_arr56(combined_key);
        //
        // Finally, using the PC2 table to transpose the key bits
        uchar tmp_round_key[48];
        for (int k = 0; k < 48; k++) {
            tmp_round_key[k] = combined_key[pc2[k]-1];
        }
        for (int m=0; m<48; m++) {
            round_keys[i][m] = tmp_round_key[m];
        }
        printf("round_key %u: ", i); print_uchar_arr48(round_keys[i]);
    } // for round_keys
    //
    // DES
    //
    // The initial permutation table
    int initial_permutation[64] = {
        58,50,42,34,26,18,10,2,
        60,52,44,36,28,20,12,4,
        62,54,46,38,30,22,14,6,
        64,56,48,40,32,24,16,8,
        57,49,41,33,25,17,9,1,
        59,51,43,35,27,19,11,3,
        61,53,45,37,29,21,13,5,
        63,55,47,39,31,23,15,7
    };
    // The expansion table
    int expansion_table[48] = {
        32,1,2,3,4,5,4,5,
        6,7,8,9,8,9,10,11,
        12,13,12,13,14,15,16,17,
        16,17,18,19,20,21,20,21,
        22,23,24,25,24,25,26,27,
        28,29,28,29,30,31,32,1
    };
    // The substitution boxes. The should contain values
    // from 0 to 15 in any order.
    int substition_boxes[8][14][16] =
    {{
        {14,4,13,1,2,15,11,8,3,10,6,12,5,9,0,7},
        {0,15,7,4,14,2,13,1,10,6,12,11,9,5,3,8},
        {4,1,14,8,13,6,2,11,15,12,9,7,3,10,5,0},
        {15,12,8,2,4,9,1,7,5,11,3,14,10,0,6,13}
    },
    {
        {15,1,8,14,6,11,3,4,9,7,2,13,12,0,5,10},
        {3,13,4,7,15,2,8,14,12,0,1,10,6,9,11,5},
        {0,14,7,11,10,4,13,1,5,8,12,6,9,3,2,15},
        {13,8,10,1,3,15,4,2,11,6,7,12,0,5,14,9}
    },
    {
        {10,0,9,14,6,3,15,5,1,13,12,7,11,4,2,8},
        {13,7,0,9,3,4,6,10,2,8,5,14,12,11,15,1},
        {13,6,4,9,8,15,3,0,11,1,2,12,5,10,14,7},
        {1,10,13,0,6,9,8,7,4,15,14,3,11,5,2,12}
    },
    {
        {7,13,14,3,0,6,9,10,1,2,8,5,11,12,4,15},
        {13,8,11,5,6,15,0,3,4,7,2,12,1,10,14,9},
        {10,6,9,0,12,11,7,13,15,1,3,14,5,2,8,4},
        {3,15,0,6,10,1,13,8,9,4,5,11,12,7,2,14}
    },
    {
        {2,12,4,1,7,10,11,6,8,5,3,15,13,0,14,9},
        {14,11,2,12,4,7,13,1,5,0,15,10,3,9,8,6},
        {4,2,1,11,10,13,7,8,15,9,12,5,6,3,0,14},
        {11,8,12,7,1,14,2,13,6,15,0,9,10,4,5,3}
    },
    {
        {12,1,10,15,9,2,6,8,0,13,3,4,14,7,5,11},
        {10,15,4,2,7,12,9,5,6,1,13,14,0,11,3,8},
        {9,14,15,5,2,8,12,3,7,0,4,10,1,13,11,6},
        {4,3,2,12,9,5,15,10,11,14,1,7,6,0,8,13}
    },
    {
        {4,11,2,14,15,0,8,13,3,12,9,7,5,10,6,1},
        {13,0,11,7,4,9,1,10,14,3,5,12,2,15,8,6},
        {1,4,11,13,12,3,7,14,10,15,6,8,0,5,9,2},
        {6,11,13,8,1,4,10,7,9,5,0,15,14,2,3,12}
    },
    {
        {13,2,8,4,6,15,11,1,10,9,3,14,5,0,12,7},
        {1,15,13,8,10,3,7,4,12,5,6,11,0,14,9,2},
        {7,11,4,1,9,12,14,2,0,6,10,13,15,3,5,8},
        {2,1,14,7,4,10,8,13,15,12,9,0,3,5,6,11}
    }};
    //
    // The permutation table
    int permutation_tab[32] = {
        16,7,20,21,29,12,28,17,
        1,15,23,26,5,18,31,10,
        2,8,24,14,32,27,3,9,
        19,13,30,6,22,11,4,25
    };
    // The inverse permutation table
    int inverse_permutation[64]= {
        40,8,48,16,56,24,64,32,
        39,7,47,15,55,23,63,31,
        38,6,46,14,54,22,62,30,
        37,5,45,13,53,21,61,29,
        36,4,44,12,52,20,60,28,
        35,3,43,11,51,19,59,27,
        34,2,42,10,50,18,58,26,
        33,1,41,9,49,17,57,25
    };
    //
    //1. Applying the initial permutation
    uchar perm[64];
    for (int i = 0; i < 64; i++){
        perm += pt[initial_permutation[i]-1];
    }
    //
    dst[gid] = current;
} // kernel
