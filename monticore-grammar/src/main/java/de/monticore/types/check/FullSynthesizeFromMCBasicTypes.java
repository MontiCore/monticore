/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesTraverser;

import java.util.Optional;

public class FullSynthesizeFromMCBasicTypes extends AbstractSynthesize {

  public FullSynthesizeFromMCBasicTypes(){
    this(MCBasicTypesMill.traverser());
  }

  public FullSynthesizeFromMCBasicTypes(MCBasicTypesTraverser traverser){
    super(traverser);
    init(traverser);
  }

  public void init(MCBasicTypesTraverser traverser) {
    SynthesizeSymTypeFromMCBasicTypes synFromBasic = new SynthesizeSymTypeFromMCBasicTypes();
    synFromBasic.setTypeCheckResult(typeCheckResult);

    traverser.add4MCBasicTypes(synFromBasic);
    traverser.setMCBasicTypesHandler(synFromBasic);
  }
}
