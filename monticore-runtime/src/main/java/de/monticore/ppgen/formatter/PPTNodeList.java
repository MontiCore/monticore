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

package de.monticore.ppgen.formatter;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a node in the pretty printing tree which can have children nodes.
 * This is the case of rule and block nodes.
 * 
 * @author diego
 */
//STATE SMELL? needed for pretty printer generation, maybe this should be part of generator project.
@Deprecated
public class PPTNodeList extends PPTCNode {
  protected List<PPTNode> children;
  
  /**
   * Constructor.
   */
  public PPTNodeList() {
    children = new ArrayList<PPTNode>();
  }
  
  /**
   * Returns a list of children.
   * 
   * @return List of children.
   */
  public List<PPTNode> getChildren() {
    return children;
  }
  
  /**
   * Sets a list of children.
   * 
   * @param children List of children.
   */
  public void setChildren(List<PPTNode> children) {
    this.children = children;
  }
  
  /**
   * Returns a children by its name. This is only applicable for children with
   * names, like PPTTerminal, PPTRule or PPTConstantGroup.
   * 
   * @param name Name of the attribute in the rule.
   * @return Children with the specified name.
   */
  public PPTNode getChildByName(String name) {
    for (PPTNode n : children) {
      if (n instanceof PPTTerminal) {
        if (name.equals(((PPTTerminal) n).getName()))
          return n;
      }
      else if (n instanceof PPTRule) {
        if (name.equals(((PPTRule) n).getName()))
          return n;
      }
      else if (n instanceof PPTConstantGroup) {
        if (name.equals(((PPTConstantGroup) n).getName()))
          return n;
      }
    }
    
    return null;
  }
}
