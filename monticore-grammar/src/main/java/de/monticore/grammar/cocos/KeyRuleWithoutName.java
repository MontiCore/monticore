/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar.GrammarMill;
import de.monticore.grammar.grammar._ast.ASTGrammarNode;
import de.monticore.grammar.grammar._ast.ASTKeyConstant;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._visitor.GrammarTraverser;
import de.monticore.grammar.grammar._visitor.GrammarVisitor2;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that a grammar using keyword rules defines the token Name
 */
public class KeyRuleWithoutName implements GrammarASTMCGrammarCoCo {
  
  public static final String ERROR_CODE = "0xA0142";
  
  public static final String ERROR_MSG_FORMAT = "Using the keyword rules a grammar must define the token Name.";
  
  @Override
  public void check(ASTMCGrammar gr) {
    MCGrammarSymbol grSymbol = gr.getSymbol();
    if (!gr.isComponent() && !grSymbol.getProdWithInherited("Name").isPresent()) {
      if (!gr.getKeywordRuleList().isEmpty() || new FindKeyConstant().getResult(gr)) {
        Log.error(ERROR_CODE + ERROR_MSG_FORMAT, gr.get_SourcePositionStart());
      }
    }
  }

  protected class FindKeyConstant implements GrammarVisitor2 {
    protected boolean hasKeyConstant = false;

    public boolean getResult(ASTGrammarNode ast) {
      GrammarTraverser traverser = GrammarMill.traverser();
      traverser.add4Grammar(this);
      ast.accept(traverser);
      return hasKeyConstant;
    }

    public void visit(ASTKeyConstant ast) {
      hasKeyConstant = true;
    }
  }
}
