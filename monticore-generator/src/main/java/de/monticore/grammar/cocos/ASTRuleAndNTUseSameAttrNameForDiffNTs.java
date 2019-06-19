/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import java.util.Optional;

import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.grammar.grammar._ast.ASTASTRule;
import de.monticore.grammar.grammar._ast.ASTAdditionalAttribute;
import de.monticore.grammar.grammar._cocos.GrammarASTASTRuleCoCo;
import de.monticore.grammar.grammar._symboltable.RuleComponentSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
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
    ProdSymbol symbol = (ProdSymbol) a.getEnclosingScope().resolve(a.getType(),
        ProdSymbol.KIND).get();
    for (ASTAdditionalAttribute attr : a.getAdditionalAttributeList()) {
      Optional<RuleComponentSymbol> rc = symbol.getProdComponent(attr.getNameOpt().orElse(""));
      String typeName = FullGenericTypesPrinter.printType(attr.getMCType());
      if (rc.isPresent()) {
        if (!typeName
            .endsWith(rc.get().getReferencedProd().get().getName())) {
          Optional<ProdSymbol> attrType = a.getEnclosingScope()
              .resolve(typeName, ProdSymbol.KIND);
          Optional<ProdSymbol> compType = a.getEnclosingScope()
              .resolve(rc.get().getReferencedProd().get().getName(), ProdSymbol.KIND);
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
