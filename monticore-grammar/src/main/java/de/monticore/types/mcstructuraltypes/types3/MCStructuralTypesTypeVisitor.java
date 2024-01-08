/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mcstructuraltypes.types3;

import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcstructuraltypes._ast.ASTMCIntersectionType;
import de.monticore.types.mcstructuraltypes._ast.ASTMCUnionType;
import de.monticore.types.mcstructuraltypes._visitor.MCStructuralTypesVisitor2;
import de.monticore.types3.AbstractTypeVisitor;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MCStructuralTypesTypeVisitor extends AbstractTypeVisitor
    implements MCStructuralTypesVisitor2 {

  @Override
  public void endVisit(ASTMCUnionType unionType) {
    SymTypeExpression symType;

    List<SymTypeExpression> arguments = new LinkedList<>();
    List<ASTMCType> astArguments =
        transformUnionTree2List(unionType.getMCTypeList());
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
        transformIntersectionTree2List(intersectionType.getMCTypeList());
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

  // Helper

  /**
   * in case that the AST has not been modified,
   * converts the parsed tree to a list
   */
  protected List<ASTMCType> transformUnionTree2List(List<ASTMCType> mcTypes) {
    List<ASTMCType> result = new ArrayList<>();
    for (ASTMCType mcType : mcTypes) {
      // todo replace with typedispatcher as soon as the issues are fixed
      if (mcType instanceof ASTMCUnionType &&
          !((ASTMCUnionType) mcType).isPresentParenthesis()) {
        List<ASTMCType> flattenedAstTypes = transformUnionTree2List(
            ((ASTMCUnionType) mcType).getMCTypeList()
        );
        result.addAll(flattenedAstTypes);
      }
      else if (mcType instanceof ASTMCIntersectionType &&
          !((ASTMCIntersectionType) mcType).isPresentParenthesis()) {
        Log.errorUser("0xFD770 use parenthesis when combining "
                + "union and intersection types",
            mcType.get_SourcePositionStart(),
            mcType.get_SourcePositionEnd()
        );
        return Collections.emptyList();
      }
      else {
        result.add(mcType);
      }
    }
    return result;
  }

  /**
   * in case that the AST has not been modified,
   * converts the parsed tree to a list
   */
  protected List<ASTMCType> transformIntersectionTree2List(List<ASTMCType> mcTypes) {
    List<ASTMCType> result = new ArrayList<>();
    for (ASTMCType mcType : mcTypes) {
      // todo replace with typedispatcher as soon as the issues are fixed
      if (mcType instanceof ASTMCIntersectionType &&
          !((ASTMCIntersectionType) mcType).isPresentParenthesis()) {
        List<ASTMCType> flattenedAstTypes = transformIntersectionTree2List(
            ((ASTMCIntersectionType) mcType).getMCTypeList()
        );
        result.addAll(flattenedAstTypes);
      }
      else if (mcType instanceof ASTMCUnionType &&
          !((ASTMCUnionType) mcType).isPresentParenthesis()) {
        Log.errorUser("0xFD771 use parenthesis when combining "
                + "union and intersection types",
            mcType.get_SourcePositionStart(),
            mcType.get_SourcePositionEnd()
        );
        return Collections.emptyList();
      }
      else {
        result.add(mcType);
      }
    }
    return result;
  }

}
