/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.cd2java.ast;

import com.google.common.base.Joiner;
import de.monticore.codegen.GeneratorHelper;
import de.monticore.generating.GeneratorSetup;
import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.monticore.symboltable.GlobalScope;
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.umlcd4a.cd4analysis._visitor.CD4AnalysisVisitor;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

public class AstGeneratorHelper extends GeneratorHelper {

  private final MCGrammarSymbol grammarSymbol;
  
  public AstGeneratorHelper(ASTCDCompilationUnit topAst, GlobalScope symbolTable) {
    super(topAst, symbolTable);
    String qualifiedGrammarName = topAst.getPackageList().isEmpty()
        ? this.cdDefinition.getName()
        : Joiner.on('.').join(Names.getQualifiedName(topAst.getPackageList()),
        this.cdDefinition.getName());

    grammarSymbol = symbolTable.<MCGrammarSymbol> resolve(
        qualifiedGrammarName, MCGrammarSymbol.KIND).orElse(null);
  }

  public MCGrammarSymbol getGrammarSymbol() {
    return this.grammarSymbol;
  }

  public String getAstAttributeValue(ASTCDAttribute attribute, ASTCDType clazz) {
    return getAstAttributeValue(attribute);
  }
  
  public String getAstAttributeValue(ASTCDAttribute attribute) {
    if (attribute.isPresentValue()) {
      return attribute.printValue();
    }
    if (isOptional(attribute)) {
      return "Optional.empty()";
    }
    String typeName = TypesPrinter.printType(attribute.getType());
    if (isListType(typeName)) {
      return "new java.util.ArrayList<>()";
    }
    if (isMapType(typeName)) {
      return "new java.util.HashMap<>()";
    }
    return "";
  }
  
  public String getAstAttributeValueForBuilder(ASTCDAttribute attribute) {
    if (isOptional(attribute)) {
      return "Optional.empty()";
    }
    else if (isBoolean(attribute)) {
      return "false";
    }
    return getAstAttributeValue(attribute);
  }
  
  public static boolean isBuilderClass(ASTCDDefinition cdDefinition, ASTCDClass clazz) {
    if (!clazz.getName().endsWith(BUILDER)
         && !clazz.getName().endsWith(BUILDER + GeneratorSetup.GENERATED_CLASS_SUFFIX)) {
      return false;
    }
    String className = clazz.getName().substring(0, clazz.getName().indexOf(BUILDER));
    return cdDefinition.getCDClassList().stream()
        .filter(c -> className.equals(GeneratorHelper.getPlainName(c))).findAny()
        .isPresent();
  }
  
  public static boolean compareAstTypes(String qualifiedType, String type) {
    if (type.indexOf('.') != -1) {
      return qualifiedType.equals(type);
    }
    String simpleName = Names.getSimpleName(qualifiedType);
    if (simpleName.startsWith(AST_PREFIX)) {
      return simpleName.equals(type);
    }
    return false;
  }
  
  /**
   * @param qualifiedName
   * @return The lower case qualifiedName + AST_PACKAGE_SUFFIX
   */
  public static String getAstPackage(String qualifiedName) {
    Log.errorIfNull(qualifiedName);
    return Joiners.DOT.join(qualifiedName.toLowerCase(), AST_PACKAGE_SUFFIX_DOT);
  }
  
  public static String getAstPackageSuffix() {
    return GeneratorHelper.AST_PACKAGE_SUFFIX;
  }
  
  public static String getSymbolTablePackageSuffix() {
    return GeneratorHelper.SYMBOLTABLE_PACKAGE_SUFFIX;
  }
  
  public static String getConstantClassName(MCGrammarSymbol grammarSymbol) {
    return grammarSymbol.getFullName().toLowerCase() +
        GeneratorHelper.AST_DOT_PACKAGE_SUFFIX + "."
        + getConstantClassSimpleName(grammarSymbol);
    
  }
  
  public static String getConstantClassSimpleName(MCGrammarSymbol grammarSymbol) {
    return "ASTConstants" + grammarSymbol.getName();
  }
  
  public static String getASTClassNameWithoutPrefix(ASTCDType type) {
    if (!GeneratorHelper.getPlainName(type).startsWith(GeneratorHelper.AST_PREFIX)) {
      return type.getName();
    }
    return GeneratorHelper.getPlainName(type).substring(GeneratorHelper.AST_PREFIX.length());
  }
  
  public static String getASTClassNameWithoutPrefix(String type) {
    return type.startsWith(GeneratorHelper.AST_PREFIX)
        ? type.substring(GeneratorHelper.AST_PREFIX.length())
        : type;
  }
  
  public static boolean isOriginalClassAbstract(ASTCDClass astType) {
    return hasStereotype(astType, "Abstract");
  }

  public static boolean isAbstract(ASTCDClass clazz) {
    return clazz.isPresentModifier() && clazz.getModifier().isAbstract();
  }
  
  /**
   * Clones the top ast and transforms CD types defined in this- or in one of the super CDs to simple CD types
   * @return cloned transformed ast
   */
  public ASTCDCompilationUnit getASTCDForReporting() {
    ASTCDCompilationUnit ast = topAst.deepClone();
    
    new Cd2JavaTypeConverter() {
      @Override
      public void visit(ASTSimpleReferenceType node) {
       AstGeneratorHelper.this.transformQualifiedToSimpleIfPossible(node, GeneratorHelper.AST_DOT_PACKAGE_SUFFIX_DOT);
      }
    }.handle(ast);
    
    return ast;
  }
  
  public String printFullType(ASTType ast) {
    return TypesPrinter.printType(ast);
  }
  
  public class Cd2JavaTypeConverter implements CD4AnalysisVisitor {}

}
