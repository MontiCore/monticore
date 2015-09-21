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

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Represents information about an attribute (resp. field) for a type. For example, the production
 * <code>A = (b:B)?</code> defines, besides a rule, also a type named <code>A</code>.
 * The type has the attribute <code>b</code> which is optional.
 *
 * @author krahn, Mir Seyed Nazari
 *
 */
public class MCAttributeInfo {
  
  public static final int STAR = -1;
  
  private String name;
  
  private int min = 0;
  private int max = 0;

  private boolean referencesConstantTerminal = false;
  
  private Set<String> referencedRule = new LinkedHashSet<>();
  
  private Set<String> constantValues = new LinkedHashSet<>();
  
  public Set<String> getConstantValues() {
    return constantValues;
  }
  
  public void addConstantValue(String value) {
    this.constantValues.add(value);
  }
  
  public int getMax() {
    return max;
  }
  
  public void setMax(int max) {
    this.max = max;
  }
  
  public int getMin() {
    return min;
  }
  
  public void setMin(int min) {
    this.min = min;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public Set<String> getReferencedRule() {
    return referencedRule;
  }
  
  public void addReferencedRule(String referencedRule) {
    this.referencedRule.add(referencedRule);
  }

  public boolean isReferencesConstantTerminal() {
    return referencesConstantTerminal;
  }

  public void setReferencesConstantTerminal(boolean terminal) {
    this.referencesConstantTerminal = terminal;
  }
  
  public MCAttributeInfo copyWithNoExistent() {
    MCAttributeInfo att = new MCAttributeInfo();
    att.setName(name);
    att.max = max;
    att.min = 0;
    
    att.referencedRule = new LinkedHashSet<>(referencedRule);
    
    att.constantValues = new LinkedHashSet<>(constantValues);
    att.setReferencesConstantTerminal(isReferencesConstantTerminal());
    
    return att;
  }
  
  public MCAttributeInfo sequence(MCAttributeInfo value) {
    MCAttributeInfo att = new MCAttributeInfo();
    att.setName(name);
    att.max = plus(max, value.getMax());
    att.min = plus(min, value.getMin());
    
    att.referencedRule = new LinkedHashSet<>(referencedRule);
    att.referencedRule.addAll(value.referencedRule);
    
    att.constantValues = new LinkedHashSet<>(constantValues);
    att.constantValues.addAll(value.constantValues);
    att.setReferencesConstantTerminal(value.isReferencesConstantTerminal() || att.isReferencesConstantTerminal());
    
    return att;
  }
  
  public MCAttributeInfo alternate(MCAttributeInfo value) {
    MCAttributeInfo att = new MCAttributeInfo();
    att.setName(name);
    att.max = max(max, value.getMax());
    att.min = min(min, value.getMin());
    
    att.referencedRule = new LinkedHashSet<>(referencedRule);
    att.referencedRule.addAll(value.referencedRule);
    
    att.constantValues = new LinkedHashSet<>(constantValues);
    att.constantValues.addAll(value.constantValues);
    att.setReferencesConstantTerminal(value.isReferencesConstantTerminal() || att.isReferencesConstantTerminal());
    
    return att;
  }
  
  public MCAttributeInfo iterateStar() {
    MCAttributeInfo att = new MCAttributeInfo();
    att.setName(name);
    att.max = STAR;
    att.min = 0;
    
    att.referencedRule = new LinkedHashSet<>(referencedRule);
    att.constantValues = new LinkedHashSet<>(constantValues);
    att.setReferencesConstantTerminal(this.isReferencesConstantTerminal());
    
    return att;
  }
  
  public MCAttributeInfo iteratePlus() {
    
    MCAttributeInfo att = new MCAttributeInfo();
    att.setName(name);
    att.max = STAR;
    att.min = min;
    
    att.referencedRule = new LinkedHashSet<>(referencedRule);
    att.constantValues = new LinkedHashSet<>(constantValues);
    att.setReferencesConstantTerminal(this.isReferencesConstantTerminal());
    
    return att;
  }
  
  public MCAttributeInfo iterateOptional() {
    
    MCAttributeInfo att = new MCAttributeInfo();
    att.setName(name);
    att.max = max;
    att.min = 0;
    
    att.referencedRule = new LinkedHashSet<>(referencedRule);
    att.constantValues = new LinkedHashSet<>(constantValues);
    att.setReferencesConstantTerminal(this.isReferencesConstantTerminal());
    
    return att;
  }
  
  private int plus(int m1, int m2) {
    if (m1 == STAR || m2 == STAR) {
      return STAR;
    }
    else {
      return m1 + m2;
    }
    
  }
  
  private int min(int m1, int m2) {
    if (m1 == STAR) {
      return m2;
    }
    if (m2 == STAR) {
      return m1;
    }
    return Math.min(m1, m2);
    
  }
  
  private int max(int m1, int m2) {
    if (m1 == STAR) {
      return STAR;
    }
    if (m2 == STAR) {
      return STAR;
    }
    return Math.max(m1, m2);
  }
  
  @Override
  public String toString() {
    return name + " min=" + min + " max=" + max + " references: " + referencedRule + " constants "
        + "" + constantValues + (isReferencesConstantTerminal() ? " references constant" : "");
  }
  
}
