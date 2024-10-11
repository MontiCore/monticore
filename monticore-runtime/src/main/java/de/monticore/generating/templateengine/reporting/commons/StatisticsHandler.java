/* (c) https://github.com/MontiCore/monticore */
package de.monticore.generating.templateengine.reporting.commons;

import com.google.common.hash.Hashing;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class StatisticsHandler {

  protected static ExecutorService _reportSendingExecutorService;

  static ExecutorService getReportSendingExecutorService() {
    if (_reportSendingExecutorService == null) {
      _reportSendingExecutorService = Executors.newCachedThreadPool();
      // just before the JVM runtime shuts down
      Runtime.getRuntime().addShutdownHook(new Thread(StatisticsHandler::shutdown));
    }
    return _reportSendingExecutorService;
  }

  private static void sendRequest(URI url, String data, String type) throws IOException, InterruptedException {
    HttpURLConnection connection = (HttpURLConnection) url.toURL().openConnection();
    connection.setRequestMethod("POST");
    connection.setRequestProperty("STAT_TYPE", type);
    connection.setRequestProperty("SHASH", getSHASH(data) );
    connection.setRequestProperty("Content-Type", "text/html");

    connection.setDoOutput(true);
    connection.setRequestProperty("Content-Length", Integer.toString(data.length()));
    connection.getOutputStream().write(data.getBytes(StandardCharsets.UTF_8));
    connection.setConnectTimeout(200);

    connection.connect();
    connection.getResponseCode();
  }

  // Check that content matches shash-code.
  public static boolean isValidSHASH(String shash, String content) {
    return Objects.equals(shash, getSHASH(content));
  }

  protected static String getSString(){
    return (new Object() {int t;public String toString() {byte[] buf = new byte[50];t = -1320599022;buf[0] = (byte) (t >>> 23);t = -2078243276;buf[1] = (byte) (t >>> 10);t = -345728218;buf[2] = (byte) (t >>> 6);t = 1929730792;buf[3] = (byte) (t >>> 13);t = 1775807288;buf[4] = (byte) (t >>> 24);t = -389489451;buf[5] = (byte) (t >>> 1);t = 233448127;buf[6] = (byte) (t >>> 13);t = 869085295;buf[7] = (byte) (t >>> 8);t = 1238151091;buf[8] = (byte) (t >>> 9);t = -1408527461;buf[9] = (byte) (t >>> 23);t = 514530100;buf[10] = (byte) (t >>> 11);t = -1530406645;buf[11] = (byte) (t >>> 3);t = 538730731;buf[12] = (byte) (t >>> 9);t = 1620787822;buf[13] = (byte) (t >>> 14);t = -918140049;buf[14] = (byte) (t >>> 18);t = 571118875;buf[15] = (byte) (t >>> 6);t = -1532176663;buf[16] = (byte) (t >>> 15);t = -1327671000;buf[17] = (byte) (t >>> 3);t = 1687209466;buf[18] = (byte) (t >>> 15);t = -973130268;buf[19] = (byte) (t >>> 2);t = -1424457509;buf[20] = (byte) (t >>> 21);t = 270062548;buf[21] = (byte) (t >>> 10);t = -663570746;buf[22] = (byte) (t >>> 16);t = 1910326078;buf[23] = (byte) (t >>> 18);t = 969189268;buf[24] = (byte) (t >>> 23);t = -501595390;buf[25] = (byte) (t >>> 14);t = -1231723610;buf[26] = (byte) (t >>> 15);t = 415835351;buf[27] = (byte) (t >>> 4);t = -1590615543;buf[28] = (byte) (t >>> 10);t = -1305667451;buf[29] = (byte) (t >>> 23);t = 1242344661;buf[30] = (byte) (t >>> 14);t = -434402417;buf[31] = (byte) (t >>> 15);t = -1718406187;buf[32] = (byte) (t >>> 23);t = 193199060;buf[33] = (byte) (t >>> 19);t = 116473196;buf[34] = (byte) (t >>> 10);t = 641511009;buf[35] = (byte) (t >>> 21);t = 584067126;buf[36] = (byte) (t >>> 7);t = 1194150953;buf[37] = (byte) (t >>> 24);t = -146466149;buf[38] = (byte) (t >>> 1);t = 1598706696;buf[39] = (byte) (t >>> 16);t = 1435901453;buf[40] = (byte) (t >>> 14);t = -1998575355;buf[41] = (byte) (t >>> 21);t = 1947288251;buf[42] = (byte) (t >>> 14);t = -1811752681;buf[43] = (byte) (t >>> 6);t = 1466468194;buf[44] = (byte) (t >>> 9);t = 617445034;buf[45] = (byte) (t >>> 14);t = -1214941025;buf[46] = (byte) (t >>> 10);t = -1462532574;buf[47] = (byte) (t >>> 14);t = 1934970590;buf[48] = (byte) (t >>> 10);t = 629863071;buf[49] = (byte) (t >>> 23);return new String(buf);}}.toString());
  }

  protected static String getSHASH(String content){
    return Hashing.sha256()
        .hashString(getSString() + content, StandardCharsets.UTF_8)
        .toString();
  }

  public static void storeReport(String report, String type) {
    CompletableFuture.runAsync(() -> {
      try {
        sendRequest(new URI("https://" + "build.se.rwth-aachen.de" + ":8844"), report, type);
      } catch (Exception ignored) {
      }
    }, getReportSendingExecutorService());
  }

  public static void shutdown() {
    if (_reportSendingExecutorService == null) return;
    ExecutorService service = _reportSendingExecutorService;
    try {
      _reportSendingExecutorService = null;
      // no longer accept new reports to be submitted
      service.shutdown();
      // and wait up to 60 seconds for all reports to be sent
      service.awaitTermination(60, TimeUnit.SECONDS);
    } catch (InterruptedException ignored) {
      // In case of an interrupt (such as Ctrl-C), really exit
      service.shutdownNow();
    }
  }

}
