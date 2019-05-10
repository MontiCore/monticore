package de.monticore.codegen.cd2java.ast_new.reference;

import de.monticore.codegen.cd2java.CompositeDecorator;
import de.monticore.codegen.cd2java.ast_new.reference.referencedDefinition.ASTReferencedDefinitionDecorator;
import de.monticore.codegen.cd2java.ast_new.reference.referencedDefinition.referencedDefinitionMethodDecorator.ReferencedDefinitionAccessorDecorator;
import de.monticore.codegen.cd2java.ast_new.reference.referencedSymbol.ASTReferencedSymbolDecorator;
import de.monticore.codegen.cd2java.ast_new.reference.referencedSymbol.referenedSymbolMethodDecorator.ReferencedSymbolAccessorDecorator;
import de.monticore.codegen.cd2java.symboltable.SymbolTableService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;

public class ASTReferenceDecorator extends CompositeDecorator<ASTCDClass> {

  public ASTReferenceDecorator(GlobalExtensionManagement glex, SymbolTableService symbolTableService) {
    this(new ASTReferencedSymbolDecorator(glex, new ReferencedSymbolAccessorDecorator(glex, symbolTableService), symbolTableService),
        new ASTReferencedDefinitionDecorator(glex, new ReferencedDefinitionAccessorDecorator(glex, symbolTableService), symbolTableService));
  }

  public ASTReferenceDecorator(ASTReferencedSymbolDecorator referencedSymbolDecorator, ASTReferencedDefinitionDecorator referencedDefinitionDecorator) {
    super(referencedSymbolDecorator, referencedDefinitionDecorator);
  }
}
