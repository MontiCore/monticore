/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesTraverser;

import java.util.Optional;

public class FullSynthesizeCompKindFromMCBasicTypes implements ISynthesizeComponent {

  protected MCBasicTypesTraverser traverser;

  protected CompKindCheckResult resultWrapper;

  @Override
  public void init() {
    this.traverser = MCBasicTypesMill.traverser();
    this.resultWrapper = new CompKindCheckResult();
    SynthesizeCompKindFromMCBasicTypes synFromBasic = new SynthesizeCompKindFromMCBasicTypes(resultWrapper);

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
