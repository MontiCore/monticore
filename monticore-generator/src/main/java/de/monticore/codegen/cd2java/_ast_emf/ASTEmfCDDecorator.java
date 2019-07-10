package de.monticore.codegen.cd2java._ast_emf;

import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java._ast.ASTCDDecorator;
import de.monticore.codegen.cd2java._ast.ast_class.ASTConstants;
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
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.umlcd4a.symboltable.CD4AnalysisSymbolTableCreator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.PACKAGE;
import static de.monticore.codegen.cd2java.CoreTemplates.createPackageHookPoint;

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

  @Override
  public ASTCDCompilationUnit decorate(final ASTCDCompilationUnit ast) {
    List<String> astPackage = new ArrayList<>(ast.getPackageList());
    astPackage.addAll(Arrays.asList(ast.getCDDefinition().getName().toLowerCase(), ASTConstants.AST_PACKAGE));

    ASTCDDefinition astCD = CD4AnalysisMill.cDDefinitionBuilder()
        .setName(ast.getCDDefinition().getName())
        .addCDClass(createPackageImpl(ast))
        .addCDInterface(createPackageInterface(ast))
        .addAllCDClasss(createASTClasses(ast))
        .addAllCDClasss(createASTBuilderClasses(ast))
        .addCDClass(createNodeFactoryClass(ast))
        .addCDClass(createMillClass(ast))
        .addCDClass(createASTConstantsClass(ast))
        .addAllCDInterfaces(createASTInterfaces(ast))
        .addCDInterface(createLanguageInterface(ast))
        .addAllCDEnums(createEnums(ast))
        .build();

    for (ASTCDClass cdClass : astCD.getCDClassList()) {
      this.replaceTemplate(PACKAGE, cdClass, createPackageHookPoint(astPackage));
    }

    for (ASTCDInterface cdInterface : astCD.getCDInterfaceList()) {
      this.replaceTemplate(CoreTemplates.PACKAGE, cdInterface, createPackageHookPoint(astPackage));
    }

    for (ASTCDEnum cdEnum : astCD.getCDEnumList()) {
      this.replaceTemplate(CoreTemplates.PACKAGE, cdEnum, createPackageHookPoint(astPackage));
    }

    return CD4AnalysisMill.cDCompilationUnitBuilder()
        .setPackageList(astPackage)
        .setCDDefinition(astCD)
        .build();
  }

  protected ASTCDInterface createPackageInterface(ASTCDCompilationUnit compilationUnit) {
    return packageInterfaceDecorator.decorate(compilationUnit);
  }

  protected ASTCDClass createPackageImpl(ASTCDCompilationUnit compilationUnit) {
    return packageImplDecorator.decorate(compilationUnit);
  }
}
