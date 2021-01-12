/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._cocos;

import com.google.common.collect.Lists;
import de.monticore.cd.cd4analysis.CD4AnalysisMill;
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
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.List;

import static de.monticore.cd.facade.CDModifier.PRIVATE;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.CoreTemplates.VALUE;
import static de.monticore.codegen.cd2java._cocos.CoCoConstants.*;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.TRAVERSER;

/**
 * creates the CoCoChecker class for a grammar
 */
public class CoCoCheckerDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDClass> {

  protected final MethodDecorator methodDecorator;

  protected final CoCoService cocoService;

  protected final VisitorService visitorService;

  public static final String TEMPLATE_PATH = "_cocos.";


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
    String cocoCheckerName = cocoService.getCheckerSimpleTypeName();
    ASTCDAttribute traverserAttribute = getCDAttributeFacade().createAttribute(PRIVATE, visitorService.getTraverserInterfaceType(), TRAVERSER);
    List<ASTCDMethod> traverserMethods = methodDecorator.decorate(traverserAttribute);

    ASTCDConstructor constructor = this.getCDConstructorFacade().createConstructor(PUBLIC, cocoCheckerName);
    this.replaceTemplate(EMPTY_BODY, constructor,
            new TemplateHookPoint(TEMPLATE_PATH + "CoCoCheckerConstructor", cocoService.getMillFullName(),
                    cocoService.getAllCDs(), visitorService));

    ASTCDClass cocoChecker = CD4AnalysisMill.cDClassBuilder()
        .setName(cocoCheckerName)
        .setModifier(PUBLIC.build())
        .addCDAttribute(traverserAttribute)
        .addCDConstructor(constructor)
        .addAllCDMethods(traverserMethods)
        .build();

    // travers all Super CDDefinitionSymbol transitive and own one
    for (CDDefinitionSymbol currentCDSymbol : cocoService.getAllCDs()) {
      CoCoService cocoService = CoCoService.createCoCoService(currentCDSymbol);
      ASTService astService = ASTService.createASTService(currentCDSymbol);

      cocoChecker.addCDMethod(createAddCheckerMethod(currentCDSymbol));

      ASTMCType astBaseInterfaceType = astService.getASTBaseInterface();

      ASTCDMethod checkAll = createCheckAllMethod(astBaseInterfaceType);
      this.replaceTemplate(EMPTY_BODY, checkAll, new StringHookPoint(NODE_SIMPLE_NAME + ".accept(getTraverser());"));

      cocoChecker.addCDMethod(checkAll);

      for (CDTypeSymbol cdTypeSymbol : currentCDSymbol.getTypes()) {
        // do not generate for enums (only classes and interfaces)
        if (cdTypeSymbol.isIsEnum()) {
          continue;
        }

        ASTMCType cocoType = cocoService.getCoCoType(cdTypeSymbol.getAstNode());
        // always use global checker type here, also for super grammar addCoCo methods
        ASTCDMethod addCoCo = createAddCoCoMethod(cocoType);
        this.replaceTemplate(EMPTY_BODY, addCoCo, createAddCoCoImpl(currentCDSymbol.getName()));

        cocoChecker.addCDMethod(addCoCo);
      }
    }

    return cocoChecker;
  }

  protected ASTCDMethod createAddCheckerMethod(CDDefinitionSymbol cdSymbol) {
    ASTMCType checkerType = getMCTypeFacade().createQualifiedType(cocoService.getCheckerFullTypeName(cdSymbol));
    ASTCDParameter parameter = getCDParameterFacade().createParameter(checkerType, CHECKER_SIMPLE_NAME);
    ASTCDMethod addCheckerMethod = getCDMethodFacade().createMethod(PUBLIC, ADD_CHECKER, parameter);
    List<String> superCds = Lists.newArrayList(cdSymbol.getName());
    cocoService.getSuperCDsTransitive(cdSymbol).forEach(c -> superCds.add(c.getName()));
    this.replaceTemplate(EMPTY_BODY, addCheckerMethod, new TemplateHookPoint(TEMPLATE_PATH + "AddChecker", superCds));
    return addCheckerMethod;
  }

  protected ASTCDAttribute createCoCoCollectionAttribute(ASTMCType cocoType, String cocoCollectionName) {
    ASTMCType cocoCollectionType = getMCTypeFacade().createCollectionTypeOf(cocoType);
    ASTCDAttribute cocoCollectionAttribute = getCDAttributeFacade().createAttribute(PRIVATE, cocoCollectionType, cocoCollectionName);
    this.replaceTemplate(VALUE, cocoCollectionAttribute, new StringHookPoint("= new LinkedHashSet<>()"));
    return cocoCollectionAttribute;
  }

  protected ASTCDMethod createAddCoCoMethod(ASTMCType cocoType) {
    ASTCDParameter parameter = getCDParameterFacade().createParameter(cocoType, COCO_SIMPLE_NAME);
    return getCDMethodFacade().createMethod(PUBLIC, ADD_COCO, parameter);
  }

  protected HookPoint createAddCoCoImpl(String traverserName) {
    return new StringHookPoint("traverser.add4" + traverserName + "(coco);");
  }

  protected ASTCDMethod createCheckAllMethod(ASTMCType astType) {
    ASTCDParameter parameter = getCDParameterFacade().createParameter(astType, NODE_SIMPLE_NAME);
    return getCDMethodFacade().createMethod(PUBLIC, CHECK_ALL, parameter);
  }

}
