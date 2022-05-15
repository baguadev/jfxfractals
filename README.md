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

It will generate installer file ( for example .msi for windows) 

to run the application execute the installer or native binary file in target/installer-work-images folder



Also, application can be executed using maven javafx plugin

```
mvn javafx:run
```
