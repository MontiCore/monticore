/* (c) https://github.com/MontiCore/monticore */
package de.monticore.io;

import com.google.common.base.Charsets;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.se_rwth.commons.io.SharedCloseable;
import de.se_rwth.commons.logging.Log;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Fix introduced in MC 7.8.0-SNAPSHOT
 */
public class FileReaderWriterFix extends FileReaderWriter {
  protected Reader _getReader(URL location) {
    try {
      if (!"jar".equals(location.getProtocol())) {
        Path p = Paths.get(location.toURI());
        Reporting.reportOpenInputFile(Optional.of(p.getParent()),
                                      p.getParent().relativize(p));
        // Do not try to decode URL
        return new FileReader(new File(location.toURI()));
      }
      String[] parts = location.toURI().toString().split("!");
      Path p = Paths.get(parts[1].substring(1));
      Reporting.reportOpenInputFile(Optional.of(Paths.get(parts[0].substring(10))),
                                    p);

      // Save opened jar files for later cleanup
      URLConnection conn = location.openConnection();
      if (conn instanceof JarURLConnection) {
        synchronized (SharedCloseable.class) {
          // We have to ensure the JarURLConnection#getJarFile and new SharedCloseable are performed atomic.
          // Otherwise, the backing JarFile might be closed in between.
          // Note: the JVM shares JarFiles across classloader isolations
          openedJarFiles.add(new SharedCloseable<>(((JarURLConnection) conn).getJarFile()));
        }
      }
      return new InputStreamReader(conn.getInputStream(), Charsets.UTF_8.name());
    } catch (IOException | URISyntaxException e) {
      Log.error("0xA6104 Exception occurred while reading the file at '" + location + "':", e);
    }
    return null;
  }

  public static void init() {
    INSTANCE = new FileReaderWriterFix();
  }
}
