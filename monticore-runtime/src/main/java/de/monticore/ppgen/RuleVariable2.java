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

import de.monticore.ppgen.formatter.PPTNode;

/**
 * Represents a variable. It is used by the @see {@link VariablesManager}.
 * 
 * @author diego
 */
//STATE SMELL? needed for pretty printer generation, maybe this should be part of generator project.
@Deprecated
public class RuleVariable2 {
  /**
   * Name of the rule where the variable is.
   */
  private String ruleName;
  /**
   * Name of the variable.
   */
  private String name;
  /**
   * Pretty print node of the variable.
   */
  private PPTNode pptNode;
  /**
   * When the variable comes from another variable, the field reference saves
   * the name of the former variable. For example, if a rule is defined as
   * RulaA[Var=PrevVar], the reference field of the variable Var will be
   * PrevVar.
   */
  private RuleVariable2 reference;
  
  public String getRuleName() {
    return ruleName;
  }
  
  public void setRuleName(String ruleName) {
    this.ruleName = ruleName;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public PPTNode getPptNode() {
    return pptNode;
  }
  
  public void setPptNode(PPTNode pptNode) {
    this.pptNode = pptNode;
  }
  
  public RuleVariable2 getReference() {
    return reference;
  }
  
  public void setReference(RuleVariable2 reference) {
    this.reference = reference;
  }
}
