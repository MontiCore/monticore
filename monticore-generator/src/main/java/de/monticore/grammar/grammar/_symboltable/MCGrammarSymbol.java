/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.grammar._symboltable;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsArtifactScope;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsGlobalScope;
import de.se_rwth.commons.Names;

import java.util.*;

import static com.google.common.collect.ImmutableList.copyOf;
import static de.se_rwth.commons.logging.Log.errorIfNull;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

public class MCGrammarSymbol extends MCGrammarSymbolTOP {

  private final List<MCGrammarSymbolLoader> superGrammars = new ArrayList<>();

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

  public List<MCGrammarSymbolLoader> getSuperGrammars() {
    return copyOf(superGrammars);
  }

  public List<MCGrammarSymbol> getSuperGrammarSymbols() {
    return copyOf(superGrammars.stream().filter(g -> g.isSymbolLoaded())
            .map(g -> g.getLoadedSymbol())
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

  public void addSuperGrammar(MCGrammarSymbolLoader superGrammarRef) {
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
    Iterator<MCGrammarSymbolLoader> itSuperGrammars = superGrammars.iterator();

    while (!mcProd.isPresent() && itSuperGrammars.hasNext()) {
      mcProd = itSuperGrammars.next().getLoadedSymbol().getProdWithInherited(ruleName);
    }

    return mcProd;
  }

  public Optional<ProdSymbol> getInheritedProd(String ruleName) {
    Optional<ProdSymbol> mcProd = empty();
    Iterator<MCGrammarSymbolLoader> itSuperGrammars = superGrammars.iterator();

    while (!mcProd.isPresent() && itSuperGrammars.hasNext()) {
      mcProd = itSuperGrammars.next().getLoadedSymbol().getProdWithInherited(ruleName);
    }

    return mcProd;
  }

  public Map<String, ProdSymbol> getProdsWithInherited() {
    final Map<String, ProdSymbol> ret = new LinkedHashMap<>();

    for (int i = superGrammars.size() - 1; i >= 0; i--) {
      final MCGrammarSymbolLoader superGrammarRef = superGrammars.get(i);

      if (superGrammarRef.isSymbolLoaded()) {
        ret.putAll(superGrammarRef.getLoadedSymbol().getProdsWithInherited());
      }
    }

    for (final ProdSymbol prodSymbol : getProds()) {
      ret.put(prodSymbol.getName(), prodSymbol);
    }

    return ret;
  }

  public List<String> getTokenRulesWithInherited() {
    final List<String> ret = Lists.newArrayList();

    for (int i = superGrammars.size() - 1; i >= 0; i--) {
      final MCGrammarSymbolLoader superGrammarRef = superGrammars.get(i);

      if (superGrammarRef.isSymbolLoaded()) {
        ret.addAll(superGrammarRef.getLoadedSymbol().getTokenRulesWithInherited());
      }
    }
    forEachSplitRules(t -> ret.add(t));
    return ret;
  }

  public List<String> getKeywordRulesWithInherited() {
    final List<String> ret = Lists.newArrayList();

    for (int i = superGrammars.size() - 1; i >= 0; i--) {
      final MCGrammarSymbolLoader superGrammarRef = superGrammars.get(i);

      if (superGrammarRef.isSymbolLoaded()) {
        ret.addAll(superGrammarRef.getLoadedSymbol().getKeywordRulesWithInherited());
      }
    }
    forEachNoKeywords(t -> ret.add(t));
    return ret;
  }

  public Optional<ASTMCGrammar> getAstGrammar() {
    return this.astNode;
  }

  /**
   * Determines <b>dynamically</b> the full name of the symbol.
   *
   * @return the full name of the symbol determined dynamically
   */
  protected String determineFullName() {
    if (enclosingScope == null) {
      // There should not be a symbol that is not defined in any scope. This case should only
      // occur while the symbol is built (by the symbol table creator). So, here the full name
      // should not be cached yet.
      return name;
    }

    final Deque<String> nameParts = new ArrayDeque<>();
    nameParts.addFirst(name);

    Optional<? extends IGrammarScope> optCurrentScope = Optional.of(enclosingScope);

    while (optCurrentScope.isPresent()) {
      final IGrammarScope currentScope = optCurrentScope.get();
      if (currentScope.isPresentSpanningSymbol()) {
        // If one of the enclosing scope(s) is spanned by a symbol, the full name
        // of that symbol is the missing prefix, and hence, the calculation
        // ends here. This check is important, since the full name of the enclosing
        // symbol might be set manually.
        nameParts.addFirst(currentScope.getSpanningSymbol().getFullName());
        break;
      }

      if (!(currentScope instanceof Grammar_WithConceptsGlobalScope)) {
        if (currentScope instanceof Grammar_WithConceptsArtifactScope) {
          // We have reached the artifact scope. Get the package name from the
          // symbol itself, since it might be set manually.
          if (!getPackageName().isEmpty()) {
            nameParts.addFirst(getPackageName());
          }
        } else {
          if (currentScope.isPresentName()) {
            nameParts.addFirst(currentScope.getName());
          }
          // ...else stop? If one of the enclosing scopes is unnamed,
          //         the full name is same as the simple name.
        }
        optCurrentScope = Optional.ofNullable(currentScope.getEnclosingScope());
      } else {
        optCurrentScope = Optional.empty();
      }
    }

    return Names.getQualifiedName(nameParts);
  }

  protected String determinePackageName() {
    Optional<? extends IGrammarScope> optCurrentScope = Optional.ofNullable(enclosingScope);

    while (optCurrentScope.isPresent()) {
      final IGrammarScope currentScope = optCurrentScope.get();
      if (currentScope.isPresentSpanningSymbol()) {
        // If one of the enclosing scope(s) is spanned by a symbol, take its
        // package name. This check is important, since the package name of the
        // enclosing symbol might be set manually.
        return currentScope.getSpanningSymbol().getPackageName();
      } else if (currentScope instanceof Grammar_WithConceptsArtifactScope) {
        return ((Grammar_WithConceptsArtifactScope) currentScope).getPackageName();
      }

      optCurrentScope = Optional.of(currentScope.getEnclosingScope());
    }

    return "";
  }
}
