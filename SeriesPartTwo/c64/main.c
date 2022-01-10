#include <conio.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h >

// uninitialized section
char* read_buffer[1000];
static char filename[10];
static char buffer[5];

// data with defaults
unsigned char i=0;
unsigned char j=0;
unsigned char tmp=0;

// prototypes
FILE* open_file(unsigned char* file_name, const char* mode);
void write_string(FILE* file, unsigned char* str);
void read_string(FILE* file, int size);
void close_file(FILE* file);

// fastcalls
char* __fastcall__ strcat (char* s1, const char* s2);
char* __fastcall__ strcpy (char* s1, const char* s2);
void __fastcall__ srand (unsigned seed);
void __fastcall__ sleep (unsigned seconds);


// program
int main(void) {
	FILE* file;
	srand(10);
	clrscr();
	for (i=0; i<=0; i++) {
		//strcat(filename, "myfile");
		strcpy(filename, itoa(i, buffer, 10));
		file = open_file(filename, "w");
		write_string(file, "testujemy");
		close_file(file);
		cprintf("\n\r");
		sleep(1);
		file = open_file(filename, "r");
		read_string(file, 9);
		close_file(file);
		cputs((const char*)read_buffer);
		//clrscr();
	}
	return 0;
}


int dummy(a)
int a;
{
	return a;
}


FILE* open_file(unsigned char* file_name, const char* mode) {
	FILE* file;
	cputs("open_file = 1. Opening data file...\n\r");
	sprintf("file_name: %s - with mode - %s\r\n", file_name, mode);
	_filetype = 's';
	if (file = fopen(file_name, mode)) {
		cputs("open_file = 2. File opened\n\r");
	} else {
		cputs("open_file = 2. File could not be opened\n\r");
	}
	return file;
}


void write_string(FILE* file, unsigned char* str) {
	unsigned char n = 0;
	cputs("write_string\n\r");
	n = fwrite(str, strlen(str), 1, file);
	if(n != 1) {
		cputs("write_string = error: File could not be written\n\r");
		fclose(file);
	} else {
		cputs("write_string = done\n\r");
		fclose(file);
	}
}


void read_string(FILE* file, int size) {
	unsigned char n = 0;
	cputs("read_string\n\r");
	n = fread(read_buffer, sizeof(unsigned char)*size, 1, file);
	if(n != 1) {
		cputs("read_string = error: File could not be read\n\r");
		fclose(file);
	} else {
		cputs("read_string = done\n\r");
		fclose(file);
	}
}


void close_file(FILE* file) {
	cputs("close_file\n\r");
	fclose(file);
}

