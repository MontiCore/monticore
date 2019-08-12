/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.methods;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java.methods.mutator.ListMutatorDecorator;
import de.monticore.codegen.cd2java.methods.mutator.MandatoryMutatorDecorator;
import de.monticore.codegen.cd2java.methods.mutator.OptionalMutatorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

import java.util.List;

public class MutatorDecorator extends SpecificMethodDecorator {

  public MutatorDecorator(final GlobalExtensionManagement glex) {
    super(glex, new MandatoryMutatorDecorator(glex), new OptionalMutatorDecorator(glex), new ListMutatorDecorator(glex));
  }

  public MutatorDecorator(final GlobalExtensionManagement glex,
      final AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> mandatoryMethodDecorator,
      final AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> optionalMethodDecorator,
      final AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> listMethodDecorator) {
    super(glex,mandatoryMethodDecorator, optionalMethodDecorator, listMethodDecorator);
  }
}
