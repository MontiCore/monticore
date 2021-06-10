/* (c) https://github.com/MontiCore/monticore */

package de.monticore.io;

import de.monticore.io.paths.ModelCoordinate;
import de.monticore.io.paths.ModelCoordinates;
import de.monticore.io.paths.ModelPath;
import de.monticore.utils.Names;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.io.filefilter.RegexFileFilter;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Deprecated
public class FileFinder {

  /**
   * Method for calculating a list of files located in an entry of the passed model path,
   * with the passed qualified model name, and the passed regular expression over the file extension.
   * <p>
   * Example: for a model path comprising two entries "src/test/resources" and "target", the
   * qualified model name "foo.bar.Car", and the file extension regular expression "*sym", the
   * result of this method could be a list with three files:
   * "src/test/resources/foo/bar/Car.fdsym"
   * "src/test/resources/foo/bar/Car.cdsym"
   * "target/foo/bar/Car.fdsym"
   *
   * @param mp
   * @param qualifiedModelName
   * @param fileExtRegEx
   * @return
   */
  public static List<File> getFiles(ModelPath mp, String qualifiedModelName, String fileExtRegEx) {
    List<File> result = new ArrayList<>();
    // calculate the folderPath (e.g., "foo/bar") and fileNameRegEx (e.g., "Car.*sym")
    String folderPath = Names.getPathFromQualifiedName(qualifiedModelName);
    String fileNameRegEx = Names.getSimpleName(qualifiedModelName) + "\\." + fileExtRegEx;

    // initialize a file filter filtering for the regular expression
    FileFilter filter = new RegexFileFilter(fileNameRegEx);

    // iterate model path entries and check whether folder path exists within these
    for (Path p : mp.getFullPathOfEntries()) {
      File folder = p.resolve(folderPath).toFile(); //e.g., "src/test/resources/foo/bar"
      if (folder.exists() && folder.isDirectory()) {
        // perform the actual file filter on the folder and collect result
        for (File f : folder.listFiles(filter)) {
          result.add(Paths.get(folderPath, f.getName()).toFile());
        }
      }
    }

    return result;
  }

  public static List<ModelCoordinate> findFiles(List<File> files, ModelPath mp,
      Set<String> loadedFiles) {
    List<ModelCoordinate> result = new ArrayList<>();
    for (File f : files) {
      if (!loadedFiles.contains(f.toString())) {
        ModelCoordinate mc = ModelCoordinates.createQualifiedCoordinate(Paths.get(f.toString()));
        mc = mp.resolveModel(mc);
        result.add(mc);
      }
    }
    return result;
  }

  public static List<ModelCoordinate> findFiles(ModelPath mp,
      String qualifiedModelName, String fileExtRegEx, Set<String> loadedFiles) {
    List<File> files = getFiles(mp, qualifiedModelName, fileExtRegEx);
    return findFiles(files, mp, loadedFiles);
  }

  public static Optional<ModelCoordinate> findFile(ModelPath mp,
      String qualifiedModelName, String fileExtRegEx, Set<String> loadedFiles) {
    List<ModelCoordinate> files = findFiles(mp, qualifiedModelName, fileExtRegEx, loadedFiles);
    if (1 == files.size()) {
      return Optional.of(files.get(0));
    }
    else if (1 < files.size()) {
      StringBuilder sb = new StringBuilder();
      sb.append("0xA7654 Found multiple files in the model path that could contain the model '"
          + qualifiedModelName + "'!\nThe files are:");
      for (ModelCoordinate mc : files) {
        sb.append("\n" + mc.getLocation());
      }
      Log.error(sb.toString());
    }
    return Optional.empty();
  }

}
