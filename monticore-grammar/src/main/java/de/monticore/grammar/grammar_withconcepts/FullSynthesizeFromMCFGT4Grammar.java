/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.grammar_withconcepts;

import de.monticore.types.check.*;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcfullgenerictypes.MCFullGenericTypesMill;
import de.monticore.types.mcfullgenerictypes._visitor.MCFullGenericTypesTraverser;

import java.util.Optional;

public class FullSynthesizeFromMCFGT4Grammar extends AbstractSynthesize {

  public FullSynthesizeFromMCFGT4Grammar(){
    this(MCFullGenericTypesMill.traverser());
  }

  public FullSynthesizeFromMCFGT4Grammar(MCFullGenericTypesTraverser traverser){
    super(traverser);
    init(traverser);
  }

  public void init(MCFullGenericTypesTraverser traverser) {
    SynthesizeSymTypeFromMCFullGenericTypes synFromFull = new SynthesizeSymTypeFromMCFullGenericTypes();
    synFromFull.setTypeCheckResult(typeCheckResult);
    SynthesizeSymTypeFromMCSimpleGenericTypes synFromSimple = new SynthesizeFromMCSGT4Grammar();
    synFromSimple.setTypeCheckResult(typeCheckResult);
    SynthesizeSymTypeFromMCCollectionTypes synFromCollection = new SynthesizeSymTypeFromMCCollectionTypes();
    synFromCollection.setTypeCheckResult(typeCheckResult);
    SynthesizeSymTypeFromMCBasicTypes synFromBasic = new SynthesizeFromMCBT4Grammar();
    synFromBasic.setTypeCheckResult(typeCheckResult);

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
