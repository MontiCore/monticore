package de.monticore.grammar.cocos;

import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.grammar.grammar._ast.ASTNonTerminal;
import de.monticore.grammar.grammar._cocos.GrammarASTNonTerminalCoCo;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

public class ReferencedSymbolExists implements GrammarASTNonTerminalCoCo {

  public static final String ERROR_CODE = "0xA4037";

  public static final String ERROR_MSG_FORMAT = " The production for the referenced symbol %s does not exist as a symbol or not at all.";

  @Override
  public void check(ASTNonTerminal node) {
    Optional<MCGrammarSymbol> grammarSymbol = MCGrammarSymbolTableHelper
        .getMCGrammarSymbol(node);
    if (node.isPresentReferencedSymbol()) {
      String symbol = node.getReferencedSymbol();
      if (grammarSymbol.get().getProdWithInherited(symbol).isPresent() &&
          grammarSymbol.get().getProdWithInherited(symbol).get().isSymbolDefinition()) {
        return;
      }
      Log.error(String.format(ERROR_CODE + String.format(ERROR_MSG_FORMAT, symbol),
          node.get_SourcePositionStart()));
    }
  }
}

