/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import java.util.List;
import java.util.Optional;

import de.monticore.grammar.grammar._ast.ASTEnumProd;
import de.monticore.grammar.grammar._ast.ASTExternalProd;
import de.monticore.grammar.grammar._ast.ASTInterfaceProd;
import de.monticore.grammar.grammar._ast.ASTLexProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that nonterminals only extends abstract or normal nonterminals.
 *
 * @author KH
 */
public class OverridingAbstractNTs implements GrammarASTMCGrammarCoCo {
  
  public static final String ERROR_CODE = "0xA4008";
  
  public static final String ERROR_MSG_FORMAT = " The production for the abstract nonterminal %s must not be overridden\n"
      +
      "by a production for an %s nonterminal.";
  
  @Override
  public void check(ASTMCGrammar a) {
    MCGrammarSymbol grammarSymbol = (MCGrammarSymbol) a.getSymbol().get();
    List<MCGrammarSymbol> grammarSymbols = grammarSymbol.getSuperGrammarSymbols();
    
    for (MCGrammarSymbol s : grammarSymbols) {
      for (ASTEnumProd p : a.getEnumProdList()) {
        doCheck(s.getProd(p.getName()), "enum");
      }
      for (ASTExternalProd p : a.getExternalProdList()) {
        doCheck(s.getProd(p.getName()), "external");
      }
      for (ASTInterfaceProd p : a.getInterfaceProdList()) {
        doCheck(s.getProd(p.getName()), "interface");
      }
      for (ASTLexProd p : a.getLexProdList()) {
        doCheck(s.getProd(p.getName()), "lexical");
      }
    }
  }
  
  private void doCheck(Optional<MCProdSymbol> typeSymbol, String type) {
    if (typeSymbol.isPresent() && typeSymbol.get().isAbstract()) {
      Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, typeSymbol.get().getName(), type));
    }
  }
  
}
