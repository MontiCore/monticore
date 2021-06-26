package de.monticore.cli;

import de.se_rwth.commons.logging.Log;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Properties;

public class UpdateCheckerRunner implements Runnable {

  final protected static String REMOTE_PROPERTIES_PATH = "https://raw.githubusercontent.com/MontiCore/monticore/dev/gradle.properties";
  final protected static String LOCAL_PROPERTIES_PATH = "gradle.properties";

  protected class Version {
    private final boolean snapshot;
    private final int[] versionNumbers;
    private final String versionString;

    public Version(String version) {
      versionString = version;

      snapshot = version.contains("SNAPSHOT");

      String withoutSnapshot = version.replace("-SNAPSHOT", "");
      String[] versionNumbersStrings = withoutSnapshot.split("\\.");

      versionNumbers = new int[]{0, 0, 0};
      versionNumbers[0] = Integer.parseInt(versionNumbersStrings[0]);
      versionNumbers[1] = Integer.parseInt(versionNumbersStrings[1]);
      versionNumbers[2] = Integer.parseInt(versionNumbersStrings[2]);
    }

    public boolean isOlderThan(Version other) {
      if (this.versionNumbers[0] > other.versionNumbers[0]) return false;
      if (this.versionNumbers[1] > other.versionNumbers[1]) return false;
      if (this.versionNumbers[2] > other.versionNumbers[2]) return false;
      if (this.snapshot && !other.snapshot) return false;

      return true;
    }

    public String getString() {
      return versionString;
    }


  }

  protected String getRemotePropertiesString() {
    String ret = "";

    try {
      URL remotePropertiesUrl = new URL(REMOTE_PROPERTIES_PATH);
      HttpURLConnection connection = (HttpURLConnection) remotePropertiesUrl.openConnection();
      connection.setRequestMethod("GET");

      BufferedReader in = new BufferedReader(
          new InputStreamReader(connection.getInputStream()));
      String inputLine;
      StringBuilder content = new StringBuilder();
      while ((inputLine = in.readLine()) != null) {
        content.append(inputLine);
      }
      in.close();

      ret = content.toString();

    } catch(Exception e) {

    }

    return ret;
  }

  protected Properties getProperties(String raw) {
    Properties properties = new Properties();
    try {
      properties.load(new StringReader(raw));
    } catch(Exception e) {

    }

    return properties;
  }

  protected Properties getRemoteProperties() {
    return getProperties(getRemotePropertiesString());
  }

  protected Properties getLocalProperties() {
    Properties properties = new Properties();
    try {
      properties.load(new FileInputStream(LOCAL_PROPERTIES_PATH));
    } catch (Exception e) {
      e.printStackTrace();
    }

    return properties;
  }

  @Override
  public void run() {
    Properties local = getLocalProperties();
    Properties remote = getRemoteProperties();

    Version localVersion = new Version(local.getProperty("version"));
    Version remoteVersion = new Version(remote.getProperty("version"));

    if (localVersion.isOlderThan(remoteVersion)) {
      // log that
      Log.warn("[INFO] There is a newer Version "
          + remoteVersion.getString()
          + " of this tool available at monticore.de/download");
    }

  }
}
