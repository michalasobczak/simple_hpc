void bin_prnt_byte(uchar x, uchar str[]) {
   int j=0;
   int i;
   for (i=0; i<8; i++) {
      if ((x & 0x80) !=0) {
         str[j] = '1';
      } else {
         str[j] = '0';
      }
      x = x<<1;
      j++;
   }
} // bin_prnt_byte


// Takes 8 char values holding consecutive value characters
void print_uchar_arr_as_decimals(uchar arr[]) {
    printf("decimal: %u %u %u %u %u %u %u %u \n", arr[0], arr[1], arr[2], arr[3], arr[4], arr[5], arr[6], arr[7]);
} // print_decimal


void print_uchar_arr_as_binaries(uchar arr[]) {
    int i;
    for (i=0; i<8; i++) {
        uchar str[8];
        bin_prnt_byte(arr[i], str);
        printf("binary %u: %c %c %c %c %c %c %c %c\n", i, str[0], str[1], str[2], str[3], str[4], str[5], str[6], str[7]);
    } // for
} // print_binary


void print_uchar_arr(uchar arr[], uchar size) {
    int i;
    for (int i=0; i<size; i++) {
        printf("%c", arr[i]);
    }
    printf("\n");
}


uchar* convert_8decimals_into_bs(uchar arr[], __private uchar result[]) {
    int j = 0;
    int i;
    for (i=0; i<8; i++) {
        uchar str[8];
        bin_prnt_byte(arr[i], str);
        int k;
        for (k=0; k<=7; k++) {
            result[(i*8)+k] = str[k];
        }
        j++;
    } // for
    return result;
}


void shift_left_once(uchar arr[]) {
    uchar tmp1 = arr[0];
    int i;
	for (i = 1; i < 28; i++) {
		 arr[i-1] = arr[i];
	} // for
	arr[27] = tmp1;
} // shift_left_once


void Xor(uchar a[], uchar b[], uchar result[], int size) {
    int i;
	for (i = 0; i < size; i++) {
		if (a[i] != b[i]){
			result[i] = '1';
		}
		else {
			result[i] = '0';
		}
	} // for
} //Xor


int convert_binary_to_decimal(uchar binaryChar[], int length) {
    int multiplier = 0;
    int i;
    int sum = 0;
    for(i=length-1;i>=0;i--) {
        sum = sum + ((binaryChar[i]-48) * pow(2,(double)multiplier) );
        multiplier = multiplier + 1;
    }
    //printf("sum = %i \n", sum);
    return sum;
}


void convert_decimal_to_binary(int n, uchar result[]) {
    int a[10], i;
    int res[4] = {0, 0, 0, 0};
    int c = 0;
    // convert
    for (i=0; n>0; i++) {
        a[i]= n % 2;
        n = n / 2;
        c++;
    }
    // map
    int resc = 0;
    for (i=i-1; i>=0; i--) {
        res[(4-c)+resc] = a[i];
        resc++;
    }
    // print repr.
    for (int j=0; j<4; j++) {
        if (res[j] == 0) {
            result[j] = '0';
        }
        else if (res[j] == 1) {
            result[j] = '1';
        }
    }
    //printf("result = %c %c %c %c \n", result[0], result[1], result[2], result[3]);
}


int convert_bs_part_to_decimal(uchar bs[], int start) {
    uchar tmp[8];
    int c = 0;
    for (int i=start; i<start+8; i++) {
        tmp[c] = bs[i];
        c++;
    }
    //print_uchar_arr(tmp, 8);
    int result = convert_binary_to_decimal(tmp, 8);
    //printf("result = %u '%c' ", result, result);
    return result;
}


