/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTProd;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that nonterminals only extends abstract or normal nonterminals.
 *
 */
public class OverridingEnumNTs implements GrammarASTMCGrammarCoCo {

  public static final String ERROR_CODE = "0xA4027";

  public static final String ERROR_MSG_FORMAT = " The production for the enum nonterminal %s must not be overridden.";

  @Override
  public void check(ASTMCGrammar a) {
    List<ASTProd> prods = new ArrayList<>(a.getClassProdList());
    prods.addAll(a.getExternalProdList());
    prods.addAll(a.getLexProdList());
    prods.addAll(a.getInterfaceProdList());
    prods.addAll(a.getEnumProdList());
    prods.addAll(a.getAbstractProdList());
    
    MCGrammarSymbol grammarSymbol = a.getSymbol();
    
    for (ASTProd p : prods) {
      Optional<ProdSymbol> typeSymbol = grammarSymbol.getInheritedProd(p.getName());
      if (typeSymbol.isPresent() && typeSymbol.get().isIsEnum()) {
        Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, p.getName()));
      }
    }

  }

}
