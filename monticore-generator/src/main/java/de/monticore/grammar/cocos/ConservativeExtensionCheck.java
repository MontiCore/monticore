/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.grammar.grammar._symboltable.RuleComponentSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

public class ConservativeExtensionCheck implements GrammarASTMCGrammarCoCo {

    // TODO: Multiple kinds of Errors, #2376

  public static final String ERROR_CODE = "0xA2007";

  public static final String ERROR_MSG_FORMAT = " The production %s does not extend the Rule %s conservative at component %s. This can lead to problems in the AST.";

  @Override
  public void check(ASTMCGrammar node) {
    MCGrammarSymbol g = node.getSymbol();
    for (ProdSymbol nt : g.getProds()) {
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

  private void compareComponents(ProdSymbol p, ProdSymbol superp) {
    for (RuleComponentSymbol comp : superp.getProdComponents()) {
      Optional<RuleComponentSymbol> prodComponent = p.getProdComponent(comp.getName());
      if (!prodComponent.isPresent()) {
        Log.warn(String.format(ERROR_CODE + ERROR_MSG_FORMAT, p.getName(), superp.getName(), comp.getName(),
            p.getSourcePosition()));
      }else if (prodComponent.get().isIsTerminal() != comp.isIsTerminal() ||
          prodComponent.get().isIsNonterminal() != comp.isIsNonterminal() ||
          prodComponent.get().isIsList() != comp.isIsList() ||
          prodComponent.get().isIsOptional() != comp.isIsOptional() ||
          !prodComponent.get().getName().equals(comp.getName())) {
        Log.warn(String.format(ERROR_CODE + ERROR_MSG_FORMAT, p.getName(), superp.getName(), comp.getName(),
            p.getSourcePosition()));
      }
    }
  }
}
