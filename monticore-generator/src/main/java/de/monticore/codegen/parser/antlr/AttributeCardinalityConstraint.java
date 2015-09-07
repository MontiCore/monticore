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

package de.monticore.codegen.parser.antlr;


import java.util.Optional;

import de.monticore.codegen.parser.ParserGeneratorHelper;
import de.monticore.grammar.HelperGrammar;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTNonTerminal;
import de.monticore.languages.grammar.MCAttributeSymbol;
import de.monticore.languages.grammar.MCGrammarSymbol;
import de.monticore.languages.grammar.MCRuleSymbol;
import de.monticore.languages.grammar.MCTypeSymbol;

/**
 * 
 * MinMax-constraint checks
 *
 */
public class AttributeCardinalityConstraint {
  
  protected ParserGeneratorHelper parserGenHelper;
  
  protected MCGrammarSymbol symbolTable;
  
  public AttributeCardinalityConstraint(ParserGeneratorHelper parserGenHelper) {
    this.parserGenHelper = parserGenHelper;
    this.symbolTable = parserGenHelper.getGrammarSymbol();
  }
  
  public String addActionForRuleBeforeRuleBody(ASTClassProd ast) {
    StringBuilder ret = new StringBuilder();
    MCTypeSymbol definedType = symbolTable.getRuleWithInherited(ast).getDefinedType();
    
    for (MCAttributeSymbol att : definedType.getAttributes()) {
      String usageName = att.getName();
      if (att.isMinCheckedDuringParsing() || att.isMaxCheckedDuringParsing()) {
        if (att.getMin() != MCAttributeSymbol.UNDEF || att.getMax() != MCAttributeSymbol.UNDEF) {
          ret.append("\n" + "int " + getCounterName(usageName) + "=0;");
        }
      }
    }
    
    return ret.toString();
  }
  
  public String addActionForRuleAfterRuleBody(ASTClassProd ast) {
    StringBuilder ret = new StringBuilder();
    MCTypeSymbol definedType = symbolTable.getRuleWithInherited(ast).getDefinedType();
    for (MCAttributeSymbol att : definedType.getAttributes()) {
      
      String usageName = att.getName();
      
      if ((att.getMin() != MCAttributeSymbol.UNDEF || att.getMax() != MCAttributeSymbol.UNDEF)) {
        if (att.getMin() != MCAttributeSymbol.UNDEF) {
          
          if (att.isMinCheckedDuringParsing()) {
            String runtimemessage = "\"Invalid minimal occurence for %attributename% in rule %rulename% : Should be %reference% but is \"+%value%+\"!\"";
            
            runtimemessage = runtimemessage.replaceAll("%attributename%", usageName);
            runtimemessage = runtimemessage.replaceAll("%rulename%", HelperGrammar.getRuleName(ast));
            runtimemessage = runtimemessage.replaceAll("%value%", getCounterName(usageName));
            runtimemessage = runtimemessage.replaceAll("%reference%", format(att.getMin()));
            
            String message = "if (!checkMin("
                + getCounterName(usageName)
                + ","
                + att.getMin()
                + ")) { String message = "
                + runtimemessage
                + ";\n"
                + "de.se_rwth.commons.logging.Log.error(message);\nsetErrors(true);}\n";
            ret.append("\n" + message);
          }
        }
        
        if (att.getMax() != MCAttributeSymbol.UNDEF && att.getMax() != MCAttributeSymbol.STAR) {
          
          if (att.isMaxCheckedDuringParsing()) {
            
            String runtimemessage = "\"Invalid maximal occurence for %attributename% in rule %rulename% : Should be %reference% but is \"+%value%+\"!\"";
            
            runtimemessage = runtimemessage.replaceAll("%attributename%", usageName);
            runtimemessage = runtimemessage.replaceAll("%rulename%", HelperGrammar.getRuleName(ast));
            runtimemessage = runtimemessage.replaceAll("%value%", getCounterName(usageName));
            runtimemessage = runtimemessage.replaceAll("%reference%", format(att.getMax()));
            
            String message = "if (!checkMax("
                + getCounterName(usageName)
                + ","
                + att.getMax()
                + ")) {"
                + " String message = "
                + runtimemessage
                + ";\n"
                + "de.se_rwth.commons.logging.Log.error(message);setErrors(true);}\n";
            ret.append("\n" + message);
          }
        }
      }
    }
    
    return ret.toString();
  }
  
  public String addActionForNonTerminal(ASTNonTerminal ast) {
    StringBuilder ret = new StringBuilder();
    if (!ast.getVariableName().isPresent()) {
      
      String usageName = HelperGrammar.getUsuageName(ast);
      
      Optional<MCRuleSymbol> rule = parserGenHelper.getMCRuleForThisComponent(ast);
      if (!rule.isPresent()) {
        return ret.toString();
      }
      
      MCAttributeSymbol att = rule.get().getDefinedType().getAttribute(usageName);

      if (att.isMinCheckedDuringParsing() || att.isMaxCheckedDuringParsing()) {
        if (att.getMin() != MCAttributeSymbol.UNDEF || att.getMax() != MCAttributeSymbol.UNDEF) {
          ret.append(getCounterName(usageName) + "++;");
        }
      }
    }
    
    return ret.toString();
  }
  
  private String format(int minFor) {
    if (minFor == MCAttributeSymbol.UNDEF) {
      return "undef";
    }
    else if (minFor == MCAttributeSymbol.STAR) {
      return "*";
    }
    else {
      return Integer.toString(minFor);
    }
  }
  
  private String getCounterName(String name) {
    return "_mccounter" + name;
  }
  
}
