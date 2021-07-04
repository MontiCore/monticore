<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("metadataFile")}

// Get version string from Metadata and print

java.util.Properties metaproperties = new java.util.Properties();
java.util.Properties gradleproperties = new java.util.Properties();

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


String toolName = metaproperties.getProperty("toolName");
String buildDate = metaproperties.getProperty("buildDate");
String toolVersion = gradleproperties.getProperty("version");
String monticoreVersion = gradleproperties.getProperty("mc_version");

System.out.println(toolName +
    ", version " + toolVersion +
    ", build " + buildDate +
    ", based on MontiCore version " + monticoreVersion);