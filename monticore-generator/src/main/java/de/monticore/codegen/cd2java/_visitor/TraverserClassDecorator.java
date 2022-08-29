/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._visitor;

import com.google.common.collect.Lists;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.*;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mccollectiontypes._ast.ASTMCListType;
import de.monticore.types.mccollectiontypes._ast.ASTMCOptionalType;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.codegen.cd2java.CDModifier.*;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.CoreTemplates.VALUE;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.*;

/**
 * creates a DelegatorVisitor class from a grammar
 */
public class TraverserClassDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDClass> {

  protected final VisitorService visitorService;

  protected final SymbolTableService symbolTableService;

  public TraverserClassDecorator(final GlobalExtensionManagement glex,
                                   final VisitorService visitorService,
                                   final SymbolTableService symbolTableService) {
    super(glex);
    this.visitorService = visitorService;
    this.symbolTableService = symbolTableService;
  }

  @Override
  public ASTCDClass decorate(ASTCDCompilationUnit input) {
    // get visitor names of current node
    String delegatorVisitorSimpleName = visitorService.getTraverserSimpleName();
    ASTMCQualifiedType visitorType = visitorService.getTraverserType();
    String simpleVisitorName = visitorService.getVisitorSimpleName();

    // get visitor types and names of super cds and own cd
    List<DiagramSymbol> cDsTransitive = visitorService.getSuperCDsTransitive();
    cDsTransitive.add(visitorService.getCDSymbol());

    return CD4CodeMill.cDClassBuilder()
        .setName(delegatorVisitorSimpleName)
        .setModifier(PUBLIC.build())
        .setCDInterfaceUsage(CD4CodeMill.cDInterfaceUsageBuilder()
                .addInterface(getMCTypeFacade().createQualifiedType(visitorService.getTraverserInterfaceFullName())).build())
        .addCDMember(getRealThisAttribute(delegatorVisitorSimpleName))
        .addCDMember(ivisitorAttribute())
        .addAllCDMembers(getVisitorAttributes(cDsTransitive))
        .addAllCDMembers(getHandlerAttributes(cDsTransitive))
        .addAllCDMembers(addVisitorMethods(cDsTransitive))
        .addAllCDMembers(addHandlerMethods(cDsTransitive))
        .addAllCDMembers(addDefaultMethods())
        .addAllCDMembers(addTraversedElementsControl())
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
    ASTCDAttribute realThisAttribute = getCDAttributeFacade().createAttribute(PROTECTED.build(), delegatorVisitorSimpleName, REAL_THIS);
    this.replaceTemplate(VALUE, realThisAttribute, new StringHookPoint("= (" + delegatorVisitorSimpleName + ") this"));
    return realThisAttribute;
  }

  /**
   * Adds default attribute.
   *
   * @return The decorated attribute
   */
  protected ASTCDAttribute ivisitorAttribute() {
    String simpleName = Names.getSimpleName(IVISITOR_FULL_NAME) + "List";
    ASTMCQualifiedType type = getMCTypeFacade().createQualifiedType(IVISITOR_FULL_NAME);
    ASTCDAttribute visitorAttribute = getCDAttributeFacade().createAttribute(PROTECTED.build(), getMCTypeFacade().createListTypeOf(type),
            StringTransformations.uncapitalize(simpleName));
    this.replaceTemplate(VALUE, visitorAttribute, new StringHookPoint("= new ArrayList<>()"));
    return visitorAttribute;
  }

  /**
   * Adds default attribute.
   *
   * @return The decorated attribute
   */
  protected List<ASTCDMethod> addDefaultMethods() {
    List<ASTCDMethod> methodList = Lists.newArrayList();

    String simpleName = Names.getSimpleName(IVISITOR_FULL_NAME);
    // add setter: public  void add4IVisitor (IVisitor iVisitor)
    ASTMCQualifiedType visitorType = getMCTypeFacade().createQualifiedType(IVISITOR_FULL_NAME);
    ASTCDParameter visitorParameter = getCDParameterFacade().createParameter(visitorType, StringTransformations.uncapitalize(simpleName));
    ASTCDMethod addVisitorMethod = getCDMethodFacade().createMethod(PUBLIC.build(), "add4" + simpleName, visitorParameter);
    this.replaceTemplate(EMPTY_BODY, addVisitorMethod, new TemplateHookPoint(
            TRAVERSER_ADD_VISITOR_TEMPLATE, simpleName));
    methodList.add(addVisitorMethod);

    // add getter: public  List<IVisitor> getIVisitorList ()
    ASTMCListType listVisitorType = getMCTypeFacade().createListTypeOf(visitorType);
    ASTCDMethod getVisitorMethod = getCDMethodFacade().createMethod(PUBLIC.build(), listVisitorType, "get" + simpleName + "List");
    this.replaceTemplate(EMPTY_BODY, getVisitorMethod,
            new StringHookPoint("return " + StringTransformations.uncapitalize(simpleName) + "List;"));
    methodList.add(getVisitorMethod);

    return methodList;
  }

