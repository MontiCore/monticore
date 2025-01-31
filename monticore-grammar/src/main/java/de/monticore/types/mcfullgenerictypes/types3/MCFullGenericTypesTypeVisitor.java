/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mcfullgenerictypes.types3;

import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCMultipleGenericType;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCWildcardTypeArgument;
import de.monticore.types.mcfullgenerictypes._visitor.MCFullGenericTypesVisitor2;
import de.monticore.types3.AbstractTypeVisitor;
import de.se_rwth.commons.logging.Log;

public class MCFullGenericTypesTypeVisitor extends AbstractTypeVisitor
    implements MCFullGenericTypesVisitor2 {

  @Override
  public void endVisit(ASTMCWildcardTypeArgument wildcardType) {
    SymTypeExpression tex;
    if (wildcardType.isPresentLowerBound()) {
      if (getType4Ast().getPartialTypeOfTypeId((wildcardType.getLowerBound()))
          .isObscureType()) {
        Log.error("0xE9CDD The lower bound type of the wildcard type " +
                "could not be synthesized.",
            wildcardType.get_SourcePositionStart(),
            wildcardType.get_SourcePositionEnd()
        );
        getType4Ast().setTypeOfTypeIdentifier(wildcardType,
            SymTypeExpressionFactory.createObscureType());
        return;
      }
      if (!getType4Ast()
          .getPartialTypeOfTypeId(wildcardType.getLowerBound())
          .isObscureType()) {
        tex = SymTypeExpressionFactory.createWildcard(false,
            getType4Ast().getPartialTypeOfTypeId(wildcardType.getLowerBound()));
      }
      else {
        tex = SymTypeExpressionFactory.createObscureType();
      }
    }
    else if (wildcardType.isPresentUpperBound()) {
      if (getType4Ast()
          .getPartialTypeOfTypeId((wildcardType.getUpperBound()))
          .isObscureType()) {
        Log.error("0xE9CDA The upper bound type of the wildcard type " +
                "could not be synthesized.",
            wildcardType.get_SourcePositionStart(),
            wildcardType.get_SourcePositionEnd()
        );
        getType4Ast().setTypeOfTypeIdentifier(wildcardType,
            SymTypeExpressionFactory.createObscureType());
        return;
      }
      if (!getType4Ast()
          .getPartialTypeOfTypeId(wildcardType.getUpperBound())
          .isObscureType()) {
        tex = SymTypeExpressionFactory.createWildcard(true,
            getType4Ast().getPartialTypeOfTypeId(wildcardType.getUpperBound()));
      }
      else {
        tex = SymTypeExpressionFactory.createObscureType();
      }
    }
    else {
      tex = SymTypeExpressionFactory.createWildcard();
    }
    getType4Ast().setTypeOfTypeIdentifier(wildcardType, tex);
  }

  @Override
  public void endVisit(ASTMCMultipleGenericType node) {
    Log.error("0xFD577 ASTMCMultipleGenericType not supported (yet).",
        node.get_SourcePositionStart(),
        node.get_SourcePositionEnd()
    );
  }

}
