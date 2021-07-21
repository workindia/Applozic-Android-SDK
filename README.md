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
   * [Prerequisites](#prerequisites)
   * [Integrating the Applozic SDK](#integration)
   * [Setting up the SDK](#setting-up)
   * [Authentication](#authentication)
   * [Sending a message](#sending-message)
* [Announcements](#announcements)
* [Roadmap](#roadmap)
* [Features](#feature)
* [About](#about)
* [License](#license)

<a name="quickstart"></a>
## Quick Start :rocket:

<a name="prerequisites"></a>
## Step 0: Before we start integrating the SDK, there are some tasks we need to do :crystal_ball:

### Sign up to Applozic

[Sign up](https://www.applozic.com/signup.html?utm_source=github&utm_medium=readme&utm_campaign=android) to the Applozic Dashboard and [get your application key](https://console.applozic.com/settings/install). Keep this handy.

### Create a tutorial project

**1:** Install and open [Android Studio](https://developer.android.com/studio) and create a new project. Choose an empty activity.

<img align="middle" src="https://user-images.githubusercontent.com/73516112/126483090-0362c713-d89c-42ab-ab57-ba541696260d.png" width="600"/>


**2:**  Fill in the project details.
- Keep the minimum SDK version 21 or higher
- Do not use the legacy support library. AndroidX is required.
It should look like this:

<img align="middle" src="https://user-images.githubusercontent.com/73516112/126483366-1bdf42e1-97df-4f84-bde6-21b8f98ebda1.png" width="600"/>


<a name="integration"></a>
## Step 2: Now, let's start integrating 🧰

**1: Open your project level `build.gradle`.**

Add the path to the *Applozic repository* inside `allprojects` as shown:

```groovy
allprojects {
   repositories {
       google()
       jcenter()

       maven {
           url 'https://applozic.jfrog.io/artifactory/applozic-android-sdk'
       }
   }
}
```

**2: Open your app level `build.gradle` and add the dependency to the *Applozic SDK*:**

```groovy
dependencies {
   implementation 'com.applozic.communication.uiwidget:mobicomkitui:5.101.0'
   //other dependencies
}
```

**3: Sync the project.**

<a name="setting-up"></a>
## Step 3: Setting up the SDK ⚙️

Open the project’s `AndroidManifest.xml` file and paste the following code inside the `<application>` tag:

```xml
<meta-data android:name="com.applozic.application.key"
   android:value="applozic-sample-app" />

<meta-data android:name="com.package.name"
   android:value="${applicationId}" />
```

In the future you can replace `applozic-sample-app` with the *application id* you got from the *Applozic Dashboard*. For now, keep it as it is.

<a name="authentication"></a>
## Step 4: Creating and authenticating a user 🔐

Open `MainActivity.java` in your project and paste the following code inside the `onCreate()` method.

```java
Toast.makeText(MainActivity.this, "Connecting user...", Toast.LENGTH_LONG).show();

User user = new User();
user.setUserId(“applozicuser");
user.setPassword("password");

Applozic.connectUser(this, user, new AlLoginHandler() {
   @Override
   public void onSuccess(RegistrationResponse registrationResponse, Context context) {
       Toast.makeText(MainActivity.this, "Login successful...", Toast.LENGTH_LONG).show();
   }

   @Override
   public void onFailure(RegistrationResponse registrationResponse, Exception exception) {
       Toast.makeText(MainActivity.this, "Login failed...", Toast.LENGTH_LONG).show();
   }
});
```

***The `User` object above already has some `userId` and `password`. Keep it as it is for now.***

> What we are doing here, is that first we create a new `User` object.
> An Applozic User is an entity that “uses” the chat to send and receive messages. 
> A user is identified by its `userId`.
> Then we “connect” that user to Applozic servers. This connects and authenticates the user and sends back an authentication token that is saved and used by the SDK for future server calls.
> If the user object being passed to `Applozic.connectUser()` is unregistered, then it is registered in the server first and then authenticated. 


<a name="sending-message"></a>
## Step 5: Opening a conversation and sending the user’s first message 📨

Replace the code inside the `AlLoginHandler#onSuccess()` method that is being passed to `Applozic.connectUser()` with:

```java
Toast.makeText(MainActivity.this, "Login successful. Opening conversation...", Toast.LENGTH_LONG).show();

Intent intent = new Intent(MainActivity.this, ConversationActivity.class);
intent.putExtra(ConversationUIService.USER_ID, "lilapplozic");
startActivity(intent);
```

This will open a conversation with the user *“lilapplozic”*.

The `onCreate()` inside `MainActicity.java` method should finally look like this:

```java
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toast.makeText(MainActivity.this, "Connecting user...", Toast.LENGTH_LONG).show();

        User user = new User();
        user.setUserId("applozicuser");
        user.setPassword("password");

        Applozic.connectUser(this, user, new AlLoginHandler() {
            @Override
            public void onSuccess(RegistrationResponse registrationResponse, Context context) {
                Toast.makeText(MainActivity.this, "Login successful. Opening conversation...", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(MainActivity.this, ConversationActivity.class);
                intent.putExtra(ConversationUIService.USER_ID, "lilapplozic");
                startActivity(intent);
            }

            @Override
            public void onFailure(RegistrationResponse registrationResponse, Exception exception) {
                Toast.makeText(MainActivity.this, "Login failed...", Toast.LENGTH_LONG).show();
            }
        });
    }
```


## Congratulations 🥳
You have successfully integrated Applozic.
When you open the app you will be taken to the respective conversation.

<a name="documentation"></a>
# Documentation :book:

**Trying out the demo app:**

* Open project in Android Studio to run the sample app in your device & Send messages between multiple devices. 

* For advanced options and customization, visit [Applozic Android Chat & Messaging SDK Documentation](https://www.applozic.com/docs/android-chat-sdk.html?utm_source=github&utm_medium=readme&utm_campaign=android)

<a name="announcements"></a>
## Announcements :loudspeaker: 

v5.101.0 has been released! Please see the [release notes](https://github.com/AppLozic/Applozic-Android-SDK/releases/tag/v5.101.0) for details.

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
