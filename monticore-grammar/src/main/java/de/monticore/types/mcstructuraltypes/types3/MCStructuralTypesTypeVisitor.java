/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mcstructuraltypes.types3;

import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcstructuraltypes._ast.ASTMCBracketType;
import de.monticore.types.mcstructuraltypes._ast.ASTMCIntersectionType;
import de.monticore.types.mcstructuraltypes._ast.ASTMCTupleType;
import de.monticore.types.mcstructuraltypes._ast.ASTMCUnionType;
import de.monticore.types.mcstructuraltypes._visitor.MCStructuralTypesVisitor2;
import de.monticore.types3.AbstractTypeVisitor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class MCStructuralTypesTypeVisitor extends AbstractTypeVisitor
    implements MCStructuralTypesVisitor2 {

  @Override
  public void endVisit(ASTMCUnionType unionType) {
    SymTypeExpression symType;

    List<SymTypeExpression> arguments = new LinkedList<>();
    List<ASTMCType> astArguments = transformUnionTree2List(unionType);
    if (astArguments.isEmpty()) {
      // error logged
      getType4Ast().setTypeOfTypeIdentifier(unionType,
          SymTypeExpressionFactory.createObscureType());
      return;
    }

    for (ASTMCType arg : astArguments) {
      if (getType4Ast().getPartialTypeOfTypeId(arg).isObscureType()) {
        getType4Ast().setTypeOfTypeIdentifier(unionType,
            SymTypeExpressionFactory.createObscureType());
        return;
      }
      arguments.add(getType4Ast().getPartialTypeOfTypeId(arg));
    }

    symType = SymTypeExpressionFactory.createUnion(arguments);
    getType4Ast().setTypeOfTypeIdentifier(unionType, symType);
  }

  public void endVisit(ASTMCIntersectionType intersectionType) {
    SymTypeExpression symType;

    List<SymTypeExpression> arguments = new LinkedList<>();
    List<ASTMCType> astArguments =
        transformIntersectionTree2List(intersectionType);
    if (astArguments.isEmpty()) {
      // error logged
      getType4Ast().setTypeOfTypeIdentifier(intersectionType,
          SymTypeExpressionFactory.createObscureType());
      return;
    }

    for (ASTMCType arg : astArguments) {
      if (getType4Ast().getPartialTypeOfTypeId(arg).isObscureType()) {
        getType4Ast().setTypeOfTypeIdentifier(intersectionType,
            SymTypeExpressionFactory.createObscureType());
        return;
      }
      arguments.add(getType4Ast().getPartialTypeOfTypeId(arg));
    }

    symType = SymTypeExpressionFactory.createIntersection(arguments);
    getType4Ast().setTypeOfTypeIdentifier(intersectionType, symType);
  }

  public void endVisit(ASTMCTupleType tupleType) {
    SymTypeExpression symType;

    List<SymTypeExpression> arguments = tupleType.streamMCTypes()
        .map(t -> getType4Ast().getPartialTypeOfTypeId(t))
        .collect(Collectors.toList());
    if (arguments.stream().anyMatch(SymTypeExpression::isObscureType)) {
      // error logged
      symType = SymTypeExpressionFactory.createObscureType();
    }
    else {
      symType = SymTypeExpressionFactory.createTuple(arguments);
    }

    getType4Ast().setTypeOfTypeIdentifier(tupleType, symType);
  }

  public void endVisit(ASTMCBracketType bracketType) {
    SymTypeExpression symType;
    symType = getType4Ast().getPartialTypeOfTypeId(bracketType.getMCType());
    getType4Ast().setTypeOfTypeIdentifier(bracketType, symType);
  }

  // Helper

  /**
   * converts the parsed tree to a list
   * this is a specific kind of type normalization,
   * which also reduces the complexity of the type printed,
   * which is the reason this has been added here.
   * E.g., given A | B | C, (A | B | C) is printed rather than ((A | B) | C).
   */
  protected List<ASTMCType> transformUnionTree2List(ASTMCType mcType) {
    List<ASTMCType> result = new ArrayList<>();
    // todo replace with typedispatcher as soon as the issues are fixed
    if (mcType instanceof ASTMCUnionType) {
      result.addAll(transformUnionTree2List(((ASTMCUnionType) mcType).getLeft()));
      result.addAll(transformUnionTree2List(((ASTMCUnionType) mcType).getRight()));
    }
    else {
      result.add(mcType);
    }
    return result;
  }

  /**
   * converts the parsed tree to a list
   * this is a specific kind of type normalization,
   * which also reduces the complexity of the type printed,
   * which is the reason this has been added here.
   * E.g., given A & B & C, (A & B & C) is printed rather than ((A & B) & C).
   */
  protected List<ASTMCType> transformIntersectionTree2List(ASTMCType mcType) {
    List<ASTMCType> result = new ArrayList<>();
    // todo replace with typedispatcher as soon as the issues are fixed
    if (mcType instanceof ASTMCIntersectionType) {
      result.addAll(transformIntersectionTree2List(((ASTMCIntersectionType) mcType).getLeft()));
      result.addAll(transformIntersectionTree2List(((ASTMCIntersectionType) mcType).getRight()));
    }
    else {
      result.add(mcType);
    }
    return result;
  }

}
