/* (c) https://github.com/MontiCore/monticore */


package de.monticore.grammar.grammar._symboltable;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.references.ISymbolReference;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;
import java.util.function.Predicate;

public class MCGrammarSymbolReference extends MCGrammarSymbol implements ISymbolReference {
  protected AccessModifier accessModifier = AccessModifier.ALL_INCLUSION;

  protected Predicate<MCGrammarSymbol> predicate = x -> true;

  protected ASTMCGrammar astNode;

  protected MCGrammarSymbol referencedSymbol;

  public MCGrammarSymbolReference(final String name, final IGrammarScope enclosingScopeOfReference) {
    super(name);
    this.name = name;
    this.enclosingScope = enclosingScopeOfReference;
  }


  @Override
  public String getName() {
    if (isReferencedSymbolLoaded()) {
      return getReferencedSymbol().getName();
    }
    return this.name;
  }

  @Override
  public String getFullName() {
    return getReferencedSymbol().getFullName();
  }

  @Override
  public void setEnclosingScope(IGrammarScope scope) {
    getReferencedSymbol().setEnclosingScope(scope);
  }

  @Override
  public IGrammarScope getEnclosingScope() {
    return getReferencedSymbol().getEnclosingScope();
  }

  @Override
  public IGrammarScope getSpannedScope() {
    return getReferencedSymbol().getSpannedScope();
  }

  @Override
  public AccessModifier getAccessModifier() {
    return getReferencedSymbol().getAccessModifier();
  }

  @Override
  public void setAccessModifier(AccessModifier accessModifier) {
    getReferencedSymbol().setAccessModifier(accessModifier);
  }

  public Optional<ASTMCGrammar> getAstNode() {
    return getReferencedSymbol().getAstNode();
  }

  @Override
  public void setAstNode(ASTMCGrammar astNode) {
    getReferencedSymbol().setAstNode(astNode);
  }


  public void setPredicate(Predicate<MCGrammarSymbol> predicate) {
    this.predicate = predicate;
  }

  @Override
  public String getPackageName() {
    return getReferencedSymbol().getPackageName();
  }

  @Override
  public MCGrammarSymbol getReferencedSymbol() {
    if (!isReferencedSymbolLoaded()) {
      referencedSymbol = loadReferencedSymbol().orElse(null);

      if (!isReferencedSymbolLoaded()) {
        Log.error("0xA1038 " + MCGrammarSymbolReference.class.getSimpleName() + " Could not load full information of '" +
                name + "' (Kind " + "MCGrammarSymbol" + ").");
      }
    }

    return referencedSymbol;
  }


  @Override
  public boolean existsReferencedSymbol() {
    return isReferencedSymbolLoaded() || loadReferencedSymbol().isPresent();
  }

  public boolean isReferencedSymbolLoaded() {
    return referencedSymbol != null;
  }

  protected Optional<MCGrammarSymbol> loadReferencedSymbol() {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(name), " 0xA4070 Symbol name may not be null or empty.");

    Log.debug("Load full information of '" + name + "' (Kind " + "MCGrammarSymbol" + ").",
            MCGrammarSymbolReference.class.getSimpleName());
    Optional<MCGrammarSymbol> resolvedSymbol = enclosingScope.resolveMCGrammar(name, accessModifier, predicate);

    if (resolvedSymbol.isPresent()) {
      Log.debug("Loaded full information of '" + name + "' successfully.",
              MCGrammarSymbolReference.class.getSimpleName());
    }
    else {
      Log.debug("Cannot load full information of '" + name,
              MCGrammarSymbolReference.class.getSimpleName());
    }
    return resolvedSymbol;
  }

}