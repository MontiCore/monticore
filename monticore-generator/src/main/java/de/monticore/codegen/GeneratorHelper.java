/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import de.monticore.ast.ASTNode;
import de.monticore.codegen.cd2java.ast.AstGeneratorHelper;
import de.monticore.codegen.cd2java.ast_emf.AstEmfGeneratorHelper;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.grammar.Multiplicity;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.monticore.grammar.symboltable.MCGrammarSymbolReference;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.monticore.io.FileReaderWriter;
import de.monticore.io.paths.IterablePath;
import de.monticore.java.prettyprint.JavaDSLPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.CommonSymbol;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.types.references.ActualTypeArgument;
import de.monticore.types.TypesHelper;
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTConstantsTypes;
import de.monticore.types.types._ast.ASTImportStatement;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.CD4AnalysisHelper;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.umlcd4a.prettyprint.CDPrettyPrinterConcreteVisitor;
import de.monticore.umlcd4a.symboltable.*;
import de.monticore.umlcd4a.symboltable.references.CDTypeSymbolReference;
import de.monticore.utils.ASTNodes;
import de.se_rwth.commons.JavaNamesHelper;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static de.monticore.codegen.mc2cd.TransformationHelper.createSimpleReference;
import static de.monticore.codegen.mc2cd.transl.ConstantsTranslation.CONSTANTS_ENUM;
import static de.monticore.grammar.Multiplicity.multiplicityByAlternative;
import static de.monticore.grammar.Multiplicity.multiplicityByIteration;
import static java.util.Collections.max;

import de.monticore.generating.GeneratorSetup;

public class GeneratorHelper extends TypesHelper {

    public static final String AST_PREFIX = "AST";

    public static final String AST_NODE = "ASTNode";

    public static final String AST_NODE_CLASS_NAME = "de.monticore.ast.ASTNode";

    public static final String ASTC_NODE_CLASS_NAME = "mc.ast.ASTCNode";

    public static final String AST_PACKAGE_SUFFIX = "_ast";

    public static final String VISITOR_PACKAGE_SUFFIX = "_visitor";

    public static final String TYPERESOLVER_PACKAGE_SUFFIX = "_types";

    public static final String COCOS_PACKAGE_SUFFIX = "_cocos";

    public static final String PARSER_PACKAGE_SUFFIX = "._parser";

    public static final String AST_DOT_PACKAGE_SUFFIX = "._ast";

    public static final String AST_PACKAGE_SUFFIX_DOT = "_ast.";

    public static final String AST_DOT_PACKAGE_SUFFIX_DOT = "._ast.";

    public static final String MC_CONCRETE_PARSER_CONTEXT = "MCParser";

    public static final String BUILDER_PREFIX = "Builder_";

    public static final String OPTIONAL = "Optional";

    public static final String SYMBOL = "Symbol";

    public static final String SCOPE = "Scope";

    public static final String BASE = "Node";

    public static final String CD_EXTENSION = ".cd";

    public static final String GET_PREFIX_BOOLEAN = "is";

    public static final String GET_SUFFIX_OPTINAL = "Opt";

    public static final String GET_SUFFIX_LIST = "List";

    public static final String GET_PREFIX = "get";

    public static final String SET_PREFIX = "set";

    public static final String ARRAY_LIST = "ArrayList";

    public static final String JAVA_LIST = "java.util.List";

    public static final int STAR = -1;

    protected static final String LOG_NAME = "GeneratorHelper";

    // TODO: reserve names of the base grammars like CD4A, Types, Common ...
    private static List<String> reservedCdNames = Arrays.asList(new String[]{
            // CD4A
            "derived",
            "association",
            "composition",
            // Common.mc4
            "local",
            "readonly"});

    static JavaDSLPrettyPrinter javaPrettyPrinter;

    static CDPrettyPrinterConcreteVisitor cdPrettyPrinter;

    protected static Collection<String> additionalAttributes = Lists.newArrayList(SYMBOL, SCOPE);

    protected ASTCDDefinition cdDefinition;

    protected String packageName;

    protected String qualifiedName;

    // preserves order of appearance in the extends list of the grammar
    protected List<String> superGrammarCds = new ArrayList<>();

    protected GlobalScope symbolTable;

    protected CDSymbol cdSymbol;

    protected ASTCDCompilationUnit topAst;

    public GeneratorHelper(ASTCDCompilationUnit topAst, GlobalScope symbolTable) {
        Preconditions.checkArgument(topAst.getCDDefinition() != null);

        this.topAst = topAst;

        cdDefinition = topAst.getCDDefinition();

        this.symbolTable = symbolTable;

        // Qualified Name
        qualifiedName = Names.getQualifiedName(topAst.getPackageList(), getCdName());

        // CD package
        packageName = getCdPackage(qualifiedName);

        // CD symbol
        this.cdSymbol = getCd();

        // Create list of CDs for super grammars
        for (ASTImportStatement importSt : topAst.getImportStatementList()) {
            if (importSt.isStar()) {
                superGrammarCds.add(Names.getQualifiedName(importSt.getImportList()));
            }
        }
    }

    /**
     * Converts CD type to Java type using the given package suffix.
     *
     * @param type
     * @param packageSuffix
     * @return converted type or original type if type is java type already
     */
    public void transformTypeCd2Java(ASTSimpleReferenceType astType,
                                     String packageSuffix) {
        Log.trace("Converted Cd or Java type: " + TypesPrinter.printType(astType), LOG_NAME);
        String genericType = "";
        ASTSimpleReferenceType convertedType = astType;
        if (isOptional(astType)) {
            Optional<ASTSimpleReferenceType> typeArgument = TypesHelper
                    .getFirstTypeArgumentOfOptional(astType);
            if (!typeArgument.isPresent()) {
                return;
            }
            convertedType = typeArgument.get();
            genericType = OPTIONAL;
        } else if (TypesHelper.isGenericTypeWithOneTypeArgument(astType, ARRAY_LIST)) {
            Optional<ASTSimpleReferenceType> typeArgument = TypesHelper
                    .getFirstTypeArgumentOfGenericType(astType, ARRAY_LIST);
            if (!typeArgument.isPresent()) {
                return;
            }
            convertedType = typeArgument.get();
            genericType = ARRAY_LIST;
        } else if (TypesHelper.isGenericTypeWithOneTypeArgument(astType, JAVA_LIST)) {
            Optional<ASTSimpleReferenceType> typeArgument = TypesHelper
                    .getFirstTypeArgumentOfGenericType(astType, JAVA_LIST);
            if (!typeArgument.isPresent()) {
                return;
            }
            convertedType = typeArgument.get();
            genericType = JAVA_LIST;
        }

        String convertedTypeName = TypesPrinter.printType(convertedType);
        // Resolve only qualified types
        if (!convertedTypeName.contains(".")) {
            return;
        }

        // TODO: GV, PN: path converter by resolving
        if (convertedTypeName.contains("<")) {
            return;
        }

        Optional<CDTypeSymbol> symbol = resolveCdType(convertedTypeName);
        if (symbol.isPresent()) {
            CDTypeSymbol cdType = symbol.get();
            Log.trace("CD Type: " + cdType, LOG_NAME);
            String typeName = cdType.getModelName().toLowerCase()
                    + packageSuffix
                    + cdType.getName();
            if (!genericType.isEmpty()) {
                convertedType.setNameList(Arrays.asList(typeName.split("\\.")));
                return;
            }
            astType.setNameList(Arrays.asList(typeName.split("\\.")));
        } else {
            Log.debug("CD or Java type couldn't be resolved: " + convertedTypeName, LOG_NAME);
        }
        return;
    }

