/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.manipul;

import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;

import java.util.function.UnaryOperator;


/**
 * The term manipulation refers to transformations that write to the same AST that they're reading
 * from.
 * <p>
 * This function composes all CD manipulations into a single function.
 * 
 */
public final class CDManipulation implements UnaryOperator<ASTCDCompilationUnit> {
  
  @Override
  public ASTCDCompilationUnit apply(ASTCDCompilationUnit cdCompilationUnit) {
    return new BaseInterfaceAddingManipulation()
        .andThen(new AddAttributesOfExtendedInterfacesManipulation())
        .andThen(new RemoveRedundantAttributesManipulation())
        .andThen(new RemoveRedundantSupertypesManipulation())
        .andThen(new JavaAndCdConformNameManipulation())
        .andThen(new VisibilityManipulation())
        .apply(cdCompilationUnit);
  }
}
