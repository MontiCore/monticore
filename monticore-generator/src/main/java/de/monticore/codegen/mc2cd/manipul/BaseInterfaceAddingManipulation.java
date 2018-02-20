/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.manipul;

import java.util.function.UnaryOperator;

import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDDefinition;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisNodeFactory;

/**
 * Adds the base interface for all AST nodes of the handled grammar
 * 
 * @author Galina Volkova
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
