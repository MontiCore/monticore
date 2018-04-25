package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.monticore.grammar.symboltable.MCGrammarSymbol;

public class ReferencedSymbolExists implements GrammarASTMCGrammarCoCo {

  public static final String ERROR_CODE = "0xA4037";

  public static final String ERROR_MSG_FORMAT = " The production for the referenced symbol %s does not exist as a symbol or not at all.";

  @Override
  public void check(ASTMCGrammar a) {
    MCGrammarSymbol grammarSymbol = (MCGrammarSymbol) a.getSymbol();

    ReferencedSymbolVisitor visitor = new ReferencedSymbolVisitor(grammarSymbol);
    visitor.visit(a);

  }
}

