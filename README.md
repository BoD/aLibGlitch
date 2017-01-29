aLibGlitch
===

A "glitch effect" library for Android

![Glitch effect](/preview.gif?raw=true "Glitch effect")


This little library will produce a "glitch effect" on your app's UI.

[![Release](https://jitpack.io/v/BoD/aLibGlitch.svg)](https://jitpack.io/#BoD/aLibGlitch/)

Usage
---

### 1/ Add the dependencies to your project

```groovy

repositories {
    /* ... */
    maven { url 'https://jitpack.io' }
}


dependencies {
    /* ... */
    compile 'com.github.BoD:aLibGlitch:1.0.0'
}
```


### 2/ Use the lib

```java
// Show a "glitch effect" for a few milliseconds
GlitchEffect.showGlitch(this);
```

License
---

```
Copyright (C) 2017 Benoit 'BoD' Lubek (BoD@JRAF.org)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
