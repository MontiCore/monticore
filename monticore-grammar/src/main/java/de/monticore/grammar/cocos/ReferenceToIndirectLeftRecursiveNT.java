/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTNonTerminal;
import de.monticore.grammar.grammar._cocos.GrammarASTNonTerminalCoCo;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

/**
 * (Indirect) left recursive rules (i.e., rules which are left recursive due to
 * an interface) are not available as non-terminal-references.
 *
 */
public class ReferenceToIndirectLeftRecursiveNT implements GrammarASTNonTerminalCoCo {

  public static final String ERROR_CODE = "0xA4060";

  public static final String ERROR_MSG_FORMAT = " The indirect left recursive rule %s is not allowed here, use the super interface instead. ";

  @Override
  public void check(ASTNonTerminal a) {
    Optional<ProdSymbol> symbol = a.getEnclosingScope().resolveProd(a.getName());
    if (symbol.isPresent() && symbol.get().isIsIndirectLeftRecursive() && symbol.get().isClass()) {
      Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, a.getName()),
          a.get_SourcePositionStart());
    }
  }

}
