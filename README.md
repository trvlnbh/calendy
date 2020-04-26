# Calendy

This is a simple GUI calendar and memo application. It had made as Swing library in Java.

The language of this application is **Korean**.



## Requirements

* Java SE 10
* Oracle Database 11g Express Edition
  * I just used this DBMS. But if you have a break time to modify a little bit of code, you can use another DBMS.



## Run

You can compile and run with IDE for Java. (e.g. Eclipse)



### Preset

* All directories must be located in the same directory.
* Some database table settings are needed before running. Create tables as shown below.

```sql
CREATE TABLE "MEMODATA" (	
    "USERKEY" NUMBER,
    "DATEKEY" NUMBER,
    "MEMO" VARCHAR2(150 BYTE)
);
```

```sql
CREATE TABLE "USERDATA" (	
    "USERKEY" NUMBER,
    "NAME" VARCHAR2(32 BYTE)
);
```

* You will probably use different database settings, so please modify all of the following codes for your environment in the code.

```java
Class.forName("oracle.jdbc.driver.OracleDriver");
Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:XE", "hr", "hr");
```



## Screenshot

![main](https://user-images.githubusercontent.com/47481501/74946090-20021600-543c-11ea-94a6-2d1c6754b65b.jpg)
