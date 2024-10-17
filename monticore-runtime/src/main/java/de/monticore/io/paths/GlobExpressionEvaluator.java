/* (c) https://github.com/MontiCore/monticore */
package de.monticore.io.paths;

import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.StringMatchers;
import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * This class evaluates a globbing expression on the current file system.
 */
public class GlobExpressionEvaluator extends SimpleFileVisitor<Path> {

  protected Pattern pattern;

  protected Set<URI> result;

  protected FileSystem fileSystem;

  protected boolean isLocatedInJar;

  public GlobExpressionEvaluator(String pathRegex, FileSystem fs, boolean isLocatedInJar) {
    this.isLocatedInJar = isLocatedInJar;
    if (isLocatedInJar) {
      // this line removes the file system path from the pathRegex
      pathRegex = pathRegex.replaceAll(Pattern.quote(fs.toString().replaceAll("\\\\", "/")), "");
    }
    pattern = Pattern.compile(pathRegex);
    this.fileSystem = fs;

    result = new HashSet<>();
  }

  @Override
  public FileVisitResult visitFile(Path path, BasicFileAttributes attribs) {
    path = path.toAbsolutePath();
    if (pattern.matcher(path.toString()).matches()) {
      URI uri = path.toUri();
      // this takes care of white spaces in files, especially jars, if they are double encoded
      if (uri.toString().contains("%2520")) {
        uri = URI.create(uri.toString().replaceAll("%2520", "%20"));
      }
      result.add(uri);
    }
    return FileVisitResult.CONTINUE;
  }

  protected Set<URI> getResult() {
    return result;
  }

  public Set<URI> evaluate(File start) {
    try {
      Path startingPath = start.toPath();
      if (isLocatedInJar) {
        startingPath = fileSystem.getPath("/");
      }
      Files.walkFileTree(startingPath, this);
    }
    catch (IOException e) {
      Log.error("0x1C193 Error while traversing the file system!", e);
      e.printStackTrace();
      return null;
    }
    return getResult();
  }

}
