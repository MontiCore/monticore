/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.grammar.grammar._ast.ASTASTRule;
import de.monticore.grammar.grammar._cocos.GrammarASTASTRuleCoCo;
import de.monticore.grammar.grammar._symboltable.AdditionalAttributeSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.grammar.grammar._symboltable.RuleComponentSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcfullgenerictypes.MCFullGenericTypesMill;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Optional;

/**
 *
 */
public class ASTRuleAndNTUseSameAttrNameForDiffNTs implements GrammarASTASTRuleCoCo {

  public static final String ERROR_CODE = "0xA4028";

  public static final String ERROR_MSG_FORMAT = " The AST rule for the nonterminal %s must not use the "
      + "same attribute name %s as the corresponding production "
      + "with the type %s is not "
      + "identical to or a super type of %s.";

  @Override
  public void check(ASTASTRule a) {
    ProdSymbol prodSymbol = a.getEnclosingScope().resolveProd(a.getType()).get();
    for (AdditionalAttributeSymbol attr : prodSymbol.getSpannedScope().getLocalAdditionalAttributeSymbols()) {
      List<RuleComponentSymbol> rcs = prodSymbol.getSpannedScope().resolveRuleComponentMany(attr.getName());
      if (!rcs.isEmpty()) {
        RuleComponentSymbol rc = rcs.get(0);
        if (rc.isIsNonterminal()) {
          String typeName = attr.getAstNode().getMCType().printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter());
          if (!typeName
                  .endsWith(rc.getReferencedProd().get().getName())) {
            Optional<ProdSymbol> attrType = a.getEnclosingScope()
                    .resolveProd(typeName);
            Optional<ProdSymbol> compType = a.getEnclosingScope()
                    .resolveProd(rc.getReferencedProd().get().getName());
            if (attrType.isPresent() && compType.isPresent()) {
              if (MCGrammarSymbolTableHelper.isSubtype(compType.get(), attrType.get())
                      || isCorrespondingJavaTypeFromToken(attrType.get(), compType.get())) {
                continue;
              } else {
                Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, a.getType(),
                        attr.getName(), typeName,
                        rc.getReferencedProd().get().getName()),
                        a.get_SourcePositionStart());
              }
            }
          }
        } else if (rc.isIsTerminal() || rc.isIsLexerNonterminal()) {
          // Compare to String
          if (!("String".equals(attr.getType()) || "java.lang.String".equals(attr.getType()))) {
            Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, a.getType(),
                    attr.getName(), attr.getType(),
                    rc.getName()),
                    a.get_SourcePositionStart());
          }
        } else if (rc.isIsConstant()) {
          // Compare to boolean
          ASTMCType attrType = attr.getAstNode().getMCType();
          if (!((attrType instanceof ASTMCPrimitiveType) || ((ASTMCPrimitiveType) attrType).isBoolean())) {
            Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, a.getType(),
                    attr.getName(), attr.getType(),
                    rc.getName()),
                    a.get_SourcePositionStart());
          }
        } else if (rc.isIsConstantGroup()) {
          // Compare to int
          ASTMCType attrType = attr.getAstNode().getMCType();
          if (!(attrType instanceof ASTMCPrimitiveType) || !((ASTMCPrimitiveType) attrType).isInt()) {
            Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, a.getType(),
                    attr.getName(), attr.getType(),
                    rc.getName()),
                    a.get_SourcePositionStart());
          }
        } else
          Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, a.getType(),
                  attr.getName(), attr.getType(),
                  rc.getName()),
                  a.get_SourcePositionStart());
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
