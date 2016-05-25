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
