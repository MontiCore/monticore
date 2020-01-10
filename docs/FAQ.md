# FAQ for MontiCore
### Using Maven
1. Eclipse shows me an error stating that my project configuration is not up-to-date.
2. I get an error saying something about Lifecycle Mappings.
3. Maven build fails because of a missing JDK path.
4. Maven build for de.monticore.parent project fails in eclipse
5. I have a very weird problem, seriously, very weird ...
6. My .m2 folder does not exist.
7. I get a strange error telling me that something is wrong with the UTF8 encoding.
8. Changes I made on one module are not reflected in another module.
--------------------------
1. **Eclipse shows me an error stating that my project configuration is not up-to-date.**
Right-click that project and select Maven -> Update Project Configuration.

2. **I get an error saying something about Lifecycle Mappings.**
Install the m2e extensions mentioned in the developer tutorial.

3. **Maven build fails because of a missing JDK path.**
Change the installed runtime JREs to the installed JDK. Go to Window -> Preferences -> Installed JREs. Add the JDK path and select it to be the default one.

4. **Maven build for de.monticore.parent project fails in eclipse.**
Go to Window -> Preferences -> General -> Workspace. Disable "Build automatically" preference.

5. **I have a very weird problem, seriously, very weird ...**
Right-click that project and select Maven -> Update Project Configuration.

6. **My .m2 folder does not exist.**
Folders in Windows with a leading "." can only be created using the command line. Start the command line and type in "mkdir .m2" in your home folder.

7. **I get a strange error telling me that something is wrong with the UTF8 encoding.**
Change the UTF8 encoding by clicking on Window -> Preferences. Then, select the item as shown below and change the values accordingly.

8. **Changes I made on one module are not reflected in another module.**
Remember that all Maven modules are independent units. By default, they are not directly imported into each other. Instead, Maven resolves dependencies between projects by selecting packages (e.g.jar files) produced by these modules from your local Maven dependency repository. To make the latest version of a module available through this repository, you have to explicitly install it. If you execute an install on an aggregating POM-project, all child modules will be built with the current state of their depending projects as Maven always builds a hierarchy of modules in order of their mutual dependencies. However, if you are working in Eclipse, the workbench can import modules live. This feature is called "Workspace resolution" and is enabled by default for Eclipse automatic project builders. Nevertheless, if you build a module using a Run Configuration you have to explicitly activate "Resolve Workspaces artifacts".
