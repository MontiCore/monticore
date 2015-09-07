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

import java.util.function.UnaryOperator;

import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTNonTerminal;
import de.monticore.languages.grammar.MCGrammarSymbol;
import de.monticore.languages.grammar.MCRuleComponentSymbol;
import de.monticore.languages.grammar.MCRuleSymbol;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.utils.Link;

/**
 * Creates the ASTCDAttributes corresponding to NonTerminals
 *
 * @author Sebastian Oberhoff
 */
class NonTerminalsWithSymbolReferenceToCDAttributeStereotypes implements
    UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {

  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {

    for (Link<ASTNonTerminal, ASTCDAttribute> link : rootLink.getLinks(ASTNonTerminal.class, ASTCDAttribute.class)) {
      final ASTNonTerminal nonTerminal = link.source();
      final ASTCDAttribute cdAttribute = link.target();

      if (nonTerminal.referencedSymbolIsPresent()) {
        final MCRuleComponentSymbol ruleComponentSymbol = (MCRuleComponentSymbol) nonTerminal.getSymbol().get();
        final MCGrammarSymbol grammarSymbol = ruleComponentSymbol.getGrammarSymbol();

        final MCRuleSymbol referencedSymbol = grammarSymbol.getRuleWithInherited(nonTerminal.getReferencedSymbol().get());

        if (referencedSymbol != null) {
          final String referencedSymbolName =
              referencedSymbol.getGrammarSymbol().getFullName() + "." + referencedSymbol.getName() + "Symbol";

          TransformationHelper.addStereoType(cdAttribute, "referencedSymbol", referencedSymbolName);
        }
      }
    }

    return rootLink;
  }

}
