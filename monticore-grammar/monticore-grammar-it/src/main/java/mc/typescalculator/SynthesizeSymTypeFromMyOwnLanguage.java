/* (c) https://github.com/MontiCore/monticore */
package mc.typescalculator;

import de.monticore.types.check.*;
import de.monticore.types.typesymbols._symboltable.TypeSymbolsScope;
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

  public SynthesizeSymTypeFromMyOwnLanguage(TypeSymbolsScope scope){
    realThis = this;
    symTypeFromMCBasicTypes = new SynthesizeSymTypeFromMCBasicTypes(scope);
    symTypeFromMCBasicTypes.setTypeCheckResult(result);
    setMCBasicTypesVisitor(symTypeFromMCBasicTypes);
    symTypeFromMCCollectionTypes = new SynthesizeSymTypeFromMCCollectionTypes(scope);
    symTypeFromMCCollectionTypes.setTypeCheckResult(result);
    setMCCollectionTypesVisitor(symTypeFromMCCollectionTypes);
    symTypeFromUnitTypes = new SynthesizeSymTypeFromUnitTypes(scope);
    symTypeFromUnitTypes.setTypeCheckResult(result);
    setUnitTypesVisitor(symTypeFromUnitTypes);
  }


  @Override
  public Optional<SymTypeExpression> getResult() {
    return Optional.ofNullable(result.getLast());
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
