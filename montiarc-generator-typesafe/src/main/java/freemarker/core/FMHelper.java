package freemarker.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import freemarker.template.Template;

/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class FMHelper {
  
  
  /**
   * Finds all MethodCalls within dollarsigns in the passed template. Returns a Map with the methodCallName as key 
   * an a list of list of arguments. Each list contains arguments of a single methodCall. 
   * 
   * @param t
   * @return methodCallName -> [param11, param12,...][param21, param22, ...]
   */
  public static Map<String, List<List<String>>> getMethodCalls(Template t) {
    Map<String, List<List<String>>> arguments = new HashMap<>();
    TemplateElement e = t.getRootTreeNode();
    MixedContent mc = (MixedContent) e;
    for (int i = 0; i < mc.getRegulatedChildCount(); i++) {
      TemplateElement child = mc.getRegulatedChild(i);
      if (child instanceof DollarVariable) {
        DollarVariable d = (DollarVariable) child;
        Object o = d.getParameterValue(0);
        if (o instanceof MethodCall) {
          MethodCall m = (MethodCall) o;
          String name = getMethodCallName(m);
          List<String> args = getMethodCallArguments(m);
          List<List<String>> tmp = new ArrayList<>();
          if (arguments.containsKey(name)) {
            tmp = arguments.get(name);
          }
          tmp.add(args);
          arguments.put(name, tmp);
        }
      }
    }
    return arguments;
  }
  
  public static List<Parameter> getParams(List<String> params){
    List<Parameter> ret = new ArrayList<>();
    for(String s: params){
      s = s.replace("\"", "");
      String type = s.substring(0,s.indexOf(" "));
      String name = s.substring(s.indexOf(" ")+1);
      Parameter a = new Parameter(type, name);
      ret.add(a);
    }
    return ret;
  }
  
  
  private static String getMethodCallName(MethodCall m) {
    return ((Expression) m.getParameterValue(0)).toString();
  }
  
  private static List<String> getMethodCallArguments(MethodCall m) {
    List<String> params = new ArrayList<>();
    for (int i = 1; i < m.getParameterCount(); i++) {
      params.add(((Expression) m.getParameterValue(i)).toString());
    }
    return params;
  }
}
