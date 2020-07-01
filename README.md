# 📚 Handle Path Oz

<p align="center">
   <a title="API 16+">
        <img src="https://img.shields.io/badge/API-16%2B-orange?logo=android&logoColor=white">
    </a>
    <a href="./LICENSE" title="License">
        <img src="https://img.shields.io/github/license/onimur/handle-path-oz?label=License&logo=Apache&logoColor=white">
    </a>
    <a href="https://app.circleci.com/pipelines/github/onimur/handle-path-oz" title="onimur">
        <img src="https://img.shields.io/circleci/build/github/onimur/handle-path-oz?label=circleci&logo=CircleCI&logoColor=white">
    </a>
    <a href="https://play.google.com/store/apps/details?id=br.com.onimur.sample.handlepathoz" title="HandlePathOz">
        <img src="https://img.shields.io/badge/GooglePlay-SampleApp-yellow?logo=google%20play&logoColor=white">
    </a>
</p>

<p align="center">
    <a href="https://bintray.com/onimur/maven/HandlePathOz/_latestVersion" title="Bintray">
        <img src="https://img.shields.io/bintray/v/onimur/maven/HandlePathOz?label=bintray">
    </a>
    <a href="https://search.maven.org/artifact/com.github.onimur/handle-path-oz" title="Maven Central">
        <img src="https://img.shields.io/maven-central/v/com.github.onimur/handle-path-oz?color=brightgreen&label=maven%20central&logo=Apache%20Maven">
    </a>
</p>

Android Library to handle multiple Uri(paths) received through Intents.

<p align="center">
    <a title="HandlePathOz">
        <img width="75%" src=".gitresources/logo_git.png">
    </a>
</p>

## 💞 Support us

We are developing this structure in the open source community without financial planning.
If you like this project and would like to help us, make a donation:

<p align="center">
    <a href="https://www.patreon.com/onimur" target="_blank">
        <img width="30%" alt="Check my Patreon" src=".gitresources/support-patreon.png"/>
    </a>
    <a href="https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=YUTBBKXR2XCPJ" target="_blank">
        <img width="30%" alt="Donate with Paypal" src=".gitresources/support-paypal.png"/>
    </a>
    <a href="https://www.buymeacoffee.com/onimur" target="_blank">
        <img width="30%" alt="Buy me a coffee" src=".gitresources/support-buy-coffee.png"/>
    </a>
</p>

## 📝 Content

