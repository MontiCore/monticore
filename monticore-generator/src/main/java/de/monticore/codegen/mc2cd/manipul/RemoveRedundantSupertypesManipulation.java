/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.manipul;

import com.google.common.collect.Iterables;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDInterface;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.utils.ASTNodes;

import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.StreamSupport;

public class RemoveRedundantSupertypesManipulation implements UnaryOperator<ASTCDCompilationUnit> {
  
  @Override
  public ASTCDCompilationUnit apply(ASTCDCompilationUnit cdCompilationUnit) {
    for (ASTCDClass cdClass : ASTNodes.getSuccessors(cdCompilationUnit, ASTCDClass.class)) {
      // TODO SO <- GV: don't need it any more?
      // removeRedundantSuperTypes(cdClass.getSuperclass());
      removeRedundantSuperTypes(cdClass.getInterfaceList());
    }
    for (ASTCDInterface cdInterface : ASTNodes.getSuccessors(cdCompilationUnit,
        ASTCDInterface.class)) {
      removeRedundantSuperTypes(cdInterface.getInterfaceList());
    }
    return cdCompilationUnit;
  }
  
  private void removeRedundantSuperTypes(List<ASTMCQualifiedType> superClasses) {
    for (int i = 0; i < superClasses.size();) {
      ASTMCQualifiedType inspectedAttribute = superClasses.get(i);
      Iterable<ASTMCQualifiedType> remainingAttributes = Iterables.filter(superClasses,
          attribute -> !attribute.equals(inspectedAttribute));
      if (isRedundant(inspectedAttribute, remainingAttributes)) {
        superClasses.remove(i);
      }
      else {
        i++;
      }
    }
  }
  
  private boolean isRedundant(ASTMCQualifiedType inspectedReference,
      Iterable<ASTMCQualifiedType> remainingReferences) {
    return StreamSupport.stream(remainingReferences.spliterator(), false)
        .anyMatch(
            ref -> TransformationHelper.typeToString(inspectedReference).equals(
                TransformationHelper.typeToString(ref)));
  }
}
