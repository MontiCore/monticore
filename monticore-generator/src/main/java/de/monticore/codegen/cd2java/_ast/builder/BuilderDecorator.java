package de.monticore.codegen.cd2java._ast.builder;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java._ast.builder.buildermethods.BuilderMutatorMethodDecorator;
import de.monticore.codegen.cd2java.exception.DecorateException;
import de.monticore.codegen.cd2java.factories.CDModifier;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._ast.MCBasicTypesMill;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.CoreTemplates.VALUE;
import static de.monticore.codegen.cd2java.factories.CDModifier.*;

public class BuilderDecorator extends AbstractCreator<ASTCDClass, ASTCDClass> {

  public static final String BUILD_INIT_TEMPLATE = "_ast.builder.BuildInit";

  public static final String BUILDER_SUFFIX = "Builder";

  public static final String REAL_BUILDER = "realBuilder";

  public static final String BUILD_METHOD = "build";

  public static final String IS_VALID = "isValid";

  private final AccessorDecorator accessorDecorator;

  protected final AbstractService service;

  public BuilderDecorator(final GlobalExtensionManagement glex,
                          final AccessorDecorator accessorDecorator,
                          final AbstractService service) {
    super(glex);
    this.accessorDecorator = accessorDecorator;
    this.service = service;
  }

  @Override
  public ASTCDClass decorate(final ASTCDClass domainClass) throws DecorateException {
    String builderClassName = domainClass.getName() + BUILDER_SUFFIX;
    ASTMCType domainType = this.getCDTypeFacade().createQualifiedType(domainClass.getName());
    ASTMCType builderType = this.getCDTypeFacade().createQualifiedType(builderClassName);


    CDModifier modifier = PUBLIC;
    if (domainClass.isPresentModifier() && domainClass.getModifier().isAbstract()) {
      modifier = PUBLIC_ABSTRACT;
    }

    ASTCDAttribute realThisAttribute = this.getCDAttributeFacade().createAttribute(PROTECTED, builderType, REAL_BUILDER);
    List<ASTCDAttribute> builderAttributes = domainClass.getCDAttributeList().stream()
        .map(ASTCDAttribute::deepClone)
        .collect(Collectors.toList());
    List<ASTCDAttribute> mandatoryAttributes = builderAttributes.stream()
        .filter(a -> !GeneratorHelper.isListType(a.printType()))
        .filter(a -> !GeneratorHelper.isOptional(a))
        .filter(a -> !GeneratorHelper.isPrimitive(a.getMCType()))
        .filter(a -> !service.isInherited(a))
        .collect(Collectors.toList());


    ASTCDConstructor constructor = this.getCDConstructorFacade().createConstructor(PROTECTED, builderClassName);
    this.replaceTemplate(EMPTY_BODY, constructor, new StringHookPoint("this." + REAL_BUILDER + " = (" + builderClassName + ") this;"));

    ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(domainType).build();
    ASTCDMethod buildMethod = this.getCDMethodFacade().createMethod(modifier, returnType, BUILD_METHOD);
    this.replaceTemplate(EMPTY_BODY, buildMethod, new TemplateHookPoint("_ast.builder.BuildMethod", domainClass, mandatoryAttributes));

    returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(getCDTypeFacade().createBooleanType()).build();
    ASTCDMethod isValidMethod = this.getCDMethodFacade().createMethod(PUBLIC, returnType, IS_VALID);
    this.replaceTemplate(EMPTY_BODY, isValidMethod, new TemplateHookPoint("_ast.builder.IsValidMethod", mandatoryAttributes));

    List<ASTCDMethod> accessorMethods = builderAttributes.stream()
        .map(accessorDecorator::decorate)
        .flatMap(List::stream)
        .collect(Collectors.toList());

    BuilderMutatorMethodDecorator mutatorDecorator = new BuilderMutatorMethodDecorator(glex, builderType);
    List<ASTCDMethod> mutatorMethods = builderAttributes.stream()
        .map(mutatorDecorator::decorate)
        .flatMap(List::stream)
        .collect(Collectors.toList());

    builderAttributes.forEach(this::addAttributeDefaultValues);

    return CD4AnalysisMill.cDClassBuilder()
        .setModifier(modifier.build())
        .setName(builderClassName)
        .addCDAttribute(realThisAttribute)
        .addAllCDAttributes(builderAttributes)
        .addCDConstructor(constructor)
        .addCDMethod(buildMethod)
        .addCDMethod(isValidMethod)
        .addAllCDMethods(accessorMethods)
        .addAllCDMethods(mutatorMethods)
        .build();
  }

  protected void addAttributeDefaultValues(ASTCDAttribute attribute) {
    if (GeneratorHelper.isListType(attribute.printType())) {
      this.replaceTemplate(VALUE, attribute, new StringHookPoint("= new java.util.ArrayList<>()"));

    } else if (GeneratorHelper.isOptional(attribute)) {
      this.replaceTemplate(VALUE, attribute, new StringHookPoint("= Optional.empty()"));
    }
  }
}
