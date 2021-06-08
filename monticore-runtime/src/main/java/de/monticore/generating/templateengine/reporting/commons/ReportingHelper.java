/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.reporting.commons;

import com.google.common.hash.Hashing;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.io.IOException;

/**
 * Helper to write files
 * 
 */
public class ReportingHelper {
  
  /**
   * Removes all tabs and whitespaces. Transforms the string into a StringValue, beginning and
   * ending with quotes. If the length is lower than 7 and the formatted string does not fit in, the
   * output is "[...]".
   * 
   * @param toBeFormatted
   * @param length
   * @return
   */
  public static String formatStringToReportingString(String toBeFormatted,
      int length) {
    String replaced = toBeFormatted.replaceAll("\n", " ");
    replaced = replaced.replaceAll("\t", " ");
    replaced = replaced.replaceAll(" +", " ");
    if (length > 2 && replaced.length() < length - 2) {
      return "\"" + replaced + "\"";
    }
    else if (length > 7) {
      return "\"" + replaced.substring(0, length - 7) + "[...]\"";
    }
    else {
      return "\"[...]\"";
    }
  }
  
  /**
   * Removes all line breaks. Returns [...] if formatted string is longer than the parameter length.
   * 
   * @param toBeFormatted
   * @param length
   * @return
   */
  public static String formatLineToReportingLine(String toBeFormatted,
      int length) {
    String replaced = toBeFormatted.replaceAll("\n", " ");
    if (replaced.length() <= length) {
      return replaced;
    }
    else if (length > 5) {
      return replaced.substring(0, length - 5) + "[...]";
    }
    else {
      return "[...]";
    }
  }
  
  public static String getHookPointName(String hookName) {
    return "HP:\"" + hookName + "\"";
  }
  
  public static String getTemplateName(String hookName) {
    return Names.getSimpleName(hookName) + "."
        + ReportingConstants.TEMPLATE_FILE_EXTENSION;
  }

  /**
   * Calculate the MD5 checksum for the given file.
   *
   * @param file
   * @return
   */
  public static String getChecksum(String file) {
    try {
      return com.google.common.io.Files.hash(new File(file), Hashing.md5()).toString();
    } catch (IOException e) {
      Log.error("0xA1021 Failed to calculate current checksum for file " + file, e);
      return "";
    }
  }
}
