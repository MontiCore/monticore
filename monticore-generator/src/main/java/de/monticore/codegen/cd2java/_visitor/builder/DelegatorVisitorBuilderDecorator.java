/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._visitor.builder;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4code._ast.CD4CodeMill;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._ast.builder.buildermethods.BuilderMutatorMethodDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.CoreTemplates.VALUE;
import static de.monticore.codegen.cd2java._ast.builder.BuilderConstants.BUILDER_SUFFIX;
import static de.monticore.codegen.cd2java._ast.builder.BuilderConstants.BUILD_INIT_TEMPLATE;
import static de.monticore.codegen.cd2java._ast.builder.BuilderConstants.BUILD_METHOD;
import static de.monticore.codegen.cd2java._ast.builder.BuilderConstants.IS_VALID;
import static de.monticore.codegen.cd2java._ast.builder.BuilderConstants.REAL_BUILDER;
import static de.monticore.cd.facade.CDModifier.*;

/**
 * Creates a DelegatorVisitorBuilder class from a grammar
 */
public class DelegatorVisitorBuilderDecorator extends AbstractCreator<ASTCDClass, ASTCDClass> {

  protected final VisitorService visitorService;

  protected final SymbolTableService symbolTableService;

  protected static final String VISITOR_BUILD_TEMPLATE = "_visitor.delegator.BuildDelegatorVisitor";
  
  public DelegatorVisitorBuilderDecorator(final GlobalExtensionManagement glex,
                                   final VisitorService visitorService,
                                   final SymbolTableService symbolTableService) {
    super(glex);
    this.visitorService = visitorService;
    this.symbolTableService = symbolTableService;
  }

  @Override
  public ASTCDClass decorate(ASTCDClass domainClass) {
    
    String delegatorVisitorSimpleName = visitorService.getDelegatorVisitorSimpleName();
    String builderClassName = delegatorVisitorSimpleName + BUILDER_SUFFIX;
    ASTMCType builderType = this.getMCTypeFacade().createQualifiedType(builderClassName);
    ASTMCType domainType = this.getMCTypeFacade().createQualifiedType(delegatorVisitorSimpleName);
    ASTCDAttribute realThisAttribute = this.getCDAttributeFacade().createAttribute(PROTECTED, builderType, REAL_BUILDER);
    
    // get visitor types and names of super cds and own cd
    List<String> visitorFullNameList =  visitorService.getSuperCDsTransitive().stream()
        .map(visitorService::getVisitorFullName)
        .collect(Collectors.toList());
    visitorFullNameList.add(visitorService.getVisitorFullName());
    List<ASTCDAttribute> builderAttributes = getVisitorAttributes(visitorFullNameList);
    
    // constructor
    ASTCDConstructor constructor = this.getCDConstructorFacade().createConstructor(PUBLIC, builderClassName);
    this.replaceTemplate(EMPTY_BODY, constructor, new StringHookPoint("this." + REAL_BUILDER + " = (" + builderClassName + ") this;"));
    
    // is valid method
    ASTCDMethod isValidMethod = this.getCDMethodFacade().createMethod(PUBLIC, getMCTypeFacade().createBooleanType(), IS_VALID);
    this.replaceTemplate(EMPTY_BODY, isValidMethod, new TemplateHookPoint("_ast.builder.IsValidMethod", new ArrayList<ASTCDAttribute>()));
    
    // accessor methods
    AccessorDecorator accessorDecorator = new AccessorDecorator(glex, visitorService);
    List<ASTCDMethod> accessorMethods = builderAttributes.stream()
        .map(accessorDecorator::decorate)
        .flatMap(List::stream)
        .collect(Collectors.toList());

    // mutator methods
    BuilderMutatorMethodDecorator mutatorDecorator = new BuilderMutatorMethodDecorator(glex, builderType);
    List<ASTCDMethod> mutatorMethods = builderAttributes.stream()
        .map(mutatorDecorator::decorate)
        .flatMap(List::stream)
        .collect(Collectors.toList());
    
    // build method
    ASTCDMethod buildMethod = this.getCDMethodFacade().createMethod(PUBLIC.build(), domainType, BUILD_METHOD);
    this.replaceTemplate(EMPTY_BODY, buildMethod, new TemplateHookPoint(VISITOR_BUILD_TEMPLATE, domainClass.getName(), builderAttributes));
    
    return CD4CodeMill.cDClassBuilder()
        .setName(builderClassName)
        .setModifier(PUBLIC.build())
        .addCDConstructor(constructor)
        .addCDAttribute(realThisAttribute)
        .addAllCDAttributes(builderAttributes)
        .addAllCDMethods(accessorMethods)
        .addAllCDMethods(mutatorMethods)
        .addCDMethod(buildMethod)
        .addCDMethod(isValidMethod)
        .build();
  }

  /**
   * Creates the simple visitor attributes for the delegator visitor based on
   * their fully qualified names.
   * 
   * @param fullVisitorNameList The qualified names of the simple visitors
   * @return The attributes derived from their names
   */
  protected List<ASTCDAttribute> getVisitorAttributes(List<String> fullVisitorNameList) {
    // generate a attribute for own visitor and all super visitors
    // e.g. private Optional<automata._visitor.AutomataVisitor> automataVisitor = Optional.empty();
    List<ASTCDAttribute> attributeList = new ArrayList<>();
    for (String fullName : fullVisitorNameList) {
      String simpleName = Names.getSimpleName(fullName);
      ASTCDAttribute visitorAttribute = getCDAttributeFacade().createAttribute(PRIVATE, getMCTypeFacade().createOptionalTypeOf(fullName),
          StringTransformations.uncapitalize(simpleName));
      this.replaceTemplate(VALUE, visitorAttribute, new StringHookPoint("= Optional.empty();"));
      attributeList.add(visitorAttribute);
    }
    return attributeList;
  }


}
