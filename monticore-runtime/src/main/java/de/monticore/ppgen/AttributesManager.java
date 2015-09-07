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

package de.monticore.ppgen;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.monticore.ast.ASTNode;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;

/**
 * Manages the attributes of a of rule. It accepts an AST node as a parameter in
 * the constructor and it takes care of the number of the attributes used, their
 * types and some other things which are useful later. Thus, instead of
 * accessing the attributes of the rule directly through the node, the attribute
 * manager is used. Also it is useful when checking if an alternative is valid
 * as it locks attributes. Examples are in the checking methods of generated
 * pretty print concrete visitors.
 * 
 * @author diego
 */
// STATE SMELL? needed for pretty printer generation, maybe this should be part of generator project. 
@Deprecated
public class AttributesManager {
  private ASTNode node;
  
  /**
   * Saves the number of elements of an attributes that has been used.
   */
  private Map<String, Integer> counters;
  
  // information about the locked attributes in the rule. look at the
  // setters and getters for more information
  private Map<String, Integer> locks;
  private List<List<String>> lockLevels;
  private int currentLockLevel;
  
  // information about the parameters of the rule. look at the getters
  // and setters for more information
  private int numberOfParameters;
  private Map<String, Integer> paramsPosition;
  
  /**
   * Constructor.
   * 
   * @param node Node to be managed.
   */
  public AttributesManager(ASTNode node) {
    this.node = node;
    counters = new HashMap<String, Integer>();
    locks = new HashMap<String, Integer>();
    lockLevels = new ArrayList<List<String>>();
    paramsPosition = new HashMap<String, Integer>();
    currentLockLevel = -1;
  }
  
  /**
   * Returns the number of parameters the rule was called with. For example, if
   * the non terminal is RuleA->[M, N], the number of parameters of the
   * attribute manager of RuleA will be 2.
   * 
   * @return Number of parameters.
   */
  public int getNumberOfParameters() {
    return numberOfParameters;
  }
  
  /**
   * Sets the number of parameters the rule was called with. For example, if the
   * non terminal is RuleA->[M, N], the number of parameters of the attribute
   * manager of RuleA will be 2.
   * 
   * @param numberOfParameters Number of parameters.
   */
  public void setNumberOfParameters(int numberOfParameters) {
    this.numberOfParameters = numberOfParameters;
  }
  
  /**
   * Returns a hash map with the position of the parameters the rule was called
   * with. For example, if the non terminal is RuleA->[M, N], the hash map will
   * have two pairs, <"M", 0> y <"N", 1>.
   * 
   * @return Hash map with the position of the parameters.
   */
  public Map<String, Integer> getParamsPosition() {
    return paramsPosition;
  }
  
  /**
   * Returns the managed node.
   * 
   * @return Managed node.
   */
  public ASTNode getNode() {
    return node;
  }
  
  /**
   * Adds a new chicking level. It should be called before an alternative is
   * checked. It sets the new level for it and, in case the alternative is no
   * possible, the attributes locked by this alternative can be unlock, letting
   * other alternatives to use those attributes.
   * 
   * @return The new locking level.
   */
  public int startLock() {
    List<String> attributesLocked = new ArrayList<String>();
    
    lockLevels.add(attributesLocked);
    currentLockLevel++;
    
    return currentLockLevel;
  }
  
  /**
   * Mark one element of an attribute as locked. If the attribute is an array,
   * only one attribute will be locked.
   * 
   * @param name Name of the attribute to lock.
   * @return true if the attribute could be locked. false if the attribute was
   *         already locked or used, or if the list has no more available
   *         elements to lock.
   */
  public boolean lockAttribute(String name) {
    if (currentLockLevel < 0)
      throw new RuntimeException("0xA4093 starLock() has not been called yet!");
    
    int currentLocks = 0;
    
    if (locks.get(name) != null) {
      currentLocks = locks.get(name);
    }
    
    // checks if there is another element available
    if (hasNext(name, currentLocks + 1)) {
      currentLocks++;
      locks.put(name, currentLocks);
      lockLevels.get(currentLockLevel).add(name);
    }
    else {
      return false;
    }
    
    return true;
  }
  
  /**
   * Returns the last element locked.
   * 
   * @param name Name of the last locked element.
   * @return Last locked element.
   */
  public Object lastLocked(String name) {
    if (currentLockLevel < 0)
      throw new RuntimeException("0xA4094 starLock() has not been called yet!");
    
    int currentLocks = 0;
    
    if (locks.get(name) != null) {
      currentLocks = locks.get(name);
    }
    
    return getValue(name, currentLocks);
  }
  
  /**
   * Unlocks all the elements in a specific level and it upper levels.
   * 
   * @param level Level to start unlocking the elements.
   */
  public void unlock(int level) {
    while (currentLockLevel >= level) {
      for (String name : lockLevels.get(currentLockLevel)) {
        Integer l = locks.get(name);
        
        if (l != null) {
          l--;
          locks.put(name, l);
        }
      }
      
      lockLevels.remove(currentLockLevel);
      currentLockLevel--;
    }
  }
  
  /**
   * Returns an attribute or element of an attribute in case it is a list. The
   * returned element will be marked as used, so it can't be used anymore.
   * 
   * @param name Name of the attribute.
   * @return Attribute.
   */
  public Object getNext(String name) {
    return getNextValue(name, false);
  }
  
