# uber-clone

A clone of one of the most popular apps in recent times.

So Uber is a pretty straightforward idea. A rider taps on the app when they want to take a car somewhere, and then a nearby driver is alerted of that rider and then goes and pick them up.

<img align="left" alt="java" height="25px" src="https://upload.wikimedia.org/wikipedia/de/e/e1/Java-Logo.svg" />
<img align="left" alt="gradle" height="25px" src="https://upload.wikimedia.org/wikipedia/commons/6/6b/Gradle_logo.svg" />
<img align="left" alt="android studio" height="25px" src="https://upload.wikimedia.org/wikipedia/commons/5/55/Android_Studio_Logo_%282023%29.svg" />

<img align="left" alt="AWS" height="25px" src="https://upload.wikimedia.org/wikipedia/commons/9/93/Amazon_Web_Services_Logo.svg" />
<img align="left" alt="Parse" height="25px" src="https://parseplatform.org/img/logo.svg" />
<br/>
<br/>

# What I have learned 👍 

* Server cloud storage skills with **`maps`** and **`GPS`**, which are perhaps the two most powerful aspects of mobile apps working together.
* **`AWS`**, creating a new **`EC2`** instance for our **`Parse Server`** code.
* How to use **`Google Map SDK`**
* How to setup **`google console cloud`**. restricting _**API**_.
* How to search for a **`Location`**, adding **`Marker`**in map, **`Navigation`**,
* **`Parse Query:`** inserting, deleting, updating and How to store GeoLocation.
* How to get **`User`** **`Location`** and share location with other.

***
# Features
- User authentication (Sign Up, Log In)
- Activity tracking
- Notifications upon new Rider request post
- Driver LOcation Tracking
- Cancle a request.
- one app for both Driver and Rider

***
# Screenshots 
<p align="center">
  <img src="app/src/main/res/raw/ss_4_user_make_a_reques.jpeg" alt="Image 1" width="200" style="margin: 30px;"/>
  <img src="app/src/main/res/raw/ss_2.jpeg" alt="Image 2" width="200" style="margin: 30px;"/>
  <img src="app/src/main/res/raw/ss_3_driver_seeing_all_available_request.jpeg" alt="Image 3" width="200" style="margin: 30px;"/>
  <img src="app/src/main/res/raw/ss_1.jpeg" alt="Image 4" width="200" style="margin: 30px;"/>
  <img src="app/src/main/res/raw/ss_5_driver_seeing_over_view.jpeg" alt="Image 4" width="200" style="margin: 30px;"/>
  <img src="app/src/main/res/raw/ss_6_driver_is_coming.jpeg" alt="Image 4" width="200" style="margin: 30px;"/>
  <img src="app/src/main/res/raw/ss_7_request_not_accepted.jpeg" alt="Image 4" width="200" style="margin: 30px;"/>
  
</p>

***
# Installation
1. Clone the repository
   ```
   git clone https://github.com/nazmos-sakib/uber-clone.git
   ```
2. Open the project in Android Studio
3. Build the project and run it on an emulator or physical device

***
# Requirements

### Software
- **Android Studio** 4.0 or higher
- **Android SDK** 26 or higher
- **Build Tools Version** 34
- **Gradle.kts Version** gradle-8.2 

### Libraries
- **Parse**: `implementation 'com.github.parse-community.Parse-SDK-Android:parse:4.2.0'`
- **Google Map**: `"com.google.android.gms:play-services-maps:18.2.0"`
- **multidex**: `"com.android.support:multidex:1.0.3"`

### Hardware
- **GPS**: Required for location tracking

### Permissions
- `android.permission.INTERNET`
- `android.permission.ACCESS_FINE_LOCATION`
- `android.permission.ACCESS_COARSE_LOCATION`
- `android.permission.ACCESS_NETWORK_STATE`

***
# License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
