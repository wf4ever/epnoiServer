Initial requirements
------------------------------------------------------------------------------
Java 1.6 should be installed in the system. 

Running the recommender
------------------------------------------------------------------------------
Assuming that the epnoiServer.zip has been SERVERPATH just type 
cd SERVERPATH
java -jar epnoiServer.jar

Configuration
------------------------------------------------------------------------------
The epnoiServer should work as it is. In order to set an speceific configuration of  the epnoiServer you have to set its environment variables. They can be found in the file SERVERPATH/epnoi/server/epnoi.xml

The file should look like this:

<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd">
<properties>
  <comment>epnoi configuration file</comment>
  <entry key="index.path">SERVERPATH/indexMyExperiment</entry>
  <entry key="model.path">SERVERPATH/lastImportedModel.xml</entry>
  <entry key="server.hostname">the hostname, (localhost if you don't want to specifiy anything special)</entry>
  <entry key="server.path">Server path. It can be left empty</entry>
  <entry key="server.port">The port that the server should listen</entry>
</properties>



