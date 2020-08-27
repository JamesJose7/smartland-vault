# Authors

- [José Eguiguren](https://github.com/JamesJose7/) (developer)
- [Nelson Piedra](https://investigacion.utpl.edu.ec/es/nopiedra) (supervisor)

# [SmartLand](https://smartland.utpl.edu.ec/) Vault

Project that aims to centralize, disambiguate, and link multiple data sources. Excel workbooks and REST endpoints are supported as of right now.

## Configuration required

This is a Spring Web Java project. It uses both a relational and non-relational database. To configure the connections to them, locally edit the following properties under `src/main/resources/application.properties`

#### Relational database config

By default it uses a mysql database. Create one and change the connection properties if needed

Default config:
```
smartlandvault.sql.datasource.driver=com.mysql.cj.jdbc.Driver
smartlandvault.sql.datasource.jdbcUrl=jdbc:mysql://localhost:3306/smartlandvault?useLegacyDatetimeCode=false&serverTimezone=UTC
smartlandvault.sql.datasource.username=root
smartlandvault.sql.datasource.password=
```

#### Non-relational database config

A mongodb database is required. Create one and change the connection properties if needed

Default config:
```
smartlandvault.nosql.host=192.168.99.100
smartlandvault.nosql.port=32768
smartlandvault.nosql.db=smartlandvault
```

## Demo

This demonstrates the process of uploading Excel workbooks and REST endpoints to the application.

### Excel

Make sure your data table has the name of the columns in the first row. Example: 

![table](https://imgur.com/Z4rTTjK.png)

(Optional) Create a second sheet in the workbook to describe the metadata with the following format:

![metadata](https://imgur.com/pvBCIFl.png)

Select **+ Add container**

![e1](https://imgur.com/CRQysOr.png)

Select **Upload an Excel Workbook**

![e2](https://imgur.com/eiRYNBR.png)

Fill out the form and once uploaded, click on the ID of the newly created container

![e3](https://imgur.com/IhMxSCb.png)


shows the dataset's information

![e4](https://imgur.com/a8FFcZW.png)

**Browse Data** will show the dataset's columns and their metadata. Clicking on **Raw Data** will open the REST endpoint where the data can now be accesed from.

![e5](https://imgur.com/0noM52w.png)

### REST endpoint

Select **+ Add container**

![r1](https://imgur.com/CRQysOr.png)

Select **Register a REST API endpoint**

![r2](https://imgur.com/eiRYNBR.png)

Fill out the form and once uploaded, click on the ID of the newly created container

![r3](https://imgur.com/neuoFS4.png)

This page shows the dataset's information

![r4](https://imgur.com/r4EeBvl.png)

**Browse Data** on REST endpoints can navigate through the JSON data tree and select a the desired object as the main data property

![r5](https://imgur.com/RlQwo90.png)

**Raw Data** will open the REST endpoint where the data can now be accesed from.

![r6](https://imgur.com/LlIhi9Q.png)
