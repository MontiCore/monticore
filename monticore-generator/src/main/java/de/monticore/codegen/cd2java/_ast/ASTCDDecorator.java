package de.monticore.codegen.cd2java._ast;

import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java._ast.ast_class.ASTConstants;
import de.monticore.codegen.cd2java._ast.ast_class.ASTFullDecorator;
import de.monticore.codegen.cd2java._ast.ast_interface.ASTLanguageInterfaceDecorator;
import de.monticore.codegen.cd2java._ast.ast_interface.FullASTInterfaceDecorator;
import de.monticore.codegen.cd2java._ast.builder.ASTBuilderDecorator;
import de.monticore.codegen.cd2java._ast.constants.ASTConstantsDecorator;
import de.monticore.codegen.cd2java._ast.enums.EnumDecorator;
import de.monticore.codegen.cd2java._ast.factory.NodeFactoryDecorator;
import de.monticore.codegen.cd2java._ast.mill.MillDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.umlcd4a.symboltable.CD4AnalysisSymbolTableCreator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.PACKAGE;
import static de.monticore.codegen.cd2java.CoreTemplates.createPackageHookPoint;

public class ASTCDDecorator extends AbstractDecorator<ASTCDCompilationUnit, ASTCDCompilationUnit> {

  protected final ASTFullDecorator astFullDecorator;

  protected final ASTLanguageInterfaceDecorator astLanguageInterfaceDecorator;

  protected final ASTBuilderDecorator astBuilderDecorator;

  protected final NodeFactoryDecorator nodeFactoryDecorator;

  protected final MillDecorator millDecorator;

  protected final ASTConstantsDecorator astConstantsDecorator;

  protected final EnumDecorator enumDecorator;

  protected final FullASTInterfaceDecorator astInterfaceDecorator;

  protected final CD4AnalysisSymbolTableCreator symbolTableCreator;

  public ASTCDDecorator(final GlobalExtensionManagement glex,
                        final CD4AnalysisSymbolTableCreator symbolTableCreator,
                        final ASTFullDecorator astFullDecorator,
                        final ASTLanguageInterfaceDecorator astLanguageInterfaceDecorator,
                        final ASTBuilderDecorator astBuilderDecorator,
                        final NodeFactoryDecorator nodeFactoryDecorator,
                        final MillDecorator millDecorator,
                        final ASTConstantsDecorator astConstantsDecorator,
                        final EnumDecorator enumDecorator,
                        final FullASTInterfaceDecorator astInterfaceDecorator) {
    super(glex);
    this.astFullDecorator = astFullDecorator;
    this.astLanguageInterfaceDecorator = astLanguageInterfaceDecorator;
    this.astBuilderDecorator = astBuilderDecorator;
    this.nodeFactoryDecorator = nodeFactoryDecorator;
    this.millDecorator = millDecorator;
    this.astConstantsDecorator = astConstantsDecorator;
    this.enumDecorator = enumDecorator;
    this.astInterfaceDecorator = astInterfaceDecorator;
    this.symbolTableCreator = symbolTableCreator;
  }

  @Override
  public ASTCDCompilationUnit decorate(final ASTCDCompilationUnit ast) {
    List<String> astPackage = new ArrayList<>(ast.getPackageList());
    astPackage.addAll(Arrays.asList(ast.getCDDefinition().getName().toLowerCase(), ASTConstants.AST_PACKAGE));

    ASTCDDefinition astCD = CD4AnalysisMill.cDDefinitionBuilder()
        .setName(ast.getCDDefinition().getName())
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

  protected List<ASTCDClass> createASTClasses(final ASTCDCompilationUnit ast) {
    ASTCDCompilationUnit copyedCD = ast.deepClone();
    symbolTableCreator.createFromAST(copyedCD);
    return copyedCD.getCDDefinition().getCDClassList().stream()
        .map(astFullDecorator::decorate)
        .collect(Collectors.toList());
  }

  protected ASTCDInterface createLanguageInterface(final ASTCDCompilationUnit ast) {
    return astLanguageInterfaceDecorator.decorate(ast);
  }

  protected List<ASTCDClass> createASTBuilderClasses(final ASTCDCompilationUnit ast) {
    return ast.getCDDefinition().getCDClassList().stream()
        .map(astBuilderDecorator::decorate)
        .collect(Collectors.toList());
  }

  protected ASTCDClass createNodeFactoryClass(final ASTCDCompilationUnit ast) {
    return nodeFactoryDecorator.decorate(ast);
  }

  protected ASTCDClass createMillClass(final ASTCDCompilationUnit ast) {
    return millDecorator.decorate(ast);
  }

  protected ASTCDClass createASTConstantsClass(final ASTCDCompilationUnit ast) {
    return astConstantsDecorator.decorate(ast);
  }

  protected List<ASTCDInterface> createASTInterfaces(final ASTCDCompilationUnit ast) {
    return ast.getCDDefinition().getCDInterfaceList().stream()
        .map(astInterfaceDecorator::decorate)
        .collect(Collectors.toList());
  }

  protected List<ASTCDEnum> createEnums(final ASTCDCompilationUnit ast) {
    return ast.getCDDefinition().getCDEnumList().stream()
        .map(enumDecorator::decorate)
        .collect(Collectors.toList());
  }
}
