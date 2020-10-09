/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._visitor;

import static de.monticore.cd.facade.CDModifier.PRIVATE;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.CoreTemplates.VALUE;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.GET_REAL_THIS;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.REAL_THIS;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.SET_REAL_THIS;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.SET_REAL_THIS_DELEGATOR_TEMPLATE;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.TRAVERSER_SET_VISITOR_TEMPLATE;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.cd.cd4analysis._ast.ASTCDParameter;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.cd.cd4code.CD4CodeMill;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCOptionalType;
import de.monticore.types.mcfullgenerictypes.MCFullGenericTypesMill;
import de.monticore.types.prettyprint.MCSimpleGenericTypesPrettyPrinter;
import de.se_rwth.commons.StringTransformations;

/**
 * creates a DelegatorVisitor class from a grammar
 */
public class TraverserDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDClass> {

  protected final VisitorService visitorService;

  protected final SymbolTableService symbolTableService;

  public TraverserDecorator(final GlobalExtensionManagement glex,
                                   final VisitorService visitorService,
                                   final SymbolTableService symbolTableService) {
    super(glex);
    this.visitorService = visitorService;
    this.symbolTableService = symbolTableService;
  }

  @Override
  public ASTCDClass decorate(ASTCDCompilationUnit input) {
    // change class names to qualified name
    visitorService.calculateCDTypeNamesWithASTPackage(input);
    
    // get visitor names of current node
    String delegatorVisitorSimpleName = visitorService.getTraverserSimpleName();
    ASTMCQualifiedType visitorType = visitorService.getTraverserType();
    String simpleVisitorName = visitorService.getVisitorSimpleName();

    // get visitor types and names of super cds and own cd
    List<CDDefinitionSymbol> cDsTransitive = visitorService.getSuperCDsTransitive();
    cDsTransitive.add(visitorService.getCDSymbol());

    return CD4CodeMill.cDClassBuilder()
        .setName(delegatorVisitorSimpleName)
        .setModifier(PUBLIC.build())
        .addInterface(getMCTypeFacade().createQualifiedType(visitorService.getTraverserInterfaceFullName()))
        .addCDAttribute(getRealThisAttribute(delegatorVisitorSimpleName))
        .addCDMethod(addGetRealThisMethod(delegatorVisitorSimpleName))
        .addCDMethod(addSetRealThisMethod(visitorType, delegatorVisitorSimpleName, simpleVisitorName))
        .addAllCDAttributes(getVisitorAttributes(cDsTransitive))
        .addAllCDMethods(addVisitorMethods(cDsTransitive))
        .build();
  }

  /**
   * Adds the realThis attribute.
   * 
   * @param delegatorVisitorSimpleName The name of the visitor and type of the
   *          attribute
   * @return The decorated attribute
   */
  protected ASTCDAttribute getRealThisAttribute(String delegatorVisitorSimpleName) {
    ASTCDAttribute realThisAttribute = getCDAttributeFacade().createAttribute(PRIVATE, delegatorVisitorSimpleName, REAL_THIS);
    this.replaceTemplate(VALUE, realThisAttribute, new StringHookPoint("= (" + delegatorVisitorSimpleName + ") this"));
    return realThisAttribute;
  }

  /**
   * Adds the getRealThis method.
   * 
   * @param delegatorVisitorSimpleName The return type of the method
   * @return The decorated getRealThis method
   */
  protected ASTCDMethod addGetRealThisMethod(String delegatorVisitorSimpleName) {
    ASTMCQualifiedType visitorType = getMCTypeFacade().createQualifiedType(delegatorVisitorSimpleName);

    ASTCDMethod getRealThisMethod = this.getCDMethodFacade().createMethod(PUBLIC, visitorType, GET_REAL_THIS);
    this.replaceTemplate(EMPTY_BODY, getRealThisMethod, new StringHookPoint("return realThis;"));
    return getRealThisMethod;
  }

