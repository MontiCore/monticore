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

package de.monticore.languages.grammar.attributeinfos;

import java.util.List;

import de.monticore.grammar.HelperGrammar;
import de.monticore.grammar.grammar._ast.ASTAlt;
import de.monticore.grammar.grammar._ast.ASTAnything;
import de.monticore.grammar.grammar._ast.ASTBlock;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTConstant;
import de.monticore.grammar.grammar._ast.ASTConstantGroup;
import de.monticore.grammar.grammar._ast.ASTConstantsGrammar;
import de.monticore.grammar.grammar._ast.ASTEof;
import de.monticore.grammar.grammar._ast.ASTMCAnything;
import de.monticore.grammar.grammar._ast.ASTNonTerminal;
import de.monticore.grammar.grammar._ast.ASTNonTerminalSeparator;
import de.monticore.grammar.grammar._ast.ASTRuleComponent;
import de.monticore.grammar.grammar._ast.ASTSemanticpredicateOrAction;
import de.monticore.grammar.grammar._ast.ASTTerminal;
import de.monticore.languages.grammar.MCGrammarSymbol;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;

/**
 * Creates {@link MCAttributeInfo}s using {@link ASTRuleComponent} nodes and stores the attributes in an
 * {@link MCAttributeInfoMap}.
 *
 * @author krahn, Mir Seyed Nazari
 *
*/
public class MCAttributeInfoCalculator {

  public static MCAttributeInfoMap calculateRuleComponentAttributes(ASTRuleComponent ruleComponent, MCGrammarSymbol grammar) {
    Log.errorIfNull(ruleComponent);

    if (ruleComponent instanceof ASTAnything) {
      return calculateAttributes((ASTAnything) ruleComponent);
    }
    if (ruleComponent instanceof ASTBlock) {
      return calculateAttributes((ASTBlock) ruleComponent, grammar);
    }
    if (ruleComponent instanceof ASTConstantGroup) {
      return calculateAttributes((ASTConstantGroup) ruleComponent);
    }
    if (ruleComponent instanceof ASTEof) {
      return calculateAttributes((ASTEof) ruleComponent);
    }
    if (ruleComponent instanceof List) {
      return null; // nothing to do
    }
    if (ruleComponent instanceof ASTMCAnything) {
      return calculateAttributes((ASTMCAnything) ruleComponent);
    }
    if (ruleComponent instanceof ASTNonTerminal) {
      return calculateAttributes((ASTNonTerminal) ruleComponent);
    }
    if (ruleComponent instanceof ASTSemanticpredicateOrAction) {
      return calculateAttributes((ASTSemanticpredicateOrAction) ruleComponent);
    }
    if (ruleComponent instanceof ASTTerminal) {
      return calculateAttributes((ASTTerminal) ruleComponent);
    }
    if (ruleComponent instanceof ASTNonTerminalSeparator) {
      return calculateAttributes((ASTNonTerminalSeparator) ruleComponent);
    }

    throw new IllegalArgumentException("0xA4079 Unknown rule component type '" + ruleComponent.getClass() + "'.");
  }

  public static MCAttributeInfoMap calculateAttributes(ASTClassProd a, MCGrammarSymbol grammar) {
    MCAttributeInfoMap m = null;

    for (ASTAlt r : a.getAlts()) {
      MCAttributeInfoMap calculateAttributes = MCAttributeInfoCalculator.calculateAttributes(r,
          grammar); //r.calculateAttributes(grammar);
      if (m == null) {
        m = calculateAttributes;
      }
      else {
        m = m.alternate(calculateAttributes);
      }
    }
    if (m == null) {
      m = new MCAttributeInfoMap();
    }

    return m;
  }
  
  public static MCAttributeInfoMap calculateAttributes(ASTBlock block, MCGrammarSymbol grammar) {
    MCAttributeInfoMap m = new MCAttributeInfoMap();
    
    for (ASTAlt r : block.getAlts()) {
      m = m.alternate(MCAttributeInfoCalculator.calculateAttributes(r, grammar));
    }
    
    if (block.getIteration() == ASTConstantsGrammar.STAR) {
      m = m.iterateStar();
    }
    if (block.getIteration() == ASTConstantsGrammar.PLUS) {
      m = m.iteratePlus();
    }
    if (block.getIteration() == ASTConstantsGrammar.QUESTION) {
      m = m.iterateOptional();
    }
    
    return m;
  }
  
