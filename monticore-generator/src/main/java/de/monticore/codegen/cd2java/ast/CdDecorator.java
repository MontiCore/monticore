/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.cd2java.ast;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.cd.prettyprint.AstPrinter;
import de.monticore.cd.transformation.ASTCDTransformation;
import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.visitor.VisitorGeneratorHelper;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.codegen.mc2cd.manipul.BaseInterfaceAddingManipulation;
import de.monticore.codegen.mc2cd.transl.ConstantsTranslation;
import de.monticore.codegen.symboltable.SymbolTableGenerator;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.io.paths.IterablePath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.Symbol;
import de.monticore.types.CollectionTypesPrinter;
import de.monticore.types.MCCollectionTypesHelper;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._ast.MCBasicTypesMill;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import groovyjarjarantlr.ANTLRException;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static de.se_rwth.commons.Names.getSimpleName;

/**
 * Decorates class diagrams by adding of new classes and methods using in ast
 * files
 */
public class CdDecorator {

  public static final String DIR_REPORTS = "_reports";

  public static final String THIS_PREFIX = String.format("%n%15cthis.", ' ');

  /**
   * The qualified name of the empty method body template
   */
  public static final String EMPTY_BODY_TEMPLATE = "ast.EmptyMethodBody";

  public static final String CLASS_CONTENT_TEMPLATE = "ast.ClassContent";

  public static final String ERROR_IFNULL_TEMPLATE = "ast.ErrorIfNull";

  public static final String NODE_FACTORY = "NodeFactory";

  public static final String MILL = "Mill";

  protected static final String DEL = ", ";

  protected ASTCDTransformation cdTransformation = new ASTCDTransformation();

  protected GlobalExtensionManagement glex;

  protected IterablePath targetPath;

  protected GlobalScope symbolTable;

  public CdDecorator(
      GlobalExtensionManagement glex,
      GlobalScope symbolTable,
      IterablePath targetPath) {
    this.glex = glex;
    this.symbolTable = symbolTable;
    this.targetPath = targetPath;
  }

  protected void addAbstractStereotype(ASTCDDefinition def) {
    for (ASTCDClass clazz : def.getCDClassList()) {
      ASTModifier modifier;
      if (clazz.isPresentModifier()) {
        modifier = clazz.getModifier();
        if (modifier.isAbstract()) {
          ASTCDStereotype stereo;
          // Create Stereo
          if (modifier.isPresentStereotype()) {
            stereo = modifier.getStereotype();
          } else {
            stereo = CD4AnalysisMill.cDStereotypeBuilder().build();
            modifier.setStereotype(stereo);
          }
          ASTCDStereoValue value = CD4AnalysisMill.cDStereoValueBuilder().setName("Abstract").build();
          stereo.getValueList().add(value);
        }
      }
    }
  }

