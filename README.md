# IOT Display Pannel

---

Author: Wesley Campbell
Version: 1.0.0
Date: 2026-04-10

---

### Overview

This project contains the code necessary to drive E-Paper display pannels connected to a centeralized data server. These pannels will routinely make HTTP requests to collect live data and will display them on the screen. 

Additionally, via buttons built into the display, users will be able to interact with the data and will be able to send updates to the server.

Applications include daily habbit tracking, displaying quotes of the day, displaying live sensor data from other IOT devices, and displaying data scraped from the internet.

The display will contain two sections: the large state that will take up the majority of the screen and a small banner on the bottom that will display relevant information as well as global info banners.

The display will automatically rotate between the large states, although users will be able to switch back and forth via buttons on the pannel. Users will also be able to cycle between the smaller banner metadata by using a rotary encoder knob.

### Usage

There are two main components to the project: the server and the client. The server is a simple java HTTP server that can be compiled and run on any machine containing the JDK 21. 

The client code is designed to be run by a ESP32 microcontroller that has WiFi capabilities. Due to the rendering of frame buffers, it is recommended to use a varient that has at least 2MB of memory. Connected to the ESP32 via SPI is a 7.4 inch E-Paper display, two push buttons, and one rotary encoder. For more hardware details, see the README of the client sub-directory. The server address, WiFi info, and unique API key need to be hard coded into the code before it is uploaded to the ESP32 or it will be unable to access any data and will display the default "No Connection" screen.

### Change-Log

##### Version 1.0.0, 2026-04-10
  - Added default server boilerplate code
  - Wrote initial README
