/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.manipul;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.GeneratorHelper;
import de.monticore.utils.ASTNodes;

import java.util.function.UnaryOperator;

/**
 * Ensures that attributes are spelled with lower case in order to comply with the standard Java
 * convention.
 * 
 */
public class JavaAndCdConformNameManipulation implements UnaryOperator<ASTCDCompilationUnit> {
  
  @Override
  public ASTCDCompilationUnit apply(ASTCDCompilationUnit cdCompilationUnit) {
    
    for (ASTCDAttribute cdAttribute : ASTNodes.getSuccessors(cdCompilationUnit,
        ASTCDAttribute.class)) {
      
      cdAttribute.setName(GeneratorHelper.getJavaAndCdConformName(cdAttribute.getName()));
    }
    
    return cdCompilationUnit;
  }
}
