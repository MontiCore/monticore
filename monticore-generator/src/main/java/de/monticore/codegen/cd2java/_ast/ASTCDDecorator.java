/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._ast.ast_class.ASTFullDecorator;
import de.monticore.codegen.cd2java._ast.ast_interface.ASTLanguageInterfaceDecorator;
import de.monticore.codegen.cd2java._ast.ast_interface.FullASTInterfaceDecorator;
import de.monticore.codegen.cd2java._ast.builder.ASTBuilderDecorator;
import de.monticore.codegen.cd2java._ast.constants.ASTConstantsDecorator;
import de.monticore.codegen.cd2java._ast.enums.EnumDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.codegen.CD2JavaTemplates.ANNOTATIONS;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_PACKAGE;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_PREFIX;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.NODE_SUFFIX;

/**
 * combines all decorators to create all classes, interfaces and enums for the _ast package
 * returns them all combined in ASTCDCompilationUnit
 */
public class ASTCDDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDCompilationUnit> {

  protected final ASTFullDecorator astFullDecorator;

  protected final ASTLanguageInterfaceDecorator astLanguageInterfaceDecorator;

  protected final ASTBuilderDecorator astBuilderDecorator;

  protected final ASTConstantsDecorator astConstantsDecorator;

  protected final EnumDecorator enumDecorator;

  protected final FullASTInterfaceDecorator astInterfaceDecorator;

  public ASTCDDecorator(final GlobalExtensionManagement glex,
                        final ASTFullDecorator astFullDecorator,
                        final ASTLanguageInterfaceDecorator astLanguageInterfaceDecorator,
                        final ASTBuilderDecorator astBuilderDecorator,
                        final ASTConstantsDecorator astConstantsDecorator,
                        final EnumDecorator enumDecorator,
                        final FullASTInterfaceDecorator astInterfaceDecorator) {
    super(glex);
    this.astFullDecorator = astFullDecorator;
    this.astLanguageInterfaceDecorator = astLanguageInterfaceDecorator;
    this.astBuilderDecorator = astBuilderDecorator;
    this.astConstantsDecorator = astConstantsDecorator;
    this.enumDecorator = enumDecorator;
    this.astInterfaceDecorator = astInterfaceDecorator;
  }

  @Override
  public ASTCDCompilationUnit decorate(final ASTCDCompilationUnit ast) {

    ASTCDDefinition astCD = CD4AnalysisMill.cDDefinitionBuilder()
        .setName(ast.getCDDefinition().getName())
        .setModifier(CD4CodeMill.modifierBuilder().build()).build();
    ASTCDCompilationUnit compUnit = CD4AnalysisMill.cDCompilationUnitBuilder()
            .setMCPackageDeclaration(ast.getMCPackageDeclaration().deepClone())
            .setCDDefinition(astCD)
            .build();
    ASTCDPackage astPackage = getPackage(ast, compUnit, AST_PACKAGE);
    astPackage.addAllCDElements(createASTClasses(ast));
    astPackage.addAllCDElements(createASTBuilderClasses(ast));
    astPackage.addCDElement(createASTConstantsClass(ast));
    astPackage.addAllCDElements(createASTInterfaces(ast));
    astPackage.addCDElement(createLanguageInterface(ast));
    astPackage.addAllCDElements(createEnums(ast));

    //  add deprecated annotations to all classes, interfaces, enums
    for (ASTCDClass cdClass : astCD.getCDClassesList()) {
      this.replaceTemplate(ANNOTATIONS, cdClass, decorationHelper.createAnnotationsHookPoint(cdClass.getModifier()));
    }

    for (ASTCDInterface cdInterface : astCD.getCDInterfacesList()) {
      this.replaceTemplate(ANNOTATIONS, cdInterface, decorationHelper.createAnnotationsHookPoint(cdInterface.getModifier()));
    }

    for (ASTCDEnum cdEnum : astCD.getCDEnumsList()) {
      this.replaceTemplate(ANNOTATIONS, cdEnum, decorationHelper.createAnnotationsHookPoint(cdEnum.getModifier()));
    }
    return compUnit;
  }

  protected List<ASTCDClass> createASTClasses(final ASTCDCompilationUnit ast) {
    List<ASTCDClass> astcdClassList = new ArrayList<>();
    for (ASTCDClass astcdClass : ast.getCDDefinition().getCDClassesList()) {
      ASTCDClass changedClass = CD4CodeMill.cDClassBuilder()
          .setName(astcdClass.getName())
          .setModifier(astcdClass.getModifier().deepClone())
          .build();
      astFullDecorator.decorate(astcdClass, changedClass);
      astcdClassList.add(changedClass);
    }
    return astcdClassList;
  }


  protected ASTCDInterface createLanguageInterface(final ASTCDCompilationUnit ast) {
    return astLanguageInterfaceDecorator.decorate(ast);
  }

  protected List<ASTCDClass> createASTBuilderClasses(final ASTCDCompilationUnit ast) {
    return ast.getCDDefinition().getCDClassesList().stream()
        .map(astBuilderDecorator::decorate)
        .collect(Collectors.toList());
  }

  protected ASTCDClass createASTConstantsClass(final ASTCDCompilationUnit ast) {
    return astConstantsDecorator.decorate(ast);
  }

  protected List<ASTCDInterface> createASTInterfaces(final ASTCDCompilationUnit ast) {
    List<ASTCDInterface> astcdInterfaceList = new ArrayList<>();
    for (ASTCDInterface astcdInterface : ast.getCDDefinition().getCDInterfacesList()) {
      // do not create normal ast interface for language interface, is seperately created by ASTLanguageInterfaceDecorator
      if (!astcdInterface.getName().equals(AST_PREFIX + ast.getCDDefinition().getName() + NODE_SUFFIX)) {
        ASTCDInterface changedInterface = CD4AnalysisMill.cDInterfaceBuilder().setName(astcdInterface.getName())
            .setModifier(astcdInterface.getModifier().deepClone())
            .build();
        ASTCDInterface decoratedASTClass = astInterfaceDecorator.decorate(astcdInterface, changedInterface);
        astcdInterfaceList.add(decoratedASTClass);
      }
    }
    return astcdInterfaceList;
  }

  protected List<ASTCDEnum> createEnums(final ASTCDCompilationUnit ast) {
    return ast.getCDDefinition().getCDEnumsList().stream()
        .map(enumDecorator::decorate)
        .collect(Collectors.toList());
  }
}
