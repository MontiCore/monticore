/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.manipul;

import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDDefinition;
import de.monticore.cd.cd4analysis._ast.ASTCDInterface;
import de.monticore.cd.cd4analysis._ast.CD4AnalysisNodeFactory;
import de.monticore.codegen.mc2cd.TransformationHelper;

import java.util.function.UnaryOperator;

/**
 * Adds the base interface for all AST nodes of the handled grammar
 * 
 */
public final class BaseInterfaceAddingManipulation implements UnaryOperator<ASTCDCompilationUnit> {
  
  @Override
  public ASTCDCompilationUnit apply(ASTCDCompilationUnit cdCompilationUnit) {
    ASTCDInterface baseInterface = CD4AnalysisNodeFactory.createASTCDInterface();
    baseInterface.setName(getBaseInterfaceName(cdCompilationUnit.getCDDefinition()));
  //  baseInterface.getInterfaces().add(TransformationHelper.createSimpleReference("ASTNode"));
    cdCompilationUnit.getCDDefinition().getCDInterfaceList().add(baseInterface); 
    return cdCompilationUnit;
  }
  
  public static String getBaseInterfaceName(ASTCDDefinition astcdDefinition) {
    return TransformationHelper.AST_PREFIX + astcdDefinition.getName() + "Node";
  }
  
}
