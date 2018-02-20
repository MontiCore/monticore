/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.symboltable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.symboltable.CommonScopeSpanningSymbol;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.SymbolKind;
import de.se_rwth.commons.logging.Log;

/**
 * @author Pedram Mir Seyed Nazari
 */
public class MCGrammarSymbol extends CommonScopeSpanningSymbol {
  
  public static final EssentialMCGrammarKind KIND = new EssentialMCGrammarKind();
  
  private final List<MCGrammarSymbolReference> superGrammars = new ArrayList<>();
  
  /**
   * Is the grammar abstract?
   */
  private boolean isComponent = false;
  
  // the start production of the grammar
  private MCProdSymbol startProd;
  
  public MCGrammarSymbol(String name) {
    super(name, KIND);
  }
  
  @Override
  protected MutableScope createSpannedScope() {
    return new MCGrammarScope(Optional.empty());
  }
  
  public void setStartProd(MCProdSymbol startRule) {
    this.startProd = startRule;
  }
  
  /**
   * The start production typically is the first defined production in the
   * grammar.
   *
   * @return the start production of the grammar, if not a component grammar
   */
  public Optional<MCProdSymbol> getStartProd() {
    return Optional.ofNullable(startProd);
  }
  
  /**
   * @return true, if the grammar is abstract
   */
  public boolean isComponent() {
    return isComponent;
  }
  
  public void setComponent(boolean isComponent) {
    this.isComponent = isComponent;
  }
  
  public List<MCGrammarSymbolReference> getSuperGrammars() {
    return ImmutableList.copyOf(superGrammars);
  }
  
  public List<MCGrammarSymbol> getSuperGrammarSymbols() {
    return ImmutableList.copyOf(superGrammars.stream().filter(g -> g.getReferencedSymbol() != null)
        .map(g -> g.getReferencedSymbol())
        .collect(Collectors.toList()));
  }
  
  public void addSuperGrammar(MCGrammarSymbolReference superGrammarRef) {
    this.superGrammars.add(Log.errorIfNull(superGrammarRef));
  }
  
  public Collection<MCProdSymbol> getProds() {
    return this.getSpannedScope().resolveLocally(MCProdSymbol.KIND);
  }
  
  public Collection<String> getProdNames() {
    final Set<String> prodNames = new LinkedHashSet<>();
    
    for (final MCProdSymbol prodSymbol : getProds()) {
      prodNames.add(prodSymbol.getName());
    }
    
    return ImmutableSet.copyOf(prodNames);
  }
  
  public Optional<MCProdSymbol> getProd(String prodName) {
    return this.getSpannedScope().resolveLocally(prodName, MCProdSymbol.KIND);
  }
  
  public Optional<MCProdSymbol> getProdWithInherited(String ruleName) {
    Optional<MCProdSymbol> mcProd = getProd(ruleName);
    Iterator<MCGrammarSymbolReference> itSuperGrammars = superGrammars.iterator();
    
    while (!mcProd.isPresent() && itSuperGrammars.hasNext()) {
      mcProd = itSuperGrammars.next().getReferencedSymbol().getProdWithInherited(ruleName);
    }
    
    return mcProd;
  }
  
  public Optional<MCProdSymbol> getInheritedProd(String ruleName) {
    Optional<MCProdSymbol> mcProd = Optional.empty();
    Iterator<MCGrammarSymbolReference> itSuperGrammars = superGrammars.iterator();
    
    while (!mcProd.isPresent() && itSuperGrammars.hasNext()) {
      mcProd = itSuperGrammars.next().getReferencedSymbol().getProdWithInherited(ruleName);
    }
    
    return mcProd;
  }
  
  public Map<String, MCProdSymbol> getProdsWithInherited() {
    final Map<String, MCProdSymbol> ret = new LinkedHashMap<>();
    
    for (int i = superGrammars.size() - 1; i >= 0; i--) {
      final MCGrammarSymbolReference superGrammarRef = superGrammars.get(i);
      
      if (superGrammarRef.existsReferencedSymbol()) {
        ret.putAll(superGrammarRef.getReferencedSymbol().getProdsWithInherited());
      }
    }
    
    for (final MCProdSymbol prodSymbol : getProds()) {
      ret.put(prodSymbol.getName(), prodSymbol);
    }
    
    return ret;
  }
  
  public Optional<ASTMCGrammar> getAstGrammar() {
    return getAstNode().filter(ASTMCGrammar.class::isInstance).map(ASTMCGrammar.class::cast);
  }
  
  public static class EssentialMCGrammarKind implements SymbolKind {
    
    private static final String NAME = EssentialMCGrammarKind.class.getName();
    
    protected EssentialMCGrammarKind() {
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
