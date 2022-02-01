/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.types.mcsimplegenerictypes.MCSimpleGenericTypesMill;
import de.monticore.types.mcsimplegenerictypes._visitor.MCSimpleGenericTypesTraverser;

import java.util.Optional;

public class FullSynthesizeFromMCSimpleGenericTypes implements ISynthesize {

  protected MCSimpleGenericTypesTraverser traverser;

  protected TypeCheckResult typeCheckResult;

  public FullSynthesizeFromMCSimpleGenericTypes(){
    init();
  }

  @Override
  public Optional<SymTypeExpression> getResult() {
    if(typeCheckResult.isPresentCurrentResult()){
      return Optional.of(typeCheckResult.getCurrentResult());
    }else{
      return Optional.empty();
    }
  }

  @Override
  public void init() {
    traverser = MCSimpleGenericTypesMill.traverser();
    typeCheckResult = new TypeCheckResult();

    SynthesizeSymTypeFromMCSimpleGenericTypes synFromSimple = new SynthesizeSymTypeFromMCSimpleGenericTypes();
    synFromSimple.setTypeCheckResult(typeCheckResult);
    SynthesizeSymTypeFromMCCollectionTypes synFromCollection = new SynthesizeSymTypeFromMCCollectionTypes();
    synFromCollection.setTypeCheckResult(typeCheckResult);
    SynthesizeSymTypeFromMCBasicTypes synFromBasic = new SynthesizeSymTypeFromMCBasicTypes();
    synFromBasic.setTypeCheckResult(typeCheckResult);

    traverser.add4MCSimpleGenericTypes(synFromSimple);
    traverser.setMCSimpleGenericTypesHandler(synFromSimple);
    traverser.add4MCCollectionTypes(synFromCollection);
    traverser.setMCCollectionTypesHandler(synFromCollection);
    traverser.add4MCBasicTypes(synFromBasic);
    traverser.setMCBasicTypesHandler(synFromBasic);
  }

  public MCSimpleGenericTypesTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(MCSimpleGenericTypesTraverser traverser) {
    this.traverser = traverser;
  }
}
