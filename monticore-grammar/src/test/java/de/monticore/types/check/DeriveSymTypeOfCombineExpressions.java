/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.collect.Lists;
import de.monticore.expressions.combineexpressionswithliterals._ast.ASTExtReturnType;
import de.monticore.expressions.combineexpressionswithliterals._ast.ASTExtType;
import de.monticore.expressions.combineexpressionswithliterals._ast.ASTExtTypeArgument;
import de.monticore.expressions.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsVisitor;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;

import java.util.List;
import java.util.Optional;

public class DeriveSymTypeOfCombineExpressions implements CombineExpressionsWithLiteralsVisitor {

  private CombineExpressionsWithLiteralsVisitor realThis;
  private SynthesizeSymTypeFromMCBasicTypes synthesizer;
  private LastResult lastResult;

  @Override
  public void setRealThis(CombineExpressionsWithLiteralsVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public CombineExpressionsWithLiteralsVisitor getRealThis() {
    return realThis;
  }

  public DeriveSymTypeOfCombineExpressions(SynthesizeSymTypeFromMCBasicTypes synthesizer){
    this.realThis=this;
    this.lastResult = new LastResult();
    this.synthesizer = synthesizer;
  }

  @Override
  public void traverse(ASTExtType type){
    SymTypeExpression wholeResult = null;
    type.getMCType().accept(synthesizer);
    Optional<SymTypeExpression> result = synthesizer.getResult();
    if(result.isPresent()){
      wholeResult=result.get();
    }
    if(wholeResult!=null){
      lastResult.setLast(wholeResult);
      lastResult.setType();
    }else{
      lastResult.reset();
    }
  }

  @Override
  public void traverse(ASTExtReturnType returnType){
    SymTypeExpression wholeResult = null;
    if(returnType.getMCReturnType().isPresentMCVoidType()){
      wholeResult = SymTypeExpressionFactory.createTypeVoid();
    }else if(returnType.getMCReturnType().isPresentMCType()){
      returnType.getMCReturnType().accept(synthesizer);
      if(synthesizer.getResult().isPresent()){
        wholeResult = synthesizer.getResult().get();
      }
    }
    if(wholeResult!=null){
      lastResult.setLast(wholeResult);
      lastResult.setType();
    }else{
      lastResult.reset();
    }
  }

  @Override
  public void traverse(ASTExtTypeArgument typeArgument){
    SymTypeExpression wholeResult = null;
    if(typeArgument.getMCTypeArgument().getMCTypeOpt().isPresent()){
      typeArgument.getMCTypeArgument().getMCTypeOpt().get().accept(synthesizer);
      if(synthesizer.getResult().isPresent()){
        wholeResult = synthesizer.getResult().get();
      }
    }
    if(wholeResult!=null){
      lastResult.setLast(wholeResult);
      lastResult.setType();
    }else{
      lastResult.reset();
    }
  }

  public void setLastResult(LastResult lastResult) {
    this.lastResult = lastResult;
  }

  public LastResult getLastResult() {
    return lastResult;
  }
}
