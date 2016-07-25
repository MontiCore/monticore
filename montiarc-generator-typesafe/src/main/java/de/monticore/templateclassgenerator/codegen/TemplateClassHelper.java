/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
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
  
  public static String printPackageClassWithDepthIndex(String packagePath, String modelPath,
      int depthIndex,
      List<File> files) {
    String ret = packagePath;
    if (ret.contains(modelPath)) {
      ret = ret.replace(modelPath, "");
    }
    if (ret.indexOf(File.separator) == 0) {
      ret = ret.substring(ret.indexOf(File.separator) + 1);
    }
    String[] packages = new String[0];
    if (ret.contains(File.separator)) {
      ret = ret.replace(File.separator, ".");
      packages = ret.split("\\.");
    }
    String currentPackage = "";
    if (packages.length > 0 && packages.length >= depthIndex) {
      currentPackage = packages[depthIndex];
    }
    int occurences = 0;
    for (int i = 0; i < depthIndex; i++) {
      if (packages[i].equals(currentPackage)) {
        occurences++;
      }
    }
    
    for (File f : files) {
      String cleanName = f.getName();
      if (f.getName().contains(".ftl")) {
        cleanName = cleanName.replace(".ftl", "");
      }
      cleanName = cleanName.toLowerCase();
      if (currentPackage.equals(cleanName)) {
        occurences++;
      }
    }
    
    if (occurences > 0) {
      ret += occurences - 1;
    }
    else if (ret.equals("templates")) {
      ret += depthIndex;
    }
    if (ret.contains(".")) {
      ret = ret.substring(ret.lastIndexOf(".") + 1);
    }
    return ret;
  }
  
  public static boolean isTemplateName(String name) {
    return name.endsWith(".ftl");
  }
  
  // public String printGettersForTemplate(String templatePath, String
  // modelPath) {
  // String ret = "";
  // String tmp = "";
  // tmp = templatePath.replace(modelPath, "");
  // if (tmp.indexOf(File.separator) == 0) {
  // tmp = tmp.substring(tmp.indexOf(File.separator) + 1);
  // }
  // tmp = tmp.replace(".ftl", "");
  //
  // String[] packages = new String[0];
  // if (tmp.contains(File.separator)) {
  // tmp = tmp.replace(File.separator, ".");
  // packages = tmp.split("\\.");
  // }
  //
  // String packs = "";
  // for(int i = 0; i<packages.length-1;i++){
  // packs+=packages[i];
  // ret+="get" +
  // capitalizeFirst(printPackageClassWithDepthIndex(modelPath+File.separator+packs,
  // modelPath, i))+"().";
  // if(i<packages.length-2){
  // packs+=File.separator;
  // }
  // }
  //
  // ret+="get"+packages[packages.length-1]+"()";
  //
  // return ret;
  // }
  
  public String printGettersForTemplate(String templatePath, String modelPath,
      List<File> visitedNodes) {
    String tmp = "";
    String ret = "";
    tmp = templatePath.replace(modelPath, "");
    if (tmp.indexOf(File.separator) == 0) {
      tmp = tmp.substring(tmp.indexOf(File.separator) + 1);
    }
    tmp = tmp.replace(".ftl", "");
    
    String[] packages = new String[0];
    if (tmp.contains(File.separator)) {
      tmp = tmp.replace(File.separator, ".");
      packages = tmp.split("\\.");
    }
    
    ret += "get" + capitalizeFirst(packages[0]) + "().";
    for (int i = 1; i < packages.length - 1; i++) {
      ret += "get" + capitalizeFirst(
          printPackageClassWithDepthIndex(getPackageUntilDepthIndex(tmp, i), modelPath,
              i, walkTree(visitedNodes.get(i - 1))))
          + "().";
    }
    
    ret += "get" + packages[packages.length - 1]
        + TemplateClassGeneratorConstants.TEMPLATE_CLASSES_POSTFIX + "()";
    
    return ret;
  }
  
  private String getPackageUntilDepthIndex(String packagePath, int depthIndex) {
    String ret = "";
    String[] packages = new String[1];
    if (packagePath.contains(".")) {
      packages = packagePath.split("\\.");
    }
    else {
      packages[0] = packagePath;
    }
    ret += packages[0];
    for (int i = 1; i <= depthIndex; i++) {
      ret += File.separator + packages[i];
    }
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
  
}
