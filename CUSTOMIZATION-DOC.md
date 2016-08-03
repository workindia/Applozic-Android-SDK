#### Chat Bubble

Add UI customization setting inside UserLoginTask "onSuccess" method

Sent Message Chat Bubble Color

 ```
ApplozicSetting.getInstance(context).setSentMessageBackgroundColor(int color); // accepts the R.color.name
 ```
 
Received Message Chat Bubble Color

 ```
ApplozicSetting.getInstance(context).setReceivedMessageBackgroundColor(int color); // accepts the R.color.name
 ```

 Sent Message Chat Bubble Border Color

 ```
ApplozicSetting.getInstance(context).setSentMessageBorderColor(int color); // accepts the R.color.name
 ```
 
Received Message Chat Bubble Border Color

 ```
ApplozicSetting.getInstance(context).setReceivedMessageBorderColor(int color); // accepts the R.color.name
 ```

Sent Message Text color

 ```
ApplozicSetting.getInstance(context).setSentMessageTextColor(int color); // accepts the R.color.name
 ```
 
Received Message Text Color

 ```
ApplozicSetting.getInstance(context).setReceivedMessageTextColor(int color); // accepts the R.color.name
 ```


Sent Contact Message Text color

 ```
ApplozicSetting.getInstance(context).setSentContactMessageTextColor(int color); // accepts the R.color.name
 ```
 
Received Contact Message Text Color

 ```
ApplozicSetting.getInstance(context).setReceivedContactMessageTextColor(int color); // accepts the R.color.name
 ```
 
Sent Message Hyper Link Text Color

 ```
ApplozicSetting.getInstance(context).setSentMessageLinkTextColor(int color); // accepts the R.color.name
 ```
 
 Received Message Hyper Link Text Color

 ```
ApplozicSetting.getInstance(context).setReceivedMessageLinkTextColor(int color); // accepts the R.color.name
 ```
 
 
#### Chat Background

Chat Background Image or Color

 ```
ApplozicSetting.getInstance(context).setChatBackgroundColorOrDrawableResource(int color); // accepts the R.color.name or  R.drawable.drawableName
 ```
 
Edit Text Background  Color Or DrawableResource 

 ```
ApplozicSetting.getInstance(context).setEditTextBackgroundColorOrDrawableResource(int colorOrdrawableName); // accepts the R.color.name or  R.drawable.drawableName
 ```

#### Send Message Layout

Message EditText Text Color

 ```
ApplozicSetting.getInstance(context).setMessageEditTextTextColor(int color); // accepts the R.color.name
 ```

Message EditText Hint Color

 ```
ApplozicSetting.getInstance(context).setMessageEditTextHintColor(int color); // accepts the R.color.name
 ```

Attachment Icons Background Color

 ```
ApplozicSetting.getInstance(context).setAttachmentIconsBackgroundColor(int color); // accepts the R.color.name
 ```
 
 Send Button Background Color
 
 ```
  ApplozicSetting.getInstance(context).setSendButtonBackgroundColor(int color); // accepts the R.color.name
  ```

#### Online status

Show/Hide Green Dot for Online

 ```
ApplozicSetting.getInstance(context).showOnlineStatusInMasterList();
ApplozicSetting.getInstance(context).hideOnlineStatusInMasterList();
 ```

#### Group Messaging

For Group Add Member Button Hide

```
ApplozicSetting.getInstance(context).setHideGroupAddButton(true);
```
For Group Exit Button Hide

```
 ApplozicSetting.getInstance(context).setHideGroupExitButton(true);
 ```
 
 For Group Name Change Button Hide
 
 ```
 ApplozicSetting.getInstance(context).setHideGroupNameEditButton(true);
 ```
 
 For  Group Member Remove Option Hide
 
  ```
 ApplozicSetting.getInstance(context).setHideGroupRemoveMemberOption(true);
  ```
  
#### 'Start New' button
Show/hide 'Start New Conversation' Plus (+) Button 
 
 ```
 ApplozicSetting.getInstance(context).showStartNewButton();
 ApplozicSetting.getInstance(context).hideStartNewButton();
```

Show/hide 'Start New' FloatingActionButton

```
ApplozicSetting.getInstance(context).showStartNewFloatingActionButton();
ApplozicSetting.getInstance(context).hideStartNewFloatingActionButton();
```

#### Theme customization

  To customize the theme, copy paste the following in your theme's  res file:
   ```
  <style name="ApplozicTheme" parent="Theme.AppCompat.Light.NoActionBar">
  
   <!--To change the toolbar color change the colorPrimary  -->
    <item name="colorPrimary">@color/applozic_theme_color_primary</item>
    
    <!-- To change the status bar  color change the color of  colorPrimaryDark-->
    <item name="colorPrimaryDark">@color/applozic_theme_color_primary_dark</item>
    
    <!-- colorAccent is used as the default value for colorControlActivated which is used to tint widgets -->
    <item name="colorAccent">@color/applozic_theme_color_primary</item>
    
    <item name="windowActionModeOverlay">true</item>
  </style>
   ```
  
   Change the name of the style  name="ApplozicTheme"  to some new name and in your app androidmanifest.xml file find for ApplozicTheme and replace with your new theme style.
 
 
#### UI source code

For complete control over UI, you can also download open source chat UI toolkit and change it as per your designs :
```
[https://github.com/AppLozic/Applozic-Android-SDK](https://github.com/AppLozic/Applozic-Android-SDK)
```

Import [MobiComKitUI Library](https://github.com/AppLozic/Applozic-Android-SDK/tree/master/mobicomkitui) into your android project and add the following in the build.gradle file:

```
compile project(':mobicomkitui')
```

MobiComKitUI contains the UI related source code, icons, layouts and other resources which you can customize based on your design needs.

For your custom contact list, replace MobiComKitPeopleActivity with your contact list activity.

Sample app with integration is available under [app](https://github.com/AppLozic/Applozic-Android-SDK/tree/master/app)

