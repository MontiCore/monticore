/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import java.util.Map.Entry;
import java.util.Optional;

import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.grammar.grammar._ast.ASTProd;
import de.monticore.grammar.grammar._cocos.GrammarASTProdCoCo;
import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.monticore.grammar.symboltable.MCProdSymbolReference;
import de.se_rwth.commons.logging.Log;

/**
 * Created by
 *
 * @author KH
 */
public class ProdWithExtensionMustNotBeOverridden implements GrammarASTProdCoCo {
  
  public static final String ERROR_CODE = "0xA4010";
  
  public static final String ERROR_MSG_FORMAT = " The production %s must not be overridden because there"
      + " already exist productions extending it.";
  
  @Override
  public void check(ASTProd a) {
    
    Optional<MCGrammarSymbol> grammarSymbol = MCGrammarSymbolTableHelper
        .getMCGrammarSymbol(a);
    
    boolean isOverriding = false;
    for (MCGrammarSymbol sup : MCGrammarSymbolTableHelper.getAllSuperGrammars(grammarSymbol.get())) {
      if (sup.getProd(a.getName()).isPresent()) {
        isOverriding = true;
        break;
      }
    }
    if(!isOverriding) {
      return;
    }
    
    boolean extensionFound = false;
    entryLoop: for (Entry<String, MCProdSymbol> entry : grammarSymbol.get().getProdsWithInherited()
        .entrySet()) {
      MCProdSymbol rs = entry.getValue();
      for (MCProdSymbolReference typeSymbol : rs.getSuperProds()) {
        if (a.getName().equals(typeSymbol.getName())) {
          extensionFound = true;
          break entryLoop;
        }
      }
    }
    if (extensionFound) {
      Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, a.getName()),
          a.get_SourcePositionStart());
    }
  }
}
