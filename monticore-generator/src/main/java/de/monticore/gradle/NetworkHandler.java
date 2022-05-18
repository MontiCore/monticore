package de.monticore.gradle;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class NetworkHandler {

  private static void sendRequest(URL url, String data){
    try {
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

      System.out.println(connection.getResponseCode());

    } catch(Exception e) {
      System.err.println("Error sending Gradle Statistics to Server. \n"
          + e.getMessage());
    }
  }
  public static void sendReport(String report){
    try {
      System.out.println("Begin Sending Report to Server");
      sendRequest(new URL("https://build.se.rwth-aachen.de:8844"), report);
      System.out.println("Finished Sending Report to Server");
    } catch (MalformedURLException e) {
      System.err.println("Malformed URL. \n"
          + e.getMessage());    }
  }

  /* public static void sendError(Exception e){

  } */
}
