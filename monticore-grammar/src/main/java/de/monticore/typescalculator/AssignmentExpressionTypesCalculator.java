package de.monticore.typescalculator;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.assignmentexpressions._ast.*;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsVisitor;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.EVariableSymbol;
import de.monticore.expressions.prettyprint2.ExpressionsBasisPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.*;
import de.monticore.types.mcbasictypes._symboltable.MCTypeSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.*;

public class AssignmentExpressionTypesCalculator extends ExpressionsBasisTypesCalculator implements AssignmentExpressionsVisitor {

  private AssignmentExpressionsVisitor realThis;

  @Override
  public void setRealThis(AssignmentExpressionsVisitor realThis){
    this.realThis=realThis;
  }

  @Override
  public AssignmentExpressionsVisitor getRealThis(){
    return realThis;
  }

  public AssignmentExpressionTypesCalculator(){
    realThis=this;
  }

  @Override
  public void endVisit(ASTIncSuffixExpression expr){
    ASTMCType result = calculatePlusMinusIncDecPrefixInfix(expr.getExpression());

    if(result!=null){
      this.result = result;
      MCTypeSymbol res = new MCTypeSymbol(result.getBaseName());
      res.setASTMCType(result);
      types.put(expr,res);
    }else{
      Log.error("0xA0170 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTDecSuffixExpression expr){
    ASTMCType result = calculatePlusMinusIncDecPrefixInfix(expr.getExpression());

    if(result!=null){
      this.result = result;
      MCTypeSymbol res = new MCTypeSymbol(result.getBaseName());
      res.setASTMCType(result);
      types.put(expr,res);
    }else{
      Log.error("0xA0171 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTIncPrefixExpression expr){
    ASTMCType result = calculatePlusMinusIncDecPrefixInfix(expr.getExpression());

    if(result!=null){
      this.result = result;
      MCTypeSymbol res = new MCTypeSymbol(result.getBaseName());
      res.setASTMCType(result);
      types.put(expr,res);
    }else{
      Log.error("0xA0172 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTDecPrefixExpression expr){
    ASTMCType result = calculatePlusMinusIncDecPrefixInfix(expr.getExpression());

    if(result!=null){
      this.result = result;
      MCTypeSymbol res = new MCTypeSymbol(result.getBaseName());
      res.setASTMCType(result);
      types.put(expr,res);
    }else{
      Log.error("0xA0173 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTPlusPrefixExpression expr){
    ASTMCType result = calculatePlusMinusIncDecPrefixInfix(expr.getExpression());
    if(result!=null){
      this.result = result;
      MCTypeSymbol res = new MCTypeSymbol(result.getBaseName());
      res.setASTMCType(result);
      types.put(expr,res);
    }else{
      Log.error("0xA0174 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTMinusPrefixExpression expr){
    ASTMCType result = calculatePlusMinusIncDecPrefixInfix(expr.getExpression());
    if(result!=null){
      this.result = result;
      MCTypeSymbol res = new MCTypeSymbol(result.getBaseName());
      res.setASTMCType(result);
      types.put(expr,res);
    }else{
      Log.error("0xA0175 The resulting type cannot be calculated");
    }
  }

  private ASTMCType calculatePlusMinusIncDecPrefixInfix(ASTExpression expr){
    ASTMCType result = null;
    if(types.containsKey(expr)){
      if(types.get(expr).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build())){
        result=MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build();
      }else if(types.get(expr).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build())){
        result=MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build();
      }
    }
    return result;
  }

  private void calculatePlusAssignment(ASTRegularAssignmentExpression expr){
    ASTMCType result = calculateTypeArithmetic(expr.getLeft(),expr.getRight());
    if(types.containsKey(expr.getLeft())&&types.containsKey(expr.getRight())) {
      List<String> name = new ArrayList<>();
      name.add("String");
      if(types.get(expr.getLeft()).deepEqualsWithType(MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build())&&types.get(expr.getRight()).deepEqualsWithType(MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build())){
        result = MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build();
      }
      ArrayList<String> nameB = new ArrayList<>();
      nameB.add("String");
      nameB.add(0,"lang");
      nameB.add(0,"java");
      if(types.get(expr.getLeft()).deepEqualsWithType(MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(nameB).build()).build())&&types.get(expr.getRight()).deepEqualsWithType(MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(nameB).build()).build())){
        result = MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(nameB).build()).build();
      }
      if(types.get(expr.getLeft()).deepEqualsWithType(MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build())&&types.get(expr.getRight()).deepEqualsWithType(MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(nameB).build()).build())
       ||types.get(expr.getLeft()).deepEqualsWithType(MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(nameB).build()).build())&&types.get(expr.getRight()).deepEqualsWithType(MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build())){
        result = MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build();
      }
    }
    if(result!=null){
      this.result = result;
      MCTypeSymbol res = new MCTypeSymbol(result.getBaseName());
      res.setASTMCType(result);
      types.put(expr,res);
    }else{
      Log.error("0xA0176 The resulting type cannot be calculated");
    }
  }

  private void calculateMinusAssignment(ASTRegularAssignmentExpression expr){
    ASTMCType result = calculateTypeArithmetic(expr.getLeft(),expr.getRight());
    if(result!=null){
      this.result = result;
      MCTypeSymbol res = new MCTypeSymbol(result.getBaseName());
      res.setASTMCType(result);
      types.put(expr,res);
    }else{
      Log.error("0xA0177 The resulting type cannot be calculated");
    }
  }

  private void calculateMultAssignment(ASTRegularAssignmentExpression expr){
    ASTMCType result = calculateTypeArithmetic(expr.getLeft(),expr.getRight());
    if(result!=null){
      this.result = result;
      MCTypeSymbol res = new MCTypeSymbol(result.getBaseName());
      res.setASTMCType(result);
      types.put(expr,res);
    }else{
      Log.error("0xA0178 The resulting type cannot be calculated");
    }
  }

  private void calculateDivideAssignment(ASTRegularAssignmentExpression expr){
    ASTMCType result = calculateTypeArithmetic(expr.getLeft(),expr.getRight());
    if(result!=null){
      this.result = result;
      MCTypeSymbol res = new MCTypeSymbol(result.getBaseName());
      res.setASTMCType(result);
      types.put(expr,res);
    }else{
      Log.error("0xA0179 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTRegularAssignmentExpression expr){
    Optional<EVariableSymbol> leftEx = scope.resolveEVariable(new ExpressionsBasisPrettyPrinter(new IndentPrinter()).prettyprint(expr.getLeft()));
    if(!leftEx.isPresent()){
      Log.error("0xA0180 The resulting type cannot be calculated");
    }
    if(expr.getOperator()==ASTConstantsAssignmentExpressions.PLUSEQUALS){
      calculatePlusAssignment(expr);
    }else if(expr.getOperator()==ASTConstantsAssignmentExpressions.MINUSEQUALS){
      calculateMinusAssignment(expr);
    }else if(expr.getOperator()==ASTConstantsAssignmentExpressions.STAREQUALS){
      calculateMultAssignment(expr);
    }else if(expr.getOperator()==ASTConstantsAssignmentExpressions.SLASHEQUALS){
      calculateDivideAssignment(expr);
    }else if(expr.getOperator()==ASTConstantsAssignmentExpressions.ANDEQUALS){
      calculateAndAssigment(expr);
    }else if(expr.getOperator()==ASTConstantsAssignmentExpressions.PIPEEQUALS){
      calculateOrAssignment(expr);
    }else if(expr.getOperator()==ASTConstantsAssignmentExpressions.GTGTEQUALS){
      calculateDoubleRightAssignment(expr);
    }else if(expr.getOperator()==ASTConstantsAssignmentExpressions.LTLTEQUALS){
      calculateDoubleLeftAssignment(expr);
    }else if(expr.getOperator()==ASTConstantsAssignmentExpressions.GTGTGTEQUALS){
      calculateLogicalRightAssignment(expr);
    }else if(expr.getOperator()==ASTConstantsAssignmentExpressions.ROOFEQUALS){
      calculateBinaryXorAssignment(expr);
    }else if(expr.getOperator()==ASTConstantsAssignmentExpressions.PERCENTEQUALS){
      calculateModuloAssignment(expr);
    }else {
      ASTMCType result = null;
      Optional<EVariableSymbol> left = Optional.empty();
      Optional<EVariableSymbol> right = Optional.empty();
      if (types.get(expr.getLeft()).getEVariableSymbol() != null && types.get(expr.getRight()).getEVariableSymbol() != null) {
        left = scope.resolveEVariable(types.get(expr.getLeft()).getEVariableSymbol().getName());
        right = scope.resolveEVariable(types.get(expr.getRight()).getEVariableSymbol().getName());
      }

      if (types.containsKey(expr.getLeft()) && types.containsKey(expr.getRight())) {
        if (types.get(expr.getLeft()).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build()) && types.get(expr.getRight()).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build())) {
          result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build();
        }
        else if (types.get(expr.getLeft()).deepEquals(types.get(expr.getRight()))) {
          result = types.get(expr.getLeft()).getASTMCType().deepClone();
        }
        else if (left.isPresent() && right.isPresent()) {
          if (left.get().getMCTypeSymbol().getSubtypes().contains(right.get().getMCTypeSymbol())) {
            result = types.get(expr.getLeft()).getASTMCType().deepClone();
          }
        }
      }
      if (result != null) {
        this.result = result;
        MCTypeSymbol res = new MCTypeSymbol(result.getBaseName());
        res.setASTMCType(result);
        types.put(expr, res);
      }
      else {
        Log.error("0xA0180 The resulting type cannot be calculated");
      }
    }
  }

  private void calculateAndAssigment(ASTRegularAssignmentExpression expr){
    //definiert auf boolean - boolean und ganzzahl - ganzzahl
    ASTMCType result = calculateTypeBinaryOperations(expr.getLeft(),expr.getRight());
    if(result!=null){
      this.result=result;
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr,sym);
    }else{
      Log.error("0xA0181 The resulting type cannot be calculated");
    }
  }

  private void calculateOrAssignment(ASTRegularAssignmentExpression expr){
    //definiert auf boolean - boolean und ganzzahl - ganzzahl
    ASTMCType result = calculateTypeBinaryOperations(expr.getLeft(),expr.getRight());
    if(result!=null){
      this.result=result;
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr,sym);
    }else{
      Log.error("0xA0182 The resulting type cannot be calculated");
    }
  }

  private void calculateBinaryXorAssignment(ASTRegularAssignmentExpression expr){
    //definiert auf boolean - boolean und ganzzahl - ganzzahl
    ASTMCType result = calculateTypeBinaryOperations(expr.getLeft(),expr.getRight());
    if(result!=null){
      this.result=result;
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr,sym);
    }else{
      Log.error("0xA0183 The resulting type cannot be calculated");
    }
  }

