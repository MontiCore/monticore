/* (c) https://github.com/MontiCore/monticore */
package mc.typescalculator;

import de.monticore.types.check.*;
import mc.typescalculator.myownlanguage._visitor.MyOwnLanguageDelegatorVisitor;

import java.util.Optional;

public class SynthesizeSymTypeFromMyOwnLanguage extends MyOwnLanguageDelegatorVisitor implements ISynthesize {

  protected SynthesizeSymTypeFromMCBasicTypes symTypeFromMCBasicTypes;
  protected SynthesizeSymTypeFromMCCollectionTypes symTypeFromMCCollectionTypes;
  protected SynthesizeSymTypeFromUnitTypes symTypeFromUnitTypes;
  protected TypeCheckResult result = new TypeCheckResult();
  private MyOwnLanguageDelegatorVisitor realThis;

  @Override
  public MyOwnLanguageDelegatorVisitor getRealThis() {
    return realThis;
  }

  public SynthesizeSymTypeFromMyOwnLanguage(){
    realThis = this;
    symTypeFromMCBasicTypes = new SynthesizeSymTypeFromMCBasicTypes();
    symTypeFromMCBasicTypes.setTypeCheckResult(result);
    setMCBasicTypesVisitor(symTypeFromMCBasicTypes);
    symTypeFromMCCollectionTypes = new SynthesizeSymTypeFromMCCollectionTypes();
    symTypeFromMCCollectionTypes.setTypeCheckResult(result);
    setMCCollectionTypesVisitor(symTypeFromMCCollectionTypes);
    symTypeFromUnitTypes = new SynthesizeSymTypeFromUnitTypes();
    symTypeFromUnitTypes.setTypeCheckResult(result);
    setUnitTypesVisitor(symTypeFromUnitTypes);
  }


  @Override
  public Optional<SymTypeExpression> getResult() {
    return Optional.ofNullable(result.getCurrentResult());
  }

  @Override
  public void init() {
    result = new TypeCheckResult();
    symTypeFromUnitTypes.setTypeCheckResult(result);
    setUnitTypesVisitor(symTypeFromUnitTypes);
    symTypeFromMCCollectionTypes.setTypeCheckResult(result);
    setMCCollectionTypesVisitor(symTypeFromMCCollectionTypes);
    symTypeFromMCBasicTypes.setTypeCheckResult(result);
    setMCBasicTypesVisitor(symTypeFromMCBasicTypes);
  }
}
