/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import com.google.common.collect.Lists;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Collections;

public class NoForbiddenGrammarName implements GrammarASTMCGrammarCoCo {

  public static final String ERROR_CODE = "0xA4036";

  public static final String ERROR_MSG_FORMAT = " There must not exist a grammar with the name %s.";

  protected static final List<String> forbiddenNames = Collections.unmodifiableList(Lists.newArrayList("I"));

  @Override
  public void check (ASTMCGrammar node){
    String grammarName = node.getName();
    if(forbiddenNames.contains(grammarName)){
      Log.error(ERROR_CODE + String.format(ERROR_MSG_FORMAT, grammarName));
    }
  }



}
