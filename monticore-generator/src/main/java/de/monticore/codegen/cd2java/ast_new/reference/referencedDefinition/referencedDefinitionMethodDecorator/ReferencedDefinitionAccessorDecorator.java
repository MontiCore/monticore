package de.monticore.codegen.cd2java.ast_new.reference.referencedDefinition.referencedDefinitionMethodDecorator;

import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

public class ReferencedDefinitionAccessorDecorator extends AccessorDecorator {
  public ReferencedDefinitionAccessorDecorator(GlobalExtensionManagement glex) {
    super(glex, new ReferencedDefinitionOptAccessorDecorator(glex), new ReferencedDefinitionOptAccessorDecorator(glex), new ReferencedDefinitionListAccessorDecorator(glex));
  }
}
