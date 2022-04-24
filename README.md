# jfxfractals

JavaFx application that renders various fractal types using  LWJGL OpenGL library and Eclipse DriftFx library.

Requires JDK 17. Maven uses toolchain plugin so need to add corresponding toolchain into your toolchains.xml file
located at user home .m2/toolchains.xml

```
<toolchain>
    <type>jdk</type>
    <provides>
        <version>17</version>
        <vendor>openjdk</vendor>
    </provides>
    <configuration>
        <jdkHome>PATH_TO_JDK_17</jdkHome>
    </configuration>
</toolchain>
```

To build use command:

```
mvn install
```

to run the application use the .bat file 

```
start-jdk17.bat
```
Adjust the path to Java 17 and JavaFX SDK in it.



Also, application can be executed using maven javafx plugin

```
mvn javafx:run
```
