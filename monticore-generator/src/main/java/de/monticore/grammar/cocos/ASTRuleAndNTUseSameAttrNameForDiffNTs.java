/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import java.util.Optional;

import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.grammar.grammar._ast.ASTASTRule;
import de.monticore.grammar.grammar._ast.ASTAdditionalAttribute;
import de.monticore.grammar.grammar._cocos.GrammarASTASTRuleCoCo;
import de.monticore.grammar.grammar._symboltable._symboltable.MCProdComponentSymbol;
import de.monticore.grammar.grammar._symboltable._symboltable.MCProdSymbol;
import de.monticore.types.FullGenericTypesPrinter;
import de.se_rwth.commons.logging.Log;

/**

 */
public class ASTRuleAndNTUseSameAttrNameForDiffNTs implements GrammarASTASTRuleCoCo {
  
  public static final String ERROR_CODE = "0xA4028";
  
  public static final String ERROR_MSG_FORMAT = " The AST rule for the nonterminal %s must not use the "
      + "same attribute name %s as the corresponding production "
      + "with the type %s as %s is not "
      + "identical to or a super type of %s.";
  
  @Override
  public void check(ASTASTRule a) {
    MCProdSymbol symbol = (MCProdSymbol) a.getEnclosingScope().resolve(a.getType(),
        MCProdSymbol.KIND).get();
    for (ASTAdditionalAttribute attr : a.getAdditionalAttributeList()) {
      Optional<MCProdComponentSymbol> rc = symbol.getProdComponent(attr.getNameOpt().orElse(""));
      String typeName = FullGenericTypesPrinter.printType(attr.getMCType());
      if (rc.isPresent()) {
        if (!typeName
            .endsWith(rc.get().getReferencedProd().get().getName())) {
          Optional<MCProdSymbol> attrType = a.getEnclosingScope()
              .resolve(typeName, MCProdSymbol.KIND);
          Optional<MCProdSymbol> compType = a.getEnclosingScope()
              .resolve(rc.get().getReferencedProd().get().getName(), MCProdSymbol.KIND);
          if (attrType.isPresent() && compType.isPresent()
              && !MCGrammarSymbolTableHelper.isSubtype(compType.get(), attrType.get())) {
            Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, a.getType(),
                attr.getName(), typeName,
                FullGenericTypesPrinter.printType(attr.getMCType()), rc.get().getReferencedProd().get().getName()),
                a.get_SourcePositionStart());
          }
        }
      }
    }
  }
}
