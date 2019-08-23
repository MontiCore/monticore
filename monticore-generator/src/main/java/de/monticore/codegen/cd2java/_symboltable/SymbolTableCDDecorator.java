package de.monticore.codegen.cd2java._symboltable;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java._symboltable.builder.SymbolBuilderDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.PACKAGE;
import static de.monticore.codegen.cd2java.CoreTemplates.createPackageHookPoint;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.SYMBOL_TABLE_PACKAGE;

public class SymbolTableCDDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDCompilationUnit> {

  protected final SymbolDecorator symbolDecorator;

  protected final SymbolBuilderDecorator symbolBuilderDecorator;

  protected final SymbolTableService symbolTableService;

  public SymbolTableCDDecorator(final GlobalExtensionManagement glex,
                                final SymbolTableService symbolTableService,
                                final SymbolDecorator symbolDecorator,
                                final SymbolBuilderDecorator symbolBuilderDecorator) {
    super(glex);
    this.symbolDecorator = symbolDecorator;
    this.symbolBuilderDecorator = symbolBuilderDecorator;
    this.symbolTableService = symbolTableService;
  }

  @Override
  public ASTCDCompilationUnit decorate(ASTCDCompilationUnit ast) {
    List<String> symbolTablePackage = new ArrayList<>(ast.getPackageList());
    symbolTablePackage.addAll(Arrays.asList(ast.getCDDefinition().getName().toLowerCase(), SYMBOL_TABLE_PACKAGE));

    List<ASTCDClass> symbolClasses = getSymbolClasses(ast.getCDDefinition().getCDClassList());
    List<ASTCDInterface> symbolInterfaces = getSymbolInterfaces(ast.getCDDefinition().getCDInterfaceList());
    List<ASTCDClass> scopeClasses = getScopeClasses(ast.getCDDefinition().getCDClassList());
    List<ASTCDInterface> scopeInterfaces = getScopeInterfaces(ast.getCDDefinition().getCDInterfaceList());

    List<ASTCDClass> decoratedSymbolClasses = createSymbolClasses(symbolClasses);
    List<ASTCDClass> decoratedSymbolInterfaces = createSymbolClasses(symbolInterfaces);

    ASTCDDefinition astCD = CD4AnalysisMill.cDDefinitionBuilder()
        .setName(ast.getCDDefinition().getName())
        .addAllCDClasss(decoratedSymbolClasses)
        .addAllCDClasss(createSymbolBuilderClasses(decoratedSymbolClasses))
        .addAllCDClasss(decoratedSymbolInterfaces)
        .addAllCDClasss(createSymbolBuilderClasses(decoratedSymbolInterfaces))
        .build();

    for (ASTCDClass cdClass : astCD.getCDClassList()) {
      this.replaceTemplate(PACKAGE, cdClass, createPackageHookPoint(symbolTablePackage));
    }

    for (ASTCDInterface cdInterface : astCD.getCDInterfaceList()) {
      this.replaceTemplate(CoreTemplates.PACKAGE, cdInterface, createPackageHookPoint(symbolTablePackage));
    }

    for (ASTCDEnum cdEnum : astCD.getCDEnumList()) {
      this.replaceTemplate(CoreTemplates.PACKAGE, cdEnum, createPackageHookPoint(symbolTablePackage));
    }

    return CD4AnalysisMill.cDCompilationUnitBuilder()
        .setPackageList(symbolTablePackage)
        .setCDDefinition(astCD)
        .build();
  }

  protected List<ASTCDClass> createSymbolClasses(List<?extends ASTCDType> astcdTypeList) {
    return astcdTypeList
        .stream()
        .map(symbolDecorator::decorate)
        .collect(Collectors.toList());
  }

  protected List<ASTCDClass> createSymbolBuilderClasses(List<ASTCDClass> symbolASTClasses) {
    return symbolASTClasses
        .stream()
        .map(symbolBuilderDecorator::decorate)
        .collect(Collectors.toList());
  }

  protected List<ASTCDClass> getSymbolClasses(List<ASTCDClass> classList) {
    return classList.stream()
        .filter(ASTCDClassTOP::isPresentModifier)
        .filter(c -> symbolTableService.hasSymbolStereotype(c.getModifier()))
        .collect(Collectors.toList());
  }

  protected List<ASTCDInterface> getSymbolInterfaces(List<ASTCDInterface> astcdInterfaces) {
    return astcdInterfaces.stream()
        .filter(ASTCDInterface::isPresentModifier)
        .filter(c -> symbolTableService.hasSymbolStereotype(c.getModifier()))
        .collect(Collectors.toList());
  }

  protected List<ASTCDClass> getScopeClasses(List<ASTCDClass> classList) {
    return classList.stream()
        .filter(ASTCDClassTOP::isPresentModifier)
        .filter(c -> symbolTableService.hasScopeStereotype(c.getModifier()))
        .collect(Collectors.toList());
  }

  protected List<ASTCDInterface> getScopeInterfaces(List<ASTCDInterface> astcdInterfaces) {
    return astcdInterfaces.stream()
        .filter(ASTCDInterface::isPresentModifier)
        .filter(c -> symbolTableService.hasScopeStereotype(c.getModifier()))
        .collect(Collectors.toList());
  }
}
