/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._parser;

import com.google.common.collect.Lists;
import de.monticore.cd.cd4analysis.CD4AnalysisMill;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static de.monticore.codegen.cd2java.CoreTemplates.*;
import static de.monticore.codegen.cd2java.CoreTemplates.createAnnotationsHookPoint;
import static de.monticore.codegen.cd2java._parser.ParserConstants.PARSER_PACKAGE;

public class ParserCDDecorator extends AbstractDecorator {

  protected final ParserClassDecorator parserClassDecorator;

  protected final ParserForSuperDecorator parserForSuperDecorator;

  protected final ParserService service;

  public ParserCDDecorator(GlobalExtensionManagement glex,
                           ParserClassDecorator parserClassDecorator,
                           ParserForSuperDecorator parserForSuperDecorator,
                           ParserService service){
    super(glex);
    this.parserClassDecorator = parserClassDecorator;
    this.parserForSuperDecorator = parserForSuperDecorator;
    this.service = service;
  }

  public ASTCDCompilationUnit decorate(ASTCDCompilationUnit astCD){
    List<String> parserPackage = Lists.newArrayList();
    astCD.getPackageList().forEach(p -> parserPackage.add(p.toLowerCase()));
    parserPackage.addAll(Arrays.asList(astCD.getCDDefinition().getName().toLowerCase(), PARSER_PACKAGE));

    ASTCDDefinition parserCD = CD4AnalysisMill.cDDefinitionBuilder()
        .setName(astCD.getCDDefinition().getName())
        .build();

    createParserClass(astCD).ifPresent(parserCD::addCDClass);
    List<ASTCDClass> parserForSuperClasses = createParserForSuperClasses(astCD);
    parserCD.addAllCDClasss(parserForSuperClasses);

    addPackageAndAnnotation(parserCD, parserPackage);

    return CD4AnalysisMill.cDCompilationUnitBuilder()
        .setPackageList(parserPackage)
        .setCDDefinition(parserCD)
        .build();
  }

  protected Optional<ASTCDClass> createParserClass(ASTCDCompilationUnit astCD){
    return parserClassDecorator.decorate(astCD);
  }

  protected List<ASTCDClass> createParserForSuperClasses(ASTCDCompilationUnit astCD){
    return parserForSuperDecorator.decorate(astCD);
  }

  protected void addPackageAndAnnotation(ASTCDDefinition parserCD, List<String> parserPackage) {
    for (ASTCDClass cdClass : parserCD.getCDClassList()) {
      this.replaceTemplate(PACKAGE, cdClass, createPackageHookPoint(parserPackage));
      if (cdClass.isPresentModifier()) {
        this.replaceTemplate(ANNOTATIONS, cdClass, createAnnotationsHookPoint(cdClass.getModifier()));
      }
    }

    for (ASTCDInterface cdInterface : parserCD.getCDInterfaceList()) {
      this.replaceTemplate(CoreTemplates.PACKAGE, cdInterface, createPackageHookPoint(parserPackage));
      if (cdInterface.isPresentModifier()) {
        this.replaceTemplate(ANNOTATIONS, cdInterface, createAnnotationsHookPoint(cdInterface.getModifier()));
      }
    }

    for (ASTCDEnum cdEnum : parserCD.getCDEnumList()) {
      this.replaceTemplate(CoreTemplates.PACKAGE, cdEnum, createPackageHookPoint(parserPackage));
      if (cdEnum.isPresentModifier()) {
        this.replaceTemplate(ANNOTATIONS, cdEnum, createAnnotationsHookPoint(cdEnum.getModifier()));
      }
    }
  }

}
