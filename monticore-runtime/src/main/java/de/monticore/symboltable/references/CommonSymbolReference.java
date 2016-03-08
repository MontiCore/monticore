/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.monticore.symboltable.references;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.isNullOrEmpty;

import java.util.Optional;
import java.util.function.Predicate;

import de.monticore.ast.ASTNode;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.SymbolKind;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.se_rwth.commons.logging.Log;

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
        throw new FailedLoadingSymbol(referencedName);
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
    // TODO PN resolve with access modifier predicate
    Optional<T> resolvedSymbol = enclosingScope.resolve(referencedName, referencedKind);

    if (resolvedSymbol.isPresent()) {
      Log.debug("Loaded full information of '" + referencedName + "' successfully.",
          SymbolReference.class.getSimpleName());
    }
    else {
      Log.warn("0xA1038 " + SymbolReference.class.getSimpleName() + " Could not load full information of '" +
          referencedName + "' (Kind " + referencedKind.getName() + ").");
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