    /**
     * Converts CD type to Java type using the given package suffix.
     *
     * @param type
     * @param packageSuffix
     * @return converted type or original type if type is java type already
     */
    public ASTSimpleReferenceType convertTypeCd2Java(ASTSimpleReferenceType astType,
                                                     String packageSuffix) {
        Log.trace("Converted Cd or Java type: " + TypesPrinter.printType(astType), LOG_NAME);
        String genericType = "";
        ASTSimpleReferenceType convertedType = astType;
        if (isOptional(astType)) {
            Optional<ASTSimpleReferenceType> typeArgument = TypesHelper
                    .getFirstTypeArgumentOfOptional(astType);
            if (!typeArgument.isPresent()) {
                return astType;
            }
            convertedType = typeArgument.get();
            genericType = OPTIONAL;
        } else if (TypesHelper.isGenericTypeWithOneTypeArgument(astType, ARRAY_LIST)) {
            Optional<ASTSimpleReferenceType> typeArgument = TypesHelper
                    .getFirstTypeArgumentOfGenericType(astType, ARRAY_LIST);
            if (!typeArgument.isPresent()) {
                return astType;
            }
            convertedType = typeArgument.get();
            genericType = ARRAY_LIST;
        } else if (TypesHelper.isGenericTypeWithOneTypeArgument(astType, JAVA_LIST)) {
            Optional<ASTSimpleReferenceType> typeArgument = TypesHelper
                    .getFirstTypeArgumentOfGenericType(astType, JAVA_LIST);
            if (!typeArgument.isPresent()) {
                return astType;
            }
            convertedType = typeArgument.get();
            genericType = JAVA_LIST;
        }

        String convertedTypeName = TypesPrinter.printType(convertedType);
        // Resolve only qualified types
        if (!convertedTypeName.contains(".")) {
            return astType;
        }

        // TODO: GV, PN: path converter by resolving
        if (convertedTypeName.contains("<")) {
            return astType;
        }

        Optional<CDTypeSymbol> symbol = resolveCdType(convertedTypeName);
        if (symbol.isPresent()) {
            CDTypeSymbol cdType = symbol.get();
            Log.trace("CD Type: " + cdType, LOG_NAME);
            String typeName = cdType.getModelName().toLowerCase()
                    + packageSuffix
                    + cdType.getName();
            if (!genericType.isEmpty()) {
                return createSimpleReference(genericType, typeName);
            }
            return createSimpleReference(typeName);
        } else {
            Log.debug("CD or Java type couldn't be resolved: " + convertedTypeName, LOG_NAME);
        }
        return astType;
    }

    /**
     * Converts CD type to Java type using the given package suffix.
     *
     * @param type
     * @param packageSuffix
     * @return converted type or original type if type is java type already
     */
    public String convertTypeCd2Java(CDTypeSymbolReference astType,
                                     String packageSuffix) {
        Log.trace("Converted Cd or Java type: " + astType.getName(), LOG_NAME);
        String genericType = "";
        CDTypeSymbolReference convertedType = astType;
        if (isOptional(astType)) {
            List<ActualTypeArgument> typeArgs = astType.getActualTypeArguments();
            if (typeArgs.size() != 1) {
                return astType.getName();
            }
            CDTypeSymbolReference typeArgument = (CDTypeSymbolReference) typeArgs.get(0).getType();
            convertedType = typeArgument;
            genericType = OPTIONAL;
        } else if (isList(astType)) {
            List<ActualTypeArgument> typeArgs = astType.getActualTypeArguments();
            if (typeArgs.size() != 1) {
                return astType.getName();
            }
            CDTypeSymbolReference typeArgument = (CDTypeSymbolReference) typeArgs.get(0).getType();
            convertedType = typeArgument;
            genericType = JAVA_LIST;
        }

        String convertedTypeName = convertedType.isReferencedSymbolLoaded()
                ? convertedType.getFullName()
                : convertedType.getName();
        // Resolve only qualified types
        if (!convertedTypeName.contains(".")) {
            return astType.getName();
        }

        // TODO: GV, PN: path converter by resolving
        if (convertedTypeName.contains("<")) {
            return astType.getName();
        }

        Optional<CDTypeSymbol> symbol = resolveCdType(convertedTypeName);
        if (symbol.isPresent()) {
            CDTypeSymbol cdType = symbol.get();
            Log.trace("CD Type: " + cdType, LOG_NAME);
            String typeName = cdType.getModelName().toLowerCase()
                    + packageSuffix
                    + cdType.getName();
            if (!genericType.isEmpty()) {
                return getGenericTypeName(genericType, typeName);
            }
            return typeName;
        } else {
            Log.debug("CD or Java type couldn't be resolved: " + convertedTypeName, LOG_NAME);
        }
        return astType.getName();
    }

    /**
     * Converts CD types defined in this- or in one of the super CDs to simple CD Types
     *
     * @param type
     * @param packageSuffix
     */
    public void transformQualifiedToSimpleIfPossible(ASTSimpleReferenceType astType,
                                                     String packageSuffix) {
        Log.trace("Converted Cd or Java type: " + TypesPrinter.printType(astType), LOG_NAME);
        String genericType = "";
        ASTSimpleReferenceType convertedType = astType;
        if (isOptional(astType)) {
            Optional<ASTSimpleReferenceType> typeArgument = TypesHelper
                    .getFirstTypeArgumentOfOptional(astType);
            if (!typeArgument.isPresent()) {
                return;
            }
            convertedType = typeArgument.get();
            genericType = OPTIONAL;
        } else if (TypesHelper.isGenericTypeWithOneTypeArgument(astType, ARRAY_LIST)) {
            Optional<ASTSimpleReferenceType> typeArgument = TypesHelper
                    .getFirstTypeArgumentOfGenericType(astType, ARRAY_LIST);
            if (!typeArgument.isPresent()) {
                return;
            }
            convertedType = typeArgument.get();
            genericType = ARRAY_LIST;
        } else if (TypesHelper.isGenericTypeWithOneTypeArgument(astType, JAVA_LIST)) {
            Optional<ASTSimpleReferenceType> typeArgument = TypesHelper
                    .getFirstTypeArgumentOfGenericType(astType, JAVA_LIST);
            if (!typeArgument.isPresent()) {
                return;
            }
            convertedType = typeArgument.get();
            astType.setNameList(Arrays.asList("List"));
            genericType = JAVA_LIST;
        }

        String convertedTypeName = TypesPrinter.printType(convertedType);
        // Resolve only qualified types
        if (!convertedTypeName.contains(".")) {
            return;
        }

        // TODO: GV, PN: path converter by resolving
        if (convertedTypeName.contains("<")) {
            return;
        }

        Optional<CDTypeSymbol> symbol = resolveCdType(convertedTypeName);
        if (symbol.isPresent()) {
            CDTypeSymbol cdType = symbol.get();
            String typeName = cdType.getName();
            if (superGrammarCds.contains(cdType.getModelName())) {
                typeName = Joiner.on('.').join(Names.getSimpleName(cdType.getModelName()),
                        cdType.getName());
            } else if (!cdSymbol.getFullName().equals(cdType.getModelName())) {
                typeName = cdType.getModelName().toLowerCase()
                        + packageSuffix
                        + cdType.getName();
            }
            if (!genericType.isEmpty()) {
                convertedType.setNameList(Arrays.asList(typeName.split("\\.")));
                return;
            }
            astType.setNameList(Arrays.asList(typeName.split("\\.")));
        } else {
            Log.debug("CD or Java type couldn't be resolved: " + convertedTypeName, LOG_NAME);
        }
        return;
    }

    /**
     *
     * @param genericType
     * @param typeName
     * @return
     */
    public String getGenericTypeName(String genericType, String typeName) {
        return genericType + '<' + typeName + '>';
    }

    /**
     * @return cdSymbol
     */
    public CDSymbol getCdSymbol() {
        return this.cdSymbol;
    }

    /**
     * @return name of the language's AST-Nodes marker interface
     * @see #getASTNodeBaseType(String)
     */
    public String getASTNodeBaseType() {
        return getASTNodeBaseType(getCdName());
    }

