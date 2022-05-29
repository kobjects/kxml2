# README
- Interested in Kotlin? Try [kmxl3](https://github.com/kobjects/kxml3) and provide feedback!
- Now the content of `org.xmlpull.v1.XmlPullParserFactory` is split across multiple lines `org.kxml2.io.KXmlParser` & `org.kxml2.io.KXmlSerializer` instead of comma separated `org.kxml2.io.KXmlParser,org.kxml2.io.KXmlSerializer` which bombs in Java 9+. This fix will ensure java 9 module system will at least not complain. Even with this fix, implementation of `XmlPullParserFactory` in `xmlpull` is still nothing but abuse of `ServiceLoader` implementation. But at least it does not bomb right now.
- Library can be added as a dependency using

```groovy
repositories {
    mavenCentral()
    maven {
        url "https://jitpack.io" // maven repo where the current library resides
    }
    maven {
        url "https://dl.bintray.com/unverbraucht/java9-fixed-jars" // repo for fetching `xmlpull` dependency that's java 9 enabled
    }
}

dependencies {
  implementation("com.github.kobjects:kxml2:<version>")
}
```