- [Sample application](#-sample-application)
- [Config](#%EF%B8%8F-config)
- [Getting start](#-getting-start)
    - [Kotlin](#-kotlin)
    - [Java](#-java)
- [Main features](#-main-features)
- [Built with](#-built-with)
- [Contributing](#-contributing)
- [License](#-license)

## 📱 Sample application

<p align="center">
    <img alt="Sample App" src=".gitresources/sample.gif"/>
</p>

### 🌱 Download release
  
You can download the sample application with the latest release [here](app/build/outputs/apk/release/HandlePathOZ.apk).

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

    implementation 'com.github.onimur:handle-path-oz:1.0.3'

```

### Maven

```

    <dependency>
      <groupId>com.github.onimur</groupId>
      <artifactId>handle-path-oz</artifactId>
      <version>1.0.3</version>
      <type>pom</type>
    </dependency>

```

### Ivy

```

    <dependency org='com.github.onimur' name='handle-path-oz' rev='1.0.3'>
      <artifact name='handle-path-oz' ext='pom' ></artifact>
    </dependency>

```

## 💡 Getting start

### 🎲 Kotlin
  
#### 💫 Initialization

1.1 - In Kotlin for the implementation of the Listener you can implement it within the scope of the class, as shown below, or also as shown in item **1.2**:
 
```kotlin

  class MainActivity : AppCompatActivity(), HandlePathOzListener {
   //...
   }

```
  
`Alt+Enter` to implement the methods, we will discuss the methods later in the topic **Controller**.
Implement handlePathOz in your `onCreate()` method, as shown below:
  
```kotlin

    private lateinit var handlePathOz: HandlePathOz

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Initialize HandlePathOz
        //context, listener
        handlePathOz = HandlePathOz(this, this)
    }

```

1.2 - You can also implement the Listener when initializing the class, without having to implement it within the scope of the class:
  
```kotlin

      private lateinit var handlePathOz: HandlePathOz
      private val listener = object: HandlePathOzListener{
      //implement methods
      }
  
      override fun onCreate(savedInstanceState: Bundle?) {
          super.onCreate(savedInstanceState)
          //Initialize HandlePathOz
          //context, listener
          handlePathOz = HandlePathOz(this, listener)
      }

```
     
2 - After selecting the desired files (The sample application has the entire step) in ```onActivityResult``` leave as follows:
  
```kotlin

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == REQUEST_OPEN_GALLERY) and (resultCode == Activity.RESULT_OK)) {
            //This extension retrieves the path of all selected files without treatment.
            val listUri = data.getListUri()

            //with the list you can update some recyclerview and switch to the method that handles Uri's.

            //set list of the Uri to handle
            //in concurrency use:
            // 1                -> for tasks sequentially
            //greater than 1    -> for the number of tasks you want to perform in parallel.
            //Nothing           -> for parallel tasks - by default the value is 10
            handlePathOz.getRealPath(listUri)
            // handlePathOz.getRealPath(listUri, 1)

            //show Progress Loading
        }
    }

```
  
#### 🎮 Controller

We have two methods in the listeners, one of which is optional:
  
```kotlin

      //On Completion (Sucess or Error)
      //If there is a cancellation or error, the entire task that was handled will be returned in the list.
      override fun onRequestHandlePathOz(listPath: List<PairPath>, tr: Throwable?) {
          //Hide Progress
          //Update the recyclerview with the list
          yourAdapter.updateListChanged(listPath.map { uri -> Uri.parse(uri.path) })
  
          //Handle any Exception (Optional)
          tr?.let {
              Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
          }
      }
  
      //This method is Optional
      override fun onLoading(currentUri: Int) {
          //Update UI with the current Uri
          //progressLoading.setText = "${currentUri}/${listUri.size}"
      }

```

 #### ☁️ Cloud files and Unknown Providers
  
If the selected file was from Dropbox,Google Drive, OneDrive or an unknown file provider, it will then be copied/created in
InternalStorage/Android/data/your.package.name/files/Temp/sameFileNameAndExtension
When you want to delete the generated files call:
  
```kotlin

   handlePathOz.deleteTemporaryFiles()

```
  
#### 💣 Cancel the tasks
  
There are two methods for canceling tasks, ```cancelTask()``` and ```onDestroy()```.
  
**handlePathOz.cancelTask() ->** Can be called as a button action for canceling or by progressBar (As shown in the demo application).
In the cancellation of the task by this method, all Uri that was treated will be passed in the ```onRequestHandlePathOz()``` method.
  
**handlePathOz.onDestroy() ->**  It can be called with the Activity or fragment's  ```onDestroy()``` method. 
This method destroys the task and its cancellation does not update anything and cannot be restarted.
Example of use:
  
```kotlin

    override fun onDestroy() {
        handlePathOz.onDestroy()
        //You can delete the temporary files here as well.
        super.onDestroy()
    }

```

---

### 🎲 Java
  
#### 💫 Initialization

The implementation of the Listener you can implement it within the scope of the class, as shown below:
  
```java

     public class MainActivity extends AppCompatActivity implements HandlePathOzListener {
      //
      }

```

`Alt+Enter` to implement the methods, we will discuss the methods later in the topic **Controller**.
Implement handlePathOz in your `onCreate()` method, as shown below:
  
```java

    private HandlePathOz handlePathOz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize HandlePathOz
        //context, listener
        handlePathOz = HandlePathOz(this, this)
    }

```

After selecting the desired files (The sample application has the entire step) in ```onActivityResult``` leave as follows:
  
```java

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_OPEN_GALLERY && resultCode == RESULT_OK) {
            //This extension retrieves the path of all selected files without treatment.
            listUri = getListUri(data);

            //with the list you can update some recyclerview and switch to the method that handles Uri's.


            //set list of the Uri to handle
            //in concurrency use:
            // 1                -> for tasks sequentially
            //greater than 1    -> for the number of tasks you want to perform in parallel.
            //Nothing           -> for parallel tasks - by default the value is 10
            handlePathOz.getRealPath(listUri);
            // handlePathOz.getRealPath(listUri, 1)

            //show Progress Loading
        }
    }

```
  
#### 🎮 Controller
  
We have two methods in the listeners, one of which is optional:
    
```java

        //On Completion (Sucess or Error)
        //If there is a cancellation or error, the entire task that was handled will be returned in the list.
       @Override
       public void onRequestHandlePathOz(@NonNull List<PairPath> listPath, Throwable tr) {
            //Hide Progress
            //Update the recyclerview with the list
            //Update the adapter
            List<Uri> listUri = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                Uri uri = Uri.parse(list.get(i).getPath());
                listUri.add(uri);
            }
            yourAdapter.updateListChanged(listUri);
    
            //Handle Exception (Optional)
            if (throwable != null) {
                Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
       }

       //This method is Optional
       @Override
       public void onLoading(int currentUri) {
           //Update UI with the current Uri
           //progressLoading.setText(currentUri + "/" + listUri.size());
       }

```

#### ☁️ Cloud files and Unknown Providers
  
If the selected file was from Dropbox,Google Drive, OneDrive or an unknown file provider, it will then be copied/created in
InternalStorage/Android/data/your.package.name/files/Temp/sameFileNameAndExtension
When you want to delete the generated files call:
  
```java

   handlePathOz.deleteTemporaryFiles()

```
  
#### 💣 Cancel the tasks
  
There are two methods for canceling tasks, ```cancelTask()``` and ```onDestroy()```.
  
**handlePathOz.cancelTask() ->** Can be called as a button action for canceling or by progressBar (As shown in the demo application).
In the cancellation of the task by this method, all Uri that was treated will be passed in the ```onRequestHandlePathOz()``` method.
  
**handlePathOz.onDestroy() ->**  It can be called with the Activity or fragment's  ```onDestroy()``` method. 
This method destroys the task and its cancellation does not update anything and cannot be restarted.
Example of use:
  
```java

    @Override
    public void onDestroy() {
        handlePathOz.onDestroy();
        //You can delete the temporary files here as well.
        super.onDestroy();
    }

```
  
## 🔍 Main features

- [Kotlin Coroutines/Flow](https://kotlinlang.org/docs/reference/coroutines-overview.html) 
- Parse Uri
- Multiple tasks in parallel

## 📐 Built with

  * [Android Studio 4.0](https://developer.android.com/studio)
  
## 🧩 Contributing

This project is open-source, so feel free to fork, or to share your ideas and changes to improve the project. 

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

  * [Apache License 2.0](LICENSE)
