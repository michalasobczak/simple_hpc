#include <stdio.h>
#include <stdlib.h>

void first(int *ptr) {  
  (*ptr)++;
}

int main(int argc, char *argv[]) {
	int var = 0;
	int *ptr = &var;
	printf("ptr: %i, %i\n", ptr, *ptr);
	*ptr = *ptr + 2;
	printf("ptr: %i, %i\n", ptr, *ptr);
	first(ptr);
	printf("ptr: %i, %i\n", ptr, *ptr);
	return 0;
}
