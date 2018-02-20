/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.reporting.artifacts;

import java.nio.file.FileSystems;
import java.nio.file.Path;

public class ReportingNameHelper {

  /**
   * Constructor for de.monticore.generating.templateengine.reporting.artifacts.ReportingNameHelper.
   */
  private ReportingNameHelper() {
  }

  public static Path getPath(String outputDir, String qualifiedFilename, String fileextension) {
    String[] pathParts = qualifiedFilename.split("\\.");
    pathParts[pathParts.length-1] = pathParts[pathParts.length-1] + "." + fileextension; 
    return FileSystems.getDefault().getPath(outputDir, pathParts);
  }
  
  /**
   * @param qn QualifiedName (without fileExtesion) or FullQualifiedName (with fileExtesion)
   * @return
   */
  public static String getPath(String qn) {
    String path = "";
    if (qn.contains(".")) {
      path = qn.substring(0, qn.lastIndexOf("."));
    }
    return path;
  }
  
  /**
   * @param qn QualifiedName (without fileExtension)
   * @return
   */
  public static String getSimpleName(String qn) {
    String simpleName = qn;
    if (qn.contains(".")) {
      simpleName = qn.substring(qn.lastIndexOf(".") + 1, qn.length());
    }
    return simpleName;
  }
  
  /**
   * @param simpleName 
   * @param extension fileExtension
   * @return
   */
  public static String getFullName(String simpleName, String extension) {
    return extension.isEmpty() ? simpleName : simpleName + "." + extension;
  }

  /**
   * @param qn any qualifiedName
   * @return first part of the qualifiedName
   */
  public static String getFirstPathPart(String qn) {
    if (!qn.contains(".")) {
      return qn;
    }
    
    return qn.substring(0, qn.indexOf("."));
  }

  /**
   * @param qn any qualifiedName
   * @return qn without the first part
   */
  public static String removeFirstPathPart(String qn) {
    if (!qn.contains(".")) {
    return "";
    }
    
    return qn.substring(qn.indexOf(".") + 1);
  }

  /** Returns a dot separated name of the file represented by the given path without its fileextension
   *  Example: outputdir is /a/b and path represents /a/b/c/d/e.txt
   *  returns "c.d.e"
   * 
   * @param outputDir
   * @param path
   * @return
   */
  public static String getQualifiedName(String outputDir, Path path) {
    StringBuilder qualifiedName = new StringBuilder(path.getName(0).toString());
    
    for (int i = 1; i < path.getNameCount() - 1; i++) {
      qualifiedName.append(".");
      qualifiedName.append(path.getName(i));
    }
    if (path.getFileName() != null) {
      String[] seperatedFileName = path.getFileName().toString().split("\\.");
      qualifiedName.append("." + seperatedFileName[0]);
    }
    return qualifiedName.toString();
  }
  
  /** Returns the fileextension of the file represented by the given path
   *  Example: outputdir is /a/b and path represents /a/b/c/d/e.txt.tmp
   *  returns txt.tmp
   * 
   * @param path
   * @return
   */
  public static String getFileextension(Path path) {   
    String fileextension = null;    
    String fullFileName = (path.getFileName()==null)?"":path.getFileName().toString();  
    if (fullFileName.contains(".")) {
      fileextension = fullFileName.substring(fullFileName.indexOf(".") + 1);
    }
    return fileextension;
  }
  
}
