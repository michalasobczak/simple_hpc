#include <conio.h>
#include <stdio.h>

char data1[12] = "Test Data 1";
char data2[12] = "Test Data 2";
char data3[12] = "\0";
char data4[12] = "\0";

void std_write(unsigned char * file_name);
void std_read(unsigned char * file_name);
void kernel_read(unsigned char * file_name);
void kernel_getin(void *ptr, unsigned char size);

void main(void)
    {
        clrscr();

        // File read & write with stdio functions
        cputs("Writing with stido.h\n\r");
        std_write("testfile");

        cputs("\n\rReading with stido.h\n\r");
        std_read("testfile");

        cprintf("\n\rdata3 : %s\n\r",data3);
        cprintf("data4 : %s\n\r",data4);
        cprintf("\n\r");

        cgetc();
        clrscr();

        // Trying to read with cbm specific functions
        data3[0]='\0';
        data4[0]='\0';

        cputs("\n\rReading with cbm.h\n\r");
        kernel_read("testfile");

        cprintf("\n\rdata3 : %s\n\r",data3);
        cprintf("data4 : %s\n\r",data4);
        cprintf("\n\r");

        // Trying to read non exiting file
        data3[0]='\0';
        data4[0]='\0';

        cputs("\n\rReading with cbm.h again\n\r");
        kernel_read("testfile2");

        cprintf("\n\rdata3 : %s\n\r",data3);
        cprintf("data4 : %s\n\r",data4);
        cprintf("\n\r");

    }


void std_write(unsigned char * file_name)
    {
        FILE *file;
        unsigned char n;

        cputs("Opening data file...\n\r");
        _filetype = 's';
        if(file = fopen(file_name, "w"))
            {
                cputs("Writing...\n\r");
                n = fwrite(data1, sizeof(unsigned char)*11, 1, file);
                n = n + fwrite(data2, sizeof(unsigned char)*11, 1, file);

                if(n != 2)
                {
                    cputs("Error: File could not be written.\n\r");
                    fclose(file);
                }
                else
                {
                    cputs("Done.\n\r");
                    fclose(file);
                }
            }
        else
            {
                cputs("File could not be opened\n\r");
            }
    }

void std_read(unsigned char * file_name)
    {
        FILE *file;
        unsigned char n;

        cputs("Opening data file...\n\r");
        _filetype = 's';
        if(file = fopen(file_name, "r"))
        {
            cputs("Reading...\n\r");
            n = fread(data3, sizeof(unsigned char)*11, 1, file);
            n = n + fread(data4, sizeof(unsigned char)*11, 1, file);

            if(n != 2)
            {
                cputs("Error while reading!\n\r");
                fclose(file);
            }
            else
            {
                cprintf("Done.\n\r");
                fclose(file);
            }
        }
        else
        {
            cputs("File could not be opened\n\r");
        }
    }

void kernel_read(unsigned char * file_name)
    {
        unsigned char error_channel;

        // Open error channel
        cbm_k_setlfs(15,8,15);
        cbm_k_setnam('\0');
        error_channel = cbm_k_open();

        cbm_k_setlfs(2,8,0);
        cbm_k_setnam(file_name);

        cputs("Opening data file...\n\r");
        if(!cbm_k_open())
        {
            _filetype = 's';
            cputs("Reading...\n\r");
            cbm_k_chkin(2);
            kernel_getin(data3, sizeof(unsigned char)*11);
            kernel_getin(data4, sizeof(unsigned char)*11);

            cbm_k_chkin(15);
            if(!cbm_k_getin())
            {
                cputs("Error while reading!\n\r");
                cbm_k_clall();
            }
            else
            {
                cprintf("Done.\n\r");
                cbm_k_clall();

            }
        }
        else
        {
            cputs("File could not be opened\n\r");
        }
    }

void kernel_getin(void *ptr, unsigned char size)
    {
        unsigned char * data = (unsigned char *)ptr;
        unsigned char i;

        for(i=0; i<size; ++i)
        {
            data[i] = cbm_k_getin();
        }
        data[i] = '\0';
    }
