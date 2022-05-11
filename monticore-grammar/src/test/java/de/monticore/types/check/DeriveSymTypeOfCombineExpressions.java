/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.combineexpressionswithliterals._ast.ASTExtReturnType;
import de.monticore.expressions.combineexpressionswithliterals._ast.ASTExtType;
import de.monticore.expressions.combineexpressionswithliterals._ast.ASTExtTypeArgument;
import de.monticore.expressions.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsHandler;
import de.monticore.expressions.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsTraverser;
import de.monticore.expressions.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsVisitor2;

public class DeriveSymTypeOfCombineExpressions extends AbstractDeriveFromExpression implements CombineExpressionsWithLiteralsVisitor2, CombineExpressionsWithLiteralsHandler {

  private FullSynthesizeFromCombineExpressionsWithLiterals synthesizer;

  public FullSynthesizeFromCombineExpressionsWithLiterals getSynthesizer() {
    return synthesizer;
  }

  protected CombineExpressionsWithLiteralsTraverser traverser;

  @Override
  public CombineExpressionsWithLiteralsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(CombineExpressionsWithLiteralsTraverser traverser) {
    this.traverser = traverser;
  }

  public DeriveSymTypeOfCombineExpressions(FullSynthesizeFromCombineExpressionsWithLiterals synthesizer){
    this.synthesizer = synthesizer;
  }

  @Override
  public void traverse(ASTExtType type){
    SymTypeExpression wholeResult = null;
    TypeCheckResult result = getSynthesizer().synthesizeType(type.getMCType());
    if(result.isPresentResult()){
      wholeResult=result.getResult();
    }
    if(wholeResult!=null){
      getTypeCheckResult().setResult(wholeResult);
      getTypeCheckResult().setType();
    }else{
      getTypeCheckResult().reset();
    }
  }

  @Override
  public void traverse(ASTExtReturnType returnType){
    SymTypeExpression wholeResult = null;
    if(returnType.getMCReturnType().isPresentMCVoidType()){
      wholeResult = SymTypeExpressionFactory.createTypeVoid();
    }else if(returnType.getMCReturnType().isPresentMCType()){
      TypeCheckResult res = getSynthesizer().synthesizeType(returnType.getMCReturnType());
      if(res.isPresentResult()){
        wholeResult = res.getResult();
      }
    }
    if(wholeResult!=null){
      getTypeCheckResult().setResult(wholeResult);
      getTypeCheckResult().setType();
    }else{
      getTypeCheckResult().reset();
    }
  }

  @Override
  public void traverse(ASTExtTypeArgument typeArgument){
    SymTypeExpression wholeResult = null;
    if(typeArgument.getMCTypeArgument().getMCTypeOpt().isPresent()){
      TypeCheckResult res = getSynthesizer().synthesizeType(typeArgument.getMCTypeArgument().getMCTypeOpt().get());
      if(res.isPresentResult()){
        wholeResult = res.getResult();
      }
    }
    if(wholeResult!=null){
      getTypeCheckResult().setResult(wholeResult);
      getTypeCheckResult().setType();
    }else{
      getTypeCheckResult().reset();
    }
  }

}
