/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTProd;
import de.monticore.grammar.grammar._cocos.GrammarASTProdCoCo;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbolLoader;

import java.util.Map.Entry;
import java.util.Optional;

import static de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper.getAllSuperGrammars;
import static de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper.getMCGrammarSymbol;
import static de.se_rwth.commons.logging.Log.error;
import static java.lang.String.format;

public class ProdWithExtensionMustNotBeOverridden implements GrammarASTProdCoCo {

  public static final String ERROR_CODE = "0xA4010";

  public static final String ERROR_MSG_FORMAT = " The production %s must not be overridden because there"
          + " already exist productions extending it.";

  @Override
  public void check(ASTProd a) {

    Optional<MCGrammarSymbol> grammarSymbol = getMCGrammarSymbol(a.getEnclosingScope());

    boolean isOverriding = false;
    for (MCGrammarSymbol sup : getAllSuperGrammars(grammarSymbol.get())) {
      if (sup.getProd(a.getName()).isPresent()) {
        isOverriding = true;
        break;
      }
    }
    if (!isOverriding) {
      return;
    }

    boolean extensionFound = false;
    entryLoop:
    for (Entry<String, ProdSymbol> entry : grammarSymbol.get().getProdsWithInherited()
            .entrySet()) {
      ProdSymbol rs = entry.getValue();
      for (ProdSymbolLoader typeSymbol : rs.getSuperProds()) {
        if (a.getName().equals(typeSymbol.getName())) {
          extensionFound = true;
          break entryLoop;
        }
      }
    }
    if (extensionFound) {
      error(format(ERROR_CODE + ERROR_MSG_FORMAT, a.getName()),
              a.get_SourcePositionStart());
    }
  }
}
