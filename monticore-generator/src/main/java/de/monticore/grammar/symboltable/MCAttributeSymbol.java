/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.grammar.symboltable;

import java.util.Optional;

import de.monticore.symboltable.CommonSymbol;
import de.monticore.symboltable.SymbolKind;
import de.se_rwth.commons.logging.Log;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 */
public class MCAttributeSymbol extends CommonSymbol {
  
  public static final MCProdAttributeKind KIND = new MCProdAttributeKind();
  
  public static final int STAR = -1;
  
  public static final int UNDEF = -2;
  
  private boolean iterated = false;
  
  private int min = UNDEF;
  
  private int max = UNDEF;
  
  private boolean unordered = false;
  
  private String derived = "";
  
  private boolean minCheckedDuringParsing = false;
  
  private boolean maxCheckedDuringParsing = false;
  
  private MCProdOrTypeReference referencedProd;

  public MCAttributeSymbol(String name) {
    super(name, KIND);
  }
  
  public boolean isDerived() {
    return !"".equals(derived);
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
    if ("*".equals(max)) {
      setMax(STAR);
    }
    else {
      try {
        int x = Integer.parseInt(max);
        setMax(x);
      }
      catch (NumberFormatException ignored) {
        Log.warn("0xA0140 Failed to parse an integer from string " + max);
      }
    }
  }
  
  public void setMin(String min) {
    try {
      int x = Integer.parseInt(min);
      setMin(x);
    }
    catch (NumberFormatException ignored) {
      Log.warn("0xA0141 Failed to parse an integer from string " + min);
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
  
  public void setReferencedProd(MCProdOrTypeReference referencedProd) {
    this.referencedProd = referencedProd;
  }
  
  /**
   * @return A reference to the defining production of this component, e.g., the
   * defining prod for the nonterminal <code>... = s:A</code> is the production
   * <code>A = ...</code>.
   */
  public Optional<MCProdOrTypeReference> getReferencedProd() {
    return Optional.ofNullable(referencedProd);
  }
  
  public static class MCProdAttributeKind implements SymbolKind {
    
    private static final String NAME = MCProdAttributeKind.class.getName();
    
    protected MCProdAttributeKind() {
    }
    
    @Override
    public String getName() {
      return NAME;
    }
    
    @Override
    public boolean isKindOf(SymbolKind kind) {
      return NAME.equals(kind.getName()) || SymbolKind.super.isKindOf(kind);
    }
    
  }
}
