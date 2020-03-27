/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.monticore.types.mcfullgenerictypes._ast.MCFullGenericTypesMill;
import de.se_rwth.commons.logging.Log;

import java.util.List;

import static de.monticore.grammar.grammar._ast.ASTConstantsGrammar.*;

/**
 * CoCo that checks if in a additional attribute of a astrule, symbolrule or scoperule a generic type does not contain a MCCustomTypeArgument
 * with the *,+,? and min, max notation this can be created in different ways
 * these cases are covered e.g.:
 * A<B<C>>
 * A<B>*, A<B>+, A<B>?
 * A<B> min = 0, A<B> max = 5, A<B> max = *
 * because these cases cannot be handled in the generator so far and will generate the wrong type
 */
public class NoNestedGenericsInAdditionalAttributes implements GrammarASTMCGrammarCoCo {

  public static final String ERROR_CODE = "0xA4102";

  public static final String ERROR_MSG_FORMAT = " %srule does not allow the definition of nested generics. " +
      "Problem in grammar '%s', rule for '%s', with additional attribute: '%s'.";

  @Override
  public void check(ASTMCGrammar node) {
    String grammarName = node.getName();
    // astrule
    for (ASTASTRule astastRule : node.getASTRuleList()) {
      findMultipleGenericAttributes(astastRule.getAdditionalAttributeList(), "Ast", grammarName, astastRule.getType());
    }
    // symbolrule
    for (ASTSymbolRule astSymbolRule : node.getSymbolRuleList()) {
      findMultipleGenericAttributes(astSymbolRule.getAdditionalAttributeList(), "Symbol", grammarName, astSymbolRule.getType());
    }
    // scoperule
    if (node.isPresentScopeRule()) {
      findMultipleGenericAttributes(node.getScopeRule().getAdditionalAttributeList(), "Scope", grammarName, grammarName + "Scope");
    }
  }

  private void findMultipleGenericAttributes(List<ASTAdditionalAttribute> astAdditionalAttributes, String ruleName,
                                             String grammarName, String prodName) {
    for (ASTAdditionalAttribute astAdditionalAttribute : astAdditionalAttributes) {
      ASTMCType mcType = astAdditionalAttribute.getMCType();

      if (mcType instanceof ASTMCGenericType) {
        if (hasNestedGeneric(mcType)) {
          // for e.g. A<B<C>>
          logError(ruleName, grammarName, prodName, astAdditionalAttribute);
        } else if (astAdditionalAttribute.isPresentCard()) {
          if (hasGenericIteration(astAdditionalAttribute)) {
            // for e.g. A<B>*
            logError(ruleName, grammarName, prodName, astAdditionalAttribute);
          } else if (hasGenericMaxValue(astAdditionalAttribute)) {
            // for e.g. A<B> min=0 or A<B> max=2 or A<B> max=*
            logError(ruleName, grammarName, prodName, astAdditionalAttribute);
          }
        }
      }
    }
  }

  /**
   * for e.g. A<B<C>>
   */
  private boolean hasNestedGeneric(ASTMCType mcType){
    return((ASTMCGenericType) mcType).getMCTypeArgumentList()
        .stream()
        .filter(ta -> ta.getMCTypeOpt().isPresent())
        .anyMatch(ta -> ta.getMCTypeOpt().get() instanceof ASTMCGenericType);
  }

  /**
   * for e.g. A<B>*, A<B>+, A<B>?
   */
  private boolean hasGenericIteration(ASTAdditionalAttribute astAdditionalAttribute){
    return astAdditionalAttribute.getCard().getIteration() == STAR || astAdditionalAttribute.getCard().getIteration() == PLUS ||
        astAdditionalAttribute.getCard().getIteration() == QUESTION;
  }

  /**
   * for e.g. A<B> min=0, A<B> max=2, A<B> max=*
   */
  private boolean hasGenericMaxValue(ASTAdditionalAttribute astAdditionalAttribute){
    return (astAdditionalAttribute.getCard().isPresentMax() && ("*".equals(astAdditionalAttribute.getCard().getMax()) ||
        Integer.parseInt(astAdditionalAttribute.getCard().getMax()) > 1) ||
        (astAdditionalAttribute.getCard().isPresentMin() && Integer.parseInt(astAdditionalAttribute.getCard().getMin()) == 0));
  }

  private void logError(String ruleName, String grammarName, String prodName, ASTAdditionalAttribute astAdditionalAttribute) {
    Log.error(ERROR_CODE + String.format(ERROR_MSG_FORMAT, ruleName, grammarName, prodName,
        printASTAdditionalAttribute(astAdditionalAttribute)));
  }

  private String printASTAdditionalAttribute(ASTAdditionalAttribute astAdditionalAttribute) {
    String attribute = "";
    if (astAdditionalAttribute.isPresentName()) {
      attribute += astAdditionalAttribute.getName() + ":";
    }
    attribute += MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter().prettyprint(astAdditionalAttribute.getMCType());
    if (astAdditionalAttribute.isPresentCard()) {
      ASTCard card = astAdditionalAttribute.getCard();
      if (card.getIteration() == STAR) {
        attribute += "*";
      } else if (card.getIteration() == PLUS) {
        attribute += "+";
      } else if (card.getIteration() == QUESTION) {
        attribute += "?";
      }
      if (card.isPresentMin()) {
        attribute += " min=" + card.getMin();
      }
      if (card.isPresentMax()) {
        attribute += " max=" + card.getMax();
      }
    }
    return attribute;
  }
}
