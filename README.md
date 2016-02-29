#Server Installation Guide
Download CZ4013Server.jar

## Run with default configuration
```
java -jar CZ4013Server.jar
```
By default, server will listen to port 8888 and choose AT_MOST_ONCE semantics

##Run with customized port
```
java -jar CZ4013Server.jar 8800
```
Server will listen to port 8800 and choose AT_MOST_ONCE semantics as default

##Run with customized port and semantics
```
java -jar CZ4013Server.jar 8800 1
```
Server will listen to port 8800 and choose AT_MOST_ONCE semantics as default

```
java -jar CZ4013Server.jar 8800 2
```
Server will listen to port 8800 and choose AT_LEAST_ONCE semantics as default

##Note
* My Java Runtime Environment is 
```
java version "1.8.0_05"
Java(TM) SE Runtime Environment (build 1.8.0_05-b13)
Java HotSpot(TM) 64-Bit Server VM (build 25.5-b02, mixed mode)
```
* All the test cases have passed.
* Refer to source code for understanding
