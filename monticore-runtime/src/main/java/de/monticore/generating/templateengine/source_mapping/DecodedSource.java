package de.monticore.generating.templateengine.source_mapping;

import java.net.URL;

public class DecodedSource {

  public final URL url;

  public final String content;

  /**
   * Default is false
   */
  public final boolean ignored;

  public DecodedSource(URL url) {
    this.url = url;
    this.content = null;
    this.ignored = false;
  }

  public DecodedSource(URL url, String content) {
    this.url = url;
    this.content = content;
    this.ignored = false;
  }

  public DecodedSource(URL url, String content, boolean ignored) {
    this.url = url;
    this.content = content;
    this.ignored = ignored;
  }

  @Override
  public String toString() {
    return "{"
        +url.toString()+","
        +content+","
        +ignored
        +"}";
  }

  @Override
  public int hashCode() {
    return this.url.getFile().hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if(o instanceof DecodedSource) {
      return this.hashCode() == o.hashCode();
    }
    return false;
  }
}
