/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.serialization;

import de.monticore.cd.cd4analysis.CD4AnalysisMill;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4code.CD4CodeMill;
import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.PACKAGE;
import static de.monticore.codegen.cd2java.CoreTemplates.createPackageHookPoint;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.SERIALIZATION_PACKAGE;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.SYMBOL_TABLE_PACKAGE;

/**
 * creates all sclasses for the _symboltable._serialization package
 * is own decorator because of the different package
 */
public class SerializationCDDecorator extends AbstractDecorator {

  protected final SymbolTableService symbolTableService;

  protected final SymbolDeSerDecorator symbolDeSerDecorator;

  protected final ScopeDeSerDecorator scopeDeSerDecorator;

  protected final SymbolTablePrinterDecorator symbolTablePrinterDecorator;

  public SerializationCDDecorator(final GlobalExtensionManagement glex,
                                  final SymbolTableService symbolTableService,
                                  final SymbolDeSerDecorator symbolDeSerDecorator,
                                  final ScopeDeSerDecorator scopeDeSerDecorator,
                                  final SymbolTablePrinterDecorator symbolTablePrinterDecorator) {
    super(glex);
    this.symbolDeSerDecorator = symbolDeSerDecorator;
    this.scopeDeSerDecorator = scopeDeSerDecorator;
    this.symbolTableService = symbolTableService;
    this.symbolTablePrinterDecorator = symbolTablePrinterDecorator;
  }

  public ASTCDCompilationUnit decorate(ASTCDCompilationUnit astCD, ASTCDCompilationUnit symbolInput, ASTCDCompilationUnit scopeInput) {

    List<String> symbolTablePackage = new ArrayList<>(symbolInput.getPackageList());
    symbolTablePackage.addAll(Arrays.asList(symbolInput.getCDDefinition().getName().toLowerCase(), SYMBOL_TABLE_PACKAGE, SERIALIZATION_PACKAGE));

    ASTCDDefinition serializeCD = CD4CodeMill.cDDefinitionBuilder()
        .setName(symbolInput.getCDDefinition().getName())
        .addAllCDClasss(createSymbolDeSerClasses(symbolInput.getCDDefinition().getCDClassList()))
        .addCDClass(createSymbolTablePrinterClass(scopeInput, symbolInput))
        .build();

    if (symbolTableService.hasStartProd(astCD.getCDDefinition())) {
      serializeCD.addCDClass(createScopeDeSerClass(scopeInput, symbolInput));
    }

    // change to _serialization package
    for (ASTCDClass cdClass : serializeCD.getCDClassList()) {
      this.replaceTemplate(PACKAGE, cdClass, createPackageHookPoint(symbolTablePackage));
    }

    return CD4AnalysisMill.cDCompilationUnitBuilder()
        .setPackageList(symbolTablePackage)
        .setCDDefinition(serializeCD)
        .build();
  }

  protected List<ASTCDClass> createSymbolDeSerClasses(List<ASTCDClass> symbolClassList) {
    return symbolClassList
        .stream()
        .map(symbolDeSerDecorator::decorate)
        .collect(Collectors.toList());
  }

  protected ASTCDClass createScopeDeSerClass(ASTCDCompilationUnit scopeCD, ASTCDCompilationUnit symbolCd) {
    return scopeDeSerDecorator.decorate(scopeCD, symbolCd);
  }

  protected ASTCDClass createSymbolTablePrinterClass(ASTCDCompilationUnit scopeCD, ASTCDCompilationUnit symbolCd) {
    return symbolTablePrinterDecorator.decorate(scopeCD, symbolCd);
  }
}
