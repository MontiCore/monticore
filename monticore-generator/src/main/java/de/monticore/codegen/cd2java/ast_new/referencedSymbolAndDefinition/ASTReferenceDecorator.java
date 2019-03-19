package de.monticore.codegen.cd2java.ast_new.referencedSymbolAndDefinition;

import de.monticore.codegen.cd2java.CompositeDecorator;
import de.monticore.codegen.cd2java.ast_new.referencedSymbolAndDefinition.referencedDefinitionMethodDecorator.ReferencedDefinitionAccessorDecorator;
import de.monticore.codegen.cd2java.ast_new.referencedSymbolAndDefinition.referenedSymbolMethodDecorator.ReferencedSymbolAccessorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;

public class ASTReferenceDecorator extends CompositeDecorator<ASTCDClass> {

  public ASTReferenceDecorator(GlobalExtensionManagement glex) {
    this(new ASTReferencedSymbolDecorator(glex, new ReferencedSymbolAccessorDecorator(glex)),
        new ASTReferencedDefinitionDecorator(glex, new ReferencedDefinitionAccessorDecorator(glex)));
  }

  public ASTReferenceDecorator(ASTReferencedSymbolDecorator referencedSymbolDecorator, ASTReferencedDefinitionDecorator referencedDefinitionDecorator) {
    super(referencedSymbolDecorator, referencedDefinitionDecorator);
  }
}
