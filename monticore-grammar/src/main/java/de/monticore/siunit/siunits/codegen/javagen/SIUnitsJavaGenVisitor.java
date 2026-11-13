/* (c) https://github.com/MontiCore/monticore */
package de.monticore.siunit.siunits.codegen.javagen;

import de.monticore.siunit.siunits._ast.ASTSIUnit;
import de.monticore.siunit.siunits._visitor.SIUnitsInheritanceHandler;

/**
 * SIUnits are not represented at runtime;
 * nothing is printed for, e.g., the m^2 of {@code [m^2]<double>}.
 * The types and literals containing the SIUnits are printed by
 * their respective visitors.
 */
public class SIUnitsJavaGenVisitor extends SIUnitsInheritanceHandler {

  @Override
  public void traverse(ASTSIUnit node) {
    // do not print the exponents
  }

}
