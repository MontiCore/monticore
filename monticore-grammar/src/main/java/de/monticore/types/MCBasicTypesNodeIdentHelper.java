/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types;

import de.monticore.generating.templateengine.reporting.commons.ASTNodeIdentHelper;
import de.monticore.generating.templateengine.reporting.commons.Layouter;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

public class MCBasicTypesNodeIdentHelper extends ASTNodeIdentHelper {

  public String getIdent(ASTMCPrimitiveType a){
    int p = a.getPrimitive();
    return format(String.valueOf(p), Layouter.nodeName(a));
  }

  public String getIdent(ASTMCType a)
  {
    return format(a.getBaseName(),Layouter.nodeName(a));
  }

  public String getIdent(ASTMCQualifiedName a)
  {
    return format(a.getBaseName(),Layouter.nodeName(a));
  }


}
