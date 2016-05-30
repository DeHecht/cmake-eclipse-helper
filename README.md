# CMake Eclipse Helper

The CMake Eclipse Helper is a simplistic plug-in that tries to help the user through the rough edges of setting up their CMake-based projects within Eclipse for cross-compiling.

Internally, it uses CMakes "Eclipse Generator". So your projects do not have to meet any other prerequisites other than having a `CMakeLists.txt` in the projects root. Just use the option *"CMake"->"Setup"* in the context menu of your project and you can build your project using CMake (this even works, if the project wasn't a CDT-Project before).

## Features

 * Context menu to switch the Build Type (`Debug`, `Release`, `RelWithDebugInfo`, etc.)
 * Context menu to switch the Toolchain (Own toolchains can be added)
 * Support for adding/removing source files and updating the globs (by automatically touching the `CMakeLists.txt` file).
 * It provides simple context menus for your project to run the CMake generator and to tag "generated" files (like the `.project` and `.cproject`) so you won't accidentally spam your version control system with changes on these files.

## How to install?

Our eclipse update-site is available at: http://www.cmake-helper.eu/releases/1.0

## How to define toolchains?

You require to configure the path of the directory, where the toolchain files are stored. The parser uses the following pattern: `toolchain.<name>.cmake`

The "name" will be picked up by the toolchain context menu. 

## How to use the plug-in?

All you need is a project with a `CMakeLists.txt` file in the projects root.

1. Right-click your project
2. Choose "CMake"->"Setup" in the context menu
3. Build your project

In case your `CMakeLists.txt` does not contain any syntax errors thats all you need to do.

If you have a arm toolchain file and you want to switch from `x86_64` to the `arm` toolchain this is the way to go.

 1. Right-click your project
 2. Choose *CMake* -> *Toolchains* -> `name-of-your-arm-toolchain`
 3. Build your project

You will find your binaries in the *"bin/name-of-your-arm-toolchain"* directory (in case you did not specify a different output directory within your `CMakeLists.txt`).
