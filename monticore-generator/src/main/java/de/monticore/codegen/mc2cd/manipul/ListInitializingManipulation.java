/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.manipul;

import java.util.function.UnaryOperator;

import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.utils.ASTNodes;

public class ListInitializingManipulation implements UnaryOperator<ASTCDCompilationUnit> {
  
  @Override
  public ASTCDCompilationUnit apply(ASTCDCompilationUnit cdCompilationUnit) {
    
    for (ASTCDAttribute cdAttribute : ASTNodes.getSuccessors(cdCompilationUnit,
        ASTCDAttribute.class)) {
      if ("List".equals(((ASTSimpleReferenceType) cdAttribute.getType()).getNameList().get(0))) {
        // TODO: Implement
      }
    }
    
    return cdCompilationUnit;
  }
}
