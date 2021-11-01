#include <stdio.h>
#include <stdlib.h>

void first(int *ptr) {  
  (*ptr)++;
}

int main(int argc, char *argv[]) {
	int var = 0;
	int *ptr = &var;
	printf("ptr: %p, %i, %i\n", ptr, *ptr, var);
	*ptr = *ptr + 2;
	printf("ptr: %p, %i, %i\n", ptr, *ptr, var);
	first(ptr);
	printf("ptr: %p, %i, %i\n", ptr, *ptr, var);
	return 0;
}
