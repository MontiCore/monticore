/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.grammar._symboltable;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import de.monticore.grammar.MCGrammarSymbolTableHelper;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.se_rwth.commons.Names;

import java.util.*;

import static com.google.common.collect.ImmutableList.copyOf;
import static de.se_rwth.commons.logging.Log.errorIfNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

public class MCGrammarSymbol extends MCGrammarSymbolTOP {

  public static final String DEFAULT_MODE = "DEFAULT_MODE";

  protected final List<MCGrammarSymbolSurrogate> superGrammars = new ArrayList<>();

  protected Map<String, Collection<String>> tokenModes = Maps.newHashMap();

  protected final LoadingCache<String, Optional<ProdSymbol>> prodCache = CacheBuilder.newBuilder()
          .maximumSize(10000)
          .build(new CacheLoader<String, Optional<ProdSymbol>>() {
                   @Override
                   public Optional<ProdSymbol> load(String key) {
                     Optional<ProdSymbol> mcProd = getProd(key);
                     if (mcProd.isPresent()) {
                       return mcProd;
                     }
                     return getInheritedProd(key);
                   }
                 });

  // the start production of the grammar
  protected ProdSymbol startProd;

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

  public List<MCGrammarSymbolSurrogate> getSuperGrammars() {
    return copyOf(superGrammars);
  }

  public List<MCGrammarSymbol> getSuperGrammarSymbols() {
    return copyOf(superGrammars.stream()
            .map(g -> g.lazyLoadDelegate())
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

  public void addSuperGrammar(MCGrammarSymbolSurrogate superGrammarRef) {
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

  // return local prod or prod from supergrammars
  public Optional<ProdSymbol> getProdWithInherited(String ruleName) {
   return prodCache.getUnchecked(ruleName);
  }

  // return only prod from supergrammars
  public Optional<ProdSymbol> getInheritedProd(String ruleName) {
    final Map<String, ProdSymbol> map = new LinkedHashMap<>();

    for (int i = superGrammars.size() - 1; i >= 0; i--) {
      final MCGrammarSymbol superGrammar = superGrammars.get(i).lazyLoadDelegate();
      Optional<ProdSymbol> inheritedProd = superGrammar.getProdWithInherited(ruleName);
      if (inheritedProd.isPresent()) {
        return inheritedProd;
      }
    }
    return Optional.empty();
  }

  // return local prods and prods from supergrammars
  public Map<String, ProdSymbol> getProdsWithInherited() {
    final Map<String, ProdSymbol> ret = new LinkedHashMap<>();

    for (int i = superGrammars.size() - 1; i >= 0; i--) {
      final MCGrammarSymbolSurrogate superGrammarRef = superGrammars.get(i);

      for (ProdSymbol prod:superGrammarRef.lazyLoadDelegate().getProdsWithInherited().values()) {
        if (ret.containsKey(prod.getName())) {
          ProdSymbol superProd = ret.get(prod.getName());
          if (MCGrammarSymbolTableHelper.getAllSuperProds(prod).contains(superProd)) {
            ret.put(prod.getName(), prod);
          }
        } else {
          ret.put(prod.getName(), prod);
        }
      }
    }

    for (final ProdSymbol prodSymbol : getProds()) {
      ret.put(prodSymbol.getName(), prodSymbol);
    }

    return ret;
  }

  public Collection<String> getTokenRulesWithInherited() {
    final Collection<String> ret = Sets.newHashSet();

    for (int i = superGrammars.size() - 1; i >= 0; i--) {
      final MCGrammarSymbolSurrogate superGrammarRef = superGrammars.get(i);

      ret.addAll(superGrammarRef.lazyLoadDelegate().getTokenRulesWithInherited());
    }
    forEachSplitRules(t -> ret.add(t));
    return ret;
  }

  public Collection<String> getKeywordRulesWithInherited() {
    final Collection<String> ret = Sets.newHashSet();

    for (int i = superGrammars.size() - 1; i >= 0; i--) {
      final MCGrammarSymbolSurrogate superGrammarRef = superGrammars.get(i);

      ret.addAll(superGrammarRef.lazyLoadDelegate().getKeywordRulesWithInherited());
    }
    forEachNoKeywords(t -> ret.add(t));
    return ret;
  }

  public Map<String, Collection<String>> getTokenModesWithInherited() {
    // A token may only be assigned to one mode
    final Map<String, Collection<String>> ret = Maps.newHashMap(tokenModes);
    // allToken is the list of all already assigned characters
    Collection<String> allToken = Sets.newHashSet();
    ret.forEach((k,v) -> v.forEach(t -> allToken.add(t)));
    for (MCGrammarSymbol superGrammar: getAllSuperGrammars()) {
      for (Map.Entry<String, Collection<String>> superMode: superGrammar.getTokenModes().entrySet()) {
        Collection<String> superTokenSet;
        if (ret.containsKey(superMode.getKey())) {
          // the mode already exists
          superTokenSet = ret.get(superMode.getKey());
        } else {
          superTokenSet = Sets.newHashSet();
        }
        superMode.getValue().stream().filter(t ->!allToken.contains(t)).forEach(t ->superTokenSet.add(t));
        if (!superTokenSet.isEmpty()) {
          ret.put(superMode.getKey(), superTokenSet);
          allToken.addAll(superTokenSet);
        }
      }
    }
    return ret;
  }

  public Map<String, Collection<String>> getTokenModes() {
    return  Maps.newHashMap(tokenModes);
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

      if (!(currentScope instanceof IGrammarGlobalScope)) {
        if (currentScope instanceof IGrammarArtifactScope) {
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
      } else if (currentScope instanceof IGrammarArtifactScope) {
        return ((IGrammarArtifactScope) currentScope).getPackageName();
      }

      optCurrentScope = Optional.of(currentScope.getEnclosingScope());
    }

    return "";
  }

  public void addMode(String modeName, String tokenName) {
    if (tokenModes.containsKey(modeName)) {
      tokenModes.get(modeName).add(tokenName);
    } else {
      Collection<String> tokenList = Sets.newHashSet(tokenName);
      tokenModes.put(modeName, tokenList);
    }
  }

}
