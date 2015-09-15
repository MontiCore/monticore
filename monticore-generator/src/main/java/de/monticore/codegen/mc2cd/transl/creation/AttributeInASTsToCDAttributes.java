/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
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

package de.monticore.codegen.mc2cd.transl.creation;

import java.util.function.UnaryOperator;

import de.monticore.grammar.grammar._ast.ASTASTRule;
import de.monticore.grammar.grammar._ast.ASTAttributeInAST;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisNodeFactory;
import de.monticore.utils.ASTNodes;
import de.monticore.utils.Link;

public class AttributeInASTsToCDAttributes implements
    UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {
  
  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {
    
    for (Link<ASTASTRule, ASTCDClass> link : rootLink.getLinks(ASTASTRule.class,
        ASTCDClass.class)) {
      for (ASTAttributeInAST attributeInAST : ASTNodes.getSuccessors(link.source(),
          ASTAttributeInAST.class)) {
        ASTCDAttribute cdAttribute = CD4AnalysisNodeFactory.createASTCDAttribute();
        link.target().getCDAttributes().add(cdAttribute);
        new Link<>(attributeInAST, cdAttribute, link);
      }
    }
    
    for (Link<ASTASTRule, ASTCDInterface> link : rootLink.getLinks(ASTASTRule.class,
        ASTCDInterface.class)) {
      for (ASTAttributeInAST attributeInAST : ASTNodes.getSuccessors(link.source(),
          ASTAttributeInAST.class)) {
        ASTCDAttribute cdAttribute = CD4AnalysisNodeFactory.createASTCDAttribute();
        link.target().getCDAttributes().add(cdAttribute);
        new Link<>(attributeInAST, cdAttribute, link);
      }
    }
    
    return rootLink;
  }
  
}
