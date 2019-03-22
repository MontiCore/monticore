package de.monticore.codegen.cd2java.constants;

import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java.factories.CDModifier;
import de.monticore.codegen.cd2java.factories.SuperSymbolHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.umlcd4a.symboltable.CDSymbol;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC_STATIC;

public class ASTConstantsDecorator extends AbstractDecorator<ASTCDCompilationUnit, ASTCDClass> {

  private static final String LITERALS_SUFFIX = "Literals";

  private static final String LANGUAGE = "LANGUAGE";

  private static final String DEFAULT = "DEFAULT";

  private static final String SUPER_GRAMMARS = "superGrammars";

  private static final String AST_CONSTANTS = "ASTConstants";

  private static final String GET_ALL_LANGUAGES = "getAllLanguages";

  public ASTConstantsDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public ASTCDClass decorate(ASTCDCompilationUnit input) {
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
    return CD4AnalysisMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setName(className)
        .addCDAttribute(getLanguageAttribute(grammarName))
        .addCDAttribute(getDefaultAttribute())
        .addAllCDAttributes(getConstantAttribute(enumConstants))
        .addCDAttribute(getSuperGrammarsAttribute())
        .addCDConstructor(getDefaultConstructor(className, input))
        .addCDMethod(getGetAllLanguagesMethod())
        .build();
  }

  protected ASTCDAttribute getLanguageAttribute(String grammarName) {
    return getCDAttributeFactory().createAttribute(CDModifier.PUBLIC_STATIC_FINAL, getCDTypeFactory().createTypeByDefinition("String"), LANGUAGE, "\"" + grammarName + "\"");
  }

  protected List<ASTCDAttribute> getConstantAttribute(List<ASTCDEnumConstant> enumConstants) {
    List<ASTCDAttribute> attributeList = new ArrayList<>();
    for (int i = 0; i < enumConstants.size(); i++) {
      attributeList.add(getCDAttributeFactory().createAttribute(CDModifier.PUBLIC_STATIC_FINAL, getCDTypeFactory().createIntType(), enumConstants.get(i).getName(), Integer.toString(i + 1)));
    }
    return attributeList;
  }

  protected ASTCDAttribute getDefaultAttribute() {
    return getCDAttributeFactory().createAttribute(CDModifier.PUBLIC_STATIC_FINAL, getCDTypeFactory().createIntType(), DEFAULT, "0");
  }

  protected ASTCDAttribute getSuperGrammarsAttribute() {
    return getCDAttributeFactory().createAttribute(CDModifier.PUBLIC_STATIC, getCDTypeFactory().createTypeByDefinition("String[]"), SUPER_GRAMMARS);
  }

  protected ASTCDConstructor getDefaultConstructor(String className, ASTCDCompilationUnit compilationUnit) {
    ASTCDConstructor defaultConstructor = getCDConstructorFactory().createDefaultConstructor(PUBLIC.build(), className);
    List<CDSymbol> superSymbolList = SuperSymbolHelper.getSuperCDs(compilationUnit);
    List<String> superGrammarNames = superSymbolList.stream().map(CDSymbol::getFullName).collect(Collectors.toList());
    this.replaceTemplate(EMPTY_BODY, defaultConstructor, new TemplateHookPoint("ast_constants.ASTConstantsConstructor", superGrammarNames));
    return defaultConstructor;
  }

  protected ASTCDMethod getGetAllLanguagesMethod() {
    ASTCDMethod method = getCDMethodFactory().createMethod(PUBLIC_STATIC, getCDTypeFactory().createTypeByDefinition("Collection<String>"), GET_ALL_LANGUAGES);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("ast_constants.GetAllLanguages"));
    return method;
  }
}