// ---------------------------------------------------------------------------------------------------------------------
// FP01
__kernel void sampleKernel(__global const uchar8* src,
                           __global       uchar8* dst)
{
    // --- DATA ---

    // FP02
    uchar gc, ls, gs, gid, lid;
    uchar8 current;
    // FP03
    const uchar key[8] =  {0, 255, 255, 255, 4, 5, 100, 255}; // key (decimal)
    uchar key_bs[64];
    uchar round_keys[16][48];
    // FP04
    //const uchar pt_str[8] =  {48, 49, 50, 51, 52, 53, 54, 55}; // plain text (decimal ASCI)
    uchar pt_str[8]; // plain text (decimal ASCI)
    uchar pt[64];    // plain text
    // FP05: The PC1/PC2 table
    const uchar pc1[56] = {
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
    const uchar pc2[48] = {
        14,17,11,24,1,5,
        3,28,15,6,21,10,
        23,19,12,4,26,8,
        16,7,27,20,13,2,
        41,52,31,37,47,55,
        30,40,51,45,33,48,
        44,49,39,56,34,53,
        46,42,50,36,29,32
    };
    // FP06
    uchar perm_key[56];
    uchar iperm = 0;
    uchar index;
    // FP07
    uchar left[28];
    uchar _right[28];
    uchar c = 0;
    // FP08
    uchar i;
    // FP10
    uchar combined_key[56];
    uchar j;
    // FP11
    uchar tmp_round_key[48];
    uchar k, m;
    // FP12: The initial permutation table
    const uchar initial_permutation[64] = {
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
    const uchar expansion_table[48] = {
        32,1,2,3,4,5,4,5,
        6,7,8,9,8,9,10,11,
        12,13,12,13,14,15,16,17,
        16,17,18,19,20,21,20,21,
        22,23,24,25,24,25,26,27,
        28,29,28,29,30,31,32,1
    };
    // FP13: The substitution boxes. Should contain values from 0 to 15 in any order.
    const uchar substition_boxes[8][4][16] =
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
    // FP14: The permutation table
    const uchar permutation_tab[32] = {
        16,7,20,21,29,12,28,17,
        1,15,23,26,5,18,31,10,
        2,8,24,14,32,27,3,9,
        19,13,30,6,22,11,4,25
    };
    // The inverse permutation table
    const uchar inverse_permutation[64]= {
        40,8,48,16,56,24,64,32,
        39,7,47,15,55,23,63,31,
        38,6,46,14,54,22,62,30,
        37,5,45,13,53,21,61,29,
        36,4,44,12,52,20,60,28,
        35,3,43,11,51,19,59,27,
        34,2,42,10,50,18,58,26,
        33,1,41,9,49,17,57,25
    };
    // FP15
    uchar perm[64];
    uchar ip;
    // FP16
    uchar ptleft[32];
    uchar ptright[32];
    // FP17
    uchar h;
    uchar right_expanded[48];
    // FP18
    uchar xored[48];
    uchar res[32];
    // FP19
    uchar g;
    uchar row1[2];
    uchar row, col, val;
    uchar col1[4];
    uchar tmp[4] = {'0', '0', '0', '0'};
    // FP20
    uchar perm2[32];
    // FP21
    uchar xored2[32];
    // FP22
    uchar temp[32];
    uchar d, e, t, y;
    // FP23
    uchar combined_text[64]; int ctc = 0;
    uchar x, z;
    // FP24
    uchar u;
    // FP25
    uchar tmpchar[8] = {'0', '0', '0', '0', '0', '0', '0', '0'};
    uchar8 finalresult;

    // -- PROCEDURE

    // FP02
    //printf("FP02: init\n");
    //gc  = get_num_groups(0);
    //ls  = get_local_size(0);
    //gs  = gc*ls;
    gid = get_global_id(0);
    //lid = get_local_id(0);
    current = src[gid];
    //printf("lid:%u, gid:%u, value: %u, %u, %u, %u, %u, %u, %u, %u \n\n", lid, gid, current.s0, current.s1, current.s2, current.s3, current.s4, current.s5, current.s6, current.s7);

    // FP03
    //printf("FP03: key \n");
    //print_uchar_arr_as_decimals(key);
    //print_uchar_arr_as_binaries(key);
    convert_8decimals_into_bs(key, key_bs); // binary string, uchar* arr
    //printf("key_bs: "); print_uchar_arr(key_bs, 64);
    //printf("\n\n");

    // FP04
    //printf("FP04: pt \n");
    pt_str[0] = current.s0;
    pt_str[1] = current.s1;
    pt_str[2] = current.s2;
    pt_str[3] = current.s3;
    pt_str[4] = current.s4;
    pt_str[5] = current.s5;
    pt_str[6] = current.s6;
    pt_str[7] = current.s7;
    convert_8decimals_into_bs(pt_str, pt); // plain text (binary string)
    //printf("pt in bs: "); print_uchar_arr(pt, 64);
    //printf("\n\n");

    // FP06
	//printf("PERM_KEY: ");
	for (iperm = 0; iperm < 56; iperm++) {
	    index = pc1[iperm]-1;
		perm_key[iperm] = key_bs[index];
	} // for
	//print_uchar_arr(perm_key, 56);

    // FP07: Dividing the key into two equal halves
    for (int i=0; i<28; i++) { left[i]  = perm_key[i]; } //printf("LEFT:  "); print_uchar_arr(left, 28);
    for (int i=28; i<56; i++) { _right[c] = perm_key[i]; c++; }; //printf("RIGHT: "); print_uchar_arr(_right, 28);

    // FP08: For rounds 1, 2, 9, 16 key_chunks are shifted by one.
    for (i=0; i<16; i++) {
        if (i == 0 || i == 1 || i==8 || i==15) {
            shift_left_once(left);   //print_uchar_arr(left, 28);
            shift_left_once(_right); //print_uchar_arr(_right, 28);
        }

        // FP09: For other rounds, key_chunks are shifted by two
        else {
            shift_left_once(left); shift_left_once(left);
            shift_left_once(_right); shift_left_once(_right);
            //print_uchar_arr(left, 28);
            //print_uchar_arr(_right, 28);
        }

        // FP10: Combining the two chunks
        for (j=0; j<28; j++) { combined_key[j] = left[j]; }
        c = 0; for (j=28; j<56; j++) { combined_key[j] = _right[c]; c++; }
        //printf("combined: %u: ", i); print_uchar_arr(combined_key, 56);

        // FP11: using PC2 table to transpose  key bits
        for (k = 0; k < 48; k++) {
            tmp_round_key[k] = combined_key[pc2[k]-1];
        }
        for (m=0; m<48; m++) {
            round_keys[i][m] = tmp_round_key[m];
        }
        //printf("round_key %u: ", i); print_uchar_arr(round_keys[i], 48);

    } // for round_keys

    // FP12-14: data

    // ~~~ DES encryption ~~~
    //printf("\n--> Encryption\n");

    // FP15: Applying the initial permutation
    //printf("pt: "); print_uchar_arr(pt, 64);
    for (ip = 0; ip < 64; ip++){
        perm[ip] = pt[initial_permutation[ip]-1];
    }
    //printf("perm initial permutation applied: "); print_uchar_arr(perm, 64);

    // FP16: Dividing the result into two equal halves
    for (i=0; i<32; i++) { ptleft[i]  = perm[i]; }; //printf("LEFT:  "); print_uchar_arr(ptleft, 28);
    c = 0; for (i=32; i<64; i++) { ptright[c] = perm[i]; c++; }; //printf("RIGHT: "); print_uchar_arr(ptright, 28);

    // FP17: The plain text is encrypted 16 times
    for (i=0; i<16; i++) {
		// The right half of the plain text is expanded
    	for (h = 0; h < 48; h++) {
      		right_expanded[h] = ptright[expansion_table[h]-1];
    	}
    	//printf("right_expanded: "); print_uchar_arr(right_expanded, 48);

    	// FP18: The result is xored with a key
        Xor(round_keys[i], right_expanded, xored, 48);
        //printf("xored: "); print_uchar_arr(xored, 48);

        // FP19: The result is divided into 8 equal parts and passed
        // through 8 substitution boxes. After passing through a
        // substituion box, each box is reduces from 6 to 4 bits.
        for (g=0;g<8; g++) {
            // Finding row and column indices to lookup the
            // substituition box
            row1[0] = xored[(g*6)];
            row1[1] = xored[(g*6)+5];
            //printf("row1: %c %c \n", row1[0], row1[1]);
            row = convert_binary_to_decimal(row1, 2);
            col1[0] = xored[(g*6) + 1];
            col1[1] = xored[(g*6) + 2];
            col1[2] = xored[(g*6) + 3];
            col1[3] = xored[(g*6) + 4];
            col = convert_binary_to_decimal(col1, 4);
            val = substition_boxes[g][row][col];
            convert_decimal_to_binary(val, tmp);
            //printf("val = %i ", val); print_uchar_arr(tmp, 4);
            for (h=0; h<4; h++) {
                res[(g*4) + h] = tmp[h];
            }
        } // for
        //printf("res = "); print_uchar_arr(res, 32);

        // FP20 Another permutation is applied
        for (int i = 0; i < 32; i++){
        	perm2[i] = res[permutation_tab[i]-1];
        }
        // FP21: The result is xored with the left half
        Xor(perm2, ptleft, xored2, 32);
        //printf("xored2: "); print_uchar_arr(xored2, 32);

        // FP22: The left and the right parts of the plain text are swapped
        for (d=0; d<32; d++) {
            ptleft[d] = xored2[d];
        }
        //printf("ptleft: "); print_uchar_arr(ptleft, 32);
        if (i < 15) {
            for (e=0; e<32; e++) {
                temp[e] = ptright[e];
            }
            //printf("temp: "); print_uchar_arr(temp, 32);
            for (t=0; t<32; t++) {
                ptright[t] = xored2[t];
            }
            //printf("ptright: "); print_uchar_arr(ptright, 32);
            for (y=0; y<32; y++) {
                ptleft[y] = temp[y];
            }
            //printf("ptleft: "); print_uchar_arr(ptleft, 32);
        }
        //printf("\n");
    } // for, 16 times enc

    // FP23: The halves of the plain text are applied
    for (int z=0; z<32; z++)  { combined_text[z] = ptleft[z]; }
    for (int x=32; x<64; x++) { combined_text[x] = ptright[ctc]; ctc++; }
    uchar ciphertext[64];

    // FP24: The inverse of the initial permuttaion is applied
    for (u = 0; u < 64; u++) {
        ciphertext[u] = combined_text[inverse_permutation[u]-1];
    }
    //printf("ciphertext = "); print_uchar_arr(ciphertext, 64);

    // FP25: final result
    finalresult.s0 = convert_bs_part_to_decimal(ciphertext, 0);
    finalresult.s1 = convert_bs_part_to_decimal(ciphertext, 8);
    finalresult.s2 = convert_bs_part_to_decimal(ciphertext, 16);
    finalresult.s3 = convert_bs_part_to_decimal(ciphertext, 24);
    finalresult.s4 = convert_bs_part_to_decimal(ciphertext, 32);
    finalresult.s5 = convert_bs_part_to_decimal(ciphertext, 40);
    finalresult.s6 = convert_bs_part_to_decimal(ciphertext, 48);
    finalresult.s7 = convert_bs_part_to_decimal(ciphertext, 56);

    // FP26: write to output buffer
    dst[gid] = finalresult;
} // kernel