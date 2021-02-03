# Simple High Performance Computing

## OpenCL

There are few examples available:
- main module (opencl/src)
- reusing_code (to show how Java submodules works)
- naive_sort (array indexing)
- pi_est_monte_carlo (estimating PI by direct relation between square and circle)
- WIP: c64_3d (orthographic and perspective calculations)
- WIP: bandwidth (set of tests checking various scalar operation performance)

In order to run samples (i.e. reusing_code, com.company2.Main), try as it as follows:

```
$HOME/.jdks/openjdk-15.0.2/bin/java 
-classpath $HOME/IdeaProjects/simple_hpc/opencl/out/production/reusing_code
          :$HOME/jocl-2.0.2.jar
          :$HOME/IdeaProjects/simple_hpc/opencl/out/production/opencl com.company2.Main
```

It will work properly assuming you:
- fetched code into $HOME/IdeaProjects/simple_hpc
- downloaded JOCL library JAR into $HOME as well
- built modules (main and reusing_code) to output out/production output folder

