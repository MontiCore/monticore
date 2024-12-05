/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.parser.antlr;

import de.monticore.ast.ASTNode;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.codegen.parser.ParserGeneratorHelper;
import de.monticore.grammar.MCGrammarSymbolTableHelper;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTNonTerminal;
import de.monticore.grammar.grammar._symboltable.AdditionalAttributeSymbol;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;

import java.util.Optional;

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
    ProdSymbol prodSymbol = ast.getSymbol();

    for (AdditionalAttributeSymbol att : prodSymbol.getSpannedScope().getLocalAdditionalAttributeSymbols()) {
      String usageName = att.getName();
      if (TransformationHelper.getMax(att).isPresent()
          || MCGrammarSymbolTableHelper.getMin(att).isPresent()) {
        ret.append("int " + getCounterName(usageName) + "=0;\n");
      }
    }
    return ret.toString();
  }

  public String addActionForRuleAfterRuleBody(ASTClassProd ast) {
    StringBuilder ret = new StringBuilder();
    ProdSymbol prodSymbol = ast.getSymbol();
    for (AdditionalAttributeSymbol att : prodSymbol.getSpannedScope().getLocalAdditionalAttributeSymbols()) {

      String usageName = att.getName();
      Optional<Integer> min = MCGrammarSymbolTableHelper.getMin(att);
      Optional<Integer> max = TransformationHelper.getMax(att);
      if (min.isPresent() || max.isPresent()) {
        if (min.isPresent()) {

          String runtimemessage = "\"0xA7017" + getGeneratedErrorCode(ast) + " Invalid minimal occurence for %attributename% in rule %rulename% : Should be %reference% but is \"+%value%+\"!\"";

          runtimemessage = runtimemessage.replaceAll("%attributename%", usageName);
          runtimemessage = runtimemessage.replaceAll("%rulename%", ast.getName());
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

        if (max.isPresent() && max.get() != TransformationHelper.STAR) {

          String runtimemessage = "\"0xA7018" + getGeneratedErrorCode(ast) + " Invalid maximal occurence for %attributename% in rule %rulename% : Should be %reference% but is \"+%value%+\"!\"";

          runtimemessage = runtimemessage.replaceAll("%attributename%", usageName);
          runtimemessage = runtimemessage.replaceAll("%rulename%", ast.getName());
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

    String usageName = parserGenHelper.getUsageName(ast);

    Optional<ProdSymbol> rule = MCGrammarSymbolTableHelper.getEnclosingRule(ast);
    if (!rule.isPresent()) {
      return ret.toString();
    }

    Optional<AdditionalAttributeSymbol> att = rule.get().getSpannedScope().resolveAdditionalAttributeLocally(usageName);
    if (att.isPresent() && (TransformationHelper.getMax(att.get()).isPresent()
        || MCGrammarSymbolTableHelper.getMin(att.get()).isPresent())) {
      ret.append(getCounterName(usageName) + "++;\n");
    }
    return ret.toString();
  }

  protected static String getCounterName(String name) {
    return "_mccounter" + name;
  }

  /**
   * Generates an error code suffix in format "_ddd" where d is a decimal. If
   * there is an ast-name then always the same error code will be generated.
   *
   * @param ast
   * @return generated error code suffix in format "xddd" where d is a decimal.
   */
  protected String getGeneratedErrorCode(ASTNode ast) {
    int hashCode = 0;
    // If there is an ast-name then always generate the same error code.

    hashCode = Math.abs(ast.toString().hashCode());

    String errorCodeSuffix = String.valueOf(hashCode);
    return "x" + (hashCode < 1000 ? errorCodeSuffix : errorCodeSuffix
        .substring(errorCodeSuffix.length() - 3));
  }
}
