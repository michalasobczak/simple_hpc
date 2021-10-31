#include <stdio.h>
#include <stdlib.h>

/* run this program using the console pauser or add your own getch, system("pause") or input loop */


void first(int* ptr) {  
  *ptr = 2;
}

int main(int argc, char *argv[]) {
	int ptr1 = 0;
	int *ptr2 = &ptr1;
	//ptr2 = &ptr1;
	first(&ptr1);
	printf("ptr1: %i\n", ptr1);
	printf("ptr1: %i\n", *ptr2);
	return 0;
}



