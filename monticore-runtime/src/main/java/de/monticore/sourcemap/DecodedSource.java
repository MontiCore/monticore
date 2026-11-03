/* (c) https://github.com/MontiCore/monticore */

package de.monticore.sourcemap;

import java.net.URL;

/**
 * Represents a decoded source that contains a URL, associated content, and a flag indicating
 * whether it is ignored.
 */
public record DecodedSource(URL url, String content, boolean ignored) {

  /**
   * Constructs a {@code DecodedSource} with the specified URL, a null content value,
   * and an ignored flag set to false.
   *
   * @param url the URL representing the source to be decoded
   */
  public DecodedSource(URL url) {
    this(url, null, false);
  }

  /**
   * Constructs a {@code DecodedSource} with the specified URL and content, and an ignored flag set to false.
   *
   * @param url     the URL representing the source to be decoded
   * @param content the content associated with the source
   */
  public DecodedSource(URL url, String content) {
    this(url, content, false);
  }

  @Override
  public String toString() {
    return "{"
        + url.toString() + ","
        + content + ","
        + ignored
        + "}";
  }
}
