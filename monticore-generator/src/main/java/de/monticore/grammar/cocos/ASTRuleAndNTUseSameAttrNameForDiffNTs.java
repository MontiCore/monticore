/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import java.util.Optional;

import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.grammar.grammar._ast.ASTASTRule;
import de.monticore.grammar.grammar._ast.ASTAttributeInAST;
import de.monticore.grammar.grammar._cocos.GrammarASTASTRuleCoCo;
import de.monticore.grammar.symboltable.MCProdComponentSymbol;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * @author KH
 */
public class ASTRuleAndNTUseSameAttrNameForDiffNTs implements GrammarASTASTRuleCoCo {
  
  public static final String ERROR_CODE = "0xA4028";
  
  public static final String ERROR_MSG_FORMAT = " The AST rule for the nonterminal %s must not use the "
      + "same attribute name %s as the corresponding production "
      + "with the type %s as %s is not "
      + "identical to or a super type of %s.";
  
  @Override
  public void check(ASTASTRule a) {
    MCProdSymbol symbol = (MCProdSymbol) a.getEnclosingScope().get().resolve(a.getType(),
        MCProdSymbol.KIND).get();
    for (ASTAttributeInAST attr : a.getAttributeInASTList()) {
      Optional<MCProdComponentSymbol> rc = symbol.getProdComponent(attr.getNameOpt().orElse(""));
      if (rc.isPresent()) {
        if (!attr.getGenericType().getTypeName()
            .endsWith(rc.get().getReferencedProd().get().getName())) {
          Optional<MCProdSymbol> attrType = a.getEnclosingScope().get()
              .resolve(attr.getGenericType().getTypeName(), MCProdSymbol.KIND);
          Optional<MCProdSymbol> compType = a.getEnclosingScope().get()
              .resolve(rc.get().getReferencedProd().get().getName(), MCProdSymbol.KIND);
          if (attrType.isPresent() && compType.isPresent()
              && !MCGrammarSymbolTableHelper.isSubtype(compType.get(), attrType.get())) {
            Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, a.getType(),
                attr.getName(), attr.getGenericType().getTypeName(),
                attr.getGenericType().getTypeName(), rc.get().getReferencedProd().get().getName()),
                a.get_SourcePositionStart());
          }
        }
      }
    }
  }
}
