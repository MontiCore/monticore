/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.combineexpressionswithliterals._cocos;

import de.monticore.expressions.javaclassexpressions._ast.ASTClassExpression;
import de.monticore.expressions.javaclassexpressions._cocos.JavaClassExpressionsASTClassExpressionCoCo;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.monticore.types.mcsimplegenerictypes._prettyprint.MCSimpleGenericTypesFullPrettyPrinter;
import de.se_rwth.commons.logging.Log;

/**
 * prototype for the CoCo of your language. Because of the use of externals, it can only be #
 * implemented as a prototype in the language extending JavaClassExpressions.
 */
public class NoClassExpressionForGenerics implements JavaClassExpressionsASTClassExpressionCoCo {

  public static final String ERROR_CODE = "0xA0302";

  public static final String ERROR_MSG_FORMAT = " Generic types like %s cannot use the ClassExpression";

  @Override
  public void check(ASTClassExpression node) {
    checkNoGeneric(node.getMCReturnType());
  }

  private void checkNoGeneric(ASTMCReturnType returnType){
    MCSimpleGenericTypesFullPrettyPrinter prettyPrinter = new MCSimpleGenericTypesFullPrettyPrinter(new IndentPrinter());
    if(returnType.isPresentMCType()){
      ASTMCType type = returnType.getMCType();
      if(type instanceof ASTMCGenericType){
        Log.error(String.format(ERROR_CODE+ERROR_MSG_FORMAT,prettyPrinter.prettyprint(returnType)));
      }
    }
  }

}
