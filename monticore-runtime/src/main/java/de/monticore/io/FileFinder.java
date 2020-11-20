/* (c) https://github.com/MontiCore/monticore */

package de.monticore.io;

import de.monticore.io.paths.ModelPath;
import de.monticore.utils.Names;
import org.apache.commons.io.filefilter.RegexFileFilter;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileFinder {

  /**
   * Method for calculating a list of files located in an entry of the passed model path,
   * with the passed qualified model name, and the passed regular expression over the file extension.
   *
   * Example: for a model path comprising two entries "src/test/resources" and "target", the
   * qualified model name "foo.bar.Car", and the file extension regular expression "*sym", the
   * result of this method could be a list with three files:
   *   "src/test/resources/foo/bar/Car.fdsym"
   *   "src/test/resources/foo/bar/Car.cdsym"
   *   "target/foo/bar/Car.fdsym"
   * @param mp
   * @param qualifiedModelName
   * @param fileExtRegEx
   * @return
   */
  public static List<File> getFiles(ModelPath mp, String qualifiedModelName, String fileExtRegEx) {
    List<File> result = new ArrayList<>();
    // calculate the folderPath (e.g., "foo/bar") and fileNameRegEx (e.g., "Car.*sym")
    String folderPath = Names.getPathFromQualifiedName(qualifiedModelName);
    String fileNameRegEx = Names.getSimpleName(qualifiedModelName) + "." + fileExtRegEx;

    // initialize a file filter filtering for the regular expression
    FileFilter filter = new RegexFileFilter(fileNameRegEx);

    // iterate model path entries and check whether folder path exists within these
    for(Path p : mp.getFullPathOfEntries()){
      File folder = p.resolve(folderPath).toFile(); //e.g., "src/test/resources/foo/bar"
      if(folder.exists() && folder.isDirectory()){
        // perform the actual file filter on the folder and collect result
        File[] files = folder.listFiles(filter);
        result.addAll(Arrays.asList(files));
      }
    }

    return result;
  }
}
