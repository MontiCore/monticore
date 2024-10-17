/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.typeparameters.cocos;

import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeVariable;
import de.monticore.types.typeparameters._ast.ASTTypeParameter;
import de.monticore.types.typeparameters._cocos.TypeParametersASTTypeParameterCoCo;
import de.monticore.types3.SymTypeRelations;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.types.check.SymTypeExpressionFactory.createTypeVariable;

/**
 * Finds instances of circular inheritance,
 * e.g., <T extends U, U extends Person & T>
 */
public class TypeParameterNoCyclicInheritance
    implements TypeParametersASTTypeParameterCoCo {

  @Override
  public void check(ASTTypeParameter node) {
    SymTypeVariable thisVar = createTypeVariable(node.getSymbol());
    checkForCircularInheritance(List.of(thisVar), node);
  }

  protected boolean checkForCircularInheritance(
      List<SymTypeExpression> currentInheritanceList,
      ASTTypeParameter node
  ) {
    List<SymTypeExpression> superTypes = SymTypeRelations.getNominalSuperTypes(
        currentInheritanceList.get(currentInheritanceList.size() - 1)
    );
    for (SymTypeExpression superType : superTypes) {
      if (currentInheritanceList.stream().anyMatch(superType::deepEquals)) {
        Log.error("0xFDC12 Checked supertypes of type variable \""
                + node.getName()
                + "\" and found circular inheritance of type "
                + superType.printFullName(),
            node.get_SourcePositionStart(),
            node.get_SourcePositionEnd()
        );
        return false;
      }
      else {
        List<SymTypeExpression> nextInheritanceList = new ArrayList<>();
        nextInheritanceList.addAll(currentInheritanceList);
        nextInheritanceList.add(superType);
        if (!checkForCircularInheritance(nextInheritanceList, node)) {
          return false;
        }
      }
    }
    return true;
  }

}
