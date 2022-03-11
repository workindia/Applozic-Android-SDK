<div style="width:100%">
    <div style="width:50%; display:inline-block">
        <p align="center">
         <img style="border-radius: 50px;" align="center" alt="Applozic's LOGO" src="https://i.imgur.com/mGIQIXy.png?1">
        </p>
    </div>
</div>

# Official Android SDK for [Chat](https://docs.applozic.com/docs/android-integration-overview) :speech_balloon:

![Platform](https://img.shields.io/badge/Platform-Android-brightgreen.svg)
![Platform](https://img.shields.io/badge/Language-java-yellow.svg)
![GitHub repo size](https://img.shields.io/github/repo-size/AppLozic/Applozic-Android-SDK)
![GitHub contributors](https://img.shields.io/github/contributors/AppLozic/Applozic-Android-SDK)
![GitHub stars](https://img.shields.io/github/stars/AppLozic/Applozic-Android-SDK?style=social)
![Twitter Follow](https://img.shields.io/twitter/follow/Applozic?style=social)

## Introduction :cyclone:         

<img align="right" src="https://i.imgur.com/OK9dSLS.png?1" />


Applozic brings real-time engagement with chat, video, and voice to your web,
mobile, and conversational apps. We power emerging startups and established
companies with the most scalable and powerful chat APIs, enabling application
product teams to drive better user engagement, and reduce time-to-market.

Customers and developers from over 50+ countries use us and love us, from online
marketplaces and eCommerce to on-demand services, to Education Tech, Health
Tech, Gaming, Live-Streaming, and more.

Our feature-rich product includes robust client-side SDKs for iOS, Android, React
Native, and Flutter. We also support popular server-side languages, a beautifully
customizable UI kit, and flexible platform APIs.

Chat, video, and audio-calling have become the new norm in the post-COVID era,
and we're bridging the gap between businesses and customers by delivering those
exact solutions.

## Table of Contents :beginner:

* [Quick Start](#quickstart)
   * [Setting Up Android Studio for new project](#setting-android-studio)
   * [Integrating Sample App](sample-app)
* [Announcements](#announcements)
* [Roadmap](#roadmap)
* [Features](#feature)
* [About](#about)
* [License](#license)


## Prerequisites :crystal_ball:

:one: [Android Studio](https://developer.android.com/studio) (latest version recommended)<br>
:two: ```Android Device | Emulator``` with Android Version 6.0+.<br>
:three: [Sign-Up](https://www.applozic.com/signup.html?utm_source=github&utm_medium=readme&utm_campaign=android) or Login to get your Applozic's [API/Application Key](https://console.applozic.com/settings/install). <br>

<a name="quickstart"></a>
## Quick Start :rocket:

Before getting started with installation. We recommend to go through some basic documentation for [Android Chat & Messaging SDK Documentation](https://www.applozic.com/docs/android-chat-sdk.html?utm_source=github&utm_medium=readme&utm_campaign=android) :memo: <br>

<a name="setting-android-studio"></a>
### Setting up Android Studio

* Create a new project using **File ➙ New Project** on the top right of application<br>
* Rename the project as per your preference (we will name it as **applozic-first-app**)

<a name="sample-app"></a>
### Step 1: Adding in app build.gradle:      

* Make sure you open your app's build.gradle (*hint: Gradle Scripts ➙ build.grade(Module: \<your-app-name>.app)*) add the below line in **```dependencies{}```**.

```bash
implementation 'com.github.workindia:Applozic-Android-SDK:5.101.0b'
```

**Note: Versions from *v5.99.0* and onwards will be hosted at Jfrog Artifactory.**

Add the following repo to you project level *build.gradle* inside *allProjects { repositories { ... }}*:
```bash
maven {
    url 'https://jitpack.io'
}
```

Older versions can still be consumed from Jcenter.


* Add the below code in your gradle **```android{}```** target:      

```java
        packagingOptions {           
           exclude 'META-INF/DEPENDENCIES'      
           exclude 'META-INF/NOTICE'         
           exclude 'META-INF/LICENSE'      
           exclude 'META-INF/LICENSE.txt'    
           exclude 'META-INF/NOTICE.txt' 
           exclude 'META-INF/ECLIPSE_.SF'
           exclude 'META-INF/ECLIPSE_.RSA'
         }    
```

### Step 2: Add Activities, Services and Receivers in androidmanifest.xml:
        
**Note:**<br>
  * Add meta-data, Activities, Services and Receivers within application Tag ``` <application> </application> ```<br>

```xml

<!-- Applozic App ID -->
<meta-data android:name="com.applozic.application.key"
           android:value="<YOUR_APPLOZIC_APP_ID" /> 

<!-- Launcher white Icon -->
<meta-data android:name="com.applozic.mobicomkit.notification.smallIcon"
           android:resource="YOUR_LAUNCHER_SMALL_ICON" /> 

<!-- Notification color -->
<meta-data android:name="com.applozic.mobicomkit.notification.color"
           android:resource="YOUR_NOTIFICATION_COLOR_RESOURCE" /> 

<!--Replace with your geo api key from google developer console  --> 
<!-- For testing purpose use AIzaSyAYB1vPc4cpn_FJv68eS_ZGe1UasBNwxLI
To disable the location sharing via map add this line ApplozicSetting.getInstance(context).disableLocationSharingViaMap(); in onSuccess of Applozic UserLoginTask -->             
<meta-data android:name="com.google.android.geo.API_KEY"
           android:value="YOUR_GEO_API_KEY" />  

<!-- NOTE: Do NOT change this, it should remain same i.e 'com.package.name' -->            
<meta-data android:name="com.package.name" 
           android:value="${applicationId}" /> 
                     
```

**Note:** If you are *not using gradle build* you need to replace **```${applicationId}```**  with your Android app package name

* Define Attachment Folder Name in your string.xml.          
     
```html
<string name="default_media_location_folder">YOUR_APP_NAME</string> 
```

* Paste the following in your androidmanifest.xml:        

```xml
<activity android:name="com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity"
           android:configChanges="keyboardHidden|screenSize|smallestScreenSize|screenLayout|orientation"
           android:label="@string/app_name"
           android:parentActivityName="<APP_PARENT_ACTIVITY>"
           android:theme="@style/ApplozicTheme"
           android:launchMode="singleTask"
           tools:node="replace">
      <!-- Parent activity meta-data to support API level 7+ -->
<meta-data
           android:name="android.support.PARENT_ACTIVITY"
           android:value="<APP_PARENT_ACTIVITY>" />
 </activity>               
```

* Replace APP_PARENT_ACTIVITY with your app's parent activity (reference below). 

```xml
<!-- you will be having .MainActivity-->
        <activity android:name="com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity"
            android:configChanges="keyboardHidden|screenSize|smallestScreenSize|screenLayout|orientation"
            android:label="@string/app_name"
            android:parentActivityName=".MainActivity"
            android:theme="@style/ApplozicTheme"
            android:launchMode="singleTask"
            tools:node="replace">
            <!-- Parent activity meta-data to support API level 7+ -->
            <meta-data
                android:name="android.support.PARENT_ACTIVITY"
                android:value=".MainActivity" />
        </activity>
```

### Step 3: Register user account in your code:     

* For creating your first user we need to create an New user object which can be created using below code.
     
```java
User user = new User();          
user.setUserId(userId); //userId it can be any unique user identifier
user.setDisplayName(displayName); //displayName is the name of the user which will be shown in chat messages
user.setEmail(email); //optional  
user.setAuthenticationTypeId(User.AuthenticationType.APPLOZIC.getValue());  //User.AuthenticationType.APPLOZIC.getValue() for password verification from Applozic server and User.AuthenticationType.CLIENT.getValue() for access Token verification from your server set access token as password
user.setPassword(""); //optional, leave it blank for testing purpose, read this if you want to add additional security by verifying password from your server https://www.applozic.com/docs/configuration.html#access-token-url
user.setImageLink("");//optional,pass your image link

 Applozic.connectUser(context, user, new AlLoginHandler() {
                @Override
                public void onSuccess(RegistrationResponse registrationResponse, Context context) {
                    // After successful registration with Applozic server the callback will come here 
                }

                @Override
                public void onFailure(RegistrationResponse registrationResponse, Exception exception) {
                    // If any failure in registration the callback  will come here 
             }
   });                                      
```

If it is a new user, new user account will get created else existing user will be logged in to the application. You can check if user is logged in to applozic or not by using **``` Applozic.isConnected(context) ```**

### Step 4: Push Notification Setup 🔔

*Note : Go to Applozic Dashboard, Edit Application -> Push Notification -> Android -> GCM/FCM Server Key.*

* Firebase Cloud Messaging (FCM)  is already enabled in my app
    * Add the below code and pass the FCM registration token:
  
1. UserLoginTask "onSuccess" (refer Step 3)

```java
if(MobiComUserPreference.getInstance(context).isRegistered()) {
  Applozic.registerForPushNotification(context, registrationToken, new AlPushNotificationHandler() {
                @Override
                public void onSuccess(RegistrationResponse registrationResponse) {
                   
                }

                @Override
                public void onFailure(RegistrationResponse registrationResponse, Exception exception) {

                }
    });
}
```

 2. In your FcmListenerService onNewToken(Token registrationToken) method

 ```java
 if (MobiComUserPreference.getInstance(this).isRegistered()) {
      new RegisterUserClientService(this).updatePushNotificationId(registrationToken);
 }
```

### For Receiving Notifications in FCM

* Add the following in your FcmListenerService in onMessageReceived(RemoteMessage remoteMessage) 

```java
 if (MobiComPushReceiver.isMobiComPushNotification(remoteMessage.getData())) {
           MobiComPushReceiver.processMessageAsync(this, remoteMessage.getData());
           return;
   }
```

### GCM is already enabled in my app

* If you already have GCM enabled in your app, add the below code and pass the GCM registration token:
  
1. In UserLoginTask "onSuccess" (refer Step 3)
  
```java
if(MobiComUserPreference.getInstance(context).isRegistered()) {
  Applozic.registerForPushNotification(context, registrationToken, new AlPushNotificationHandler() {
                @Override
                public void onSuccess(RegistrationResponse registrationResponse) {
                   
                }

                @Override
                public void onFailure(RegistrationResponse registrationResponse, Exception exception) {

                }
     });
}
```

2. At the place where you are getting the GCM registration token in your app.       

 ```java
 if (MobiComUserPreference.getInstance(this).isRegistered()) {
      new RegisterUserClientService(this).updatePushNotificationId(registrationToken);
 }
```

### For Receiving Notifications In GCM

* Add the following in your GcmListenerService  in onMessageReceived 

```java
if(MobiComPushReceiver.isMobiComPushNotification(data)) {            
        MobiComPushReceiver.processMessageAsync(this, data);               
        return;          
}                                          
```

### Don't have Android Push Notification code ?

* To Enable Android Push Notification using Firebase Cloud Messaging (FCM) 
    * visit the [Firebase console](https://console.firebase.google.com) 
    * Create new project
    * Add the google service json to your app.
    * Configure the build.gradle files in your app.
    * Get server key from project settings.
    * Update in **[Applozic Dashboard](https://console.applozic.com/settings/pushnotification)** under **Push Notification -> Android -> GCM/FCM Server Key**.

* In case, if you don't have the existing FCM related code, then copy the push notification related files from Applozic sample app to your project from the below github link
    * [Github push notification code link](https://github.com/AppLozic/Applozic-Android-SDK/tree/master/app/src/main/java/com/applozic/mobicomkit/sample/pushnotification)

* And add below code in your androidmanifest.xml file

```xml 
<service android:name="<CLASS_PACKAGE>.FcmListenerService"
android:stopWithTask="false">
        <intent-filter>
            <action android:name="com.google.firebase.MESSAGING_EVENT" />
        </intent-filter>
</service>
  ``` 
  
### Setup PushNotificationTask in UserLoginTask "onSuccess" (refer Step 3).

```java
Applozic.registerForPushNotification(context, Applozic.getInstance(context).getDeviceRegistrationId(), new   AlPushNotificationHandler() {
                @Override
                public void onSuccess(RegistrationResponse registrationResponse) {
                   
                }

                @Override
                public void onFailure(RegistrationResponse registrationResponse, Exception exception) {

                }
    });
```


### Step 5: For starting the messaging activity        
      
```java
Intent intent = new Intent(this, ConversationActivity.class);            
startActivity(intent);                               
``` 
  
* For starting individual conversation thread, set "userId" in intent:        
            
```java
Intent intent = new Intent(this, ConversationActivity.class);            
intent.putExtra(ConversationUIService.USER_ID, "receiveruserid123");             
intent.putExtra(ConversationUIService.DISPLAY_NAME, "Receiver display name"); //put it for displaying the title.  
intent.putExtra(ConversationUIService.TAKE_ORDER,true); //Skip chat list for showing on back press 
startActivity(intent);

```

### Step 6: On logout, call the following:       

```java
Applozic.logoutUser(context, new AlLogoutHandler() {
                @Override
                public void onSuccess(Context context) {
                    
                }

                @Override
                public void onFailure(Exception exception) {

                }
        });     
 ```

<a name="documentation"></a>
## Documentation :book:

**Trying out the demo app:**

* Open project in Android Studio to run the sample app in your device & Send messages between multiple devices. 
* Display name for users:
    * You can either choose to handle display name from your app or have Applozic handle it.
    * From your app's first activity, set the following to disable display name feature:
    * **```ApplozicClient.getInstance(this).setHandleDisplayName(false);```**
    * By default, the display name feature is enabled.
* For advanced options and customization, visit [Applozic Android Chat & Messaging SDK Documentation](https://www.applozic.com/docs/android-chat-sdk.html?utm_source=github&utm_medium=readme&utm_campaign=android)

<a name="announcements"></a>
## Announcements :loudspeaker: 

v5.98 has been released! Please see the [release notes](https://github.com/AppLozic/Applozic-Android-SDK/releases/tag/v5.98) for details.

All updates to this library are documented in our [releases](https://github.com/AppLozic/Applozic-Android-SDK/releases). For any queries, feel free to reach out us at github@applozic.com

<a name="roadmap"></a>
## Roadmap :vertical_traffic_light:

If you are interested in the future direction of this project, please take a look at our open [issues](https://github.com/AppLozic/Applozic-Android-SDK/issues) and [pull requests](https://github.com/AppLozic/Applozic-Android-SDK/pulls).<br> We would :heart: to hear your feedback.

[Changelog](https://github.com/AppLozic/Applozic-Android-SDK/blob/master/CHANGELOG.md)

<a name="feature"></a>
## Features :confetti_ball:

* One to one and Group Chat
* Image capture
* Photo sharing
* File attachment
* Location sharing
* Push notifications
* In App notifications
* Online presence
* Last seen at
* Unread message count
* Typing indicator
* Message sent
* Read Recipients and Delivery report
* Offline messaging
* User block/unblock
* Multi Device sync
* Application to user messaging
* Customized chat bubble
* UI Customization Toolkit
* Cross Platform Support(iOS,Android&Web)


<a name="about"></a>
## About & Help/Support :rainbow:

We provide support over at [StackOverflow](http://stackoverflow.com/questions/tagged/applozic) when you tag using applozic, ask us anything.

* Applozic is the best android chat sdk for instant messaging, still not convinced? 
    - Write to us at github@applozic.com 
    - We will be happy to schedule a demo for you.
    - Special plans for startup and open source contributors.

* Android Chat SDK https://github.com/AppLozic/Applozic-Android-SDK
* Web Chat Plugin https://github.com/AppLozic/Applozic-Web-Plugin
* iOS Chat SDK https://github.com/AppLozic/Applozic-iOS-SDK

<a name="license"></a>
## License :heavy_check_mark:
This code library fully developed and supported by Applozic's [team of contributors](https://github.com/AppLozic/Applozic-Android-SDK/graphs/contributors):sunglasses: and licensed under the [BSD-3 Clause License](https://github.com/AppLozic/Applozic-Android-SDK/blob/master/LICENSE). 
