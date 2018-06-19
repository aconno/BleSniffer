# BLE Sniffer

Android app for dynamic BLE advertisement analysis.

## Main Screen
![](./tutorial/img1.png?raw=true "Main Screen")

The main screen contains several items:

1. The Start/Stop Scan Button

   By pressing the Start Scan button the scan will begin.<br/>
   ![](./tutorial/img2.png?raw=true "Start Scan")
2. The Menu Button

   By pressing the menu button you will be prompted with a menu button that when pressed will bring you to the deserializer list.<br/>
   ![](./tutorial/img3.png?raw=true "Deserializer Menu")

## Deserializer List
![](./tutorial/img4.png?raw=true "Deserializer List")

The deserializer list will show all available deserializers and let you create your own.<br/>
You can create your own deserializer by pressing the button `Add Deserializer`.<br/>
You can import, export and delete deserializers by either holding the deserializer you wish to export/delete, or choosing to appropriate item from to menu to do batch operations.

### Individual Deserializer actions
![](./tutorial/img10.png?raw=true "Export/Delete")

You can export or delete individual deserializers.

## Creating a Deserializer
![](./tutorial/img5.png?raw=true "Creating a Deserializer")

When creating a deserializer you have several fields to input, these are the following:
* Name: The name of the deserializer
* Filter: The data you want to filter by (this is exclusively REGEX)
* Filter Type: Do you want to filter my MAC or the DATA
* Sample Data: Sample data to preview the deserialization
* Field Deserializers: A list of deserializers for given indices of the data

### Editing Field Deserializers
![](./tutorial/img6.png?raw=true "Editing Field Deserializers")

After entering all the other data it is time to add the field deserializers.
You do this by pressing the `Add Field` button.<br/>
![](./tutorial/img7.png?raw=true "Add Field")

The field deserializer has several values to be filled in:
* Name: Name to be displayed for field
* Type: Type of field
* Start and End Index: Both inclusive, mark start and end of byte array to be deserialized.

You can preview how the deserialized data will look by using the `Preview` button.<br/>
![](./tutorial/img8.png?raw=true "Preview")

This is how such a deserializer looks in action.<br/>
![](./tutorial/img9.png?raw=true "Actual View")
