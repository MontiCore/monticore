/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast;

import com.google.common.collect.Lists;
import de.monticore.cd.codegen.CD2JavaTemplates;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._ast.ast_class.ASTConstants;
import de.monticore.codegen.cd2java._ast.ast_class.ASTFullDecorator;
import de.monticore.codegen.cd2java._ast.ast_interface.ASTLanguageInterfaceDecorator;
import de.monticore.codegen.cd2java._ast.ast_interface.FullASTInterfaceDecorator;
import de.monticore.codegen.cd2java._ast.builder.ASTBuilderDecorator;
import de.monticore.codegen.cd2java._ast.constants.ASTConstantsDecorator;
import de.monticore.codegen.cd2java._ast.enums.EnumDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.mcbasictypes._ast.ASTMCPackageDeclaration;
import de.se_rwth.commons.Joiners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.codegen.CD2JavaTemplates.ANNOTATIONS;
import static de.monticore.cd.codegen.CD2JavaTemplates.PACKAGE;
import static de.monticore.codegen.cd2java.CoreTemplates.createAnnotationsHookPoint;
import static de.monticore.codegen.cd2java.CoreTemplates.createPackageHookPoint;
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
    List<String> astPackage = Lists.newArrayList();
    ast.getMCPackageDeclaration().getMCQualifiedName().getPartsList().forEach(p -> astPackage.add(p.toLowerCase()));
    astPackage.addAll(Arrays.asList(ast.getCDDefinition().getName().toLowerCase(), ASTConstants.AST_PACKAGE));
    ASTMCPackageDeclaration packageDecl = CD4CodeMill.mCPackageDeclarationBuilder().setMCQualifiedName(
            CD4CodeMill.mCQualifiedNameBuilder().setPartsList(astPackage).build()).build();

    ASTCDDefinition astCD = CD4AnalysisMill.cDDefinitionBuilder()
        .setName(ast.getCDDefinition().getName())
        .setModifier(CD4CodeMill.modifierBuilder().build()).build();
    astCD.setDefaultPackageName(Joiners.DOT.join(astPackage));

    astCD.addAllCDElements(createASTClasses(ast));
    astCD.addAllCDElements(createASTBuilderClasses(ast));
    astCD.addCDElement(createASTConstantsClass(ast));
    astCD.addAllCDElements(createASTInterfaces(ast));
    astCD.addCDElement(createLanguageInterface(ast));
    astCD.addAllCDElements(createEnums(ast));

    // change the package and add deprecated annotations to all classes, interfaces, enums
    for (ASTCDClass cdClass : astCD.getCDClassesList()) {
      this.replaceTemplate(PACKAGE, cdClass, createPackageHookPoint(astPackage));
      this.replaceTemplate(ANNOTATIONS, cdClass, createAnnotationsHookPoint(cdClass.getModifier()));
    }

    for (ASTCDInterface cdInterface : astCD.getCDInterfacesList()) {
      this.replaceTemplate(CD2JavaTemplates.PACKAGE, cdInterface, createPackageHookPoint(astPackage));
      this.replaceTemplate(ANNOTATIONS, cdInterface, createAnnotationsHookPoint(cdInterface.getModifier()));
    }

    for (ASTCDEnum cdEnum : astCD.getCDEnumsList()) {
      this.replaceTemplate(CD2JavaTemplates.PACKAGE, cdEnum, createPackageHookPoint(astPackage));
      this.replaceTemplate(ANNOTATIONS, cdEnum, createAnnotationsHookPoint(cdEnum.getModifier()));
    }

    return CD4AnalysisMill.cDCompilationUnitBuilder()
        .setMCPackageDeclaration(packageDecl)
        .setCDDefinition(astCD)
        .build();
  }

  protected List<ASTCDClass> createASTClasses(final ASTCDCompilationUnit ast) {
    List<ASTCDClass> astcdClassList = new ArrayList<>();
    for (ASTCDClass astcdClass : ast.getCDDefinition().getCDClassesList()) {
      ASTCDClass changedClass = CD4AnalysisMill.cDClassBuilder()
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
