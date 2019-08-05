package de.monticore.grammar.cocos;

import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.RuleComponentSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Optional;

public class ConservativeExtensionCheck implements GrammarASTMCGrammarCoCo {

  public static final String ERROR_CODE = "0xA2007";

  public static final String ERROR_MSG_FORMAT = " The production %s does not extend the Rule %s conservative at component %s. This can lead to problems in the AST.";

  @Override
  public void check(ASTMCGrammar node) {
    Optional<MCGrammarSymbol> symbol = node.getMCGrammarSymbolOpt();
    for (ProdSymbol prodSymbol : symbol.get().getProds()) {
      //check when you extend a class not conservative directly (Subclass extends Superclass = ...)
      if (prodSymbol.isClass() && !prodSymbol.getSuperProds().isEmpty() && !MCGrammarSymbolTableHelper.getAllSuperProds(prodSymbol).isEmpty()) {
        for (ProdSymbol superProdSymbol : MCGrammarSymbolTableHelper.getAllSuperProds(prodSymbol)) {
          compareComponents(prodSymbol, superProdSymbol);
        }
      }
      //checks when you define a Prod with the same Name as a Prod in a Supergrammar
      if(!symbol.get().getSuperGrammarSymbols().isEmpty()){
        List<MCGrammarSymbol> superGrammarSymbols = symbol.get().getSuperGrammarSymbols();
        for(MCGrammarSymbol grammarSymbol : superGrammarSymbols){
          for(ProdSymbol mcProdSymbol : grammarSymbol.getProds()){
            if(prodSymbol.getName().equals(mcProdSymbol.getName())){
              compareComponents(prodSymbol, mcProdSymbol);
            }
          }
        }
      }
    }
  }

  private void compareComponents(ProdSymbol prodSymbol, ProdSymbol superProdSymbol) {
    for (RuleComponentSymbol superProdComponent : superProdSymbol.getProdComponents()) {
      Optional<RuleComponentSymbol> prodComponent = prodSymbol.getProdComponent(superProdComponent.getName());
      if (!prodComponent.isPresent()) {
        Log.warn(String.format(ERROR_CODE + ERROR_MSG_FORMAT, prodSymbol.getName(), superProdSymbol.getName(), superProdComponent.getName(),
            prodSymbol.getSourcePosition()));
      }else if (prodComponent.get().isTerminal() != superProdComponent.isTerminal() ||
          prodComponent.get().isNonterminal() != superProdComponent.isNonterminal() ||
          prodComponent.get().isList() != superProdComponent.isList() ||
          prodComponent.get().isOptional() != superProdComponent.isOptional() ||
          !prodComponent.get().getUsageName().equals(superProdComponent.getUsageName())) {
        Log.warn(String.format(ERROR_CODE + ERROR_MSG_FORMAT, prodSymbol.getName(), superProdSymbol.getName(), superProdComponent.getName(),
            prodSymbol.getSourcePosition()));
      }
    }
  }
}
