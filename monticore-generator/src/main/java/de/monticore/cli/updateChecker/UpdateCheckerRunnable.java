package de.monticore.cli.updateChecker;

import de.monticore.cli.updateChecker.HttpGetter;
import de.se_rwth.commons.logging.Log;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Properties;

public class UpdateCheckerRunnable implements Runnable {

  final protected static String REMOTE_PROPERTIES_PATH = "https://raw.githubusercontent.com/MontiCore/monticore/opendev/gradle.properties";
  final protected static String LOCAL_PROPERTIES_PATH = "/buildInfo.properties";

  protected String newVersion;

  protected HttpGetter httpGetter;

  protected static class Version {
    protected final boolean snapshot;
    protected final int[] versionNumbers;
    protected final String versionString;

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
      if (this.versionNumbers[0] < other.versionNumbers[0]) return true;
      else if (this.versionNumbers[0] > other.versionNumbers[0]) return false;

      if (this.versionNumbers[1] < other.versionNumbers[1]) return true;
      else if (this.versionNumbers[1] > other.versionNumbers[1]) return false;

      if (this.versionNumbers[2] < other.versionNumbers[2]) return true;
      else if (this.versionNumbers[2] > other.versionNumbers[2]) return false;

      if (!this.snapshot && other.snapshot) return true;

      return false;
    }

    public String getString() {
      return versionString;
    }


  }

  public UpdateCheckerRunnable() {
    httpGetter = new HttpGetter(REMOTE_PROPERTIES_PATH);
    newVersion = "";
  }

  public void setHttpGetter(HttpGetter httpGetter) {
    this.httpGetter = httpGetter;
  }

  public String getNewVersion() {
    return newVersion;
  }

  public String getLocalVersion() {
    return getLocalProperties().getProperty("version");
  }

  public boolean newVersionAvailable() {
    Properties local = getLocalProperties();
    Properties remote = getRemoteProperties();

    String localVersionString = local.getProperty("version");
    String remoteVersionString = remote.getProperty("version");

    if (localVersionString == null || remoteVersionString == null) return false;

    Version localVersion = new Version(localVersionString);
    Version remoteVersion = new Version(remoteVersionString);

    if (localVersion.isOlderThan(remoteVersion)) {
      newVersion = remoteVersion.getString();
      return true;
    }
    return false;
  }

  @Override
  public void run() {
    if(newVersionAvailable()) {
      Log.info("0xA9001 There is a newer Version "
          + newVersion
          + " of this tool available at monticore.de/download", "");
    }
  }

  protected Properties getRemoteProperties() {
    Properties properties = new Properties();

    String raw = httpGetter.getResponse();

    try {
      properties.load(new StringReader(raw));
    } catch(Exception e) {
      Log.warn("0xA9002 Remote properties file is not a well defined properties file");
    }

    return properties;
  }

  protected Properties getLocalProperties() {
    Properties properties = new Properties();

    try {
      properties.load(this.getClass().getResourceAsStream(LOCAL_PROPERTIES_PATH));
    } catch (Exception e) {
      Log.debug("0xA9004 Could not find local properties file", UpdateCheckerRunnable.class.getName());
    }

    return properties;
  }
}