  /**
   * Adds the setRealThis method.
   * 
   * @param visitorType The input parameter type
   * @param delegatorVisitorSimpleName The name and type of the traverser
   * @param simpleVisitorType The name of the available visitor of this language
   * @return The decorated setRealThis method
   */
  protected ASTCDMethod addSetRealThisMethod(ASTMCType visitorType, String delegatorVisitorSimpleName,
                                              String simpleVisitorType) {
    ASTCDParameter visitorParameter = getCDParameterFacade().createParameter(visitorType, "realThis");

    List<ASTMCQualifiedType> superVisitors = visitorService.getSuperVisitors();
    List<String> superVisitorNames = superVisitors
        .stream()
        .map(t -> t.printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()))
        .filter(s -> s.contains("."))
        .map(s -> s = s.substring(s.lastIndexOf(".") + 1))
        .collect(Collectors.toList());
    String generatedErrorCode = visitorService.getGeneratedErrorCode(visitorType.printType(new MCSimpleGenericTypesPrettyPrinter(new IndentPrinter())) +
        delegatorVisitorSimpleName + simpleVisitorType);
    ASTCDMethod getRealThisMethod = this.getCDMethodFacade().createMethod(PUBLIC, SET_REAL_THIS, visitorParameter);
    this.replaceTemplate(EMPTY_BODY, getRealThisMethod, new TemplateHookPoint(
        SET_REAL_THIS_DELEGATOR_TEMPLATE, delegatorVisitorSimpleName, simpleVisitorType,
        superVisitorNames, generatedErrorCode ));
    return getRealThisMethod;
  }

  /**
   * Adds the attributes for all attachable visitors for the current traverser.
   * The available visitors result from the current language and its super
   * languages.
   * 
   * @param cdSymbols The class diagram symbol of the current language and the
   *          symbols of its transitive super languages
   * @return The decorated visitor attributes
   */
  protected List<ASTCDAttribute> getVisitorAttributes(List<CDDefinitionSymbol> cdSymbols) {
    // generate a attribute for own visitor and all super visitors
    // e.g. private Optional<automata._visitor.AutomataVisitor> automataVisitor = Optional.empty();
    List<ASTCDAttribute> attributeList = new ArrayList<>();
    for (CDDefinitionSymbol cd : cdSymbols) {
      String simpleName = visitorService.getVisitorSimpleName(cd);
      ASTMCQualifiedType type = visitorService.getVisitor2Type(cd);
      ASTCDAttribute visitorAttribute = getCDAttributeFacade().createAttribute(PRIVATE, getMCTypeFacade().createOptionalTypeOf(type),
          StringTransformations.uncapitalize(simpleName));
      this.replaceTemplate(VALUE, visitorAttribute, new StringHookPoint("= Optional.empty();"));
      attributeList.add(visitorAttribute);
    }
    return attributeList;
  }

  /**
   * Adds the getter and setter methods for all attachable visitors for the
   * current traverser. The available visitors result from the current language
   * and its super languages.
   * 
   * @param cdSymbols The class diagram symbol of the current language and the
   *          symbols of its transitive super languages
   * @return The decorated visitor getter and setter methods
   */
  protected List<ASTCDMethod> addVisitorMethods(List<CDDefinitionSymbol> cdSymbols) {
    // add setter and getter for created attribute in 'getVisitorAttributes'
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (CDDefinitionSymbol cd : cdSymbols) {
      String simpleName = visitorService.getVisitorSimpleName(cd);
      //add setter for visitor attribute
      //e.g. public void setAutomataVisitor(automata._visitor.AutomataVisitor AutomataVisitor)
      ASTMCQualifiedType visitorType = visitorService.getVisitor2Type(cd);
      ASTCDParameter visitorParameter = getCDParameterFacade().createParameter(visitorType, StringTransformations.uncapitalize(simpleName));
      ASTCDMethod setVisitorMethod = getCDMethodFacade().createMethod(PUBLIC, "set" + simpleName, visitorParameter);
      this.replaceTemplate(EMPTY_BODY, setVisitorMethod, new TemplateHookPoint(
          TRAVERSER_SET_VISITOR_TEMPLATE, simpleName));
      methodList.add(setVisitorMethod);

      //add getter for visitor attribute
      // e.g. public Optional<automata._visitor.AutomataVisitor> getAutomataVisitor()
      ASTMCOptionalType optionalVisitorType = getMCTypeFacade().createOptionalTypeOf(visitorType);
      ASTCDMethod getVisitorMethod = getCDMethodFacade().createMethod(PUBLIC, optionalVisitorType, "get" + simpleName);
      this.replaceTemplate(EMPTY_BODY, getVisitorMethod,
          new StringHookPoint("return " + StringTransformations.uncapitalize(simpleName) + ";"));
      methodList.add(getVisitorMethod);
    }
    return methodList;
  }
}
