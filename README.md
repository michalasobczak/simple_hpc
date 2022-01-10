# Simple High Performance Computing

This is work in progress book project. You can review contents right now, but please consider visiting this 
repository periodically as it changes quite fast. The main goal for all of submodules is to excel in terms of both 
software and hardware performance capabilities, still using commodity utilities. I try not to step into enterprise 
grade or high pricing solutions willing to accomplish my tasks within modest budget and time frame.

## Part 1 - "Podstawy elektroniki i budowa komputera w symulatorze" (SeriesPartOne)
Please find TINA TI examples from the first part of series.

## Part 2 - hardware (home computing)
TBD

## Part 3 - multimedia computing
TBD

## Part 4 - OpenGL
TBD

## Part 5 - Undestanding OpenCL (SeriesPartFive)
WIP

### Modules available

There are few examples available:
- main module (opencl/src)
- bandwidth (set of tests checking various scalar operation performance)
- c64_3d (orthographic and perspective calculations)      
- naive_sort (array indexing)
- pi_est_monte_carlo (estimating PI by direct relation between square and circle)
- picture
- reduction
- reference_c
- sort2

### How to run

In order to run samples (i.e. reusing_code, com.company2.Main), try as it as follows:

```
$HOME/.jdks/openjdk-15.0.2/bin/java 
-classpath $HOME/IdeaProjects/simple_hpc/opencl/out/production/bandwidth
          :$HOME/jocl-2.0.2.jar
          :$HOME/IdeaProjects/simple_hpc/opencl/out/production/opencl com.michalasobczak.bandwidth.Main
```

It will work properly assuming you:
- fetched code into $HOME/IdeaProjects/simple_hpc
- downloaded JOCL library JAR into $HOME as well
- built modules (main and bandwidth) to output out/production output folder

You can also run it directly from IntelliJ or other IDE.


## Part 6 - data mining
TBD
