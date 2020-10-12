/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import com.ibm.icu.text.StringTransform;
import de.monticore.grammar.grammar._ast.ASTNonTerminal;
import de.monticore.grammar.grammar._ast.ASTProd;
import de.monticore.grammar.grammar._cocos.GrammarASTNonTerminalCoCo;
import de.monticore.grammar.grammar._cocos.GrammarASTProdCoCo;
import de.monticore.grammar.grammar._symboltable.RuleComponentSymbol;
import de.monticore.grammar.grammar._symboltable.RuleComponentSymbolTOP;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.stream.Collectors;

/**
 * checks that one NonTerminal does not define a component with the same derived and a manual name at the same time
 * for example:
 * A = names:Name* Name*;
 * is not allowed because both definition are merged into the same attribute but create different method names
 * form names:Name* e.g. the method name getNameList() is created
 * but from Name* e.g. the method name getNameList() is created
 * <p>
 * this does not fit together and is therefore forbidden
 */
public class DerivedAndManualListName implements GrammarASTProdCoCo {

  public static final String ERROR_CODE = "0xA2008";

  public static final String ERROR_MSG_FORMAT = " The production '%s' contains two list NonTerminals " +
      "that result in the attribute name '%s'. " +
      "But one name is derived from the NonTerminal name and one is set manually. This is not allowed.";

  @Override
  public void check(ASTProd node) {
    if (node.isPresentSymbol()) {
      List<RuleComponentSymbol> listComponents = node.getSymbol().getProdComponents().stream()
          .filter(RuleComponentSymbolTOP::isIsList)
          .collect(Collectors.toList());
      for (int i = 0; i < listComponents.size(); i++) {
        for (int j = i + 1; j < listComponents.size(); j++) {
          if ((listComponents.get(i).isIsNonterminal() && listComponents.get(j).isIsNonterminal()) ||
              (listComponents.get(i).isIsLexerNonterminal() && listComponents.get(j).isIsLexerNonterminal())) {
            ASTNonTerminal nonterminal1 = (ASTNonTerminal) listComponents.get(i).getAstNode();
            ASTNonTerminal nonterminal2 = (ASTNonTerminal) listComponents.get(j).getAstNode();
            if (getAttributeName(nonterminal1).equals(getAttributeName(nonterminal2)) &&
                nonterminal1.isPresentUsageName() != nonterminal2.isPresentUsageName()){
              Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, node.getName(),getAttributeName(nonterminal1)),
                  node.get_SourcePositionStart());
            }
          }
        }
      }
    }
  }

  protected String getAttributeName(ASTNonTerminal astNonTerminal) {
    if (astNonTerminal.isPresentUsageName()) {
      return astNonTerminal.getUsageName();
    } else {
      return StringTransformations.uncapitalize(astNonTerminal.getName()) + "s";
    }
  }
}
