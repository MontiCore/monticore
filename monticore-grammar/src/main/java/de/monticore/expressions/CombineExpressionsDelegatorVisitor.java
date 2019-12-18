/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions;

import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;

import de.monticore.expressions.oclexpressions._visitor.OCLExpressionsVisitor;
import de.se_rwth.commons.logging.Log;
import de.monticore.ast.ASTNode;
import de.monticore.ast.Comment;
import de.se_rwth.commons.SourcePosition;
import de.monticore.symboltable.*;
import de.monticore.ast.ASTCNode;


public class CombineExpressionsDelegatorVisitor  implements CombineExpressionsInheritanceVisitor {


  private  CombineExpressionsDelegatorVisitor realThis = (CombineExpressionsDelegatorVisitor) this;;


  private  Optional<de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsVisitor> assignmentExpressionsVisitor = Optional.empty();;


  private  Optional<de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor> commonExpressionsVisitor = Optional.empty();;


  private  Optional<de.monticore.expressions.bitexpressions._visitor.BitExpressionsVisitor> bitExpressionsVisitor = Optional.empty();;


  private  Optional<de.monticore.literals.mccommonliterals._visitor.MCCommonLiteralsVisitor> mCCommonLiteralsVisitor = Optional.empty();;


  private  Optional<de.monticore.expressions.javaclassexpressions._visitor.JavaClassExpressionsVisitor> javaClassExpressionsVisitor = Optional.empty();;


  private  Optional<de.monticore.expressions.setexpressions._visitor.SetExpressionsVisitor> setExpressionsVisitor = Optional.empty();;


  private  Optional<de.monticore.types.mcsimplegenerictypes._visitor.MCSimpleGenericTypesVisitor> mCSimpleGenericTypesVisitor = Optional.empty();;


  private  Optional<de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor> expressionsBasisVisitor = Optional.empty();;


  private  Optional<de.monticore.mcbasics._visitor.MCBasicsVisitor> mCBasicsVisitor = Optional.empty();;


  private  Optional<de.monticore.types.typesymbols._visitor.TypeSymbolsVisitor> typeSymbolsVisitor = Optional.empty();;


  private  Optional<de.monticore.literals.mcliteralsbasis._visitor.MCLiteralsBasisVisitor> mCLiteralsBasisVisitor = Optional.empty();;


  private  Optional<de.monticore.types.mccollectiontypes._visitor.MCCollectionTypesVisitor> mCCollectionTypesVisitor = Optional.empty();;


  private  Optional<de.monticore.types.mcbasictypes._visitor.MCBasicTypesVisitor> mCBasicTypesVisitor = Optional.empty();;


  private  Optional<CombineExpressionsVisitor> combineExpressionsVisitor = Optional.empty();;

  private Optional<de.monticore.expressions.oclexpressions._visitor.OCLExpressionsVisitor> oclExpressionsVisitor = Optional.empty();




  public  CombineExpressionsDelegatorVisitor getRealThis ()  {
    return realThis;
  }


  public  void setRealThis (CombineExpressionsVisitor realThis)  {


    if (this.realThis != realThis) {
      if (!(realThis instanceof CombineExpressionsDelegatorVisitor)) {
        Log.error("0xA7111x046 realThis of CombineExpressionsDelegatorVisitor must be CombineExpressionsDelegatorVisitor itself.");
      }
      this.realThis = (CombineExpressionsDelegatorVisitor) realThis;
      // register the known delegates to the realThis (and therby also set their new realThis)
      if (this.combineExpressionsVisitor.isPresent()) {
        this.setCombineExpressionsVisitor(combineExpressionsVisitor.get());
      }
      if (this.assignmentExpressionsVisitor.isPresent()) {
        this.setAssignmentExpressionsVisitor(assignmentExpressionsVisitor.get());
      }
      if (this.commonExpressionsVisitor.isPresent()) {
        this.setCommonExpressionsVisitor(commonExpressionsVisitor.get());
      }
      if (this.bitExpressionsVisitor.isPresent()) {
        this.setBitExpressionsVisitor(bitExpressionsVisitor.get());
      }
      if (this.mCCommonLiteralsVisitor.isPresent()) {
        this.setMCCommonLiteralsVisitor(mCCommonLiteralsVisitor.get());
      }
      if (this.javaClassExpressionsVisitor.isPresent()) {
        this.setJavaClassExpressionsVisitor(javaClassExpressionsVisitor.get());
      }
      if (this.setExpressionsVisitor.isPresent()) {
        this.setSetExpressionsVisitor(setExpressionsVisitor.get());
      }
      if (this.mCSimpleGenericTypesVisitor.isPresent()) {
        this.setMCSimpleGenericTypesVisitor(mCSimpleGenericTypesVisitor.get());
      }
      if(this.oclExpressionsVisitor.isPresent()){
        this.setOCLExpressionsVisitor(oclExpressionsVisitor.get());
      }

    }
  }


  public  void setAssignmentExpressionsVisitor (de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsVisitor assignmentExpressionsVisitor)  {


    this.assignmentExpressionsVisitor = Optional.ofNullable(assignmentExpressionsVisitor);
    if (this.assignmentExpressionsVisitor.isPresent()) {
      this.assignmentExpressionsVisitor.get().setRealThis(getRealThis());
    }
    // register the AssignmentExpressionsVisitor also to realThis if not this
    if (getRealThis() != this) {
      // to prevent recursion we must differentiate between realThis being
      // the current this or another instance.
      getRealThis().setAssignmentExpressionsVisitor(assignmentExpressionsVisitor);
    }
  }


  public  Optional<de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsVisitor> getAssignmentExpressionsVisitor ()  {
    return assignmentExpressionsVisitor;
  }


  public  void setCommonExpressionsVisitor (de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor commonExpressionsVisitor)  {


    this.commonExpressionsVisitor = Optional.ofNullable(commonExpressionsVisitor);
    if (this.commonExpressionsVisitor.isPresent()) {
      this.commonExpressionsVisitor.get().setRealThis(getRealThis());
    }
    // register the CommonExpressionsVisitor also to realThis if not this
    if (getRealThis() != this) {
      // to prevent recursion we must differentiate between realThis being
      // the current this or another instance.
      getRealThis().setCommonExpressionsVisitor(commonExpressionsVisitor);
    }
  }


  public  Optional<de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor> getCommonExpressionsVisitor ()  {
    return commonExpressionsVisitor;
  }


  public  void setBitExpressionsVisitor (de.monticore.expressions.bitexpressions._visitor.BitExpressionsVisitor bitExpressionsVisitor)  {


    this.bitExpressionsVisitor = Optional.ofNullable(bitExpressionsVisitor);
    if (this.bitExpressionsVisitor.isPresent()) {
      this.bitExpressionsVisitor.get().setRealThis(getRealThis());
    }
    // register the BitExpressionsVisitor also to realThis if not this
    if (getRealThis() != this) {
      // to prevent recursion we must differentiate between realThis being
      // the current this or another instance.
      getRealThis().setBitExpressionsVisitor(bitExpressionsVisitor);
    }
  }


  public  Optional<de.monticore.expressions.bitexpressions._visitor.BitExpressionsVisitor> getBitExpressionsVisitor ()  {
    return bitExpressionsVisitor;
  }


  public  void setMCCommonLiteralsVisitor (de.monticore.literals.mccommonliterals._visitor.MCCommonLiteralsVisitor mCCommonLiteralsVisitor)  {


    this.mCCommonLiteralsVisitor = Optional.ofNullable(mCCommonLiteralsVisitor);
    if (this.mCCommonLiteralsVisitor.isPresent()) {
      this.mCCommonLiteralsVisitor.get().setRealThis(getRealThis());
    }
    // register the MCCommonLiteralsVisitor also to realThis if not this
    if (getRealThis() != this) {
      // to prevent recursion we must differentiate between realThis being
      // the current this or another instance.
      getRealThis().setMCCommonLiteralsVisitor(mCCommonLiteralsVisitor);
    }
  }


  public  Optional<de.monticore.literals.mccommonliterals._visitor.MCCommonLiteralsVisitor> getMCCommonLiteralsVisitor ()  {
    return mCCommonLiteralsVisitor;
  }


  public  void setJavaClassExpressionsVisitor (de.monticore.expressions.javaclassexpressions._visitor.JavaClassExpressionsVisitor javaClassExpressionsVisitor)  {


    this.javaClassExpressionsVisitor = Optional.ofNullable(javaClassExpressionsVisitor);
    if (this.javaClassExpressionsVisitor.isPresent()) {
      this.javaClassExpressionsVisitor.get().setRealThis(getRealThis());
    }
    // register the JavaClassExpressionsVisitor also to realThis if not this
    if (getRealThis() != this) {
      // to prevent recursion we must differentiate between realThis being
      // the current this or another instance.
      getRealThis().setJavaClassExpressionsVisitor(javaClassExpressionsVisitor);
    }
  }


  public  Optional<de.monticore.expressions.javaclassexpressions._visitor.JavaClassExpressionsVisitor> getJavaClassExpressionsVisitor ()  {
    return javaClassExpressionsVisitor;
  }


  public  void setSetExpressionsVisitor (de.monticore.expressions.setexpressions._visitor.SetExpressionsVisitor setExpressionsVisitor)  {


    this.setExpressionsVisitor = Optional.ofNullable(setExpressionsVisitor);
    if (this.setExpressionsVisitor.isPresent()) {
      this.setExpressionsVisitor.get().setRealThis(getRealThis());
    }
    // register the SetExpressionsVisitor also to realThis if not this
    if (getRealThis() != this) {
      // to prevent recursion we must differentiate between realThis being
      // the current this or another instance.
      getRealThis().setSetExpressionsVisitor(setExpressionsVisitor);
    }
  }


  public  Optional<de.monticore.expressions.setexpressions._visitor.SetExpressionsVisitor> getSetExpressionsVisitor ()  {
    return setExpressionsVisitor;
  }


  public  void setMCSimpleGenericTypesVisitor (de.monticore.types.mcsimplegenerictypes._visitor.MCSimpleGenericTypesVisitor mCSimpleGenericTypesVisitor)  {


    this.mCSimpleGenericTypesVisitor = Optional.ofNullable(mCSimpleGenericTypesVisitor);
    if (this.mCSimpleGenericTypesVisitor.isPresent()) {
      this.mCSimpleGenericTypesVisitor.get().setRealThis(getRealThis());
    }
    // register the MCSimpleGenericTypesVisitor also to realThis if not this
    if (getRealThis() != this) {
      // to prevent recursion we must differentiate between realThis being
      // the current this or another instance.
      getRealThis().setMCSimpleGenericTypesVisitor(mCSimpleGenericTypesVisitor);
    }
  }