  public static MCAttributeInfoMap calculateAttributes(ASTAlt alt, MCGrammarSymbol grammar) {
    MCAttributeInfoMap m = new MCAttributeInfoMap();
    
    for (ASTRuleComponent r : alt.getComponents()) {
      if (r == null) {
        // Actually, this case should never happen.
        continue;
      }
      m = m.sequence(MCAttributeInfoCalculator.calculateRuleComponentAttributes(r, grammar));
    }
    
    return m;
  }
  
  public static MCAttributeInfoMap calculateAttributes(ASTConstantGroup a) {
    final MCAttributeInfo att = new MCAttributeInfo();

    setAttributeMinMax(a.getIteration(), att);

    String name = "";

    if (a.getVariableName().isPresent() || a.getUsageName().isPresent()) {
      name = a.getVariableName().orElse(a.getUsageName().get());

      for (ASTConstant x : a.getConstants()) {
        String humanName = x.getHumanName().orElse(null);
        if (humanName == null) {
          humanName = x.getName();
        }
        att.addConstantValue(humanName);
      }
    }
    else {
      // derive attribute name from constant entry (but only if we have
      // one entry!)
      if (a.getConstants().size() == 1) {
        ASTConstant astConstant = a.getConstants().get(0);
        name = HelperGrammar.getAttributeNameForConstant(astConstant);
      }
    }

    att.setName(name);

    MCAttributeInfoMap attrMap = new MCAttributeInfoMap();

    if (!a.getVariableName().isPresent()) {
      attrMap.putAttribute(att);
    }
    else {
      attrMap.putVariable(att);
    }

    return attrMap;
  }

  private static void setAttributeMinMax(int iteration, MCAttributeInfo att) {
    att.setMin(1);
    att.setMax(1);

    if (iteration == ASTConstantsGrammar.STAR
        || iteration == ASTConstantsGrammar.QUESTION) {
      att.setMin(0);
    }
    if (iteration == ASTConstantsGrammar.STAR
        || iteration == ASTConstantsGrammar.PLUS) {
      att.setMax(MCAttributeInfo.STAR);
    }
  }
  
  public static MCAttributeInfoMap calculateAttributes(ASTTerminal a) {
    final MCAttributeInfo att = new MCAttributeInfo();
    setAttributeMinMax(a.getIteration(), att);
    att.setReferencesConstantTerminal(true);

    final MCAttributeInfoMap attrMap = new MCAttributeInfoMap();

    if (a.getVariableName().isPresent()) {
      att.setName(a.getVariableName().get());
      attrMap.putVariable(att);
    }
    else if (a.getUsageName().isPresent()) {
      att.setName(a.getUsageName().get());
      attrMap.putAttribute(att);
    }
    
    return attrMap;
  }
  
  /**
   * A action or predicate defines no attributes
   * 
   */
  public static MCAttributeInfoMap calculateAttributes(ASTEof eof) {
    return MCAttributeInfoMap.getEmptyMap();
  }
  
  public static MCAttributeInfoMap calculateAttributes(ASTNonTerminalSeparator a) {
    return MCAttributeInfoMap.getEmptyMap();
  }
  
  public static MCAttributeInfoMap calculateAttributes(ASTNonTerminal a) {
    final MCAttributeInfo att = new MCAttributeInfo();
    setAttributeMinMax(a.getIteration(), att);
    att.addReferencedRule(a.getName());

    MCAttributeInfoMap attrMap = new MCAttributeInfoMap();

    if (a.getVariableName().isPresent()) {
      att.setName(a.getVariableName().get());
      attrMap.putVariable(att);
    }
    else {
      att.setName(a.getUsageName().orElse(StringTransformations.uncapitalize(a.getName())));
      attrMap.putAttribute(att);
    }

    return attrMap;
  }
  
  public static MCAttributeInfoMap calculateAttributes(ASTSemanticpredicateOrAction sematicprecicateoraction) {
    return MCAttributeInfoMap.getEmptyMap();
  }
  
  public static MCAttributeInfoMap calculateAttributes(ASTMCAnything anything) {
    return MCAttributeInfoMap.getEmptyMap();
  }
  
  public static MCAttributeInfoMap calculateAttributes(ASTAnything anything) {
    return MCAttributeInfoMap.getEmptyMap();
  }
  
}
