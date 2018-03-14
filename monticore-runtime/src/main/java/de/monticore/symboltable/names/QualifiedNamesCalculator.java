/* (c) https://github.com/MontiCore/monticore */


package de.monticore.symboltable.names;

import java.util.List;
import java.util.Set;

import de.monticore.symboltable.ImportStatement;

/**
 * Calculates the possible qualified name for a simple name, considering the package
 * information and import statements.
 *
 * @author Pedram Mir Seyed Nazari
 */
public interface QualifiedNamesCalculator {

  /**
   * Calculates possible qualified names for the <code>simpleName</code>. For this,
   * it considers the (possible) <code>packageName</code> and the <code>imports</code>
   * (i.e., import statements).
   *
   * @param simpleName the simple name of the symbol
   * @param packageName the possible package name
   * @param imports the import statements
   * @return a set of possible qualified names for the <code>simpleName</code>
   */
  Set<String> calculateQualifiedNames(String simpleName, String packageName, List<ImportStatement> imports);

}
