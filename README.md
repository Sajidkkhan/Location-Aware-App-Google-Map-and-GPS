# Location-Aware-App-Google-map-and-GPS
Google Map and GPS Android Java Project
Google Map and Location Aware App using GPS: 
Add following dependencies to your project gradle dependencies
implementation("com.google.android.gms:play-services-maps:18.2.0")
implementation("com.google.android.gms:play-services-location:21.0.1")
implementation("com.google.android.gms:play-services-places:17.0.0")

To Access Google Maps, and GPS location based services you need to Add following Permissions to Manefist file.
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />

Also Add API Key to Manefis file.
    <meta-data
            android:name="com.google.android.geo.API_KEY"
            android:value="@string/google_maps_key" />
            
Before you run your application, you need a Google Maps API key. 
To get one, follow the directions here: https://developers.google.com/maps/documentation/android-sdk/get-api-key 
Once you have your API key (it starts with "AIza"),
define a new property in your project's local.properties file (e.g. MAPS_API_KEY=Aiza...), 
and replace the "YOUR_API_KEY" string in this file with "${MAPS_API_KEY}".
