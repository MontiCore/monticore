package de.monticore.types.check;

import com.google.common.collect.Lists;
import de.monticore.expressions.combineexpressionswithliterals._ast.ASTExtReturnType;
import de.monticore.expressions.combineexpressionswithliterals._ast.ASTExtType;
import de.monticore.expressions.combineexpressionswithliterals._ast.ASTExtTypeArguments;
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
    type.getMCType().accept(synthesizer);
    Optional<SymTypeExpression> result = synthesizer.getResult();
    result.ifPresent(lastResult::setLast);
    lastResult.setType();
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
    }
  }

  @Override
  public void traverse(ASTExtTypeArguments typeArguments){
    List<SymTypeExpression> wholeResult = Lists.newArrayList();
    for(ASTMCTypeArgument typeArgument:typeArguments.getMCTypeArgumentList()){
      if(typeArgument.getMCTypeOpt().isPresent()) {
        typeArgument.getMCTypeOpt().get().accept(synthesizer);
        if (synthesizer.getResult().isPresent()) {
          wholeResult.add(synthesizer.getResult().get());
        }
      }
    }
    //TODO: wie macht man es hier?
  }

  public void setLastResult(LastResult lastResult) {
    this.lastResult = lastResult;
  }

  public LastResult getLastResult() {
    return lastResult;
  }
}
