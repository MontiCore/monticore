<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("buildDate", "toolName")}

// Get version string from Metadata and print

// final String buildDate = "${buildDate}";
final String toolName = "${toolName}";

java.util.Properties properties = new java.util.Properties();

try {
  java.io.BufferedInputStream metastream = new java.io.BufferedInputStream(getClass().getResourceAsStream("${metadataFile}"));
  metaproperties.load(metastream);
  metastream.close();
} catch(java.io.IOException e) {
  e.printStackTrace();
  // Metadata property file not present
}

try {
  java.io.BufferedInputStream gradlestream = new java.io.BufferedInputStream(new java.io.FileInputStream("gradle.properties"));
  gradleproperties.load(gradlestream);
  gradlestream.close();
} catch(java.io.IOException e) {
  e.printStackTrace();
  // Gradle property file not present
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