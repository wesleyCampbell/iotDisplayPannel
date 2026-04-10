# Server for IOT Display Pannels

---

Author: Wesley Campbell
Version: 1.0.0
Date: 2026-04-10

---

### Overview

This directory contains all the code and resources for the IOT Display Pannel web server. It is primarily based in java. The server will contain a web gui dashboard that outlines board state and each of the endpoints that each board has access to. Additionally, the server will provide structured data to display pannels that send valid HTTP requests, allowing them to display syncronized data from the main server. 

### Usage

The code is structed to use Maven for building. To compile the server code, run `mvn package` or `mvn package -DskipTests`, if testing is not needed. During the development process, you can run the server with `mvn exec:java` and run automatic testing with `mvn test`. 

After the server jar is built, it can be run with `java -jar server.jar`. The server by default binds to port `24560`. If accessing the web gui via the same machine, it can be accessed by `http://localhost:24560`, otherwise port forwarding and firewall configuration will be required to access the site.

### HTTP Endpoints

From the back-end perspective, the server currently contains endpoints for each of the following services:
- Daily habbit tracking with performance metrics

The endpoints for each service are:

**Daily Habbit Tracking**:

| Path             | Method  | Purpose                           | Returns                               |
|------------------|---------|-----------------------------------|---------------------------------------|
| api/habbit       | PUT     | Marks habbit completed for today  | {}                                    | 
| api/habbit       | DELETE  | Marks habbit uncompleted today    | {}                                    |
| api/habbit       | GET     | Get the habbit completion history | JSON set of dates with status         | 
| api/habbit/stats | GET     | Get the habbit stats              | JSON serialization of relevant stats  |

### Change Log

##### Version 1.0.0, 2026-04-10:
  - Created initial Schema for Habbit Tracking endpoints
  - Created initial project structure
  - Created maven infrastructure 
