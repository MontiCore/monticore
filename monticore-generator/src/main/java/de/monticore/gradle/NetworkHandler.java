package de.monticore.gradle;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class NetworkHandler {

  private static void sendRequest(URL url, String data) throws IOException {
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("POST");
      connection.setRequestProperty("STAT_TYPE", "MC_GRADLE");
      connection.setRequestProperty("Content-Type", "text/html");

      connection.setDoOutput(true);
      // connection.setDoInput(false);
      connection.setRequestProperty("Content-Length", Integer.toString(data.length()));
      connection.getOutputStream().write(data.getBytes(StandardCharsets.UTF_8));
      connection.setConnectTimeout(1000);

      connection.connect();

      if(connection.getResponseCode() != 200){
        throw new IOException("Server responded with code " + connection.getResponseCode());
      }
  }

  public static void sendReport(String report){
    try {
      sendRequest(new URL("https://build.se.rwth-aachen.de:8844"), report);
      System.out.println("Performance Statistic Successful");
    } catch (Exception e) {
      System.err.println("Failure Sending Performance Statistic. \n"
          + e.getMessage());    }
  }

  /* public static void sendError(Exception e){

  } */
}
