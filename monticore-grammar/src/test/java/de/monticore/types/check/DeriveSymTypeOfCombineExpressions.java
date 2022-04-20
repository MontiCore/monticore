/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.combineexpressionswithliterals._ast.ASTExtReturnType;
import de.monticore.expressions.combineexpressionswithliterals._ast.ASTExtType;
import de.monticore.expressions.combineexpressionswithliterals._ast.ASTExtTypeArgument;
import de.monticore.expressions.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsHandler;
import de.monticore.expressions.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsTraverser;
import de.monticore.expressions.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsVisitor2;

import java.util.Optional;

public class DeriveSymTypeOfCombineExpressions extends AbstractDeriveFromExpression implements CombineExpressionsWithLiteralsVisitor2, CombineExpressionsWithLiteralsHandler {

  private SynthesizeSymTypeFromCombineExpressionsWithLiteralsDelegator synthesizer;

  protected CombineExpressionsWithLiteralsTraverser traverser;

  @Override
  public CombineExpressionsWithLiteralsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(CombineExpressionsWithLiteralsTraverser traverser) {
    this.traverser = traverser;
  }

  public DeriveSymTypeOfCombineExpressions(SynthesizeSymTypeFromCombineExpressionsWithLiteralsDelegator synthesizer){
    this.synthesizer = synthesizer;
  }

  @Override
  public void traverse(ASTExtType type){
    SymTypeExpression wholeResult = null;
    TypeCheckResult result = synthesizer.synthesizeType(type.getMCType());
    if(result.isPresentCurrentResult()){
      wholeResult=result.getCurrentResult();
    }
    if(wholeResult!=null){
      typeCheckResult.setCurrentResult(wholeResult);
      typeCheckResult.setType();
    }else{
      typeCheckResult.reset();
    }
  }

  @Override
  public void traverse(ASTExtReturnType returnType){
    SymTypeExpression wholeResult = null;
    if(returnType.getMCReturnType().isPresentMCVoidType()){
      wholeResult = SymTypeExpressionFactory.createTypeVoid();
    }else if(returnType.getMCReturnType().isPresentMCType()){
      TypeCheckResult res = synthesizer.synthesizeType(returnType.getMCReturnType());
      if(res.isPresentCurrentResult()){
        wholeResult = res.getCurrentResult();
      }
    }
    if(wholeResult!=null){
      typeCheckResult.setCurrentResult(wholeResult);
      typeCheckResult.setType();
    }else{
      typeCheckResult.reset();
    }
  }

  @Override
  public void traverse(ASTExtTypeArgument typeArgument){
    SymTypeExpression wholeResult = null;
    if(typeArgument.getMCTypeArgument().getMCTypeOpt().isPresent()){
      TypeCheckResult res = synthesizer.synthesizeType(typeArgument.getMCTypeArgument().getMCTypeOpt().get());
      if(res.isPresentCurrentResult()){
        wholeResult = res.getCurrentResult();
      }
    }
    if(wholeResult!=null){
      typeCheckResult.setCurrentResult(wholeResult);
      typeCheckResult.setType();
    }else{
      typeCheckResult.reset();
    }
  }

}
