/* (c)  https://github.com/MontiCore/monticore */

package de.monticore.grammar.grammar._symboltable;

import de.monticore.grammar.grammar._ast.ASTAdditionalAttribute;
import de.monticore.symboltable.SymbolKind;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.se_rwth.commons.Names;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

import static de.monticore.symboltable.modifiers.AccessModifier.ALL_INCLUSION;

public class MCProdAttributeSymbol implements ICommonGrammarSymbol {

  protected IGrammarScope enclosingScope;

  protected String fullName;

  protected String name;

  protected ASTAdditionalAttribute node;

  protected String packageName;

  protected AccessModifier accessModifier = ALL_INCLUSION;

  private MCProdOrTypeReference typeReference;
  
  public MCProdAttributeSymbol(String name) {
    this.name = name;
  }

  public Optional<de.monticore.grammar.grammar._ast.ASTAdditionalAttribute> getAstNode() {
    return Optional.ofNullable(node);
  }

  public void setAstNode(de.monticore.grammar.grammar._ast.ASTAdditionalAttribute node) {
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

  public void setTypeReference(MCProdOrTypeReference referencedProd) {
    this.typeReference = referencedProd;
  }
  
  /**
   * @return A reference to the defining production of this component, e.g., the
   * defining prod for the nonterminal <code>... = s:A</code> is the production
   * <code>A = ...</code>.
   */
  public Optional<MCProdOrTypeReference> getTypeReference() {
    return Optional.ofNullable(typeReference);
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
