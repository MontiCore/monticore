/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import java.util.ArrayList;
import java.util.List;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTProd;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that nonterminals or only overridden by normal nonterminals.
 *
 * @author KH
 */
public class NTUniqueIgnoreCase implements GrammarASTMCGrammarCoCo {
  
public static final String ERROR_CODE = "0xA2026";
  
  public static final String ERROR_MSG_FORMAT = " The nonterminal %s must not be defined by more than one production: nonterminals aren't case-sensitive.";
  
  @Override
  public void check(ASTMCGrammar a) {
    List<String> prodnames = new ArrayList<>();
    List<String> prodnamesIgnoreCase = new ArrayList<>();
    List<ASTProd> prods = new ArrayList<>();
    prods.addAll(a.getAbstractProdList());
    prods.addAll(a.getClassProdList());
    prods.addAll(a.getEnumProdList());
    prods.addAll(a.getInterfaceProdList());
    prods.addAll(a.getLexProdList());
    prods.addAll(a.getExternalProdList());

    for(ASTProd p: prods){
      if(!prodnames.contains(p.getName()) && prodnamesIgnoreCase.contains(p.getName().toLowerCase())){
        Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, p.getName()),
                a.get_SourcePositionStart());
      } else {
        prodnames.add(p.getName());
        prodnamesIgnoreCase.add(p.getName().toLowerCase());
      }
    }
  }

}
