/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.grammar._symboltable;

import de.monticore.ast.ASTNode;
import de.monticore.symboltable.IScopeSpanningSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.se_rwth.commons.Names;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Optional;

import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.nullToEmpty;
import static de.monticore.symboltable.modifiers.AccessModifier.ALL_INCLUSION;
import static de.se_rwth.commons.logging.Log.errorIfNull;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

public class MCProdComponentSymbol implements ICommonGrammarSymbol, IScopeSpanningSymbol {

  protected IGrammarScope enclosingScope;

  protected String fullName;

  protected String name;

  protected ASTNode node;

  protected String packageName;

  protected AccessModifier accessModifier = ALL_INCLUSION;

  protected IGrammarScope spannedScope;

  private boolean isTerminal;

  private boolean isNonterminal;

  private boolean isConstantGroup;

  private boolean isConstant;

  private boolean isLexerNonterminal;

  /**
   * E.g. usageName:QualifiedName
   */
  private String usageName = "";

  /**
   * Only for nonterminals. E.g., in u:R R is the name of the referenced prod.
   */
  private MCProdSymbolReference referencedProd;

  /**
   * E.g., in from:Name@State State is the referenced symbol name
   */
  private Optional<String> referencedSymbolName = empty();

  /**
   * e.g., A* or A+
   */
  private boolean isList = false;

  /**
   * e.g., A?
   */
  private boolean isOptional = false;

  public MCProdComponentSymbol(String name) {
    this.name = name;
  }

  public IGrammarScope getSpannedScope() {
    return spannedScope;
  }

  public void setSpannedScope(IGrammarScope scope) {
    this.spannedScope = scope;
    getSpannedScope().setSpanningSymbol(this);
  }

  public Optional<ASTNode> getAstNode() {
    return Optional.ofNullable(node);
  }

  public void setAstNode(ASTNode node) {
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

  public void setNonterminal(boolean nonterminal) {
    isNonterminal = nonterminal;
  }

  public boolean isNonterminal() {
    return isNonterminal;
  }

  public boolean isTerminal() {
    return isTerminal;
  }

  public void setTerminal(boolean terminal) {
    isTerminal = terminal;
  }

  public boolean isConstantGroup() {
    return isConstantGroup;
  }

  public void setConstantGroup(boolean constantGroup) {
    isConstantGroup = constantGroup;
  }

  public boolean isConstant() {
    return isConstant;
  }

  public void setConstant(boolean constant) {
    isConstant = constant;
  }

  public boolean isLexerNonterminal() {
    return isLexerNonterminal;
  }

  public void setLexerNonterminal(boolean lexerNonterminal) {
    isLexerNonterminal = lexerNonterminal;
  }

  /**
   * @return true, if rule is used as a list, i.e. '+' or '*'.
   */
  public boolean isList() {
    return isList;
  }

  /**
   * @param isList true, if rule is used as a list, i.e. '+' or '*'.
   */
  public void setList(boolean isList) {
    this.isList = isList;
  }

  /**
   * @return true, if rule is optional, i.e. '?'.
   */
  public boolean isOptional() {
    return isOptional;
  }

  /**
   * @param isOptional true, if rule is optional, i.e. '?'.
   */
  public void setOptional(boolean isOptional) {
    this.isOptional = isOptional;
  }

  /**
   * E.g. usageName:QualifiedName
   *
   * @param usageName the usageName to set
   */
  public void setUsageName(String usageName) {
    this.usageName = nullToEmpty(usageName);
  }

  /**
   * @return usageName
   */
  public String getUsageName() {
    return this.usageName;
  }

  public void setReferencedProd(MCProdSymbolReference referencedProd) {
    this.referencedProd = referencedProd;
  }

  /**
   * @return A reference to the defining production of this component, e.g., the
   * defining prod for the nonterminal <code>... = s:A</code> is the production
   * <code>A = ...</code>.
   */
  public Optional<MCProdSymbolReference> getReferencedProd() {
    return ofNullable(referencedProd);
  }

  /**
   * @return the name of the symbol referenced by this component (via the
   * <code>@</code> notation)
   */
  public Optional<String> getReferencedSymbolName() {
    return referencedSymbolName;
  }

  public void setReferencedSymbolName(String referencedSymbolName) {
    this.referencedSymbolName = ofNullable(emptyToNull(referencedSymbolName));
  }

  public boolean isSymbolReference() {
    return referencedSymbolName.isPresent();
  }


  public void addSubProdComponent(MCProdComponentSymbol prodComp) {
    errorIfNull(prodComp);
    getSpannedScope().add(prodComp);
  }

}