  /**
   * Adds a hashset as well as implementations for getting and setting it
   *
   * @return The attribute and two methods
   */
  protected List<ASTCDMember> addTraversedElementsControl() {
    List<ASTCDMember> cdElements = Lists.newArrayList();

    // create traversed elements hash set
    ASTCDAttribute traversedElementsAttr = getCDAttributeFacade().createAttribute(
            PROTECTED.build(), TRAVERSED_ELEMS_TYPE, TRAVERSED_ELEMS_NAME);
    this.replaceTemplate(VALUE, traversedElementsAttr,
            new StringHookPoint("= new " + TRAVERSED_ELEMS_INSTANCE_TYPE + "()"));
    cdElements.add(traversedElementsAttr);

    // create getter
    ASTCDMethod getMethod = getCDMethodFacade().createMethod(PUBLIC.build(),
            TRAVERSED_ELEMS_TYPE, "get" + StringTransformations.capitalize(TRAVERSED_ELEMS_NAME));
    this.replaceTemplate(EMPTY_BODY, getMethod, new StringHookPoint("return " + TRAVERSED_ELEMS_NAME + ";"));
    cdElements.add(getMethod);

    // create setter
    // ASTMCListType listVisitorType = getMCTypeFacade().createListTypeOf(visitorType);
    ASTCDParameter setParam = getCDParameterFacade().createParameter(TRAVERSED_ELEMS_TYPE, TRAVERSED_ELEMS_NAME);
    ASTCDMethod setMethod = getCDMethodFacade().createMethod(PUBLIC.build(),
            "set" + StringTransformations.capitalize(TRAVERSED_ELEMS_NAME), setParam);
    this.replaceTemplate(EMPTY_BODY, setMethod,
            new StringHookPoint("this." + TRAVERSED_ELEMS_NAME + " = " + TRAVERSED_ELEMS_NAME  + ";"));
    cdElements.add(setMethod);

    return cdElements;
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
  protected List<ASTCDAttribute> getVisitorAttributes(List<DiagramSymbol> cdSymbols) {
    // generate a attribute for own visitor and all super visitors
    // e.g. private Optional<automata._visitor.AutomataVisitor> automataVisitor = Optional.empty();
    List<ASTCDAttribute> attributeList = new ArrayList<>();
    for (DiagramSymbol cd : cdSymbols) {
      String simpleName = visitorService.getVisitorSimpleName(cd) + "List";
      ASTMCQualifiedType type = visitorService.getVisitor2Type(cd);
      ASTCDAttribute visitorAttribute = getCDAttributeFacade().createAttribute(PROTECTED.build(), getMCTypeFacade().createListTypeOf(type),
          StringTransformations.uncapitalize(simpleName));
      this.replaceTemplate(VALUE, visitorAttribute, new StringHookPoint("= new ArrayList<>();"));
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
  protected List<ASTCDMethod> addVisitorMethods(List<DiagramSymbol> cdSymbols) {
    // add setter and getter for created attribute in 'getVisitorAttributes'
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (DiagramSymbol cd : cdSymbols) {
      String simpleName = visitorService.getVisitorSimpleName(cd);
      // add setter for visitor attribute
      // e.g. public void setAutomataVisitor(automata._visitor.AutomataVisitor visitor)
      ASTMCQualifiedType visitorType = visitorService.getVisitor2Type(cd);
      ASTCDParameter visitorParameter = getCDParameterFacade().createParameter(visitorType, StringTransformations.uncapitalize(simpleName));
      ASTCDMethod addVisitorMethod = getCDMethodFacade().createMethod(PUBLIC.build(), "add4" + cd.getName(), visitorParameter);
      this.replaceTemplate(EMPTY_BODY, addVisitorMethod, new TemplateHookPoint(
          TRAVERSER_ADD_VISITOR_TEMPLATE, simpleName));
      methodList.add(addVisitorMethod);

      // add getter for visitor attribute
      // e.g. public Optional<automata._visitor.AutomataVisitor> getAutomataVisitor()
      ASTMCListType listVisitorType = getMCTypeFacade().createListTypeOf(visitorType);
      ASTCDMethod getVisitorMethod = getCDMethodFacade().createMethod(PUBLIC.build(), listVisitorType, "get" + simpleName + "List");
      this.replaceTemplate(EMPTY_BODY, getVisitorMethod,
          new StringHookPoint("return " + StringTransformations.uncapitalize(simpleName) + "List;"));
      methodList.add(getVisitorMethod);
    }
    return methodList;
  }
  
  /**
   * Adds the attributes for all attachable handlers for the current traverser.
   * The available handlers result from the current language and its super
   * languages.
   * 
   * @param cdSymbols The class diagram symbol of the current language and the
   *          symbols of its transitive super languages
   * @return The decorated handler attributes
   */
  protected List<ASTCDAttribute> getHandlerAttributes(List<DiagramSymbol> cdSymbols) {
    // generate a attribute for own handler and all super handlers
    // e.g. private Optional<automata._visitor.AutomataHandler> automataHandler = Optional.empty();
    List<ASTCDAttribute> attributeList = new ArrayList<>();
    for (DiagramSymbol cd : cdSymbols) {
      String simpleName = visitorService.getHandlerSimpleName(cd);
      ASTMCQualifiedType type = visitorService.getHandlerType(cd);
      ASTCDAttribute handlerAttribute = getCDAttributeFacade().createAttribute(PROTECTED.build(), getMCTypeFacade().createOptionalTypeOf(type),
          StringTransformations.uncapitalize(simpleName));
      this.replaceTemplate(VALUE, handlerAttribute, new StringHookPoint("= Optional.empty()"));
      attributeList.add(handlerAttribute);
    }
    return attributeList;
  }
  
  /**
   * Adds the getter and setter methods for all attachable handlers for the
   * current traverser. The available handlers result from the current language
   * and its super languages.
   * 
   * @param cdSymbols The class diagram symbol of the current language and the
   *          symbols of its transitive super languages
   * @return The decorated handler getter and setter methods
   */
  protected List<ASTCDMethod> addHandlerMethods(List<DiagramSymbol> cdSymbols) {
    // add setter and getter for created attribute in 'getVisitorAttributes'
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (DiagramSymbol cd : cdSymbols) {
      String simpleName = visitorService.getHandlerSimpleName(cd);
      // add setter for handler attribute
      // e.g. public void setAutomataHandler(automata._visitor.AutomataHandler handler)
      ASTMCQualifiedType handlerType = visitorService.getHandlerType(cd);
      ASTCDParameter handlerParameter = getCDParameterFacade().createParameter(handlerType, StringTransformations.uncapitalize(simpleName));
      ASTCDMethod setHandlerMethod = getCDMethodFacade().createMethod(PUBLIC.build(), "set" + simpleName, handlerParameter);
      this.replaceTemplate(EMPTY_BODY, setHandlerMethod, new TemplateHookPoint(
          TRAVERSER_SET_HANDLER_TEMPLATE, simpleName));
      methodList.add(setHandlerMethod);

      // add getter for handler attribute
      // e.g. public Optional<automata._visitor.AutomataHandler> getAutomataHandler()
      ASTMCOptionalType optionalHandlerType = getMCTypeFacade().createOptionalTypeOf(handlerType);
      ASTCDMethod getHandlerMethod = getCDMethodFacade().createMethod(PUBLIC.build(), optionalHandlerType, "get" + simpleName);
      this.replaceTemplate(EMPTY_BODY, getHandlerMethod,
          new StringHookPoint("return " + StringTransformations.uncapitalize(simpleName) + ";"));
      methodList.add(getHandlerMethod);
    }
    return methodList;
  }
  
}
