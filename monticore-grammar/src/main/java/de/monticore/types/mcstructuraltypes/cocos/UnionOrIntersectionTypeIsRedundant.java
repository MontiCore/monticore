/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mcstructuraltypes.cocos;

import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfIntersection;
import de.monticore.types.check.SymTypeOfUnion;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcstructuraltypes.MCStructuralTypesMill;
import de.monticore.types.mcstructuraltypes._ast.ASTMCIntersectionType;
import de.monticore.types.mcstructuraltypes._ast.ASTMCUnionType;
import de.monticore.types.mcstructuraltypes._cocos.MCStructuralTypesASTMCIntersectionTypeCoCo;
import de.monticore.types.mcstructuraltypes._cocos.MCStructuralTypesASTMCUnionTypeCoCo;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Checks if UnionType or IntersectionType is declared redundantly, <br>
 * e.g. {@code Person | Student} is equivalent to {@code Person} <br>
 * and {@code Person & Student} is equivalent to {@code Student}
 */
public class UnionOrIntersectionTypeIsRedundant implements
    MCStructuralTypesASTMCUnionTypeCoCo,
    MCStructuralTypesASTMCIntersectionTypeCoCo {

  @Override
  public void check(ASTMCUnionType node) {
    SymTypeExpression type = TypeCheck3.symTypeFromAST(node);

    if (type.isUnionType()) {
      SymTypeOfUnion union = type.asUnionType();
      List<SymTypeExpression> unionList = new ArrayList<>(union.getUnionizedTypeSet());

      checkRedundancy(unionList, node);
    }
  }

  @Override
  public void check(ASTMCIntersectionType node) {
    SymTypeExpression type = TypeCheck3.symTypeFromAST(node);

    if (type.isIntersectionType()) {
      SymTypeOfIntersection intersection = type.asIntersectionType();
      List<SymTypeExpression> intersectionList = new ArrayList<>(intersection.getIntersectedTypeSet());

      checkRedundancy(intersectionList, node);
    }
  }

  protected void checkRedundancy(List<SymTypeExpression> expressionList, ASTMCType node) {
    for (int i = 0; i < expressionList.size(); i++) {
      for (int j = 0; j < expressionList.size(); j++) {
        if (i != j) {
          if (SymTypeRelations.isSubTypeOf(expressionList.get(i), expressionList.get(j))) {
            Log.warn(
              String.format("0xE0000 Declaration %s is redundant because %s is a subtype of %s.",
                MCStructuralTypesMill.prettyPrint(node, false),
                expressionList.get(i).printFullName(),
                expressionList.get(j).printFullName()
              ),
              node.get_SourcePositionStart(),
              node.get_SourcePositionEnd()
            );
          }
        }
      }
    }
  }

}
