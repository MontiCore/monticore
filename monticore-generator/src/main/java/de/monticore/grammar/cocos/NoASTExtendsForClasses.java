/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.grammar.grammar._ast.ASTASTRule;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbolLoader;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCArrayType;
import de.monticore.types.mcfullgenerictypes._ast.MCFullGenericTypesMill;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

import java.util.Map;

/**
 * Checks that no ast rules exist for enum nonterminals.
 *
 */
public class NoASTExtendsForClasses implements GrammarASTMCGrammarCoCo {
  
  public static final String ERROR_CODE = "0xA4097";
  
  public static final String ERROR_MSG_FORMAT = " It is not allowed to extend the rule %s with the external class %s.";
  
  @Override
  public void check(ASTMCGrammar a) {
    MCGrammarSymbol grammarSymbol = a.getSymbol();
    Map<String, ProdSymbol> allProds = grammarSymbol.getProdsWithInherited();
    
    for (ProdSymbol classProd : grammarSymbol.getProds()) {
      for (ProdSymbolLoader sClass : classProd.getAstSuperClasses()) {
        if (!allProds.containsKey(
            sClass.getName().substring(TransformationHelper.AST_PREFIX.length()))) {
          Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT,
              classProd.getName(),
              Names.getSimpleName(sClass.getName()),
              classProd.getAstNode().get_SourcePositionStart()));
        }
      }
    }
    
    for (ASTASTRule rule : a.getASTRuleList()) {
      if (allProds.containsKey(rule.getType())) {
        ProdSymbol prod = allProds.get(rule.getType());
        if (prod.isClass()) {
          for (ASTMCType type : rule.getASTSuperClassList()) {
            String simpleName = simpleName(type);
            if (!allProds.containsKey(simpleName.substring(TransformationHelper.AST_PREFIX.length()))) {
              Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT,
                  rule.getType(), simpleName,
                  rule.get_SourcePositionStart()));
            }
          }
        }
      }
    }
  }

  protected static String simpleName(ASTMCType type) {
    String name;
    if (type instanceof ASTMCGenericType) {
      name = ((ASTMCGenericType) type).printWithoutTypeArguments();
    } else if (type instanceof ASTMCArrayType) {
      name = ((ASTMCArrayType) type).printTypeWithoutBrackets();
    } else {
      name = type.printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter());
    }
    return Names.getSimpleName(name);
  }
}
