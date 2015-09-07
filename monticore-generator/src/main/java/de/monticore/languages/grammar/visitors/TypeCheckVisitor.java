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

package de.monticore.languages.grammar.visitors;

import static de.monticore.languages.grammar.MCRuleSymbol.KindSymbolRule.INTERFACEORABSTRACTRULE;

import java.util.List;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTNonTerminal;
import de.monticore.grammar.grammar._ast.ASTRuleReference;
import de.monticore.grammar.grammar_withconcepts._visitor.Grammar_WithConceptsVisitor;
import de.monticore.languages.grammar.MCGrammarSymbol;
import de.monticore.languages.grammar.MCRuleSymbol;
import de.monticore.languages.grammar.PredicatePair;
import de.se_rwth.commons.logging.Log;

/**
 * PN: scheint während des Symtab-Aufbaus Checks zu machen, z.B. ob Non-Terminal von Super
 *     existieren, wie z.B. Name. => TODO NN <- PN als Coco implementieren??
 * 
 * TODO: Write me!
 *
 * @author  Pedram Mir Seyed Nazari
 * @version $Revision$,
 *          $Date$
 *
 */
public class TypeCheckVisitor implements Grammar_WithConceptsVisitor {
  
  private final MCGrammarSymbol grammarSymbol;
  
  private boolean fatalError = false;

  public TypeCheckVisitor(MCGrammarSymbol grammarEntry) {
    this.grammarSymbol = Log.errorIfNull(grammarEntry);
  }
  
  @Override
  public void visit(ASTRuleReference a) {
    if (grammarSymbol.getRuleWithInherited(a.getName()) == null) {
      Log.error("0xA0273 Undefined rule: " + a.getName() + ". Position: " + a
          .get_SourcePositionStart());
      fatalError = true;
    }
  }
  
  @Override
  public void visit(ASTNonTerminal a) {

    MCRuleSymbol ruleByName = grammarSymbol.getRuleWithInherited(a.getName());
    if (ruleByName == null) {
      Log.error("0xA0274 Undefined rule: " + a.getName() + ". Position: " + a.get_SourcePositionStart());
      fatalError = true;
    }
    else {
      if (grammarSymbol.isComponent()) {
        // Don't run this check for abstract grammars
        return;
      }
      
      if (ruleByName.getKindSymbolRule().equals(INTERFACEORABSTRACTRULE)) {
        
        List<PredicatePair> subRulesForParsing = grammarSymbol.getSubRulesForParsing(a.getName());
        if (subRulesForParsing == null || subRulesForParsing.size() == 0) {
          Log.error("0xA0275 Rule '" + a.getName() + "' has no body and can therefore not be "
              + "referenced! Position: " + a.get_SourcePositionStart());
          fatalError = true;
        }
      }
    }
  }
  
  public boolean hasFatalError() {
    return fatalError;
  }
  
  public void check(ASTMCGrammar ast) {
    ast.accept(getRealThis());
  }
  
}
