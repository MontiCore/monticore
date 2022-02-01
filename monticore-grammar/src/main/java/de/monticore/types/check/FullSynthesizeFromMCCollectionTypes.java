/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;


import de.monticore.types.mccollectiontypes.MCCollectionTypesMill;
import de.monticore.types.mccollectiontypes._visitor.MCCollectionTypesTraverser;

import java.util.Optional;

public class FullSynthesizeFromMCCollectionTypes implements ISynthesize {

  protected MCCollectionTypesTraverser traverser;

  protected TypeCheckResult typeCheckResult;

  public FullSynthesizeFromMCCollectionTypes(){
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
    traverser = MCCollectionTypesMill.traverser();
    typeCheckResult = new TypeCheckResult();

    SynthesizeSymTypeFromMCCollectionTypes synFromCollection = new SynthesizeSymTypeFromMCCollectionTypes();
    synFromCollection.setTypeCheckResult(typeCheckResult);
    SynthesizeSymTypeFromMCBasicTypes synFromBasic = new SynthesizeSymTypeFromMCBasicTypes();
    synFromBasic.setTypeCheckResult(typeCheckResult);

    traverser.add4MCCollectionTypes(synFromCollection);
    traverser.setMCCollectionTypesHandler(synFromCollection);
    traverser.add4MCBasicTypes(synFromBasic);
    traverser.setMCBasicTypesHandler(synFromBasic);
  }

  public MCCollectionTypesTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(MCCollectionTypesTraverser traverser) {
    this.traverser = traverser;
  }
}
