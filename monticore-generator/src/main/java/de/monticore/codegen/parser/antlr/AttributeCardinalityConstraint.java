/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.parser.antlr;

import java.util.Optional;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.codegen.parser.ParserGeneratorHelper;
import de.monticore.grammar.HelperGrammar;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTNonTerminal;
import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.monticore.grammar.symboltable.MCProdAttributeSymbol;
import de.monticore.grammar.symboltable.MCProdSymbol;

/**
 * MinMax-constraint checks
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
    Optional<MCProdSymbol> prodSymbol = symbolTable.getProdWithInherited(ast.getName());
    if (prodSymbol.isPresent()) {
      for (MCProdAttributeSymbol att : prodSymbol.get().getProdAttributes()) {
        String usageName = att.getName();
        if (MCGrammarSymbolTableHelper.getMax(att).isPresent()
            || MCGrammarSymbolTableHelper.getMin(att).isPresent()) {
          ret.append("\n" + "int " + getCounterName(usageName) + "=0;");
        }
      }
    }
    return ret.toString();
  }
  
  public String addActionForRuleAfterRuleBody(ASTClassProd ast) {
    StringBuilder ret = new StringBuilder();
    Optional<MCProdSymbol> prodSymbol = symbolTable.getProdWithInherited(ast.getName());
    if (!prodSymbol.isPresent()) {
      return ret.toString();
    }
    for (MCProdAttributeSymbol att : prodSymbol.get().getProdAttributes()) {
      
      String usageName = att.getName();
      Optional<Integer> min = MCGrammarSymbolTableHelper.getMin(att);
      Optional<Integer> max = MCGrammarSymbolTableHelper.getMax(att);
      if (min.isPresent() || max.isPresent()) {
        if (min.isPresent()) {
          
          String runtimemessage = "\"0xA7017\" + de.monticore.codegen.GeneratorHelper.getGeneratedErrorCode(_aNode) + \" Invalid minimal occurence for %attributename% in rule %rulename% : Should be %reference% but is \"+%value%+\"!\"";
          
          runtimemessage = runtimemessage.replaceAll("%attributename%", usageName);
          runtimemessage = runtimemessage.replaceAll("%rulename%", HelperGrammar.getRuleName(ast));
          runtimemessage = runtimemessage.replaceAll("%value%", getCounterName(usageName));
          runtimemessage = runtimemessage.replaceAll("%reference%",
              ParserGeneratorHelper.formatAttributeValue(min));
          
          String message = "if (!checkMin("
              + getCounterName(usageName)
              + ","
              + min.get()
              + ")) { String message = "
              + runtimemessage
              + ";\n"
              + "de.se_rwth.commons.logging.Log.error(message);\nsetErrors(true);}\n";
          ret.append("\n" + message);
        }
        
        if (max.isPresent() && max.get() != GeneratorHelper.STAR) {
          
          String runtimemessage = "\"0xA7018\" + de.monticore.codegen.GeneratorHelper.getGeneratedErrorCode(_aNode) + \" Invalid maximal occurence for %attributename% in rule %rulename% : Should be %reference% but is \"+%value%+\"!\"";
          
          runtimemessage = runtimemessage.replaceAll("%attributename%", usageName);
          runtimemessage = runtimemessage.replaceAll("%rulename%", HelperGrammar.getRuleName(ast));
          runtimemessage = runtimemessage.replaceAll("%value%", getCounterName(usageName));
          runtimemessage = runtimemessage.replaceAll("%reference%",
              ParserGeneratorHelper.formatAttributeValue(max));
          
          String message = "if (!checkMax("
              + getCounterName(usageName)
              + ","
              + max.get()
              + ")) {"
              + " String message = "
              + runtimemessage
              + ";\n"
              + "de.se_rwth.commons.logging.Log.error(message);setErrors(true);}\n";
          ret.append("\n" + message);
        }
      }
    }
    
    return ret.toString();
  }
  
  public String addActionForNonTerminal(ASTNonTerminal ast) {
    StringBuilder ret = new StringBuilder();
    
    String usageName = HelperGrammar.getUsuageName(ast);
    
    Optional<MCProdSymbol> rule = MCGrammarSymbolTableHelper.getEnclosingRule(ast);
    if (!rule.isPresent()) {
      return ret.toString();
    }
    
    Optional<MCProdAttributeSymbol> att = rule.get().getProdAttribute(usageName);
    if (att.isPresent() && (MCGrammarSymbolTableHelper.getMax(att.get()).isPresent()
        || MCGrammarSymbolTableHelper.getMin(att.get()).isPresent())) {
      ret.append(getCounterName(usageName) + "++;");
    }
    return ret.toString();
  }
  
  private static String getCounterName(String name) {
    return "_mccounter" + name;
  }
  
}
