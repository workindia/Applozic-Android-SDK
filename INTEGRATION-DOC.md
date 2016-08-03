####Step 1: Gradle Dependency

Add the following in your build.gradle dependency  

```
compile 'com.applozic.communication.uiwidget:mobicomkitui:4.53'
```


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


####Step 2: AndroidManifest.xml changes
Add the following Permissions, Activities, Services and Receivers in androidmanifest.xml
       
**Note**: Add meta-data,Activities,Services and Receivers within application Tag ``` <application> </application>  ``` 

**Note**: Add Permissions outisde the application Tag ```  <application> ```  
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
<uses-permission android:name="android.permission.GET_TASKS" />
<uses-permission android:name="android.permission.CALL_PHONE" />
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
          android:theme="@style/ApplozicTheme"
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
 
 <activity android:name="com.soundcloud.android.crop.CropImageActivity" />

                   
<service android:name="com.applozic.mobicomkit.api.conversation.MessageIntentService"
          android:exported="false" />
              
<service android:name="org.eclipse.paho.android.service.MqttService"/>

<service android:name="com.applozic.mobicomkit.api.conversation.ApplozicIntentService"
         android:exported="false" />
             
<service android:name="com.applozic.mobicomkit.api.conversation.ApplozicMqttIntentService"
         android:exported="false" />
         
<service android:name="com.applozic.mobicomkit.api.people.UserIntentService"
            android:exported="false" />

<service android:name="com.applozic.mobicomkit.api.conversation.ConversationIntentService"
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

####Step 3: Login/Register User
Applozic will create a new user if the user doesn't exists. userId is the unique identifier for any user, it can be anything like email, phone number or uuid from your database.

     
```
UserLoginTask.TaskListener listener = new UserLoginTask.TaskListener() {                  

@Override          
public void onSuccess(RegistrationResponse registrationResponse, Context context)         
{           
   // After successful registration with Applozic server the callback will come here 
    ApplozicSetting.getInstance(context).showStartNewButton();//To show contact list.
     //ApplozicSetting.getInstance(context).enableRegisteredUsersContactCall();//To enable the applozic Registered Users Contact Note:for disable that you can comment this line of code
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
user.setImageLink("");//optional,pass your image link
new UserLoginTask(user, listener, this).execute((Void) null);                                       
```

If it is a new user, new user account will get created else existing user will be logged in to the application.


####Step 4: Push Notification Setup

***Go to Applozic Dashboard, Edit Application. 
Under Module section, update the GCM Server Key.***

Here is a blog post on how to get GCM Server Key: https://www.applozic.com/blog/enable-android-push-notification-using-google-cloud-messaging-gcm/


#####GCM is already enabled in my app

If you already have GCM enabled in your app, then paste PushNotificationTask code at the place where you are getting the GCM registration id in your app.       
     
```
PushNotificationTask pushNotificationTask = null;         
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


#####Don't have GCM code?

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



####Step 5: Handling push notification
Add the following in your GcmListenerService onMessageReceived method.     

       
```
if(MobiComPushReceiver.isMobiComPushNotification(data)) {            
        MobiComPushReceiver.processMessageAsync(this, data);               
        return;          
}                                          
```


####Step 6: Initiate chat with other user

For starting the messaging activity      
      
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

####Step 7: Logout user       

Call the following when user logout from your app:

```
new UserClientService(this).logout();      
```
 

#### Step 8: ProGuard Setup
Add the following if you are using ProGuard:
 
```
 #keep json classes                
 -keepclassmembernames class * extends com.applozic.mobicommons.json.JsonMarker {
 	!static !transient <fields>;
 }

 -keepclassmembernames class * extends com.applozic.mobicommons.json.JsonParcelableMarker {
 	!static !transient <fields>;
 }
 #GSON Config          
-keepattributes Signature          
-keep class sun.misc.Unsafe { *; }           
-keep class com.google.gson.examples.android.model.** { *; }            
-keep class org.eclipse.paho.client.mqttv3.logging.JSR47Logger { *; } 
-keep class android.support.** { *; }
-keep interface android.support.** { *; }
 ``` 
