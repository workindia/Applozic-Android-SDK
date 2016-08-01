
##Changelog
 
**Version 4.53**  - Monday,1 August 2016

Bug fixes and improvement

###  Steps for upgrading from 4.52 to 4.53

**Step 1: Add the following in your build.gradle dependency**

`compile 'com.applozic.communication.uiwidget:mobicomkitui:4.3'`


**Version 4.52**  - Wednesday,27 July 2016

User and Group image upload and change

Group typing status 

Typing status is moved from bottom to App Bar

User status message change

Bug fixes and performance improvement

###  Steps for upgrading from 4.51 to 4.52

**Step 1: Add the following in your build.gradle dependency**

`compile 'com.applozic.communication.uiwidget:mobicomkitui:4.52'`


**Step 2: In Your Android manifest add below code**

```
 <activity android:name="com.soundcloud.android.crop.CropImageActivity" />

 <service android:name="com.applozic.mobicomkit.api.people.UserIntentService"
          android:exported="false" />

 <service android:name="com.applozic.mobicomkit.api.conversation.ConversationIntentService"
           android:exported="false" />

```

**Version 3.31**

Bug fixes and improvements

**Version 3.30**

 Contact search bug fix
 
 Group name sync changes
 
 Read Count bug fix 
 
**Version 3.29**

User Block

Grid layout for attachment options

Contact Search

Group Change notification
