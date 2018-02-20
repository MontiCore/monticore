/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import java.util.Map;

import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.grammar.grammar._ast.ASTASTRule;
import de.monticore.grammar.grammar._ast.ASTGenericType;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.monticore.grammar.symboltable.MCProdOrTypeReference;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that no ast rules exist for enum nonterminals.
 *
 * @author KH
 */
public class NoASTExtendsForClasses implements GrammarASTMCGrammarCoCo {
  
  public static final String ERROR_CODE = "0xA4097";
  
  public static final String ERROR_MSG_FORMAT = " It is not allowed to extend the rule %s with the external class %s.";
  
  @Override
  public void check(ASTMCGrammar a) {
    MCGrammarSymbol grammarSymbol = (MCGrammarSymbol) a.getSymbol().get();
    Map<String, MCProdSymbol> allProds = grammarSymbol.getProdsWithInherited();
    
    for (MCProdSymbol classProd : grammarSymbol.getProds()) {
      for (MCProdOrTypeReference sClass : classProd.getAstSuperClasses()) {
        if (!allProds.containsKey(
            sClass.getProdRef().getName().substring(TransformationHelper.AST_PREFIX.length()))) {
          Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT,
              classProd.getName(),
              sClass.getProdRef().getName(),
              classProd.getAstNode().get().get_SourcePositionStart()));
        }
      }
    }
    
    for (ASTASTRule rule : a.getASTRuleList()) {
      if (allProds.containsKey(rule.getType())) {
        MCProdSymbol prod = allProds.get(rule.getType());
        if (prod.isClass()) {
          for (ASTGenericType type : rule.getASTSuperClassList()) {
            String simpleName = type.getNameList().get(type.getNameList().size() - 1);

            if (!allProds.containsKey(simpleName.substring(TransformationHelper.AST_PREFIX.length()))) {
              Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT,
                  rule.getType(),
                  type.getTypeName(),
                  rule.get_SourcePositionStart()));
            }
          }
        }
      }
    }
  }
  
}
