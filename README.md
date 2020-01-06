# Madcamp_second: ***Not implemented***
2019 Winter KAIST  

## Environments
- Node v12.14.0
- Android Studio 29.0.2
- Express.js
- MongoDB

### Library
- Glide 3.7.0

### Server
Use Express.js as a serverside framework
- Link Client(Android) - Server(Express) - DB(MongoDB)

## Description  
&nbsp;&nbsp;&nbsp;&nbsp;This repository is for madcamp project of second week. 
In this project, we make an android application with three tabs.
Each tab has its own content respectively: Address book, Photo gallery, and ***not implemented***.  

  
#### Tab1: Address book
&nbsp;&nbsp;&nbsp;&nbsp;This tab is for address book. We implemented it as getting all phone numbers and names, 
and show them. Implementing, we used ListView, and adapter to show the contents. To get permission, we implemented 
a pop-up which requires the user to agree to permit read contacts. Also, we implemented the application to send all information about
contacts to the server, then the server saves them on the database(MongoDB)
  
    
#### Tab2: Gallery
&nbsp;&nbsp;&nbsp;&nbsp;This tab is for Gallery. We implemented it as getting all photos in local gallery and show
them as gridview. In the implementation, we used Glide library. To get permission, we implemented a pop-up which requires
the user to agree to permit read and wirte external local storage. Also, we implemented the application to send all Bitmap of pictures
to the server, then the server saves them on the database(MongoDB)
  
  
#### Tab3: ***Not implemented***
&nbsp;&nbsp;&nbsp;&nbsp;***not implemented***<br>
********************
## Note
- **permission**  
&nbsp;&nbsp;&nbsp;&nbsp;We need permission to access contacts on the phone, and local storage in order to get contacts 
from the phone for the Tab1, and get photos from the phone album for the Tab2, and also we have to store 'Todo list' in the 
local storage as txt file. So we put code below in the 'manifest/AndroidManifest.xml'   
<br>\<uses-permission android:name="android.permission.READ_CONTACTS"/>
<br>\<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
<br>\<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/><br><br>
&nbsp;&nbsp;&nbsp;&nbsp;In addition, we have to request users permissions, so we implemented requesting function(checkVerify())
and overrided function 'onRequestPermissionResult'. After user agree all permission, the function calls a function 'startApp()',
which will actually start the actual application.  

- **Login and Registeration**  
&nbsp;&nbsp;&nbsp;&nbsp; Even an user turn off the application, the user should be able to access the application without login again,
until the user logout. For this, we used Shared preferences, which lets the application save login information in the local storage.
In the loading screen, the application will check if there is Shared preferences information about id and password, if there does not
exists, turn on the login activity, otherwise, turn on the Mainactivity. 
    
    
## Authors
- Jiwoong Na (NaJiwoong)
- Seungjun Oh (tmdwns0907)
