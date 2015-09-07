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

/**
 * Represents a non terminal which comes from a lexical rule in the pretty
 * printing tree.
 * 
 * @author diego
 */
// STATE SMELL? needed for pretty printer generation, maybe this should be part of generator project. 
@Deprecated
public class PPTLexRule extends PPTCNode {
  protected String type;
  protected String name;
  
  /**
   * Constructor.
   */
  public PPTLexRule() {
    type = "";
    name = "";
  }
  
  /**
   * Returns the name of the lexical rule.
   * 
   * @return Name of the lexical rule.
   */
  public String getType() {
    return type;
  }
  
  /**
   * Sets type, which is the name of the lexical rule.
   * 
   * @return Name of the lexical rule.
   */
  public void setType(String type) {
    this.type = type;
  }
  
  /**
   * Returns the attribute name in the grammar rule.
   * 
   * @return Attribute name in the grammar rule.
   */
  public String getName() {
    return name;
  }
  
  /**
   * Sets the attribute name in the grammar rule.
   * 
   * @param name Attribute name in the grammar rule.
   */
  public void setName(String name) {
    this.name = name;
  }
}
