/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/ 
 */
package de.monticore.types;

import java.util.List;

import de.monticore.generating.templateengine.reporting.commons.ASTNodeIdentHelper;
import de.monticore.generating.templateengine.reporting.commons.Layouter;
import de.monticore.literals.literals._ast.ASTIntLiteral;
import de.monticore.types.types._ast.ASTPrimitiveType;
import de.monticore.types.types._ast.ASTQualifiedName;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.types.types._ast.ASTTypeParameters;
import de.monticore.types.types._ast.ASTTypeVariableDeclaration;

/**
 * @author MB
 */
public class TypesNodeIdentHelper extends ASTNodeIdentHelper {
  
  protected String unqualName(ASTQualifiedName ast) {
    List<String> parts = ast.getParts();
    return parts.get(parts.size() - 1);
  }
  
  public String getIdent(ASTPrimitiveType a) {
    int p = a.getPrimitive();
    return format(String.valueOf(p), Layouter.nodeName(a));
  }
  
  /* The "local" QualifiedName-class gets a short explanation */
  public String getIdent(ASTQualifiedName a) {
    return format(unqualName(a), "!mc.javadsl._ast.ASTQualifiedName");
  }
  
  public String getIdent(ASTSimpleReferenceType a) {
    String name = "";
    for (int i = 0; i < a.getNames().size(); i++) {
      name += a.getNames().get(i);
      if (i != a.getNames().size() - 1) {
        name += ".";
      }
    }
    return format(name, Layouter.nodeName(a));
  }
  
  public String getIdent(ASTTypeParameters a) {
    List<ASTTypeVariableDeclaration> l = a.getTypeVariableDeclarations();
    String n = "-";
    if (l == null) {
      n = "??";
    }
    else {
      if (l.size() == 0) {
        n += "-";
      }
      if (l.size() > 0) {
        n += l.get(0).getName();
      }
      if (l.size() > 1) {
        n += "..";
      }
    }
    return format(n, Layouter.nodeName(a));
  }
  
  // TODO: TGr
  public String getIdent(ASTIntLiteral a) {
    return format(a.getSource(), Layouter.nodeName(a));
  }
  
}
