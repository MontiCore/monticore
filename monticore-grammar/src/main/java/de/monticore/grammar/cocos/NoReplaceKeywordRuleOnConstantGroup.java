/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTConstant;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTTerminal;
import de.monticore.grammar.grammar._cocos.GrammarASTConstantCoCo;
import de.monticore.grammar.grammar._cocos.GrammarASTTerminalCoCo;
import de.monticore.grammar.grammar._visitor.GrammarVisitor2;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

public class NoReplaceKeywordRuleOnConstantGroup implements GrammarASTConstantCoCo, GrammarVisitor2 {

  /**
   * Coco that checks whether a constant-rule constant
   * is replaced wit a replace keword rule.
   * Due to the generated Antlr parser files not respecting these replaced keywords,
   * this CoCo ensures that replacekeyword does not target constants.
   */

  public static final String ERROR_CODE = "0xA4162";

  public static final String ERROR_MSG_FORMAT = " There is a replacekeyword rule targeting a constant-group constant: '%s'. ";


  protected List<String> replacedKeywords = new ArrayList<>();


  @Override
  public void visit(ASTMCGrammar node) {
    // Cache the replaced keywords for this grammar
    this.replacedKeywords.addAll(node.getSymbol().getReplacedKeywordsWithInherited().keySet());
  }

  @Override
  public void endVisit(ASTMCGrammar node) {
    this.replacedKeywords.clear();
  }

  @Override
  public void check(ASTConstant node) {
    if (this.replacedKeywords.contains(node.getName())) {
      Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, node.getName()),
              node.get_SourcePositionStart());
    }
  }
}
