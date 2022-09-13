/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._cocos;

import com.google.common.collect.Lists;
import de.monticore.cd.codegen.CD2JavaTemplates;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.MCPath;
import de.monticore.types.mcbasictypes._ast.ASTMCPackageDeclaration;

import java.util.Arrays;
import java.util.List;

import static de.monticore.cd.codegen.CD2JavaTemplates.ANNOTATIONS;
import static de.monticore.codegen.cd2java.CoreTemplates.createAnnotationsHookPoint;
import static de.monticore.codegen.cd2java.CoreTemplates.createPackageHookPoint;

/**
 * combines the CoCo checker class and all CoCo interfaces to create the _coco package
 */
public class CoCoDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDCompilationUnit> {

  protected final CoCoCheckerDecorator cocoCheckerDecorator;

  protected final CoCoInterfaceDecorator cocoInterfaceDecorator;

  protected final MCPath handCodedPath;

  public CoCoDecorator(final GlobalExtensionManagement glex,
                       final MCPath handCodedPath,
                       final CoCoCheckerDecorator cocoCheckerDecorator,
                       final CoCoInterfaceDecorator cocoInterfaceDecorator) {
    super(glex);
    this.cocoCheckerDecorator = cocoCheckerDecorator;
    this.cocoInterfaceDecorator = cocoInterfaceDecorator;
    this.handCodedPath = handCodedPath;
  }

  @Override
  public ASTCDCompilationUnit decorate(ASTCDCompilationUnit input) {
    List<String> cocoPackage = Lists.newArrayList();
    input.getMCPackageDeclaration().getMCQualifiedName().getPartsList().forEach(p -> cocoPackage.add(p.toLowerCase()));
    cocoPackage.addAll(Arrays.asList(input.getCDDefinition().getName().toLowerCase(), CoCoConstants.COCO_PACKAGE));

    ASTCDDefinition cocoCD = CD4AnalysisMill.cDDefinitionBuilder()
        .setModifier(CD4AnalysisMill.modifierBuilder().build())
        .setName(input.getCDDefinition().getName())
        .addCDElement(cocoCheckerDecorator.decorate(input))
        .addAllCDElements(cocoInterfaceDecorator.decorate(input.getCDDefinition()))
        .build();

    // change the package to _coco
    for (ASTCDClass ast : cocoCD.getCDClassesList()) {
      this.replaceTemplate(CD2JavaTemplates.PACKAGE, ast, createPackageHookPoint(cocoPackage));
      this.replaceTemplate(ANNOTATIONS, ast, createAnnotationsHookPoint(ast.getModifier()));
    }

    for (ASTCDInterface ast : cocoCD.getCDInterfacesList()) {
      this.replaceTemplate(CD2JavaTemplates.PACKAGE, ast, createPackageHookPoint(cocoPackage));
      this.replaceTemplate(ANNOTATIONS, ast, createAnnotationsHookPoint(ast.getModifier()));
    }
    
    ASTMCPackageDeclaration mCPackageDeclaration = CD4AnalysisMill.mCPackageDeclarationBuilder().setMCQualifiedName(
        CD4AnalysisMill.mCQualifiedNameBuilder()
        .setPartsList(cocoPackage)
        .build())
        .build();

    return CD4AnalysisMill.cDCompilationUnitBuilder()
        .setMCPackageDeclaration(mCPackageDeclaration)
        .setCDDefinition(cocoCD)
        .build();
  }
}
