/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable;

import com.google.common.collect.FluentIterable;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.names.CommonQualifiedNamesCalculator;
import de.monticore.symboltable.names.QualifiedNamesCalculator;
import de.monticore.symboltable.resolving.ResolvingInfo;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.Splitters;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

/**
 * Represents the scope of the whole artifact (i.e., file or compilation unit).
 *
 * @author Pedram Mir Seyed Nazari
 */
public class ArtifactScope extends CommonScope {

  private final String packageName;
  private final List<ImportStatement> imports;

  private QualifiedNamesCalculator qualifiedNamesCalculator;

  public ArtifactScope(final String packageName, final List<ImportStatement> imports) {
    this(Optional.empty(), packageName, imports);
  }

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

    this.qualifiedNamesCalculator = new CommonQualifiedNamesCalculator();
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
      final SymbolKind kind, final AccessModifier modifier, final Predicate<Symbol> predicate) {
    final Collection<T> result = new LinkedHashSet<>();

    if (checkIfContinueWithEnclosingScope(resolvingInfo.areSymbolsFound()) && (getEnclosingScope().isPresent())) {
      if (!(enclosingScope instanceof GlobalScope)) {
        Log.warn("0xA1039 The artifact scope " + getName().orElse("") + " should have the global scope as enclosing scope or no "
            + "enclosing scope at all.");
      }

      final Set<String> potentialQualifiedNames = qualifiedNamesCalculator.calculateQualifiedNames(name, packageName, imports);

      for (final String potentialQualifiedName : potentialQualifiedNames) {
        final Collection<T> resolvedFromEnclosing = enclosingScope.resolveMany(resolvingInfo, potentialQualifiedName, kind, modifier, predicate);

        result.addAll(resolvedFromEnclosing);
      }
    }
    return result;
  }

  protected String getRemainingNameForResolveDown(String symbolName) {
    final String packageAS = this.getPackageName();
    final FluentIterable<String> packageASNameParts = FluentIterable.from(Splitters.DOT.omitEmptyStrings().split(packageAS));

    final FluentIterable<String> symbolNameParts = FluentIterable.from(Splitters.DOT.split(symbolName));
    String remainingSymbolName = symbolName;

    if (symbolNameParts.size() > packageASNameParts.size()) {
      remainingSymbolName = Joiners.DOT.join(symbolNameParts.skip(packageASNameParts.size()));
    }

    return remainingSymbolName;
  }

  @Override
  protected boolean checkIfContinueAsSubScope(String symbolName, SymbolKind kind) {
    if(this.exportsSymbols()) {
      final String symbolQualifier = Names.getQualifier(symbolName);

      final List<String> symbolQualifierParts = Splitters.DOT.splitToList(symbolQualifier);
      final List<String> packageParts = Splitters.DOT.splitToList(packageName);

      boolean symbolNameStartsWithPackage = true;

      if (packageName.isEmpty()) {
        // symbol qualifier always contains default package (i.e., empty string)
        symbolNameStartsWithPackage = true;
      }
      else if (symbolQualifierParts.size() >= packageParts.size()) {
        for (int i = 0; i < packageParts.size(); i++) {
          if (!packageParts.get(i).equals(symbolQualifierParts.get(i))) {
            symbolNameStartsWithPackage = false;
            break;
          }
        }
      }
      else {
        symbolNameStartsWithPackage = false;
      }
      return symbolNameStartsWithPackage;
    }
    return false;
  }

  public void setQualifiedNamesCalculator(QualifiedNamesCalculator qualifiedNamesCalculator) {
    this.qualifiedNamesCalculator = requireNonNull(qualifiedNamesCalculator);
  }

  public List<ImportStatement> getImports() {
    return Collections.unmodifiableList(imports);
  }
}
