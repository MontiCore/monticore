/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.grammar_withconcepts;

import de.monticore.types.check.*;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcsimplegenerictypes.MCSimpleGenericTypesMill;
import de.monticore.types.mcsimplegenerictypes._visitor.MCSimpleGenericTypesTraverser;

public class FullSynthesizeFromMCSGT4Grammar extends AbstractSynthesize {

  public FullSynthesizeFromMCSGT4Grammar(){
    this(MCSimpleGenericTypesMill.traverser());
  }

  public FullSynthesizeFromMCSGT4Grammar(MCSimpleGenericTypesTraverser traverser){
    super(traverser);
    init(traverser);
  }

  public void init(MCSimpleGenericTypesTraverser traverser) {
    SynthesizeSymTypeFromMCFullGenericTypes synFromFull = new SynthesizeSymTypeFromMCFullGenericTypes();
    synFromFull.setTypeCheckResult(typeCheckResult);
    SynthesizeSymTypeFromMCSimpleGenericTypes synFromSimple = new SynthesizeFromMCSGT4Grammar();
    synFromSimple.setTypeCheckResult(typeCheckResult);
    SynthesizeSymTypeFromMCCollectionTypes synFromCollection = new SynthesizeSymTypeFromMCCollectionTypes();
    synFromCollection.setTypeCheckResult(typeCheckResult);
    SynthesizeSymTypeFromMCBasicTypes synFromBasic = new SynthesizeFromMCBT4Grammar();
    synFromBasic.setTypeCheckResult(typeCheckResult);

    traverser.add4MCSimpleGenericTypes(synFromSimple);
    traverser.setMCSimpleGenericTypesHandler(synFromSimple);
    traverser.add4MCCollectionTypes(synFromCollection);
    traverser.setMCCollectionTypesHandler(synFromCollection);
    traverser.add4MCBasicTypes(synFromBasic);
    traverser.setMCBasicTypesHandler(synFromBasic);
  }

}
