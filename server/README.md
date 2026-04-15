# Server for IOT Display Pannels

---

Author: Wesley Campbell
Version: 1.0.2
Date: 2026-04-10

---

### Overview

This directory contains all the code and resources for the IOT Display Pannel web server. It is primarily based in java. The server will contain a web gui dashboard that outlines board state and each of the endpoints that each board has access to. Additionally, the server will provide structured data to display pannels that send valid HTTP requests, allowing them to display syncronized data from the main server. 

### Usage

The code is structed to use Maven for building. To compile the server code, run `mvn package` or `mvn package -DskipTests`, if testing is not needed. During the development process, you can run the server with `mvn exec:java` and run automatic testing with `mvn test`. 

**DATABASE CONFIGURATION:**
The server code requires access to an external MariaDB database. The properties of this database need to be included into a file `db.properties` located within the `src/main/java/resources` directory (although the code should be able to find it anywhere in the classpath). The credentials for each database need to be outlined as followed in this document:
```
db.{database_name}.username={database_user}
db.{database_name}.password={database_password}
db.{database_name}.host={database_address}
db.{database_name}.port={database_port}
```
Note that `{database_name}` will need to be replaced with the HTTP endpoint of the associated database. For example, the "habits" endpoint relies on the `habits` database. The other variables are outlined as follows:

| Variable          | Description                                              |
|-------------------|----------------------------------------------------------|
| database_user     | The user that can access the database                    |
| database_password | The password of the database user                        |
| database_address  | The ip address/domain of the database server             |
| database_port     | The port that the database opperates on (typically 3306) |

The server code will automatically create these databases, but the user must already exist and have full permisions within each of the databases to function.

**BUILDING SERVER EXECUTABLE**:
After configuring these database values, the server can be built using the `mvn package` command.
After the server jar is built, it can be run with `java -jar server.jar`. The server by default binds to port `24560`. If accessing the web gui via the same machine, it can be accessed by `http://localhost:24560`, otherwise port forwarding and firewall configuration will be required to access the site.

### HTTP Endpoints

From the back-end perspective, the server currently contains endpoints for each of the following services:
- Daily habbit tracking with performance metrics

The endpoints for each service are:

**Daily Habbit Tracking**:

| Path            | Method  | Purpose                          | Returns                               |
|-----------------|---------|----------------------------------|---------------------------------------|
| api/habit       | PUT     | Marks habit completed for today  | {}                                    | 
| api/habit       | DELETE  | Marks habit uncompleted today    | {}                                    |
| api/habit       | GET     | Get the habit completion history | JSON set of dates with status         | 
| api/habit/stats | GET     | Get the habit stats              | JSON serialization of relevant stats  |

### Change Log

##### Version 1.0.0, (2026-04-10):
  - Created initial Schema for Habbit Tracking endpoints
  - Created initial project structure
  - Created maven infrastructure 

##### Version 1.0.1, (2026-04-13):
  - Updated database schema
  - Implemented very basic server outline that autocreates database tables

##### Version 1.0.2, (2026-04-14):
  - Refactored the database mangement system to use Flyway
  - Databases are now automatically configured by `.sql` files found in `src/main/resources/db/{db_name}` with version control
  - Refactored Data Access classes: `DatabaseManager` is no longer static and will now control connections to a specific database
  - DAO classes now require a pointer to a `DatabaseManager` instance to interface with the correct database
  - Now, each DAO class represents one table within a database schema
