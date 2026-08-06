/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cli.updateChecker;

import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateCheckerRunnable implements Runnable {
  
  final protected static String REMOTE_PROPERTIES_PATH =
      "https://raw.githubusercontent.com/MontiCore/monticore/HEAD/gradle.properties";
  final protected static String LOCAL_PROPERTIES_PATH = "/buildInfo.properties";
  
  protected static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(5);
  protected static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(5);
  
  @Override
  public void run() {
    checkVersion();
  }

  public String getLocalVersion() {
    return getLocalProperties().getProperty("version");
  }

  public void checkVersion() {
    Properties local = getLocalProperties();
    Properties remote = getRemoteProperties();

    String localVersionString = local.getProperty("version");
    String remoteVersionString = remote.getProperty("version");

    if (localVersionString == null || remoteVersionString == null) {
      return;
    }

    Version localVersion = new Version(localVersionString);
    Version remoteVersion = new Version(remoteVersionString);

    if (localVersion.isOlderThan(remoteVersion)) {
      Log.info("0xA9001 There is a newer Version " + remoteVersion.getString()
          + " of this tool available at monticore.de/download", "");
    }
  }

  protected Properties getRemoteProperties() {
    Properties properties = new Properties();

    try(HttpClient client = createHttpClient()) {
      HttpRequest request =
          HttpRequest.newBuilder(URI.create(REMOTE_PROPERTIES_PATH))
              .GET()
              .timeout(REQUEST_TIMEOUT)
              .header("Accept", "text/plain")
              .build();

      HttpResponse<String> response =
          client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
      
      if (response.statusCode() / 100 != 2) {
        Log.warn("0xA9008 Could not get remote properties file, http status: " + response.statusCode());
      }
      else {
        String remoteProperties = response.body();
        try {
          properties.load(new StringReader(remoteProperties));
        }
        catch (IOException e) {
          Log.warn("0xA9002 Remote properties file is not a well defined properties file");
        }
      }
    }
    catch (IOException | InterruptedException e) {
      Log.warn("0xA9003 Could not retrieve remote properties file");
    }

    return properties;
  }

  protected HttpClient createHttpClient() {
    return HttpClient.newBuilder().connectTimeout(CONNECT_TIMEOUT)
        .followRedirects(HttpClient.Redirect.NORMAL).build();
  }

  protected Properties getLocalProperties() {
    Properties properties = new Properties();

    Optional<InputStream> localPropertiesStream = Optional.ofNullable(
        this.getClass().getResourceAsStream(LOCAL_PROPERTIES_PATH));
    
    if (localPropertiesStream.isEmpty()) {
      Log.warn("0xA9006 Could not retrieve local properties file");
    }
    else {
      try {
        properties.load(localPropertiesStream.get());
      }
      catch (IOException e) {
        Log.debug("0xA9004 Could not find local properties file",
            UpdateCheckerRunnable.class.getName());
      }
    }

    return properties;
  }

  protected static class Version {

    protected final boolean snapshot;
    protected final int[] versionNumbers;
    protected final String versionString;

    public Version(String version) {
      this.versionString = version;

      this.snapshot = version.contains("SNAPSHOT");

      Pattern pattern = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)");
      Matcher matcher = pattern.matcher(this.versionString);
      
      if (matcher.find()) {
        int major = Integer.parseInt(matcher.group(1));
        int minor = Integer.parseInt(matcher.group(2));
        int patch = Integer.parseInt(matcher.group(3));
        
        this.versionNumbers = new int[] { major, minor, patch };
      }
      else {
        Log.warn("0xC4111: Could not parse version number: " + version);
        this.versionNumbers = new int[] { 0, 0, 0 };
      }
    }

    public boolean isOlderThan(Version other) {
      for (int i = 0; i < this.versionNumbers.length; i++) {
        int comparison = Integer.compare(this.versionNumbers[i], other.versionNumbers[i]);
        if (comparison != 0) {
          return comparison < 0;
        }
      }

      return !this.snapshot && other.snapshot;
    }

    public String getString() {
      return this.versionString;
    }

  }
}
