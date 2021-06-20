<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("metadataFile")}

// Get version string from Metadata and print

java.util.Properties properties = new java.util.Properties();
java.io.BufferedInputStream stream = new java.io.BufferedInputStream(new java.io.FileInputStream("${metadataFile}"));
properties.load(stream);
stream.close();

String toolName = properties.getProperty("toolName");
String buildDate = properties.getProperty("buildDate");
String toolVersion = properties.getProperty("toolVersion");
String monticoreVersion = properties.getProperty("monticoreVersion");

System.out.println(toolname +
    ", version " + toolVersion +
    ", build " + buildDate +
    ", based on MontiCore version " + monticoreVersion);