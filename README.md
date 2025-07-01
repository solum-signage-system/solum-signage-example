## SoluM Signage Feature Examples

This sample is created to provide a code-level guide on how to use various features necessary for working with SoluM Signage.

### What's Included in This Example

* Example code for device operations that are often difficult to experience when using Android.
* Usage examples for SoluM's Device API.

### What This Example Does Not Aim For

* This is not an example for Android project configuration. Instead, please refer to the official Google samples: [android/architecture-samples](https://github.com/android/architecture-samples): A collection of samples to discuss and showcase different architectural tools and patterns for Android apps.

### How to Use

Follow these steps to use this project:

1.  **Generate a GitHub Personal Access Token:**
    First, you need to generate a GitHub Personal Access Token. Please refer to the document below to create your token.
    [https://catkin-gargoyle-8a3.notion.site/Devie-API-package-import-9ea28026f1c54b238f9781c088701384](https://catkin-gargoyle-8a3.notion.site/Devie-API-package-import-9ea28026f1c54b238f9781c088701384)

2.  **Modify `settings.gradle.kts` file:**
    Open the `settings.gradle.kts` file located at the root of the project and find the lines below. Replace `USER` and `SECRET` with your **account username** and **GitHub Personal Access Token**, respectively.

    ```kotlin
    username = "USER"
    password = "SECRET"
    ```

    **⚠️ Important:** To prevent GitHub Push Protection errors and enhance security, we strongly recommend managing sensitive information using [Gradle Properties](https://docs.gradle.org/current/userguide/build_environment.html#sec:gradle_properties_files) or [Environment Variables](https://docs.gradle.org/current/userguide/build_environment.html#sec:environment_variables) instead of hardcoding.

3.  **Build the Project:**
    Open the project in Android Studio and build it.

4.  **Generate and Copy APK File:**
    Generate the APK file and copy it to a USB memory stick.

5.  **Connect USB Memory to SoluM Signage Device:**
    Connect the USB memory stick to your SoluM signage device.

6.  **Install via CMS PlayWizard:**
    On the device, locate and run `CMS PlayWizard`. When `Solum Signage example` appears on the screen, follow the on-screen instructions to install it.

7.  **Run the Application:**
    Once the installation is complete, run the `Solum Signage example` application.

---

### License
```
Copyright © 2025 SOLUM. All rights reserved.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```