/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import com.google.common.collect.Lists;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class NoForbiddenProdName implements GrammarASTMCGrammarCoCo {

  public static final String ERROR_CODE = "0xA4096";

  public static final String ERROR_MSG_FORMAT = " There must not exist a production with the name %s in the grammar %s.";

  protected static final List<String> forbiddenNames = Collections.unmodifiableList(Lists.newArrayList("EnclosingScope", "SpannedScope", "Node", "CNode",
          "Class", "Traverser", "ScopesGenitor", "ScopesGenitorDelegator", "Scope", "ArtifactScope", "GlobalScope",
          // Antlr
          "Mode", "Parser", "Lexer", "Options", "Returns"));

  protected static final String NODE = "Node";

  protected static final String CONSTANTS = "Constants";

  @Override
  public void check(ASTMCGrammar node) {
    String grammarName = node.getName();
    MCGrammarSymbol symbol = node.getSymbol();
    Collection<ProdSymbol> prods = symbol.getProdsWithInherited().values();
    for(ProdSymbol prod: prods){
      String prodName = prod.getName();
      if(forbiddenNames.contains(prodName)){
        Log.error(ERROR_CODE + String.format(ERROR_MSG_FORMAT, prodName, grammarName));
      }
      if((grammarName+NODE).equals(prodName) || (CONSTANTS+grammarName).equals(prodName)){
        Log.error(ERROR_CODE + String.format(ERROR_MSG_FORMAT, prodName, grammarName));
      }
    }
  }

}
