/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */
 
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
    List<String> parts = ast.getParts();
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
    for (int i = 0; i < a.getNames().size(); i++) {
      name.append(a.getNames().get(i));
      if (i != a.getNames().size() - 1) {
        name.append(".");
      }
    }
    return format(name.toString(), Layouter.nodeName(a));
  }
  
  public String getIdent(ASTTypeParameters a) {
    List<ASTTypeVariableDeclaration> l = a.getTypeVariableDeclarations();
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
