/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.types.mcfullgenerictypes.MCFullGenericTypesMill;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

/**
 * Checks that nonterminal names start lower-case.
 */
public class NTAndASTRuleExtendType implements GrammarASTMCGrammarCoCo {

  public static final String ERROR_CODE = "0xA4013";

  public static final String ERROR_MSG_FORMAT = " The AST rule for %s must not extend the type " +
          "%s because the production already extends a type.";

  @Override
  public void check(ASTMCGrammar a) {
    MCGrammarSymbol grammarSymbol = a.getSymbol();
    for (ASTASTRule rule : a.getASTRuleList()) {
      if (!rule.getASTSuperClassList().isEmpty()) {
        Optional<ProdSymbol> ruleSymbol = grammarSymbol.getProdWithInherited(rule.getType());
        if (ruleSymbol.isPresent()) {
          if (ruleSymbol.get().isClass()) {
            if (ruleSymbol.get().isPresentAstNode()
                    && (!((ASTClassProd) ruleSymbol.get().getAstNode()).getASTSuperClassList().isEmpty()
                    || !((ASTClassProd) ruleSymbol.get().getAstNode()).getSuperRuleList().isEmpty())) {
              Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, rule.getType(),
                      MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter().prettyprint(rule.getASTSuperClassList().get(0))),
                      rule.get_SourcePositionStart());
            }
          } else if (ruleSymbol.get().isPresentAstNode()
                  && ruleSymbol.get().getAstNode() instanceof ASTAbstractProd) {
            ASTAbstractProd prod = (ASTAbstractProd) ruleSymbol.get().getAstNode();
            if (!prod.getASTSuperClassList().isEmpty() || !prod.getSuperRuleList().isEmpty()) {
              Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, rule.getType(),
                      MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter().prettyprint(rule.getASTSuperClassList().get(0))),
                      rule.get_SourcePositionStart());
            }
          }
        }
      }
    }
  }
}