  /**
   * Returns an attribute or element of an attribute in case it is a list. In
   * contrast with getNext(), it doesn't mark the attribute as used.
   * 
   * @param name Name of the attribute.
   * @return Attribute.
   */
  public Object lookNext(String name) {
    return getNextValue(name, true);
  }
  
  /**
   * Checks if the attribute is available. If the attribute is a list, it checks
   * if the list has another unused element in it.
   * 
   * @param name Name of the attribute.
   * @return true if the attribute is available. false if not.
   */
  public boolean hasNext(String name) {
    return checkValues(name, 1);
  }
  
  /**
   * Checks if a number of elements of the attributes are available.
   * 
   * @param name Name of the attribute.
   * @param amount Number of attributes that have to be available.
   * @return true if the attribute is available. false if not.
   */
  public boolean hasNext(String name, int amount) {
    return checkValues(name, amount);
  }
  
  /**
   * Returns the attribute with the specified name. It is the raw attribute in
   * the node.
   * 
   * @param name Name of the attribute.
   * @return Object that represent the attribute.
   */
  private Object getAttribute(String name) {
    // we try to get the attribute with the get method
    String methodName = "get" + StringTransformations.capitalize(name);
    Method m;
    
    try {
      m = node.getClass().getMethod(methodName, (Class<?>[]) null);
    }
    catch (SecurityException e) {
      e.printStackTrace();
      return null;
    }
    catch (NoSuchMethodException e) {
      // if the get method didn't work, we try with the is mehotd
      methodName = "is" + StringTransformations.capitalize(name);
      
      try {
        m = node.getClass().getMethod(methodName, (Class<?>[]) null);
      }
      catch (SecurityException e1) {
        Log.error("0xA0810 Security violation while getting methods of the class " +  node.getClass() + " : " + e.getMessage());
        return null;
      }
      catch (NoSuchMethodException e1) {
        Log.error("0xA0850 There is no such method " + methodName + " : " + e.getMessage());
        return null;
      }
    }
    
    // get the object by reflection
    Object res = null;
    
    try {
      res = m.invoke(node, (Object[]) null);
    }
    catch (IllegalArgumentException e) {
      Log.error("0xA0819 Illegal arguments while calling the method " + m.getName() + " : " + e.getMessage());
    }
    catch (IllegalAccessException e) {
      Log.error("0xA0846 Inaccessible method " + m.getName() + " : " + e.getMessage());
    }
    catch (InvocationTargetException e) {
      Log.error("0xA0840 Error while calling the method " + m.getName() + " : " + e.getMessage());
    }
    
    return res;
  }
  
  /**
   * Checks if an attribute has a specified number of elements available.
   * 
   * @param name Name of the attribute.
   * @param amount Number of elements to check.
   * @return true if the elements are available. false if not.
   */
  private boolean checkValues(String name, int amount) {
    Object res = getAttribute(name);
    
    Integer counter = counters.get(name);
    
    if (counter == null) {
      counter = 0;
    }
    else {
      counter = new Integer(counter);
      counter++;
    }
    
    if (res instanceof List) {
      if (((List<?>) res).size() > (counter + amount - 1)) {
        return true;
      }
      else {
        return false;
      }
    }
    else if (res == null) {
      return false;
    }
    else {
      if (counter > 0)
        return false;
      else
        return true;
    }
  }
  
  /**
   * Returns the next available element of an attribute.
   * 
   * @param name Name ofthe attribute.
   * @param justCheck true if the element must be marked as used. false if not.
   * @return Next available element of the attribute.
   */
  private Object getNextValue(String name, boolean justCheck) {
    Object res = getAttribute(name);
    
    Integer counter = counters.get(name);
    
    if (counter == null) {
      counter = 0;
    }
    else {
      counter = new Integer(counter);
      counter++;
    }
    
    if (!justCheck)
      counters.put(name, counter);
    
    if (res instanceof List) {
      if (((List<?>) res).size() > (counter)) {
        res = ((List<?>) res).get(counter);
      }
      else {
        res = null;
      }
    }
    
    return res;
  }
  
  /**
   * Returns an element of an attribute in a specific position.
   * 
   * @param name Name of the attribute.
   * @param position Position of the attribute. If the attribute is a list, it
   *          is the index + 1, if not, it will be ignored.
   * @return Element in the specified position.
   */
  private Object getValue(String name, int position) {
    Object res = getAttribute(name);
    
    if (res instanceof List) {
      Integer counter = counters.get(name);
      
      if (counter == null) {
        position = position - 1;
      }
      else {
        position = counter + position;
      }
      
      if (((List<?>) res).size() > (position)) {
        res = ((List<?>) res).get(position);
      }
      else {
        res = null;
      }
    }
    
    return res;
  }
  
  /**
   * Returns the position of the first attribute in the source code.
   * 
   * @param names Names of the attributes.
   * @return First position. It can be null if position of all attributes is
   *         null.
   */
  public SourcePosition getPositionList(String... names) {
    SourcePosition firstPos = null;
    
    for (String name : names) {
      Object res = lookNext(name);
      
      if (res instanceof ASTNode) {
        firstPos = firstPosition(((ASTNode) res).get_SourcePositionStart(), firstPos);
      }
    }
    
    return firstPos;
  }
  
  private SourcePosition firstPosition(SourcePosition p1, SourcePosition p2) {
    if (p1 == null && p2 == null)
      return null;
    if (p1 == null)
      return p2;
    if (p2 == null)
      return p1;
    
    if (p1.compareTo(p2) < 0)
      return p1;
    else
      return p2;
  }
}
