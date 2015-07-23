# USE CMake Eclipse Support

This eclipse plugin helps C/C++ developers setting up their CMake-based projects within eclipse. Additionally, it helps the developer to switch between different architectures within a single eclipse project scope.

The plugin does only wrap calls to the cmake executable and does not try to interpret the content of the CMakeLists.txt files itself. For that reason it could also work for projects that are not intentionally setup for development with eclipse-cdt.

## Requirements

 * CMake - http://www.cmake.org/cmake/resources/software.html
 * Eclipse with CDT installed - http://www.eclipse.org/downloads/packages/eclipse-ide-cc-developers/lunar
 * (Optional) CMake Editor - http://cmakeed.sourceforge.net/

## Features

 * Support for adding/removing source files and updating the GLOBs. (By automatically touching the CMakeLists.txt file).
 * Context menu to switch the Build Type (Debug, Release, etc.)
 * Context menu to switch the Toolchain (Own toolchains can be added)

## How to install?

Currently we do not host an update site our own. But the "update-site" project
is available within this git repository.

## How to use the plugin?

We are currently preparing a Getting Started Guide.

Basically all you need is an eclipse project that contains a "CMakeLists.txt" file and chose "CMake"->"Setup" in the context menu. Then you should be able to build the project using CDT.
