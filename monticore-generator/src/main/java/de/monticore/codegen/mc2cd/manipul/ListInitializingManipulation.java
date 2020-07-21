/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.manipul;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.utils.ASTNodes;

import java.util.function.UnaryOperator;

import static de.monticore.codegen.mc2cd.TransformationHelper.simpleName;

public class ListInitializingManipulation implements UnaryOperator<ASTCDCompilationUnit> {
  
  @Override
  public ASTCDCompilationUnit apply(ASTCDCompilationUnit cdCompilationUnit) {
    
    for (ASTCDAttribute cdAttribute : ASTNodes.getSuccessors(cdCompilationUnit,
        ASTCDAttribute.class)) {
      if ("List".equals(simpleName( cdAttribute.getMCType()))) {
        // TODO: Implement
      }
    }
    
    return cdCompilationUnit;
  }
}
