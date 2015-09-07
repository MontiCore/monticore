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

package de.monticore.codegen.mc2cd.transl;

import java.util.Optional;
import java.util.function.UnaryOperator;

import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.languages.grammar.MCRuleSymbol;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.utils.Link;

/**
 * Adds the CDClass corresponding to another rule 'X' as a superclass to the CDClass corresponding
 * to 'Y' if 'Y' overwrites 'X'.
 * 
 * @author Sebastian Oberhoff
 */
public class OverridingClassProdTranslation implements
    UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {
  
  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {
    
    for (Link<ASTClassProd, ASTCDClass> link : rootLink.getLinks(ASTClassProd.class,
        ASTCDClass.class)) {
      
      Optional<MCRuleSymbol> ruleSymbol = MCGrammarSymbolTableHelper.resolveRuleInSupersOnly(
          rootLink.source(),
          link.source().getName());
      if (ruleSymbol.isPresent() && !ruleSymbol.get().getDefinedType().isExternal()) {
        String qualifiedASTNodeName = TransformationHelper.getAstPackageName(ruleSymbol.get()
            .getGrammarSymbol()) + "AST" + ruleSymbol.get().getName();
        link.target().setSuperclass(
            TransformationHelper.createSimpleReference(qualifiedASTNodeName));
      }
      
    }
    
    return rootLink;
  }
}
