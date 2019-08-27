/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._cocos;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.types.MCCollectionTypesHelper;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.CoreTemplates.VALUE;
import static de.monticore.codegen.cd2java._cocos.CoCoConstants.*;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.VISIT;
import static de.monticore.codegen.cd2java.factories.CDModifier.PRIVATE;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

public class CoCoCheckerDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDClass> {

  protected static final String REAL_THIS = "realThis";

  protected static final String COCOS = "CoCos";

  protected static final String NODE = "node";

  protected static final String COCO = "coco";

  protected static final String CHECKER = "checker";

  protected final MethodDecorator methodDecorator;

  protected final CoCoService cocoService;

  protected final VisitorService visitorService;

  public CoCoCheckerDecorator(final GlobalExtensionManagement glex, final MethodDecorator methodDecorator,
      final CoCoService cocoService,
      final VisitorService visitorService) {
    super(glex);
    this.methodDecorator = methodDecorator;
    this.cocoService = cocoService;
    this.visitorService = visitorService;

  }

  @Override
  public ASTCDClass decorate(ASTCDCompilationUnit compilationUnit) {
    ASTCDAttribute realThisAttribute = getCDAttributeFacade().createAttribute(PRIVATE, visitorService.getVisitorType(), REAL_THIS);
    this.replaceTemplate(VALUE, realThisAttribute, new StringHookPoint("= this"));
    List<ASTCDMethod> realThisMethods = methodDecorator.decorate(realThisAttribute);

    ASTCDConstructor constructor = this.getCDConstructorFacade().createConstructor(PUBLIC, cocoService.getCheckerSimpleTypeName());

    ASTCDClass cocoChecker = CD4AnalysisMill.cDClassBuilder()
        .setName(cocoService.getCheckerSimpleTypeName())
        .addInterface(visitorService.getVisitorReferenceType())
        .addCDAttribute(realThisAttribute)
        .addCDConstructor(constructor)
        .addAllCDMethods(realThisMethods)
        .build();

    CDDefinitionSymbol cdSymbol = cocoService.getCDSymbol();
    for (CDDefinitionSymbol currentCDSymbol : cocoService.getAllCDs()) {
      CoCoService cocoService = CoCoService.createCoCoService(currentCDSymbol);
      ASTService astService = ASTService.createASTService(currentCDSymbol);

      ASTMCType checkerType = cocoService.getCheckerType();
      String checkerName = MCCollectionTypesHelper.printType(checkerType).replaceAll("\\.", "_");
      boolean isCurrentDiagram = cdSymbol.getFullName().equals(currentCDSymbol.getFullName());

      cocoChecker.addCDAttribute(createCheckerAttribute(checkerType, checkerName, isCurrentDiagram));
      cocoChecker.addCDMethod(createAddCheckerMethod(checkerType, checkerName));

      ASTMCType astBaseInterfaceType = astService.getASTBaseInterface();
      ASTMCType cocoNodeType = cocoService.getCoCoType();
      String cocoNodeCollectionName = MCCollectionTypesHelper.printType(astBaseInterfaceType).replaceAll("\\.", "_") + COCOS;
      cocoChecker.addCDAttribute(createCoCoCollectionAttribute(cocoNodeType, cocoNodeCollectionName));

      ASTCDMethod addNodeCoCo = createAddCoCoMethod(cocoNodeType, checkerType);
      this.replaceTemplate(EMPTY_BODY, addNodeCoCo, createAddCoCoImpl(true, cocoNodeCollectionName, checkerName));

      ASTCDMethod visitNode = createVisitMethod(astBaseInterfaceType);
      this.replaceTemplate(EMPTY_BODY, visitNode, createVisitImpl(true, cocoNodeType, cocoNodeCollectionName, checkerName));

      ASTCDMethod checkAll = createCheckAllMethod(astBaseInterfaceType);
      this.replaceTemplate(EMPTY_BODY, checkAll, new StringHookPoint(NODE + ".accept(getRealThis());"));

      cocoChecker.addCDMethod(addNodeCoCo);
      cocoChecker.addCDMethod(visitNode);
      cocoChecker.addCDMethod(checkAll);


      for (CDTypeSymbol cdTypeSymbol : currentCDSymbol.getTypes()) {
        if (!cdTypeSymbol.isClass() && !cdTypeSymbol.isInterface()) {
          continue;
        }

        ASTMCType cocoType = cocoService.getCoCoType((ASTCDType) cdTypeSymbol.getAstNode().get());
        ASTMCType astType = astService.getASTType((ASTCDType) cdTypeSymbol.getAstNode().get());
        String cocoCollectionName = MCCollectionTypesHelper.printType(astType).replaceAll("\\.", "_") + COCOS;

        if (isCurrentDiagram) {
          cocoChecker.addCDAttribute(createCoCoCollectionAttribute(cocoType, cocoCollectionName));
        }

        ASTCDMethod addCoCo = createAddCoCoMethod(cocoType, checkerType);
        this.replaceTemplate(EMPTY_BODY, addCoCo, createAddCoCoImpl(isCurrentDiagram, cocoCollectionName, checkerName));

        ASTCDMethod visit = createVisitMethod(astType);
        this.replaceTemplate(EMPTY_BODY, visit, createVisitImpl(isCurrentDiagram, cocoType, cocoCollectionName, checkerName));

        cocoChecker.addCDMethod(addCoCo);
        cocoChecker.addCDMethod(visit);
      }
    }

    return cocoChecker;
  }

