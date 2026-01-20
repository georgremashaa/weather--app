# Weather-App

This Android app is a simple Weather Forecast app using OpenWeatherMap API.

Important: Do NOT commit real API keys to public repositories.

How to add your OpenWeatherMap API key:

1. Sign up at https://home.openweathermap.org/users/sign_up and obtain an API key.
2. Open `app/src/main/res/values/strings.xml` and replace the value of `openweather_api_key` with your API key.
   Example:
   `<string name="openweather_api_key">abcd1234yourkeyhere</string>`
3. Open the project in Android Studio and run.

Alternative (safer): store the API key in `local.properties` and inject it into BuildConfig via Gradle.

USB Debugging & deployment:
- Enable Developer Options on your device and enable USB debugging.
- Connect device, then Run the app from Android Studio.

Troubleshooting:
- Ensure `INTERNET` permission is in AndroidManifest.xml.
- Check Logcat for Retrofit logs (tag: Retrofit).
