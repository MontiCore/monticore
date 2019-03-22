package de.monticore.codegen.cd2java.cocos_new;

import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java.factories.SuperSymbolHelper;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.types.TypesHelper;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.umlcd4a.symboltable.CDSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.CoreTemplates.VALUE;
import static de.monticore.codegen.cd2java.factories.CDModifier.*;

public class CoCoCheckerDecorator extends AbstractDecorator<ASTCDCompilationUnit, ASTCDClass> {

  private static final String COCO_PACKAGE = "._cocos.";

  private static final String COCO_SUFFIX = "CoCo";

  private static final String COCOS_SUFFIX = "CoCos";

  private static final String COCO_CHECKER_SUFFIX = "CoCoChecker";

  private static final String REAL_THIS = "realThis";

  private static final String ADD_CHECKER = "addChecker";

  private static final String ADD_COCO = "addCoCo";

  private static final String VISITOR_SUFFIX = "Visitor";

  private static final String VISIT = "visit";

  private static final String AST_PACKAGE = "._ast.";

  private static final String AST_PREFIX = "AST";

  private static final String CHECKER = "checker";

  private static final String NODE = "node";

  private static final String COCO = "coco";

  private final MethodDecorator methodDecorator;

  public CoCoCheckerDecorator(final GlobalExtensionManagement glex, final MethodDecorator methodDecorator) {
    super(glex);
    this.methodDecorator = methodDecorator;
  }

