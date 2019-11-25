/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.grammar.grammar._ast.ASTASTRule;
import de.monticore.grammar.grammar._ast.ASTAdditionalAttribute;
import de.monticore.grammar.grammar._cocos.GrammarASTASTRuleCoCo;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.grammar.grammar._symboltable.RuleComponentSymbol;
import de.monticore.types.mcfullgenerictypes._ast.MCFullGenericTypesMill;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

/**
 *
 */
public class ASTRuleAndNTUseSameAttrNameForDiffNTs implements GrammarASTASTRuleCoCo {

  public static final String ERROR_CODE = "0xA4028";

  public static final String ERROR_MSG_FORMAT = " The AST rule for the nonterminal %s must not use the "
      + "same attribute name %s as the corresponding production "
      + "with the type %s as %s is not "
      + "identical to or a super type of %s.";

  @Override
  public void check(ASTASTRule a) {
    ProdSymbol symbol = a.getEnclosingScope().resolveProd(a.getType()).get();
    for (ASTAdditionalAttribute attr : a.getAdditionalAttributeList()) {
      Optional<RuleComponentSymbol> rc = symbol.getProdComponent(attr.getNameOpt().orElse(""));
      String typeName = MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter().prettyprint(attr.getMCType());
      if (rc.isPresent()) {
        if (!typeName
            .endsWith(rc.get().getReferencedProd().get().getName())) {
          Optional<ProdSymbol> attrType = a.getEnclosingScope()
              .resolveProd(typeName);
          Optional<ProdSymbol> compType = a.getEnclosingScope()
              .resolveProd(rc.get().getReferencedProd().get().getName());
          if (attrType.isPresent() && compType.isPresent()) {
            if (MCGrammarSymbolTableHelper.isSubtype(compType.get(), attrType.get())
                || isCorrespondingJavaTypeFromToken(attrType.get(), compType.get())) {
              continue;
            } else {
              Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, a.getType(),
                  attr.getName(), typeName,
                  MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter().prettyprint(attr.getMCType()),
                  rc.get().getReferencedProd().get().getName()),
                  a.get_SourcePositionStart());
            }
          }
        }
      }
    }
  }

  private boolean isCorrespondingJavaTypeFromToken(ProdSymbol astRuleType, ProdSymbol compType) {
    if ("Name".equals(compType.getName())) {
      return "String".equals(astRuleType.getName());
    }
    return false;
  }
}