  public  Optional<de.monticore.types.mcsimplegenerictypes._visitor.MCSimpleGenericTypesVisitor> getMCSimpleGenericTypesVisitor ()  {
    return mCSimpleGenericTypesVisitor;
  }


  public  void setExpressionsBasisVisitor (de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor expressionsBasisVisitor)  {


    this.expressionsBasisVisitor = Optional.ofNullable(expressionsBasisVisitor);
    if (this.expressionsBasisVisitor.isPresent()) {
      this.expressionsBasisVisitor.get().setRealThis(getRealThis());
    }
    // register the ExpressionsBasisVisitor also to realThis if not this
    if (getRealThis() != this) {
      // to prevent recursion we must differentiate between realThis being
      // the current this or another instance.
      getRealThis().setExpressionsBasisVisitor(expressionsBasisVisitor);
    }
  }


  public  Optional<de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor> getExpressionsBasisVisitor ()  {
    return expressionsBasisVisitor;
  }


  public  void setMCBasicsVisitor (de.monticore.mcbasics._visitor.MCBasicsVisitor mCBasicsVisitor)  {


    this.mCBasicsVisitor = Optional.ofNullable(mCBasicsVisitor);
    if (this.mCBasicsVisitor.isPresent()) {
      this.mCBasicsVisitor.get().setRealThis(getRealThis());
    }
    // register the MCBasicsVisitor also to realThis if not this
    if (getRealThis() != this) {
      // to prevent recursion we must differentiate between realThis being
      // the current this or another instance.
      getRealThis().setMCBasicsVisitor(mCBasicsVisitor);
    }
  }


  public  Optional<de.monticore.mcbasics._visitor.MCBasicsVisitor> getMCBasicsVisitor ()  {
    return mCBasicsVisitor;
  }


  public  void setTypeSymbolsVisitor (de.monticore.types.typesymbols._visitor.TypeSymbolsVisitor typeSymbolsVisitor)  {


    this.typeSymbolsVisitor = Optional.ofNullable(typeSymbolsVisitor);
    if (this.typeSymbolsVisitor.isPresent()) {
      this.typeSymbolsVisitor.get().setRealThis(getRealThis());
    }
    // register the TypeSymbolsVisitor also to realThis if not this
    if (getRealThis() != this) {
      // to prevent recursion we must differentiate between realThis being
      // the current this or another instance.
      getRealThis().setTypeSymbolsVisitor(typeSymbolsVisitor);
    }
  }


  public  Optional<de.monticore.types.typesymbols._visitor.TypeSymbolsVisitor> getTypeSymbolsVisitor ()  {
    return typeSymbolsVisitor;
  }


  public  void setMCLiteralsBasisVisitor (de.monticore.literals.mcliteralsbasis._visitor.MCLiteralsBasisVisitor mCLiteralsBasisVisitor)  {


    this.mCLiteralsBasisVisitor = Optional.ofNullable(mCLiteralsBasisVisitor);
    if (this.mCLiteralsBasisVisitor.isPresent()) {
      this.mCLiteralsBasisVisitor.get().setRealThis(getRealThis());
    }
    // register the MCLiteralsBasisVisitor also to realThis if not this
    if (getRealThis() != this) {
      // to prevent recursion we must differentiate between realThis being
      // the current this or another instance.
      getRealThis().setMCLiteralsBasisVisitor(mCLiteralsBasisVisitor);
    }
  }


  public  Optional<de.monticore.literals.mcliteralsbasis._visitor.MCLiteralsBasisVisitor> getMCLiteralsBasisVisitor ()  {
    return mCLiteralsBasisVisitor;
  }


  public  void setMCCollectionTypesVisitor (de.monticore.types.mccollectiontypes._visitor.MCCollectionTypesVisitor mCCollectionTypesVisitor)  {


    this.mCCollectionTypesVisitor = Optional.ofNullable(mCCollectionTypesVisitor);
    if (this.mCCollectionTypesVisitor.isPresent()) {
      this.mCCollectionTypesVisitor.get().setRealThis(getRealThis());
    }
    // register the MCCollectionTypesVisitor also to realThis if not this
    if (getRealThis() != this) {
      // to prevent recursion we must differentiate between realThis being
      // the current this or another instance.
      getRealThis().setMCCollectionTypesVisitor(mCCollectionTypesVisitor);
    }
  }


  public  Optional<de.monticore.types.mccollectiontypes._visitor.MCCollectionTypesVisitor> getMCCollectionTypesVisitor ()  {
    return mCCollectionTypesVisitor;
  }


  public  void setMCBasicTypesVisitor (de.monticore.types.mcbasictypes._visitor.MCBasicTypesVisitor mCBasicTypesVisitor)  {


    this.mCBasicTypesVisitor = Optional.ofNullable(mCBasicTypesVisitor);
    if (this.mCBasicTypesVisitor.isPresent()) {
      this.mCBasicTypesVisitor.get().setRealThis(getRealThis());
    }
    // register the MCBasicTypesVisitor also to realThis if not this
    if (getRealThis() != this) {
      // to prevent recursion we must differentiate between realThis being
      // the current this or another instance.
      getRealThis().setMCBasicTypesVisitor(mCBasicTypesVisitor);
    }
  }


  public  Optional<de.monticore.types.mcbasictypes._visitor.MCBasicTypesVisitor> getMCBasicTypesVisitor ()  {
    return mCBasicTypesVisitor;
  }


  public  void setCombineExpressionsVisitor (CombineExpressionsVisitor combineExpressionsVisitor)  {


    this.combineExpressionsVisitor = Optional.ofNullable(combineExpressionsVisitor);
    if (this.combineExpressionsVisitor.isPresent()) {
      this.combineExpressionsVisitor.get().setRealThis(getRealThis());
    }
    // register the CombineExpressionsVisitor also to realThis if not this
    if (getRealThis() != this) {
      // to prevent recursion we must differentiate between realThis being
      // the current this or another instance.
      getRealThis().setCombineExpressionsVisitor(combineExpressionsVisitor);
    }
  }


  public  Optional<CombineExpressionsVisitor> getCombineExpressionsVisitor ()  {
    return combineExpressionsVisitor;
  }

  public void setOCLExpressionsVisitor(OCLExpressionsVisitor oclExpressionsVisitor){
    this.oclExpressionsVisitor = Optional.ofNullable(oclExpressionsVisitor);
    if(this.oclExpressionsVisitor.isPresent()){
      this.oclExpressionsVisitor.get().setRealThis(getRealThis());
    }
    if(getRealThis()!=this){
      getRealThis().setOCLExpressionsVisitor(oclExpressionsVisitor);
    }
  }

  public Optional<OCLExpressionsVisitor> getOCLExpressionsVisitor(){
    return oclExpressionsVisitor;
  }


