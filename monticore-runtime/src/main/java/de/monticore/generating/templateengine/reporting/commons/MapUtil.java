/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.reporting.commons;

// TODO: Used by reporting tool

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * This class provides some basic functionality to manage
 * additional data.
 * 
 * * Organizing Maps 
 * 
 * static methods are are a bad solution, but methods are used from the templates
 * as well as from other Java-code.
 * 
 * Method have no sideffects and can thus be used robustly
 * 
 * TODO: Better: Provide complete implementation of a Map and all its functionality
 * with encapsulated map (provide it as a good util). Unless such a thing already exists 
 * e.g. in Guava
 * 
 * @author rumpe
 *
 */

public class MapUtil {

  private MapUtil() {} 
  
  /**
   * Given a compact identifier: this method validates
   * it is new in map obj2idents. Otherwise a distinguishing extension is added
   * and the second map (which counts the extensions updated)
   * @param obj2idents lists all the   
   * @param o
   * @param s   to be checked for uniqueness
   */
  public static <T> void addANewIdent(Map<T, String> obj2idents, Map<String, Integer> identNo, T o, String s) {
    int ext = 0;
    String res = s;
    if (identNo.containsKey(s)) {
      ext = identNo.get(s);
      res = s + "!" + (ext + 1) + "!";
    }
    identNo.put(s, ext + 1);
    obj2idents.put(o, res);
  }
  
  /**
   * Increments the number of occurrences counted in the map
   * @param map
   * @param key    Key as String
   */
  public static void incMapValue(Map<String, Integer> map, String key) {
    int currentVal;
    if (map.containsKey(key)) {
      currentVal = map.get(key);
    }
    else {
      currentVal = 0;
    }
    map.put(key, currentVal + 1);
  }
  
  /**
   * Adds another element to the Map:
   * The list of strings is enlarged 
   * @param map
   * @param key   Key as String
   * @param value additional value to be added (at end)
   */
  public static void addToListMap(Map<String,List<String>> map, String key, String value) {
    List<String> currentList;
    if(map.containsKey(key)) {
      currentList = map.get(key);
    } else {
      currentList = new ArrayList<String>();
    }
    currentList.add(value);
    map.put(key, currentList);
  }

  /**
   * Adds another element to the Map:
   * The set of strings is enlarged 
   * @param map
   * @param key   Key as String
   * @param value additional value to be added (if not yet present)
   */
  public static void addToSetMap(Map<String,Set<String>> map, String key, String value) {
    Set<String> currentSet;
    if(map.containsKey(key)) {
      currentSet = map.get(key);
    } else {
      currentSet = new LinkedHashSet<String>();
    }
    currentSet.add(value);
    map.put(key, currentSet);
  }
  
}
