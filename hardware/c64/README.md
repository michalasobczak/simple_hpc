# c64

## Compiling

In order to compile the following sample code you can manually run cl65 binary as follows:
```
cl65 -r -O main.c -t c64
```

For example:
```
G:\apps\cc65-snapshot-win32\bin\cl65.exe -r -O hello.c -t c64
```

You can find additional information regarding memory map and compiling c64 C programs in separate repository on my 
GitHub account. You can also compile programs using Embarcadero Dev-C++ 6.3 which you can find on sf.net. It will 
make whole process a little easier. 

Go to Tools - Compiler options and add blank compiler entry. In general settings put the following:
```
-r -O -t c64
```
In directories section put path to compilers bin directory. Same for libraries and C includes. In programs section 
put gcc and g++ with both cl65.exe (on Windows). This way you will be able to compile program by pressing F9 
(or compile a button in UI). In result, you will get executable file which can be smart/auto attached into emulator 
(VICE). In current (2021) VICE version this option is called Smart attach disk/tape/cartridge.

## Running on real hardware

For sake of convenience I suggest using SD2IEC as I do not own 1541 drive to describe it properly at the moment. 
SD2IEC is a card reader designed for Commodore computers. It supports PRG programs (raw programs with a BASIC header 
included) as well as D64 disk images. It does not support T64 tape images. I own 3 button version without LCD. First 
you connect PCB into tape drive slot for power and serial connector for data transfer. You need to have SD card 
formatted. I use 32 GB SD card.  

You could and should download FB utility in order just to navigate with cursors to some program and run it by 
pressing return button. It is way easier than mounting and running it manually. 

Link to the required utilities: https://ncsystems.eu/upload/SD2IEC_PACK.zip

Complete user manual can be found there: https://www.ncsystems.eu/pl/content/8-sd2iec-user-manual

In order to run program power up your C64 computer (or similar based on the same chip) and type:
```
load "fb", 8
```

Please note that BASIC command do not need to be separated with space as all command are reserved keywords. There is 
plenty of other feature as multi disk programs (e.g. games) or software archiving. In order to use tape drive at the 
same time you need to power SD2IEC from external power source such as USB, because by default it is powered from 
tape drive connector on your C64 computer.



