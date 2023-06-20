/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mcarraytypes.types3;

import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mcarraytypes._ast.ASTMCArrayType;
import de.monticore.types.mcarraytypes._visitor.MCArrayTypesVisitor2;
import de.monticore.types3.AbstractTypeVisitor;
import de.se_rwth.commons.logging.Log;

public class MCArrayTypesTypeVisitor extends AbstractTypeVisitor
    implements MCArrayTypesVisitor2 {

  @Override
  public void endVisit(ASTMCArrayType arrayType) {
    SymTypeExpression type;
    SymTypeExpression innerType =
        getType4Ast().getPartialTypeOfTypeId(arrayType.getMCType());
    if (innerType.isObscureType()) {
      Log.error("0xE9CDC The type of the array could not be synthesized",
          arrayType.get_SourcePositionStart(),
          arrayType.get_SourcePositionEnd()
      );
      type = SymTypeExpressionFactory.createObscureType();
    }
    else {
      type = SymTypeExpressionFactory
          .createTypeArray(innerType, arrayType.getDimensions());
    }
    getType4Ast().setTypeOfTypeIdentifier(arrayType, type);
  }
}
