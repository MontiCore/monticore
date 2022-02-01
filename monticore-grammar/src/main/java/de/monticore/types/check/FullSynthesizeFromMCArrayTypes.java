/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.types.mcarraytypes.MCArrayTypesMill;
import de.monticore.types.mcarraytypes._visitor.MCArrayTypesTraverser;

import java.util.Optional;

public class FullSynthesizeFromMCArrayTypes implements ISynthesize {

  protected MCArrayTypesTraverser traverser;

  protected TypeCheckResult typeCheckResult;

  public FullSynthesizeFromMCArrayTypes(){
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
    traverser = MCArrayTypesMill.traverser();
    typeCheckResult = new TypeCheckResult();

    SynthesizeSymTypeFromMCArrayTypes synFromArray = new SynthesizeSymTypeFromMCArrayTypes();
    synFromArray.setTypeCheckResult(typeCheckResult);
    SynthesizeSymTypeFromMCBasicTypes synFromBasic = new SynthesizeSymTypeFromMCBasicTypes();
    synFromBasic.setTypeCheckResult(typeCheckResult);

    traverser.add4MCArrayTypes(synFromArray);
    traverser.setMCArrayTypesHandler(synFromArray);
    traverser.add4MCBasicTypes(synFromBasic);
    traverser.setMCBasicTypesHandler(synFromBasic);
  }

  public MCArrayTypesTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(MCArrayTypesTraverser traverser) {
    this.traverser = traverser;
  }
}
