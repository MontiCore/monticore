/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cli.updateChecker;

import de.se_rwth.commons.logging.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpGetter {

  protected String url;

  public HttpGetter(String url) {
    this.url = url;
  }

  public String getResponse() {
    String ret = "";

    try {
      URL remotePropertiesUrl = new URL(url);
      HttpURLConnection connection = (HttpURLConnection) remotePropertiesUrl.openConnection();
      connection.setRequestMethod("GET");

      BufferedReader in = new BufferedReader(
          new InputStreamReader(connection.getInputStream()));
      String inputLine;
      StringBuilder content = new StringBuilder();
      while ((inputLine = in.readLine()) != null) {
        content.append(inputLine + "\n");
      }
      in.close();

      ret = content.toString();

    } catch(Exception e) {
      Log.warn("0xA9000 Could not get remote properties file");
    }

    return ret;
  }
}
