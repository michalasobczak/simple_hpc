#include <stdio.h>
#include <stdlib.h>

void first(int **ptr) {  
  (*ptr)++;
}

int main(int argc, char *argv[]) {
    int *ptr1;
    ptr1 = 0;
    first(&ptr1);
	return 0;
}
