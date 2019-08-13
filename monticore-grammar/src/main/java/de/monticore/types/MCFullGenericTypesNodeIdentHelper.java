/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types;

import de.monticore.generating.templateengine.reporting.commons.Layouter;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCTypeParameters;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCTypeVariableDeclaration;

import java.util.List;

public class MCFullGenericTypesNodeIdentHelper extends MCSimpleGenericTypesNodeIdentHelper {
  public String getIdent(ASTMCTypeParameters a) {
    List<ASTMCTypeVariableDeclaration> l = a.getMCTypeVariableDeclarationList();
    String n = "";
    if (l.isEmpty()) {
      n += "-";
    }
    if (!l.isEmpty()) {
      n += l.get(0).getName();
    }
    if (l.size() > 1) {
      n += "..";
    }
    return format(n, Layouter.nodeName(a));
  }
}
