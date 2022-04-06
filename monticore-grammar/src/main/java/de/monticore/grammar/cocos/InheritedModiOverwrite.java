/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTLexProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class InheritedModiOverwrite implements GrammarASTMCGrammarCoCo {

  public static final String ERROR_CODE = "0xA4069";
  public static final String ERROR_MSG_FORMAT = " The lexical production %s of the grammar %s will inherit the token mode %s as it overwrites the lexical production %s of the grammar %s";


  @Override
  public void check(ASTMCGrammar node) {
    String grammarName = node.getName();
    List<MCGrammarSymbol> superGrammars = node.getSymbol().getAllSuperGrammars();
    for (MCGrammarSymbol superGrammar : superGrammars) {
      ASTMCGrammar astNode = superGrammar.getAstNode();
      String superGrammarName = superGrammar.getName();
      for (ASTLexProd lexProd : astNode.getLexProdList()) {
        if (lexProd.isPresentMode()) {
          String modeString = lexProd.getMode();
          String prodName = lexProd.getName();
          List<ASTLexProd> supLexProdList = node.getLexProdList().stream().filter(prod -> prod.getName().equals(prodName)).collect(Collectors.toList());
          for (ASTLexProd lex : supLexProdList) {
            if (!lex.isPresentMode()) {
              //warn the user that he inherits a token mode
              Log.warn(String.format(ERROR_CODE + ERROR_MSG_FORMAT, prodName, grammarName, modeString, prodName, superGrammarName));
            }
          }
        }
      }
    }
  }
}