  private void calculateDoubleRightAssignment(ASTRegularAssignmentExpression expr){
    //definiert auf Ganzzahl - Ganzzahl
    ASTMCType result = calculateTypeBitOperation(expr.getLeft(),expr.getRight());
    if(result!=null){
      this.result=result;
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr,sym);
    }else{
      Log.error("0xA0184 The resulting type cannot be calculated");
    }
  }

  private void calculateDoubleLeftAssignment(ASTRegularAssignmentExpression expr){
    //definiert auf Ganzzahl - Ganzzahl
    ASTMCType result = calculateTypeBitOperation(expr.getLeft(),expr.getRight());
    if(result!=null){
      this.result=result;
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr,sym);
    }else{
      Log.error("0xA0185 The resulting type cannot be calculated");
    }
  }

  private void calculateLogicalRightAssignment(ASTRegularAssignmentExpression expr){
    //definiert auf Ganzzahl - Ganzzahl
    ASTMCType result = calculateTypeBitOperation(expr.getLeft(),expr.getRight());
    if(result!=null){
      this.result=result;
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr,sym);
    }else{
      Log.error("0xA0186 The resulting type cannot be calculated");
    }
  }

  private void calculateModuloAssignment(ASTRegularAssignmentExpression expr){
    ASTMCType result = calculateTypeArithmetic(expr.getLeft(),expr.getRight());
    if(result!=null){
      this.result = result;
      MCTypeSymbol res = new MCTypeSymbol(result.getBaseName());
      res.setASTMCType(result);
      types.put(expr,res);
    }else{
      Log.error("0xA0187 The resulting type cannot be calculated");
    }
  }

  private ASTMCType calculateTypeArithmetic(ASTExpression left, ASTExpression right){
    ASTMCType result = null;
    if(types.containsKey(left)&&types.containsKey(right)) {
      if (types.get(left).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build()) && types.get(right).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build())) {
        result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build();
      }
      else if (types.get(left).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build()) && types.get(right).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build())) {
        result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build();
      }
      else if (types.get(left).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build()) && types.get(right).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build())){
        result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build();
      }else if(types.get(left).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build()) && types.get(right).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build())) {
        result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build();
      }
    }
    return result;
  }

  private ASTMCType calculateTypeBitOperation(ASTExpression left, ASTExpression right){
    ASTMCType result = null;
    if(types.containsKey(left)&&types.containsKey(right)){
      if(types.get(left).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build())&&types.get(right).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build())){
        result=MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build();
      }
    }
    return result;
  }

  private ASTMCType calculateTypeBinaryOperations(ASTExpression left, ASTExpression right){
    ASTMCType result = null;
    if(types.containsKey(left)&&types.containsKey(right)){
      if(types.get(left).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build())&&types.get(right).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build())){
        result=MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build();
      }else if(types.get(left).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build())&&types.get(right).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build())) {
        result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build();
      }
    }
    return result;
  }

  public void setTypes(Map<ASTNode,MCTypeSymbol> types){
    this.types=types;
  }

  public ASTMCType calculateType(ASTExpression expr){
    expr.accept(realThis);
    return types.get(expr).getASTMCType();
  }

  //TODO: bisher nur double und int behandelt, bei += auch String, es fehlen noch RegularAssignmentExpr, AndAssignmentExpr, OrAssignmentExpr, BinaryXorAssignmentExpr, RightShiftAssignmentExpr, LeftShiftAssignmentExpr, LogicalRightAssignmentExpr und die Tests
}
