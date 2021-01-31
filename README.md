# Simple High Performance Computing

## OpenCL

There are few example available:
- main module
- reusing_code
- naive_sort

In order to run samples (ie. reusing_code, com.company2,Main), try as it as follows:

```
$HOME/.jdks/openjdk-15.0.2/bin/java 
-classpath $HOME/IdeaProjects/simple_hpc/opencl/out/production/reusing_code
          :$HOME/jocl-2.0.2.jar
          :$HOME/IdeaProjects/simple_hpc/opencl/out/production/opencl com.company2.Main
```

It will work properly assuming you :
- fetched code into $HOME/IdeaProjects/simple_hpc
- downloaded JOCL library JAR into $HOME as well
- built module to output out/production output folder

