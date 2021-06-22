#project 5 template:
- # General
    - #### Team#: 27
    
    - #### Names: Jordan Young, Yasna Nekooei
    
    - #### Project 5 Video Demo Link: ???

    - #### Instruction of deployment:
    1. Log onto the AWS server through terminal
    2. clone onto AWS instance - "git clone https://github.com/UCI-Chenli-teaching/cs122b-spring21-team-27.git"
    3. cd into repository: "cs122b-spring21-team-27
    4. run command "mvn package"
    5. move built war file into tomcat/webapps 

    #### disposable account credentials:

    1. mySQL user: mytestuser
    2. mySQL password: My6$Password

    - #### Collaborations and Work Distribution:
    -Equal contribution amongst both team members. Met up regularly in person to work on the tasks of the project.


- # Connection Pooling
    - #### Include the filename/path of all code/configuration files in GitHub of using JDBC Connection Pooling.
    - https://github.com/UCI-Chenli-teaching/cs122b-spring21-team-27/blob/main/WebContent/META-INF/context.xml
    
    - #### Explain how Connection Pooling is utilized in the Fabflix code.
    - We enabled connection Pooling by modifying the Resources in context.xml. Connection pooling helps scale the website when the number of users           increase. It does does so by using a cache of database connections that can be reused with every request to the database (as opposed to having to open and close the connections for every request). It also reduces the amount of time it takes to estbalish a connection to the database.
 
    - #### Explain how Connection Pooling works with two backend SQL.
    - We create a separate connection pool for each of the two backend SQL servers and they are each created when the website is deployed on their corresponding AWS instance.
    

- # Master/Slave
    - #### Include the filename/path of all code/configuration files in GitHub of routing queries to Master/Slave SQL.
    -  the following is the context.xml file where we created separate resources for the master and slave instances:
    - https://github.com/UCI-Chenli-teaching/cs122b-spring21-team-27/blob/main/WebContent/META-INF/context.xml
    - the following 3 java files modify the database (write to the database), therefore they are routed to master's sql. All other java files only read from the database and are routed to slave sql (localhost):
    - https://github.com/UCI-Chenli-teaching/cs122b-spring21-team-27/blob/main/src/AddMovieServlet.java
    - https://github.com/UCI-Chenli-teaching/cs122b-spring21-team-27/blob/main/src/AddStarServlet.java
    - https://github.com/UCI-Chenli-teaching/cs122b-spring21-team-27/blob/main/src/PayServlet.java

    - #### How were read/write requests routed to Master/Slave SQL?
    - Read requests can be sent to master or slave and will access their sql database to obtain the read information. However, write requests can only be sent to the master instance.
   

- # JMeter TS/TJ Time Logs
    - #### Instructions of how to use the `log_processing.*` script to process the JMeter logs.


- # JMeter TS/TJ Time Measurement Report

| **Single-instance Version Test Plan**          | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 2: HTTP/10 threads                        | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 3: HTTPS/10 threads                       | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 4: HTTP/10 threads/No connection pooling  | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |

| **Scaled Version Test Plan**                   | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 2: HTTP/10 threads                        | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 3: HTTP/10 threads/No connection pooling  | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
