package de.monticore;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * This class is a duplicate of de.monticore.utils.IncChecker (monticore-runtime)
 * and will be removed once 7.3.0 is the previous MC version
 */
public class IncChecker {

  /**
   * Provides the logic for the incremental build checks
   * @param incGenGradleCheckFile the IncGenGradleCheck.txt file
   * @param modelName name of the model (such as name of the grammar), used for logging
   * @param logger logger
   * @param fileExtension file extension (such as "mc4") of the input model files (must be equal to the one used in the IncGenGradleCheck)
   * @return false, if the input files have changed and the task should not be skipped
   */
  public static boolean incCheck(File incGenGradleCheckFile, String modelName, Logger logger, String fileExtension) throws IOException {
    if (incGenGradleCheckFile.exists()) { // check whether report exists
      int fileExtensionLength = fileExtension.length();
      // check for new files to consider
      StringBuilder newFiles = new StringBuilder();
      // check for considered but deleted files
      StringBuilder removedFiles = new StringBuilder();
      // consider every file referenced in the inc gen gradle check file
      for (String line : Files.readLines(incGenGradleCheckFile, Charset.defaultCharset())) {
        if (line.startsWith("gen:") && new File(line.substring(4)).exists()) {
          newFiles.append(line);
        }
        if (line.startsWith("hwc:") && !new File(line.substring(4)).exists()) {
          removedFiles.append(line);
        }
        // check whether local modals (such as super grammars) have changed
        if (line.startsWith(fileExtension + ":") && line.length() > 33 + fileExtensionLength) {
          // Since the path can also contain spaces, do not use tokenize
          String inputModelString = line.substring(fileExtensionLength + 1, line.length() - 32);
          String checksum = line.substring(line.length() - 33);
          File inputModelFile = new File(inputModelString);
          if (!inputModelFile.exists()) { // deleted model -> generate
            logger.info("Regenerating Code for " + modelName + " : Input Model " + inputModelString + " does so longer exist.");
            return false;
          } else if (!Files.asByteSource(inputModelFile).hash(Hashing.md5()).toString().equals(checksum.trim())) {
            // changed model -> generate
            logger.info("Regenerating Code for " + modelName + " : Input Model " + inputModelString + " has changed");
            return false;
          }
        }

      }
      String added = newFiles.toString();
      if (!added.isEmpty()) { // added files -> generate
        logger.info("Added files:\n" + added);
        return false;
      }
      // check for considered but deleted files
      String removed = removedFiles.toString();
      if (!removed.isEmpty()) { // deleted files -> generate
        logger.info("Removed files:\n" + removed);
        return false;
      }
    } else { // no report -> generate
      logger.info("No previous generation report " + incGenGradleCheckFile + " found");
      return false;
    }
    return true;
  }
}
