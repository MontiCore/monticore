/*(c) https://github.com/MontiCore/monticore*/
package de.monticore.types.check;

import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsTraverser;

public class FullSynthesizeFromCombineExpressionsWithLiterals extends AbstractSynthesize {

  public FullSynthesizeFromCombineExpressionsWithLiterals(){
    this(CombineExpressionsWithLiteralsMill.traverser());
  }

  public FullSynthesizeFromCombineExpressionsWithLiterals(CombineExpressionsWithLiteralsTraverser traverser){
    super(traverser);
    init(traverser);
  }

  public void init(CombineExpressionsWithLiteralsTraverser traverser) {
    SynthesizeSymTypeFromMCBasicTypes symTypeFromMCBasicTypes = new SynthesizeSymTypeFromMCBasicTypes();
    symTypeFromMCBasicTypes.setTypeCheckResult(getTypeCheckResult());
    traverser.add4MCBasicTypes(symTypeFromMCBasicTypes);
    traverser.setMCBasicTypesHandler(symTypeFromMCBasicTypes);

    SynthesizeSymTypeFromMCCollectionTypes symTypeFromMCCollectionTypes = new SynthesizeSymTypeFromMCCollectionTypes();
    symTypeFromMCCollectionTypes.setTypeCheckResult(getTypeCheckResult());
    traverser.add4MCCollectionTypes(symTypeFromMCCollectionTypes);
    traverser.setMCCollectionTypesHandler(symTypeFromMCCollectionTypes);

    SynthesizeSymTypeFromMCSimpleGenericTypes symTypeFromMCSimpleGenericTypes = new SynthesizeSymTypeFromMCSimpleGenericTypes();
    symTypeFromMCSimpleGenericTypes.setTypeCheckResult(getTypeCheckResult());
    traverser.add4MCSimpleGenericTypes(symTypeFromMCSimpleGenericTypes);
    traverser.setMCSimpleGenericTypesHandler(symTypeFromMCSimpleGenericTypes);
  }

}
