/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast_emf;

import de.monticore.cd.codegen.CD2JavaTemplates;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.codegen.cd2java._ast.ASTCDDecorator;
import de.monticore.codegen.cd2java._ast.ast_class.ASTConstants;
import de.monticore.codegen.cd2java._ast.ast_interface.ASTLanguageInterfaceDecorator;
import de.monticore.codegen.cd2java._ast.ast_interface.FullASTInterfaceDecorator;
import de.monticore.codegen.cd2java._ast.builder.ASTBuilderDecorator;
import de.monticore.codegen.cd2java._ast.constants.ASTConstantsDecorator;
import de.monticore.codegen.cd2java._ast_emf.ast_class.ASTFullEmfDecorator;
import de.monticore.codegen.cd2java._ast_emf.emf_package.PackageImplDecorator;
import de.monticore.codegen.cd2java._ast_emf.emf_package.PackageInterfaceDecorator;
import de.monticore.codegen.cd2java._ast_emf.enums.EmfEnumDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.Joiners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.createPackageHookPoint;

/**
 * extension of the ASTCDDecorator with additional EMF functionality
 */
public class ASTEmfCDDecorator extends ASTCDDecorator {

  protected final PackageImplDecorator packageImplDecorator;

  protected final PackageInterfaceDecorator packageInterfaceDecorator;

  public ASTEmfCDDecorator(final GlobalExtensionManagement glex,
                           final ASTFullEmfDecorator astFullDecorator,
                           final ASTLanguageInterfaceDecorator astLanguageInterfaceDecorator,
                           final ASTBuilderDecorator astBuilderDecorator,
                           final ASTConstantsDecorator astConstantsDecorator,
                           final EmfEnumDecorator enumDecorator,
                           final FullASTInterfaceDecorator astInterfaceDecorator,
                           final PackageImplDecorator packageImplDecorator,
                           final PackageInterfaceDecorator packageInterfaceDecorator) {
    super(glex,
        astFullDecorator,
        astLanguageInterfaceDecorator,
        astBuilderDecorator,
        astConstantsDecorator,
        enumDecorator,
        astInterfaceDecorator);
    this.packageImplDecorator = packageImplDecorator;
    this.packageInterfaceDecorator = packageInterfaceDecorator;
  }

  @Override
  public ASTCDCompilationUnit decorate(final ASTCDCompilationUnit ast) {
    ASTCDCompilationUnit compilationUnit = super.decorate(ast);

    compilationUnit.getCDDefinition().addCDElement(createPackageInterface(ast));
    compilationUnit.getCDDefinition().addCDElement(createPackageImpl(ast));
    return compilationUnit;
  }

  protected ASTCDInterface createPackageInterface(ASTCDCompilationUnit compilationUnit) {
    ASTCDInterface astcdInterface = packageInterfaceDecorator.decorate(compilationUnit);
    return astcdInterface;
  }

  protected ASTCDClass createPackageImpl(ASTCDCompilationUnit compilationUnit) {
    ASTCDClass astcdClass = packageImplDecorator.decorate(compilationUnit);
    return astcdClass;
  }
}
