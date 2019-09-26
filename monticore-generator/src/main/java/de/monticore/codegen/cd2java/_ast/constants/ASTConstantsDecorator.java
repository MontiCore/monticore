/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.constants;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.MCBasicTypesMill;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.CoreTemplates.VALUE;
import static de.monticore.codegen.cd2java.factories.CDModifier.*;

public class ASTConstantsDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDClass> {

  public static final String LITERALS_SUFFIX = "Literals";

  protected static final String LANGUAGE = "LANGUAGE";

  protected static final String DEFAULT = "DEFAULT";

  protected static final String SUPER_GRAMMARS = "superGrammars";

  protected static final String AST_CONSTANTS = "ASTConstants";

  protected static final String GET_ALL_LANGUAGES = "getAllLanguages";

  protected final AbstractService<?> service;

  public ASTConstantsDecorator(final GlobalExtensionManagement glex,
                               final AbstractService abstractService) {
    super(glex);
    this.service = abstractService;
  }

  @Override
  public ASTCDClass decorate(final ASTCDCompilationUnit input) {
    String grammarName = input.getCDDefinition().getName();
    String className = AST_CONSTANTS + grammarName;
    Optional<ASTCDEnum> literalsEnum = input.getCDDefinition().getCDEnumList().stream()
        .filter(astcdEnum -> astcdEnum.getName().equals(grammarName + LITERALS_SUFFIX))
        .findFirst();
    List<ASTCDEnumConstant> enumConstants = new ArrayList<>();
    if (literalsEnum.isPresent()) {
      enumConstants = literalsEnum.get().getCDEnumConstantList().stream()
          .map(ASTCDEnumConstant::deepClone)
          .collect(Collectors.toList());
    } else {
      for (ASTCDEnum astcdEnum : input.getCDDefinition().getCDEnumList()) {
        enumConstants.addAll(astcdEnum.getCDEnumConstantList().stream()
            .map(ASTCDEnumConstant::deepClone)
            .collect(Collectors.toList()));
      }
    }
    List<CDDefinitionSymbol> superSymbolList = service.getSuperCDsTransitive();
    return CD4AnalysisMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setName(className)
        .addCDAttribute(getLanguageAttribute(grammarName))
        .addCDAttribute(getDefaultAttribute())
        .addAllCDAttributes(getConstantAttribute(enumConstants))
        .addCDAttribute(getSuperGrammarsAttribute(superSymbolList))
        .addCDConstructor(getDefaultConstructor(className))
        .addCDMethod(getGetAllLanguagesMethod(superSymbolList))
        .build();
  }

  protected ASTCDAttribute getLanguageAttribute(String grammarName) {
    ASTCDAttribute languageAttribute = getCDAttributeFacade().createAttribute(PUBLIC_STATIC_FINAL, String.class, LANGUAGE);
    this.replaceTemplate(VALUE, languageAttribute, new StringHookPoint("= \"" + grammarName + "\""));
    return languageAttribute;
  }

  protected List<ASTCDAttribute> getConstantAttribute(List<ASTCDEnumConstant> enumConstants) {
    List<ASTCDAttribute> attributeList = new ArrayList<>();
    for (int i = 0; i < enumConstants.size(); i++) {
      ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(PUBLIC_STATIC_FINAL, getCDTypeFacade().createIntType(), enumConstants.get(i).getName());
      this.replaceTemplate(VALUE, attribute, new StringHookPoint("= " + (i + 1)));
      attributeList.add(attribute);
    }
    return attributeList;
  }

  protected ASTCDAttribute getSuperGrammarsAttribute(Collection<CDDefinitionSymbol> superSymbolList) {
    List<String> superGrammarNames = superSymbolList.stream().map(CDDefinitionSymbol::getFullName).map(x -> "\"" + x + "\"").collect(Collectors.toList());
    ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(PUBLIC_STATIC, getCDTypeFacade().createArrayType(String.class, 1), SUPER_GRAMMARS);
    if (!superSymbolList.isEmpty()) {
      String s = superGrammarNames.stream().reduce((a, b) -> a + ", " + b).get();
      this.replaceTemplate(VALUE, attribute, new StringHookPoint("= {" + s + "}"));
    } else {
      this.replaceTemplate(VALUE, attribute, new StringHookPoint("= {}"));
    }
    return attribute;
  }

  protected ASTCDAttribute getDefaultAttribute() {
    ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(PUBLIC_STATIC_FINAL, getCDTypeFacade().createIntType(), DEFAULT);
    this.replaceTemplate(VALUE, attribute, new StringHookPoint("= 0"));
    return attribute;
  }

 /*
   TODO Braucht man das?
 protected ASTCDAttribute getSuperGrammarsAttribute(Collection<CDDefinitionSymbol> superSymbolList) {
    List<String> superGrammarNames = superSymbolList.stream().map(CDDefinitionSymbol::getFullName).map(x -> "\"" + x + "\"").collect(Collectors.toList());
    ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(PUBLIC_STATIC, getCDTypeFacade().createArrayType(String.class, 1), SUPER_GRAMMARS);
    if (!superSymbolList.isEmpty()) {
      String s = superGrammarNames.stream().reduce((a, b) -> a + ", " + b).get();
      this.replaceTemplate(VALUE, attribute, new StringHookPoint("= {" + s + "}"));
    } else {
      this.replaceTemplate(VALUE, attribute, new StringHookPoint("= {}"));
    }
    return attribute;
  }
*/
  protected ASTCDConstructor getDefaultConstructor(String className) {
    return getCDConstructorFacade().createConstructor(PUBLIC, className);
  }

  protected ASTCDMethod getGetAllLanguagesMethod(Collection<CDDefinitionSymbol> superCDs) {
    ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(getCDTypeFacade().createTypeByDefinition("Collection<String>")).build();

    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC_STATIC, returnType, GET_ALL_LANGUAGES);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_ast.ast_constants.GetAllLanguages",
       superCDs));
    return method;
  }
}
