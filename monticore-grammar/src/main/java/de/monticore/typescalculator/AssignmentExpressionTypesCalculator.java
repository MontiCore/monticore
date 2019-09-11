/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typescalculator;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.assignmentexpressions._ast.*;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsVisitor;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.prettyprint2.ExpressionsBasisPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import de.monticore.types2.SymTypeConstant;
import de.monticore.types2.SymTypeExpression;
import de.monticore.types2.SymTypeOfObject;
import de.se_rwth.commons.logging.Log;

import java.util.Map;
import java.util.Optional;

import static de.monticore.typescalculator.TypesCalculator.isSubtypeOf;
import static de.monticore.typescalculator.TypesCalculatorHelper.*;

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
    SymTypeExpression result = getUnaryNumericPromotionType(expr.getExpression());

    if(result!=null){
      SymTypeExpression sym = result.deepClone();
      sym.setName(result.getName());
      types.put(expr, sym);
      this.result = sym;
    }else{
      Log.error("0xA0170 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTDecSuffixExpression expr){
    SymTypeExpression result = getUnaryNumericPromotionType(expr.getExpression());

    if(result!=null){
      SymTypeExpression sym = result.deepClone();
      sym.setName(result.getName());
      types.put(expr, sym);
      this.result = sym;
    }else{
      Log.error("0xA0171 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTIncPrefixExpression expr){
    SymTypeExpression result = getUnaryNumericPromotionType(expr.getExpression());

    if(result!=null){
      SymTypeExpression sym = result.deepClone();
      sym.setName(result.getName());
      types.put(expr, sym);
      this.result = sym;
    }else{
      Log.error("0xA0172 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTDecPrefixExpression expr){
    SymTypeExpression result = getUnaryNumericPromotionType(expr.getExpression());

    if(result!=null){
      SymTypeExpression sym = result.deepClone();
      sym.setName(result.getName());
      types.put(expr, sym);
      this.result = sym;
    }else{
      Log.error("0xA0173 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTPlusPrefixExpression expr){
    SymTypeExpression result = getUnaryNumericPromotionType(expr.getExpression());
    if(result!=null){
      SymTypeExpression sym = result.deepClone();
      sym.setName(result.getName());
      types.put(expr, sym);
      this.result = sym;
    }else{
      Log.error("0xA0174 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTMinusPrefixExpression expr){
    SymTypeExpression result = getUnaryNumericPromotionType(expr.getExpression());
    if(result!=null){
      SymTypeExpression sym = result.deepClone();
      sym.setName(result.getName());
      types.put(expr, sym);
      this.result = sym;
    }else{
      Log.error("0xA0175 The resulting type cannot be calculated");
    }
  }

  private void calculatePlusAssignment(ASTRegularAssignmentExpression expr){
    SymTypeExpression result = calculateTypeArithmeticWithString(expr.getLeft(),expr.getRight());
    if(types.containsKey(expr.getLeft())&&types.containsKey(expr.getRight())) {
      if("String".equals(types.get(expr.getLeft()).getName())&&"String".equals(types.get(expr.getRight()).getName())){
        result = new SymTypeOfObject();
        result.setName("String");
      }
      if("java.lang.String".equals(types.get(expr.getLeft()).getName())&&"java.lang.String".equals(types.get(expr.getRight()).getName())){
        result = new SymTypeOfObject();
        result.setName("String");
      }
      if(("String".equals(types.get(expr.getLeft()).getName())&&"java.lang.String".equals(types.get(expr.getRight()).getName()))
          ||("java.lang.String".equals(types.get(expr.getLeft()).getName())&&"String".equals(types.get(expr.getRight()).getName()))){
        result = new SymTypeOfObject();
        result.setName("String");
      }
    }
    if(result!=null){
      SymTypeExpression sym = result.deepClone();
      sym.setName(result.getName());
      types.put(expr, sym);
      this.result = sym;
    }else{
      Log.error("0xA0176 The resulting type cannot be calculated");
    }
  }

  private void calculateMinusAssignment(ASTRegularAssignmentExpression expr){
    SymTypeExpression result = calculateTypeArithmetic(expr.getLeft(),expr.getRight());
    if(result!=null){
      SymTypeExpression sym = result.deepClone();
      sym.setName(result.getName());
      types.put(expr, sym);
      this.result = sym;
    }else{
      Log.error("0xA0177 The resulting type cannot be calculated");
    }
  }

  private void calculateMultAssignment(ASTRegularAssignmentExpression expr){
    SymTypeExpression result = calculateTypeArithmetic(expr.getLeft(),expr.getRight());
    if(result!=null){
      SymTypeExpression sym = result.deepClone();
      sym.setName(result.getName());
      types.put(expr, sym);
      this.result = sym;
    }else{
      Log.error("0xA0178 The resulting type cannot be calculated");
    }
  }

  private void calculateDivideAssignment(ASTRegularAssignmentExpression expr){
    SymTypeExpression result = calculateTypeArithmetic(expr.getLeft(),expr.getRight());
    if(result!=null){
      SymTypeExpression sym = result.deepClone();
      sym.setName(result.getName());
      types.put(expr, sym);
      this.result = sym;
    }else{
      Log.error("0xA0179 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTRegularAssignmentExpression expr){
    Optional<FieldSymbol> leftEx = scope.resolveField(new ExpressionsBasisPrettyPrinter(new IndentPrinter()).prettyprint(expr.getLeft()));
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
      if (types.containsKey(expr.getLeft()) && types.containsKey(expr.getRight())) {
        SymTypeExpression right =types.get(expr.getRight());
        SymTypeExpression left = types.get(expr.getLeft());
        result=calculateRegularAssignment(expr.getLeft(),expr.getRight());
        if (isSubtypeOf(right,left)||right.getName().equals(left.getName())) {
          result = types.get(expr.getLeft()).deepClone();
        }
      }
      if (result != null) {
        SymTypeExpression sym = result.deepClone();
        sym.setName(result.getName());
        types.put(expr, sym);
        this.result = sym;
      }
      else {
        Log.error("0xA0180 The resulting type cannot be calculated");
      }
    }
  }

  private void calculateAndAssigment(ASTRegularAssignmentExpression expr){
    //definiert auf boolean - boolean und ganzzahl - ganzzahl
    SymTypeExpression result = calculateTypeBinaryOperations(expr.getLeft(),expr.getRight());
    if(result!=null){
      SymTypeExpression sym = result.deepClone();
      sym.setName(result.getName());
      types.put(expr, sym);
      this.result = sym;
    }else{
      Log.error("0xA0181 The resulting type cannot be calculated");
    }
  }

  private void calculateOrAssignment(ASTRegularAssignmentExpression expr){
    //definiert auf boolean - boolean und ganzzahl - ganzzahl
    SymTypeExpression result = calculateTypeBinaryOperations(expr.getLeft(),expr.getRight());
    if(result!=null){
      SymTypeExpression sym = result.deepClone();
      sym.setName(result.getName());
      types.put(expr, sym);
      this.result = sym;
    }else{
      Log.error("0xA0182 The resulting type cannot be calculated");
    }
  }

  private void calculateBinaryXorAssignment(ASTRegularAssignmentExpression expr){
    //definiert auf boolean - boolean und ganzzahl - ganzzahl
    SymTypeExpression result = calculateTypeBinaryOperations(expr.getLeft(),expr.getRight());
    if(result!=null){
      SymTypeExpression sym = result.deepClone();
      sym.setName(result.getName());
      types.put(expr, sym);
      this.result = sym;
    }else{
      Log.error("0xA0183 The resulting type cannot be calculated");
    }
  }

  private void calculateDoubleRightAssignment(ASTRegularAssignmentExpression expr){
    //definiert auf Ganzzahl - Ganzzahl
    SymTypeExpression result = calculateTypeBitOperation(expr.getLeft(),expr.getRight());
    if(result!=null){
      SymTypeExpression sym = result.deepClone();
      sym.setName(result.getName());
      types.put(expr, sym);
      this.result = sym;
    }else{
      Log.error("0xA0184 The resulting type cannot be calculated");
    }
  }

  private void calculateDoubleLeftAssignment(ASTRegularAssignmentExpression expr){
    //definiert auf Ganzzahl - Ganzzahl
    SymTypeExpression result = calculateTypeBitOperation(expr.getLeft(),expr.getRight());
    if(result!=null){
      SymTypeExpression sym = result.deepClone();
      sym.setName(result.getName());
      types.put(expr, sym);
      this.result = sym;
    }else{
      Log.error("0xA0185 The resulting type cannot be calculated");
    }
  }

  private void calculateLogicalRightAssignment(ASTRegularAssignmentExpression expr){
    //definiert auf Ganzzahl - Ganzzahl
    SymTypeExpression result = calculateTypeBitOperation(expr.getLeft(),expr.getRight());
    if(result!=null){
      SymTypeExpression sym = result.deepClone();
      sym.setName(result.getName());
      types.put(expr, sym);
      this.result = sym;
    }else{
      Log.error("0xA0186 The resulting type cannot be calculated");
    }
  }

  private void calculateModuloAssignment(ASTRegularAssignmentExpression expr){
    SymTypeExpression result = calculateTypeArithmetic(expr.getLeft(),expr.getRight());
    if(result!=null){
      SymTypeExpression sym = result.deepClone();
      sym.setName(result.getName());
      types.put(expr, sym);
      this.result = sym;
    }else{
      Log.error("0xA0187 The resulting type cannot be calculated");
    }
  }

  public SymTypeExpression getBinaryNumericPromotion(ASTExpression leftType,
                                                     ASTExpression rightType) {
    SymTypeExpression res = null;
    if(types.containsKey(leftType)&&types.containsKey(rightType)&&isPrimitiveType(types.get(leftType))&&isPrimitiveType(types.get(rightType))){
      if("double".equals(unbox(types.get(leftType)).getName())||"double".equals(unbox(types.get(rightType)).getName())){
        res = new SymTypeConstant();
        res.setName("double");
      }
      if("float".equals(unbox(types.get(leftType)).getName())||"float".equals(unbox(types.get(rightType)).getName())){
        res=new SymTypeConstant();
        res.setName("float");
      }
      if("long".equals(unbox(types.get(leftType)).getName())||"long".equals(unbox(types.get(rightType)).getName())){
        res = new SymTypeConstant();
        res.setName("long");
      }
      res = new SymTypeConstant();
      res.setName("int");
    }
    return res;
  }

  public SymTypeExpression getUnaryNumericPromotionType(ASTExpression expr){
    if(types.containsKey(expr)) {
      if ("byte".equals(unbox(types.get(expr)).getName()) || "short".equals(unbox(types.get(expr)).getName()) || "char".equals(unbox(types.get(expr)).getName()) || "int".equals(unbox(types.get(expr)).getName())) {
        SymTypeExpression sym = new SymTypeConstant();
        sym.setName("int");
        return sym;
      }
      if ("long".equals(unbox(types.get(expr)).getName()) || "double".equals(unbox(types.get(expr)).getName()) || "float".equals(unbox(types.get(expr)).getName())) {
        return unbox(types.get(expr)).deepClone();
      }
    }
    return null;
  }

  private SymTypeExpression calculateTypeArithmetic(ASTExpression left, ASTExpression right){
    SymTypeExpression result = null;
    if(types.containsKey(left)&&types.containsKey(right)) {
      if(isNumericType(types.get(left))&&isNumericType(types.get(right))){
        result = types.get(left).deepClone();
      }
    }
    return result;
  }

  private SymTypeExpression calculateTypeArithmeticWithString(ASTExpression left, ASTExpression right){
    SymTypeExpression result = null;
    if(types.containsKey(left)&&types.containsKey(right)){
      if("String".equals(unbox(types.get(left)).getName())){
        result= new SymTypeOfObject();
        result.setName("String");
      }else{
        result=calculateTypeArithmetic(left,right);
      }
    }
    return result;
  }

  private SymTypeExpression calculateTypeBitOperation(ASTExpression left, ASTExpression right){
    SymTypeExpression result = null;
    if(types.containsKey(left)&&types.containsKey(right)){
      if(isIntegralType(types.get(left))&&isIntegralType(types.get(right))){
        result=types.get(left).deepClone();
      }
    }
    return result;
  }

  private SymTypeExpression calculateTypeBinaryOperations(ASTExpression left, ASTExpression right){
    SymTypeExpression result = null;
    if(types.containsKey(left)&&types.containsKey(right)){
      if(isIntegralType(types.get(left))&&isIntegralType(types.get(right))){
        result=types.get(left).deepClone();
      }else if("boolean".equals(types.get(left).getName())&&"boolean".equals(types.get(right).getName())) {
        result = new SymTypeConstant();
        result.setName("boolean");
      }
    }
    return result;
  }

  public SymTypeExpression calculateRegularAssignment(ASTExpression left,
                                                      ASTExpression right) {
    if(types.containsKey(left)&&types.containsKey(right)) {
      if(isNumericType(types.get(left))&&isNumericType(types.get(right))){
        return types.get(left).deepClone();
      }
    }
    return null;
  }

  public void setTypes(Map<ASTNode, SymTypeExpression> types){
    this.types=types;
  }

  public SymTypeExpression calculateType(ASTExpression expr){
    expr.accept(realThis);
    return types.get(expr);
  }

}
