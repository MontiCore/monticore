/* (c) https://github.com/MontiCore/monticore */


package de.monticore.symboltable;

import com.google.common.collect.FluentIterable;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Splitters;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static de.se_rwth.commons.Names.getQualifier;
import static de.se_rwth.commons.Names.getSimpleName;
import static de.se_rwth.commons.logging.Log.trace;

/**
 * Common interface for all artifact scopes
 */
public interface IArtifactScope {

  String getPackageName ();

  default  public  String getRemainingNameForResolveDown (String symbolName)  {
    final FluentIterable<String> packageASNameParts = FluentIterable
        .from(Splitters.DOT.omitEmptyStrings().split(getPackageName()));

    final FluentIterable<String> symbolNameParts = FluentIterable
        .from(Splitters.DOT.split(symbolName));
    String remainingSymbolName = symbolName;

    if (symbolNameParts.size() > packageASNameParts.size()) {
      remainingSymbolName = Joiners.DOT.join(symbolNameParts.skip(packageASNameParts.size()));
    }

    return remainingSymbolName;
  }

  /**
   * Calculates possible qualified names for the <code>simpleName</code>. For this,
   * it considers the (possible) <code>packageName</code> and the <code>imports</code>
   * (i.e., import statements).
   *
   * @param name  the simple name of the symbol
   * @param packageName the possible package name
   * @param imports     the import statements
   * @return a set of possible qualified names for the <code>simpleName</code>
   * @deprecated This method will be removed soon. Instead, symbol table creators should
   *  qualify names pointing to symbols of foreign models with the respective
   *  import statements in the model.
   */
  @Deprecated
  default public Set<String> calculateQualifiedNames(String name, String packageName, List<ImportStatement> imports) {
    final Set<String> potentialSymbolNames = new LinkedHashSet<>();

    // the simple name (in default package)
    potentialSymbolNames.add(name);

    // if name is already qualified, no further (potential) names exist.
    if (getQualifier(name).isEmpty()) {
      // maybe the model belongs to the same package
      if (!packageName.isEmpty()) {
        potentialSymbolNames.add(packageName + "." + name);
      }

      for (ImportStatement importStatement : imports) {
        if (importStatement.isStar()) {
          potentialSymbolNames.add(importStatement.getStatement() + "." + name);
        } else if (getSimpleName(importStatement.getStatement()).equals(name)) {
          potentialSymbolNames.add(importStatement.getStatement());
        }
      }
    }
    trace("Potential qualified names for \"" + name + "\": " + potentialSymbolNames.toString(),
        "IArtifactScope");

    return potentialSymbolNames;
  }

}
