package de.monticore.gradle;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class StatisticsHandler {
  public enum ReportType { GradleReport, JarReport }

  protected static String getStatType(ReportType type){
    String result = "";
    switch(type){
      case GradleReport:
        result = "MC_GRADLE_JSON";
        break;
      case JarReport:
        result = "MC_JAR_JSON";
        break;
    }
    return result;
  }

  private static void sendRequest(URL url, String data, ReportType type) throws IOException {
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("POST");
      connection.setRequestProperty("STAT_TYPE", getStatType(type));
      connection.setRequestProperty("Content-Type", "text/html");

      connection.setDoOutput(true);
      connection.setRequestProperty("Content-Length", Integer.toString(data.length()));
      connection.getOutputStream().write(data.getBytes(StandardCharsets.UTF_8));
      connection.setConnectTimeout(200);

      connection.connect();

      if(connection.getResponseCode() != 200){
        throw new IOException("Server responded with code " + connection.getResponseCode());
      }
  }

  public static void storeReport(String report, ReportType type) {
    try {
      sendRequest(new URL("https://" + "build.se.rwth-aachen.de" + ":8844"), report, type);
    } catch (Exception ignored) {
    }
  }
}
