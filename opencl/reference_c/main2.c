#include <stdio.h>
#include <stdlib.h>

void first(int *ptr) {  
  *ptr = 2;
}

int main(int argc, char *argv[]) {
    int *ptr1;
    ptr1 = 0;
    printf("%p\n", ptr1);
    first(&ptr1);
    printf("%p\n", ptr1);
	return 0;
}
