# Server for IOT Display Pannels

---

Author: Wesley Campbell
Version: 1.0.6
Date: 2026-04-10

---

### Overview

This directory contains all the code and resources for the IOT Display Pannel web server. It is primarily based in java. The server will contain a web gui dashboard that outlines board state and each of the endpoints that each board has access to. Additionally, the server will provide structured data to display pannels that send valid HTTP requests, allowing them to display syncronized data from the main server. 

### Usage

The code is structed to use Maven for building. To compile the server code, run `mvn package` or `mvn package -DskipTests`, if testing is not needed. During the development process, you can run the server with `mvn exec:java` and run automatic testing with `mvn test`. 

#### **DATABASE CONFIGURATION:**

###### Compilation Pipeline
The server code requires access to an external MariaDB database. This program treats databases on a per-database basis, meaning that each unique database must be configured. To correctly generate data access classes and correctly apply the database migrations during the compilation pipeline, each directory must be stored within the `db` directory in the server root.For example, the 'habits' database information is stored in the `db/habits` directory. 

Within the `db` directory there must be a file called `jooq.conf`. This file contains the database credentials that jOOQ needs to automatically generate the data access classes. The outline of the file is outlined below:
```
user={database_user}
password={database_password}
host={database_address}
port={database_port}
```

Replace each value surrounded by brackets with its associated value (without brackets). 

Notice that to function correctly, the `jooq` user requires `SELECT` privileges on all databases that are found within the `db` directory and on the `mysql.proc` database. Without these permissions, the pipeline will be unable to read the database schemas and create the data access classes. 

Within each database directory there must be a file called `db.properties` that outlines the database address and user credentials. Without this information, the pipeline will fail and the project will not compile as the project depends on automatically generated data access classes generated from the database schema. The outline of the `db.properties` file is the exact same as the format of the `jooq.conf` file outlined above. The user specified in the `db.properties` file must have full permissions on its respective database, else the compilation pipeline will be unable to apply the correct schemas. 

Below is an example file tree of a correct `db` directory:
```
server/
└─── db/ 
     ├──database1
     │  ├── V1__database_init.sql
     │  └── db.properties
     ├──database2
     │  ├── V1__database_init.sql
     │  ├── V2__database_update.sql
     │  └── db.properties
     └── jooq.conf
```

###### Program Execution

Note that this implementation is initial and will be changed in further updates.

For the program to operate correctly, it needs to be able to access a database and its tables. To do this, database credentials are stored in a `db.properties` file, located within the `src/main/java/resources` directory. Note that this project treats databases on a individual basis, allowing for different databases to be located at different addresses. The format for the `db.properties` file is outlined below:
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

#### **BUILDING SERVER EXECUTABLE**:
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

##### Version 1.0.6, (2026-05-20):
  - Implemented the API service level of the application
  - Can now make API calls defined in the protobuf configuration to apply CRUD opperations to the database
  - Developed testing infrastructure for service verrification.

##### Version 1.0.5, (2026-05-08):
  - Implemented Data Access Objects as bridge between MariaDB database and Java program
  - Java program now has structured CRUD access to database entries for Habit tracking.
  - Developed infrastructure for future feature development

##### Version 1.0.4, (2026-04-24):
  - Added in Data access testing infrastructure
  - Tests will now be performed in temporary database run in a container
  - DatabaseManager class modified to allow arbitrary database properties instead of just `db.properties`

##### Version 1.0.3, (2026-04-23):
  - Refactored the data access classes to use jOOQ rather than vanilla JDBC
  - jOOQ is now used to access database objects
  - Added jOOQ into the `generate-sources` phase of maven pipeline. Data object classes representing database schema are now automatically generated 
  - Added Flyway migration into the `generate-sources` phase of maven pipeline. SQL database changes are now automatically applied to the database
  - Removed Flyway from java program
  - Changed location of `.sql` migration files to server root directory. Each database directory `db/{database_name}` directory must contain `db.properties` file

##### Version 1.0.2, (2026-04-14):
  - Refactored the database mangement system to use Flyway
  - Databases are now automatically configured by `.sql` files found in `src/main/resources/db/{db_name}` with version control
  - Refactored Data Access classes: `DatabaseManager` is no longer static and will now control connections to a specific database
  - DAO classes now require a pointer to a `DatabaseManager` instance to interface with the correct database
  - Now, each DAO class represents one table within a database schema

##### Version 1.0.1, (2026-04-13):
  - Updated database schema
  - Implemented very basic server outline that autocreates database tables

##### Version 1.0.0, (2026-04-10):
  - Created initial Schema for Habbit Tracking endpoints
  - Created initial project structure
  - Created maven infrastructure 

