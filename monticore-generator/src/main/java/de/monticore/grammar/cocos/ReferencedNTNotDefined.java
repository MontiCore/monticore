/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that nonterminals only extends abstract or normal nonterminals.
 *

 */
public class ReferencedNTNotDefined implements GrammarASTMCGrammarCoCo {

  public static final String ERROR_CODE = "0xA2030";

  public static final String ERROR_MSG_FORMAT = " The production %s must not reference the " +
      "%snonterminal %s because there exists no defining production for %s.";

  @Override
  public void check(ASTMCGrammar a) {
    MCGrammarSymbol grammarSymbol = a.getMCGrammarSymbol();
    for (ASTClassProd p : a.getClassProdList()) {
      if (!p.getSuperRuleList().isEmpty() && p.isPresentProdSymbol()) {
        for (ASTRuleReference sr : p.getSuperRuleList()) {
          if (!grammarSymbol.getProdWithInherited(sr.getName()).isPresent()) {
            Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, p.getName(), "", sr.getName(),
                sr.getName()),
                p.get_SourcePositionStart());
          }
        }
      }
      if (!p.getSuperInterfaceRuleList().isEmpty()) {
        for (ASTRuleReference sr : p.getSuperInterfaceRuleList()) {
          if (!grammarSymbol.getProdWithInherited(sr.getName()).isPresent()) {
            Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, p.getName(), "interface ",
                sr.getName(), sr.getName()),
                p.get_SourcePositionStart());
          }
        }
      }
    }
    for (ASTAbstractProd p : a.getAbstractProdList()) {
      if (!p.getSuperRuleList().isEmpty() && p.isPresentProdSymbol()) {
        for (ASTRuleReference sr : p.getSuperRuleList()) {
          if (!grammarSymbol.getProdWithInherited(sr.getName()).isPresent()) {
            Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, p.getName(), "", sr.getName(),
                sr.getName()),
                p.get_SourcePositionStart());
          }
        }
      }
      if (!p.getSuperInterfaceRuleList().isEmpty() && p.isPresentProdSymbol()) {
        for (ASTRuleReference sr : p.getSuperInterfaceRuleList()) {
          if (!grammarSymbol.getProdWithInherited(sr.getName()).isPresent()) {
            Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, p.getName(), "interface ",
                sr.getName(), sr.getName()),
                p.get_SourcePositionStart());
          }
        }
      }
    }
    for (ASTInterfaceProd p : a.getInterfaceProdList()) {
      if (!p.getSuperInterfaceRuleList().isEmpty() && p.isPresentProdSymbol()) {
        for (ASTRuleReference sr : p.getSuperInterfaceRuleList()) {
          if (!grammarSymbol.getProdWithInherited(sr.getName()).isPresent()) {
            Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, p.getName(), "interface ",
                sr.getName(), sr.getName()),
                p.get_SourcePositionStart());
          }
        }
      }
    }
  }

}