  @Override
  public ASTCDClass decorate(ASTCDCompilationUnit compilationUnit) {
    ASTType cocoCheckerType = getCDTypeFactory().createSimpleReferenceType(compilationUnit.getCDDefinition().getName() + COCO_CHECKER_SUFFIX);
    ASTType visitorType = getCDTypeFactory().createSimpleReferenceType(compilationUnit.getCDDefinition().getName() + VISITOR_SUFFIX);

    ASTCDAttribute realThisAttribute = getCDAttributeFactory().createAttribute(PROTECTED, visitorType, REAL_THIS);
    List<ASTCDMethod> realThisMethods = methodDecorator.decorate(realThisAttribute);

    ASTCDConstructor constructor = this.getCDConstructorFactory().createConstructor(PRIVATE, TypesHelper.printType(cocoCheckerType));
    this.replaceTemplate(EMPTY_BODY, constructor, new StringHookPoint("this."  + REAL_THIS + " = (" + TypesHelper.printType(visitorType) + ") this;"));

    ASTCDClass cocoChecker = CD4AnalysisMill.cDClassBuilder()
        .setName(TypesHelper.printType(cocoCheckerType))
        .addCDAttribute(realThisAttribute)
        .addCDConstructor(constructor)
        .addAllCDMethods(realThisMethods)
        .build();

    CDSymbol cdSymbol = (CDSymbol) compilationUnit.getCDDefinition().getSymbol();

    List<CDSymbol> allCDs = new ArrayList<>();
    allCDs.add(cdSymbol);
    allCDs.addAll(SuperSymbolHelper.getSuperCDs(compilationUnit));
    for (CDSymbol currentCDSymbol : allCDs) {
      ASTType checkerType = getCDTypeFactory().createSimpleReferenceType(currentCDSymbol.getPackageName() + COCO_PACKAGE + currentCDSymbol.getName() + COCO_CHECKER_SUFFIX);
      String checkerName = TypesHelper.printType(checkerType).replaceAll("\\.", "_");
      boolean isCurrentDiagram = cdSymbol.getFullName().equals(currentCDSymbol.getFullName());

      cocoChecker.addCDAttribute(createCheckerAttribute(checkerType, checkerName, isCurrentDiagram));
      cocoChecker.addCDMethod(createAddCheckerMethod(checkerType, checkerName));

      for (CDTypeSymbol cdTypeSymbol : currentCDSymbol.getTypes()) {
        if (!cdTypeSymbol.isClass() && !cdTypeSymbol.isInterface()) {
          continue;
        }
        ASTType cocoType = getCDTypeFactory().createSimpleReferenceType(cdTypeSymbol.getPackageName() + COCO_PACKAGE + cdTypeSymbol.getName() + COCO_SUFFIX);
        ASTType astType = getCDTypeFactory().createSimpleReferenceType(cdTypeSymbol.getPackageName() + AST_PACKAGE + AST_PREFIX + cdTypeSymbol.getName());
        String cocoCollectionName = TypesHelper.printType(astType).replaceAll("\\.", "_") + COCOS_SUFFIX;

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

  protected ASTCDAttribute createCheckerAttribute(ASTType checkerType, String checkerName, boolean isCurrentDiagram) {
    ASTType checkerListType = getCDTypeFactory().createListTypeOf(checkerType);
    ASTCDAttribute checker = getCDAttributeFactory().createAttribute(PRIVATE, checkerListType, checkerName);
    HookPoint hp = isCurrentDiagram ? new StringHookPoint("new ArrayList<>(Arrays.asList(new " + TypesHelper.printType(checkerType) + "()))")
        : new StringHookPoint("= new ArrayList<>()");
    this.replaceTemplate(VALUE, checker, hp);
    return checker;
  }

  protected ASTCDMethod createAddCheckerMethod(ASTType checkerType, String checkerName) {
    ASTCDParameter parameter = getCDParameterFactory().createParameter(checkerType, CHECKER);
    ASTCDMethod addCheckerMethod = getCDMethodFactory().createMethod(PUBLIC, ADD_CHECKER, parameter);
    this.replaceTemplate(EMPTY_BODY, addCheckerMethod, new StringHookPoint("this." + checkerName + ".add(" + CHECKER + ");"));
    return addCheckerMethod;
  }

  protected ASTCDAttribute createCoCoCollectionAttribute(ASTType cocoType, String cocoCollectionName) {
    ASTType cocoCollectionType = getCDTypeFactory().createCollectionTypeOf(cocoType);
    ASTCDAttribute cocoCollectionAttribute = getCDAttributeFactory().createAttribute(PRIVATE, cocoCollectionType, cocoCollectionName);
    this.replaceTemplate(VALUE, cocoCollectionAttribute, new StringHookPoint("= new LinkedHashSet<>()"));
    return cocoCollectionAttribute;
  }

  protected ASTCDMethod createAddCoCoMethod(ASTType cocoType, ASTType checkerType) {
    ASTCDParameter parameter = getCDParameterFactory().createParameter(cocoType, COCO);
    return getCDMethodFactory().createMethod(PUBLIC, checkerType, ADD_COCO, parameter);
  }

  protected HookPoint createAddCoCoImpl(boolean isCurrentDiagram, String cocoCollectionName, String checkerName) {
    if (isCurrentDiagram) {
      return new StringHookPoint(cocoCollectionName + ".add(" + COCO + ");");
    }
    else {
      return new StringHookPoint(checkerName + ".stream().findFirst().get().addCoCo(" + COCO + ");");
    }
  }

  protected ASTCDMethod createVisitMethod(ASTType astType) {
    ASTCDParameter parameter = getCDParameterFactory().createParameter(astType, NODE);
    return getCDMethodFactory().createMethod(PUBLIC, VISIT, parameter);
  }

  protected HookPoint createVisitImpl(boolean isCurrentDiagram, ASTType cocoType, String cocoCollectionName, String checkerName) {
    if (isCurrentDiagram) {
      return new StringHookPoint(
          "for (" + TypesHelper.printType(cocoType) + " coco : " + cocoCollectionName + ") {\n" +
              "coco.check(" + NODE + ");\n" +
              "}\n" +
              "// and delegate to all registered checkers of the same language as well\n" +
              checkerName + ".stream().forEach(c -> c.visit(" + NODE + "));");
    }
    else {
      return new StringHookPoint( checkerName + ".stream().forEach(c -> c.visit(" + NODE + "));");
    }
  }
}
