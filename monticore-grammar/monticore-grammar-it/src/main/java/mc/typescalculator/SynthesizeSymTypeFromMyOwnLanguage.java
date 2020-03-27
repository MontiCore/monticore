/* (c) https://github.com/MontiCore/monticore */
package mc.typescalculator;

import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisScope;
import de.monticore.types.check.*;
import mc.typescalculator.myownlanguage._visitor.MyOwnLanguageDelegatorVisitor;

import java.util.Optional;

public class SynthesizeSymTypeFromMyOwnLanguage extends MyOwnLanguageDelegatorVisitor implements ISynthesize {

  protected SynthesizeSymTypeFromMCBasicTypes symTypeFromMCBasicTypes;
  protected SynthesizeSymTypeFromMCCollectionTypes symTypeFromMCCollectionTypes;
  protected SynthesizeSymTypeFromUnitTypes symTypeFromUnitTypes;
  protected LastResult result = new LastResult();
  private MyOwnLanguageDelegatorVisitor realThis;

  @Override
  public MyOwnLanguageDelegatorVisitor getRealThis() {
    return realThis;
  }

  public SynthesizeSymTypeFromMyOwnLanguage(ExpressionsBasisScope scope){
    realThis = this;
    symTypeFromMCBasicTypes = new SynthesizeSymTypeFromMCBasicTypes(scope);
    symTypeFromMCBasicTypes.setLastResult(result);
    setMCBasicTypesVisitor(symTypeFromMCBasicTypes);
    symTypeFromMCCollectionTypes = new SynthesizeSymTypeFromMCCollectionTypes(scope);
    symTypeFromMCCollectionTypes.setLastResult(result);
    setMCCollectionTypesVisitor(symTypeFromMCCollectionTypes);
    symTypeFromUnitTypes = new SynthesizeSymTypeFromUnitTypes(scope);
    symTypeFromUnitTypes.setLastResult(result);
    setUnitTypesVisitor(symTypeFromUnitTypes);
  }


  @Override
  public Optional<SymTypeExpression> getResult() {
    return Optional.ofNullable(result.getLast());
  }

  @Override
  public void init() {
    result = new LastResult();
    symTypeFromUnitTypes.setLastResult(result);
    setUnitTypesVisitor(symTypeFromUnitTypes);
    symTypeFromMCCollectionTypes.setLastResult(result);
    setMCCollectionTypesVisitor(symTypeFromMCCollectionTypes);
    symTypeFromMCBasicTypes.setLastResult(result);
    setMCBasicTypesVisitor(symTypeFromMCBasicTypes);
  }
}
