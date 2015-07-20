# cmake-eclipse-helper

## What is it?

This eclipse plugin is for all developers that want to use CMake based cdt-projects
within eclipse with support for different architectures.

This plugin does not provide a front-end/wizard to create and edit CMakeLists.txt
files.

Current features:
 - Adding source files in eclipse will trigger "touching" the CMakeLists.txt files
   so that GLOBS will be updated.
 - Switching between different toolchains (you can define your own toolchains)
   within eclipse.
 - Automatically adding module path for "custom" modules.
 


## How to install?

Currently we do not host an update site our own. But the "update-site" project
is available within this git repository.

## How to use the plugin?

In case your project root directory contains a "CMakeLists.txt" file you will see
a "CMake" entry in the context menu when right-clicking the project (NOTE: You have
to be in the "Project Explorer" or "Navigator").

Then you can select "Setup" and a small "CMake-Icon" should appear instead in the project.

If not, you may have to look in the "Cmake Output" console for the reason why the setup failed.

## The plugin asks for a directory, what should I do?

You may select any directory you want. But only in case the directory contains a
"cmake-toolchains" directory you will be able to switch betwenn different architectures.

We currently use it to chose between arm and x86_64 (host environment). So we have
two files within the "cmake-toolchains" sub-directory (the pattern is toolchain.<name>.cmake):

 * toolchain.arm.cmake
 * toolchain.x86_64.cmake
 
These files define different variables we require for our build:

```
set(CMAKE_SYSTEM_NAME Linux)

set(PROCESSOR_ARCHITECTURE arm)
set(CMAKE_SYSTEM_PROCESSOR arm)
set(TOOLCHAIN_PREFIX arm)

set(CMAKE_C_COMPILER ${TOOLCHAIN_PREFIX}-gcc)
set(CMAKE_CXX_COMPILER ${TOOLCHAIN_PREFIX}-g++)
```

The plugin will then configure CMake to output the binaries into "bin/<architecture>". And copy
the .project file from bin/architecture to the eclipse project root.