  protected ASTCDAttribute createCheckerAttribute(ASTMCType checkerType, String checkerName, boolean isCurrentDiagram) {
    ASTMCType checkerListType = getCDTypeFacade().createListTypeOf(checkerType);
    ASTCDAttribute checker = getCDAttributeFacade().createAttribute(PRIVATE, checkerListType, checkerName);
    HookPoint hp = isCurrentDiagram ? new StringHookPoint("= new ArrayList<>(Arrays.asList(new " + MCCollectionTypesHelper.printType(checkerType) + "()))")
        : new StringHookPoint("= new ArrayList<>()");
    this.replaceTemplate(VALUE, checker, hp);
    return checker;
  }

  protected ASTCDMethod createAddCheckerMethod(ASTMCType checkerType, String checkerName) {
    ASTCDParameter parameter = getCDParameterFacade().createParameter(checkerType, CHECKER);
    ASTCDMethod addCheckerMethod = getCDMethodFacade().createMethod(PUBLIC, ADD_CHECKER, parameter);
    this.replaceTemplate(EMPTY_BODY, addCheckerMethod, new StringHookPoint("this." + checkerName + ".add(" + CHECKER + ");"));
    return addCheckerMethod;
  }

  protected ASTCDAttribute createCoCoCollectionAttribute(ASTMCType cocoType, String cocoCollectionName) {
    ASTMCType cocoCollectionType = getCDTypeFacade().createCollectionTypeOf(cocoType);
    ASTCDAttribute cocoCollectionAttribute = getCDAttributeFacade().createAttribute(PRIVATE, cocoCollectionType, cocoCollectionName);
    this.replaceTemplate(VALUE, cocoCollectionAttribute, new StringHookPoint("= new LinkedHashSet<>()"));
    return cocoCollectionAttribute;
  }

  protected ASTCDMethod createAddCoCoMethod(ASTMCType cocoType, ASTMCType checkerType) {
    ASTCDParameter parameter = getCDParameterFacade().createParameter(cocoType, COCO);
    return getCDMethodFacade().createMethod(PUBLIC, checkerType, ADD_COCO, parameter);
  }

  protected HookPoint createAddCoCoImpl(boolean isCurrentDiagram, String cocoCollectionName, String checkerName) {
    String impl;
    if (isCurrentDiagram) {
      impl = cocoCollectionName + ".add(" + COCO + ");\n";
    }
    else {
      impl = checkerName + ".stream().findFirst().get()." + ADD_COCO + "(" + COCO + ");\n";
    }
    return new StringHookPoint(impl + "return this;");
  }

  protected ASTCDMethod createVisitMethod(ASTMCType astType) {
    ASTCDParameter parameter = getCDParameterFacade().createParameter(astType, NODE);
    return getCDMethodFacade().createMethod(PUBLIC, VISIT, parameter);
  }

  protected HookPoint createVisitImpl(boolean isCurrentDiagram, ASTMCType cocoType, String cocoCollectionName, String checkerName) {
    if (isCurrentDiagram) {
      return new StringHookPoint(
          "for (" + MCCollectionTypesHelper.printType(cocoType) + " " + COCO + " : " + cocoCollectionName + ") {\n" +
              COCO + "." + CHECK + "(" + NODE + ");\n" +
              "}\n" +
              "// and delegate to all registered checkers of the same language as well\n" +
              checkerName + ".stream().forEach(c -> c.visit(" + NODE + "));");
    }
    else {
      return new StringHookPoint( checkerName + ".stream().forEach(c -> c.visit(" + NODE + "));");
    }
  }

  protected ASTCDMethod createCheckAllMethod(ASTMCType astType) {
    ASTCDParameter parameter = getCDParameterFacade().createParameter(astType, NODE);
    return getCDMethodFacade().createMethod(PUBLIC, CHECK_ALL, parameter);
  }
}
