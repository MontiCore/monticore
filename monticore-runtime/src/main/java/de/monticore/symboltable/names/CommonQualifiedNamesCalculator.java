/* (c) https://github.com/MontiCore/monticore */


package de.monticore.symboltable.names;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import de.monticore.symboltable.ImportStatement;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

/**
 * Provides default implementation for {@link QualifiedNamesCalculator}.
 *
 * @author Pedram Mir Seyed Nazari
 */
public class CommonQualifiedNamesCalculator implements QualifiedNamesCalculator {

  @Override
  public Set<String> calculateQualifiedNames(String name, String packageName, List<ImportStatement> imports) {
    final Set<String> potentialSymbolNames = new LinkedHashSet<>();

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
        CommonQualifiedNamesCalculator.class.getSimpleName());

    return potentialSymbolNames;
  }
}
