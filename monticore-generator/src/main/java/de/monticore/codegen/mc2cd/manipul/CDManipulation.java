/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.manipul;

import java.util.function.UnaryOperator;

import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;

/**
 * The term manipulation refers to transformations that write to the same AST that they're reading
 * from.
 * <p>
 * This function composes all CD manipulations into a single function.
 * 
 * @author Sebastian Oberhoff
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
