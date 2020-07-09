# BLE Sniffer

The purpose of this app is to take raw data BLE advertisements and interpret their data as primitive type such as ints or doubles and show them as clear values.

#### Before
![](./tutorial/before.jpg?raw=true "Before")
#### After
![](./tutorial/after.jpg?raw=true "After")


## Main Screen
![](./tutorial/img1.jpg?raw=true "Main Screen")

The main screen contains several items:

1. The Start/Stop Scan Button

   By pressing the Start Scan button the scan begins.<br/>
   ![](./tutorial/img2.jpg?raw=true "Start Scan")
2. Advertisement Filter Button

   By pressing the advertisement filter button, an input field shows enabling you to input advertisement bytes or device MAC address to filter by. After you submit filter, it will be applied to all future advertisements, i.e. only new advertisements that match the filter criteria will be displayed on screen.

   If you use the app in portrait mode, then the filter button and the input field are displayed at the bottom of the screen.<br/>
   ![](./tutorial/img11.jpg?raw=true "Scan Analyzer - portrait mode")
3. The Menu Button

   By pressing the menu button you are given three options to select from. Option Clear is used to clear all displayed advertisements, Deserializers option is used to list all deserializers and Settings option is used to customize application settings.<br/>
   ![](./tutorial/img3.jpg?raw=true "Main Menu")

## Deserializer List
![](./tutorial/img4.jpg?raw=true "Deserializer List")

The deserializer list screen displays all available deserializers and let you create your own.<br/>
You can create your own deserializer by pressing the plus button.<br/>
You can import, export and delete deserializers by either holding the deserializer you wish to export/delete, or by choosing the appropriate menu option to do batch operations.

## Application settings
![](./tutorial/img12.jpg?raw=true "Settings")

In application settings screen, you can set the following settings:

1. Show only manufacturer data

   Check this option if you don´t want all advertisement bytes to be shown when scanning but only the manufacturer data portion of advertisement.

2. Keep screen on

   Check this option if you want to keep screen on while scanning.

3. Advertisement bytes display mode

   This option enables you to select how you want advertisement bytes to be displayed.



### Individual Deserializer actions
![](./tutorial/img10.jpg?raw=true "Export/Delete")

You can export or delete individual deserializers.

## Creating a Deserializer
![](./tutorial/img5.jpg?raw=true "Creating a Deserializer")

When creating a deserializer you have several fields to input, these are the following:
* Name: The name of the deserializer
* Filter: The data you want to filter by (this is exclusively REGEX)
* Filter Type: Do you want to filter by MAC or by the DATA
* Sample Data: Sample data to preview the deserialization
* Field Deserializers: A list of deserializers for given portions of the data

Sample data can be automatically generated after inputting field deserializers by pressing `Generate` button.

### Editing Field Deserializers
![](./tutorial/img6.jpg?raw=true "Editing Field Deserializers")

To add a new field deserializer, press the plus button.<br/>
![](./tutorial/img7.jpg?raw=true "Add Field")

The field deserializer has several values to be filled in:
* Name: Name to be displayed for field
* Type: Type of field
* Start and End Index: Start index is inclusive, End index is exclusive (end index is automatically calculated for fixed size value types) - this indices mark start and end of byte array to be deserialized.
* Color: Select how you want the deserialized field to be colored

You can preview how the deserialized data will look by using the `Preview` button.<br/>
![](./tutorial/img8.jpg?raw=true "Preview")

To remove a field deserializer, press the remove button placed next to it.

To save the deserializer, press the `Save` button.

This is how such a deserializer looks in action.<br/>
![](./tutorial/img9.jpg?raw=true "Actual View")
