package de.monticore.codegen.cd2java._ast_emf;

import de.monticore.codegen.cd2java._ast.ASTCDDecorator;
import de.monticore.codegen.cd2java._ast.ast_interface.ASTLanguageInterfaceDecorator;
import de.monticore.codegen.cd2java._ast.ast_interface.FullASTInterfaceDecorator;
import de.monticore.codegen.cd2java._ast.builder.ASTBuilderDecorator;
import de.monticore.codegen.cd2java._ast.constants.ASTConstantsDecorator;
import de.monticore.codegen.cd2java._ast.enums.EnumDecorator;
import de.monticore.codegen.cd2java._ast.mill.MillDecorator;
import de.monticore.codegen.cd2java._ast_emf.ast_class.ASTFullEmfDecorator;
import de.monticore.codegen.cd2java._ast_emf.emf_package.PackageImplDecorator;
import de.monticore.codegen.cd2java._ast_emf.emf_package.PackageInterfaceDecorator;
import de.monticore.codegen.cd2java._ast_emf.factory.EmfNodeFactoryDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.umlcd4a.symboltable.CD4AnalysisSymbolTableCreator;

public class ASTEmfCDDecorator extends ASTCDDecorator {

  protected final PackageImplDecorator packageImplDecorator;

  protected final PackageInterfaceDecorator packageInterfaceDecorator;

  public ASTEmfCDDecorator(final GlobalExtensionManagement glex,
                           final CD4AnalysisSymbolTableCreator symbolTableCreator,
                           final ASTFullEmfDecorator astFullDecorator,
                           final ASTLanguageInterfaceDecorator astLanguageInterfaceDecorator,
                           final ASTBuilderDecorator astBuilderDecorator,
                           final EmfNodeFactoryDecorator nodeFactoryDecorator,
                           final MillDecorator millDecorator,
                           final ASTConstantsDecorator astConstantsDecorator,
                           final EnumDecorator enumDecorator,
                           final FullASTInterfaceDecorator astInterfaceDecorator,
                           final PackageImplDecorator packageImplDecorator,
                           final PackageInterfaceDecorator packageInterfaceDecorator) {
    super(glex,
        symbolTableCreator,
        astFullDecorator,
        astLanguageInterfaceDecorator,
        astBuilderDecorator,
        nodeFactoryDecorator,
        millDecorator,
        astConstantsDecorator,
        enumDecorator,
        astInterfaceDecorator);
    this.packageImplDecorator = packageImplDecorator;
    this.packageInterfaceDecorator = packageInterfaceDecorator;
  }
}
