/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;


import de.monticore.types.mccollectiontypes.MCCollectionTypesMill;
import de.monticore.types.mccollectiontypes._visitor.MCCollectionTypesTraverser;

public class FullSynthesizeFromMCCollectionTypes extends AbstractSynthesize {

  public FullSynthesizeFromMCCollectionTypes(){
    this(MCCollectionTypesMill.traverser());
  }

  public FullSynthesizeFromMCCollectionTypes(MCCollectionTypesTraverser traverser){
    super(traverser);
    init(traverser);
  }

  public void init(MCCollectionTypesTraverser traverser) {
    SynthesizeSymTypeFromMCCollectionTypes synFromCollection = new SynthesizeSymTypeFromMCCollectionTypes();
    synFromCollection.setTypeCheckResult(typeCheckResult);
    SynthesizeSymTypeFromMCBasicTypes synFromBasic = new SynthesizeSymTypeFromMCBasicTypes();
    synFromBasic.setTypeCheckResult(typeCheckResult);

    traverser.add4MCCollectionTypes(synFromCollection);
    traverser.setMCCollectionTypesHandler(synFromCollection);
    traverser.add4MCBasicTypes(synFromBasic);
    traverser.setMCBasicTypesHandler(synFromBasic);
  }

}
