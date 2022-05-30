/* (c) https://github.com/MontiCore/monticore */
package mc.typechecktest;

import de.monticore.types.check.*;
import mc.typechecktest._visitor.TypeCheckTestTraverser;

public class FullSynthesizeFromTypeCheckTest extends AbstractSynthesize {

  public FullSynthesizeFromTypeCheckTest(){
    this(TypeCheckTestMill.traverser());
  }

  public FullSynthesizeFromTypeCheckTest(TypeCheckTestTraverser traverser) {
    super(traverser);
    init(traverser);
  }

  public void init(TypeCheckTestTraverser traverser) {
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
}
