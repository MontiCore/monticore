/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.grammar._symboltable;

import com.google.common.collect.ImmutableSet;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.symboltable.IScopeSpanningSymbol;
import de.monticore.symboltable.SymbolKind;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.se_rwth.commons.Names;

import java.util.*;

import static com.google.common.collect.ImmutableList.copyOf;
import static de.monticore.symboltable.modifiers.AccessModifier.ALL_INCLUSION;
import static de.se_rwth.commons.logging.Log.errorIfNull;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

public class MCGrammarSymbol implements ICommonGrammarSymbol, IScopeSpanningSymbol {

  protected IGrammarScope enclosingScope;

  protected String fullName;

  protected String name;

  protected ASTMCGrammar node;

  protected String packageName;

  protected AccessModifier accessModifier = ALL_INCLUSION;

  private final List<MCGrammarSymbolReference> superGrammars = new ArrayList<>();

  /**
   * Is the grammar abstract?
   */
  private boolean isComponent = false;

  // the start production of the grammar
  private MCProdSymbol startProd;

  public MCGrammarSymbol(String name) {
    this.name = name;
  }

  protected IGrammarScope spannedScope;

  public IGrammarScope getSpannedScope() {
    return spannedScope;
  }

  public void setSpannedScope(IGrammarScope scope) {
    this.spannedScope = scope;
    getSpannedScope().setSpanningSymbol(this);
  }

  public Optional<de.monticore.grammar.grammar._ast.ASTMCGrammar> getAstNode() {
    return Optional.ofNullable(node);
  }

  public void setAstNode(de.monticore.grammar.grammar._ast.ASTMCGrammar node) {
    this.node = node;
  }

  public void accept(de.monticore.grammar.grammar._visitor.GrammarSymbolVisitor visitor) {
    visitor.handle(this);
  }

  public IGrammarScope getEnclosingScope(){
    return this.enclosingScope;
  }

  public void setEnclosingScope(IGrammarScope newEnclosingScope){
    this.enclosingScope = newEnclosingScope;
  }

  @Override
  public void setAccessModifier(AccessModifier accessModifier) {
    this.accessModifier = accessModifier;
  }

  @Override
  public AccessModifier getAccessModifier() {
    return this.accessModifier;
  }

  @Override public String getName() {
    return name;
  }

  @Override public String getPackageName() {
    if (packageName == null) {
      packageName = determinePackageName();
    }

    return packageName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getFullName() {
    if (fullName == null) {
      fullName = determineFullName();
    }

    return fullName;
  }
  protected String determinePackageName() {
    Optional<? extends IGrammarScope> optCurrentScope = Optional.ofNullable(enclosingScope);

    while (optCurrentScope.isPresent()) {
      final IGrammarScope currentScope = optCurrentScope.get();
      if (currentScope.isSpannedBySymbol()) {
        // If one of the enclosing scope(s) is spanned by a symbol, take its
        // package name. This check is important, since the package name of the
        // enclosing symbol might be set manually.
        return currentScope.getSpanningSymbol().get().getPackageName();
      } else if (currentScope instanceof GrammarArtifactScope) {
        return ((GrammarArtifactScope) currentScope).getPackageName();
      }

      optCurrentScope = currentScope.getEnclosingScope();
    }

    return "";
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
      if (currentScope.isSpannedBySymbol()) {
        // If one of the enclosing scope(s) is spanned by a symbol, the full name
        // of that symbol is the missing prefix, and hence, the calculation
        // ends here. This check is important, since the full name of the enclosing
        // symbol might be set manually.
        nameParts.addFirst(currentScope.getSpanningSymbol().get().getFullName());
        break;
      }

      if (!(currentScope instanceof IGrammarGlobalScope)) {
        if (currentScope instanceof GrammarArtifactScope) {
          // We have reached the artifact scope. Get the package name from the
          // symbol itself, since it might be set manually.
          if (!getPackageName().isEmpty()) {
            nameParts.addFirst(getPackageName());
          }
        } else {
          if (currentScope.getName().isPresent()) {
            nameParts.addFirst(currentScope.getName().get());
          }
          // ...else stop? If one of the enclosing scopes is unnamed,
          //         the full name is same as the simple name.
        }
      }
      optCurrentScope = currentScope.getEnclosingScope();
    }

    return Names.getQualifiedName(nameParts);
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

  public Collection<MCProdSymbol> getProds() {
    return this.getSpannedScope().getLocalMCProdSymbols();
  }

  public Collection<String> getProdNames() {
    final Set<String> prodNames = new LinkedHashSet<>();

    for (final MCProdSymbol prodSymbol : getProds()) {
      prodNames.add(prodSymbol.getName());
    }

    return ImmutableSet.copyOf(prodNames);
  }

  public Optional<MCProdSymbol> getProd(String prodName) {
    return this.getSpannedScope().resolveProdLocally(prodName);
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
    Optional<MCProdSymbol> mcProd = empty();
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
