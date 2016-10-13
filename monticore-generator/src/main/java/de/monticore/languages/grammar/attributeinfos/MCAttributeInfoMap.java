/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.monticore.languages.grammar.attributeinfos;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

/**
 * A map for {@link MCAttributeInfo}s.
 *
 * @author krahn, Mir Seyed Nazari
 */
public class MCAttributeInfoMap {

  /**
   * e.g., A = attr:B
   */
  private HashMap<String, MCAttributeInfo> attributes = new LinkedHashMap<>();

  /**
   * e.g., A = variable=B
   */
//  private HashMap<String, MCAttributeInfo> variables = new LinkedHashMap<>();

  public static MCAttributeInfoMap getEmptyMap() {
    return new MCAttributeInfoMap();
  }

  /**
   * Adds an attribute to the map. Use this method in cases like <code>A = attr:B</code> (colon
   * between <code>attr</code> and <code>B</code>).
   */
  public MCAttributeInfo putAttribute(MCAttributeInfo attr) {
    return attributes.put(attr.getName(), attr);
  }

  /**
   * Adds an attribute to the map. Use this method in cases like <code>A = attr:B</code>
   * (equality sign between <code>attr</code> and <code>B</code>).
   */
//  public MCAttributeInfo putVariable(MCAttributeInfo attr) {
//    return variables.put(attr.getName(), attr);
//  }

  public MCAttributeInfoMap sequence(MCAttributeInfoMap otherMap) {
    MCAttributeInfoMap resultingMap = new MCAttributeInfoMap();
    
    resultingMap.attributes.putAll(attributes);
    
    for (Entry<String, MCAttributeInfo> e : otherMap.attributes.entrySet()) {
      if (resultingMap.attributes.containsKey(e.getKey())) {
        MCAttributeInfo att = resultingMap.attributes.get(e.getKey()).sequence(e.getValue());
        resultingMap.putAttribute(att);
      }
      else {
        resultingMap.attributes.put(e.getKey(), e.getValue());
      }
      
    }
    
//    resultingMap.variables.putAll(variables);
//    for (Entry<String, MCAttributeInfo> e : otherMap.variables.entrySet()) {
//      
//      if (resultingMap.variables.containsKey(e.getKey())) {
//        MCAttributeInfo var = resultingMap.variables.get(e.getKey()).sequence(e.getValue());
//        resultingMap.putVariable(var);
//      }
//      else {
//        resultingMap.variables.put(e.getKey(), e.getValue());
//      }
//      
 //   }
        
    return resultingMap;
    
  }
  
  public MCAttributeInfoMap alternate(MCAttributeInfoMap otherMap) {
    if (otherMap == null) {
      return this;
    }
    
    MCAttributeInfoMap resultingMap = new MCAttributeInfoMap();
    
    for (Entry<String, MCAttributeInfo> e : otherMap.attributes.entrySet()) {
      
      // In both maps
      if (attributes.containsKey(e.getKey())) {
        MCAttributeInfo att = attributes.get(e.getKey()).alternate(
            e.getValue());
        resultingMap.putAttribute(att);
      }
      // Only in other map
      else {
        MCAttributeInfo att = e.getValue().copyWithNoExistent();
        resultingMap.attributes.put(e.getKey(), att);
      }
      
    }
    
    for (Entry<String, MCAttributeInfo> e : attributes.entrySet()) {
      
      // In both maps
      if (otherMap.attributes.containsKey(e.getKey())) {
        // already done
      }
      // Only in this map
      else {
        
        MCAttributeInfo att = e.getValue().copyWithNoExistent();
        resultingMap.attributes.put(e.getKey(), att);
      }
      
    }
    
    return resultingMap;
  }
  
  public MCAttributeInfoMap iterateStar() {
    MCAttributeInfoMap m = new MCAttributeInfoMap();
    
    for (Entry<String, MCAttributeInfo> e : attributes.entrySet()) {
      m.putAttribute(e.getValue().iterateStar());
    }
    
  //  m.variables.putAll(variables);
    
    return m;
    
  }
  
  public MCAttributeInfoMap iteratePlus() {
    MCAttributeInfoMap m = new MCAttributeInfoMap();
    
    for (Entry<String, MCAttributeInfo> e : attributes.entrySet()) {
      
      m.putAttribute(e.getValue().iteratePlus());
    }
    
//    m.variables.putAll(variables);
    
    return m;
  }
  
  public MCAttributeInfoMap iterateOptional() {
    MCAttributeInfoMap m = new MCAttributeInfoMap();
    
    for (Entry<String, MCAttributeInfo> e : attributes.entrySet()) {
      
      m.putAttribute(e.getValue().iterateOptional());
    }
    
 //  m.variables.putAll(variables);
    
    return m;
  }
  
  public Set<Entry<String, MCAttributeInfo>> entrySetAttributes() {
    return attributes.entrySet();
  }
  
  @Override
  public String toString() {
    StringBuilder ret = new StringBuilder();
    
    for (Entry<String, MCAttributeInfo> e : attributes.entrySet()) {
      ret.append(e.getValue()).append("\n");
    }
    
//    for (Entry<String, MCAttributeInfo> e : variables.entrySet()) {
//      ret.append(e.getValue()).append("\n");
//    }
    
    return ret.toString();
  }

}
