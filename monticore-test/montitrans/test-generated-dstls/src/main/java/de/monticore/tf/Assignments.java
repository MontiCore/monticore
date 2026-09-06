/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

import java.util.List;

/**
 * This class does nothing except checking, that the TOP mechanism for TFGen
 * works correctly (by not failing to compile)
 */
public class Assignments extends AssignmentsTOP {
  public Assignments(List<ASTNode> hostGraph) {
    super(hostGraph);
  }

  public Assignments(ASTNode... hostGraph) {
    super(hostGraph);
  }

  public Assignments(GlobalExtensionManagement glex, ASTNode... hostGraph) {
    super(glex, hostGraph);
  }

  public Assignments(GlobalExtensionManagement glex, ASTNode astNode) {
    super(glex, astNode);
  }

  public Assignments(ASTNode astNode) {
    super(astNode);
  }

  public Assignments(ASTNode astNode, GlobalExtensionManagement glex) {
    super(astNode, glex);
  }
}
