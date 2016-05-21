# Applozic-Android-SDK
Android Chat SDK


### Overview         

<img align="right" src="https://raw.githubusercontent.com/AppLozic/Applozic-Android-SDK/master/img/android.png" />


Open source Android Chat SDK / Messaging SDK that lets you add real time chat and in-app messaging in your mobile (android, iOS) applications and website.

Signup at https://www.applozic.com/signup.html to get the application key.

Applozic One to One and Group Chat SDK



### Getting Started       


###### Quick Start for Applozic Messaging   

To integrate android chat library into your android app, signup at [Applozic](https://www.applozic.com/signup.html?utm_source=android&utm_medium=github) to get the application key.

Documentation: [Applozic Developers](https://www.applozic.com/developers.html#android-sdk-getting-started)

**Step 1: Add the following in your build.gradle dependency**:      

`compile 'com.applozic.communication.uiwidget:mobicomkitui:3.39' `


Add the following in gradle android target:      


```
android {

        packagingOptions    
         {           
           exclude 'META-INF/DEPENDENCIES'      
           exclude 'META-INF/NOTICE'         
           exclude 'META-INF/LICENSE'      
           exclude 'META-INF/LICENSE.txt'    
           exclude 'META-INF/NOTICE.txt' 
           exclude 'META-INF/ECLIPSE_.SF'
           exclude 'META-INF/ECLIPSE_.RSA'
         }    
    }               
```

**Step 2: Addition of Permissions,Activities, Services and Receivers in androidmanifest.xml**:
          
```

<meta-data android:name="com.applozic.application.key"
           android:value="YOUR_APPLOZIC_APPLICATION_KEY" /> <!-- Applozic Application Key -->

<meta-data android:name="com.applozic.mobicomkit.notification.icon" 
           android:resource="YOUR_LAUNCHER_ICON" />  <!-- Launcher Icon -->

<meta-data android:name="com.applozic.mobicomkit.notification.smallIcon"
           android:resource="YOUR_LAUNCHER_SMALL_ICON" /> <!-- Launcher white Icon -->
           
<meta-data android:name="share_text"
           android:value="YOUR INVITE MESSAGE" />  <!-- Invite Message -->
           
<meta-data android:name="main_folder_name"
           android:value="@string/default_media_location_folder" /> <!-- Attachment Folder Name -->
           
 <meta-data android:name="com.google.android.geo.API_KEY"
            android:value="YOUR_GEO_API_KEY" />  <!--Replace with your geo api key from google developer console  --> 
<!-- For testing purpose use AIzaSyAYB1vPc4cpn_FJv68eS_ZGe1UasBNwxLI
To disable the location sharing via map add this line ApplozicSetting.getInstance(context).disableLocationSharingViaMap(); in onSuccess of Applozic UserLoginTask -->
           
 <meta-data android:name="activity.open.on.notification"
            android:value="com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity" /> <!-- NOTE : Do NOT change this value -->    
            
 <meta-data android:name="com.package.name" 
           android:value="${applicationId}" /> <!-- NOTE: Do NOT change this, it should remain same i.e 'com.package.name' -->
         
```
   **Note**: If you are **not using gradle build** you need to replace ${applicationId}  with your Android app package name

  
  Define Attachment Folder Name in your string.xml.          
     
```
<string name="default_media_location_folder"><YOUR_APP_NAME></string> 
```



Permissions:          






```
<uses-permission android:name="<APP_PKG_NAME>.permission.C2D_MESSAGE" />
<uses-permission android:name="<APP_PKG_NAME>.permission.MAPS_RECEIVE" />
<permission android:name="<APP_PKG_NAME>.permission.C2D_MESSAGE" android:protectionLevel="signature" />
<permission android:name="<APP_PKG_NAME>.permission.MAPS_RECEIVE" android:protectionLevel="signature" />
<uses-permission android:name="com.google.android.c2dm.permission.RECEIVE" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"  />
<uses-permission android:name="android.permission.READ_CONTACTS" />
<uses-permission android:name="android.permission.WRITE_CONTACTS" />
<uses-permission android:name="android.permission.VIBRATE"/>
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.GET_TASKS" />                                              <uses-permission android:name="android.permission.CALL_PHONE" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
  ```


Broadcast Registration For PushNotification:        


   
```
<receiver android:name="com.applozic.mobicomkit.uiwidgets.notification.MTNotificationBroadcastReceiver">
   <intent-filter>            
        <action android:name="${applicationId}.send.notification"/>                    
   </intent-filter>           
</receiver>                  
```

**Note**: If you are **not using gradle build** you need to replace ${applicationId}  with your Android app package name



Paste the following in your androidmanifest.xml:       




   
```
 <activity android:name="com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity"
           android:configChanges="keyboardHidden|orientation|screenSize"
           android:label="@string/app_name"
           android:parentActivityName="<APP_PARENT_ACTIVITY>"
           android:theme="@style/ApplozicTheme"
           android:launchMode="singleTask" >
      <!-- Parent activity meta-data to support API level 7+ -->
<meta-data
           android:name="android.support.PARENT_ACTIVITY"
           android:value="<APP_PARENT_ACTIVITY>" />
 </activity>
                   
<activity android:name="com.applozic.mobicomkit.uiwidgets.people.activity.MobiComKitPeopleActivity"
          android:configChanges="keyboardHidden|orientation|screenSize"
          android:label="@string/activity_contacts_list"
          android:parentActivityName="com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity"
          android:theme="@style/Applozic.People.Theme"
          android:windowSoftInputMode="adjustResize">
     <!-- Parent activity meta-data to support API level 7+ -->
<meta-data
          android:name="android.support.PARENT_ACTIVITY"
          android:value="com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity" />
         <intent-filter>
                 <action android:name="android.intent.action.SEARCH" />
         </intent-filter>
<meta-data
          android:name="android.app.searchable"
          android:resource="@xml/searchable_contacts" />
</activity>

<activity android:name="com.applozic.mobicomkit.uiwidgets.conversation.activity.FullScreenImageActivity"
          android:configChanges="keyboardHidden|orientation|screenSize"
          android:label="Image"
 android:parentActivityName="com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity"
          android:theme="@style/Applozic_FullScreen_Theme">
    <!-- Parent activity meta-data to support API level 7+ -->
<meta-data
          android:name="android.support.PARENT_ACTIVITY"
          android:value="com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity" />
</activity>

<activity
    android:name="com.applozic.mobicomkit.uiwidgets.conversation.activity.ContactSelectionActivity"
    android:configChanges="keyboardHidden|orientation|screenSize"
    android:launchMode="singleTop"
    android:parentActivityName="com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity"
    android:theme="@style/ApplozicTheme">
 <meta-data
           android:name="android.support.PARENT_ACTIVITY"
           android:value="com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity" />
</activity>

<activity
    android:name="com.applozic.mobicomkit.uiwidgets.conversation.activity.ChannelCreateActivity"
    android:configChanges="keyboardHidden|orientation|screenSize"
    android:launchMode="singleTop"
    android:parentActivityName="com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity"
    android:theme="@style/ApplozicTheme">
<meta-data
          android:name="android.support.PARENT_ACTIVITY"
          android:value="com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity" />
</activity>

<activity android:name="com.applozic.mobicomkit.uiwidgets.conversation.activity.ChannelNameActivity"
           android:configChanges="keyboardHidden|orientation|screenSize"
           android:launchMode="singleTop"
           android:parentActivityName="com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity"
           android:theme="@style/ApplozicTheme">
</activity>

 <activity android:name="com.applozic.mobicomkit.uiwidgets.conversation.activity.ChannelInfoActivity"
           android:configChanges="keyboardHidden|orientation|screenSize"
           android:launchMode="singleTop"
           android:parentActivityName="com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity"
           android:theme="@style/ApplozicTheme">
  <meta-data
           android:name="android.support.PARENT_ACTIVITY"
           android:value="com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity" />
 </activity>

<activity
     android:name="com.applozic.mobicomkit.uiwidgets.conversation.activity.MobiComAttachmentSelectorActivity"
     android:configChanges="keyboardHidden|orientation|screenSize"
     android:launchMode="singleTop"
     android:parentActivityName="com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity"
     android:theme="@style/ApplozicTheme"
     android:windowSoftInputMode="stateHidden|adjustResize">
 <meta-data 
           android:name="android.support.PARENT_ACTIVITY"
           android:value="com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity" />
  </activity>
  
  <activity android:name="com.applozic.mobicomkit.uiwidgets.conversation.activity.MobicomLocationActivity"
            android:configChanges="keyboardHidden|orientation|screenSize"
            android:parentActivityName="com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity"
            android:theme="@style/ApplozicTheme"
            android:windowSoftInputMode="adjustResize">
 </activity>

                   
<service android:name="com.applozic.mobicomkit.api.conversation.MessageIntentService"
          android:exported="false" />
              
<service android:name="org.eclipse.paho.android.service.MqttService"/>

<service android:name="com.applozic.mobicomkit.api.conversation.ApplozicIntentService"
         android:exported="false" />
             
<service android:name="com.applozic.mobicomkit.api.conversation.ApplozicMqttIntentService"
         android:exported="false" />

<receiver android:name="com.applozic.mobicomkit.broadcast.TimeChangeBroadcastReceiver">
         <intent-filter>
                 <action android:name="android.intent.action.TIME_SET" />
                 <action android:name="android.intent.action.TIMEZONE_CHANGED" />
         </intent-filter>
</receiver>

<receiver android:name="com.applozic.mobicomkit.broadcast.ConnectivityReceiver"
          android:exported="true" android:enabled="true">
          <intent-filter>
                  <action android:name="android.intent.action.BOOT_COMPLETED" />
                  <action android:name="android.net.conn.CONNECTIVITY_CHANGE" />
          </intent-filter>
</receiver>                  
```






Replace APP_PARENT_ACTIVITY with your app's parent activity.        

**Step 3: Register user account**:     



     
```
UserLoginTask.TaskListener listener = new UserLoginTask.TaskListener() {                  

@Override          
public void onSuccess(RegistrationResponse registrationResponse, Context context)         
{           
   // After successful registration with Applozic server the callback will come here 
    ApplozicSetting.getInstance(context).showStartNewButton();//To show contact list.
}                       

@Override             
public void onFailure(RegistrationResponse registrationResponse, Exception exception)         
{  
    // If any failure in registration the callback  will come here 
}};                      

User user = new User();          
user.setUserId(userId); //userId it can be any unique user identifier
user.setDisplayName(displayName); //displayName is the name of the user which will be shown in chat messages
user.setEmail(email); //optional                        
new UserLoginTask(user, listener, this).execute((Void) null);                                       
```

If it is a new user, new user account will get created else existing user will be logged in to the application.


**Step 4: Updating GCM registration id:**

**Don't have GCM setup?**

In case, if you don't have the existing GCM related code, then copy the push notification related files from Applozic sample app to your project

``` 
https://github.com/AppLozic/Applozic-Android-SDK/tree/master/app/src/main/java/com/applozic/mobicomkit/sample/pushnotification 

``` 
And add this in your androidmanifest.xml file

``` 
<receiver android:name="com.google.android.gms.gcm.GcmReceiver"
          android:exported="true"
          android:permission="com.google.android.c2dm.permission.SEND">
          <intent-filter>
                <action android:name="com.google.android.c2dm.intent.RECEIVE" />
                <category android:name="<APP_PKG_NAME>" />
          </intent-filter>
</receiver>
        
<service android:name="<CLASS_PACKAGE>.ApplozicGcmListenerService">
         <intent-filter>
                <action android:name="com.google.android.c2dm.intent.RECEIVE" />
         </intent-filter>
</service>
        
<service android:name="<CLASS_PACKAGE>.GcmInstanceIDListenerService"
         android:exported="false">
         <intent-filter>
                <action android:name="com.google.android.gms.iid.InstanceID" />
         </intent-filter>
</service>

  ``` 
Setup GCM in UserLoginTask "onSuccess" (refer Step 3).

```
 GCMRegistrationUtils gcmRegistrationUtils = new GCMRegistrationUtils(activity);          
 gcmRegistrationUtils.setUpGcmNotification();                      
```

To Enable Android Push Notification using Google Cloud Messaging (GCM) visit the below link http://www.applozic.com/blog/enable-android-push-notification-using-google-cloud-messaging-gcm/

After Registering project at https://console.developers.google.com Replace the value of GCM_SENDER_ID in GCMRegistrationUtils.java with your own project gcm sender id.
SenderId is a unique numerical value created when you configure your API project (given as "Project Number" in the Google Developers Console).            


**GCM is already enabled in my app**

If you already have GCM enabled in your app, then paste PushNotificationTask code at the place where you are getting the GCM registration id in your app.       
     
```
PushNotificationTask pushNotificationTask = null         
PushNotificationTask.TaskListener listener = new PushNotificationTask.TaskListener()   
{                  

@Override           
public void onSuccess(RegistrationResponse registrationResponse)             
{            
}            
@Override          
public void onFailure(RegistrationResponse registrationResponse, Exception exception)      
{             
} 

};                    

pushNotificationTask = new PushNotificationTask(pushnotificationId, listener, mActivity);            
pushNotificationTask.execute((Void) null);                          
```



**Step 5: Handling push notification**:
Add the following in your GcmListenerService onMessageReceived method.     





       
```
if(MobiComPushReceiver.isMobiComPushNotification(data)) {            
        MobiComPushReceiver.processMessageAsync(this, data);               
        return;          
}                                          
```


**Step 6: For starting the messaging activity**:        

      
```
Intent intent = new Intent(this, ConversationActivity.class);            
startActivity(intent);                               
``` 
 
 
 For starting individual conversation thread, set "userId" in intent:        
 
           
```
Intent intent = new Intent(this, ConversationActivity.class);            
intent.putExtra(ConversationUIService.USER_ID, "devashish@applozic.com");             
intent.putExtra(ConversationUIService.DISPLAY_NAME, "Devashish Mamgain"); //put it for displaying the title.             
startActivity(intent);                              
```

**Step 7: On logout, call the following**:       




 new UserClientService(this).logout();      
 
 
 
 Note: If you are running ProGuard, please add following lines:        
 
 
 
 
 
```
 #keep json classes                
 -keepclassmembernames class * extends com.applozic.mobicomkit.api.JsonMarker         
 {            
 !static !transient <fields>;                  
 }              
 #GSON Config          
-keepattributes Signature          
-keep class sun.misc.Unsafe { *; }           
-keep class com.google.gson.examples.android.model.** { *; }            
-keep class org.eclipse.paho.client.mqttv3.logging.JSR47Logger { *; }                                    
 ``` 
   
**Trying out the demo app:**

Open project in Android Studio to run the sample app in your device. Send messages between multiple devices. 


Display name for users:
You can either choose to handle display name from your app or have Applozic handle it.
From your app's first activity, set the following to disable display name feature:
ApplozicClient.getInstance(this).setHandleDisplayName(false);
By default, the display name feature is enabled.

For more details, visit: https://www.applozic.com/developers.html#android-sdk-getting-started



####Features:


 One to one and Group Chat
 
 Image capture
 
 Photo sharing
 
 File attachment
 
 Location sharing
 
 Push notifications
 
 In-App notifications
 
 Online presence
 
 Last seen at 
 
 Unread message count
 
 Typing indicator
 
 Message sent, Read Recipients and Delivery report
 
 Offline messaging
 
 User block / unblock
 
 Multi Device sync
 
 Application to user messaging
 
 Customized chat bubble
 
 UI Customization Toolkit
 
 Cross Platform Support (iOS, Android & Web)


###Sample code to build messenger and chat app
https://github.com/AppLozic/Applozic-Android-SDK/tree/master/app


##Help

We provide support over at [StackOverflow] (http://stackoverflow.com/questions/tagged/applozic) when you tag using applozic, ask us anything.

Applozic is the best android chat sdk for instant messaging, still not convinced? Write to us at github@applozic.com and we will be happy to schedule a demo for you.

##Github projects

Android Chat SDK https://github.com/AppLozic/Applozic-Android-SDK

Web Chat Plugin https://github.com/AppLozic/Applozic-Web-Plugin

iOS Chat SDK https://github.com/AppLozic/Applozic-iOS-SDK
