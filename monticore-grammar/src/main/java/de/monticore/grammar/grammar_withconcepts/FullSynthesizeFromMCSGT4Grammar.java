/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.grammar_withconcepts;

import de.monticore.types.check.*;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcsimplegenerictypes.MCSimpleGenericTypesMill;
import de.monticore.types.mcsimplegenerictypes._visitor.MCSimpleGenericTypesTraverser;

public class FullSynthesizeFromMCSGT4Grammar implements ISynthesize {

  protected MCSimpleGenericTypesTraverser traverser;

  protected TypeCheckResult typeCheckResult;

  public FullSynthesizeFromMCSGT4Grammar(){
    init();
  }

  @Override
  public TypeCheckResult synthesizeType(ASTMCType type) {
    init();
    type.accept(traverser);
    return typeCheckResult.copy();
  }

  @Override
  public TypeCheckResult synthesizeType(ASTMCReturnType type) {
    init();
    type.accept(traverser);
    return typeCheckResult.copy();
  }

  @Override
  public TypeCheckResult synthesizeType(ASTMCQualifiedName qName) {
    init();
    qName.accept(traverser);
    return typeCheckResult.copy();
  }

  public void init() {
    traverser = MCSimpleGenericTypesMill.traverser();
    typeCheckResult = new TypeCheckResult();

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

  public MCSimpleGenericTypesTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(MCSimpleGenericTypesTraverser traverser) {
    this.traverser = traverser;
  }


}
