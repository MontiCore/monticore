/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesTraverser;

import java.util.Optional;

public class FullSynthesizeComponentFromMCBasicTypes implements ISynthesizeComponent {

  protected MCBasicTypesTraverser traverser;

  protected CompTypeCheckResult resultWrapper;

  @Override
  public void init() {
    this.traverser = MCBasicTypesMill.traverser();
    this.resultWrapper = new CompTypeCheckResult();
    SynthesizeCompTypeFromMCBasicTypes synFromBasic = new SynthesizeCompTypeFromMCBasicTypes(resultWrapper);

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
