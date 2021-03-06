/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._od;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.mcbasictypes._ast.ASTMCPackageDeclaration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.createPackageHookPoint;
import static de.monticore.codegen.cd2java._od.ODConstants.OD_PACKAGE;

public class ODCDDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDCompilationUnit> {

  protected final ODDecorator odDecorator;

  public ODCDDecorator(GlobalExtensionManagement glex,
                       ODDecorator odDecorator) {
    super(glex);
    this.odDecorator = odDecorator;
  }

  @Override
  public ASTCDCompilationUnit decorate(ASTCDCompilationUnit input) {
    List<String> cocoPackage = new ArrayList<>(input.getCDPackageList());
    cocoPackage.addAll(Arrays.asList(input.getCDDefinition().getName().toLowerCase(), OD_PACKAGE));
    ASTMCPackageDeclaration packageDecl = CD4CodeMill.mCPackageDeclarationBuilder().setMCQualifiedName(
            CD4CodeMill.mCQualifiedNameBuilder().setPartsList(cocoPackage).build()).build();

    ASTCDClass odClass = odDecorator.decorate(input);
    this.replaceTemplate(CoreTemplates.PACKAGE, odClass, createPackageHookPoint(cocoPackage));

    ASTCDDefinition odCDDefinition = CD4AnalysisMill.cDDefinitionBuilder()
        .setModifier(CD4AnalysisMill.modifierBuilder().build())
        .setName(input.getCDDefinition().getName())
        .addCDElement(odClass)
        .build();

    return CD4AnalysisMill.cDCompilationUnitBuilder()
        .setMCPackageDeclaration(packageDecl)
        .setCDDefinition(odCDDefinition)
        .build();
  }
}
