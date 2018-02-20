/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.manipul;

import java.util.function.UnaryOperator;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.utils.ASTNodes;

/**
 * Ensures that attributes are spelled with lower case in order to comply with the standard Java
 * convention.
 * 
 * @author Sebastian Oberhoff
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
