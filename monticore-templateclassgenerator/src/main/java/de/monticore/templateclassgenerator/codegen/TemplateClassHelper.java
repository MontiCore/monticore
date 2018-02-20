/* (c) https://github.com/MontiCore/monticore */
package de.monticore.templateclassgenerator.codegen;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Strings;
import com.google.common.primitives.Chars;

import de.se_rwth.commons.Files;
import de.se_rwth.commons.Names;
import freemarker.core.Parameter;

/**
 * Common helper methods for generator.
 *
 * @author Jerome Pfeiffer
 */
public class TemplateClassHelper {
  
  public static String getTimeNow() {
    return LocalDateTime.now().toString();
  }
  
  /**
   * Prints a list of parameters as a single String seperated by a comma
   * 
   * @param parameters
   * @return
   */
  public static String printParameters(List<Parameter> parameters) {
    String ret = "";
    for (Parameter p : parameters) {
      ret += p.getType() + " " + p.getName() + ", ";
    }
    if (ret.contains(",")) {
      return ret.substring(0, ret.lastIndexOf(","));
    }
    
    return ret;
  }
  
  /**
   * Prints a String with only the parameter's names seperated by a comma
   * 
   * @param parameters
   * @return
   */
  public static String printParameterNames(List<Parameter> parameters) {
    String ret = "";
    for (Parameter a : parameters) {
      ret += a.getName() + ", ";
    }
    if (ret.contains(",")) {
      return ret.substring(0, ret.lastIndexOf(","));
    }
    return ret;
  }
  
  /**
   * Prints name without generics and unqualified e.g. a.b.C<T> -> C
   * 
   * @param fqn
   * @return
   */
  public static String printSimpleName(String fqn) {
    String ret = fqn;
    if (fqn.contains(".")) {
      ret = fqn.substring(fqn.lastIndexOf(".") + 1);
    }
    if (ret.contains("<")) {
      ret = ret.substring(0, ret.indexOf("<"));
    }
    return ret;
  }
  
  /**
   * Prints a list of Parameters as String seperated by a comma with "". e.g.
   * Integer i -> "Integer i"
   * 
   * @param parameters
   * @return
   */
  public static String printParametersAsStringList(List<Parameter> parameters) {
    String ret = "";
    for (int i = 0; i < parameters.size(); i++) {
      if (i != parameters.size() - 2 && i != 0) {
        ret += ", ";
      }
      ret += "\"" + parameters.get(i).getName() + "\"";
    }
    return ret;
  }
  
  /**
   * Walks down one level in the filetree.
   * @param node
   * @return all files below the level of {@code node}
   */
  public static List<File> walkTree(File node) {
    List<File> ret = new ArrayList<File>();
    if (node.isDirectory()) {
      String[] subnote = node.list();
      for (String filename : subnote) {
        File f = new File(node, filename);
        ret.add(f);
      }
      return ret;
    }
    return ret;
  }
  
  /**
   * Converts a/b/c/X.ftl -> a.b.c.X
   * 
   * @param path
   * @return
   */
  public static String printFQNTemplateNameFromPath(String path, String modelPath) {
    String ret = path;
    if (ret.contains(modelPath)) {
      ret = ret.replace(modelPath, "");
    }
    if (ret.contains(File.separator)) {
      if (ret.indexOf(File.separator) == 0) {
        ret = ret.substring(1);
      }
      ret = ret.replace(File.separatorChar, '.');
      
    }
    if (ret.contains(".ftl")) {
      ret = ret.replace(".ftl", "");
    }
    return ret;
  }
  
  /**
   * Converts a/b/c/X.ftl -> X
   * 
   * @param path
   * @return
   */
  public static String printSimpleTemplateNameFromPath(String path, String modelPath) {
    return printSimpleName(printFQNTemplateNameFromPath(path, modelPath));
  }
  
  public static boolean isTemplateName(String name) {
    return name.endsWith(".ftl");
  }
  
  public String printGettersForTemplate(String templatePath, String modelPath) {
    String tmp = "";
    String ret = "";
    tmp = templatePath.replace(modelPath, "");
    if (tmp.indexOf(File.separator) == 0) {
      tmp = tmp.substring(tmp.indexOf(File.separator) + 1);
    }
    tmp = tmp.replace(".ftl", "");
    if (tmp.contains(File.separator)) {
      tmp = tmp.replace(File.separator, "_");
    }
    
    ret += "get" + capitalizeFirst(tmp) + "()";
    return ret;
  }
  
  private static String capitalizeFirst(String toCap) {
    String ret = "";
    if (null != toCap && toCap.length() > 0) {
      char first = toCap.charAt(0);
      first = Character.toUpperCase(first);
      ret += first;
    }
    if (toCap.length() > 1) {
      ret += toCap.substring(1);
    }
    return ret;
  }
  
  public static String replaceDotsWithUnderscores(String packageName) {
    if (packageName.contains(".")) {
      return packageName.replace(".", "_");
    }
    return packageName;
  }
  
  public static boolean isMainTemplate(String template) {
    return template.endsWith("Main");
  }
}
