/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions;

public  interface CombineExpressionsVisitor extends de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsVisitor,de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor,de.monticore.expressions.bitexpressions._visitor.BitExpressionsVisitor,de.monticore.literals.mccommonliterals._visitor.MCCommonLiteralsVisitor,de.monticore.expressions.javaclassexpressions._visitor.JavaClassExpressionsVisitor,de.monticore.expressions.setexpressions._visitor.SetExpressionsVisitor,de.monticore.expressions.oclexpressions._visitor.OCLExpressionsVisitor {



  default
  public  CombineExpressionsVisitor getRealThis ()  {
    return this;
  }

  default
  public  void setRealThis (CombineExpressionsVisitor realThis)  {
    throw new UnsupportedOperationException("0xA7011x709 The setter for realThis is not implemented. You might want to implement a wrapper class to allow setting/getting realThis.");
  }

  default
  public  void endVisit (de.monticore.ast.ASTNode node)  {

  }

  default
  public  void visit (de.monticore.ast.ASTNode node)  {

  }
}
