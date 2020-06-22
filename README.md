# :books: Handle Path Oz

Android Library to handle multiple Uri(paths) received through Intents.


![](gitresources/logo_git.png)


## :iphone: Sample Application

<details>
  <summary markdown="span"> :octocat: Download Release </summary>
  
  You can download the sample application with the latest release [here](https://github.com/onimur/handle-path-oz/raw/master/app/build/outputs/apk/release/HandlePathOZ.apk).

   ---
  </details>
  
  <details>
    <summary markdown="span"> :octocat: Install by Google Play  </summary>
    
   <p align="left">
   <a href="https://play.google.com/store/apps/details?id=br.com.onimur.sample.handlepathoz" target="_blank">
   <img width="25%" alt="Check HandlePathOz on Google Play" src="https://play.google.com/intl/en_gb/badges/static/images/badges/en_badge_web_generic.png"/>
   </a>
   </p>
 
   </details>
    

## :revolving_hearts: Support us

## :hammer_and_wrench: Config
<details>
  <summary markdown="span">:gear: Installation </summary>
      
   ```Add to gradle```

   ---

  </details>
  

  
  <details>
    <summary markdown="span">:bar_chart: Usage </summary>
    
  ```Any config```
  
</details>


## :bulb: Getting Start
<details>
  <summary markdown="span">:game_die: Kotlin </summary>
  
  ### :star2: Initialization
  1.1 - In Kotlin for the implementation of the Listener you can implement it within the scope of the class, as shown below, or also as shown in item **1.2**:
 
  ```class MainActivity : AppCompatActivity(), HandlePathOzListener {...}```
  
  ```Alt+Enter``` to implement the methods, we will discuss the methods later in the topic **Controller**.
  
  Implement handlePathOz in your ```onCreate()``` method, as shown below:
  
   ```
    private lateinit var handlePathOz: HandlePathOz

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Initialize HandlePathOz
        //context, listener
        handlePathOz = HandlePathOz(this, this)
        
    }

   ```

  1.2 - You can also implement the Listener when initializing the class, without having to implement it within the scope of the class:
  
     ```
      private lateinit var handlePathOz: HandlePathOz
      private val listener = object: HandlePathOzListener{...//implement methods}
  
      override fun onCreate(savedInstanceState: Bundle?) {
          super.onCreate(savedInstanceState)
          //Initialize HandlePathOz
          //context, listener
          handlePathOz = HandlePathOz(this, listener)
          
      }
  
     ```
     
   2 - After selecting the desired files (The sample application has the entire step) in ```onActivityResult``` leave as follows:
  
  ```
    @FlowPreview
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
  
  ### :video_game: Controller
  We have two methods in the listeners, one of which is optional:
  ```
      //On Completion (Sucess or Error)
      //If there is a cancellation or error, the entire task that was handled will be returned in the list.
      override fun onRequestHandlePathOz(listPath: List<Pair<Int, String>>, tr: Throwable?) {
          //Hide Progress
          //Update the recyclerview with the list
          yourAdapter.updateListChanged(listPath.map { uri -> Uri.parse(uri.second) })
  
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

  ### :cloud: Cloud files and Unknown Providers
  
  If the selected file was from Dropbox,Google Drive, OneDrive or an unknown file provider, it will then be copied/created in
  InternalStorage/Android/data/your.package.name/files/Temp/sameFileNameAndExtension
  
  When you want to delete the generated files call:
  
  ```
  handlePathOz.deleteTemporaryFiles()
  ```
  
  
  ### :bomb: Cancel the tasks
  
  There are two methods for canceling tasks, ```cancelTask()``` and ```onDestroy()```.
  
  **handlePathOz.cancelTask() ->** Can be called as a button action for canceling or by progressBar (As shown in the demo application).
  In the cancellation of the task by this method, all Uri that was treated will be passed in the ```onRequestHandlePathOz()``` method.
  
  **handlePathOz.onDestroy() ->**  It can be called with the Activity or fragment's  ```onDestroy()``` method. 
  This method destroys the task and its cancellation does not update anything and cannot be restarted.
  Example of use:
  ```
    override fun onDestroy() {
        handlePathOz.onDestroy()
        //You can delete the temporary files here as well.
        super.onDestroy()
    }
  ```
  
  ---
  
</details>

<details>
  <summary markdown="span">:game_die: Java</summary>
  
  ### :star2: Initialization
  
  
  
  ### :video_game: Controller
  
  ---
  
</details>

## :mag: Main Features

## :triangular_ruler: Built With
  * [Android Studio 4.0](https://developer.android.com/studio)
  
  
## 🧩 Contributing
  This project is open-source, so feel free to share your ideas and changes to improve the project. 
  

## :page_with_curl: License
 
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

  * [Apache License 2.0](gitresources/LICENSE.md)
