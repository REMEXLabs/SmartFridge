# SmartFridge Android Application

Android application to communicate with the Webserver and the Raspberry PI of the SmartFridge.

The functionalities are:

- Store and delete products manually or via voice detection
- Display all stored products
- Create/delete and edit shopping lists
- Display the pictures taken by the camera inside the fridge 


To use the barcode scanner, enter the IP-Adress of the RPI in the settings menu.
This is necessary to send the Google Cloudmessage key to the RPI, wich is needed to receive the push notifications.

## Related Repositories

* [SmartFridge RPI](https://github.com/REMEXLabs/SmartFridgeRPI).
* [SmartFridge Webserver][https://github.com/REMEXLabs/SmartFridgeWebserver).
