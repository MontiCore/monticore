<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("toolName", "mcVersion", "buildDate")}

// Get version string from Metadata and print

final String buildDate = "${buildDate}";
final String toolName = "${toolName}";
final String mcVersion = "${mcVersion}";

java.util.Properties properties = new java.util.Properties();

try {
  java.io.BufferedInputStream stream = new java.io.BufferedInputStream(getClass().getResourceAsStream("/buildInfo.properties"));
  properties.load(stream);
  stream.close();
} catch(java.io.IOException e) {
  // BuildInfo not present
}

String toolVersion = properties.getProperty("version");
if (toolVersion == null) toolVersion = mcVersion;

System.out.println(toolName +
    ", version " + toolVersion +
    ", build date " + buildDate +
    ", based on MontiCore version " + mcVersion);