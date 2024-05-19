# RigID_v2

## An Android application, powered by machine learning to recognize electric guitars.

This README provides instructions for getting started with this project's two different workflows: Jupyter Notebook for model training and Android Studio for running the Android application.

## Training a model
The notebook provided in this repo was designed to be entirely subject-agnostic - meaning, you can train this model to perform inference on any subject of your choosing; guitar or otherwise. 

## Prerequisites:

- Labeled Dataset (Pascal VOC was used, COCO dataset can be used with modifications)
- Labelmap.pbtxt - contains simple text identifiers for your classes.
- A selected model from the TensorFlow Model Zoo - just have one in mind, you'll download it later.

Run each cell individually in the notebook and take care to modify any path that doesn't match your own. This was originally written to run on my Google Drive, so there are some hard coded paths. 

## Running the Android App
This section guides you through setting up the project within the Android Studio development environment.

## Prerequisites:

- Android Studio Jellyfish (https://developer.android.com/studio/intro)
- An Android device or emulator
- Android SDK minimum 28
  
### Installation:

1. Open Android Studio.

2. Click "File" -> "Open" and select the project directory.

### Import Project:

3. If Android Studio doesn't automatically import the project, navigate to "File" -> "Import Project".

4. Select the project directory and click "Import".

### Running the App:

5. Connect your Android device (the emulator camera is not sufficient).

6. Click the "Run" button (green triangle) in the toolbar.

7. Select your device from the dropdown menu.

8. Click "OK" to start the app.

### Using the App:

9. Once the application is launched, you will be presented with the start screen.
    
11. Select the Photo button to open your camera and take a picture of a guitar.

12. Once you take a picture, select OK or Retry to proceed to the next stage of inference.

13. The following screen will show a bounding box around the detected guitars in the image. Choose a guitar to obtain the details - model and pickup configuration.

Note: This application requires a roboflow API key to handle the secondary inference. Luckily, it is free to get an API Key and you can follow the instructions below.


### RoboFlow API Key

- Create a Free Roboflow Account: If you haven’t already, sign up for a free account on the Roboflow website.
- Go to the Roboflow Dashboard: Once logged in, navigate to your dashboard.
- Access the Settings: Look for a settings button or dropdown in your workspace.
- Select the ‘Roboflow API’ Tab: In the settings dropdown, find and click on the ‘Roboflow API’ tab.
- Copy Your Private API Key: You should see your Private API Key displayed here. Click to copy it.
- Edit the Fragment: Replace Line 212 in FirstFragment.kt with the API string.


## License information:

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.
