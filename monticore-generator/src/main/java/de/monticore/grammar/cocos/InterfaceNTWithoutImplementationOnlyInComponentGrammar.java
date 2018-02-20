/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import java.util.Map;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTProd;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.monticore.grammar.symboltable.MCProdSymbolReference;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that abstract nonterminals witheout extending productions only occur in a component grammar.
 *
 * @author KH
 */
public class InterfaceNTWithoutImplementationOnlyInComponentGrammar implements GrammarASTMCGrammarCoCo {

  public static final String ERROR_CODE = "0xA0278";

  public static final String ERROR_MSG_FORMAT = " The interface nonterminal %s must not be used without nonterminals " +
          "implementing it in a grammar not marked as a grammar component.";

  @Override
  public void check(ASTMCGrammar a) {
    MCGrammarSymbol grammarSymbol = (MCGrammarSymbol) a.getSymbol().get();
    if (!a.isComponent()) {
      for (ASTProd p : a.getInterfaceProdList()) {
        boolean extensionFound = false;
        entryLoop: for (Map.Entry<String, MCProdSymbol> entry : grammarSymbol
            .getProdsWithInherited().entrySet()) {
          MCProdSymbol rs = (MCProdSymbol) entry.getValue();
          // TODO GV: getAllSuperInterfaces()?
          for (MCProdSymbolReference typeSymbol : rs.getSuperInterfaceProds()) {
            if (p.getName().equals(typeSymbol.getName())) {
              extensionFound = true;
              break entryLoop;
            }
          }
        }
        if (!extensionFound) {
          Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, p.getName()),
                  a.get_SourcePositionStart());
        }
      }
    }
  }

}
