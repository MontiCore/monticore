/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2016, MontiCore, All rights reserved.
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

package de.monticore.grammar.symboltable;

import static com.google.common.base.Preconditions.checkArgument;
import static de.monticore.codegen.GeneratorHelper.isQualified;
import static de.monticore.symboltable.modifiers.AccessModifier.ALL_INCLUSION;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.function.Predicate;

import de.monticore.symboltable.CommonScope;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ScopeSpanningSymbol;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.SymbolKind;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.resolving.ResolvingInfo;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

/**
 * @author Pedram Mir Seyed Nazari
 */
public class MCGrammarScope extends CommonScope {

  public MCGrammarScope(Optional<MutableScope> enclosingScope) {
    super(enclosingScope, true);
  }

  @Override
  public void setSpanningSymbol(ScopeSpanningSymbol symbol) {
    checkArgument(symbol instanceof MCGrammarSymbol);
    super.setSpanningSymbol(symbol);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Optional<MCGrammarSymbol> getSpanningSymbol() {
    return (Optional<MCGrammarSymbol>) super.getSpanningSymbol();
  }
  
  @Override
  public <T extends Symbol> Optional<T> resolveImported(String name, SymbolKind kind, AccessModifier modifier) {
    final Collection<T> resolvedSymbols = resolveManyLocally(new ResolvingInfo(getResolvingFilters()), name, kind, modifier, x -> true);
    if (resolvedSymbols.isEmpty()) {
      return resolveInSuperGrammars(name, kind, modifier);
    }

    return getResolvedOrThrowException(resolvedSymbols);
  }

  public <T extends Symbol> Collection<T> resolveMany(ResolvingInfo resolvingInfo, String name, SymbolKind kind, AccessModifier modifier,
      Predicate<Symbol> predicate) {

    final Collection<T> resolvedSymbols = new LinkedHashSet<T>();

    Optional<T> resolvedSymbol = this.resolveImported(name, kind, modifier);

    if (!resolvedSymbol.isPresent()) {
      resolvedSymbol = resolveInSuperGrammars(name, kind, modifier);
    }

    if (!resolvedSymbol.isPresent()) {
      // continue with enclosing scope
      resolvedSymbols.addAll(super.resolveMany(resolvingInfo, name, kind, modifier, predicate));
    }
    else {
      resolvedSymbols.add(resolvedSymbol.get());
    }

    return resolvedSymbols;
  }

  protected <T extends Symbol> Optional<T> resolveInSuperGrammars(String name, SymbolKind kind, AccessModifier modifier) {
    Optional<T> resolvedSymbol = Optional.empty();
    
    // TODO (GV, MB)
    // Die Methode muss überarbeitet werden. GrammarSymbols sollen nicht gefunden werden? Dann braucht man u.U. 
    // checkIfContinueWithSuperGrammar gar nicht mehr ...
    if (kind.equals(MCGrammarSymbol.KIND)) {
      return resolvedSymbol;
    }
    final MCGrammarSymbol spanningSymbol = getSpanningSymbol().get();
    for (MCGrammarSymbolReference superGrammarRef : spanningSymbol.getSuperGrammars()) {
      if (checkIfContinueWithSuperGrammar(name, superGrammarRef)
          && (superGrammarRef.existsReferencedSymbol())) {
        final MCGrammarSymbol superGrammar = superGrammarRef.getReferencedSymbol();
        resolvedSymbol = resolveInSuperGrammar(name, kind, superGrammar);
        // Stop as soon as symbol is found in a super grammar.
        if (resolvedSymbol.isPresent()) {
          break;
        }
      }
    }

    return resolvedSymbol;
  }

  private boolean checkIfContinueWithSuperGrammar(String name, MCGrammarSymbolReference superGrammar) {
    // checks cases:
    // 1) A   and A
    // 2) c.A and A
    // 3) A   and p.A
    // 4) p.A and p.A
    // 5) c.A and p.A // <-- only continue with this case, since we can be sure,
    //                       that we are not searching for the super grammar itself.
    String superGrammarName = superGrammar.getName();
    if (Names.getSimpleName(superGrammarName).equals(Names.getSimpleName(name))) {

      // checks cases 1) and 4)
      if (superGrammarName.equals(name) ||
          // checks cases 2) and 3)
          (isQualified(superGrammar) != isQualified(name))) {
        return false;
      } else {
        // case 5)
        return true;
      }
    }
    // names have different simple names and the name isn't qualified (A and p.B)
    return isQualified(superGrammar) && !isQualified(name);
  }

  private <T extends Symbol> Optional<T> resolveInSuperGrammar(String name, SymbolKind kind,
      MCGrammarSymbol superGrammar) {

    Log.trace("Continue in scope of super grammar " + superGrammar.getName(),
        MCGrammarScope.class.getSimpleName());

    return superGrammar.getSpannedScope().resolveImported(name, kind, ALL_INCLUSION);
  }

}
