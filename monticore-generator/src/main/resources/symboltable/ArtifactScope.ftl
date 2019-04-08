<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className","scopeClass", "languageName", "symbolNames")}

<#assign genHelper = glex.getGlobalVar("stHelper")>


<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${genHelper.getTargetPackage()};

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import com.google.common.collect.FluentIterable;

import de.monticore.symboltable.ImportStatement;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.names.CommonQualifiedNamesCalculator;
import de.monticore.symboltable.names.QualifiedNamesCalculator;
import de.monticore.utils.Names;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Splitters;
import de.se_rwth.commons.logging.Log;

public class ${className} extends ${scopeClass} {


  private final String packageName;
  
  private final List<ImportStatement> imports;

  private QualifiedNamesCalculator qualifiedNamesCalculator;

  public ${className}(final String packageName, final List<ImportStatement> imports) {
    this(Optional.empty(), packageName, imports);
  }

  public ${className}(final Optional<I${languageName}Scope> enclosingScope, final String packageName, final List<ImportStatement> imports) {
    super(true);
    if (enclosingScope.isPresent()) {
      setEnclosingScope(enclosingScope.get());
    }
    setExportsSymbols(true);
    Log.errorIfNull(packageName);
    Log.errorIfNull(imports);

    if (!packageName.isEmpty()) {
      this.packageName = packageName.endsWith(".") ? packageName.substring(0, packageName.length() - 1) : packageName;
    } else {
      // default package
      this.packageName = "";
    }

    this.imports = Collections.unmodifiableList(new ArrayList<>(imports));
    this.qualifiedNamesCalculator = new CommonQualifiedNamesCalculator();
  }

  @Override
  public Optional<String> getName() {
    if (!super.getName().isPresent()) {
      final Optional<ICommon${languageName}Symbol> topLevelSymbol = getTopLevelSymbol();
      if (topLevelSymbol.isPresent()) {
        setName(topLevelSymbol.get().getName());
      }
    }
    return super.getName();
  }

  public Optional<ICommon${languageName}Symbol> getTopLevelSymbol() {
    if (getSubScopes().size() == 1) {
      return getSubScopes().get(0).getSpanningSymbol();
    }
    // there is no top level symbol, if more than one sub scope exists.
    return Optional.empty();
  }

  public String getPackageName() {
    return packageName;
  }
  
    public void setQualifiedNamesCalculator(QualifiedNamesCalculator qualifiedNamesCalculator) {
    this.qualifiedNamesCalculator = Objects.requireNonNull(qualifiedNamesCalculator);
  }

  public List<ImportStatement> getImports() {
    return Collections.unmodifiableList(imports);
  }
  
  @Override //TODO: Does not depend on a specific language. Move implementation to RTE?
  public boolean checkIfContinueAsSubScope(String symbolName) {
    if (this.exportsSymbols()) {
      final String symbolQualifier = Names.getQualifier(symbolName);

      final List<String> symbolQualifierParts = Splitters.DOT.splitToList(symbolQualifier);
      final List<String> packageParts = Splitters.DOT.splitToList(packageName);

      boolean symbolNameStartsWithPackage = true;

      if (packageName.isEmpty()) {
        // symbol qualifier always contains default package (i.e., empty string)
        symbolNameStartsWithPackage = true;
      } else if (symbolQualifierParts.size() >= packageParts.size()) {
        for (int i = 0; i < packageParts.size(); i++) {
          if (!packageParts.get(i).equals(symbolQualifierParts.get(i))) {
            symbolNameStartsWithPackage = false;
            break;
          }
        }
      } else {
        symbolNameStartsWithPackage = false;
      }
      return symbolNameStartsWithPackage;
    }
    return false;
  }
  
  @Override //TODO: Does not depend on a specific language. Move implementation to RTE?
  public String getRemainingNameForResolveDown(String symbolName) {
    final String packageAS = this.getPackageName();
    final FluentIterable<String> packageASNameParts = FluentIterable.from(Splitters.DOT.omitEmptyStrings().split(packageAS));

    final FluentIterable<String> symbolNameParts = FluentIterable.from(Splitters.DOT.split(symbolName));
    String remainingSymbolName = symbolName;

    if (symbolNameParts.size() > packageASNameParts.size()) {
      remainingSymbolName = Joiners.DOT.join(symbolNameParts.skip(packageASNameParts.size()));
    }

    return remainingSymbolName;
  }
  
<#list symbolNames?keys as symbol> 
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
  public Collection<${symbolNames[symbol]}> continue${symbol}WithEnclosingScope(
      boolean foundSymbols, String name,
      AccessModifier modifier, Predicate<${symbolNames[symbol]}> predicate) {
    final Collection<${symbolNames[symbol]}> result = new LinkedHashSet<>();

    if (checkIfContinueWithEnclosingScope(foundSymbols) && (getEnclosingScope().isPresent())) {
      if (!(enclosingScope instanceof I${languageName}GlobalScope)) {
        warn("0xA1039 The artifact scope " + getName().orElse("") + " should have a global scope as enclosing scope or no "
                + "enclosing scope at all.");
      }
      foundSymbols = foundSymbols | result.size() > 0;
      final Set<String> potentialQualifiedNames = qualifiedNamesCalculator.calculateQualifiedNames(name, packageName, imports);

      for (final String potentialQualifiedName : potentialQualifiedNames) {
        final Collection<${symbolNames[symbol]}> resolvedFromEnclosing = enclosingScope.resolve${symbol}Many(foundSymbols, potentialQualifiedName, modifier, predicate);
        foundSymbols = foundSymbols | resolvedFromEnclosing.size() > 0;
        result.addAll(resolvedFromEnclosing);
      }
    }
    return result;
  }
  
</#list>
}
