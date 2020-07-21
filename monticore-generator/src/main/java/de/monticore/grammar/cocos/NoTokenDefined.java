/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.stream.Collectors;

public class NoTokenDefined implements GrammarASTMCGrammarCoCo {

  /**
   * Coco that checks whether a token is defined by the grammar or any super grammar.
   * If not, there will be no lexer generated, so the parser will not compile
   * This coco ensures that this will not happen
   */

  public static final String ERROR_CODE = "0xA4101";

  public static final String ERROR_MSG_FORMAT = " There is no production defining a token in Grammar : '%s'. ";

  @Override
  public void check(ASTMCGrammar node) {
    if (node.isPresentSymbol() && !node.getSymbol().isIsComponent()) {
      MCGrammarSymbol symbol = node.getSymbol();
      List<ASTMCGrammar> superGrammars = symbol.getAllSuperGrammars().stream()
          .filter(x -> x.isPresentAstNode())
          .map(x -> (ASTMCGrammar) x.getAstNode())
          .collect(Collectors.toList());
      //check for own and super grammars tokens
      if (!hasTokenDefinition(node) && superGrammars.stream().noneMatch(this::hasTokenDefinition)) {
        Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, node.getName()),
            node.get_SourcePositionStart());
      }
    }
  }

  private boolean hasTokenDefinition(ASTMCGrammar node) {
    //if there is a body check if the body contains tokens
    NoTokenDefinedVisitor visitor = new NoTokenDefinedVisitor();
    node.accept(visitor);
    return visitor.foundTerminal();
  }
}
