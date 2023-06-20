/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3.util;

import de.monticore.expressions.combineexpressionswithliterals._ast.ASTExtReturnType;
import de.monticore.expressions.combineexpressionswithliterals._ast.ASTExtType;
import de.monticore.expressions.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsVisitor2;
import de.monticore.types3.AbstractTypeVisitor;

/**
 * @deprecated only used because of the ASTExtType not being a MCType, etc.
 * to be changed
 */
@Deprecated
public class CombineExpressionsWithLiteralsTypeVisitor extends AbstractTypeVisitor
    implements CombineExpressionsWithLiteralsVisitor2 {

  @Override
  public void endVisit(ASTExtType type){
    getType4Ast().internal_setTypeOfTypeIdentifier2(
        type,
        getType4Ast().internal_getPartialTypeOfTypeId2(type.getMCType())
    );
  }

  @Override
  public void endVisit(ASTExtReturnType returnType){
    getType4Ast().internal_setTypeOfTypeIdentifier2(
        returnType,
        getType4Ast().getPartialTypeOfTypeId(returnType.getMCReturnType())
    );
  }

}
