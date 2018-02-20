/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.references;

import de.monticore.ast.ASTNode;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.SymbolKind;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;
import java.util.function.Predicate;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Default implementation of {@link SymbolReference}.
 *
 * @author Pedram Mir Seyed Nazari
 *
 */
public class CommonSymbolReference<T extends Symbol> implements SymbolReference<T> {

  private final String referencedName;
  private final SymbolKind referencedKind;

  private AccessModifier accessModifier = AccessModifier.ALL_INCLUSION;
  private Predicate<Symbol> predicate = x -> true;


  private ASTNode astNode;


  /**
   * The enclosing scope of the reference, not of the referenced symbol (i.e., symbol definition).
   */
  private final Scope enclosingScope;

  private T referencedSymbol;

  public CommonSymbolReference(String referencedSymbolName, SymbolKind referencedSymbolKind,
      Scope enclosingScopeOfReference) {
    this.referencedName = Log.errorIfNull(emptyToNull(referencedSymbolName));
    this.referencedKind = Log.errorIfNull(referencedSymbolKind);
    this.enclosingScope = Log.errorIfNull(enclosingScopeOfReference);
  }

  @Override
  public String getName() {
    return referencedName;
  }

  @Override
  public T getReferencedSymbol() {
    if (!isReferencedSymbolLoaded()) {
      referencedSymbol = loadReferencedSymbol().orElse(null);

      if (!isReferencedSymbolLoaded()) {
        Log.error("0xA1038 " + SymbolReference.class.getSimpleName() + " Could not load full information of '" +
            referencedName + "' (Kind " + referencedKind.getName() + ").");
      }
    }
    else {
      Log.debug("Full information of '" + referencedName + "' already loaded. Use cached "
              + "version.",
          CommonSymbolReference.class.getSimpleName());
    }

    return referencedSymbol;
  }

  @Override
  public Scope getEnclosingScope() {
    return enclosingScope;
  }

  @Override
  public boolean existsReferencedSymbol() {
    return isReferencedSymbolLoaded() || loadReferencedSymbol().isPresent();
  }

  public boolean isReferencedSymbolLoaded() {
    return referencedSymbol != null;
  }

  protected Optional<T> loadReferencedSymbol() {
    checkArgument(!isNullOrEmpty(referencedName), " 0xA4070 Symbol name may not be null or empty.");
    Log.errorIfNull(referencedKind);

    Log.debug("Load full information of '" + referencedName + "' (Kind " + referencedKind.getName() + ").",
        SymbolReference.class.getSimpleName());
    Optional<T> resolvedSymbol = enclosingScope.resolve(referencedName, referencedKind, accessModifier, predicate);

    if (resolvedSymbol.isPresent()) {
      Log.debug("Loaded full information of '" + referencedName + "' successfully.",
          SymbolReference.class.getSimpleName());
    }
    else {
      Log.debug("Cannot load full information of '" + referencedName,
          SymbolReference.class.getSimpleName());
    }
    return resolvedSymbol;
  }

  public Optional<ASTNode> getAstNode() {
    return Optional.ofNullable(astNode);
  }

  @Override public void setAstNode(ASTNode astNode) {
    this.astNode = astNode;
  }

  public void setAccessModifier(AccessModifier accessModifier) {
    this.accessModifier = accessModifier;
  }

  public void setPredicate(Predicate<Symbol> predicate) {
    this.predicate = predicate;
  }
}
