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

package de.monticore.codegen.mc2cd.transl.creation;

import java.util.function.UnaryOperator;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDDefinition;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisNodeFactory;
import de.monticore.utils.Link;

/**
 * Creates the CDDefinition corresponding to the ASTMCGrammar
 * 
 * @author Sebastian Oberhoff
 */
class GrammarToCDDefinition implements
    UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {
  
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {
    
    for (Link<ASTMCGrammar, ASTCDCompilationUnit> link : rootLink.getLinks(ASTMCGrammar.class,
        ASTCDCompilationUnit.class)) {
      ASTCDDefinition cdDefinition = CD4AnalysisNodeFactory.createASTCDDefinition();
      link.target().setCDDefinition(cdDefinition);
      new Link<>(link.source(), cdDefinition, link);
    }
    
    return rootLink;
  }
}
