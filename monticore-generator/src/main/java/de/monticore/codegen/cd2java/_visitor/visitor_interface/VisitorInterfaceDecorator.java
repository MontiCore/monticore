package de.monticore.codegen.cd2java._visitor.visitor_interface;

import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.*;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

public class VisitorInterfaceDecorator extends AbstractDecorator<ASTCDCompilationUnit, ASTCDInterface> {

  private final VisitorService visitorService;

  public VisitorInterfaceDecorator(final GlobalExtensionManagement glex, VisitorService visitorService) {
    super(glex);
    this.visitorService = visitorService;
  }

  @Override
  public ASTCDInterface decorate(final ASTCDCompilationUnit compilationUnit) {
    ASTCDDefinition astcdDefinition = compilationUnit.getCDDefinition();
    ASTType visitorType = this.visitorService.getVisitorType();
    return CD4AnalysisMill.cDInterfaceBuilder()
        .setName(this.visitorService.getVisitorSimpleTypeName())
        .addAllInterfaces(this.visitorService.getAllVisitorTypesInHierarchy())
        .setModifier(PUBLIC.build())
        .addCDMethod(addGetRealThisMethods(visitorType))
        .addCDMethod(addSetRealThisMethods(visitorType))
        .addAllCDMethods(addClassVisitorMethods(astcdDefinition.getCDClassList()))
        .addAllCDMethods(addInterfaceVisitorMethods(astcdDefinition.getCDInterfaceList()))
        .addAllCDMethods(addEnumVisitorMethods(astcdDefinition.getCDEnumList()))
        .build();
  }

  protected ASTCDMethod addGetRealThisMethods(ASTType visitorType) {
    ASTCDMethod getRealThisMethod = this.getCDMethodFacade().createMethod(PUBLIC, visitorType, GET_REAL_THIS);
    this.replaceTemplate(EMPTY_BODY, getRealThisMethod, new StringHookPoint("return this;"));
    return getRealThisMethod;
  }

  protected ASTCDMethod addSetRealThisMethods(ASTType visitorType) {
    ASTCDParameter visitorParameter = getCDParameterFacade().createParameter(visitorType, "realThis");
    ASTCDMethod getRealThisMethod = this.getCDMethodFacade().createMethod(PUBLIC, SET_REAL_THIS, visitorParameter);
    this.replaceTemplate(EMPTY_BODY, getRealThisMethod, new StringHookPoint("    throw new UnsupportedOperationException(\"0xA7011x709 The setter for realThis is not implemented. You might want to implement a wrapper class to allow setting/getting realThis.\");\n"));
    return getRealThisMethod;
  }

  protected List<ASTCDMethod> addClassVisitorMethods(List<ASTCDClass> astcdClassList) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    for (ASTCDClass astcdClass : astcdClassList) {
      boolean doTraverse = !(astcdClass.isPresentModifier() && astcdClass.getModifier().isAbstract());
      ASTType classType = getCDTypeFacade().createTypeByDefinition(astcdClass.getName());
      visitorMethods.add(addVisitMethod(classType));
      visitorMethods.add(addEndVisitMethod(classType));
      visitorMethods.add(addTraversMethod(classType, astcdClass));
      visitorMethods.add(addHandleMethod(classType, doTraverse));
    }
    return visitorMethods;
  }

  protected List<ASTCDMethod> addEnumVisitorMethods(List<ASTCDEnum> astcdEnumList) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    for (ASTCDEnum astcdEnum : astcdEnumList) {
      ASTType enumType = getCDTypeFacade().createTypeByDefinition(astcdEnum.getName());
      visitorMethods.add(addVisitMethod(enumType));
      visitorMethods.add(addEndVisitMethod(enumType));
      visitorMethods.add(addHandleMethod(enumType, false));
    }
    return visitorMethods;
  }

  protected List<ASTCDMethod> addInterfaceVisitorMethods(List<ASTCDInterface> astcdInterfaceList) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    for (ASTCDInterface astcdInterface : astcdInterfaceList) {
      ASTType interfaceType = getCDTypeFacade().createTypeByDefinition(astcdInterface.getName());
      visitorMethods.add(addVisitMethod(interfaceType));
      visitorMethods.add(addEndVisitMethod(interfaceType));
      visitorMethods.add(addHandleMethod(interfaceType, false));
    }
    return visitorMethods;
  }

  protected ASTCDMethod addVisitMethod(ASTType astType) {
    return visitorService.getVisitorMethod(VISIT, astType);
  }

  protected ASTCDMethod addEndVisitMethod(ASTType astType) {
    return visitorService.getVisitorMethod(END_VISIT, astType);
  }

  protected ASTCDMethod addHandleMethod(ASTType astType, boolean traverse) {
    ASTCDMethod handleMethod = visitorService.getVisitorMethod(HANDLE, astType);
    this.replaceTemplate(EMPTY_BODY, handleMethod, new TemplateHookPoint("_visitor.Handle", traverse));
    return handleMethod;
  }

  protected ASTCDMethod addTraversMethod(ASTType astType, ASTCDClass astcdClass) {
    ASTCDMethod traverseMethod = visitorService.getVisitorMethod(TRAVERSE, astType);
    this.replaceTemplate(EMPTY_BODY, traverseMethod, new TemplateHookPoint("_visitor.Traverse", astcdClass));
    return traverseMethod;
  }
}
