# Calendy

This is a simple GUI calendar and memo application. It has made as a Swing library in Java.

The language of this application is **Korean**.



## Requirements

* Java SE 10
* Oracle Database 11g Express Edition
  * I just used this DBMS. But if modifying a little bit of code, you can use another DBMS.



## Run

You can compile and run with IDE for Java.



### Preset

* All directories must be located in the same directory.
* Some database table settings are needed before running. Create tow tables as shown below.

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

* You will probably use different database settings, so please modify all of the following codes in the files.

```java
Class.forName("oracle.jdbc.driver.OracleDriver");
Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:XE", "hr", "hr");
```

