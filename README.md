#Plugin generates java class documentation

###Technology stack:
~~~
Java 8
intellij.openapi
itextpdf
guice
~~~
###Description:

The plugin generates pdf file with class documentation.

Open .java file in your intellij, choose "Generate class document" in Tools Menu or press shortcut "control alt g" for initialize generating.
The plugin will suggest you choose directory to save file and file name. 

For each inner classes the plugin will generate separate pdf file with note about its outer class.

![](screenshots/java.util.AbstractList.png?raw=true "java.util.AbstractList")

