<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("buildDate", "toolName")}

// Get version string from Metadata and print

// final String buildDate = "${buildDate}";
final String toolName = "${toolName}";

java.util.Properties properties = new java.util.Properties();

try {
  java.io.BufferedInputStream stream = new java.io.BufferedInputStream(getClass().getResourceAsStream("/buildInfo.properties"));
  properties.load(stream);
  stream.close();
} catch(java.io.IOException e) {
  // Property files not present
}


<#-- String toolName = metaproperties.getProperty("toolName");
String buildDate = metaproperties.getProperty("buildDate"); -->
String buildDate = properties.getProperty("buildDate");
String toolVersion = properties.getProperty("version");
String monticoreVersion = properties.getProperty("mc_version");

System.out.println(toolName +
    ", version " + toolVersion +
    ", build " + buildDate +
    ", based on MontiCore version " + monticoreVersion);