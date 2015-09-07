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

package de.monticore.languages.grammar;

import de.monticore.symboltable.CommonSymbol;
import de.monticore.symboltable.types.JAttributeSymbolKind;

// TODO PN update doc
/**
 * Groups information of a rule component. For example, consider the rule <code>Foo = b:Bar</code>.
 * Here, <code>Bar</code> is a rule component with the usage name <code>b</code>.
 */
public class MCAttributeSymbol extends CommonSymbol implements Comparable<MCAttributeSymbol> {

  public static final MCAttributeKind KIND = new MCAttributeKind();

  public static final int STAR = -1;
  public static final int UNDEF = -2;

  private final MCTypeSymbol type;

  private boolean iterated = false;
  
  private int min = UNDEF;
  private int max = UNDEF;
  
  private boolean unordered = false;
  
  private String derived = "";
  
  private MCGrammarSymbol grammar;
  
  private boolean minCheckedDuringParsing = false;
  
  private boolean maxCheckedDuringParsing = false;
  
  public MCAttributeSymbol(String usageName, MCTypeSymbol type, MCGrammarSymbol grammar) {
    super(usageName, KIND);

    this.type = type;

    setGrammar(grammar);
  }

  public MCTypeSymbol getType() {
    return type;
  }

  public boolean isDerived() {
    return !derived.equals("");
  }
  
  public String getDerived() {
    return derived;
  }
  
  public void setDerived(String derived) {
    this.derived = derived;
  }
  
  public boolean isIterated() {
    
    return iterated;
  }
  
  public void setIterated(boolean iterated) {
    this.iterated = iterated;
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
  
  public void setMax(String max) {
    if (max.equals("*")) {
      setMax(STAR);
    }
    else {
      try {
        int x = Integer.parseInt(max);
        setMax(x);
      }
      catch (NumberFormatException ignored) {
      }
    }
  }
  
  public void setMin(String min) {
    try {
      int x = Integer.parseInt(min);
      setMin(x);
    }
    catch (NumberFormatException ignored) {
    }
  }
  
  public boolean isUnordered() {
    return unordered;
  }
  
  public void setUnordered(boolean unordered) {
    this.unordered = unordered;
  }
  
  public void setUnordered(String unordered) {
    this.unordered = Boolean.getBoolean(unordered);
  }
  
  public boolean isMaxCheckedDuringParsing() {
    return maxCheckedDuringParsing;
  }
  
  public void setMaxCheckedDuringParsing(boolean maxCheckedDuringParsing) {
    this.maxCheckedDuringParsing = maxCheckedDuringParsing;
  }
  
  public boolean isMinCheckedDuringParsing() {
    return minCheckedDuringParsing;
  }
  
  public void setMinCheckedDuringParsing(boolean minCheckedDuringParsing) {
    this.minCheckedDuringParsing = minCheckedDuringParsing;
  }
  
  @Override
  public int compareTo(MCAttributeSymbol o) {
    return getName().compareTo(o.getName());
  }
  
  public MCGrammarSymbol getGrammar() {
    return grammar;
  }

  public void setGrammar(MCGrammarSymbol grammar) {
    this.grammar = grammar;
  }

  public static final class MCAttributeKind extends JAttributeSymbolKind {
    private MCAttributeKind() {
    }
  }

}