    /**
     * @return name of the language's and (recursive) super languages' AST-Nodes
     * marker interface
     * @see #getASTNodeBaseType()
     */
    public Collection<String> getASTNodeBaseTypes() {
        Set<String> baseNodesNames = new LinkedHashSet<>();

        for (CDSymbol cd : getAllCds(getCd())) {
            String qualifiedCdName = cd.getFullName();
            String simpleCdName = getCdName(qualifiedCdName);
            String baseNodeName = getASTNodeBaseType(simpleCdName);
            String astPackage = AstGeneratorHelper.getAstPackage(qualifiedCdName);
            baseNodesNames.add(Names.getQualifiedName(astPackage, baseNodeName));
        }
        return baseNodesNames;
    }

    // -------------- Static methods ------------

    /**
     * @return full qualified name of the overall interface for AST nodes
     * @see #getASTNodeType()
     */
    public static String getQualifiedASTNodeType() {
        return AST_NODE_CLASS_NAME;
    }

    /**
     * @return name of the overall interface for AST nodes
     * @see #getQualifiedASTNodeType()
     */
    public static String getASTNodeType() {
        return AST_NODE;
    }

    /**
     * @return name of the language's AST-Nodes marker interface
     * @see #getASTNodeBaseType()
     */
    public static String getASTNodeBaseType(String languageName) {
        return AST_PREFIX + languageName + BASE;
    }

    public String getTypeResolverPackage() {
        return getTypeResolverPackage(getPackageName());
    }

    public static String getTypeResolverPackage(String qualifiedLanguageName) {
        return getPackageName(qualifiedLanguageName.toLowerCase(),
                getTypeResolverPackageSuffix());
    }

    public static String getTypeResolverPackageSuffix() {
        return GeneratorHelper.TYPERESOLVER_PACKAGE_SUFFIX;
    }

    public String getVisitorPackage() {
        return getVisitorPackage(getPackageName());
    }

    public static String getVisitorPackage(String qualifiedLanguageName) {
        return getPackageName(qualifiedLanguageName.toLowerCase(),
                getVisitorPackageSuffix());
    }

    public static String getVisitorPackageSuffix() {
        return GeneratorHelper.VISITOR_PACKAGE_SUFFIX;
    }

    public static boolean isAdditionalAttribute(ASTCDAttribute attrib) {
        return isOptional(attrib.getType())
                && additionalAttributes.stream().filter(
                a -> a.equals(getReferenceNameFromOptional(attrib.getType()))).findAny()
                .isPresent();
    }

    public static List<ASTCDAttribute> getNativeCDAttributes(ASTCDClass clazz) {
        return clazz.getCDAttributeList().stream().filter(attr -> !isAdditionalAttribute(attr))
                .collect(Collectors.toList());
    }

