/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.grammarfamily._symboltable;

import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4code.typescalculator.FullSynthesizeFromCD4Code;
import de.monticore.grammar.grammar_withconcepts.SynthesizeFromMCBT4Grammar;
import de.monticore.grammar.grammar_withconcepts.SynthesizeFromMCSGT4Grammar;
import de.monticore.types.check.*;

public class SynthesizeSymType extends FullSynthesizeFromCD4Code {

  @Override
  public void init(CD4CodeTraverser traverser){
    this.typeCheckResult = new TypeCheckResult();
    // New!
    final SynthesizeSymTypeFromMCBasicTypes synthesizeSymTypeFromMCBasicTypes = new SynthesizeFromMCBT4Grammar();
    synthesizeSymTypeFromMCBasicTypes.setTypeCheckResult(getTypeCheckResult());
    traverser.add4MCBasicTypes(synthesizeSymTypeFromMCBasicTypes);
    traverser.setMCBasicTypesHandler(synthesizeSymTypeFromMCBasicTypes);

    final SynthesizeSymTypeFromMCCollectionTypes synthesizeSymTypeFromMCCollectionTypes = new SynthesizeSymTypeFromMCCollectionTypes();
    synthesizeSymTypeFromMCCollectionTypes.setTypeCheckResult(getTypeCheckResult());
    traverser.add4MCCollectionTypes(synthesizeSymTypeFromMCCollectionTypes);
    traverser.setMCCollectionTypesHandler(synthesizeSymTypeFromMCCollectionTypes);

    final SynthesizeSymTypeFromMCArrayTypes synthesizeSymTypeFromMCArrayTypes = new SynthesizeSymTypeFromMCArrayTypes();
    synthesizeSymTypeFromMCArrayTypes.setTypeCheckResult(getTypeCheckResult());
    traverser.add4MCArrayTypes(synthesizeSymTypeFromMCArrayTypes);
    traverser.setMCArrayTypesHandler(synthesizeSymTypeFromMCArrayTypes);

    // New!
    final SynthesizeSymTypeFromMCSimpleGenericTypes synthesizeSymTypeFromMCSimpleGenericTypes = new SynthesizeFromMCSGT4Grammar();
    synthesizeSymTypeFromMCSimpleGenericTypes.setTypeCheckResult(getTypeCheckResult());
    traverser.add4MCSimpleGenericTypes(synthesizeSymTypeFromMCSimpleGenericTypes);
    traverser.setMCSimpleGenericTypesHandler(synthesizeSymTypeFromMCSimpleGenericTypes);

    final SynthesizeSymTypeFromMCFullGenericTypes synthesizeSymTypeFromMCFullGenericTypes = new SynthesizeSymTypeFromMCFullGenericTypes();
    synthesizeSymTypeFromMCFullGenericTypes.setTypeCheckResult(getTypeCheckResult());
    traverser.add4MCFullGenericTypes(synthesizeSymTypeFromMCFullGenericTypes);
    traverser.setMCFullGenericTypesHandler(synthesizeSymTypeFromMCFullGenericTypes);
  }

}
