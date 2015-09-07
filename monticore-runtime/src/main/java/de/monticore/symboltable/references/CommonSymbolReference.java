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

import de.monticore.ast.ASTNode;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.SymbolKind;
import de.se_rwth.commons.logging.Log;

/**
 * Default implementation of {@link SymbolReference}.
 *
 * @author Pedram Mir Seyed Nazari
 *
 */
public class CommonSymbolReference<T extends Symbol> implements SymbolReference<T> {

  private final String referencedSymbolName;
  private final SymbolKind referencedSymbolKind;

  private ASTNode astNode;

  /**
   * The enclosing scope of the reference, not of the referenced symbol (i.e., symbol definition).
   */
  private final Scope enclosingScopeOfReference;

  private T referencedSymbol;

  public CommonSymbolReference(String referencedSymbolName, SymbolKind referencedSymbolKind,
      Scope enclosingScopeOfReference) {
    this.referencedSymbolName = Log.errorIfNull(emptyToNull(referencedSymbolName));
    this.referencedSymbolKind = Log.errorIfNull(referencedSymbolKind);
    this.enclosingScopeOfReference = Log.errorIfNull(enclosingScopeOfReference);
  }

  @Override
  public String getName() {
    return referencedSymbolName;
  }

  @Override
  public T getReferencedSymbol() {
    if (!isReferencedSymbolLoaded()) {
      referencedSymbol = loadReferencedSymbol(referencedSymbolName, referencedSymbolKind).orElse(null);

      if (!isReferencedSymbolLoaded()) {
        throw new FailedLoadingSymbol(referencedSymbolName);
      }
    }
    else {
      Log.debug("Full information of '" + referencedSymbolName + "' already loaded. Use cached "
              + "version.",
          CommonSymbolReference.class.getSimpleName());
    }

    return referencedSymbol;
  }

  @Override
  public boolean existsReferencedSymbol() {
    return isReferencedSymbolLoaded() || loadReferencedSymbol(referencedSymbolName, referencedSymbolKind).isPresent();
  }

  public boolean isReferencedSymbolLoaded() {
    return referencedSymbol != null;
  }

  protected Optional<T> loadReferencedSymbol(final String symbolName, final SymbolKind symbolKind) {
    checkArgument(!isNullOrEmpty(symbolName), " 0xA4070 Symbol name may not be null or empty.");
    Log.errorIfNull(symbolKind);

    Log.debug("Load full information of '" + symbolName + "' (Kind " + symbolKind.getName() + ").",
        SymbolReference.class.getSimpleName());
    Optional<T> resolvedSymbol = enclosingScopeOfReference.resolve(symbolName, symbolKind);

    if (resolvedSymbol.isPresent()) {
      Log.debug("Loaded full information of '" + symbolName + "' successfully.",
          SymbolReference.class.getSimpleName());
    }
    else {
      Log.warn("0xA1038 " + SymbolReference.class.getSimpleName() + " Could not load full information of '" +
          symbolName + "' (Kind " + symbolKind.getName() + ").");
    }


    return resolvedSymbol;
  }

  public Optional<ASTNode> getAstNode() {
    return Optional.ofNullable(astNode);
  }

  @Override public void setAstNode(ASTNode astNode) {
    this.astNode = astNode;
  }
}
