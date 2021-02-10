/* (c) https://github.com/MontiCore/monticore */
package mc.typechecktest;

import de.monticore.types.check.*;
import mc.typechecktest._visitor.TypeCheckTestTraverser;

import java.util.Optional;

public class SynthesizeSymTypeFromTypeCheckTest implements ISynthesize {

  protected TypeCheckTestTraverser traverser;

  protected TypeCheckResult typeCheckResult;

  public SynthesizeSymTypeFromTypeCheckTest(){
    init();
  }

  @Override
  public Optional<SymTypeExpression> getResult() {
    return Optional.ofNullable(typeCheckResult.getCurrentResult());
  }

  @Override
  public void init() {
    traverser = TypeCheckTestMill.traverser();
    typeCheckResult = new TypeCheckResult();

    SynthesizeSymTypeFromMCBasicTypes basicTypes = new SynthesizeSymTypeFromMCBasicTypes();
    SynthesizeSymTypeFromMCCollectionTypes collectionTypes = new SynthesizeSymTypeFromMCCollectionTypes();
    SynthesizeSymTypeFromMCSimpleGenericTypes simpleGenericTypes = new SynthesizeSymTypeFromMCSimpleGenericTypes();
    basicTypes.setTypeCheckResult(typeCheckResult);
    collectionTypes.setTypeCheckResult(typeCheckResult);
    simpleGenericTypes.setTypeCheckResult(typeCheckResult);

    traverser.setMCBasicTypesHandler(basicTypes);
    traverser.add4MCBasicTypes(basicTypes);
    traverser.setMCCollectionTypesHandler(collectionTypes);
    traverser.add4MCCollectionTypes(collectionTypes);
    traverser.setMCSimpleGenericTypesHandler(simpleGenericTypes);
    traverser.add4MCSimpleGenericTypes(simpleGenericTypes);
  }

  @Override
  public TypeCheckTestTraverser getTraverser() {
    return traverser;
  }
}
