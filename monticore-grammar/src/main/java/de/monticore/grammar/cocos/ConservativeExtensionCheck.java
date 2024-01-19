/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.MCGrammarSymbolTableHelper;
import de.monticore.grammar.grammar._ast.ASTGrammarAnnotation;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.grammar.grammar._symboltable.RuleComponentSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.List;

public class ConservativeExtensionCheck implements GrammarASTMCGrammarCoCo {

  public static final String ERROR_CODE = "0xA2007";

  public static final String ERROR_MSG_FORMAT = " Warning: Production %s does not extend %s in a conservative manner in component %s. This can lead to problems in the AST.";

  @Override
  public void check(ASTMCGrammar node) {
    MCGrammarSymbol g = node.getSymbol();
    for (ProdSymbol nt : g.getProds()) {
      if (!hasNonConservativeAnno(nt.getAstNode().getGrammarAnnotationList())) {
        //check when you extend a class not conservative directly (Subclass extends Superclass = ...)
        if (nt.isClass() && !nt.getSuperProds().isEmpty()
                && !MCGrammarSymbolTableHelper.getAllSuperProds(nt).isEmpty()) {
          for (ProdSymbol superNt : MCGrammarSymbolTableHelper.getAllSuperProds(nt)) {
            compareComponents(nt, superNt);
          }
        }
        //checks when you define a Prod with the same Name as a Prod in a Supergrammar
        if(!g.getSuperGrammarSymbols().isEmpty()){
          for(MCGrammarSymbol superg : g.getSuperGrammarSymbols()){
            for(ProdSymbol superNt : superg.getProds()){
              if(nt.getName().equals(superNt.getName())){
                compareComponents(nt, superNt);
              }
            }
          }
        }
      }
    }
  }

  protected void compareComponents(ProdSymbol p, ProdSymbol superp) {
    for (RuleComponentSymbol comp : superp.getProdComponents()) {
      List<RuleComponentSymbol> prodComponents = p.getSpannedScope().resolveRuleComponentDownMany(comp.getName());
      if (prodComponents.isEmpty()) {
        Log.warn(String.format(ERROR_CODE + ERROR_MSG_FORMAT, p.getName(), superp.getFullName(), comp.getName()),
            p.getSourcePosition());
      }else if (prodComponents.get(0).isIsTerminal() != comp.isIsTerminal() ||
          prodComponents.get(0).isIsNonterminal() != comp.isIsNonterminal() ||
          prodComponents.get(0).isIsList() != comp.isIsList() ||
          prodComponents.get(0).isIsOptional() != comp.isIsOptional() ||
          !prodComponents.get(0).getName().equals(comp.getName())) {
        Log.warn(String.format(ERROR_CODE + ERROR_MSG_FORMAT, p.getName(), superp.getFullName(), comp.getName()),
            p.getSourcePosition());
      }
    }
  }

  protected boolean hasNonConservativeAnno(List<ASTGrammarAnnotation> grammarAnnotationsList) {
    for (ASTGrammarAnnotation anno : grammarAnnotationsList) {
      if (anno.isNonConservative()) {
        return true;
      }
    }
    return false;
  }
}
