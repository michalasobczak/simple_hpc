#define MAX_STRING_SIZE 4500000

int get_string_size(uchar string[]) {
    int size;
    int c;
    int counter = 0;
    for (c=0; c<MAX_STRING_SIZE; c++) {
        if (string[c] == NULL) {
            break;
        }
        counter++;
    }
    //printf("returning size = %i\n", counter);
    return counter;
}


void print_string(uchar stringa[]) {
    int size = get_string_size(stringa);
    int c;
    //printf("size = %i\n", size);
    for (c=0; c<size; c++) {
        printf("%c", stringa[c]);
    }
    //printf("c = %c", stringa[0]);
    printf("\n");
}


void set_string(uchar wb[], uchar tmp[]) {
    // set
    int c;
    int size = get_string_size(tmp);
    for (c=0; c<size; c++) {
        wb[c] = tmp[c];
    }
    // print
    //printf("size = %i\n", size);
    //for (c=0; c<size; c++) {
    //    printf("%c", wb[c]);
    //}
    //printf("\n");
}


void set_string_and_print(uchar wb[], uchar tmp[]) {
    set_string(wb, tmp);
    print_string(wb);
}


int find_string_in_string(uchar source[], uchar looking_for[]) {
    int s_size = get_string_size(source);
    int lf_size = get_string_size(looking_for);
    int c, d;
    for (c=0; c<s_size; c++) {
        //printf("current source position = %i %c\n", c, source[c]);
        for (d=0; d<lf_size; d++) {
            if (source[c+d] == looking_for[d]) {
                ;
            }
            else {
                break;
            }
            if (d == lf_size-1) {
                //printf("FOUND from %i to %i : %s\n", c, c+d, source);
                //printf("FOUND from %i to %i \n", c, c+d);
                return 1;
            }
        } // for within looking_for
    } // for within source
    //printf("\n");
    return 0;
}