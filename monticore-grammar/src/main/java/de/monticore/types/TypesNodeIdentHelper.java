/* (c) https://github.com/MontiCore/monticore */
 
package de.monticore.types;

import java.util.List;

import de.monticore.generating.templateengine.reporting.commons.Layouter;
import de.monticore.literals.LiteralsNodeIdentHelper;
import de.monticore.types.types._ast.ASTPrimitiveType;
import de.monticore.types.types._ast.ASTQualifiedName;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.types.types._ast.ASTTypeParameters;
import de.monticore.types.types._ast.ASTTypeVariableDeclaration;

/**
 * @author MB
 */
public class TypesNodeIdentHelper extends LiteralsNodeIdentHelper {
  
  protected String unqualName(ASTQualifiedName ast) {
    List<String> parts = ast.getPartList();
    return parts.get(parts.size() - 1);
  }
  
  public String getIdent(ASTPrimitiveType a) {
    int p = a.getPrimitive();
    return format(String.valueOf(p), Layouter.nodeName(a));
  }
  
  /* The "local" QualifiedName-class gets a short explanation */
  public String getIdent(ASTQualifiedName a) {
    return format(unqualName(a),  Layouter.nodeName(a));
  }
  
  public String getIdent(ASTSimpleReferenceType a) {
    StringBuilder name = new StringBuilder();
    for (int i = 0; i < a.getNameList().size(); i++) {
      name.append(a.getNameList().get(i));
      if (i != a.getNameList().size() - 1) {
        name.append(".");
      }
    }
    return format(name.toString(), Layouter.nodeName(a));
  }
  
  public String getIdent(ASTTypeParameters a) {
    List<ASTTypeVariableDeclaration> l = a.getTypeVariableDeclarationList();
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
