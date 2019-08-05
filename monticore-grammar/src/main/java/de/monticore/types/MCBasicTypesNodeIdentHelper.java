package de.monticore.types;

import de.monticore.generating.templateengine.reporting.commons.Layouter;
import de.monticore.literals.LiteralsNodeIdentHelper;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;

public class MCBasicTypesNodeIdentHelper extends LiteralsNodeIdentHelper {
  protected String unqualName(ASTMCQualifiedName ast){
    return ast.getBaseName();
  }

  public String getIdent(ASTMCPrimitiveType a){
    int p = a.getPrimitive();
    return format(String.valueOf(p), Layouter.nodeName(a));
  }

  public String getIdent(ASTMCQualifiedName a){
    return format(unqualName(a),Layouter.nodeName(a));
  }

}
