/*
 * ******************************************************************************
 * MontiCore Language Workbench
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

package de.monticore.codegen.mc2cd.transl;

import java.util.function.UnaryOperator;

import de.monticore.grammar.grammar._ast.ASTConstant;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.types.types._ast.ASTConstantsTypes;
import de.monticore.types.types._ast.TypesNodeFactory;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.utils.Link;

public class ConstantTypeTranslation implements
    UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {
  
  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {
    
    for (Link<ASTConstant, ASTCDAttribute> link : rootLink.getLinks(ASTConstant.class,
        ASTCDAttribute.class)) {
      // TODO SO <- GV: fix an attribute type according to this code
//      if (!att.isIterated()) {
//        if (attributeType.getEnumValues().size() > 1) {
//          attribDef.setObjectType("int");
//        }
//        else {
//          attribDef.setObjectType("boolean");
//        }
//      }
//      else {
//        if (attributeType.getEnumValues().size() > 1) {
//          attribDef.setObjectType("java.util.List<Integer>");
//        }
//        else {
//          attribDef.setObjectType("java.util.List<Boolean>");
//        }
//      }
      link.target().setType(TypesNodeFactory.createASTPrimitiveType(ASTConstantsTypes.BOOLEAN));
    }
    
    return rootLink;
  }
}
