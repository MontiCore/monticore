/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.types.mcfullgenerictypes.MCFullGenericTypesMill;
import de.monticore.types.mcfullgenerictypes._visitor.MCFullGenericTypesTraverser;

public class FullSynthesizeFromMCFullGenericTypes extends AbstractSynthesize {

  public FullSynthesizeFromMCFullGenericTypes(){
    this(MCFullGenericTypesMill.traverser());
  }

  public FullSynthesizeFromMCFullGenericTypes(MCFullGenericTypesTraverser traverser){
    super(traverser);
    init(traverser);
  }

  public void init(MCFullGenericTypesTraverser traverser) {
    SynthesizeSymTypeFromMCFullGenericTypes synFromFull = new SynthesizeSymTypeFromMCFullGenericTypes();
    synFromFull.setTypeCheckResult(getTypeCheckResult());
    SynthesizeSymTypeFromMCSimpleGenericTypes synFromSimple = new SynthesizeSymTypeFromMCSimpleGenericTypes();
    synFromSimple.setTypeCheckResult(getTypeCheckResult());
    SynthesizeSymTypeFromMCCollectionTypes synFromCollection = new SynthesizeSymTypeFromMCCollectionTypes();
    synFromCollection.setTypeCheckResult(getTypeCheckResult());
    SynthesizeSymTypeFromMCBasicTypes synFromBasic = new SynthesizeSymTypeFromMCBasicTypes();
    synFromBasic.setTypeCheckResult(getTypeCheckResult());

    traverser.add4MCFullGenericTypes(synFromFull);
    traverser.setMCFullGenericTypesHandler(synFromFull);
    traverser.add4MCSimpleGenericTypes(synFromSimple);
    traverser.setMCSimpleGenericTypesHandler(synFromSimple);
    traverser.add4MCCollectionTypes(synFromCollection);
    traverser.setMCCollectionTypesHandler(synFromCollection);
    traverser.add4MCBasicTypes(synFromBasic);
    traverser.setMCBasicTypesHandler(synFromBasic);
  }

  public void setTraverser(MCFullGenericTypesTraverser traverser) {
    this.traverser = traverser;
  }
}
