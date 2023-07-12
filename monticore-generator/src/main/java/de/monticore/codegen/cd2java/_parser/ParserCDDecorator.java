/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._parser;

import com.google.common.collect.Lists;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.mcbasictypes._ast.ASTMCPackageDeclaration;
import de.se_rwth.commons.Joiners;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static de.monticore.cd.codegen.CD2JavaTemplates.ANNOTATIONS;
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
    List<String> packageName = Lists.newArrayList();
    astCD.getCDPackageList().forEach(p -> packageName.add(p.toLowerCase()));
    packageName.addAll(Arrays.asList(astCD.getCDDefinition().getName().toLowerCase(), PARSER_PACKAGE));
    ASTMCPackageDeclaration packageDecl = CD4CodeMill.mCPackageDeclarationBuilder().setMCQualifiedName(
            CD4CodeMill.mCQualifiedNameBuilder().setPartsList(packageName).build()).build();

    ASTCDDefinition parserCD = CD4AnalysisMill.cDDefinitionBuilder()
            .setName(astCD.getCDDefinition().getName())
            .setModifier(CD4CodeMill.modifierBuilder().build())
            .build();
    ASTCDPackage parserPackage = parserCD.getOrCreatePackage(Joiners.DOT.join(packageName));

    createParserClass(astCD).ifPresent(parserPackage::addCDElement);
    List<ASTCDClass> parserForSuperClasses = createParserForSuperClasses(astCD);
    parserPackage.addAllCDElements(parserForSuperClasses);

    addAnnotation(parserCD);

    return CD4AnalysisMill.cDCompilationUnitBuilder()
            .setMCPackageDeclaration(packageDecl)
            .setCDDefinition(parserCD)
            .build();
  }

  protected Optional<ASTCDClass> createParserClass(ASTCDCompilationUnit astCD){
    return parserClassDecorator.decorate(astCD);
  }

  protected List<ASTCDClass> createParserForSuperClasses(ASTCDCompilationUnit astCD){
    return parserForSuperDecorator.decorate(astCD);
  }

  protected void addAnnotation(ASTCDDefinition parserCD) {
    for (ASTCDClass cdClass : parserCD.getCDClassesList()) {
      this.replaceTemplate(ANNOTATIONS, cdClass, decorationHelper.createAnnotationsHookPoint(cdClass.getModifier()));
    }

    for (ASTCDInterface cdInterface : parserCD.getCDInterfacesList()) {
      this.replaceTemplate(ANNOTATIONS, cdInterface, decorationHelper.createAnnotationsHookPoint(cdInterface.getModifier()));
    }

    for (ASTCDEnum cdEnum : parserCD.getCDEnumsList()) {
      this.replaceTemplate(ANNOTATIONS, cdEnum, decorationHelper.createAnnotationsHookPoint(cdEnum.getModifier()));
    }
  }

}
