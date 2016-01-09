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

package de.monticore.symboltable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.FluentIterable;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.resolving.ResolvingInfo;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.Splitters;
import de.se_rwth.commons.logging.Log;

/**
 * @author Pedram Mir Seyed Nazari
 */
// TODO PN Doc
public class ArtifactScope extends CommonScope {

  private final String packageName;
  private final List<ImportStatement> imports;

  public ArtifactScope(final Optional<MutableScope> enclosingScope, final String packageName,
      final List<ImportStatement> imports) {
    super(enclosingScope, true);
    setExportsSymbols(true);

    Log.errorIfNull(packageName);
    Log.errorIfNull(imports);

    if (!packageName.isEmpty()) {
      this.packageName = packageName.endsWith(".") ? packageName.substring(0, packageName.length() - 1) : packageName;
    }
    else {
      // default package
      this.packageName = "";
    }

    this.imports = Collections.unmodifiableList(new ArrayList<>(imports));
  }

  @Override
  public Optional<String> getName() {
    if (!super.getName().isPresent()) {
      final Optional<? extends ScopeSpanningSymbol> topLevelSymbol = getTopLevelSymbol();
      if (topLevelSymbol.isPresent()) {
        setName(topLevelSymbol.get().getName());
      }
    }
    return super.getName();
  }

  public Optional<? extends ScopeSpanningSymbol> getTopLevelSymbol() {
    if (getSubScopes().size() == 1) {
      return getSubScopes().get(0).getSpanningSymbol();
    }
    // there is no top level symbol, if more than one sub scope exists.
    return Optional.empty();
  }

  public String getPackageName() {
    return packageName;
  }

  /**
   * Starts the bottom-up inter-model resolution process.
   *
   * @param <T>
   * @param resolvingInfo
   * @param name
   * @param kind
   * @return
   */
  @Override
  protected <T extends Symbol> Collection<T> continueWithEnclosingScope(final ResolvingInfo resolvingInfo, final String name,
      final SymbolKind kind, final AccessModifier modifier) {
    final Collection<T> result = new LinkedHashSet<>();

    if (checkIfContinueWithEnclosing(resolvingInfo.areSymbolsFound()) && (getEnclosingScope().isPresent())) {
      if (!(enclosingScope instanceof GlobalScope)) {
        Log.warn("0xA1039 An artifact scope should have the global scope as enclosing scope or no "
            + "enclosing scope at all.");
      }

      for (final String potentialName : determinePotentialNames(name)) {
        final Collection<T> resolvedFromEnclosing = enclosingScope.resolveMany(resolvingInfo, potentialName, kind, modifier);

        result.addAll(resolvedFromEnclosing);
      }
    }
    return result;
  }

  // TODO PN doc
  protected Collection<String> determinePotentialNames(final String name) {
    final Collection<String> potentialSymbolNames = new LinkedHashSet<>();

    // the simple name (in default package)
    potentialSymbolNames.add(name);

    // if name is already qualified, no further (potential) names exist.
    if (Names.getQualifier(name).isEmpty()) {
      // maybe the model belongs to the same package
      if (!packageName.isEmpty()) {
        potentialSymbolNames.add(packageName + "." + name);
      }

      for (ImportStatement importStatement : imports) {
        if (importStatement.isStar()) {
          potentialSymbolNames.add(importStatement.getStatement() + "." + name);
        }
        else if (Names.getSimpleName(importStatement.getStatement()).equals(name)) {
          potentialSymbolNames.add(importStatement.getStatement());
        }
      }
    }
    Log.trace("Potential qualified names for \"" + name + "\": " + potentialSymbolNames.toString(),
        ArtifactScope.class.getSimpleName());
    return potentialSymbolNames;
  }

  protected String getRemainingNameForResolveDown(String symbolName) {
    final String packageAS = this.getPackageName();
    final FluentIterable<String> packageASNameParts = FluentIterable.from(Splitters.DOT.omitEmptyStrings().split(packageAS));

    final FluentIterable<String> symbolNameParts = FluentIterable.from(Splitters.DOT.split(symbolName));
    String remainingSymbolName = symbolName;

    if (symbolNameParts.size() > packageASNameParts.size()) {
      remainingSymbolName = Joiners.DOT.join(symbolNameParts.skip(packageASNameParts.size()));
    }
    // TODO PN else?
    return remainingSymbolName;
  }

  @Override
  protected boolean checkIfContinueAsSubScope(String symbolName, SymbolKind kind) {
    if(this.exportsSymbols()) {
      final String packageCU = this.getPackageName();
      final String symbolQualifier = Names.getQualifier(symbolName);

      if (symbolQualifier.startsWith(packageCU)) {
        // TODO PN compare name parts, to exclude cases like "a.bb".startsWith("a.b")
        return true;
      }
    }
    return false;
  }

}
