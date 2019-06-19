/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.grammar._symboltable;

import com.google.common.collect.ImmutableSet;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.symboltable.SymbolKind;

import java.util.*;

import static com.google.common.collect.ImmutableList.copyOf;
import static de.se_rwth.commons.logging.Log.errorIfNull;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

public class MCGrammarSymbol extends MCGrammarSymbolTOP {

  private final List<MCGrammarSymbolReference> superGrammars = new ArrayList<>();

  /**
   * Is the grammar abstract?
   */
  private boolean isComponent = false;

  // the start production of the grammar
  private ProdSymbol startProd;

  public MCGrammarSymbol(String name) {
    super(name);
  }

  public void setStartProd(ProdSymbol startRule) {
    this.startProd = startRule;
  }

  /**
   * The start production typically is the first defined production in the
   * grammar.
   *
   * @return the start production of the grammar, if not a component grammar
   */
  public Optional<ProdSymbol> getStartProd() {
    return ofNullable(startProd);
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
    return copyOf(superGrammars);
  }

  public List<MCGrammarSymbol> getSuperGrammarSymbols() {
    return copyOf(superGrammars.stream().filter(g -> g.getReferencedSymbol() != null)
            .map(g -> g.getReferencedSymbol())
            .collect(toList()));
  }

  public List<MCGrammarSymbol> getAllSuperGrammars() {
    List<MCGrammarSymbol> supGrammars = new ArrayList<>(this.getSuperGrammarSymbols());
    List<MCGrammarSymbol> superSuperGrammars = new ArrayList<>();
    for (MCGrammarSymbol superGrammar : supGrammars) {
      superGrammar.getAllSuperGrammars().stream().filter(s -> !superSuperGrammars.contains(s)).forEach(s -> superSuperGrammars.add(s));
    }
    superSuperGrammars.stream().filter(s -> !supGrammars.contains(s)).forEach(s->supGrammars.add(s));
    return copyOf(supGrammars);
  }

  public void addSuperGrammar(MCGrammarSymbolReference superGrammarRef) {
    this.superGrammars.add(errorIfNull(superGrammarRef));
  }

  public Collection<ProdSymbol> getProds() {
    return this.getSpannedScope().getLocalProdSymbols();
  }

  public Collection<String> getProdNames() {
    final Set<String> prodNames = new LinkedHashSet<>();

    for (final ProdSymbol prodSymbol : getProds()) {
      prodNames.add(prodSymbol.getName());
    }

    return ImmutableSet.copyOf(prodNames);
  }

  public Optional<ProdSymbol> getProd(String prodName) {
    return this.getSpannedScope().resolveProdLocally(prodName);
  }

  public Optional<ProdSymbol> getProdWithInherited(String ruleName) {
    Optional<ProdSymbol> mcProd = getProd(ruleName);
    Iterator<MCGrammarSymbolReference> itSuperGrammars = superGrammars.iterator();

    while (!mcProd.isPresent() && itSuperGrammars.hasNext()) {
      mcProd = itSuperGrammars.next().getReferencedSymbol().getProdWithInherited(ruleName);
    }

    return mcProd;
  }

  public Optional<ProdSymbol> getInheritedProd(String ruleName) {
    Optional<ProdSymbol> mcProd = empty();
    Iterator<MCGrammarSymbolReference> itSuperGrammars = superGrammars.iterator();

    while (!mcProd.isPresent() && itSuperGrammars.hasNext()) {
      mcProd = itSuperGrammars.next().getReferencedSymbol().getProdWithInherited(ruleName);
    }

    return mcProd;
  }

  public Map<String, ProdSymbol> getProdsWithInherited() {
    final Map<String, ProdSymbol> ret = new LinkedHashMap<>();

    for (int i = superGrammars.size() - 1; i >= 0; i--) {
      final MCGrammarSymbolReference superGrammarRef = superGrammars.get(i);

      if (superGrammarRef.existsReferencedSymbol()) {
        ret.putAll(superGrammarRef.getReferencedSymbol().getProdsWithInherited());
      }
    }

    for (final ProdSymbol prodSymbol : getProds()) {
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
