<div align="center">
<img width="1200" height="475" alt="GHBanner" src="https://ai.google.dev/static/site-assets/images/share-ais-513315318.png" />
</div>

# Run and deploy your AI Studio app

This contains everything you need to run your app locally.

View your app in AI Studio: https://ai.studio/apps/b042c2ab-afce-4297-8d92-42f64c5babf8

## Run Locally

**Prerequisites:**  [Android Studio](https://developer.android.com/studio)


1. Open Android Studio
2. Select **Open** and choose the directory containing this project
3. Allow Android Studio to fix any incompatibilities as it imports the project.
4. Create a file named `local.properties` in the project directory (or edit the existing one) and set `GEMINI_API_KEY` in that file to your Gemini API key, e.g. `GEMINI_API_KEY=YOUR_KEY_HERE`. This file is gitignored and is never uploaded. You can create a key at https://aistudio.google.com/apikey (do not put the key in `.env`, `.env.example` or anywhere that gets committed).
5. Remove this line from the app's `build.gradle.kts` file: `signingConfig = signingConfigs.getByName("debugConfig")`
6. Run the app on an emulator or physical device
7. If you have already published your app in AI Studio, please [request upload key reset](https://support.google.com/googleplay/android-developer/answer/9842756#zippy=%2Crequest-an-upload-key-reset) in Google Play Console.
