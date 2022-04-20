/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.types.mcarraytypes.MCArrayTypesMill;
import de.monticore.types.mcarraytypes._visitor.MCArrayTypesTraverser;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.Optional;

public class FullSynthesizeFromMCArrayTypes implements ISynthesize {

  protected MCArrayTypesTraverser traverser;

  protected TypeCheckResult typeCheckResult;

  public FullSynthesizeFromMCArrayTypes(){
    init();
  }

  public void init() {
    traverser = MCArrayTypesMill.traverser();
    typeCheckResult = new TypeCheckResult();

    SynthesizeSymTypeFromMCArrayTypes synFromArray = new SynthesizeSymTypeFromMCArrayTypes();
    synFromArray.setTypeCheckResult(typeCheckResult);
    SynthesizeSymTypeFromMCBasicTypes synFromBasic = new SynthesizeSymTypeFromMCBasicTypes();
    synFromBasic.setTypeCheckResult(typeCheckResult);

    traverser.add4MCArrayTypes(synFromArray);
    traverser.setMCArrayTypesHandler(synFromArray);
    traverser.add4MCBasicTypes(synFromBasic);
    traverser.setMCBasicTypesHandler(synFromBasic);
  }

  public MCArrayTypesTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(MCArrayTypesTraverser traverser) {
    this.traverser = traverser;
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
}