  public  void visit (de.monticore.expressions.assignmentexpressions._ast.ASTIncSuffixExpression node)  {


    if (getRealThis().getAssignmentExpressionsVisitor().isPresent()) {
      getRealThis().getAssignmentExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.assignmentexpressions._ast.ASTIncSuffixExpression node)  {


    if (getRealThis().getAssignmentExpressionsVisitor().isPresent()) {
      getRealThis().getAssignmentExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.assignmentexpressions._ast.ASTIncSuffixExpression node)  {


    if (getRealThis().getAssignmentExpressionsVisitor().isPresent()) {
      getRealThis().getAssignmentExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.assignmentexpressions._ast.ASTIncSuffixExpression node)  {


    if (getRealThis().getAssignmentExpressionsVisitor().isPresent()) {
      getRealThis().getAssignmentExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.assignmentexpressions._ast.ASTDecSuffixExpression node)  {


    if (getRealThis().getAssignmentExpressionsVisitor().isPresent()) {
      getRealThis().getAssignmentExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.assignmentexpressions._ast.ASTDecSuffixExpression node)  {


    if (getRealThis().getAssignmentExpressionsVisitor().isPresent()) {
      getRealThis().getAssignmentExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.assignmentexpressions._ast.ASTDecSuffixExpression node)  {


    if (getRealThis().getAssignmentExpressionsVisitor().isPresent()) {
      getRealThis().getAssignmentExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.assignmentexpressions._ast.ASTDecSuffixExpression node)  {


    if (getRealThis().getAssignmentExpressionsVisitor().isPresent()) {
      getRealThis().getAssignmentExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.assignmentexpressions._ast.ASTPlusPrefixExpression node)  {


    if (getRealThis().getAssignmentExpressionsVisitor().isPresent()) {
      getRealThis().getAssignmentExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.assignmentexpressions._ast.ASTPlusPrefixExpression node)  {


    if (getRealThis().getAssignmentExpressionsVisitor().isPresent()) {
      getRealThis().getAssignmentExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.assignmentexpressions._ast.ASTPlusPrefixExpression node)  {


    if (getRealThis().getAssignmentExpressionsVisitor().isPresent()) {
      getRealThis().getAssignmentExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.assignmentexpressions._ast.ASTPlusPrefixExpression node)  {


    if (getRealThis().getAssignmentExpressionsVisitor().isPresent()) {
      getRealThis().getAssignmentExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.assignmentexpressions._ast.ASTMinusPrefixExpression node)  {


    if (getRealThis().getAssignmentExpressionsVisitor().isPresent()) {
      getRealThis().getAssignmentExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.assignmentexpressions._ast.ASTMinusPrefixExpression node)  {


    if (getRealThis().getAssignmentExpressionsVisitor().isPresent()) {
      getRealThis().getAssignmentExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.assignmentexpressions._ast.ASTMinusPrefixExpression node)  {


    if (getRealThis().getAssignmentExpressionsVisitor().isPresent()) {
      getRealThis().getAssignmentExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.assignmentexpressions._ast.ASTMinusPrefixExpression node)  {


    if (getRealThis().getAssignmentExpressionsVisitor().isPresent()) {
      getRealThis().getAssignmentExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.assignmentexpressions._ast.ASTIncPrefixExpression node)  {


    if (getRealThis().getAssignmentExpressionsVisitor().isPresent()) {
      getRealThis().getAssignmentExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.assignmentexpressions._ast.ASTIncPrefixExpression node)  {


    if (getRealThis().getAssignmentExpressionsVisitor().isPresent()) {
      getRealThis().getAssignmentExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.assignmentexpressions._ast.ASTIncPrefixExpression node)  {


    if (getRealThis().getAssignmentExpressionsVisitor().isPresent()) {
      getRealThis().getAssignmentExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.assignmentexpressions._ast.ASTIncPrefixExpression node)  {


    if (getRealThis().getAssignmentExpressionsVisitor().isPresent()) {
      getRealThis().getAssignmentExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.assignmentexpressions._ast.ASTDecPrefixExpression node)  {


    if (getRealThis().getAssignmentExpressionsVisitor().isPresent()) {
      getRealThis().getAssignmentExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.assignmentexpressions._ast.ASTDecPrefixExpression node)  {


    if (getRealThis().getAssignmentExpressionsVisitor().isPresent()) {
      getRealThis().getAssignmentExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.assignmentexpressions._ast.ASTDecPrefixExpression node)  {


    if (getRealThis().getAssignmentExpressionsVisitor().isPresent()) {
      getRealThis().getAssignmentExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.assignmentexpressions._ast.ASTDecPrefixExpression node)  {


    if (getRealThis().getAssignmentExpressionsVisitor().isPresent()) {
      getRealThis().getAssignmentExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.assignmentexpressions._ast.ASTRegularAssignmentExpression node)  {


    if (getRealThis().getAssignmentExpressionsVisitor().isPresent()) {
      getRealThis().getAssignmentExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.assignmentexpressions._ast.ASTRegularAssignmentExpression node)  {


    if (getRealThis().getAssignmentExpressionsVisitor().isPresent()) {
      getRealThis().getAssignmentExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.assignmentexpressions._ast.ASTRegularAssignmentExpression node)  {


    if (getRealThis().getAssignmentExpressionsVisitor().isPresent()) {
      getRealThis().getAssignmentExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.assignmentexpressions._ast.ASTRegularAssignmentExpression node)  {


    if (getRealThis().getAssignmentExpressionsVisitor().isPresent()) {
      getRealThis().getAssignmentExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpression node)  {


    if (getRealThis().getAssignmentExpressionsVisitor().isPresent()) {
      getRealThis().getAssignmentExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpression node)  {


    if (getRealThis().getAssignmentExpressionsVisitor().isPresent()) {
      getRealThis().getAssignmentExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpression node)  {


    if (getRealThis().getAssignmentExpressionsVisitor().isPresent()) {
      getRealThis().getAssignmentExpressionsVisitor().get().handle(node);
    }

  }


  public  void visit (de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpressionsNode node)  {


    if (getRealThis().getAssignmentExpressionsVisitor().isPresent()) {
      getRealThis().getAssignmentExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpressionsNode node)  {


    if (getRealThis().getAssignmentExpressionsVisitor().isPresent()) {
      getRealThis().getAssignmentExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpressionsNode node)  {


    if (getRealThis().getAssignmentExpressionsVisitor().isPresent()) {
      getRealThis().getAssignmentExpressionsVisitor().get().handle(node);
    }

  }


  public  void visit (de.monticore.expressions.commonexpressions._ast.ASTCallExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.commonexpressions._ast.ASTCallExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.commonexpressions._ast.ASTCallExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.commonexpressions._ast.ASTCallExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.commonexpressions._ast.ASTBooleanNotExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.commonexpressions._ast.ASTBooleanNotExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.commonexpressions._ast.ASTBooleanNotExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.commonexpressions._ast.ASTBooleanNotExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.commonexpressions._ast.ASTLogicalNotExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.commonexpressions._ast.ASTLogicalNotExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.commonexpressions._ast.ASTLogicalNotExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.commonexpressions._ast.ASTLogicalNotExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.commonexpressions._ast.ASTMultExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.commonexpressions._ast.ASTMultExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.commonexpressions._ast.ASTMultExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.commonexpressions._ast.ASTMultExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.commonexpressions._ast.ASTDivideExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.commonexpressions._ast.ASTDivideExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.commonexpressions._ast.ASTDivideExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.commonexpressions._ast.ASTDivideExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.commonexpressions._ast.ASTModuloExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.commonexpressions._ast.ASTModuloExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.commonexpressions._ast.ASTModuloExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.commonexpressions._ast.ASTModuloExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.commonexpressions._ast.ASTPlusExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.commonexpressions._ast.ASTPlusExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.commonexpressions._ast.ASTPlusExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.commonexpressions._ast.ASTPlusExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.commonexpressions._ast.ASTMinusExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.commonexpressions._ast.ASTMinusExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.commonexpressions._ast.ASTMinusExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.commonexpressions._ast.ASTMinusExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.commonexpressions._ast.ASTLessEqualExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.commonexpressions._ast.ASTLessEqualExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.commonexpressions._ast.ASTLessEqualExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.commonexpressions._ast.ASTLessEqualExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.commonexpressions._ast.ASTGreaterEqualExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.commonexpressions._ast.ASTGreaterEqualExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.commonexpressions._ast.ASTGreaterEqualExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.commonexpressions._ast.ASTGreaterEqualExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.commonexpressions._ast.ASTLessThanExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.commonexpressions._ast.ASTLessThanExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.commonexpressions._ast.ASTLessThanExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.commonexpressions._ast.ASTLessThanExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.commonexpressions._ast.ASTGreaterThanExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.commonexpressions._ast.ASTGreaterThanExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.commonexpressions._ast.ASTGreaterThanExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.commonexpressions._ast.ASTGreaterThanExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.commonexpressions._ast.ASTEqualsExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.commonexpressions._ast.ASTEqualsExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.commonexpressions._ast.ASTEqualsExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.commonexpressions._ast.ASTEqualsExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.commonexpressions._ast.ASTNotEqualsExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.commonexpressions._ast.ASTNotEqualsExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.commonexpressions._ast.ASTNotEqualsExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.commonexpressions._ast.ASTNotEqualsExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.commonexpressions._ast.ASTBooleanAndOpExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.commonexpressions._ast.ASTBooleanAndOpExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.commonexpressions._ast.ASTBooleanAndOpExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.commonexpressions._ast.ASTBooleanAndOpExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.commonexpressions._ast.ASTBooleanOrOpExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.commonexpressions._ast.ASTBooleanOrOpExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.commonexpressions._ast.ASTBooleanOrOpExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.commonexpressions._ast.ASTBooleanOrOpExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.commonexpressions._ast.ASTConditionalExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.commonexpressions._ast.ASTConditionalExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.commonexpressions._ast.ASTConditionalExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.commonexpressions._ast.ASTConditionalExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.commonexpressions._ast.ASTBracketExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.commonexpressions._ast.ASTBracketExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.commonexpressions._ast.ASTBracketExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.commonexpressions._ast.ASTBracketExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.commonexpressions._ast.ASTArguments node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.commonexpressions._ast.ASTArguments node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.commonexpressions._ast.ASTArguments node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.commonexpressions._ast.ASTArguments node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.commonexpressions._ast.ASTInfixExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.commonexpressions._ast.ASTInfixExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.commonexpressions._ast.ASTInfixExpression node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().handle(node);
    }

  }


  public  void visit (de.monticore.expressions.commonexpressions._ast.ASTCommonExpressionsNode node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.commonexpressions._ast.ASTCommonExpressionsNode node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.commonexpressions._ast.ASTCommonExpressionsNode node)  {


    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().handle(node);
    }

  }


  public  void visit (de.monticore.expressions.bitexpressions._ast.ASTLeftShiftExpression node)  {


    if (getRealThis().getBitExpressionsVisitor().isPresent()) {
      getRealThis().getBitExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.bitexpressions._ast.ASTLeftShiftExpression node)  {


    if (getRealThis().getBitExpressionsVisitor().isPresent()) {
      getRealThis().getBitExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.bitexpressions._ast.ASTLeftShiftExpression node)  {


    if (getRealThis().getBitExpressionsVisitor().isPresent()) {
      getRealThis().getBitExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.bitexpressions._ast.ASTLeftShiftExpression node)  {


    if (getRealThis().getBitExpressionsVisitor().isPresent()) {
      getRealThis().getBitExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.bitexpressions._ast.ASTRightShiftExpression node)  {


    if (getRealThis().getBitExpressionsVisitor().isPresent()) {
      getRealThis().getBitExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.bitexpressions._ast.ASTRightShiftExpression node)  {


    if (getRealThis().getBitExpressionsVisitor().isPresent()) {
      getRealThis().getBitExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.bitexpressions._ast.ASTRightShiftExpression node)  {


    if (getRealThis().getBitExpressionsVisitor().isPresent()) {
      getRealThis().getBitExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.bitexpressions._ast.ASTRightShiftExpression node)  {


    if (getRealThis().getBitExpressionsVisitor().isPresent()) {
      getRealThis().getBitExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.bitexpressions._ast.ASTLogicalRightShiftExpression node)  {


    if (getRealThis().getBitExpressionsVisitor().isPresent()) {
      getRealThis().getBitExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.bitexpressions._ast.ASTLogicalRightShiftExpression node)  {


    if (getRealThis().getBitExpressionsVisitor().isPresent()) {
      getRealThis().getBitExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.bitexpressions._ast.ASTLogicalRightShiftExpression node)  {


    if (getRealThis().getBitExpressionsVisitor().isPresent()) {
      getRealThis().getBitExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.bitexpressions._ast.ASTLogicalRightShiftExpression node)  {


    if (getRealThis().getBitExpressionsVisitor().isPresent()) {
      getRealThis().getBitExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.bitexpressions._ast.ASTBinaryAndExpression node)  {


    if (getRealThis().getBitExpressionsVisitor().isPresent()) {
      getRealThis().getBitExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.bitexpressions._ast.ASTBinaryAndExpression node)  {


    if (getRealThis().getBitExpressionsVisitor().isPresent()) {
      getRealThis().getBitExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.bitexpressions._ast.ASTBinaryAndExpression node)  {


    if (getRealThis().getBitExpressionsVisitor().isPresent()) {
      getRealThis().getBitExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.bitexpressions._ast.ASTBinaryAndExpression node)  {


    if (getRealThis().getBitExpressionsVisitor().isPresent()) {
      getRealThis().getBitExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.bitexpressions._ast.ASTBinaryXorExpression node)  {


    if (getRealThis().getBitExpressionsVisitor().isPresent()) {
      getRealThis().getBitExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.bitexpressions._ast.ASTBinaryXorExpression node)  {


    if (getRealThis().getBitExpressionsVisitor().isPresent()) {
      getRealThis().getBitExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.bitexpressions._ast.ASTBinaryXorExpression node)  {


    if (getRealThis().getBitExpressionsVisitor().isPresent()) {
      getRealThis().getBitExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.bitexpressions._ast.ASTBinaryXorExpression node)  {


    if (getRealThis().getBitExpressionsVisitor().isPresent()) {
      getRealThis().getBitExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.bitexpressions._ast.ASTBinaryOrOpExpression node)  {


    if (getRealThis().getBitExpressionsVisitor().isPresent()) {
      getRealThis().getBitExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.bitexpressions._ast.ASTBinaryOrOpExpression node)  {


    if (getRealThis().getBitExpressionsVisitor().isPresent()) {
      getRealThis().getBitExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.bitexpressions._ast.ASTBinaryOrOpExpression node)  {


    if (getRealThis().getBitExpressionsVisitor().isPresent()) {
      getRealThis().getBitExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.bitexpressions._ast.ASTBinaryOrOpExpression node)  {


    if (getRealThis().getBitExpressionsVisitor().isPresent()) {
      getRealThis().getBitExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.bitexpressions._ast.ASTShiftExpression node)  {


    if (getRealThis().getBitExpressionsVisitor().isPresent()) {
      getRealThis().getBitExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.bitexpressions._ast.ASTShiftExpression node)  {


    if (getRealThis().getBitExpressionsVisitor().isPresent()) {
      getRealThis().getBitExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.bitexpressions._ast.ASTShiftExpression node)  {


    if (getRealThis().getBitExpressionsVisitor().isPresent()) {
      getRealThis().getBitExpressionsVisitor().get().handle(node);
    }

  }


  public  void visit (de.monticore.expressions.bitexpressions._ast.ASTBitExpressionsNode node)  {


    if (getRealThis().getBitExpressionsVisitor().isPresent()) {
      getRealThis().getBitExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.bitexpressions._ast.ASTBitExpressionsNode node)  {


    if (getRealThis().getBitExpressionsVisitor().isPresent()) {
      getRealThis().getBitExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.bitexpressions._ast.ASTBitExpressionsNode node)  {


    if (getRealThis().getBitExpressionsVisitor().isPresent()) {
      getRealThis().getBitExpressionsVisitor().get().handle(node);
    }

  }


  public  void visit (de.monticore.literals.mccommonliterals._ast.ASTNullLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.literals.mccommonliterals._ast.ASTNullLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.literals.mccommonliterals._ast.ASTNullLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.literals.mccommonliterals._ast.ASTNullLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.literals.mccommonliterals._ast.ASTBooleanLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.literals.mccommonliterals._ast.ASTBooleanLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.literals.mccommonliterals._ast.ASTBooleanLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.literals.mccommonliterals._ast.ASTBooleanLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.literals.mccommonliterals._ast.ASTCharLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.literals.mccommonliterals._ast.ASTCharLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.literals.mccommonliterals._ast.ASTCharLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.literals.mccommonliterals._ast.ASTCharLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.literals.mccommonliterals._ast.ASTStringLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.literals.mccommonliterals._ast.ASTStringLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.literals.mccommonliterals._ast.ASTStringLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.literals.mccommonliterals._ast.ASTStringLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.literals.mccommonliterals._ast.ASTNatLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.literals.mccommonliterals._ast.ASTNatLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.literals.mccommonliterals._ast.ASTNatLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.literals.mccommonliterals._ast.ASTNatLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.literals.mccommonliterals._ast.ASTSignedNatLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.literals.mccommonliterals._ast.ASTSignedNatLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.literals.mccommonliterals._ast.ASTSignedNatLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.literals.mccommonliterals._ast.ASTSignedNatLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.literals.mccommonliterals._ast.ASTBasicLongLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.literals.mccommonliterals._ast.ASTBasicLongLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.literals.mccommonliterals._ast.ASTBasicLongLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.literals.mccommonliterals._ast.ASTBasicLongLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.literals.mccommonliterals._ast.ASTSignedBasicLongLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.literals.mccommonliterals._ast.ASTSignedBasicLongLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.literals.mccommonliterals._ast.ASTSignedBasicLongLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.literals.mccommonliterals._ast.ASTSignedBasicLongLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.literals.mccommonliterals._ast.ASTBasicFloatLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.literals.mccommonliterals._ast.ASTBasicFloatLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.literals.mccommonliterals._ast.ASTBasicFloatLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.literals.mccommonliterals._ast.ASTBasicFloatLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.literals.mccommonliterals._ast.ASTSignedBasicFloatLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.literals.mccommonliterals._ast.ASTSignedBasicFloatLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.literals.mccommonliterals._ast.ASTSignedBasicFloatLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.literals.mccommonliterals._ast.ASTSignedBasicFloatLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.literals.mccommonliterals._ast.ASTBasicDoubleLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.literals.mccommonliterals._ast.ASTBasicDoubleLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.literals.mccommonliterals._ast.ASTBasicDoubleLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.literals.mccommonliterals._ast.ASTBasicDoubleLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.literals.mccommonliterals._ast.ASTSignedBasicDoubleLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.literals.mccommonliterals._ast.ASTSignedBasicDoubleLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.literals.mccommonliterals._ast.ASTSignedBasicDoubleLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.literals.mccommonliterals._ast.ASTSignedBasicDoubleLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.literals.mccommonliterals._ast.ASTSignedLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.literals.mccommonliterals._ast.ASTSignedLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.literals.mccommonliterals._ast.ASTSignedLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().handle(node);
    }

  }


  public  void visit (de.monticore.literals.mccommonliterals._ast.ASTNumericLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.literals.mccommonliterals._ast.ASTNumericLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.literals.mccommonliterals._ast.ASTNumericLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().handle(node);
    }

  }


  public  void visit (de.monticore.literals.mccommonliterals._ast.ASTSignedNumericLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.literals.mccommonliterals._ast.ASTSignedNumericLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.literals.mccommonliterals._ast.ASTSignedNumericLiteral node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().handle(node);
    }

  }


  public  void visit (de.monticore.literals.mccommonliterals._ast.ASTMCCommonLiteralsNode node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.literals.mccommonliterals._ast.ASTMCCommonLiteralsNode node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.literals.mccommonliterals._ast.ASTMCCommonLiteralsNode node)  {


    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().handle(node);
    }

  }


  public  void visit (de.monticore.expressions.javaclassexpressions._ast.ASTPrimaryThisExpression node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.javaclassexpressions._ast.ASTPrimaryThisExpression node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.javaclassexpressions._ast.ASTPrimaryThisExpression node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.javaclassexpressions._ast.ASTPrimaryThisExpression node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.javaclassexpressions._ast.ASTThisExpression node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.javaclassexpressions._ast.ASTThisExpression node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.javaclassexpressions._ast.ASTThisExpression node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.javaclassexpressions._ast.ASTThisExpression node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.javaclassexpressions._ast.ASTArrayExpression node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.javaclassexpressions._ast.ASTArrayExpression node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.javaclassexpressions._ast.ASTArrayExpression node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.javaclassexpressions._ast.ASTArrayExpression node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.javaclassexpressions._ast.ASTSuperExpression node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.javaclassexpressions._ast.ASTSuperExpression node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.javaclassexpressions._ast.ASTSuperExpression node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.javaclassexpressions._ast.ASTSuperExpression node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.javaclassexpressions._ast.ASTGenericInvocationExpression node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.javaclassexpressions._ast.ASTGenericInvocationExpression node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.javaclassexpressions._ast.ASTGenericInvocationExpression node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.javaclassexpressions._ast.ASTGenericInvocationExpression node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.javaclassexpressions._ast.ASTTypeCastExpression node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.javaclassexpressions._ast.ASTTypeCastExpression node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.javaclassexpressions._ast.ASTTypeCastExpression node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.javaclassexpressions._ast.ASTTypeCastExpression node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.javaclassexpressions._ast.ASTPrimarySuperExpression node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.javaclassexpressions._ast.ASTPrimarySuperExpression node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.javaclassexpressions._ast.ASTPrimarySuperExpression node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.javaclassexpressions._ast.ASTPrimarySuperExpression node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.javaclassexpressions._ast.ASTClassExpression node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.javaclassexpressions._ast.ASTClassExpression node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.javaclassexpressions._ast.ASTClassExpression node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.javaclassexpressions._ast.ASTClassExpression node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.javaclassexpressions._ast.ASTPrimaryGenericInvocationExpression node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.javaclassexpressions._ast.ASTPrimaryGenericInvocationExpression node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.javaclassexpressions._ast.ASTPrimaryGenericInvocationExpression node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.javaclassexpressions._ast.ASTPrimaryGenericInvocationExpression node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.javaclassexpressions._ast.ASTGenericInvocationSuffix node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.javaclassexpressions._ast.ASTGenericInvocationSuffix node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.javaclassexpressions._ast.ASTGenericInvocationSuffix node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.javaclassexpressions._ast.ASTGenericInvocationSuffix node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.javaclassexpressions._ast.ASTSuperSuffix node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.javaclassexpressions._ast.ASTSuperSuffix node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.javaclassexpressions._ast.ASTSuperSuffix node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.javaclassexpressions._ast.ASTSuperSuffix node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.javaclassexpressions._ast.ASTInstanceofExpression node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.javaclassexpressions._ast.ASTInstanceofExpression node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.javaclassexpressions._ast.ASTInstanceofExpression node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.javaclassexpressions._ast.ASTInstanceofExpression node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.javaclassexpressions._ast.ASTExtTypeExt node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.javaclassexpressions._ast.ASTExtTypeExt node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.javaclassexpressions._ast.ASTExtTypeExt node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().handle(node);
    }

  }


  public  void visit (de.monticore.expressions.javaclassexpressions._ast.ASTExtReturnTypeExt node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.javaclassexpressions._ast.ASTExtReturnTypeExt node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.javaclassexpressions._ast.ASTExtReturnTypeExt node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().handle(node);
    }

  }


  public  void visit (de.monticore.expressions.javaclassexpressions._ast.ASTExtTypeArgumentsExt node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.javaclassexpressions._ast.ASTExtTypeArgumentsExt node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.javaclassexpressions._ast.ASTExtTypeArgumentsExt node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().handle(node);
    }

  }


  public  void visit (de.monticore.expressions.javaclassexpressions._ast.ASTJavaClassExpressionsNode node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.javaclassexpressions._ast.ASTJavaClassExpressionsNode node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.javaclassexpressions._ast.ASTJavaClassExpressionsNode node)  {


    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().handle(node);
    }

  }


  public  void visit (de.monticore.expressions.setexpressions._ast.ASTIsInExpression node)  {


    if (getRealThis().getSetExpressionsVisitor().isPresent()) {
      getRealThis().getSetExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.setexpressions._ast.ASTIsInExpression node)  {


    if (getRealThis().getSetExpressionsVisitor().isPresent()) {
      getRealThis().getSetExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.setexpressions._ast.ASTIsInExpression node)  {


    if (getRealThis().getSetExpressionsVisitor().isPresent()) {
      getRealThis().getSetExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.setexpressions._ast.ASTIsInExpression node)  {


    if (getRealThis().getSetExpressionsVisitor().isPresent()) {
      getRealThis().getSetExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.setexpressions._ast.ASTSetInExpression node)  {


    if (getRealThis().getSetExpressionsVisitor().isPresent()) {
      getRealThis().getSetExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.setexpressions._ast.ASTSetInExpression node)  {


    if (getRealThis().getSetExpressionsVisitor().isPresent()) {
      getRealThis().getSetExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.setexpressions._ast.ASTSetInExpression node)  {


    if (getRealThis().getSetExpressionsVisitor().isPresent()) {
      getRealThis().getSetExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.setexpressions._ast.ASTSetInExpression node)  {


    if (getRealThis().getSetExpressionsVisitor().isPresent()) {
      getRealThis().getSetExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.setexpressions._ast.ASTUnionExpressionInfix node)  {


    if (getRealThis().getSetExpressionsVisitor().isPresent()) {
      getRealThis().getSetExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.setexpressions._ast.ASTUnionExpressionInfix node)  {


    if (getRealThis().getSetExpressionsVisitor().isPresent()) {
      getRealThis().getSetExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.setexpressions._ast.ASTUnionExpressionInfix node)  {


    if (getRealThis().getSetExpressionsVisitor().isPresent()) {
      getRealThis().getSetExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.setexpressions._ast.ASTUnionExpressionInfix node)  {


    if (getRealThis().getSetExpressionsVisitor().isPresent()) {
      getRealThis().getSetExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.setexpressions._ast.ASTIntersectionExpressionInfix node)  {


    if (getRealThis().getSetExpressionsVisitor().isPresent()) {
      getRealThis().getSetExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.setexpressions._ast.ASTIntersectionExpressionInfix node)  {


    if (getRealThis().getSetExpressionsVisitor().isPresent()) {
      getRealThis().getSetExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.setexpressions._ast.ASTIntersectionExpressionInfix node)  {


    if (getRealThis().getSetExpressionsVisitor().isPresent()) {
      getRealThis().getSetExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.setexpressions._ast.ASTIntersectionExpressionInfix node)  {


    if (getRealThis().getSetExpressionsVisitor().isPresent()) {
      getRealThis().getSetExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.setexpressions._ast.ASTUnionExpressionPrefix node)  {


    if (getRealThis().getSetExpressionsVisitor().isPresent()) {
      getRealThis().getSetExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.setexpressions._ast.ASTUnionExpressionPrefix node)  {


    if (getRealThis().getSetExpressionsVisitor().isPresent()) {
      getRealThis().getSetExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.setexpressions._ast.ASTUnionExpressionPrefix node)  {


    if (getRealThis().getSetExpressionsVisitor().isPresent()) {
      getRealThis().getSetExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.setexpressions._ast.ASTUnionExpressionPrefix node)  {


    if (getRealThis().getSetExpressionsVisitor().isPresent()) {
      getRealThis().getSetExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.setexpressions._ast.ASTIntersectionExpressionPrefix node)  {


    if (getRealThis().getSetExpressionsVisitor().isPresent()) {
      getRealThis().getSetExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.setexpressions._ast.ASTIntersectionExpressionPrefix node)  {


    if (getRealThis().getSetExpressionsVisitor().isPresent()) {
      getRealThis().getSetExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.setexpressions._ast.ASTIntersectionExpressionPrefix node)  {


    if (getRealThis().getSetExpressionsVisitor().isPresent()) {
      getRealThis().getSetExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.setexpressions._ast.ASTIntersectionExpressionPrefix node)  {


    if (getRealThis().getSetExpressionsVisitor().isPresent()) {
      getRealThis().getSetExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.setexpressions._ast.ASTSetAndExpression node)  {


    if (getRealThis().getSetExpressionsVisitor().isPresent()) {
      getRealThis().getSetExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.setexpressions._ast.ASTSetAndExpression node)  {


    if (getRealThis().getSetExpressionsVisitor().isPresent()) {
      getRealThis().getSetExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.setexpressions._ast.ASTSetAndExpression node)  {


    if (getRealThis().getSetExpressionsVisitor().isPresent()) {
      getRealThis().getSetExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.setexpressions._ast.ASTSetAndExpression node)  {


    if (getRealThis().getSetExpressionsVisitor().isPresent()) {
      getRealThis().getSetExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.setexpressions._ast.ASTSetOrExpression node)  {


    if (getRealThis().getSetExpressionsVisitor().isPresent()) {
      getRealThis().getSetExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.setexpressions._ast.ASTSetOrExpression node)  {


    if (getRealThis().getSetExpressionsVisitor().isPresent()) {
      getRealThis().getSetExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.setexpressions._ast.ASTSetOrExpression node)  {


    if (getRealThis().getSetExpressionsVisitor().isPresent()) {
      getRealThis().getSetExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.setexpressions._ast.ASTSetOrExpression node)  {


    if (getRealThis().getSetExpressionsVisitor().isPresent()) {
      getRealThis().getSetExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.setexpressions._ast.ASTSetExpressionsNode node)  {


    if (getRealThis().getSetExpressionsVisitor().isPresent()) {
      getRealThis().getSetExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.setexpressions._ast.ASTSetExpressionsNode node)  {


    if (getRealThis().getSetExpressionsVisitor().isPresent()) {
      getRealThis().getSetExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.setexpressions._ast.ASTSetExpressionsNode node)  {


    if (getRealThis().getSetExpressionsVisitor().isPresent()) {
      getRealThis().getSetExpressionsVisitor().get().handle(node);
    }

  }


  public  void visit (de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType node)  {


    if (getRealThis().getMCSimpleGenericTypesVisitor().isPresent()) {
      getRealThis().getMCSimpleGenericTypesVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType node)  {


    if (getRealThis().getMCSimpleGenericTypesVisitor().isPresent()) {
      getRealThis().getMCSimpleGenericTypesVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType node)  {


    if (getRealThis().getMCSimpleGenericTypesVisitor().isPresent()) {
      getRealThis().getMCSimpleGenericTypesVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType node)  {


    if (getRealThis().getMCSimpleGenericTypesVisitor().isPresent()) {
      getRealThis().getMCSimpleGenericTypesVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.types.mcsimplegenerictypes._ast.ASTMCCustomTypeArgument node)  {


    if (getRealThis().getMCSimpleGenericTypesVisitor().isPresent()) {
      getRealThis().getMCSimpleGenericTypesVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.types.mcsimplegenerictypes._ast.ASTMCCustomTypeArgument node)  {


    if (getRealThis().getMCSimpleGenericTypesVisitor().isPresent()) {
      getRealThis().getMCSimpleGenericTypesVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.types.mcsimplegenerictypes._ast.ASTMCCustomTypeArgument node)  {


    if (getRealThis().getMCSimpleGenericTypesVisitor().isPresent()) {
      getRealThis().getMCSimpleGenericTypesVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.types.mcsimplegenerictypes._ast.ASTMCCustomTypeArgument node)  {


    if (getRealThis().getMCSimpleGenericTypesVisitor().isPresent()) {
      getRealThis().getMCSimpleGenericTypesVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.types.mcsimplegenerictypes._ast.ASTMCSimpleGenericTypesNode node)  {


    if (getRealThis().getMCSimpleGenericTypesVisitor().isPresent()) {
      getRealThis().getMCSimpleGenericTypesVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.types.mcsimplegenerictypes._ast.ASTMCSimpleGenericTypesNode node)  {


    if (getRealThis().getMCSimpleGenericTypesVisitor().isPresent()) {
      getRealThis().getMCSimpleGenericTypesVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.types.mcsimplegenerictypes._ast.ASTMCSimpleGenericTypesNode node)  {


    if (getRealThis().getMCSimpleGenericTypesVisitor().isPresent()) {
      getRealThis().getMCSimpleGenericTypesVisitor().get().handle(node);
    }

  }


  public  void visit (de.monticore.expressions.expressionsbasis._ast.ASTNameExpression node)  {


    if (getRealThis().getExpressionsBasisVisitor().isPresent()) {
      getRealThis().getExpressionsBasisVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.expressionsbasis._ast.ASTNameExpression node)  {


    if (getRealThis().getExpressionsBasisVisitor().isPresent()) {
      getRealThis().getExpressionsBasisVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.expressionsbasis._ast.ASTNameExpression node)  {


    if (getRealThis().getExpressionsBasisVisitor().isPresent()) {
      getRealThis().getExpressionsBasisVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.expressionsbasis._ast.ASTNameExpression node)  {


    if (getRealThis().getExpressionsBasisVisitor().isPresent()) {
      getRealThis().getExpressionsBasisVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression node)  {


    if (getRealThis().getExpressionsBasisVisitor().isPresent()) {
      getRealThis().getExpressionsBasisVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression node)  {


    if (getRealThis().getExpressionsBasisVisitor().isPresent()) {
      getRealThis().getExpressionsBasisVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression node)  {


    if (getRealThis().getExpressionsBasisVisitor().isPresent()) {
      getRealThis().getExpressionsBasisVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression node)  {


    if (getRealThis().getExpressionsBasisVisitor().isPresent()) {
      getRealThis().getExpressionsBasisVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.expressionsbasis._ast.ASTExpression node)  {


    if (getRealThis().getExpressionsBasisVisitor().isPresent()) {
      getRealThis().getExpressionsBasisVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.expressionsbasis._ast.ASTExpression node)  {


    if (getRealThis().getExpressionsBasisVisitor().isPresent()) {
      getRealThis().getExpressionsBasisVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.expressionsbasis._ast.ASTExpression node)  {


    if (getRealThis().getExpressionsBasisVisitor().isPresent()) {
      getRealThis().getExpressionsBasisVisitor().get().handle(node);
    }

  }


  public  void visit (de.monticore.expressions.expressionsbasis._ast.ASTExpressionsBasisNode node)  {


    if (getRealThis().getExpressionsBasisVisitor().isPresent()) {
      getRealThis().getExpressionsBasisVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.expressionsbasis._ast.ASTExpressionsBasisNode node)  {


    if (getRealThis().getExpressionsBasisVisitor().isPresent()) {
      getRealThis().getExpressionsBasisVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.expressionsbasis._ast.ASTExpressionsBasisNode node)  {


    if (getRealThis().getExpressionsBasisVisitor().isPresent()) {
      getRealThis().getExpressionsBasisVisitor().get().handle(node);
    }

  }


  public  void visit (de.monticore.mcbasics._ast.ASTMCBasicsNode node)  {


    if (getRealThis().getMCBasicsVisitor().isPresent()) {
      getRealThis().getMCBasicsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.mcbasics._ast.ASTMCBasicsNode node)  {


    if (getRealThis().getMCBasicsVisitor().isPresent()) {
      getRealThis().getMCBasicsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.mcbasics._ast.ASTMCBasicsNode node)  {


    if (getRealThis().getMCBasicsVisitor().isPresent()) {
      getRealThis().getMCBasicsVisitor().get().handle(node);
    }

  }


  public  void visit (de.monticore.types.typesymbols._ast.ASTType node)  {


    if (getRealThis().getTypeSymbolsVisitor().isPresent()) {
      getRealThis().getTypeSymbolsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.types.typesymbols._ast.ASTType node)  {


    if (getRealThis().getTypeSymbolsVisitor().isPresent()) {
      getRealThis().getTypeSymbolsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.types.typesymbols._ast.ASTType node)  {


    if (getRealThis().getTypeSymbolsVisitor().isPresent()) {
      getRealThis().getTypeSymbolsVisitor().get().handle(node);
    }

  }


  public  void visit (de.monticore.types.typesymbols._ast.ASTTypeVar node)  {


    if (getRealThis().getTypeSymbolsVisitor().isPresent()) {
      getRealThis().getTypeSymbolsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.types.typesymbols._ast.ASTTypeVar node)  {


    if (getRealThis().getTypeSymbolsVisitor().isPresent()) {
      getRealThis().getTypeSymbolsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.types.typesymbols._ast.ASTTypeVar node)  {


    if (getRealThis().getTypeSymbolsVisitor().isPresent()) {
      getRealThis().getTypeSymbolsVisitor().get().handle(node);
    }

  }


  public  void visit (de.monticore.types.typesymbols._ast.ASTField node)  {


    if (getRealThis().getTypeSymbolsVisitor().isPresent()) {
      getRealThis().getTypeSymbolsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.types.typesymbols._ast.ASTField node)  {


    if (getRealThis().getTypeSymbolsVisitor().isPresent()) {
      getRealThis().getTypeSymbolsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.types.typesymbols._ast.ASTField node)  {


    if (getRealThis().getTypeSymbolsVisitor().isPresent()) {
      getRealThis().getTypeSymbolsVisitor().get().handle(node);
    }

  }


  public  void visit (de.monticore.types.typesymbols._ast.ASTMethod node)  {


    if (getRealThis().getTypeSymbolsVisitor().isPresent()) {
      getRealThis().getTypeSymbolsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.types.typesymbols._ast.ASTMethod node)  {


    if (getRealThis().getTypeSymbolsVisitor().isPresent()) {
      getRealThis().getTypeSymbolsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.types.typesymbols._ast.ASTMethod node)  {


    if (getRealThis().getTypeSymbolsVisitor().isPresent()) {
      getRealThis().getTypeSymbolsVisitor().get().handle(node);
    }

  }


  public  void visit (de.monticore.types.typesymbols._ast.ASTTypeSymbolsNode node)  {


    if (getRealThis().getTypeSymbolsVisitor().isPresent()) {
      getRealThis().getTypeSymbolsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.types.typesymbols._ast.ASTTypeSymbolsNode node)  {


    if (getRealThis().getTypeSymbolsVisitor().isPresent()) {
      getRealThis().getTypeSymbolsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.types.typesymbols._ast.ASTTypeSymbolsNode node)  {


    if (getRealThis().getTypeSymbolsVisitor().isPresent()) {
      getRealThis().getTypeSymbolsVisitor().get().handle(node);
    }

  }


  public  void visit (de.monticore.literals.mcliteralsbasis._ast.ASTLiteral node)  {


    if (getRealThis().getMCLiteralsBasisVisitor().isPresent()) {
      getRealThis().getMCLiteralsBasisVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.literals.mcliteralsbasis._ast.ASTLiteral node)  {


    if (getRealThis().getMCLiteralsBasisVisitor().isPresent()) {
      getRealThis().getMCLiteralsBasisVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.literals.mcliteralsbasis._ast.ASTLiteral node)  {


    if (getRealThis().getMCLiteralsBasisVisitor().isPresent()) {
      getRealThis().getMCLiteralsBasisVisitor().get().handle(node);
    }

  }


  public  void visit (de.monticore.literals.mcliteralsbasis._ast.ASTMCLiteralsBasisNode node)  {


    if (getRealThis().getMCLiteralsBasisVisitor().isPresent()) {
      getRealThis().getMCLiteralsBasisVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.literals.mcliteralsbasis._ast.ASTMCLiteralsBasisNode node)  {


    if (getRealThis().getMCLiteralsBasisVisitor().isPresent()) {
      getRealThis().getMCLiteralsBasisVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.literals.mcliteralsbasis._ast.ASTMCLiteralsBasisNode node)  {


    if (getRealThis().getMCLiteralsBasisVisitor().isPresent()) {
      getRealThis().getMCLiteralsBasisVisitor().get().handle(node);
    }

  }


  public  void visit (de.monticore.types.mccollectiontypes._ast.ASTMCListType node)  {


    if (getRealThis().getMCCollectionTypesVisitor().isPresent()) {
      getRealThis().getMCCollectionTypesVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.types.mccollectiontypes._ast.ASTMCListType node)  {


    if (getRealThis().getMCCollectionTypesVisitor().isPresent()) {
      getRealThis().getMCCollectionTypesVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.types.mccollectiontypes._ast.ASTMCListType node)  {


    if (getRealThis().getMCCollectionTypesVisitor().isPresent()) {
      getRealThis().getMCCollectionTypesVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.types.mccollectiontypes._ast.ASTMCListType node)  {


    if (getRealThis().getMCCollectionTypesVisitor().isPresent()) {
      getRealThis().getMCCollectionTypesVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.types.mccollectiontypes._ast.ASTMCOptionalType node)  {


    if (getRealThis().getMCCollectionTypesVisitor().isPresent()) {
      getRealThis().getMCCollectionTypesVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.types.mccollectiontypes._ast.ASTMCOptionalType node)  {


    if (getRealThis().getMCCollectionTypesVisitor().isPresent()) {
      getRealThis().getMCCollectionTypesVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.types.mccollectiontypes._ast.ASTMCOptionalType node)  {


    if (getRealThis().getMCCollectionTypesVisitor().isPresent()) {
      getRealThis().getMCCollectionTypesVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.types.mccollectiontypes._ast.ASTMCOptionalType node)  {


    if (getRealThis().getMCCollectionTypesVisitor().isPresent()) {
      getRealThis().getMCCollectionTypesVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.types.mccollectiontypes._ast.ASTMCMapType node)  {


    if (getRealThis().getMCCollectionTypesVisitor().isPresent()) {
      getRealThis().getMCCollectionTypesVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.types.mccollectiontypes._ast.ASTMCMapType node)  {


    if (getRealThis().getMCCollectionTypesVisitor().isPresent()) {
      getRealThis().getMCCollectionTypesVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.types.mccollectiontypes._ast.ASTMCMapType node)  {


    if (getRealThis().getMCCollectionTypesVisitor().isPresent()) {
      getRealThis().getMCCollectionTypesVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.types.mccollectiontypes._ast.ASTMCMapType node)  {


    if (getRealThis().getMCCollectionTypesVisitor().isPresent()) {
      getRealThis().getMCCollectionTypesVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.types.mccollectiontypes._ast.ASTMCSetType node)  {


    if (getRealThis().getMCCollectionTypesVisitor().isPresent()) {
      getRealThis().getMCCollectionTypesVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.types.mccollectiontypes._ast.ASTMCSetType node)  {


    if (getRealThis().getMCCollectionTypesVisitor().isPresent()) {
      getRealThis().getMCCollectionTypesVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.types.mccollectiontypes._ast.ASTMCSetType node)  {


    if (getRealThis().getMCCollectionTypesVisitor().isPresent()) {
      getRealThis().getMCCollectionTypesVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.types.mccollectiontypes._ast.ASTMCSetType node)  {


    if (getRealThis().getMCCollectionTypesVisitor().isPresent()) {
      getRealThis().getMCCollectionTypesVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.types.mccollectiontypes._ast.ASTMCBasicTypeArgument node)  {


    if (getRealThis().getMCCollectionTypesVisitor().isPresent()) {
      getRealThis().getMCCollectionTypesVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.types.mccollectiontypes._ast.ASTMCBasicTypeArgument node)  {


    if (getRealThis().getMCCollectionTypesVisitor().isPresent()) {
      getRealThis().getMCCollectionTypesVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.types.mccollectiontypes._ast.ASTMCBasicTypeArgument node)  {


    if (getRealThis().getMCCollectionTypesVisitor().isPresent()) {
      getRealThis().getMCCollectionTypesVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.types.mccollectiontypes._ast.ASTMCBasicTypeArgument node)  {


    if (getRealThis().getMCCollectionTypesVisitor().isPresent()) {
      getRealThis().getMCCollectionTypesVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.types.mccollectiontypes._ast.ASTMCPrimitiveTypeArgument node)  {


    if (getRealThis().getMCCollectionTypesVisitor().isPresent()) {
      getRealThis().getMCCollectionTypesVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.types.mccollectiontypes._ast.ASTMCPrimitiveTypeArgument node)  {


    if (getRealThis().getMCCollectionTypesVisitor().isPresent()) {
      getRealThis().getMCCollectionTypesVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.types.mccollectiontypes._ast.ASTMCPrimitiveTypeArgument node)  {


    if (getRealThis().getMCCollectionTypesVisitor().isPresent()) {
      getRealThis().getMCCollectionTypesVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.types.mccollectiontypes._ast.ASTMCPrimitiveTypeArgument node)  {


    if (getRealThis().getMCCollectionTypesVisitor().isPresent()) {
      getRealThis().getMCCollectionTypesVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.types.mccollectiontypes._ast.ASTMCGenericType node)  {


    if (getRealThis().getMCCollectionTypesVisitor().isPresent()) {
      getRealThis().getMCCollectionTypesVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.types.mccollectiontypes._ast.ASTMCGenericType node)  {


    if (getRealThis().getMCCollectionTypesVisitor().isPresent()) {
      getRealThis().getMCCollectionTypesVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.types.mccollectiontypes._ast.ASTMCGenericType node)  {


    if (getRealThis().getMCCollectionTypesVisitor().isPresent()) {
      getRealThis().getMCCollectionTypesVisitor().get().handle(node);
    }

  }


  public  void visit (de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument node)  {


    if (getRealThis().getMCCollectionTypesVisitor().isPresent()) {
      getRealThis().getMCCollectionTypesVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument node)  {


    if (getRealThis().getMCCollectionTypesVisitor().isPresent()) {
      getRealThis().getMCCollectionTypesVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument node)  {


    if (getRealThis().getMCCollectionTypesVisitor().isPresent()) {
      getRealThis().getMCCollectionTypesVisitor().get().handle(node);
    }

  }


  public  void visit (de.monticore.types.mccollectiontypes._ast.ASTMCCollectionTypesNode node)  {


    if (getRealThis().getMCCollectionTypesVisitor().isPresent()) {
      getRealThis().getMCCollectionTypesVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.types.mccollectiontypes._ast.ASTMCCollectionTypesNode node)  {


    if (getRealThis().getMCCollectionTypesVisitor().isPresent()) {
      getRealThis().getMCCollectionTypesVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.types.mccollectiontypes._ast.ASTMCCollectionTypesNode node)  {


    if (getRealThis().getMCCollectionTypesVisitor().isPresent()) {
      getRealThis().getMCCollectionTypesVisitor().get().handle(node);
    }

  }


  public  void visit (de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName node)  {


    if (getRealThis().getMCBasicTypesVisitor().isPresent()) {
      getRealThis().getMCBasicTypesVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName node)  {


    if (getRealThis().getMCBasicTypesVisitor().isPresent()) {
      getRealThis().getMCBasicTypesVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName node)  {


    if (getRealThis().getMCBasicTypesVisitor().isPresent()) {
      getRealThis().getMCBasicTypesVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName node)  {


    if (getRealThis().getMCBasicTypesVisitor().isPresent()) {
      getRealThis().getMCBasicTypesVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.types.mcbasictypes._ast.ASTMCImportStatement node)  {


    if (getRealThis().getMCBasicTypesVisitor().isPresent()) {
      getRealThis().getMCBasicTypesVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.types.mcbasictypes._ast.ASTMCImportStatement node)  {


    if (getRealThis().getMCBasicTypesVisitor().isPresent()) {
      getRealThis().getMCBasicTypesVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.types.mcbasictypes._ast.ASTMCImportStatement node)  {


    if (getRealThis().getMCBasicTypesVisitor().isPresent()) {
      getRealThis().getMCBasicTypesVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.types.mcbasictypes._ast.ASTMCImportStatement node)  {


    if (getRealThis().getMCBasicTypesVisitor().isPresent()) {
      getRealThis().getMCBasicTypesVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType node)  {


    if (getRealThis().getMCBasicTypesVisitor().isPresent()) {
      getRealThis().getMCBasicTypesVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType node)  {


    if (getRealThis().getMCBasicTypesVisitor().isPresent()) {
      getRealThis().getMCBasicTypesVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType node)  {


    if (getRealThis().getMCBasicTypesVisitor().isPresent()) {
      getRealThis().getMCBasicTypesVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType node)  {


    if (getRealThis().getMCBasicTypesVisitor().isPresent()) {
      getRealThis().getMCBasicTypesVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType node)  {


    if (getRealThis().getMCBasicTypesVisitor().isPresent()) {
      getRealThis().getMCBasicTypesVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType node)  {


    if (getRealThis().getMCBasicTypesVisitor().isPresent()) {
      getRealThis().getMCBasicTypesVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType node)  {


    if (getRealThis().getMCBasicTypesVisitor().isPresent()) {
      getRealThis().getMCBasicTypesVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType node)  {


    if (getRealThis().getMCBasicTypesVisitor().isPresent()) {
      getRealThis().getMCBasicTypesVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.types.mcbasictypes._ast.ASTMCReturnType node)  {


    if (getRealThis().getMCBasicTypesVisitor().isPresent()) {
      getRealThis().getMCBasicTypesVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.types.mcbasictypes._ast.ASTMCReturnType node)  {


    if (getRealThis().getMCBasicTypesVisitor().isPresent()) {
      getRealThis().getMCBasicTypesVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.types.mcbasictypes._ast.ASTMCReturnType node)  {


    if (getRealThis().getMCBasicTypesVisitor().isPresent()) {
      getRealThis().getMCBasicTypesVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.types.mcbasictypes._ast.ASTMCReturnType node)  {


    if (getRealThis().getMCBasicTypesVisitor().isPresent()) {
      getRealThis().getMCBasicTypesVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.types.mcbasictypes._ast.ASTMCVoidType node)  {


    if (getRealThis().getMCBasicTypesVisitor().isPresent()) {
      getRealThis().getMCBasicTypesVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.types.mcbasictypes._ast.ASTMCVoidType node)  {


    if (getRealThis().getMCBasicTypesVisitor().isPresent()) {
      getRealThis().getMCBasicTypesVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.types.mcbasictypes._ast.ASTMCVoidType node)  {


    if (getRealThis().getMCBasicTypesVisitor().isPresent()) {
      getRealThis().getMCBasicTypesVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.types.mcbasictypes._ast.ASTMCVoidType node)  {


    if (getRealThis().getMCBasicTypesVisitor().isPresent()) {
      getRealThis().getMCBasicTypesVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.types.mcbasictypes._ast.ASTMCType node)  {


    if (getRealThis().getMCBasicTypesVisitor().isPresent()) {
      getRealThis().getMCBasicTypesVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.types.mcbasictypes._ast.ASTMCType node)  {


    if (getRealThis().getMCBasicTypesVisitor().isPresent()) {
      getRealThis().getMCBasicTypesVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.types.mcbasictypes._ast.ASTMCType node)  {


    if (getRealThis().getMCBasicTypesVisitor().isPresent()) {
      getRealThis().getMCBasicTypesVisitor().get().handle(node);
    }

  }


  public  void visit (de.monticore.types.mcbasictypes._ast.ASTMCObjectType node)  {


    if (getRealThis().getMCBasicTypesVisitor().isPresent()) {
      getRealThis().getMCBasicTypesVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.types.mcbasictypes._ast.ASTMCObjectType node)  {


    if (getRealThis().getMCBasicTypesVisitor().isPresent()) {
      getRealThis().getMCBasicTypesVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.types.mcbasictypes._ast.ASTMCObjectType node)  {


    if (getRealThis().getMCBasicTypesVisitor().isPresent()) {
      getRealThis().getMCBasicTypesVisitor().get().handle(node);
    }

  }


  public  void visit (de.monticore.types.mcbasictypes._ast.ASTMCBasicTypesNode node)  {


    if (getRealThis().getMCBasicTypesVisitor().isPresent()) {
      getRealThis().getMCBasicTypesVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.types.mcbasictypes._ast.ASTMCBasicTypesNode node)  {


    if (getRealThis().getMCBasicTypesVisitor().isPresent()) {
      getRealThis().getMCBasicTypesVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.types.mcbasictypes._ast.ASTMCBasicTypesNode node)  {


    if (getRealThis().getMCBasicTypesVisitor().isPresent()) {
      getRealThis().getMCBasicTypesVisitor().get().handle(node);
    }

  }

  public  void visit (de.monticore.expressions.oclexpressions._ast.ASTInstanceOfExpression node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.oclexpressions._ast.ASTInstanceOfExpression node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.oclexpressions._ast.ASTInstanceOfExpression node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.oclexpressions._ast.ASTInstanceOfExpression node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().traverse(node);
    }

  }
  public  void visit (de.monticore.expressions.oclexpressions._ast.ASTTypeIfExpr node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.oclexpressions._ast.ASTTypeIfExpr node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.oclexpressions._ast.ASTTypeIfExpr node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.oclexpressions._ast.ASTTypeIfExpr node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().traverse(node);
    }

  }
  public  void visit (de.monticore.expressions.oclexpressions._ast.ASTThenExpressionPart node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.oclexpressions._ast.ASTThenExpressionPart node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.oclexpressions._ast.ASTThenExpressionPart node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.oclexpressions._ast.ASTThenExpressionPart node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().traverse(node);
    }

  }
  public  void visit (de.monticore.expressions.oclexpressions._ast.ASTElseExpressionPart node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.oclexpressions._ast.ASTElseExpressionPart node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.oclexpressions._ast.ASTElseExpressionPart node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.oclexpressions._ast.ASTElseExpressionPart node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().traverse(node);
    }

  }
  public  void visit (de.monticore.expressions.oclexpressions._ast.ASTIfThenElseExpr node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.oclexpressions._ast.ASTIfThenElseExpr node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.oclexpressions._ast.ASTIfThenElseExpr node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.oclexpressions._ast.ASTIfThenElseExpr node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().traverse(node);
    }

  }
  public  void visit (de.monticore.expressions.oclexpressions._ast.ASTImpliesExpression node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.oclexpressions._ast.ASTImpliesExpression node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.oclexpressions._ast.ASTImpliesExpression node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.oclexpressions._ast.ASTImpliesExpression node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().traverse(node);
    }

  }
  public  void visit (de.monticore.expressions.oclexpressions._ast.ASTSingleLogicalORExpr node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.oclexpressions._ast.ASTSingleLogicalORExpr node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.oclexpressions._ast.ASTSingleLogicalORExpr node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.oclexpressions._ast.ASTSingleLogicalORExpr node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().traverse(node);
    }

  }
  public  void visit (de.monticore.expressions.oclexpressions._ast.ASTSingleLogicalANDExpr node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.oclexpressions._ast.ASTSingleLogicalANDExpr node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.oclexpressions._ast.ASTSingleLogicalANDExpr node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.oclexpressions._ast.ASTSingleLogicalANDExpr node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().traverse(node);
    }

  }
  public  void visit (de.monticore.expressions.oclexpressions._ast.ASTForallExpr node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.oclexpressions._ast.ASTForallExpr node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.oclexpressions._ast.ASTForallExpr node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.oclexpressions._ast.ASTForallExpr node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().traverse(node);
    }

  }
  public  void visit (de.monticore.expressions.oclexpressions._ast.ASTExistsExpr node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.oclexpressions._ast.ASTExistsExpr node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.oclexpressions._ast.ASTExistsExpr node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.oclexpressions._ast.ASTExistsExpr node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().traverse(node);
    }

  }
  public  void visit (de.monticore.expressions.oclexpressions._ast.ASTAnyExpr node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.oclexpressions._ast.ASTAnyExpr node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.oclexpressions._ast.ASTAnyExpr node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.oclexpressions._ast.ASTAnyExpr node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().traverse(node);
    }

  }
  public  void visit (de.monticore.expressions.oclexpressions._ast.ASTLetinExpr node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.oclexpressions._ast.ASTLetinExpr node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.oclexpressions._ast.ASTLetinExpr node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.oclexpressions._ast.ASTLetinExpr node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().traverse(node);
    }

  }
  public  void visit (de.monticore.expressions.oclexpressions._ast.ASTIterateExpr node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.oclexpressions._ast.ASTIterateExpr node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.oclexpressions._ast.ASTIterateExpr node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.oclexpressions._ast.ASTIterateExpr node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().traverse(node);
    }

  }
  public  void visit (de.monticore.expressions.oclexpressions._ast.ASTTypeCastExpression node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.oclexpressions._ast.ASTTypeCastExpression node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.oclexpressions._ast.ASTTypeCastExpression node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.oclexpressions._ast.ASTTypeCastExpression node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.oclexpressions._ast.ASTParenthizedExpression node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.oclexpressions._ast.ASTParenthizedExpression node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.oclexpressions._ast.ASTParenthizedExpression node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.oclexpressions._ast.ASTParenthizedExpression node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.oclexpressions._ast.ASTInExpr node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.oclexpressions._ast.ASTInExpr node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.oclexpressions._ast.ASTInExpr node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.oclexpressions._ast.ASTInExpr node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().traverse(node);
    }

  }
  public  void visit (de.monticore.expressions.oclexpressions._ast.ASTOCLIsNewPrimary node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.oclexpressions._ast.ASTOCLIsNewPrimary node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.oclexpressions._ast.ASTOCLIsNewPrimary node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.oclexpressions._ast.ASTOCLIsNewPrimary node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().traverse(node);
    }

  }
  public  void visit (de.monticore.expressions.oclexpressions._ast.ASTOCLDefinedPrimary node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.oclexpressions._ast.ASTOCLDefinedPrimary node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.oclexpressions._ast.ASTOCLDefinedPrimary node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.oclexpressions._ast.ASTOCLDefinedPrimary node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().traverse(node);
    }

  }
  public  void visit (de.monticore.expressions.oclexpressions._ast.ASTOCLQualifiedPrimary node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.oclexpressions._ast.ASTOCLQualifiedPrimary node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.oclexpressions._ast.ASTOCLQualifiedPrimary node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.oclexpressions._ast.ASTOCLQualifiedPrimary node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().traverse(node);
    }

  }
  public  void visit (de.monticore.expressions.oclexpressions._ast.ASTOCLArrayQualification node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.oclexpressions._ast.ASTOCLArrayQualification node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.oclexpressions._ast.ASTOCLArrayQualification node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.oclexpressions._ast.ASTOCLArrayQualification node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().traverse(node);
    }

  }
  public  void visit (de.monticore.expressions.oclexpressions._ast.ASTOCLArgumentQualification node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.oclexpressions._ast.ASTOCLArgumentQualification node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.oclexpressions._ast.ASTOCLArgumentQualification node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.oclexpressions._ast.ASTOCLArgumentQualification node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().traverse(node);
    }

  }
  public  void visit (de.monticore.expressions.oclexpressions._ast.ASTOCLAtPreQualification node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.oclexpressions._ast.ASTOCLAtPreQualification node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.oclexpressions._ast.ASTOCLAtPreQualification node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.oclexpressions._ast.ASTOCLAtPreQualification node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().traverse(node);
    }

  }
  public  void visit (de.monticore.expressions.oclexpressions._ast.ASTOCLTransitiveQualification node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.oclexpressions._ast.ASTOCLTransitiveQualification node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.oclexpressions._ast.ASTOCLTransitiveQualification node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.oclexpressions._ast.ASTOCLTransitiveQualification node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().traverse(node);
    }

  }
  public  void visit (de.monticore.expressions.oclexpressions._ast.ASTOCLComprehensionPrimary node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.oclexpressions._ast.ASTOCLComprehensionPrimary node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.oclexpressions._ast.ASTOCLComprehensionPrimary node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.oclexpressions._ast.ASTOCLComprehensionPrimary node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().traverse(node);
    }

  }


  public  void visit (de.monticore.expressions.oclexpressions._ast.ASTOCLComprehensionExpressionStyle node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.oclexpressions._ast.ASTOCLComprehensionExpressionStyle node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.oclexpressions._ast.ASTOCLComprehensionExpressionStyle node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.oclexpressions._ast.ASTOCLComprehensionExpressionStyle node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().traverse(node);
    }

  }

  public  void visit (de.monticore.expressions.oclexpressions._ast.ASTOCLCollectionItem node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.oclexpressions._ast.ASTOCLCollectionItem node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.oclexpressions._ast.ASTOCLCollectionItem node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.oclexpressions._ast.ASTOCLCollectionItem node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().traverse(node);
    }

  }

  public  void visit (de.monticore.expressions.oclexpressions._ast.ASTOCLComprehensionItem node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.oclexpressions._ast.ASTOCLComprehensionItem node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.oclexpressions._ast.ASTOCLComprehensionItem node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.oclexpressions._ast.ASTOCLComprehensionItem node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().traverse(node);
    }

  }

  public  void visit (de.monticore.expressions.oclexpressions._ast.ASTOCLComprehensionEnumerationStyle node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.oclexpressions._ast.ASTOCLComprehensionEnumerationStyle node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.oclexpressions._ast.ASTOCLComprehensionEnumerationStyle node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.oclexpressions._ast.ASTOCLComprehensionEnumerationStyle node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().traverse(node);
    }

  }

  public  void visit (de.monticore.expressions.oclexpressions._ast.ASTOCLEquivalentExpression node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().visit(node);
    }

  }


  public  void endVisit (de.monticore.expressions.oclexpressions._ast.ASTOCLEquivalentExpression node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().endVisit(node);
    }

  }


  public  void handle (de.monticore.expressions.oclexpressions._ast.ASTOCLEquivalentExpression node)  {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().handle(node);
    }

  }


  public  void traverse (de.monticore.expressions.oclexpressions._ast.ASTOCLEquivalentExpression node) {


    if (getRealThis().getOCLExpressionsVisitor().isPresent()) {
      getRealThis().getOCLExpressionsVisitor().get().traverse(node);
    }
  }



  public  void visit (de.monticore.ast.ASTNode node)  {

    if (getRealThis().getCombineExpressionsVisitor().isPresent()) {
      getRealThis().getCombineExpressionsVisitor().get().visit(node);
    }
    if (getRealThis().getAssignmentExpressionsVisitor().isPresent()) {
      getRealThis().getAssignmentExpressionsVisitor().get().visit(node);
    }
    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().visit(node);
    }
    if (getRealThis().getBitExpressionsVisitor().isPresent()) {
      getRealThis().getBitExpressionsVisitor().get().visit(node);
    }
    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().visit(node);
    }
    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().visit(node);
    }
    if (getRealThis().getSetExpressionsVisitor().isPresent()) {
      getRealThis().getSetExpressionsVisitor().get().visit(node);
    }
    if (getRealThis().getMCSimpleGenericTypesVisitor().isPresent()) {
      getRealThis().getMCSimpleGenericTypesVisitor().get().visit(node);
    }
    if (getRealThis().getExpressionsBasisVisitor().isPresent()) {
      getRealThis().getExpressionsBasisVisitor().get().visit(node);
    }
    if (getRealThis().getMCBasicsVisitor().isPresent()) {
      getRealThis().getMCBasicsVisitor().get().visit(node);
    }
    if (getRealThis().getTypeSymbolsVisitor().isPresent()) {
      getRealThis().getTypeSymbolsVisitor().get().visit(node);
    }
    if (getRealThis().getMCLiteralsBasisVisitor().isPresent()) {
      getRealThis().getMCLiteralsBasisVisitor().get().visit(node);
    }
    if (getRealThis().getMCCollectionTypesVisitor().isPresent()) {
      getRealThis().getMCCollectionTypesVisitor().get().visit(node);
    }
    if (getRealThis().getMCBasicTypesVisitor().isPresent()) {
      getRealThis().getMCBasicTypesVisitor().get().visit(node);
    }
    if(getRealThis().getOCLExpressionsVisitor().isPresent()){
      getRealThis().getOCLExpressionsVisitor().get().visit(node);
    }

  }

  public  void endVisit (de.monticore.ast.ASTNode node)  {

    if (getRealThis().getMCBasicTypesVisitor().isPresent()) {
      getRealThis().getMCBasicTypesVisitor().get().endVisit(node);
    }
    if (getRealThis().getMCCollectionTypesVisitor().isPresent()) {
      getRealThis().getMCCollectionTypesVisitor().get().endVisit(node);
    }
    if (getRealThis().getMCLiteralsBasisVisitor().isPresent()) {
      getRealThis().getMCLiteralsBasisVisitor().get().endVisit(node);
    }
    if (getRealThis().getTypeSymbolsVisitor().isPresent()) {
      getRealThis().getTypeSymbolsVisitor().get().endVisit(node);
    }
    if (getRealThis().getMCBasicsVisitor().isPresent()) {
      getRealThis().getMCBasicsVisitor().get().endVisit(node);
    }
    if (getRealThis().getExpressionsBasisVisitor().isPresent()) {
      getRealThis().getExpressionsBasisVisitor().get().endVisit(node);
    }
    if (getRealThis().getMCSimpleGenericTypesVisitor().isPresent()) {
      getRealThis().getMCSimpleGenericTypesVisitor().get().endVisit(node);
    }
    if (getRealThis().getSetExpressionsVisitor().isPresent()) {
      getRealThis().getSetExpressionsVisitor().get().endVisit(node);
    }
    if (getRealThis().getJavaClassExpressionsVisitor().isPresent()) {
      getRealThis().getJavaClassExpressionsVisitor().get().endVisit(node);
    }
    if (getRealThis().getMCCommonLiteralsVisitor().isPresent()) {
      getRealThis().getMCCommonLiteralsVisitor().get().endVisit(node);
    }
    if (getRealThis().getBitExpressionsVisitor().isPresent()) {
      getRealThis().getBitExpressionsVisitor().get().endVisit(node);
    }
    if (getRealThis().getCommonExpressionsVisitor().isPresent()) {
      getRealThis().getCommonExpressionsVisitor().get().endVisit(node);
    }
    if (getRealThis().getAssignmentExpressionsVisitor().isPresent()) {
      getRealThis().getAssignmentExpressionsVisitor().get().endVisit(node);
    }
    if (getRealThis().getCombineExpressionsVisitor().isPresent()) {
      getRealThis().getCombineExpressionsVisitor().get().endVisit(node);
    }
    if(getRealThis().getOCLExpressionsVisitor().isPresent()){
      getRealThis().getOCLExpressionsVisitor().get().endVisit(node);
    }

  }

}
