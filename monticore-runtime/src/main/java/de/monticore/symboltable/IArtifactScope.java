/* (c) https://github.com/MontiCore/monticore */


package de.monticore.symboltable;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static de.se_rwth.commons.Names.getQualifier;
import static de.se_rwth.commons.Names.getSimpleName;
import static de.se_rwth.commons.logging.Log.trace;

/**
 * Common interface for all artifact scopes.
 */
public interface IArtifactScope {

  /**
   * This method returns the package name of the current artifact scope.
   * If the package is empty or a language does not support packages,
   * the method implementation returns an empty String.
   * @return
   */
  String getPackageName();

  /**
   * This method can be used to set the package name of the current
   * artifact scope.
   * @param packageName
   */
  void setPackageName(String packageName);

  /**
   * This method returns the full name of the current artifact scope.
   * The full name of an artifact scope is the name of the artifact,
   * preceded by the package, if it is not empty.
   * @return
   */
  String getFullName();

  /**
   * Calculates possible qualified names for the <code>simpleName</code>. For this,
   * it considers the (possible) <code>packageName</code> and the <code>imports</code>
   * (i.e., import statements).
   *
   * @param name        the simple name of the symbol
   * @param packageName the possible package name
   * @param imports     the import statements
   * @return a set of possible qualified names for the <code>simpleName</code>
   * @deprecated This method will be removed soon. Instead, symbol table creators should
   * qualify names pointing to symbols of foreign models with the respective
   * import statements in the model.
   */
  @Deprecated
  default Set<String> calculateQualifiedNames(String name, String packageName,
      List<ImportStatement> imports) {
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
        }
        else if (getSimpleName(importStatement.getStatement()).equals(name)) {
          potentialSymbolNames.add(importStatement.getStatement());
        }
      }
    }
    trace("Potential qualified names for \"" + name + "\": " + potentialSymbolNames.toString(),
        "IArtifactScope");

    return potentialSymbolNames;
  }

}
