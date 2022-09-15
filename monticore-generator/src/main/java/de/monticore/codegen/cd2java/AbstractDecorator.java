/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java;

import de.monticore.ast.ASTNode;
import de.monticore.cd.facade.CDAttributeFacade;
import de.monticore.cd.facade.CDConstructorFacade;
import de.monticore.cd.facade.CDMethodFacade;
import de.monticore.cd.facade.CDParameterFacade;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.types.MCTypeFacade;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;

import java.util.Arrays;
import java.util.Optional;

public abstract class AbstractDecorator {

  /**
   * Do not use for creation of new Decorators
   * Decide if your new Decorator is a Creator or a Transformer, to overwrite the correct decorate method
   * Only a class to sum up general Decorator functionality
   **/

  public static final String DEFAULT_PACKAGE = "";

  protected final GlobalExtensionManagement glex;

  protected boolean templatesEnabled;

  protected final MCTypeFacade mcTypeFacade;

  protected final CDAttributeFacade cdAttributeFacade;

  protected final CDConstructorFacade cdConstructorFacade;

  protected final CDMethodFacade cdMethodFacade;

  protected final CDParameterFacade cdParameterFacade;

  protected final DecorationHelper decorationHelper;

  public AbstractDecorator() {
    this(null);
  }

  public AbstractDecorator(final GlobalExtensionManagement glex) {
    this(glex,
        MCTypeFacade.getInstance(),
        CDAttributeFacade.getInstance(),
        CDConstructorFacade.getInstance(),
        CDMethodFacade.getInstance(),
        CDParameterFacade.getInstance(),
        DecorationHelper.getInstance()
    );
  }

  public AbstractDecorator(final GlobalExtensionManagement glex,
                           final MCTypeFacade mcTypeFacade,
                           final CDAttributeFacade cdAttributeFacade,
                           final CDConstructorFacade cdConstructorFacade,
                           final CDMethodFacade cdMethodFacade,
                           final CDParameterFacade cdParameterFacade,
                           final DecorationHelper decorationHelper) {
    this.glex = glex;
    this.templatesEnabled = true;
    this.mcTypeFacade = mcTypeFacade;
    this.cdAttributeFacade = cdAttributeFacade;
    this.cdConstructorFacade = cdConstructorFacade;
    this.cdMethodFacade = cdMethodFacade;
    this.cdParameterFacade = cdParameterFacade;
    this.decorationHelper = decorationHelper;
  }

  public void enableTemplates() {
    this.templatesEnabled = true;
  }

  public void disableTemplates() {
    this.templatesEnabled = false;
  }

  protected boolean templatesEnabled() {
    return this.templatesEnabled;
  }

  protected void replaceTemplate(String template, ASTNode node, HookPoint hookPoint) {
    if (this.templatesEnabled()) {
      this.glex.replaceTemplate(template, node, hookPoint);
    }
  }

  protected MCTypeFacade getMCTypeFacade() {
    return this.mcTypeFacade;
  }

  protected CDAttributeFacade getCDAttributeFacade() {
    return this.cdAttributeFacade;
  }

  protected CDConstructorFacade getCDConstructorFacade() {
    return this.cdConstructorFacade;
  }

  protected CDMethodFacade getCDMethodFacade() {
    return this.cdMethodFacade;
  }

  protected CDParameterFacade getCDParameterFacade() {
    return this.cdParameterFacade;
  }

  public DecorationHelper getDecorationHelper() {
    return decorationHelper;
  }

  protected ASTCDPackage getPackage(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD, String packageName) {
    String completeName = getPackageName(origCD, packageName);
    Optional<ASTCDPackage> astCDPackage = decoratedCD.getCDDefinition().getCDPackagesList().stream()
            .filter(p -> p.getName().equals(completeName)).findFirst();
    if (astCDPackage.isPresent()) {
      return astCDPackage.get();
    } else {
      ASTCDPackage p = CD4CodeMill.cDPackageBuilder().setMCQualifiedName(
              CD4CodeMill.mCQualifiedNameBuilder().setPartsList(Arrays.asList(completeName.split("\\."))).build()).build();
      decoratedCD.getCDDefinition().addCDPackage(0, p);
      return p;
    }
  }

  protected String getPackageName(ASTCDCompilationUnit origCD, String subPackage) {
    String origPackage = Names.constructQualifiedName(origCD.getCDPackageList(), origCD.getCDDefinition().getName());
    return (subPackage.isEmpty()?origPackage: Joiners.DOT.join(origPackage, subPackage)).toLowerCase();
  }

}
