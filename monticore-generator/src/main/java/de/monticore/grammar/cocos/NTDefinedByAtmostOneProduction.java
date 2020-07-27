/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import java.util.ArrayList;
import java.util.List;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTProd;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that nonterminals only extends abstract or normal nonterminals.
 *
 */
public class NTDefinedByAtmostOneProduction implements GrammarASTMCGrammarCoCo {
  
  public static final String ERROR_CODE = "0xA2025";
  
  public static final String ERROR_MSG_FORMAT = " The nonterminal %s must not be defined by more than one production.";
  
  @Override
  public void check(ASTMCGrammar a) {
    List<String> prodnames = new ArrayList<>();
    List<ASTProd> prods = new ArrayList<>();
    prods.addAll(a.getAbstractProdsList());
    prods.addAll(a.getClassProdsList());
    prods.addAll(a.getEnumProdsList());
    prods.addAll(a.getInterfaceProdsList());
    prods.addAll(a.getLexProdsList());
    prods.addAll(a.getExternalProdsList());

    for(ASTProd p: prods){
      if(prodnames.contains(p.getName())){
        Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, p.getName()),
                p.get_SourcePositionStart());
      } else {
        prodnames.add(p.getName());
      }
    }
  }

}