  public void decorate(ASTCDCompilationUnit cdCompilationUnit) {
    AstGeneratorHelper astHelper = new AstGeneratorHelper(cdCompilationUnit, symbolTable);
    ASTCDDefinition cdDefinition = cdCompilationUnit.getCDDefinition();
    List<ASTCDClass> nativeClasses = Lists.newArrayList(cdDefinition.getCDClassList());

    // Run over classdiagramm and converts cd types to mc-java types
    astHelper.transformCdTypes2Java();

    addAbstractStereotype(cdDefinition);

    // Interface for all ast nodes of the language
    decorateBaseInterface(cdDefinition);

    // Decorate with builder pattern
    addBuilders(cdDefinition, astHelper);

    addNodeFactoryClass(cdCompilationUnit, nativeClasses, astHelper);

    addMillClass(cdCompilationUnit, nativeClasses, astHelper);

    // Check if handwritten ast types exist
    transformCdTypeNamesForHWTypes(cdCompilationUnit);

    cdDefinition.getCDClassList().forEach(c -> addSuperInterfaces(c));

    // Decorate with additional methods and attributes
    for (ASTCDClass clazz : nativeClasses) {
      addConstructors(clazz, astHelper);
      addAdditionalMethods(clazz, astHelper);
      addListMethods(clazz, astHelper, cdDefinition);
      addGetter(clazz, astHelper);
      addSetter(clazz, astHelper, cdDefinition);
      addOptionalMethods(clazz, astHelper, cdDefinition);
      addSymbolAndScopeAttributesAndMethods(clazz, astHelper);
      addReferencedSymbolAttributes(clazz, astHelper);
      addReferencedSymbolAndDefinitionMethods(clazz, astHelper);

      Optional<ASTCDClass> builder = astHelper.getASTBuilder(clazz);
      builder.ifPresent(astcdClass -> decorateBuilderClass(astcdClass, astHelper, cdDefinition));
    }

    cdDefinition.getCDClassList().forEach(c -> makeAbstractIfHWC(c));

    for (ASTCDInterface interf : cdDefinition.getCDInterfaceList()) {
      addListMethods(interf, astHelper);
      addOptionalMethods(interf, astHelper);
      addSymbolAndScopeAttributesAndMethods(interf, astHelper);
      addGetter(interf);
      addSetter(interf);
    }

    // Add ASTConstant class
    addConstantsClass(cdDefinition, astHelper);

    // Additional imports
    cdCompilationUnit.getMCImportStatementList().add(
        MCBasicTypesMill.mCImportStatementBuilder()
            .setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(
                Lists.newArrayList(VisitorGeneratorHelper.getQualifiedVisitorType(astHelper
                    .getPackageName(), cdDefinition.getName()))).build())
            .build());


  }

  protected void decorateBuilderClass(ASTCDClass builder, AstGeneratorHelper astHelper, ASTCDDefinition cdDefinition) {
    addListMethods(builder, astHelper, cdDefinition);
    addGetter(builder, astHelper);
    addSetter(builder, astHelper, cdDefinition);
    addOptionalMethods(builder, astHelper, cdDefinition);
    addASTNodeBuilderMethods(builder, astHelper);
  }

  protected void addASTNodeBuilderMethods(ASTCDClass clazz, AstGeneratorHelper astHelper) {
    if (!clazz.printSuperClass().startsWith("de.monticore.ast.ASTNodeBuilder")) {
      for (AstBuilderMethods value : AstBuilderMethods.values()) {
        String methodSignatur = String.format(value.getMethodDeclaration(),
            clazz.getName());
        additionalNodeBuilderMethod(clazz, value.getMethodName(),
            methodSignatur);
      }
    }
  }

  protected void addSymbolAndScopeAttributesAndMethods(ASTCDType cdType, AstGeneratorHelper astHelper) {
    MCGrammarSymbol grammarSymbol = astHelper.getGrammarSymbol();
    if (grammarSymbol == null) {
      Log.warn("0xA0117 Symbol methods can not be generated, because the grammar symbol is not found");
      return;
    }

    String name = AstGeneratorHelper.getASTClassNameWithoutPrefix(cdType);
    Optional<ProdSymbol> prodSymbol = grammarSymbol.
        getSpannedScope().resolve(name, ProdSymbol.KIND);

    if (!prodSymbol.isPresent()) {
      // Externe Produktionen werden nicht gefunden (der Name endet auf "Ext", steht
      // aber in der Tabelle anders drin
      // Das Symbol für den Interface-AST eienr Grammatik wird ebenfalls nicht gefunden
      // ==> erstmal keine Warnung (Ansonsten sollte MontiCoreScriptTest angepasst werden)
      return;
    }
    String symbolName = prodSymbol.get().getSymbolDefinitionKind().orElse(name);
    String scopeName = "I" + grammarSymbol.getName() + AstGeneratorHelper.SCOPE;
    String qualifiedScopeName = grammarSymbol.getFullName().toLowerCase() + "." +
            SymbolTableGenerator.PACKAGE + "." + scopeName;
    addEnclosingScopeAttributeAndMethods2(cdType, qualifiedScopeName, grammarSymbol);

    if (prodSymbol.get().isSymbolDefinition()) {
      addSymbolAttributeAndMethods(cdType, symbolName, grammarSymbol);
      addSymbolAttributeAndMethods2(cdType, symbolName, grammarSymbol);
    }

    if (prodSymbol.get().isScopeDefinition()) {
      addSpannedScopeAttributeAndMethods(cdType, grammarSymbol);
      addSpannedScopeAttributeAndMethods2(cdType, grammarSymbol);
    }
  }

  // TODO Ersetze diese Methode durch eine Methode in cdTransformations
  private  Optional<ASTCDAttribute>  addCdAttributeUsingDefinition(ASTCDType cdType, String def) {
    if (cdType instanceof ASTCDClass) {
      return cdTransformation.addCdAttributeUsingDefinition((ASTCDClass) cdType, def);
    }
    if (cdType instanceof ASTCDInterface) {
      return cdTransformation.addCdAttributeUsingDefinition((ASTCDInterface) cdType, def);
    }
    return Optional.empty();
  }

  @Deprecated //Delete this and replace with addSymbolAttributeAndMethods2(..)
  protected void addSymbolAttributeAndMethods(ASTCDType cdType, String name, MCGrammarSymbol grammarSymbol) {
    String symbolName = name + AstGeneratorHelper.SYMBOL;
    String qualifiedName = grammarSymbol.getFullName().toLowerCase() + "." +
        SymbolTableGenerator.PACKAGE + "." + symbolName;
    symbolName = Character.toLowerCase(symbolName.charAt(0)) + symbolName.substring(1);

    Optional<ASTCDAttribute> symbolAttribute = addCdAttributeUsingDefinition(cdType,
        "<<" + GeneratorHelper.SYMBOL +
            ">> protected Optional<" + qualifiedName + "> " + symbolName + ";");

    addGetter(cdType, symbolAttribute.get());
    addOptionalGetMethods(cdType, symbolAttribute.get(), symbolName);
    addSetter(cdType, symbolAttribute.get());
    addOptionalSetMethods(cdType, symbolAttribute.get(), symbolName);
  }
  
  protected void addSymbolAttributeAndMethods2(ASTCDType cdType, String name, MCGrammarSymbol grammarSymbol) {
    String symbolName = name + AstGeneratorHelper.SYMBOL;
    String qualifiedName = grammarSymbol.getFullName().toLowerCase() + "." +
        SymbolTableGenerator.PACKAGE + "." + symbolName;
    symbolName = "symbol"+"2"; //TODO: Remove 2

    Optional<ASTCDAttribute> symbolAttribute = addCdAttributeUsingDefinition(cdType,
        "<<" + GeneratorHelper.SYMBOL +
            ">> protected Optional<" + qualifiedName + "> " + symbolName + ";");

    addGetter(cdType, symbolAttribute.get());
    addOptionalGetMethods(cdType, symbolAttribute.get(), symbolName);
    addSetter(cdType, symbolAttribute.get());
    addOptionalSetMethods(cdType, symbolAttribute.get(), symbolName);
  }
  
  protected void addEnclosingScopeAttributeAndMethods2(ASTCDType cdType, String qualifiedName, MCGrammarSymbol grammarSymbol) {
    String scopeName = "enclosingScope" +"2"; //TODO: Remove 2

    Optional<ASTCDAttribute> scopeAttribute = addCdAttributeUsingDefinition(cdType,
        "<<" + GeneratorHelper.SCOPE +
            ">> protected Optional<" + qualifiedName + "> " + scopeName + ";");

    addGetter(cdType, scopeAttribute.get());
    addOptionalGetMethods(cdType, scopeAttribute.get(), scopeName);
    addSetter(cdType, scopeAttribute.get());
    addOptionalSetMethods(cdType, scopeAttribute.get(), scopeName);
  }
  
  @Deprecated
  protected void addSpannedScopeAttributeAndMethods(ASTCDType cdType,MCGrammarSymbol grammarSymbol) {
    String scopeName = grammarSymbol.getName() + AstGeneratorHelper.SCOPE;
    String qualifiedName = grammarSymbol.getFullName().toLowerCase() + "." +
        SymbolTableGenerator.PACKAGE + "." + scopeName;
    scopeName = "spanned" + scopeName;

    Optional<ASTCDAttribute> scopeAttribute = addCdAttributeUsingDefinition(cdType,
        "<<" + GeneratorHelper.SCOPE +
            ">> protected Optional<" + qualifiedName + "> " + scopeName + ";");

    addGetter(cdType, scopeAttribute.get());
    addOptionalGetMethods(cdType, scopeAttribute.get(), scopeName);
    addSetter(cdType, scopeAttribute.get());
    addOptionalSetMethods(cdType, scopeAttribute.get(), scopeName);
  }
  
  protected void addSpannedScopeAttributeAndMethods2(ASTCDType cdType, MCGrammarSymbol grammarSymbol) {
    String scopeName = "I" + grammarSymbol.getName() + AstGeneratorHelper.SCOPE;
    String qualifiedName = grammarSymbol.getFullName().toLowerCase() + "." +
        SymbolTableGenerator.PACKAGE + "." + scopeName;
    scopeName = "spannedScope" +"2"; //TODO: Remove 2

    Optional<ASTCDAttribute> scopeAttribute = addCdAttributeUsingDefinition(cdType,
        "<<" + GeneratorHelper.SCOPE +
            ">> protected Optional<" + qualifiedName + "> " + scopeName + ";");

    addGetter(cdType, scopeAttribute.get());
    addOptionalGetMethods(cdType, scopeAttribute.get(), scopeName);
    addSetter(cdType, scopeAttribute.get());
    addOptionalSetMethods(cdType, scopeAttribute.get(), scopeName);
  }

  protected void addReferencedSymbolAttributes(ASTCDClass clazz, AstGeneratorHelper astHelper) {
    List<ASTCDAttribute> attributes = Lists.newArrayList(clazz.getCDAttributeList());
    for (ASTCDAttribute attribute : attributes) {
      if (!GeneratorHelper.isInherited(attribute)
          && GeneratorHelper.isReferencedSymbolAttribute(attribute)) {
        String referencedSymbol = astHelper.getReferencedSymbolName(attribute);

        String symbolName = getSimpleName(referencedSymbol).substring(0, getSimpleName(referencedSymbol).indexOf("Symbol"));
        Optional<ASTCDAttribute> astcdAttribute;
        if (GeneratorHelper.isListType(attribute.printType())) {
          astcdAttribute = cdTransformation.addCdAttribute(clazz, attribute.getName() + "Map", "java.util.Map<String, Optional<" + referencedSymbol + ">>", "private");
        }else {
          astcdAttribute = cdTransformation.addCdAttribute(clazz, attribute.getName() + "Symbol", "Optional<" + referencedSymbol + ">", "private");
        }
        Preconditions.checkArgument(astcdAttribute.isPresent());
      }
    }
  }


  protected void addReferencedSymbolAndDefinitionMethods(ASTCDClass clazz, AstGeneratorHelper astHelper) {
    List<ASTCDAttribute> attributes = Lists.newArrayList(clazz.getCDAttributeList());
    for (ASTCDAttribute attribute : attributes) {
      if (!GeneratorHelper.isInherited(attribute)
          && GeneratorHelper.isReferencedSymbolAttribute(attribute)) {
        String referencedSymbol = astHelper.getReferencedSymbolName(attribute);
        if (GeneratorHelper.isListType(attribute.printType())) {
          //if the attribute is a list
          addReferencedDefinitionListMethods(attribute, referencedSymbol, astHelper, clazz);
          addReferencedSymbolListMethods(attribute, referencedSymbol, clazz);
        } else {
          //if the attribute is mandatory or optional
          addReferencedDefinitionOptMethods(attribute.getName(), referencedSymbol, astHelper, clazz);
          addReferencedSymbolOptMethods(attribute, referencedSymbol, clazz);
        }
      }
    }
  }

  protected void addReferencedSymbolOptMethods(ASTCDAttribute attribute, String referencedSymbol, ASTCDClass clazz) {
    String symboName = StringTransformations.capitalize(attribute.getName()) + "Symbol";
    String simpleSymbolName = getSimpleName(referencedSymbol).substring(0, getSimpleName(referencedSymbol).indexOf("Symbol"));

    HookPoint getMethodBody = new TemplateHookPoint(
        "ast.symbolreferencemethods.GetReferencedSymbol",
        attribute.getName());
    replaceMethodBodyTemplate(clazz, String.format(AstOptionalGetMethods.get.getDeclaration(), referencedSymbol, symboName), getMethodBody);

    HookPoint getMethodBodyOpt = new TemplateHookPoint(
        "ast.symbolreferencemethods.GetReferencedSymbolOpt",
        attribute.getName(), simpleSymbolName, GeneratorHelper.isOptional(attribute));
    replaceMethodBodyTemplate(clazz, String.format(AstOptionalGetMethods.getOpt.getDeclaration(), referencedSymbol, symboName), getMethodBodyOpt);

    HookPoint getMethodBodyIsPresent = new TemplateHookPoint(
        "ast.symbolreferencemethods.IsPresentReferencedSymbol",
        attribute.getName());
    replaceMethodBodyTemplate(clazz, String.format(AstOptionalGetMethods.isPresent.getDeclaration(), symboName), getMethodBodyIsPresent);
  }

  public void addReferencedSymbolListMethods(ASTCDAttribute attribute, String referencedSymbol, ASTCDClass clazz) {
    //generation of normal getter
    String simpleSymbolName = getSimpleName(referencedSymbol).substring(0, getSimpleName(referencedSymbol).indexOf("Symbol"));
    String attributeName = attribute.getName();
    String methodNameGetList = "get" + StringTransformations.capitalize(attributeName) + "SymbolList";
    String toParseOpt = "public " + "java.util.List<Optional<" + referencedSymbol + ">>" + " "
        + methodNameGetList + "() ;";
    HookPoint getMethodBodyOpt = new TemplateHookPoint(
        "ast.symbolreferencemethods.GetReferencedSymbolList.ftl",
        attributeName, simpleSymbolName, referencedSymbol);
    replaceMethodBodyTemplate(clazz, toParseOpt, getMethodBodyOpt);
    //generation of other list methods
    String typeName = "Optional<" + referencedSymbol + ">";
    String attrName = GeneratorHelper.getSimpleListName(attribute) + "Symbol";
    String listName = StringTransformations.capitalize(attribute.getName()) + "Symbol";
    ASTCDAttribute attributeSymbol = attribute.deepClone();
    attributeSymbol.setName("get" + StringTransformations.capitalize(attributeName) + "SymbolList()");
    addListGetterMethods(clazz, attributeSymbol, typeName, listName, attrName);
  }

  public void addReferencedDefinitionOptMethods(String attributeName, String referencedSymbol, AstGeneratorHelper astHelper, ASTCDClass clazz) {
    String symbolName = getSimpleName(referencedSymbol).substring(0, getSimpleName(referencedSymbol).indexOf("Symbol"));
    String referencedNode = referencedSymbol.substring(0, referencedSymbol.lastIndexOf("_symboltable")) + GeneratorHelper.AST_PACKAGE_SUFFIX_DOT + GeneratorHelper.AST_PREFIX + symbolName;
    String definitionName = StringTransformations.capitalize(attributeName) + "Definition";

    HookPoint getMethodBody = new TemplateHookPoint(
        "ast.symbolreferencemethods.GetReferencedDefinition",
        attributeName, referencedSymbol);
    replaceMethodBodyTemplate(clazz, String.format(AstOptionalGetMethods.get.getDeclaration(), referencedNode, definitionName), getMethodBody);

    HookPoint getMethodBodyOpt = new TemplateHookPoint(
        "ast.symbolreferencemethods.GetReferencedDefinitionOpt",
        attributeName, referencedSymbol);
    replaceMethodBodyTemplate(clazz, String.format(AstOptionalGetMethods.getOpt.getDeclaration(), referencedNode, definitionName), getMethodBodyOpt);

    HookPoint getMethodBodyIsPresent = new TemplateHookPoint(
        "ast.symbolreferencemethods.IsPresentReferencedDefinition",
        attributeName);
    replaceMethodBodyTemplate(clazz, String.format(AstOptionalGetMethods.isPresent.getDeclaration(), definitionName), getMethodBodyIsPresent);
  }

  public void addReferencedDefinitionListMethods(ASTCDAttribute attribute, String referencedSymbol, AstGeneratorHelper astHelper, ASTCDClass clazz) {
    String attributeName = StringTransformations.capitalize(attribute.getName());
    String symbolName = getSimpleName(referencedSymbol).substring(0, getSimpleName(referencedSymbol).indexOf("Symbol"));
    String referencedNode = referencedSymbol.substring(0, referencedSymbol.lastIndexOf("_symboltable")) + GeneratorHelper.AST_PACKAGE_SUFFIX_DOT + GeneratorHelper.AST_PREFIX + symbolName;

    String methodNameGet = "get" + attributeName + "DefinitionList";
    String toParse = "public java.util.List<Optional<" + referencedNode + ">> " + methodNameGet + "() ;";
    HookPoint getMethodBody = new TemplateHookPoint(
        "ast.symbolreferencemethods.GetReferencedDefinitionList",
        attribute.getName(), referencedSymbol, referencedNode);
    replaceMethodBodyTemplate(clazz, toParse, getMethodBody);

    String typeName = "Optional<" + referencedNode + ">";
    String attrName = GeneratorHelper.getSimpleListName(attribute) + "Definition";
    String listName = StringTransformations.capitalize(attribute.getName()) + "Definition";
    ASTCDAttribute attributeDefinition = attribute.deepClone();
    attributeDefinition.setName("get" + attributeName + "DefinitionList()");
    addListGetterMethods(clazz, attributeDefinition, typeName, listName, attrName);
  }

  /**
   * Adds common ast methods to the all classes in the class diagram
   *
   * @param clazz     - each entry contains a class diagram class and a respective
   *                  builder class
   * @param astHelper
   * @throws ANTLRException
   */
  protected void addAdditionalMethods(ASTCDClass clazz,
                                      AstGeneratorHelper astHelper) {
    if (astHelper.isAstClass(clazz)) {
      AstAdditionalMethods additionalMethod = AstAdditionalMethods.accept;
      String visitorTypeFQN = VisitorGeneratorHelper.getQualifiedVisitorType(
          astHelper.getPackageName(), astHelper.getCdName());
      String methodSignatur = String.format(additionalMethod.getDeclaration(), visitorTypeFQN);
      replaceMethodBodyTemplate(clazz, methodSignatur, new TemplateHookPoint(
          "ast.additionalmethods.Accept", clazz));

      // node needs to accept visitors from all super languages
      for (CDDefinitionSymbol cdSym : astHelper.getAllSuperCds(astHelper.getCd())) {
        String superGrammarName = Names.getSimpleName(cdSym.getFullName());
        String visitorType = superGrammarName + "Visitor";
        String visitorPackage = VisitorGeneratorHelper.getVisitorPackage(cdSym.getFullName());

        additionalMethod = AstAdditionalMethods.accept;
        String superVisitorTypeFQN = visitorPackage + "." + visitorType;
        methodSignatur = String.format(additionalMethod.getDeclaration(), superVisitorTypeFQN);
        replaceMethodBodyTemplate(clazz, methodSignatur, new TemplateHookPoint(
            "ast.additionalmethods.AcceptSuper", clazz, astHelper.getQualifiedCdName(),
            visitorTypeFQN, superVisitorTypeFQN));
      }
    }

    Optional<ASTModifier> modifier = clazz.getModifierOpt();
    String plainClassName = GeneratorHelper.getPlainName(clazz);
    Optional<CDTypeSymbol> symbol = astHelper.getCd().getType(plainClassName);
    if (!symbol.isPresent()) {
      Log.error("0xA1062 CdDecorator error: Can't find symbol for class " + plainClassName);
    }

    ASTCDMethod meth = replaceMethodBodyTemplate(clazz, AstAdditionalMethods.get_Children.getDeclaration(),
        new TemplateHookPoint("ast.additionalmethods.GetChildren", symbol.get()));

    addDeprecatedStereotype(meth, Optional.empty());

    replaceMethodBodyTemplate(clazz, AstAdditionalMethods.deepEqualsWithOrder.getDeclaration(),
        new TemplateHookPoint("ast.additionalmethods.DeepEqualsWithOrder", clazz));

    replaceMethodBodyTemplate(clazz, AstAdditionalMethods.deepEquals.getDeclaration(),
        new StringHookPoint("return deepEquals(o, true);\n"));

    replaceMethodBodyTemplate(clazz,
        AstAdditionalMethods.deepEqualsWithCommentsWithOrder.getDeclaration(),
        new TemplateHookPoint("ast.additionalmethods.DeepEqualsWithComments", clazz));

    replaceMethodBodyTemplate(clazz,
        AstAdditionalMethods.deepEqualsWithComments.getDeclaration(),
        new StringHookPoint("return deepEqualsWithComments(o, true);\n"));

    replaceMethodBodyTemplate(clazz, AstAdditionalMethods.equalAttributes.getDeclaration(),
        new TemplateHookPoint("ast.additionalmethods.EqualAttributes", clazz));

    replaceMethodBodyTemplate(clazz, AstAdditionalMethods.equalsWithComments.getDeclaration(),
        new TemplateHookPoint("ast.additionalmethods.EqualsWithComments", clazz));

    String stringToParse = String.format(AstAdditionalMethods.deepClone.getDeclaration(),
        plainClassName);
    replaceMethodBodyTemplate(clazz, stringToParse,
        new StringHookPoint("return deepClone(_construct());\n"));

    stringToParse = String.format(AstAdditionalMethods.deepCloneWithOrder.getDeclaration(),
        plainClassName, plainClassName);
    replaceMethodBodyTemplate(clazz, stringToParse,
        new TemplateHookPoint("ast.additionalmethods.DeepCloneWithParameters", clazz));

    if (modifier.isPresent() && modifier.get().isAbstract()) {
      stringToParse = String.format(AstAdditionalMethods._construct.getDeclaration(), "abstract "
          + plainClassName);
      cdTransformation.addCdMethodUsingDefinition(clazz, stringToParse);
    } else {
      stringToParse = String.format(AstAdditionalMethods._construct.getDeclaration(),
          plainClassName);
      replaceMethodBodyTemplate(clazz, stringToParse,
          new StringHookPoint(
              "return " + astHelper.getCdName() + "NodeFactory.create" + plainClassName + "();\n"));
    }
  }

  protected void addDeprecatedStereotype(ASTCDMethod meth, Optional<String> message) {
    ASTCDStereotype stereo;
    if (meth.getModifier().isPresentStereotype()) {
      stereo = meth.getModifier().getStereotype();
    } else {
      stereo = CD4AnalysisMill.cDStereotypeBuilder().build();
      meth.getModifier().setStereotype(stereo);
    }
    ASTCDStereoValue value = CD4AnalysisMill.cDStereoValueBuilder().setName("@Deprecated").build();
    stereo.getValueList().add(value);
  }

  protected void addDeprecatedStereotype(ASTCDType cdClass, Optional<String> message) {
    ASTCDStereotype stereo;
    if (cdClass.getModifierOpt().isPresent() && cdClass.getModifierOpt().get().isPresentStereotype()) {
      stereo = cdClass.getModifierOpt().get().getStereotype();
    } else {
      stereo = CD4AnalysisMill.cDStereotypeBuilder().build();
      cdClass.getModifierOpt().get().setStereotype(stereo);
    }
    ASTCDStereoValue value = CD4AnalysisMill.cDStereoValueBuilder().setName("@Deprecated").build();
    stereo.getValueList().add(value);
  }

  /**
   * Adds common ast methods to the all classes in the class diagram
   *
   * @param clazz     - each entry contains a class diagram class and a respective
   *                  builder class
   * @param astHelper
   * @throws ANTLRException
   */
  protected void addListMethods(ASTCDClass clazz,
                                AstGeneratorHelper astHelper, ASTCDDefinition cdDefinition) {
    boolean isBuilderClass = AstGeneratorHelper.isBuilderClass(cdDefinition, clazz);
    if (astHelper.isAstClass(clazz) || isBuilderClass) {
      List<ASTCDAttribute> attributes = Lists.newArrayList(clazz.getCDAttributeList());
      for (ASTCDAttribute attribute : attributes) {
        if (!astHelper.isListType(CollectionTypesPrinter.printType(attribute.getMCType()))) {
          continue;
        }
        Optional<ASTMCType> type = MCCollectionTypesHelper
            .getFirstTypeArgumentOfGenericType(attribute.getMCType(), GeneratorHelper.JAVA_LIST).get().getMCTypeOpt();
        if (!type.isPresent()) {
          continue;
        }
        String typeName = new AstPrinter().printType(type.get());
        String attrName = GeneratorHelper.getSimpleListName(attribute);
        String listName = StringTransformations.capitalize(attribute.getName());
        if (!GeneratorHelper.isInherited(attribute)) {
          addListSetterMethods(clazz, attribute, typeName, listName, attrName, isBuilderClass, false);
          addListGetterMethods(clazz, attribute, typeName, listName, attrName);
        } else if (isBuilderClass) {
          addListSetterMethods(clazz, attribute, typeName, listName, attrName, isBuilderClass, true);
        }
      }
    }
  }

  /**
   * Adds common ast methods to the interface in the class diagram
   *
   * @param interf    - each entry contains a class diagram interface and a respective
   *                  builder class
   * @param astHelper
   * @throws ANTLRException
   */
  protected void addListMethods(ASTCDInterface interf,
                                AstGeneratorHelper astHelper) {
    if (astHelper.isAstInterface(interf)) {
      List<ASTCDAttribute> attributes = Lists.newArrayList(interf.getCDAttributeList());
      for (ASTCDAttribute attribute : attributes) {
        if (!astHelper.isListType(CollectionTypesPrinter.printType(attribute.getMCType()))) {
          continue;
        }
        Optional<ASTMCType> type = MCCollectionTypesHelper
            .getFirstTypeArgumentOfGenericType(attribute.getMCType(), GeneratorHelper.JAVA_LIST).get().getMCTypeOpt();
        if (!type.isPresent()) {
          continue;
        }
        String typeName = new AstPrinter().printType(type.get());
        String attrName = GeneratorHelper.getSimpleListName(attribute);
        String listName = StringTransformations.capitalize(attribute.getName());
        addListGetterMethods(interf, attribute, typeName, listName, attrName);
        addListSetterMethods(interf, attribute, typeName, listName, attrName, false, false);
      }
    }
  }

  protected void addListSetterMethods(ASTCDType type, ASTCDAttribute attribute, String typeName,
                                      String listName, String attrName, boolean isBuilderClass, boolean isInherited) {
    String methodSignatur = String.format(AstListMethods.clear.getMethodDeclaration(),
        isBuilderClass ? type.getName() : "void", listName);
    additionalMethodForListAttribute(type, AstListMethods.clear.getMethodName(), attribute,
        methodSignatur, isBuilderClass, isInherited, false);

    methodSignatur = String.format(AstListMethods.add.getMethodDeclaration(),
        isBuilderClass ? type.getName() : "boolean", attrName, typeName);
    additionalMethodForListAttribute(type, AstListMethods.add.getMethodName(), attribute,
        methodSignatur, isBuilderClass, isInherited, false);

    methodSignatur = String.format(AstListMethods.addAll.getMethodDeclaration(),
        isBuilderClass ? type.getName() : "boolean", listName, typeName);
    additionalMethodForListAttribute(type, AstListMethods.addAll.getMethodName(), attribute,
        methodSignatur, isBuilderClass, isInherited, false);

    methodSignatur = String.format(AstListMethods.remove.getMethodDeclaration(),
        isBuilderClass ? type.getName() : "boolean", attrName);
    additionalMethodForListAttribute(type, AstListMethods.remove.getMethodName(), attribute,
        methodSignatur, isBuilderClass, isInherited, false);

    methodSignatur = String.format(AstListMethods.removeAll.getMethodDeclaration(),
        isBuilderClass ? type.getName() : "boolean", listName);
    additionalMethodForListAttribute(type, AstListMethods.removeAll.getMethodName(), attribute,
        methodSignatur, isBuilderClass, isInherited, false);

    methodSignatur = String.format(AstListMethods.retainAll.getMethodDeclaration(),
        isBuilderClass ? type.getName() : "boolean", listName);
    additionalMethodForListAttribute(type, AstListMethods.retainAll.getMethodName(), attribute,
        methodSignatur, isBuilderClass, isInherited, false);

    methodSignatur = String.format(AstListMethods.removeIf.getMethodDeclaration(),
        isBuilderClass ? type.getName() : "boolean", attrName, typeName);
    additionalMethodForListAttribute(type, AstListMethods.removeIf.getMethodName(), attribute,
        methodSignatur, isBuilderClass, isInherited, false);

    methodSignatur = String.format(AstListMethods.forEach.getMethodDeclaration(),
        isBuilderClass ? type.getName() : "void", listName, typeName);
    additionalMethodForListAttribute(type, AstListMethods.forEach.getMethodName(), attribute,
        methodSignatur, isBuilderClass, isInherited, false);

    methodSignatur = String.format(AstListMethods.add_.getMethodDeclaration(),
        isBuilderClass ? type.getName() : "void", attrName, typeName);
    additionalMethodForListAttribute(type, AstListMethods.add_.getMethodName(), attribute,
        methodSignatur, isBuilderClass, isInherited, false);

    methodSignatur = String.format(AstListMethods.addAll_.getMethodDeclaration(),
        isBuilderClass ? type.getName() : "boolean", listName, typeName);
    additionalMethodForListAttribute(type, AstListMethods.addAll_.getMethodName(), attribute,
        methodSignatur, isBuilderClass, isInherited, false);

    methodSignatur = String.format(AstListMethods.replaceAll.getMethodDeclaration(),
        isBuilderClass ? type.getName() : "void", listName, typeName);
    additionalMethodForListAttribute(type, AstListMethods.replaceAll.getMethodName(),
        attribute, methodSignatur, isBuilderClass, isInherited, false);

    methodSignatur = String.format(AstListMethods.sort.getMethodDeclaration(),
        isBuilderClass ? type.getName() : "void", listName, typeName);
    additionalMethodForListAttribute(type, AstListMethods.sort.getMethodName(), attribute,
        methodSignatur, isBuilderClass, isInherited, false);

    methodSignatur = String.format(AstListMethods.remove_.getMethodDeclaration(),
        isBuilderClass ? type.getName() : typeName, attrName, typeName);
    additionalMethodForListAttribute(type, AstListMethods.remove_.getMethodName(), attribute,
        methodSignatur, isBuilderClass, isInherited, false);

    methodSignatur = String.format(AstListMethods.set_.getMethodDeclaration(),
        isBuilderClass ? type.getName() : typeName, attrName, typeName);
    additionalMethodForListAttribute(type, AstListMethods.set_.getMethodName(), attribute,
        methodSignatur, isBuilderClass, isInherited, false);
  }

  protected void addListGetterMethods(ASTCDType type, ASTCDAttribute attribute, String typeName,
                                      String listName, String attrName) {
    String methodSignatur = String.format(AstListMethods.contains.getMethodDeclaration(), attrName);
    additionalGetterForListAttribute(type, AstListMethods.contains.getMethodName(), attribute,
        methodSignatur);

    methodSignatur = String.format(AstListMethods.containsAll.getMethodDeclaration(),
        listName);
    additionalGetterForListAttribute(type, AstListMethods.containsAll.getMethodName(),
        attribute,
        methodSignatur);

    methodSignatur = String.format(AstListMethods.isEmpty.getMethodDeclaration(),
        listName);
    additionalGetterForListAttribute(type, AstListMethods.isEmpty.getMethodName(), attribute,
        methodSignatur);

    methodSignatur = String.format(AstListMethods.iterator.getMethodDeclaration(),
        typeName, listName);
    additionalGetterForListAttribute(type, AstListMethods.iterator.getMethodName(), attribute,
        methodSignatur);

    methodSignatur = String.format(AstListMethods.size.getMethodDeclaration(),
        listName);
    additionalGetterForListAttribute(type, AstListMethods.size.getMethodName(), attribute,
        methodSignatur);

    methodSignatur = String.format(AstListMethods.toArray.getMethodDeclaration(),
        typeName, listName, typeName);
    additionalGetterForListAttribute(type, AstListMethods.toArray.getMethodName(), attribute,
        methodSignatur);

    methodSignatur = String.format(AstListMethods.toArray_.getMethodDeclaration(),
        listName);
    additionalGetterForListAttribute(type, AstListMethods.toArray_.getMethodName(), attribute,
        methodSignatur);

    methodSignatur = String.format(AstListMethods.spliterator.getMethodDeclaration(),
        typeName, listName);
    additionalGetterForListAttribute(type, AstListMethods.spliterator.getMethodName(),
        attribute, methodSignatur);

    methodSignatur = String.format(AstListMethods.stream.getMethodDeclaration(),
        typeName, listName);
    additionalGetterForListAttribute(type, AstListMethods.stream.getMethodName(), attribute,
        methodSignatur);

    methodSignatur = String.format(AstListMethods.parallelStream.getMethodDeclaration(),
        typeName, listName);
    additionalGetterForListAttribute(type, AstListMethods.parallelStream.getMethodName(),
        attribute, methodSignatur);

    methodSignatur = String.format(AstListMethods.get.getMethodDeclaration(),
        typeName, attrName);
    additionalGetterForListAttribute(type, AstListMethods.get.getMethodName(), attribute,
        methodSignatur);

    methodSignatur = String.format(AstListMethods.indexOf.getMethodDeclaration(),
        attrName, typeName);
    additionalGetterForListAttribute(type, AstListMethods.indexOf.getMethodName(), attribute,
        methodSignatur);

    methodSignatur = String.format(AstListMethods.lastIndexOf.getMethodDeclaration(),
        attrName);
    additionalGetterForListAttribute(type, AstListMethods.lastIndexOf.getMethodName(),
        attribute, methodSignatur);

    methodSignatur = String.format(AstListMethods.equals.getMethodDeclaration(),
        listName);
    additionalGetterForListAttribute(type, AstListMethods.equals.getMethodName(), attribute,
        methodSignatur);

    methodSignatur = String.format(AstListMethods.hashCode.getMethodDeclaration(),
        listName);
    additionalGetterForListAttribute(type, AstListMethods.hashCode.getMethodName(), attribute,
        methodSignatur);

    methodSignatur = String.format(AstListMethods.listIterator.getMethodDeclaration(),
        typeName, listName);
    additionalGetterForListAttribute(type, AstListMethods.listIterator.getMethodName(),
        attribute,
        methodSignatur);

    methodSignatur = String.format(AstListMethods.listIterator_.getMethodDeclaration(),
        typeName, listName);
    additionalGetterForListAttribute(type, AstListMethods.listIterator_.getMethodName(),
        attribute,
        methodSignatur);

    methodSignatur = String.format(AstListMethods.subList.getMethodDeclaration(),
        typeName, listName);
    additionalGetterForListAttribute(type, AstListMethods.subList.getMethodName(), attribute,
        methodSignatur);
  }


  /**
   * Adds common ast methods to the all classes in the class diagram
   *
   * @param clazz     - each entry contains a class diagram class and a respective
   *                  builder class
   * @param astHelper
   * @throws ANTLRException
   */
  protected void addOptionalMethods(ASTCDClass clazz,
                                    AstGeneratorHelper astHelper, ASTCDDefinition cdDefinition) {
    for (ASTCDAttribute attribute : clazz.getCDAttributeList()) {
      boolean isBuilderClass = AstGeneratorHelper.isBuilderClass(cdDefinition, clazz);
      if (!astHelper.isOptional(attribute)) {
        continue;
      }
      String nativeName = StringTransformations.capitalize(GeneratorHelper.getNativeAttributeName(attribute.getName()));
      if (!astHelper.isInherited(attribute)) {
        addOptionalGetMethods(clazz, attribute, nativeName);
        addOptionalSetMethods(clazz, attribute, nativeName, isBuilderClass, false);
      } else if (isBuilderClass) {
        addOptionalSetMethods(clazz, attribute, nativeName, isBuilderClass, true);
      }
    }
  }

  /**
   * Adds common ast methods to the all interfaces in the class diagram
   *
   * @param interf    - each entry contains a class diagram intreface and a respective
   *                  builder class
   * @param astHelper
   * @throws ANTLRException
   */
  protected void addOptionalMethods(ASTCDInterface interf,
                                    AstGeneratorHelper astHelper) {
    for (ASTCDAttribute attribute : interf.getCDAttributeList()) {
      if (!astHelper.isOptional(attribute)) {
        continue;
      }
      String nativeName = StringTransformations.capitalize(GeneratorHelper.getNativeAttributeName(attribute.getName()));
      addOptionalGetMethods(interf, attribute, nativeName);
      addOptionalSetMethods(interf, attribute, nativeName);

    }
  }

  protected void addOptionalSetMethods(ASTCDType type, ASTCDAttribute attribute, String nativeName) {
    addOptionalSetMethods(type, attribute, nativeName, false, false);
  }

  protected void addOptionalSetMethods(ASTCDType type, ASTCDAttribute attribute, String nativeName,
                                       boolean isBuilderClass, boolean isInherited) {
    nativeName = StringTransformations.capitalize(nativeName);
    String returnType = isBuilderClass ? type.getName() : "void";
    String methodSetAbsent = "set" + nativeName + "Absent";
    String toParse = "public " + returnType + " " + methodSetAbsent + "() ;";
    HookPoint getMethodBody = new TemplateHookPoint("ast.additionalmethods.SetAbsent",
        GeneratorHelper.getJavaAndCdConformName(attribute.getName()), isBuilderClass, isInherited, GeneratorHelper.isReferencedSymbolAttribute(attribute));
    replaceMethodBodyTemplate(type, toParse, getMethodBody);

    String methodSetOpt = "set" + nativeName + "Opt";
    toParse = "public " + returnType + " " + methodSetOpt + "(" + CollectionTypesPrinter.printType(attribute.getMCType()) + " value) ;";
    getMethodBody = new TemplateHookPoint("ast.additionalmethods.SetOpt",
        GeneratorHelper.getJavaAndCdConformName(attribute.getName()), isBuilderClass, isInherited, GeneratorHelper.isReferencedSymbolAttribute(attribute));
    replaceMethodBodyTemplate(type, toParse, getMethodBody);
  }

  protected void addOptionalGetMethods(ASTCDType type, ASTCDAttribute attribute, String nativeName) {
    nativeName = StringTransformations.capitalize(nativeName);
    String methodName = GeneratorHelper.getPlainGetter(attribute);
    String returnType = CollectionTypesPrinter.printType(MCCollectionTypesHelper.getFirstTypeArgumentOfOptional(attribute.getMCType()).get());

    HookPoint getMethodBody = new TemplateHookPoint("ast.additionalmethods.GetOpt", type, methodName);
    replaceMethodBodyTemplate(type, String.format(AstOptionalGetMethods.get.getDeclaration(), returnType, nativeName), getMethodBody);

    getMethodBody = new TemplateHookPoint("ast.additionalmethods.IsPresent", methodName);
    replaceMethodBodyTemplate(type, String.format(AstOptionalGetMethods.isPresent.getDeclaration(), nativeName), getMethodBody);
  }

  /**
   * Decorates class diagram with builder pattern for all classes excepting
   * lists
   *
   * @param cdDefinition
   * @param astHelper
   * @throws ANTLRException
   */
  protected void addBuilders(ASTCDDefinition cdDefinition, AstGeneratorHelper astHelper) {
    Lists.newArrayList(cdDefinition.getCDClassList()).stream()
        .forEach(c -> {
          Optional<ASTCDClass> clazz = cdTransformation.addCdClassUsingDefinition(
              cdDefinition,
              "public class " + AstGeneratorHelper.getNameOfBuilderClass(c) + " ;");
          if (clazz.isPresent()) {
            c.getCDAttributeList().stream().forEach(a -> cdTransformation.addCdAttribute(clazz.get(), a));
            ASTMCObjectType superClass;
            if (c.isPresentSuperclass()) {
              superClass = TransformationHelper.createType(c.printSuperClass() + "Builder");
            } else {
              superClass = TransformationHelper.createType("de.monticore.ast.ASTNodeBuilder", clazz.get().getName());
            }
            clazz.get().setSuperclass(superClass);
          }
        });
  }

  protected void addSuperInterfaces(ASTCDClass clazz) {
    String interfaces = clazz.printInterfaces();
    if (!interfaces.isEmpty()) {
      glex.replaceTemplate("ast.AstSuperInterfaces", clazz,
          new StringHookPoint(interfaces + DEL));
    }
  }

  /**
   * Makes the AST class abstract if it's a super class of an HW type
   *
   * @param clazz
   */
  protected void makeAbstractIfHWC(ASTCDClass clazz) {
    if (AstGeneratorHelper.isSupertypeOfHWType(clazz.getName())) {
      Optional<ASTModifier> previousModifier = clazz.getModifierOpt();
      ASTModifier newModifier = previousModifier.isPresent()
          ? previousModifier.get()
          : CD4AnalysisNodeFactory
          .createASTModifier();
      newModifier.setAbstract(true);
      clazz.setModifier(newModifier);
    }
  }

  /**
   * @param clazz
   * @param astHelper
   * @throws ANTLRException
   */
  protected void addAdditionalAttributes(
      ASTCDClass clazz, AstGeneratorHelper astHelper) {
    // Add Symbol attribute
    cdTransformation.addCdAttributeUsingDefinition(clazz,
        AstAdditionalAttributes.symbol.getDeclaration());
    // Add Scope attribute
    cdTransformation.addCdAttributeUsingDefinition(clazz,
        AstAdditionalAttributes.enclosingScope.getDeclaration());

  }

  /**
   * Adds getter for all attributes of ast classes
   *
   * @param clazz
   * @param astHelper
   * @throws ANTLRException
   */
  protected void addGetter(ASTCDClass clazz, AstGeneratorHelper astHelper) {
    List<ASTCDAttribute> attributes = Lists.newArrayList(clazz.getCDAttributeList());
    // attributes.addAll(astHelper.getAttributesOfExtendedInterfaces(clazz));
    for (ASTCDAttribute attribute : attributes) {
      if (GeneratorHelper.isInherited(attribute)) {
        continue;
      }
      String methodName = GeneratorHelper.getPlainGetter(attribute);
      if (!clazz.getCDMethodList().stream()
          .filter(m -> methodName.equals(m.getName()) && m.getCDParameterList().isEmpty()).findAny()
          .isPresent()) {
        addGetter(clazz, attribute);
      }
    }
  }

  protected void addGetter(ASTCDType type, ASTCDAttribute attribute) {
    String methodName = GeneratorHelper.getPlainGetter(attribute);
    String toParse = "public " + CollectionTypesPrinter.printType(attribute.getMCType()) + " "
        + methodName + "() ;";
    HookPoint getMethodBody = new TemplateHookPoint("ast.additionalmethods.Get",
        type, attribute.getName());
    replaceMethodBodyTemplate(type, toParse, getMethodBody);
  }

  /**
   * @param interf
   */
  protected void addGetter(ASTCDInterface interf) {
    for (ASTCDAttribute attribute : interf.getCDAttributeList()) {
      if (GeneratorHelper.isInherited(attribute)) {
        continue;
      }
      String methodName = GeneratorHelper.getPlainGetter(attribute);
      if (interf.getCDMethodList().stream()
          .filter(m -> methodName.equals(m.getName()) && m.getCDParameterList().isEmpty()).findAny()
          .isPresent()) {
        continue;
      }
      String toParse = "public " + CollectionTypesPrinter.printType(attribute.getMCType()) + " "
          + GeneratorHelper.getPlainGetter(attribute) + "();";
      cdTransformation.addCdMethodUsingDefinition(interf, toParse);
    }
  }

  /**
   * Adds getter for all attributes of ast classes
   *
   * @param clazz
   * @param astHelper
   * @throws ANTLRException
   */
  protected void addSetter(ASTCDClass clazz, AstGeneratorHelper astHelper, ASTCDDefinition cdDefinition) {
    for (ASTCDAttribute attribute : clazz.getCDAttributeList()) {
      String typeName = MCCollectionTypesHelper.printSimpleRefType(attribute.getMCType());
      boolean isBuilderClass = AstGeneratorHelper.isBuilderClass(cdDefinition, clazz);
      if (!AstGeneratorHelper.generateSetter(clazz, attribute, typeName) && !isBuilderClass) {
        continue;
      }
      boolean isInherited = GeneratorHelper.isInherited(attribute);

      addSetter(clazz, attribute, isBuilderClass, isInherited);
    }
  }

  protected void addSetter(ASTCDType type, ASTCDAttribute attribute) {
    addSetter(type, attribute, false, false);
  }

  protected void addSetter(ASTCDType type, ASTCDAttribute attribute, boolean isBuilderClass, boolean isInherited) {
    String typeName = MCCollectionTypesHelper.printSimpleRefType(attribute.getMCType());
    String attributeName = attribute.getName();
    String methodName = GeneratorHelper.getPlainSetter(attribute);
    boolean isOptional = GeneratorHelper.isOptional(attribute);
    String returnType = isBuilderClass ? type.getName() : "void";

    String toParse = "public " + returnType + " " + methodName + "("
        + typeName + " " + attributeName + ") ;";
    HookPoint methodBody = new TemplateHookPoint("ast.additionalmethods.Set",
        attribute, attributeName, isBuilderClass, isInherited, methodName);
    ASTCDMethod setMethod = replaceMethodBodyTemplate(type, toParse, methodBody);

    if (isOptional) {
      glex.replaceTemplate(ERROR_IFNULL_TEMPLATE, setMethod, new StringHookPoint(""));
    }
  }

  /**
   * Adds getter for all attributes of ast classes
   *
   * @param interf
   * @throws ANTLRException
   */
  protected void addSetter(ASTCDInterface interf) {
    for (ASTCDAttribute attribute : interf.getCDAttributeList()) {
      String methodName = GeneratorHelper.getPlainGetter(attribute);
      if (interf.getCDMethodList().stream()
              .filter(m -> methodName.equals(m.getName()) && m.getCDParameterList().isEmpty()).findAny()
              .isPresent()) {
        continue;
      }
      addSetter(interf, attribute);
    }
  }

  /**
   * @param cdCompilationUnit
   * @param nativeClasses
   * @param astHelper
   */
  protected void addNodeFactoryClass(ASTCDCompilationUnit cdCompilationUnit,
                                     List<ASTCDClass> nativeClasses, AstGeneratorHelper astHelper) {

    // Add factory-attributes for all ast classes
    Set<String> astClasses = new LinkedHashSet<>();
    nativeClasses.stream()
        .forEach(e -> astClasses.add(GeneratorHelper.getPlainName(e)));

    ASTCDClass nodeFactoryClass = createNodeFactoryClass(cdCompilationUnit, nativeClasses,
        astHelper, astClasses);

    List<String> imports = getImportsForNodeFactory(nodeFactoryClass, astClasses, astHelper);

    glex.replaceTemplate(CLASS_CONTENT_TEMPLATE, nodeFactoryClass, new TemplateHookPoint(
        "ast.AstNodeFactory", nodeFactoryClass, imports));

  }

  /**
   * Generate Mill class
   *
   * @param cdCompilationUnit
   * @param nativeClasses
   * @param astHelper
   */
  protected void addMillClass(ASTCDCompilationUnit cdCompilationUnit,
                              List<ASTCDClass> nativeClasses, AstGeneratorHelper astHelper) {

    Set<String> astClasses = new LinkedHashSet<>();
    nativeClasses.stream()
        .forEach(e -> astClasses.add(GeneratorHelper.getPlainName(e)));

    // CreateMill 
    ASTCDClass millClass = createMillClass(cdCompilationUnit, nativeClasses,
        astHelper);

    // Create Import
    String packageName = Names.getQualifiedName(cdCompilationUnit.getPackageList());
    String importPrefix = (packageName.isEmpty() ? "" : packageName + ".")
        + cdCompilationUnit.getCDDefinition().getName().toLowerCase()
        + AstGeneratorHelper.AST_DOT_PACKAGE_SUFFIX_DOT;
    List<String> imports = nativeClasses.stream().filter(c -> c.isPresentModifier())
        .filter(c -> !c.getModifier().isAbstract())
        .filter(c -> GeneratorHelper.getPlainName(c).startsWith(GeneratorHelper.AST_PREFIX))
        .map(c -> importPrefix + AstGeneratorHelper.getNameOfBuilderClass(c))
        .collect(Collectors.toList());

    glex.replaceTemplate(CLASS_CONTENT_TEMPLATE, millClass, new TemplateHookPoint(
        "ast.AstMill", millClass,
        millClass.isPresentModifier() && millClass.getModifier().isAbstract(),
        imports));

    // Create delegate methods for inherited classes
    List<String> delegateList = Lists.newArrayList(astClasses);
    for (CDDefinitionSymbol superCd : astHelper.getAllSuperCds(astHelper.getCdSymbol())) {
      for (CDTypeSymbol cdType : superCd.getTypes()) {
        Optional<ASTCDType> node = cdType.getAstNode();
        if (node.isPresent() && node.get() instanceof ASTCDClass) {
          if (!cdType.isAbstract() && !delegateList.contains(cdType.getName())) {
            delegateList.add(cdType.getName());
            String millName = getSimpleName(superCd.getName()) + MILL;
            String millPackage = superCd.getFullName().toLowerCase()
                + AstGeneratorHelper.AST_DOT_PACKAGE_SUFFIX_DOT;
            addDelegateMethodToMill(cdType.getName(), millClass, astHelper, superCd,
                millPackage + millName);
          }

        }
      }
    }

    // Compute diagrams with overridden classes
    HashMap<CDDefinitionSymbol, Collection<CDTypeSymbol>> overridden = Maps.newHashMap();
    Collection<CDTypeSymbol> firstClasses = Lists.newArrayList();
    calculateOverriddenCds(astHelper.getCdSymbol(), astClasses, overridden, firstClasses);

    // Create init for super class diagrams
    String toParse = "public static void init();";
    HookPoint methodBody = new TemplateHookPoint("ast.ASTMillInitMethod", cdCompilationUnit.getCDDefinition().getName(),
        GeneratorHelper.getPlainName(millClass, ""), overridden.keySet());
    replaceMethodBodyTemplate(millClass, toParse, methodBody);

    // Create reset for super class diagrams
    toParse = "public static void reset();";
    methodBody = new TemplateHookPoint("ast.ASTMillResetMethod", millClass, astHelper.getCdSymbol().getImports());
    replaceMethodBodyTemplate(millClass, toParse, methodBody);

    // Create Mill for overridden Rules
    List<String> importsIgnoredRules = Lists.newArrayList();
    importsIgnoredRules.add("de.se_rwth.commons.logging.Log");
    for (Entry<CDDefinitionSymbol, Collection<CDTypeSymbol>> e : overridden.entrySet()) {
      CDDefinitionSymbol symbol = e.getKey();
      String millForName = symbol.getName() + "MillFor" + cdCompilationUnit.getCDDefinition().getName();
      String millSuperName = getSimpleName(symbol.getName()) + MILL;
      String millSuperPackage = symbol.getFullName().toLowerCase()
          + AstGeneratorHelper.AST_DOT_PACKAGE_SUFFIX_DOT;
      millClass = createMillForSuperClass(cdCompilationUnit, millForName, symbol, e.getValue(), firstClasses, astHelper);
      glex.replaceTemplate(CLASS_CONTENT_TEMPLATE, millClass, new TemplateHookPoint(
          "ast.AstMillForSuper", millClass,
          millClass.isPresentModifier() && millClass.getModifier().isAbstract(),
          importsIgnoredRules, millSuperPackage + millSuperName));
    }
  }


  protected void calculateOverriddenCds(CDDefinitionSymbol cd,
                                        Collection<String> nativeClasses, HashMap<CDDefinitionSymbol, Collection<CDTypeSymbol>> overridden,
                                        Collection<CDTypeSymbol> firstClasses) {
    HashMap<String, CDTypeSymbol> l = Maps.newHashMap();
    for (String importedCdName : cd.getImports()) {
      Optional<CDDefinitionSymbol> importedCd = symbolTable.resolve(importedCdName, CDDefinitionSymbol.KIND);
      if (importedCd.isPresent()) {
        CDDefinitionSymbol superCd = importedCd.get();
        Collection<CDTypeSymbol> overriddenSet = Lists.newArrayList();
        for (String className : nativeClasses) {
          Optional<CDTypeSymbol> cdType = superCd.getType(className);
          if (cdType.isPresent()) {
            overriddenSet.add(cdType.get());
            boolean ignore = firstClasses.stream().filter(s -> s.getName().equals(className)).count() > 0;
            if (!ignore && !l.containsKey(className)) {
              l.put(className, cdType.get());
            }
          }
        }
        if (!overriddenSet.isEmpty()) {
          overridden.put(superCd, overriddenSet);
        }
        calculateOverriddenCds(superCd, nativeClasses, overridden, firstClasses);
      }
    }
    firstClasses.addAll(l.values());
  }

  protected ASTCDClass createMillClass(ASTCDCompilationUnit cdCompilationUnit,
                                       List<ASTCDClass> nativeClasses, AstGeneratorHelper astHelper) {
    ASTCDDefinition cdDef = cdCompilationUnit.getCDDefinition();

    ASTCDClass millClass = CD4AnalysisNodeFactory.createASTCDClass();
    String millClassName = cdDef.getName() + MILL;
    String plainName = millClassName;

    // Check if a handwritten mill class exists
    if (TransformationHelper.existsHandwrittenClass(targetPath,
        TransformationHelper.getAstPackageName(cdCompilationUnit)
            + millClassName)) {
      millClassName += GeneratorSetup.GENERATED_CLASS_SUFFIX;
      millClass.setModifier(TransformationHelper.createAbstractModifier());
    }
    millClass.setName(millClassName);

    for (ASTCDClass clazz : nativeClasses) {
      String toParse = "protected static " + plainName + " mill"
          + AstGeneratorHelper.getASTClassNameWithoutPrefix(clazz) + " = null;";
      cdTransformation.addCdAttributeUsingDefinition(millClass, toParse);
    }

    // Add builder-creating methods
    for (ASTCDClass clazz : nativeClasses) {
      // TODO MB,BS: Check Builder and Mill for abstract classes
      if (AstGeneratorHelper.isAbstract(clazz)
          || !GeneratorHelper.getPlainName(clazz).startsWith(GeneratorHelper.AST_PREFIX)) {
        continue;
      }
      String className = AstGeneratorHelper.getASTClassNameWithoutPrefix(clazz);
      String methodName = StringTransformations.uncapitalize(className);
      String toParse = "public static " + clazz.getName() + "Builder "
          + methodName
          + AstGeneratorHelper.BUILDER + "() ;";

      HookPoint methodBody = new TemplateHookPoint("ast.AstMillBuilderMethod", className, methodName);
      replaceMethodBodyTemplate(millClass, toParse, methodBody);

      toParse = "protected " + clazz.getName() + "Builder _"
          + methodName + AstGeneratorHelper.BUILDER + "() ;";
      replaceMethodBodyTemplate(millClass, toParse,
          new StringHookPoint("return new " + clazz.getName() + "Builder();\n"));
    }

    cdDef.getCDClassList().add(millClass);
    return millClass;
  }

  protected ASTCDClass createMillForSuperClass(ASTCDCompilationUnit cdCompilationUnit,
                                               String millClassName,
                                               Symbol symbol,
                                               Collection<CDTypeSymbol> overriddenClasses,
                                               Collection<CDTypeSymbol> firstClasses,
                                               AstGeneratorHelper astHelper) {
    ASTCDDefinition cdDef = cdCompilationUnit.getCDDefinition();

    ASTCDClass millClass = CD4AnalysisNodeFactory.createASTCDClass();

    // Check if a handwritten mill class exists
    if (TransformationHelper.existsHandwrittenClass(targetPath,
        TransformationHelper.getAstPackageName(cdCompilationUnit)
            + millClassName)) {
      millClassName += GeneratorSetup.GENERATED_CLASS_SUFFIX;
      millClass.setModifier(TransformationHelper.createAbstractModifier());
    }
    millClass.setName(millClassName);
    millClass.setSuperclass(TransformationHelper.createType(symbol.getName()));

    // Add builder-creating methods
    for (CDTypeSymbol cdType : overriddenClasses) {
      if (!cdType.getAstNode().isPresent()) {
        continue;
      }
      ASTCDClass clazz = (ASTCDClass) cdType.getAstNode().get();
      if (AstGeneratorHelper.isAbstract(clazz)
          || !GeneratorHelper.getPlainName(clazz).startsWith(GeneratorHelper.AST_PREFIX)) {
        continue;
      }

      String className = AstGeneratorHelper.getASTClassNameWithoutPrefix(clazz);
      String methodName = StringTransformations.uncapitalize(className) + AstGeneratorHelper.BUILDER;
      String toParse = "";
      if (firstClasses.contains(cdType)) {
        toParse = "protected " + AstGeneratorHelper.getPlainName(clazz) + AstGeneratorHelper.BUILDER + " _"
            + methodName + "() ;";
        replaceMethodBodyTemplate(millClass, toParse,
            new StringHookPoint("return " + cdCompilationUnit.getCDDefinition().getName() + "Mill."
                + methodName + "();\n"));
      } else {
        toParse = "protected " + symbol.getFullName().toLowerCase() + AstGeneratorHelper.AST_DOT_PACKAGE_SUFFIX_DOT
            + AstGeneratorHelper.getPlainName(clazz) + AstGeneratorHelper.BUILDER + " _" + methodName
            + "() ;";
        replaceMethodBodyTemplate(millClass, toParse,
            new StringHookPoint("Log.error(\"0xA7009" + AstGeneratorHelper.getGeneratedErrorCode(clazz) + " Overridden production " + AstGeneratorHelper.getPlainName(clazz) + " is not reachable\");\nreturn null;\n"));

      }
    }

    cdDef.getCDClassList().add(millClass);
    return millClass;
  }

  protected ASTCDClass createNodeFactoryClass(ASTCDCompilationUnit cdCompilationUnit,
                                              List<ASTCDClass> nativeClasses, AstGeneratorHelper astHelper, Set<String> astClasses) {
    ASTCDDefinition cdDef = cdCompilationUnit.getCDDefinition();
    ASTCDClass nodeFactoryClass = CD4AnalysisNodeFactory.createASTCDClass();
    String nodeFactoryName = cdDef.getName() + NODE_FACTORY;

    // Check if a handwritten node factory exists
    if (TransformationHelper.existsHandwrittenClass(targetPath,
        TransformationHelper.getAstPackageName(cdCompilationUnit)
            + nodeFactoryName)) {
      nodeFactoryName += GeneratorSetup.GENERATED_CLASS_SUFFIX;
    }
    nodeFactoryClass.setName(nodeFactoryName);

    for (String clazz : astClasses) {
      String toParse = "protected static " + nodeFactoryName + " factory" + clazz + " = null;";
      cdTransformation.addCdAttributeUsingDefinition(nodeFactoryClass, toParse);
    }

    // Add ast-creating methods
    for (ASTCDClass clazz : nativeClasses) {
      addMethodsToNodeFactory(clazz, nodeFactoryClass, astHelper);
    }

    cdDef.getCDClassList().add(nodeFactoryClass);

    return nodeFactoryClass;
  }

  protected List<String> getImportsForNodeFactory(ASTCDClass nodeFactoryClass,
                                                  Set<String> astClasses, AstGeneratorHelper astHelper) {
    String nodeFactoryName = nodeFactoryClass.getName();
    // Add delegating methods for creating of the ast nodes of the super
    // grammars
    List<String> imports = new ArrayList<>();
    if (!astHelper.getSuperGrammarCds().isEmpty()) {
      for (CDDefinitionSymbol superCd : astHelper.getAllCds(astHelper.getCdSymbol())) {
        Log.debug(" CDDefinitionSymbol for " + nodeFactoryName + " : " + superCd, "CdDecorator");
        nodeFactoryName = getSimpleName(superCd.getName()) + NODE_FACTORY;
        String factoryPackage = superCd.getFullName().toLowerCase()
            + AstGeneratorHelper.AST_DOT_PACKAGE_SUFFIX_DOT;
        imports.add(factoryPackage + "*");
        for (CDTypeSymbol type : superCd.getTypes()) {
          Optional<ASTCDType> node = type.getAstNode();
          if (node.isPresent() && node.get() instanceof ASTCDClass) {
            ASTCDClass cdClass = (ASTCDClass) node.get();
            if (astClasses.contains(cdClass.getName())) {
              continue;
            }
            astClasses.add(cdClass.getName());
            if (!type.isAbstract()) {
              addDelegateMethodsToNodeFactory(cdClass, nodeFactoryClass, astHelper, superCd,
                      factoryPackage + nodeFactoryName);
            }
          }
        }
      }
    }
    return imports;
  }


  protected void addMethodsToNodeFactory(ASTCDClass clazz, ASTCDClass nodeFactoryClass,
                                         AstGeneratorHelper astHelper) {
    if (!clazz.isPresentModifier() || clazz.getModifier().isAbstract()) {
      return;
    }
    String className = GeneratorHelper.getPlainName(clazz);
    String toParse = "public static " + className + " create" + className + "() ;";
    HookPoint methodBody = new TemplateHookPoint("ast.factorymethods.Create", className);
    replaceMethodBodyTemplate(nodeFactoryClass, toParse, methodBody);

    toParse = "protected " + className + " doCreate" + className + "() ;";
    methodBody = new TemplateHookPoint("ast.factorymethods.DoCreate", className);
    replaceMethodBodyTemplate(nodeFactoryClass, toParse, methodBody);

    // No create methods with parameters
    if (clazz.getCDAttributeList().isEmpty()) {
      return;
    }

    toParse = "public static " + className + " create" + className + "() ;";

    Optional<ASTCDMethod> astMethod = cdTransformation.addCdMethodUsingDefinition(
        nodeFactoryClass, toParse);
    Preconditions.checkArgument(astMethod.isPresent());
    ASTCDMethod createMethod = astMethod.get();

    toParse = "protected " + className + " doCreate" + className + "() ;";
    astMethod = cdTransformation.addCdMethodUsingDefinition(
        nodeFactoryClass, toParse);
    Preconditions.checkArgument(astMethod.isPresent());
    ASTCDMethod doCreateMethod = astMethod.get();

    StringBuilder paramCall = new StringBuilder();
    List<ASTCDAttribute> parameters = Lists.newArrayList();
    String del = "";
    List<ASTCDAttribute> inheritedAttributes = Lists.newArrayList();
    for (ASTCDAttribute attr : clazz.getCDAttributeList()) {
      if (GeneratorHelper.isSymbolOrScopeAttribute(attr)) {
        continue;
      }
      if (GeneratorHelper.isInherited(attr)) {
        inheritedAttributes.add(attr);
        continue;
      }
      ASTCDParameter param = CD4AnalysisNodeFactory.createASTCDParameter();
      ASTMCType type = attr.getMCType();
      parameters.add(attr);
      param.setMCType(type);
      String javaAttrName = GeneratorHelper.getJavaAndCdConformName(attr.getName());
      param.setName(javaAttrName);
      ASTCDParameter doParam = param.deepClone();
      createMethod.getCDParameterList().add(param);
      doCreateMethod.getCDParameterList().add(doParam);
      paramCall.append(del + javaAttrName);
      del = DEL;
    }

    for (ASTCDAttribute attr : inheritedAttributes) {
      ASTCDParameter param = CD4AnalysisNodeFactory.createASTCDParameter();
      ASTMCType type = attr.getMCType();
      parameters.add(attr);
      param.setMCType(type);
      String javaAttrName = GeneratorHelper.getJavaAndCdConformName(attr.getName());
      param.setName(javaAttrName);
      ASTCDParameter doParam = param.deepClone();
      createMethod.getCDParameterList().add(param);
      doCreateMethod.getCDParameterList().add(doParam);
      paramCall.append(del + javaAttrName);
      del = DEL;
    }

    // create() method
    glex.replaceTemplate("ast.ParametersDeclaration", createMethod, new TemplateHookPoint(
        "ast.ConstructorParametersDeclaration"));
    glex.replaceTemplate(EMPTY_BODY_TEMPLATE, createMethod, new TemplateHookPoint(
        "ast.factorymethods.CreateWithParams", className, paramCall.toString()));

    // doCreate() method
    glex.replaceTemplate("ast.ParametersDeclaration", doCreateMethod, new TemplateHookPoint(
        "ast.ConstructorParametersDeclaration"));
    glex.replaceTemplate(EMPTY_BODY_TEMPLATE, doCreateMethod, new TemplateHookPoint(
        "ast.factorymethods.DoCreateWithParams", className, paramCall.toString()));

    if (parameters.size() != createMethod.getCDParameterList().size()) {
      glex.replaceTemplate(ERROR_IFNULL_TEMPLATE, createMethod, new TemplateHookPoint(
          "ast.factorymethods.ErrorIfNull", parameters));
    }
    if (parameters.size() != doCreateMethod.getCDParameterList().size()) {
      glex.replaceTemplate(ERROR_IFNULL_TEMPLATE, doCreateMethod, new TemplateHookPoint(
          "ast.factorymethods.ErrorIfNull", parameters));
    }

  }


  /**
   * @param astHelper
   * @param cdSymbol
   * @param delegateFactoryName
   * @param cdSymbol
   * @param delegateFactoryName
   */
  protected void addDelegateMethodsToNodeFactory(ASTCDClass clazz, ASTCDClass nodeFactoryClass,
                                                 AstGeneratorHelper astHelper, CDDefinitionSymbol cdSymbol, String delegateFactoryName) {
    String className = GeneratorHelper.getPlainName(clazz);
    String toParse = "public static " + cdSymbol.getFullName().toLowerCase()
        + AstGeneratorHelper.AST_DOT_PACKAGE_SUFFIX_DOT + className + " create" + className
        + "() ;";
    HookPoint methodBody = new TemplateHookPoint("ast.factorymethods.CreateDelegate",
        delegateFactoryName, className);
    replaceMethodBodyTemplate(nodeFactoryClass, toParse, methodBody);

    // No create methods without parameters
    // see ticket #2225
    int paramCount = 0;
    for (ASTCDAttribute attr : clazz.getCDAttributeList()) {
      if (!GeneratorHelper.isSymbolOrScopeAttribute(attr)) {
        paramCount++;
      }
    }
    if (paramCount == 0) {
      return;
    }

    Optional<ASTCDMethod> astMethod = cdTransformation.addCdMethodUsingDefinition(
        nodeFactoryClass, toParse);
    Preconditions.checkArgument(astMethod.isPresent());
    ASTCDMethod createMethod = astMethod.get();

    StringBuilder paramCall = new StringBuilder();
    String del = "";
    List<ASTCDAttribute> inheritedAttributes = Lists.newArrayList();
    for (ASTCDAttribute attr : clazz.getCDAttributeList()) {
      if (GeneratorHelper.isSymbolOrScopeAttribute(attr) || GeneratorHelper.isModifierPrivate(attr)) {
        continue;
      }
      if (GeneratorHelper.isInherited(attr)) {
        inheritedAttributes.add(attr);
        continue;
      }
      if (Arrays.asList(AstAdditionalAttributes.values()).stream().map(a -> a.toString())
          .anyMatch(a -> a.equals(attr.getName()))) {
        continue;
      }
      ASTCDParameter param = CD4AnalysisNodeFactory.createASTCDParameter();
      ASTMCType type = attr.getMCType();
      if (type instanceof ASTMCObjectType) {
        type = astHelper.convertTypeCd2Java((ASTMCObjectType) type,
            AstGeneratorHelper.AST_DOT_PACKAGE_SUFFIX_DOT);
      }
      param.setMCType(type);
      String javaAttrName = GeneratorHelper.getJavaAndCdConformName(attr.getName());
      param.setName(javaAttrName);
      createMethod.getCDParameterList().add(param);
      paramCall.append(del + javaAttrName);
      del = DEL;
    }
    for (ASTCDAttribute attr : inheritedAttributes) {
      ASTCDParameter param = CD4AnalysisNodeFactory.createASTCDParameter();
      ASTMCType type = attr.getMCType();
      if (type instanceof ASTMCObjectType) {
        type = astHelper.convertTypeCd2Java((ASTMCObjectType) type,
            AstGeneratorHelper.AST_DOT_PACKAGE_SUFFIX_DOT);
      }
      param.setMCType(type);
      String javaAttrName = GeneratorHelper.getJavaAndCdConformName(attr.getName());
      param.setName(javaAttrName);
      createMethod.getCDParameterList().add(param);
      paramCall.append(del + javaAttrName);
      del = DEL;
    }

    // create() method
    glex.replaceTemplate("ast.ParametersDeclaration", createMethod, new TemplateHookPoint(
        "ast.ConstructorParametersDeclaration"));
    glex.replaceTemplate(EMPTY_BODY_TEMPLATE, createMethod,
        new TemplateHookPoint(
            "ast.factorymethods.CreateWithParamsDelegate", delegateFactoryName, className,
            paramCall.toString()));
    glex.replaceTemplate(ERROR_IFNULL_TEMPLATE, createMethod, new StringHookPoint(""));

  }

  /**
   * @param name
   * @param nodeMill
   * @param astHelper
   * @param cdSymbol
   * @param delegateMillName
   */
  protected void addDelegateMethodToMill(String name, ASTCDClass nodeMill,
                                         AstGeneratorHelper astHelper, CDDefinitionSymbol cdSymbol, String delegateMillName) {
    String className = StringTransformations.uncapitalize(AstGeneratorHelper.getASTClassNameWithoutPrefix(name) + "Builder");
    String toParse = "public static " + cdSymbol.getFullName().toLowerCase()
        + AstGeneratorHelper.AST_DOT_PACKAGE_SUFFIX_DOT + name + "Builder " + className
        + "() ;";
    HookPoint methodBody = new TemplateHookPoint("ast.ASTMillDelegateMethod",
        delegateMillName, className);
    replaceMethodBodyTemplate(nodeMill, toParse, methodBody);
  }

  /**
   * @param clazz
   * @param astHelper
   */
  protected void addConstructors(ASTCDClass clazz,
                                 AstGeneratorHelper astHelper) {
    ASTCDConstructor emptyConstructor = CD4AnalysisNodeFactory.createASTCDConstructor();
    emptyConstructor.setName(clazz.getName());
    emptyConstructor.setModifier(TransformationHelper.createProtectedModifier());
    clazz.getCDConstructorList().add(emptyConstructor);

    // Only one constructor
    if (clazz.getCDAttributeList().isEmpty()) {
      return;
    }

    ASTCDConstructor fullConstructor = emptyConstructor.deepClone();
    // TODO: Collect parameters of the super class (Symbol table)
    List<ASTCDAttribute> inheritedAttributes = Lists.newArrayList();
    for (ASTCDAttribute attr : clazz.getCDAttributeList()) {
      if (GeneratorHelper.isInherited(attr)) {
        inheritedAttributes.add(attr);
        continue;
      }
      ASTCDParameter param = CD4AnalysisNodeFactory.createASTCDParameter();
      param.setMCType((ASTMCType) attr.getMCType().deepClone());
      param.setName(attr.getName());
      fullConstructor.getCDParameterList().add(param);
    }
    for (ASTCDAttribute attr : inheritedAttributes) {
      ASTCDParameter param = CD4AnalysisNodeFactory.createASTCDParameter();
      param.setMCType((ASTMCType) attr.getMCType().deepClone());
      param.setName(attr.getName());
      fullConstructor.getCDParameterList().add(param);
    }
    clazz.getCDConstructorList().add(fullConstructor);

    glex.replaceTemplate("ast.ParametersDeclaration", fullConstructor, new TemplateHookPoint(
        "ast.ConstructorParametersDeclaration", Lists.newArrayList()));
    glex.replaceTemplate(EMPTY_BODY_TEMPLATE, fullConstructor, new TemplateHookPoint(
        "ast.ConstructorAttributesSetter"));
    Optional<ASTCDClass> clazzBuilder = astHelper.getASTBuilder(clazz);
    if (clazzBuilder.isPresent()) {
      glex.replaceTemplate(
          "ast.ParametersDeclaration",
          clazzBuilder.get(),
          new TemplateHookPoint(
              "ast.BuilderConstructorParametersDeclaration", fullConstructor.getCDParameterList()));
    }
  }

  /**
   * @param cdDefinition
   * @param astHelper
   * @throws ANTLRException
   */
  protected void addConstantsClass(ASTCDDefinition cdDefinition, AstGeneratorHelper astHelper) {
    String enumLiterals = cdDefinition.getName() + ConstantsTranslation.CONSTANTS_ENUM;
    Optional<ASTCDEnum> enumConstans = cdDefinition.getCDEnumList().stream()
        .filter(e -> e.getName().equals(enumLiterals)).findAny();
    if (!enumConstans.isPresent()) {
      Log.error("0xA1004 CdDecorator error: " + enumLiterals
          + " class can't be created for the class diagramm "
          + cdDefinition.getName());
      return;
    }

    String constantsClassName = "ASTConstants" + cdDefinition.getName();
    Optional<ASTCDClass> ast = cdTransformation.addCdClassUsingDefinition(cdDefinition,
        "public class " + constantsClassName + ";");
    if (!ast.isPresent()) {
      Log.error("0xA1028 CdDecorator error:" + constantsClassName
          + " class can't be created for the class diagramm "
          + cdDefinition.getName());
      return;
    }

    ASTCDClass astConstantsClass = ast.get();
    glex.replaceTemplate(
        CLASS_CONTENT_TEMPLATE,
        astConstantsClass,
        new TemplateHookPoint(
            "ast.ASTConstantsClass", astConstantsClass, astHelper.getQualifiedCdName(), astHelper
            .getSuperGrammarCds()));
    for (ASTCDEnumConstant astConstant : enumConstans.get().getCDEnumConstantList()) {
      ASTCDAttribute constAttr = CD4AnalysisNodeFactory.createASTCDAttribute();
      constAttr.setName(astConstant.getName());
      astConstantsClass.getCDAttributeList().add(constAttr);
    }
    // cdDefinition.getCDEnumList().remove(enumConstans.get());
  }

  /**
   * @param cdDefinition
   */
  protected void decorateBaseInterface(ASTCDDefinition cdDefinition) {
    List<ASTCDInterface> stream = cdDefinition
        .getCDInterfaceList()
        .stream()
        .filter(
            i -> i.getName().equals(
                BaseInterfaceAddingManipulation.getBaseInterfaceName(cdDefinition)))
        .collect(Collectors.toList());
    if (stream.size() != 1) {
      Log.error("0xA3002 error by generation of the base node interface for the grammar "
          + cdDefinition.getName());
      return;
    }
    ASTCDInterface baseInterface = stream.get(0);
    baseInterface.getInterfaceList().add(
        TransformationHelper.createSimpleReference(GeneratorHelper.AST_NODE));
    glex.replaceTemplate(
        "ast.AstInterfaceContent",
        baseInterface,
        new TemplateHookPoint(
            "ast.ASTNodeBase", baseInterface));

  }

  /**
   * @param cdCompilationUnit
   */
  protected void transformCdTypeNamesForHWTypes(ASTCDCompilationUnit cdCompilationUnit) {
    String packageName = TransformationHelper.getAstPackageName(cdCompilationUnit);

    cdCompilationUnit
        .getCDDefinition()
        .getCDClassList()
        .forEach(
            c -> c.setName(GeneratorHelper.getSimpleTypeNameToGenerate(c.getName(), packageName,
                targetPath)));
    cdCompilationUnit
        .getCDDefinition()
        .getCDInterfaceList()
        .forEach(
            c -> c.setName(GeneratorHelper.getSimpleTypeNameToGenerate(c.getName(), packageName,
                targetPath)));
    cdCompilationUnit
        .getCDDefinition()
        .getCDEnumList()
        .forEach(
            c -> c.setName(GeneratorHelper.getSimpleTypeNameToGenerate(c.getName(), packageName,
                targetPath)));
  }

  /**
   * Performs ast specific template replacements using {@link HookPoint}
   */
  protected ASTCDMethod replaceMethodBodyTemplate(ASTCDType clazz, String methodSignatur,
                                                  HookPoint hookPoint) {
    Optional<ASTCDMethod> astMethod = cdTransformation.addCdMethodUsingDefinition(clazz,
        methodSignatur);
    Preconditions.checkArgument(astMethod.isPresent());
    glex.replaceTemplate(EMPTY_BODY_TEMPLATE, astMethod.get(), hookPoint);
    return astMethod.get();
  }

  /**
   * Performs list-valued attribute specific template replacements using
   * {@link HookPoint}
   */
  protected ASTCDMethod additionalMethodForListAttribute(ASTCDType type, String callMethod,
                                                         ASTCDAttribute attribute, String methodSignatur,
                                                         boolean returnBuilder, boolean isInherited, boolean isGetter) {
    boolean hasSymbolReference = GeneratorHelper.isReferencedSymbolAttribute(attribute);
    Optional<ASTCDMethod> astMethod = cdTransformation.addCdMethodUsingDefinition(type,
        methodSignatur);
    Preconditions.checkArgument(astMethod.isPresent());
    List<ASTCDParameter> parameters = astMethod.get().getCDParameterList();
    String callParameters = Joiners.COMMA
        .join(parameters.stream().map(ASTCDParameter::getName).collect(Collectors.toList()));
    String call;
    if (isInherited) {
      call = "super." + astMethod.get().getName();
    } else {
      call = "this." + attribute.getName() + "." + callMethod;
    }
    HookPoint hookPoint = new TemplateHookPoint(
        "ast.additionalmethods.ListAttributeMethod", call,
        !AstGeneratorHelper.hasReturnTypeVoid(astMethod.get()), callParameters, returnBuilder, hasSymbolReference && !isGetter, attribute.getName());
    glex.replaceTemplate(EMPTY_BODY_TEMPLATE, astMethod.get(), hookPoint);
    glex.replaceTemplate(ERROR_IFNULL_TEMPLATE, astMethod.get(), new StringHookPoint(""));
    return astMethod.get();
  }


  /**
   * adds methods to the attribute list
   *
   * @param interf
   * @param callMethod
   * @param attribute
   * @param methodSignatur
   */
  protected ASTCDMethod additionalMethodForListAttribute(ASTCDInterface interf, String callMethod,
                                                         ASTCDAttribute attribute, String methodSignatur) {
    Optional<ASTCDMethod> astMethod = cdTransformation.addCdMethodUsingDefinition(interf,
        methodSignatur);
    Preconditions.checkArgument(astMethod.isPresent());
    return astMethod.get();
  }

  protected ASTCDMethod additionalGetterForListAttribute(ASTCDType type, String callMethod,
                                                         ASTCDAttribute attribute, String methodSignatur) {
    return additionalMethodForListAttribute(type, callMethod, attribute, methodSignatur, false, false, true);
  }


  protected ASTCDMethod additionalNodeBuilderMethod(ASTCDClass clazz, String callMethod,
                                                    String methodSignatur) {
    Optional<ASTCDMethod> astMethod = cdTransformation.addCdMethodUsingDefinition(clazz,
        methodSignatur);
    Preconditions.checkArgument(astMethod.isPresent());
    List<ASTCDParameter> parameters = astMethod.get().getCDParameterList();
    String callParameters = Joiners.COMMA
        .join(parameters.stream().map(ASTCDParameter::getName).collect(Collectors.toList()));
    HookPoint hookPoint = new TemplateHookPoint(
        "ast.additionalmethods.NodeBuilderMethod", callMethod, callParameters);
    glex.replaceTemplate(EMPTY_BODY_TEMPLATE, astMethod.get(), hookPoint);
    glex.replaceTemplate(ERROR_IFNULL_TEMPLATE, astMethod.get(), new StringHookPoint(""));
    return astMethod.get();
  }
}
