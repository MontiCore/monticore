package de.monticore.codegen.cd2java.methods;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java.methods.accessor.ListAccessorDecorator;
import de.monticore.codegen.cd2java.methods.accessor.MandatoryAccessorDecorator;
import de.monticore.codegen.cd2java.methods.accessor.OptionalAccessorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

import java.util.List;

public class AccessorDecorator extends SpecificMethodDecorator {

  public AccessorDecorator(final GlobalExtensionManagement glex) {
    super(glex, new MandatoryAccessorDecorator(glex), new OptionalAccessorDecorator(glex), new ListAccessorDecorator(glex));
  }

  public AccessorDecorator(final GlobalExtensionManagement glex,
      final AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> mandatoryMethodDecorator,
      final AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> optionalMethodDecorator,
      final AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> listMethodDecorator) {
    super(glex,mandatoryMethodDecorator, optionalMethodDecorator, listMethodDecorator);
  }
}
