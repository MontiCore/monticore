/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.types.mcbasictypes._visitor.MCBasicTypesTraverser;
import de.monticore.types.mcsimplegenerictypes.MCSimpleGenericTypesMill;
import de.monticore.types.mcsimplegenerictypes._visitor.MCSimpleGenericTypesTraverser;

import java.util.Optional;

public class FullSynthesizeComponentFromMCSimpleGenericTypes implements ISynthesizeComponent {

  protected MCSimpleGenericTypesTraverser traverser;

  protected CompTypeCheckResult resultWrapper;

  @Override
  public void init() {
    this.traverser = MCSimpleGenericTypesMill.traverser();
    this.resultWrapper = new CompTypeCheckResult();
    SynthesizeCompTypeFromMCBasicTypes synFromBasic = new SynthesizeCompTypeFromMCBasicTypes(resultWrapper);
    SynthesizeCompTypeFromMCSimpleGenericTypes synFromSimple = new SynthesizeCompTypeFromMCSimpleGenericTypes(resultWrapper, new FullSynthesizeFromMCSimpleGenericTypes());

    traverser.setMCSimpleGenericTypesHandler(synFromSimple);
    traverser.setMCBasicTypesHandler(synFromBasic);
  }

  @Override
  public MCBasicTypesTraverser getTraverser() {
    return traverser;
  }

  @Override
  public Optional<CompKindExpression> getResult() {
    return resultWrapper.getResult();
  }
}
