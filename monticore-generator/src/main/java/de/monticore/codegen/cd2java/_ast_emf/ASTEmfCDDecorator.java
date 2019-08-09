package de.monticore.codegen.cd2java._ast_emf;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDInterface;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java._ast.ASTCDDecorator;
import de.monticore.codegen.cd2java._ast.ast_class.ASTConstants;
import de.monticore.codegen.cd2java._ast.ast_interface.ASTLanguageInterfaceDecorator;
import de.monticore.codegen.cd2java._ast.ast_interface.FullASTInterfaceDecorator;
import de.monticore.codegen.cd2java._ast.builder.ASTBuilderDecorator;
import de.monticore.codegen.cd2java._ast.constants.ASTConstantsDecorator;
import de.monticore.codegen.cd2java._ast.mill.MillDecorator;
import de.monticore.codegen.cd2java._ast.mill.MillForSuperDecorator;
import de.monticore.codegen.cd2java._ast_emf.ast_class.ASTFullEmfDecorator;
import de.monticore.codegen.cd2java._ast_emf.emf_package.PackageImplDecorator;
import de.monticore.codegen.cd2java._ast_emf.emf_package.PackageInterfaceDecorator;
import de.monticore.codegen.cd2java._ast_emf.enums.EmfEnumDecorator;
import de.monticore.codegen.cd2java._ast_emf.factory.EmfNodeFactoryDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.createPackageHookPoint;

public class ASTEmfCDDecorator extends ASTCDDecorator {

  protected final PackageImplDecorator packageImplDecorator;

  protected final PackageInterfaceDecorator packageInterfaceDecorator;

  public ASTEmfCDDecorator(final GlobalExtensionManagement glex,
                           final ASTFullEmfDecorator astFullDecorator,
                           final ASTLanguageInterfaceDecorator astLanguageInterfaceDecorator,
                           final ASTBuilderDecorator astBuilderDecorator,
                           final EmfNodeFactoryDecorator nodeFactoryDecorator,
                           final MillDecorator millDecorator,
                           final MillForSuperDecorator millForSuperDecorator,
                           final ASTConstantsDecorator astConstantsDecorator,
                           final EmfEnumDecorator enumDecorator,
                           final FullASTInterfaceDecorator astInterfaceDecorator,
                           final PackageImplDecorator packageImplDecorator,
                           final PackageInterfaceDecorator packageInterfaceDecorator) {
    super(glex,
        astFullDecorator,
        astLanguageInterfaceDecorator,
        astBuilderDecorator,
        nodeFactoryDecorator,
        millDecorator,
        millForSuperDecorator,
        astConstantsDecorator,
        enumDecorator,
        astInterfaceDecorator);
    this.packageImplDecorator = packageImplDecorator;
    this.packageInterfaceDecorator = packageInterfaceDecorator;
  }

  @Override
  public ASTCDCompilationUnit decorate(final ASTCDCompilationUnit ast) {
    ASTCDCompilationUnit compilationUnit = super.decorate(ast);

    List<String> astPackage = new ArrayList<>(ast.getPackageList());
    astPackage.addAll(Arrays.asList(ast.getCDDefinition().getName().toLowerCase(), ASTConstants.AST_PACKAGE));

    compilationUnit.getCDDefinition().addCDInterface(createPackageInterface(ast, astPackage));
    compilationUnit.getCDDefinition().addCDClass(createPackageImpl(ast, astPackage));
    return compilationUnit;
  }

  protected ASTCDInterface createPackageInterface(ASTCDCompilationUnit compilationUnit, List<String> astPackage) {
    ASTCDInterface astcdInterface = packageInterfaceDecorator.decorate(compilationUnit);
    this.replaceTemplate(CoreTemplates.PACKAGE, astcdInterface, createPackageHookPoint(astPackage));
    return astcdInterface;
  }

  protected ASTCDClass createPackageImpl(ASTCDCompilationUnit compilationUnit, List<String> astPackage) {
    ASTCDClass astcdClass = packageImplDecorator.decorate(compilationUnit);
    this.replaceTemplate(CoreTemplates.PACKAGE, astcdClass, createPackageHookPoint(astPackage));
    return astcdClass;
  }
}
