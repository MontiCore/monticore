/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.mill;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis.CD4CodeBasisMill;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.mcbasictypes._ast.ASTMCPackageDeclaration;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.*;
import static de.monticore.codegen.cd2java.CoreTemplates.createAnnotationsHookPoint;
import static de.monticore.codegen.cd2java.mill.MillConstants.AUXILIARY_PACKAGE;

public class CDAuxiliaryDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDCompilationUnit> {

  protected final MillForSuperDecorator millForSuperDecorator;

  public CDAuxiliaryDecorator(final GlobalExtensionManagement glex,
                              final MillForSuperDecorator millForSuperDecorator){
    super(glex);
    this.millForSuperDecorator = millForSuperDecorator;
  }

  @Override
  public ASTCDCompilationUnit decorate(ASTCDCompilationUnit input) {
    List<ASTCDClass> millForSuperClasses = millForSuperDecorator.decorate(input);

    //remove _ast package and add _auxiliary package
    List<String> packageName = new ArrayList<>(input.getCDPackageList());
    packageName.remove(packageName.size() - 1);
    packageName.add(AUXILIARY_PACKAGE);
    ASTMCPackageDeclaration packageDecl = CD4CodeMill.mCPackageDeclarationBuilder().setMCQualifiedName(
            CD4CodeMill.mCQualifiedNameBuilder().setPartsList(packageName).build()).build();

    ASTCDDefinition astCD = CD4AnalysisMill.cDDefinitionBuilder()
        .setName(input.getCDDefinition().getName())
        .addAllCDElements(millForSuperClasses)
        .build();

    for (ASTCDClass cdClass : astCD.getCDClassesList()) {
      this.replaceTemplate(PACKAGE, cdClass, createPackageHookPoint(packageName));
      this.replaceTemplate(ANNOTATIONS, cdClass, createAnnotationsHookPoint(cdClass.getModifier()));
    }

    return CD4AnalysisMill.cDCompilationUnitBuilder()
        .setMCPackageDeclaration(packageDecl)
        .setCDDefinition(astCD)
        .build();
  }
}
