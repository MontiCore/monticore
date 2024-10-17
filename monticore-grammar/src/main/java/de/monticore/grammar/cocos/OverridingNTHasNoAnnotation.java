/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTGrammarAnnotation;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.List;

/**
 * Checks if nonterminals with an override annotation really overrides a class
 */
public class OverridingNTHasNoAnnotation implements GrammarASTMCGrammarCoCo {

  public static final String ERROR_CODE = "0xA4098";

  public static final String ERROR_MSG_FORMAT = " Warning: The production %s overrides production %s without annotation.";

  @Override
  public void check(ASTMCGrammar a) {
    MCGrammarSymbol gSymbol = a.getSymbol();
    for (ProdSymbol p : gSymbol.getProds()) {
      if (!hasOverrideAnno(p.getAstNode().getGrammarAnnotationList()) && gSymbol.getInheritedProd(p.getName()).isPresent()) {
        String fullName = gSymbol.getInheritedProd(p.getName()).get().getFullName();
        Log.warn(String.format(ERROR_CODE + ERROR_MSG_FORMAT, p.getName(), fullName),
                p.getAstNode().get_SourcePositionStart());
      }
    }
  }

  protected boolean hasOverrideAnno(List<ASTGrammarAnnotation> grammarAnnotationsList) {
    for (ASTGrammarAnnotation anno : grammarAnnotationsList) {
      if (anno.isOverride()) {
        return true;
      }
    }
    return false;
  }

}