    public boolean hasOnlyAstAttributes(ASTCDClass type) {
        for (ASTCDAttribute attr : type.getCDAttributeList()) {
            if (!isAstNode(attr)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isString(String type) {
        return "String".equals(type) || "java.lang.String".equals(type);
    }

    public static boolean isString(ASTSimpleReferenceType type) {
        String typeName = getSimpleName(type.getNameList());
        return "String".equals(typeName) || "java.lang.String".equals(typeName);
    }

    public static String getSimpleName(List<String> nameAsList) {
        if (nameAsList == null || nameAsList.isEmpty()) {
            return "";
        }
        return nameAsList.get(nameAsList.size() - 1);
    }

    public String getAstClassNameForASTLists(CDFieldSymbol field) {
        // TODO for default types (e.g. String) this field.getType() would try to
        // resolve the default type but fail
        // hence we currently use the ast methods instead of
        // "return isOptionalAstNode(field.getType())"
        return getAstClassNameForASTLists(field.getType());
    }

    public String getAstClassNameForASTLists(CDTypeSymbolReference field) {
        List<ActualTypeArgument> typeArgs = field.getActualTypeArguments();
        if (typeArgs.size() != 1) {
            return AST_NODE_CLASS_NAME;
        }

        if (!(typeArgs.get(0).getType() instanceof CDTypeSymbolReference)) {
            return AST_NODE_CLASS_NAME;
        }
        String arg = typeArgs.get(0).getType().getReferencedSymbol().getFullName();
        return AstGeneratorHelper.getAstPackage(Names.getQualifier(arg))
                + Names.getSimpleName(arg);
    }

    public String getAstClassNameForASTLists(ASTCDAttribute attr) {
        if (!attr.getSymbol().isPresent()) {
            return "";
        }
        if (!(attr.getSymbol().get() instanceof CDFieldSymbol)) {
            Log.error(String.format("0xA04125 Symbol of ASTCDAttribute %s is not CDFieldSymbol.",
                    attr.getName()));
        }
        return getAstClassNameForASTLists(((CDFieldSymbol) attr.getSymbol().get()).getType());
    }

    public static boolean isOptional(ASTCDAttribute attribute) {
        return isOptional(attribute.getType());
    }

    public static boolean isOptional(CDTypeSymbol type) {
        // TODO proper implementation
        if (OPTIONAL.equals(type.getName())) {
            return true;
        }
        if (!type.getAstNode().isPresent()) {
            Log.debug(String.format("ASTNode of cd type symbol %s is not set.",
                    type.getName()), LOG_NAME);
            return false;
        }
        ASTNode node = type.getAstNode().get();
        if (!(node instanceof ASTType)) {
            Log.error(String
                    .format(
                            "0xA5009 Expected the ASTNode of cd type symbol %s to be an ASTType, but it is of kind %s",
                            type.getFullName(), node.getClass().getName()));
            return false;
        }
        return isOptional((ASTType) node);
    }

    public static boolean isOptional(CDFieldSymbol field) {
        return isOptional(field.getType());
    }
    
    public static boolean isBoolean(ASTCDAttribute attribute) {
      return "boolean".equals(attribute.printType());
    }

    public boolean isAstNode(CDTypeSymbol type) {
        String typeName = type.getName();
        if (!typeName.contains(".") && !typeName.startsWith(AST_PREFIX)) {
            return false;
        } else {
            List<String> listName = TypesHelper.createListFromDotSeparatedString(typeName);
            if (!listName.get(listName.size() - 1).startsWith(AST_PREFIX)) {
                return false;
            }
        }
        if (!(type instanceof CDTypeSymbolReference)) {
            return type.isClass() || type.isInterface();
        }
        CDTypeSymbolReference attrType = (CDTypeSymbolReference) type;
        if (!attrType.getActualTypeArguments().isEmpty()) {
            return false;
        }
        return attrType.existsReferencedSymbol() && !attrType.isEnum();
    }

    public boolean isOptionalAstNode(CDFieldSymbol field) {
        // TODO for default types (e.g. String) this field.getType() would try to
        // resolve the default type but fail
        // hence we currently use the ast methods instead of
        // "return isOptionalAstNode(field.getType())"
        return isOptionalAstNode(field.getType());
    }

    public boolean isListAstNode(CDFieldSymbol field) {
        // TODO for default types (e.g. String) this field.getType() would try to
        // resolve the default type but fail
        // hence we currently use the ast methods instead of
        // "return isOptionalAstNode(field.getType())"
        return isListAstNode(field.getType());
    }

    public boolean isAstNode(CDFieldSymbol field) {
        // TODO for default types (e.g. String) this field.getType() would try to
        // resolve the default type but fail
        // hence we currently use the ast methods instead of
        // "return isAstNode(field.getType())"
        return isAstNode(field.getType());
    }

    public static boolean isListType(String type) {
        // TODO : use symbol table
        int index = type.indexOf('<');
        if (index != -1) {
            type = type.substring(0, index);
        }
        return "List".equals(type) || "java.util.List".equals(type)
                || "ArrayList".equals(type) || "java.util.ArrayList".equals(type);
    }

    public static boolean isMapType(String type) {
        // TODO : use symbol table
        int index = type.indexOf('<');
        if (index != -1) {
            type = type.substring(0, index);
        }
        return "Map".equals(type) || "java.util.Map".equals(type)
                || "HashMap".equals(type) || "java.util.HashMap".equals(type);
    }

    public static boolean isAbstract(ASTCDClass clazz) {
        return clazz.isPresentModifier() && clazz.getModifier().isAbstract();
    }

    public static boolean isAbstract(ASTCDMethod method, ASTCDInterface type) {
        return true;
    }

    public static boolean isAbstract(ASTCDMethod method, ASTCDEnum type) {
        return false;
    }

    public static boolean isAbstract(ASTCDMethod method, ASTCDClass type) {
        return CD4AnalysisHelper.isAbstract(method);
    }

    public static boolean isDefault(ASTCDMethod method) {
        return hasStereotype(method, MC2CDStereotypes.DEFAULT_IMPLEMENTATION.toString());
    }

    public static boolean isInherited(ASTCDAttribute attribute) {
        return CD4AnalysisHelper.hasStereotype(attribute, MC2CDStereotypes.INHERITED.toString());
    }

    public boolean isEnum(String qualifiedName) {
        Optional<CDTypeSymbol> cdType = resolveCdType(qualifiedName);
        return cdType.isPresent() && cdType.get().isEnum();
    }

    public boolean isAttributeOfTypeEnum(ASTCDAttribute attr) {
        if (!attr.getSymbol().isPresent() || !(attr.getSymbol().get() instanceof CDFieldSymbol)) {
            return false;
        }
        CDTypeSymbolReference attrType = ((CDFieldSymbol) attr.getSymbol()
                .get()).getType();

        List<ActualTypeArgument> typeArgs = attrType.getActualTypeArguments();
        if (typeArgs.size() > 1) {
            return false;
        }

        String typeName = typeArgs.isEmpty()
                ? attrType.getName()
                : typeArgs.get(0).getType().getName();
        if (!typeName.contains(".") && !typeName.startsWith(AST_PREFIX)) {
            return false;
        }

        List<String> listName = TypesHelper.createListFromDotSeparatedString(typeName);
        if (!listName.get(listName.size() - 1).startsWith(AST_PREFIX)) {
            return false;
        }

        if (typeArgs.isEmpty()) {
            return attrType.existsReferencedSymbol() && attrType.isEnum();
        }

        CDTypeSymbolReference typeArgument = (CDTypeSymbolReference) typeArgs
                .get(0).getType();
        return typeArgument.existsReferencedSymbol() && typeArgument.isEnum();
    }

    public static boolean hasStereotype(ASTCDType ast,
                                        String stereotypeName) {
        if (!ast.getModifierOpt().isPresent()
                || !ast.getModifierOpt().get().isPresentStereotype()) {
            return false;
        }
        ASTStereotype stereotype = ast.getModifierOpt().get().getStereotype();
        return stereotype.getValueList().stream()
                .filter(v -> v.getName().equals(stereotypeName)).findAny()
                .isPresent();
    }

    public static boolean hasStereotype(ASTCDMethod ast, String stereotypeName) {
        if (ast.getModifier().isPresentStereotype()) {
            ASTStereotype stereotype = ast.getModifier().getStereotype();
            return stereotype.getValueList().stream().filter((v) -> {
                return v.getName().equals(stereotypeName);
            }).findAny().isPresent();
        } else {
            return false;
        }
    }

    public static List<String> getStereotypeValues(ASTCDType ast,
                                                   String stereotypeName) {
        List<String> values = Lists.newArrayList();
        if (ast.getModifierOpt().isPresent()
                && ast.getModifierOpt().get().isPresentStereotype()) {
            ast.getModifierOpt().get().getStereotype().getValueList().stream()
                    .filter(value -> value.getName().equals(stereotypeName))
                    .filter(value -> value.isPresentValue())
                    .forEach(value -> values.add(value.getValue()));
        }
        return values;
    }


    /**
     *
     * @param cdAttribute
     * @param type
     * @return
     */
    public boolean isAttributeOfSuperType(ASTCDAttribute cdAttribute, ASTCDType type) {
        if (!type.getSymbol().isPresent()) {
            Log.error("0xA5010 Could not load symbol information for " + type.getName() + ".");
            return false;
        }
        CDTypeSymbol sym = (CDTypeSymbol) type.getSymbol().get();
        return getAllVisibleFieldsOfSuperTypes(sym).stream().map(a -> a.getName())
                .collect(Collectors.toList()).contains(cdAttribute.getName());
    }

    /**
     *
     * @param field
     * @param type
     * @return
     */
    public boolean isAttributeOfSuperType(CDFieldSymbol field, CDTypeSymbol type) {
        return getAllVisibleFieldsOfSuperTypes(type).stream().map(a -> a.getName())
                .collect(Collectors.toList()).contains(field.getName());
    }

    /**
     *
     * @param cdType
     * @return
     */
    public Collection<CDFieldSymbol> getAllVisibleFieldsOfSuperTypes(CDTypeSymbol cdType) {
        final Set<CDFieldSymbol> allSuperTypeFields = new LinkedHashSet<>();

        for (CDTypeSymbol superType : cdType.getSuperTypes()) {
            for (CDFieldSymbol superField : superType.getFields()) {
                allSuperTypeFields.add(superField);
            }
            allSuperTypeFields.addAll(getAllVisibleFieldsOfSuperTypes(superType));
        }

        // filter-out all private fields
        final Set<CDFieldSymbol> allVisibleSuperTypeFields = allSuperTypeFields.stream()
                .filter(field -> !field.isPrivate())
                .collect(Collectors.toCollection(LinkedHashSet::new));

        return ImmutableSet.copyOf(allVisibleSuperTypeFields);
    }

    /**
     *
     * @param type
     * @return
     */
    public Optional<String> getTypeNameToResolve(ASTSimpleReferenceType astType) {
        ASTSimpleReferenceType convertedType = astType;
        if (isOptional(astType)) {
            Optional<ASTSimpleReferenceType> typeArgument = TypesHelper
                    .getFirstTypeArgumentOfOptional(astType);
            if (!typeArgument.isPresent()) {
                return Optional.empty();
            }
            convertedType = typeArgument.get();
        } else if (TypesHelper.isGenericTypeWithOneTypeArgument(astType, ARRAY_LIST)) {
            Optional<ASTSimpleReferenceType> typeArgument = TypesHelper
                    .getFirstTypeArgumentOfGenericType(astType, ARRAY_LIST);
            if (!typeArgument.isPresent()) {
                return Optional.empty();
            }
            convertedType = typeArgument.get();
        } else if (TypesHelper.isGenericTypeWithOneTypeArgument(astType, JAVA_LIST)) {
            Optional<ASTSimpleReferenceType> typeArgument = TypesHelper
                    .getFirstTypeArgumentOfGenericType(astType, JAVA_LIST);
            if (!typeArgument.isPresent()) {
                return Optional.empty();
            }
            convertedType = typeArgument.get();
        }

        String convertedTypeName = TypesPrinter.printType(convertedType);
        // Resolve only qualified types
        if (!convertedTypeName.contains(".")) {
            return Optional.empty();
        }

        // TODO: GV, PN: path converter by resolving
        if (convertedTypeName.contains("<")) {
            return Optional.empty();
        }

        return Optional.of(convertedTypeName);
    }

    /**
     * Gets super types recursively (without duplicates - the first occurrence in
     * the type hierarchy is used)
     *
     * @param type
     * @return all supertypes (without the type itself)
     */
    // TODO PN<-RH: how to refactor to cd4a symbol table? type.getSuperTypes() is
    // based on CommonJTypeSymbol
    public List<CDTypeSymbol> getSuperTypes(CDTypeSymbol type) {
        List<CDTypeSymbol> allSuperTypes = new ArrayList<>();
        for (CDTypeSymbol s : type.getSuperTypes()) {
            addIfNotContained(s, allSuperTypes);
            List<CDTypeSymbol> supers = getSuperTypes(s);
            for (CDTypeSymbol sup : supers) {
                addIfNotContained(sup, allSuperTypes);
            }
        }
        return allSuperTypes;
    }

    /**
     * Gets super types recursively (without duplicates - the first occurrence in
     * the type hierarchy is used)
     *
     * @param type
     * @return all supertypes (without the type itself)
     */
    // TODO PN<-RH: how to refactor to cd4a symbol table? type.getSuperTypes() is
    // based on CommonJTypeSymbol
    public List<CDTypeSymbol> getAllSuperInterfaces(CDTypeSymbol type) {
        List<CDTypeSymbol> allSuperTypes = new ArrayList<>();
        for (CDTypeSymbol s : type.getSuperTypes()) {
            if (s.isInterface()) {
                addIfNotContained(s, allSuperTypes);
            }
            List<CDTypeSymbol> supers = getSuperTypes(s);
            for (CDTypeSymbol sup : supers) {
                if (sup.isInterface()) {
                    addIfNotContained(sup, allSuperTypes);
                }
            }
        }
        return allSuperTypes;
    }

    protected static <T extends CommonSymbol> void addIfNotContained(T toAdd, List<T> list) {
        if (!list.stream()
                .filter(t -> t.getName().equals(toAdd.getName()))
                .findAny()
                .isPresent()) {
            list.add(toAdd);
        }
    }

    /**
     * Gets the java super types of the given interf (without the interf itself).
     *
     * @param interf
     * @return
     */
    public Collection<String> getSuperTypes(ASTCDInterface interf) {
        if (!interf.getSymbol().isPresent()) {
            Log.error("0xA5011 Could not load symbol information for " + interf.getName() + ".");
        }

        CDTypeSymbol sym = (CDTypeSymbol) interf.getSymbol().get();
        List<CDTypeSymbol> allSuperTypes = getSuperTypes(sym);
        List<String> theSuperTypes = allSuperTypes.stream().map(t -> t.getFullName())
                .collect(Collectors.toList());

        // transform to java types
        theSuperTypes = theSuperTypes.stream()
                .map(s -> AstGeneratorHelper.getAstPackage(Names.getQualifier(s)) + Names.getSimpleName(s))
                .collect(Collectors.toList());
        return theSuperTypes;
    }

    /**
     * Gets the java super types of the given clazz (without the clazz itself).
     *
     * @param clazz
     * @return
     */
    public List<String> getSuperTypes(ASTCDClass clazz) {
        if (!clazz.getSymbol().isPresent()) {
            Log.error("0xA5007 Could not load symbol information for " + clazz.getName() + ".");
        }

        CDTypeSymbol sym = (CDTypeSymbol) clazz.getSymbol().get();
        List<CDTypeSymbol> allSuperTypes = getSuperTypes(sym);
        List<String> theSuperTypes = allSuperTypes.stream().map(t -> t.getFullName())
                .collect(Collectors.toList());

        // transform to java types
        theSuperTypes = theSuperTypes.stream()
                .map(s -> AstGeneratorHelper.getAstPackage(Names.getQualifier(s)) + Names.getSimpleName(s))
                .collect(Collectors.toList());
        return theSuperTypes;
    }

    /**
     * Gets the java super types of the given clazz (without the clazz itself).
     *
     * @param clazz
     * @return
     */
    public List<CDTypeSymbol> getAllSuperInterfaces(ASTCDType type) {
        if (!type.getSymbol().isPresent()) {
            Log.error("0xA4079 Could not load symbol information for " + type.getName() + ".");
        }

        CDTypeSymbol sym = (CDTypeSymbol) type.getSymbol().get();
        return getAllSuperInterfaces(sym);
    }

    public static String getSuperClass(ASTCDClass clazz) {
        if (!clazz.isPresentSuperclass()) {
            return "de.monticore.ast.ASTCNode";
        }
        return clazz.printSuperClass();
    }

    public static String getSuperClassName(ASTCDClass clazz) {
        if (!clazz.isPresentSuperclass()) {
            return "";
        }
        return clazz.printSuperClass();
    }

    public static List<String> getValuesOfConstantEnum(ASTCDDefinition ast) {
        List<String> astConstants = new ArrayList<>();
        ASTCDEnum constants = null;
        Iterator<ASTCDEnum> it = ast.getCDEnumList().iterator();
        while (it.hasNext() && constants == null) {
            ASTCDEnum cdEnum = it.next();
            if (cdEnum.getName().equals(ast.getName() + CONSTANTS_ENUM)) {
                constants = cdEnum;
            }
        }
        if (constants != null) {
            for (ASTCDEnumConstant constant : constants.getCDEnumConstantList()) {
                astConstants.add(constant.getName());
            }
        }
        return astConstants;
    }

    public static List<CDFieldSymbol> getVisibleFields(CDTypeSymbol cdType) {
        return cdType.getFields().stream().filter(a -> !a.isPrivate()).collect(Collectors.toList());
    }

    public static JavaDSLPrettyPrinter getJavaPrettyPrinter() {
        if (javaPrettyPrinter == null) {
            javaPrettyPrinter = new JavaDSLPrettyPrinter(new IndentPrinter());
        }
        return javaPrettyPrinter;
    }

    public static CDPrettyPrinterConcreteVisitor getCDPrettyPrinter() {
        if (cdPrettyPrinter == null) {
            cdPrettyPrinter = new CDPrettyPrinterConcreteVisitor(new IndentPrinter());
        }
        return cdPrettyPrinter;
    }

    public boolean isListAstNode(ASTCDAttribute attribute) {
        if (!attribute.getSymbol().isPresent()) {
            return false;
        }
        if (!(attribute.getSymbol().get() instanceof CDFieldSymbol)) {
            Log.error(String.format("0xA5012 Symbol of ASTCDAttribute %s is not CDFieldSymbol.",
                    attribute.getName()));
        }
        return isListAstNode(((CDFieldSymbol) attribute.getSymbol().get()).getType());
    }

    public boolean isListAstNode(CDTypeSymbolReference type) {
        if (!type.getName().equals(JAVA_LIST)) {
            return false;
        }
        List<ActualTypeArgument> typeArgs = type.getActualTypeArguments();
        if (typeArgs.size() != 1) {
            return false;
        }

        if (!(typeArgs.get(0).getType() instanceof CDTypeSymbolReference)) {
            return false;
        }
        return isAstNode((CDTypeSymbolReference) typeArgs.get(0).getType());
    }

    public boolean isList(CDTypeSymbolReference type) {
        if (!type.getName().equals(JAVA_LIST)) {
            return false;
        }
        return type.getActualTypeArguments().size() == 1;
    }

    public boolean isListOfString(CDFieldSymbol field) {
        CDTypeSymbolReference type = field.getType();
        if (!type.getName().equals(JAVA_LIST)) {
            return false;
        }
        List<ActualTypeArgument> typeArgs = type.getActualTypeArguments();
        if (typeArgs.size() != 1) {
            return false;
        }
        return isString(typeArgs.get(0).getType().getName());
    }

    public boolean isOptionalAstNode(CDTypeSymbolReference type) {
        if (!type.getName().equals(OPTIONAL)) {
            return false;
        }
        List<ActualTypeArgument> typeArgs = type.getActualTypeArguments();
        if (typeArgs.size() != 1) {
            return false;
        }

        if (!(typeArgs.get(0).getType() instanceof CDTypeSymbolReference)) {
            return false;
        }
        return isAstNode((CDTypeSymbolReference) typeArgs.get(0).getType());
    }

    public static boolean isSupertypeOfHWType(String className) {
        return isSupertypeOfHWType(className, AST_PREFIX);
    }

    public static boolean isSupertypeOfHWType(String className, String prefix) {
        return (prefix.isEmpty() || className.startsWith(prefix))
                && className.endsWith(GeneratorSetup.GENERATED_CLASS_SUFFIX);
    }

    public static String getJavaConformName(String name) {
        return JavaNamesHelper.javaAttribute(name);
    }

    public static String getCdLanguageConformName(String name) {
        if (reservedCdNames.contains(name)) {
            return (JavaNamesHelper.PREFIX_WHEN_WORD_IS_RESERVED + name).intern();
        }
        return name.intern();
    }

    public static boolean isQualified(MCGrammarSymbolReference grammarRef) {
        if (grammarRef.getName().contains(".")) {
            return true;
        }
        if (grammarRef.existsReferencedSymbol()) {
            MCGrammarSymbol grammarSymbol = grammarRef.getReferencedSymbol();
            if (!grammarSymbol.getFullName().contains(".")) {
                // The complete name has no package name, therefore the grammarRefName
                // without "." is qualified
                return true;
            }
        }
        return false;
    }

    public static boolean isQualified(String name) {
        if (name.contains(".")) {
            return true;
        }
        return false;
    }

    public static String getJavaAndCdConformName(String name) {
        Log.errorIfNull(name);
        return getCdLanguageConformName(getJavaConformName(name));
    }

    public boolean isAstNode(ASTCDAttribute attr) {
        if (!attr.getSymbol().isPresent()) {
            return false;
        }
        if (!(attr.getSymbol().get() instanceof CDFieldSymbol)) {
            Log.error(String.format("0xA5013 Symbol of ASTCDAttribute %s is not CDFieldSymbol.",
                    attr.getName()));
        }
        return isAstNode(((CDFieldSymbol) attr.getSymbol().get()).getType());
    }

    public boolean isOptionalAstNode(ASTCDAttribute attr) {
        if (!attr.getSymbol().isPresent()) {
            return false;
        }
        if (!(attr.getSymbol().get() instanceof CDFieldSymbol)) {
            Log.error(String.format("0xA5014 Symbol of ASTCDAttribute %s is not CDFieldSymbol.",
                    attr.getName()));
        }
        return isOptionalAstNode(((CDFieldSymbol) attr.getSymbol().get()).getType());
    }

    public String getTypeNameWithoutOptional(ASTCDAttribute attribute) {
        if (isOptional(attribute)) {
            return TypesHelper
                    .printType(TypesHelper.getSimpleReferenceTypeFromOptional(attribute.getType()));

        }
        return attribute.printType();
    }

    public String getCdTypeNameWithoutOptional(CDFieldSymbol attribute) {
        CDTypeSymbolReference type = attribute.getType();
        if (!isOptional(type)) {
            return type.getName();
        }
        return type.getActualTypeArguments().get(0).getType().getName();
    }

    public String getJavaTypeNameWithoutOptional(CDFieldSymbol attribute) {
        CDTypeSymbolReference type = attribute.getType();
        if (!isOptional(type)) {
            return convertTypeCd2Java(type, AST_DOT_PACKAGE_SUFFIX_DOT);
        }
        return convertTypeCd2Java(
                (CDTypeSymbolReference) type.getActualTypeArguments().get(0).getType(),
                AST_DOT_PACKAGE_SUFFIX_DOT);
    }

    public static String getPlainGetter(ASTCDAttribute ast) {
        String astType = printType(ast.getType());
        StringBuilder sb = new StringBuilder();
        if (CDTypes.isBoolean(astType)) {
            sb.append(GET_PREFIX_BOOLEAN);
        } else {
            sb.append(GET_PREFIX);
        }
        sb.append(StringTransformations.capitalize(getNativeAttributeName(ast.getName())));
        if (isOptional(ast)) {
            sb.append(GET_SUFFIX_OPTINAL);
        } else if (isListType(astType)) {
            if (ast.getName().endsWith(TransformationHelper.LIST_SUFFIX)) {
                sb.replace(sb.length() - TransformationHelper.LIST_SUFFIX.length(),
                        sb.length(), GET_SUFFIX_LIST);
            } else {
                sb.append(GET_SUFFIX_LIST);
            }
        }
        return sb.toString();
    }

    public static String getPlainName(ASTCDAttribute ast) {
        return StringTransformations.capitalize(getNativeAttributeName(ast.getName()));
    }

    public static String getSimpleListName(ASTCDAttribute ast) {
        String name = ast.getName();
        if (name.endsWith(TransformationHelper.LIST_SUFFIX)) {
            name = name.substring(0, name.length() - TransformationHelper.LIST_SUFFIX.length());
        }
        return StringTransformations.capitalize(name);
    }

    public static String getPlainGetter(CDFieldSymbol field) {
        String astType = field.getType().getName();
        StringBuilder sb = new StringBuilder();
        if (CDTypes.isBoolean(astType)) {
            sb.append(GET_PREFIX_BOOLEAN);
        } else {
            sb.append(GET_PREFIX);
        }
        sb.append(StringTransformations.capitalize(getNativeAttributeName(field.getName())));
        if (isOptional(field)) {
            sb.append(GET_SUFFIX_OPTINAL);
        } else if (isListType(astType)) {
            if (field.getName().endsWith(TransformationHelper.LIST_SUFFIX)) {
                sb.replace(sb.length() - TransformationHelper.LIST_SUFFIX.length(),
                        sb.length(), GET_SUFFIX_LIST);
            } else {
                sb.append(GET_SUFFIX_LIST);
            }
        }
        return sb.toString();
    }

    /**
     * Returns the plain getter for the given attribute
     */
    public static String getPlainSetter(ASTCDAttribute ast) {
        StringBuilder sb = new StringBuilder(SET_PREFIX).append(
                StringTransformations.capitalize(getNativeAttributeName(ast.getName())));
        String astType = printType(ast.getType());
        if (isListType(astType))
            if (ast.getName().endsWith(TransformationHelper.LIST_SUFFIX)) {
                sb.replace(sb.length() - TransformationHelper.LIST_SUFFIX.length(),
                        sb.length(), GET_SUFFIX_LIST);
            } else {
                sb.append(GET_SUFFIX_LIST);
            }
        return sb.toString();
    }

    /**
     * Returns the plain getter for the given attribute
     */
    public static String getPlainSetter(CDFieldSymbol field) {
        StringBuilder sb = new StringBuilder(SET_PREFIX).append(
                StringTransformations.capitalize(getNativeAttributeName(field.getName())));
        if (isListType(field.getType().getName())) {
            if (field.getName().endsWith(TransformationHelper.LIST_SUFFIX)) {
                sb.replace(sb.length() - TransformationHelper.LIST_SUFFIX.length(),
                        sb.length(), GET_SUFFIX_LIST);
            } else {
                sb.append(GET_SUFFIX_LIST);
            }
        }
        return sb.toString();
    }

    /**
     * Returns name without suffix for HW classes
     *
     * @param type
     * @return
     */
    public static String getPlainName(ASTCDType type) {
        String name = type.getName();
        if (isSupertypeOfHWType(name)) {
            return name.substring(0, name.lastIndexOf(GeneratorSetup.GENERATED_CLASS_SUFFIX));
        }
        return name;
    }

    /**
     * Returns name without suffix for HW classes
     *
     * @param type
     * @return
     */
    public static String getPlainName(ASTCDType type, String prefix) {
        String name = type.getName();
        if (isSupertypeOfHWType(name, prefix)) {
            return name.substring(0, name.lastIndexOf(GeneratorSetup.GENERATED_CLASS_SUFFIX));
        }
        return name;
    }

    /**
     * Prints the type argument of the list-values ast type otherwise prints the
     * given type.
     */
    public static String printTypeArgumentOfAstList(ASTType type) {
        if (isGenericTypeWithOneTypeArgument(type, JAVA_LIST)) {
            Optional<ASTSimpleReferenceType> typeArgument = TypesHelper
                    .getFirstTypeArgumentOfGenericType(type, JAVA_LIST);
            if (typeArgument.isPresent()) {
                return printSimpleRefType(typeArgument.get());
            }
        }
        return printSimpleRefType(type);
    }

    /**
     * Checks if the node is part of the current language (or one of its super
     * languages) or if it is external (e.g. String, List, etc.)
     *
     * @param type
     * @return
     */
    public static boolean isExternal(ASTCDType type, String superType) {
        if (!type.getModifierOpt().isPresent()) {
            return false;
        }
        if (!type.getModifierOpt().get().isPresentStereotype()) {
            return false;
        }
        ASTStereotype stereoTypes = type.getModifierOpt().get().getStereotype();
        return stereoTypes.getValueList().stream()
                .filter(value -> value.getName().equals(MC2CDStereotypes.EXTERNAL_TYPE.toString()))
                .filter(value -> value.getValue().equals(superType))
                .count() <= 0;
    }

    public static String getDotPackageName(String packageName) {
        if (packageName.isEmpty() || packageName.endsWith(".")) {
            return packageName;
        }
        return packageName + ".";
    }

    /**
     * Checks if the node is part of the current language (or one of its super
     * languages) or if it is external (e.g. String, List, etc.)
     *
     * @param type
     * @return
     */
    public static boolean isExternal(CDTypeSymbol type, String superType) {
        Optional<Stereotype> ster = type.getStereotype(MC2CDStereotypes.EXTERNAL_TYPE.toString());
        if (ster.isPresent()) {
            return ster.get().getValue().equals(superType);
        }
        ;
        return false;
    }

    public static String getPackageName(String packageName, String suffix) {
        return packageName.isEmpty() ? suffix : packageName
                + "." + suffix;
    }

    public static String getPackageName(ASTMCGrammar astGrammar, String suffix) {
        String qualifiedGrammarName = astGrammar.getPackageList().isEmpty()
                ? astGrammar.getName()
                : Joiner.on('.').join(Names.getQualifiedName(astGrammar.getPackageList()),
                astGrammar.getName());
        return Joiner.on('.').join(qualifiedGrammarName.toLowerCase(), suffix);
    }

    public static String getSimpleTypeNameToGenerate(String simpleName, String packageName,
                                                     IterablePath targetPath) {
        if (existsHandwrittenClass(simpleName, packageName, targetPath)) {
            return simpleName + GeneratorSetup.GENERATED_CLASS_SUFFIX;
        }
        return simpleName;
    }

    public static boolean existsHandwrittenClass(String simpleName, String packageName,
                                                 IterablePath targetPath) {
        return TransformationHelper.existsHandwrittenClass(targetPath,
                getDotPackageName(packageName) + simpleName);
    }

    public String getAstPackage() {
        return getPackageName(getPackageName(), AST_PACKAGE_SUFFIX);
    }

    /**
     * TODO: Gets not transformed attribute name according to the original name in
     * MC grammar
     *
     * @param attributeName
     * @return
     */
    public static String getNativeAttributeName(String attributeName) {
        if (!attributeName.startsWith(JavaNamesHelper.PREFIX_WHEN_WORD_IS_RESERVED)) {
            return attributeName;
        }
        return attributeName.substring(JavaNamesHelper.PREFIX_WHEN_WORD_IS_RESERVED.length());
    }

    /**
     * Prints Cd4Analysis AST to the file with the extension
     * {@link GeneratorHelper#CD_EXTENSION} in the given subdirectory
     *
     * @param astCd        - the top node of the Cd4Analysis AST
     * @param outputPath   - output path
     * @param subDirectory - sub directory of the output path
     */
    public static void prettyPrintAstCd(ASTCDCompilationUnit astCd, File outputPath,
                                        String subDirectory) {
        String fileName = Names.getSimpleName(astCd.getCDDefinition().getName());
        storeInFile(astCd, fileName, CD_EXTENSION, outputPath, subDirectory);
    }

    /**
     * Prints AST node to the file with the given name and extension in the given
     * subdirectory of the given output directory
     *
     * @param ast           - the AST node to print
     * @param fileName
     * @param fileExtension
     * @param outputPath
     * @param subDirectory
     */
    public static void storeInFile(ASTCD4AnalysisNode ast, String fileName, String fileExtension,
                                   File outputPath,
                                   String subDirectory) {
        Path path = createDestinationFile(fileName, fileExtension, outputPath, subDirectory);

        String output = getCDPrettyPrinter().prettyprint(ast);

        new FileReaderWriter().storeInFile(path, output);
    }

    private static Path createDestinationFile(String fileName, String fileExtension,
                                              File outputDirectory, String subDirectory) {
        final Path filePath = Paths.get(subDirectory, fileName + fileExtension);
        return Paths.get(outputDirectory.getAbsolutePath(), filePath.toString());
    }

    /**
     * This method gets all diagrams that participate in the given one by getting
     * (1) the class diagram itself and (2) all imported class diagrams (as well
     * as recursively their imported class diagrams as well). If a CD occurs twice
     * in this import-graph, the algorithm understands that this is exactly the
     * same diagram and thus <em>ignores the second</em> occurrence. Note that
     * this is different to the rules within a grammar - there the last occurrence
     * would be used, because it overrides the former declarations, but on a class
     * diagram level exists no overriding, because different references to a
     * diagram always mean the same diagram (i.e., the one on the model path with
     * <em>the</em> matching name).
     *
     * @param cd the class diagram to get all participating class diagrams for
     * @return the class diagrams starting with the current grammar and then in
     * order of appearance in the imports of the class diagram.
     */
    public List<CDSymbol> getAllCds(CDSymbol cd) {
        List<CDSymbol> resolvedCds = new ArrayList<>();
        // the cd itself
        resolvedCds.add(cd);
        resolvedCds.addAll(getAllSuperCds(cd));
        return resolvedCds;
    }

    /**
     * This method gets all diagrams that participate in the given one by getting
     * (1) the class diagram itself and (2) all imported class diagrams (as well
     * as recursively their imported class diagrams as well). If a CD occurs twice
     * in this import-graph, the algorithm understands that this is exactly the
     * same diagram and thus <em>ignores the second</em> occurrence. Note that
     * this is different to the rules within a grammar - there the last occurrence
     * would be used, because it overrides the former declarations, but on a class
     * diagram level exists no overriding, because different references to a
     * diagram always mean the same diagram (i.e., the one on the model path with
     * <em>the</em> matching name).
     *
     * @param cd the class diagram to get all participating class diagrams for
     * @return the class diagrams starting with the current grammar and then in
     * order of appearance in the imports of the class diagram.
     */
    public List<CDSymbol> getAllSuperCds(CDSymbol cd) {
        List<CDSymbol> resolvedCds = new ArrayList<>();
        // imported cds
        for (String importedCdName : cd.getImports()) {
            Log.trace("Resolving the CD: " + importedCdName, LOG_NAME);
            Optional<CDSymbol> importedCd = resolveCd(importedCdName);
            if (!importedCd.isPresent()) {
                Log.error("0xA8451 The class diagram could not be resolved: " + importedCdName);
            } else {
                // recursively add imported cds
                /* ... and prevent duplicates. First occurrence of a grammar is used.
                 * I.e., the algorithm understands that when one grammar is imported
                 * multiple times by different diagrams, it is still the same diagram.
                 * Note that this is independent from the rules within a grammar - there
                 * the last occurrence would be used, because it overrides the former
                 * declarations . */
                List<CDSymbol> recursivImportedCds = getAllCds(importedCd.get());
                for (CDSymbol recImport : recursivImportedCds) {
                    if (!resolvedCds
                            .stream()
                            .filter(c -> c.getFullName().equals(recImport.getFullName()))
                            .findAny()
                            .isPresent()) {
                        resolvedCds.add(recImport);
                    }
                }
            }
        }
        return resolvedCds;
    }

    /**
     *
     * @param cd
     * @return
     */
    public List<CDSymbol> getDirectSuperCds(CDSymbol cd) {
        List<CDSymbol> resolvedCds = new ArrayList<>();
        // the cd itself
        resolvedCds.add(cd);
        // imported cds
        for (String importedCdName : cd.getImports()) {
            Log.trace("Resolving the CD: " + importedCdName, LOG_NAME);
            Optional<CDSymbol> importedCd = resolveCd(importedCdName);
            if (!importedCd.isPresent()) {
                Log.error("0xA8452 The class diagram could not be resolved: " + importedCdName);
            }
            resolvedCds.add(importedCd.get());
        }
        return resolvedCds;
    }

    public CDSymbol getCd() {
        Optional<CDSymbol> cdOpt = resolveCd(getQualifiedCdName());
        if (!cdOpt.isPresent()) {
            Log.error("0xA0487 The class diagram could not be resolved: " + getQualifiedCdName());
        }
        return cdOpt.get();
    }

    /**
     * @return packageName
     */
    public String getPackageName() {
        return this.packageName;
    }

    public String getQualifiedCdName() {
        return qualifiedName;
    }

    /**
     * @return superGrammars in order of appearance in the grammars extends list.
     */
    public List<String> getSuperGrammarCds() {
        return this.superGrammarCds;
    }

    public boolean isAstClass(ASTCDClass clazz) {
        String simpleName = Names.getSimpleName(clazz.getName());
        if (!simpleName.startsWith(AST_PREFIX)) {
            return false;
        }
        String nameToResolve = clazz.getName().contains(".") ? clazz.getName() : qualifiedName + "."
                + clazz.getName();
        if (nameToResolve.endsWith(GeneratorSetup.GENERATED_CLASS_SUFFIX)) {
            nameToResolve = nameToResolve.substring(0,
                    nameToResolve.lastIndexOf(GeneratorSetup.GENERATED_CLASS_SUFFIX));
        }
        return resolveCdType(nameToResolve).isPresent();
    }

    public String getCdName() {
        return cdDefinition.getName();
    }

    /**
     * Resolves the CD of the given qualified name
     *
     * @param qualifiedCdName full qualified name to resolve the CD for
     * @return the {@link CDSymbol}
     */
    public Optional<CDSymbol> resolveCd(String qualifiedCdName) {
        return symbolTable.resolve(qualifiedCdName, CDSymbol.KIND);
    }

    public Optional<CDTypeSymbol> resolveCdType(String type) {
        // Log.trace("Resolve: " + type + " -> " + symbolTable.resolve(type,
        // CDTypeSymbol.KIND), LOG_NAME);
        return symbolTable.resolve(type, CDTypeSymbol.KIND);
    }

    public static String getCdPackage(String qualifiedCdName) {
        return qualifiedCdName.toLowerCase();
    }

    /**
     * @return cdDefinition
     */
    public ASTCDDefinition getCdDefinition() {
        return this.cdDefinition;
    }

    public static String getCdName(String qualifiedCdName) {
        return Names.getSimpleName(qualifiedCdName);
    }

    /**
     * Gets the qualified java AST type for the given type.
     *
     * @param type the type symbol
     * @return [astPackage of the type].[type.getName()]
     */
    public static String getJavaASTName(CDTypeSymbol type) {
        return AstGeneratorHelper.getAstPackage(Names.getQualifier(type.getFullName()))
                + type.getName();
    }

    public static String qualifiedJavaTypeToName(String type) {
        return type.replace('.', '_');
    }

    public static Multiplicity getMultiplicity(ASTMCGrammar grammar,
                                               ASTNode nonTerminal) {
        Multiplicity byAlternative = multiplicityByAlternative(grammar,
                nonTerminal);
        Multiplicity byIteration = multiplicityByIteration(grammar,
                nonTerminal);
        return max(Lists.newArrayList(byIteration,
                byAlternative));
    }

    /**
     * @return the super productions defined in all super grammars (including
     * transitive super grammars)
     */
    public static List<ASTProd> getAllSuperProds(ASTNode astNode) {
        List<ASTProd> directSuperRules = getDirectSuperProds(astNode);
        List<ASTProd> allSuperRules = new ArrayList<>();
        for (ASTProd superRule : directSuperRules) {
            allSuperRules.addAll(getAllSuperProds(superRule));
        }
        allSuperRules.addAll(directSuperRules);
        return allSuperRules;
    }

    /**
     * @return the super productions defined in direct super grammars
     */
    public static List<ASTProd> getDirectSuperProds(ASTNode astNode) {
        if (astNode instanceof ASTClassProd) {
            List<ASTProd> directSuperProds = resolveRuleReferences(
                    ((ASTClassProd) astNode).getSuperRuleList(), astNode);
            directSuperProds.addAll(
                    resolveRuleReferences(((ASTClassProd) astNode).getSuperInterfaceRuleList(), astNode));
            return directSuperProds;
        } else if (astNode instanceof ASTInterfaceProd) {
            return resolveRuleReferences(((ASTInterfaceProd) astNode).getSuperInterfaceRuleList(), astNode);
        }
        return Collections.emptyList();
    }

    /**
     * @return the production definitions of B & C in "A extends B, C"
     */
    public static List<ASTProd> resolveRuleReferences(List<ASTRuleReference> ruleReferences,
                                                      ASTNode nodeWithSymbol) {
        List<ASTProd> superRuleNodes = new ArrayList<>();
        for (ASTRuleReference superRule : ruleReferences) {
            Optional<MCProdSymbol> symbol = MCGrammarSymbolTableHelper.resolveRule(nodeWithSymbol,
                    superRule.getName());
            if (symbol.isPresent() && symbol.get().getAstNode().isPresent()) {
                superRuleNodes.add((ASTProd) symbol.get().getAstNode().get());
            }
        }
        return superRuleNodes;
    }

    public static Map<ASTProd, List<ASTNonTerminal>> getInheritedNonTerminals(ASTProd sourceNode) {
        return GeneratorHelper.getAllSuperProds(sourceNode).stream()
                .distinct()
                .collect(Collectors.toMap(Function.identity(),
                        astProd -> ASTNodes.getSuccessors(astProd, ASTNonTerminal.class)));
    }

    /**
     * Generates an error code suffix in format "_ddd" where d is a decimal. If
     * there is an ast-name then always the same error code will be generated.
     *
     * @param ast
     * @return generated error code suffix in format "xddd" where d is a decimal.
     */
    public static String getGeneratedErrorCode(ASTNode ast) {
        int hashCode = 0;
        // If there is an ast-name then always generate the same error code.
        if (ast.getSymbol().isPresent()) {
            String nodeName = ast.getSymbol().get().getFullName();
            hashCode = Math.abs(ast.getClass().getSimpleName().hashCode() + nodeName.hashCode());
        } else { // Else use the string representation
            hashCode = Math.abs(ast.toString().hashCode());
        }
        String errorCodeSuffix = String.valueOf(hashCode);
        return "x" + (hashCode < 1000 ? errorCodeSuffix : errorCodeSuffix
                .substring(errorCodeSuffix.length() - 3));
    }

    /**
     * Creates an instance of the generator helper
     *
     * @param astClassDiagram
     * @param globalScope
     * @param emfCompatible
     * @return
     */
    public static AstGeneratorHelper createGeneratorHelper(ASTCDCompilationUnit astClassDiagram,
                                                           GlobalScope globalScope, boolean emfCompatible) {
        if (emfCompatible) {
            return new AstEmfGeneratorHelper(astClassDiagram, globalScope);
        }
        return new AstGeneratorHelper(astClassDiagram, globalScope);
    }

}
