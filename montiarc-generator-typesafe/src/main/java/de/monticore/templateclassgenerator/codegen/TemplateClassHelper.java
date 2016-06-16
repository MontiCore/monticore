/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.templateclassgenerator.codegen;

import java.time.LocalDateTime;
import java.util.List;

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
      ret += p.getType()+ " "+ p.getName() + ", ";
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
   * Prints name without generics and unqualified 
   * e.g. a.b.C<T> -> C
   * 
   * @param fqn
   * @return
   */
  public static String printSimpleName(String fqn) {
    String ret = fqn;
    if (fqn.contains(".")) {
      ret = fqn.substring(fqn.lastIndexOf(".") + 1);
    }
    if(ret.contains("<")){
      ret = ret.substring(0,ret.indexOf("<"));
    }
    return ret;
  }
  
  /**
   * Prints a list of Parameters as String seperated by a comma with "".
   * e.g. Integer i -> "Integer i"  
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
}
