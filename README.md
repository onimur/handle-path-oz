# 📚 Handle Path Oz

<p align="center">
   <a title="API 16+">
        <img src="https://img.shields.io/badge/API-16%2B-orange?logo=android&logoColor=white">
    </a>
    <a href="./LICENSE" title="License">
        <img src="https://img.shields.io/github/license/onimur/handle-path-oz?label=License&logo=Apache&logoColor=white">
    </a>
    <a href="https://circleci.com/gh/onimur/handle-path-oz" title="onimur">
        <img src="https://img.shields.io/circleci/build/github/onimur/handle-path-oz?label=circleci&logo=CircleCI&logoColor=white">
    </a>
    <a href="https://play.google.com/store/apps/details?id=br.com.onimur.sample.handlepathoz" title="HandlePathOz">
        <img src="https://img.shields.io/badge/GooglePlay-SampleApp-yellow?logo=google%20play&logoColor=white">
    </a>
</p>

<p align="center">
    <a href="https://bintray.com/onimur/maven/HandlePathOz/_latestVersion" title="Bintray">
        <img src="https://img.shields.io/bintray/v/onimur/maven/HandlePathOz?label=bintray&logo=JFrog%20Bintray&logoColor=white">
    </a>
    <a href="https://search.maven.org/artifact/com.github.onimur/handle-path-oz" title="Maven Central">
        <img src="https://img.shields.io/maven-central/v/com.github.onimur/handle-path-oz?color=brightgreen&label=maven%20central&logo=Apache%20Maven">
    </a>
</p>

Android Library to handle multiple Uri(paths) received through Intents.

<p align="center">
    <a title="HandlePathOz">
        <img width="75%" src=".github/resources/logo_git.png">
    </a>
</p>

## 💞 Support us

We are developing this structure in the open source community without financial planning.
If you like this project and would like to help us, make a donation:

<p align="center">
    <a href="https://www.patreon.com/onimur" target="_blank">
        <img width="30%" alt="Check my Patreon" src=".github/resources/support-patreon.png"/>
    </a>
    <a href="https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=YUTBBKXR2XCPJ" target="_blank">
        <img width="30%" alt="Donate with Paypal" src=".github/resources/support-paypal.png"/>
    </a>
    <a href="https://www.buymeacoffee.com/onimur" target="_blank">
        <img width="30%" alt="Buy me a coffee" src=".github/resources/support-buy-coffee.png"/>
    </a>
</p>

## 📝 Content

- [Sample application](#-sample-application)
- [Config](#%EF%B8%8F-config)
- [Getting start](#-getting-start)
    - [Multiple uri](#working-with-multiple-uri)
        - [Kotlin](#-kotlin)
        - [Java](#-java)
    - [Single uri](#working-with-single-uri)
        - [Kotlin](#-kotlin-1)
        - [Java](#-java-1)
- [Projects using this library](#-projects-using-this-library)
- [Main features](#-main-features)
- [Built with](#-built-with)
- [Contributing](#-contributing)
- [License](#-license)

## 📱 Sample application

<p align="center">
    <img alt="Sample App" src=".github/resources/sample.gif"/>
</p>

### 🌱 Download release
  
You can download the sample application with the latest release [here](artifacts/HandlePathOZ.apk).

### 🌱 Install by GooglePlay 

Check the Sample App on GooglePlay

<p align="left">
    <a href="https://play.google.com/store/apps/details?id=br.com.onimur.sample.handlepathoz" target="_blank">
        <img width="25%" alt="Check HandlePathOz on Google Play" src="https://play.google.com/intl/en_gb/badges/static/images/badges/en_badge_web_generic.png"/>
    </a>
</p>

## 🛠️ Config

First check the latest [version](https://bintray.com/onimur/maven/HandlePathOz/_latestVersion).

### Gradle

In build.gradle(Module:app) within dependencies, implement:
      
```kotlin

    implementation 'com.github.onimur:handle-path-oz:1.0.7'

```

### Maven

```

    <dependency>
      <groupId>com.github.onimur</groupId>
      <artifactId>handle-path-oz</artifactId>
      <version>1.0.7</version>
      <type>pom</type>
    </dependency>

```

### Ivy

```

    <dependency org='com.github.onimur' name='handle-path-oz' rev='1.0.7'>
      <artifact name='handle-path-oz' ext='pom' ></artifact>
    </dependency>

```

# 💡 Getting start

## Working with Multiple uri

The next steps are for working with multiple uri as well as for a single uri.

### 🎲 Kotlin
  
You can check in the wikipage, [click here](https://github.com/onimur/handle-path-oz/wiki/Kotlin---Multiple-Uri).

### 🎲 Java

You can check in the wikipage, [click here](https://github.com/onimur/handle-path-oz/wiki/Java---Multiple-Uri).

## Working with Single uri

The next steps only serve to work with a single uri.

### 🎲 Kotlin
  
You can check in the wikipage, [click here](https://github.com/onimur/handle-path-oz/wiki/Kotlin-Single-Uri).

### 🎲 Java
  
You can check in the wikipage, [click here](https://github.com/onimur/handle-path-oz/wiki/Java-Single-Uri).

## 🚀 Projects using this library

Here's a [wikipage list of projects](https://github.com/onimur/handle-path-oz/wiki/Projects-using-HandlePathOz).

If you've used this library, please let me know! Nothing makes me happier than seeing someone else take my work and go wild with it.

## 🔍 Main features

- [Kotlin Coroutines/Flow](https://kotlinlang.org/docs/reference/coroutines-overview.html) 
- Parse Uri
- Multiple tasks in parallel

## 📐 Built with

  * [Android Studio 4.0](https://developer.android.com/studio)
  
## 🧩 Contributing

This project is open-source, so feel free to fork, or to share your ideas and changes to improve the project, check with more details below.

- 💬 [Contributing](docs/CONTRIBUTING.md)
- 👮🏼 [Code of conduct](docs/CODE_OF_CONDUCT.md)
- 😷 [Support](docs/SUPPORT.md)

## 📃 License

    Copyright (c) 2020, HandlePathOz.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

  * [Apache License 2.0](./LICENSE)
