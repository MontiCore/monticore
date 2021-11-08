/* (c) https://github.com/MontiCore/monticore */
package de.monticore.dstlgen.grammartransformation;

import com.google.common.collect.Lists;
import de.monticore.grammar.grammar.GrammarMill;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.grammar.grammar_withconcepts._ast.ASTAction;
import de.monticore.grammar.grammar_withconcepts._parser.Grammar_WithConceptsParser;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static de.monticore.dstlgen.grammartransformation.ProductionType.PATTERN;
import static de.monticore.dstlgen.grammartransformation.ProductionType.REPLACEMENT;

/**
 * This singleton generates several lexical and syntactical
 *  productions that are used for the generation of transformation
 *  languages. This class provides some constant Symbols that are used
 *  when creating a transformation language from a grammar. All constants
 *  that start with LSYM are names of lexical tokens. All constants that
 *  start with PSYM are names of parser productions.
 *
 *
 */
public class ASTRuleFactory {

  private static final String CLASS_SUFFIX = ".class;";
  private static final String PARENTHESES = "();";
  private static final String RETURN_GET = "return get";
  private static final String RETURN_THIS = "return this;";
  private static final String GET_LHS = "getLhs";
  private static final String GET_RHS = "getRhs";
  private static final String AST_PREFIX = "AST";
  private static final String PATTERN_SUFFIX = "_Pat";
  private static final String AST = "astrule ";
  private static final String NAME = "Name";
  public static final String PSYM_TFIDENTIFIER = "TfIdentifier";

  public static final String CONSTANT_SUFFIX = "_Constant";

  private static ASTRuleFactory instance = null;

  /**
   * Getter to get the only instance of the
   * ProductionFactory (singleton)
   *
   * @return the instance of the factory
   */
  public static synchronized ASTRuleFactory getInstance() {
    if (instance == null) {
      instance = new ASTRuleFactory();
    }
    return instance;
  }

  /**
   * Creates an ast rule production for the negation production for a constant
   * from the source language. Should be called once for every constant of the source
   * language.
   *
   * @param grammarSymbol grammar which is currently dealt with
   * @param name the name of the constant
   * @return the ast rule as an ASTASTRule object
   */
  ASTASTRule createAstNegationProdForConstant(MCGrammarSymbol grammarSymbol, String name) {

    String nameWithPrefix = grammarSymbol.getName() + "_" + name;
    final String tfAstRuleDecl = AST + nameWithPrefix + "_Constant_Neg = ;";
    ASTASTRule tfAstRule = parseASTRule(tfAstRuleDecl);

    // create method getLhs and getRhs
    String patternNameFirstToUpper = StringTransformations.capitalize(nameWithPrefix);
    tfAstRule.getGrammarMethodList().add(
        createASTGrammarMethod(AST_PREFIX + patternNameFirstToUpper + CONSTANT_SUFFIX + PATTERN_SUFFIX,
            "getTFElement",
            RETURN_GET + patternNameFirstToUpper + CONSTANT_SUFFIX + PATTERN_SUFFIX + PARENTHESES));

    return tfAstRule;
  }

  /**
   * Creates an ast rule production for the optional production for a constant
   * from the source language. Should be called once for every constant of the source
   * language.
   *
   * @param grammarSymbol grammar which is currently dealt with
   * @param name the name of the constant
   * @return the ast rule as an ASTASTRule object
   */
  ASTASTRule createAstOptionalProdForConstant(MCGrammarSymbol grammarSymbol, String name) {

    String nameWithPrefix = grammarSymbol.getName() + "_" + name;
    final String tfAstRuleDecl = AST + nameWithPrefix + "_Constant_Opt = ;";
    ASTASTRule tfAstRule = parseASTRule(tfAstRuleDecl);

    // create method getLhs and getRhs
    String patternNameFirstToUpper = StringTransformations.capitalize(nameWithPrefix);
    tfAstRule.getGrammarMethodList().add(
        createASTGrammarMethod(AST_PREFIX + patternNameFirstToUpper + CONSTANT_SUFFIX + PATTERN_SUFFIX,
            "getTFElement",
            RETURN_GET + patternNameFirstToUpper + CONSTANT_SUFFIX + PATTERN_SUFFIX + PARENTHESES));

    return tfAstRule;
  }


  public ASTASTRule createAstPatternProd(ASTAbstractProd srcNode,MCGrammarSymbol grammarSymbol) {
    final String name = srcNode.getName();
    final String tfAstRuleDecl;
    tfAstRuleDecl = AST + name + PATTERN_SUFFIX + " = ;";
    final String grammarPackage = Joiners.DOT.join(grammarSymbol.getAstNode().getPackageList())+
            "."+ grammarSymbol.getName().toLowerCase();
    ASTASTRule tfAstRule = parseASTRule(tfAstRuleDecl);

    // create method getLhs and getRhs
    tfAstRule.getGrammarMethodList()
        .add(createASTGrammarMethod(AST_PREFIX + name + PATTERN_SUFFIX, GET_RHS, RETURN_THIS));
    tfAstRule.getGrammarMethodList()
        .add(createASTGrammarMethod(AST_PREFIX + name + PATTERN_SUFFIX, GET_LHS, RETURN_THIS));
    // create method _getTFElementType
    final String methodBody;
    if (grammarPackage != null && !grammarPackage.startsWith(".")) {
      methodBody = "return " + grammarPackage + "._ast.AST" + name + CLASS_SUFFIX;
    }
    else {
      methodBody = "return AST" + name + CLASS_SUFFIX;
    }
    tfAstRule.getGrammarMethodList().add(createASTGrammarMethod("Class", "_getTFElementType", methodBody));

    return tfAstRule;
  }

  /**
   * Creates an ast rule for a parser production (interface or class production)
   * of the source language. The type is determined by the type parameter.
   * Should be called for every parser production of the source language.
   *
   * @param srcNode       the parser production of the source language
   * @param type          the type of the production in the transformation language.
   *                      Should be either LIST, NEGATION or OPTIONAL
   * @param grammarSymbol symbol table of the source language
   * @return the ast rule as an ASTASTRule object
   */
  public ASTASTRule createAstProd(ASTProd srcNode, ProductionType type, boolean overridden, MCGrammarSymbol grammarSymbol) {
    final String name = srcNode.getName();
    final String tfAstRuleDecl = AST + name + "_" + type.getNameString() + " = ;";
    String grammarPackage = Joiners.DOT.join(grammarSymbol.getAstNode().getPackageList());
    if(!grammarPackage.isEmpty()){
      grammarPackage += ".";
    }
    grammarPackage += grammarSymbol.getName().toLowerCase();
    ASTASTRule tfAstRule = parseASTRule(tfAstRuleDecl);

    // create method _getTFElementType
    final String methodBody = "return " + grammarPackage +  "._ast.AST" + name + CLASS_SUFFIX;
    
    tfAstRule.getGrammarMethodList().add(createASTGrammarMethod("Class", "_getTFElementType", methodBody));

    if (!type.equals(REPLACEMENT) && ! type.equals(PATTERN) && !overridden) {
      String packageName = "";
      Optional<ProdSymbol> typeSymbol = grammarSymbol.getInheritedProd(srcNode.getName());
      if(typeSymbol.isPresent()) {
        packageName = typeSymbol.get().getPackageName()+".tr."+
                typeSymbol.get().getEnclosingScope().getSpanningSymbol().getName().toLowerCase()+
                "tr._ast.";
      }
      // create method getLhs and getRhs
      tfAstRule.getGrammarMethodList()
          .add(createASTGrammarMethod(packageName + AST_PREFIX + "ITF" + name, "getTFElement", RETURN_GET + name + PARENTHESES));
    }

    return tfAstRule;
  }

  /**
   * Creates an ast method from the given method type, body and return type
   *
   * @param returnType return type of the ast method
   * @param methodName name of the ast method
   * @param methodBody body of the ast method
   * @return the ast method as an ASTGrammarMethod object
   */
  protected ASTGrammarMethod createASTGrammarMethod(String returnType, String methodName, String methodBody) {
    ASTGrammarMethod method = GrammarMill.grammarMethodBuilder().uncheckedBuild();
    method.setPublic(true);
    method.setName(methodName);

    ASTMCReturnType rType = parseReturnType(returnType);
    method.setMCReturnType(rType);

    ASTAction retStatement = parseAction(methodBody);
    method.setBody(retStatement);
    return method;
  }


  /**
   * creates an AST rule for the given ast rule
   *
   * @param srcNode              the AST rule
   * @param grammarSymbol needed to resolve possible references to grammar productions
   * @return the newly created AST rule
   */
  public ASTASTRule createASTRule(ASTASTRule srcNode, MCGrammarSymbol grammarSymbol) {
    Log.debug("Creating ast rule for " + srcNode.getType(), DSL2TransformationLanguageVisitor.LOG);
    ASTASTRule targetNode = srcNode.deepClone();
    targetNode.setType("ITF"+targetNode.getType());
    if (!targetNode.getASTSuperClassList().isEmpty()) {
      for (ASTMCType ref : targetNode.getASTSuperClassList()) {
        targetNode.getASTSuperInterfaceList().add(ref.deepClone());
      }
      targetNode.getASTSuperClassList().clear();
    }
    List<ASTAdditionalAttribute> attrsToBeRemoved = Lists.newArrayList();

    for (ASTAdditionalAttribute attr : targetNode.getAdditionalAttributeList()) {
      ASTMCType ref = attr.getMCType();
      String typeName = ref.printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter()));
      if (typeName.equals(NAME)|| grammarSymbol.getProdWithInherited(typeName).isPresent()){
        attrsToBeRemoved.add(attr);
      }
    }
    //remove attributes referring to nonterminals
    targetNode.getAdditionalAttributeList().removeAll(attrsToBeRemoved);

    // remove alll mehtods
    targetNode.getGrammarMethodList().clear();
    Log.debug("Done for " + srcNode.getType(), DSL2TransformationLanguageVisitor.LOG);

    return targetNode;

  }


  /**
   * creates an AST rule for external production belonging to the production for the given type
   *
   * @param srcNode       the external production
   * @param type          the productiuon type
   * @return the newly created AST rule
   */
  public ASTASTRule createAstProd(ASTExternalProd srcNode, ProductionType type) {
    final String name = srcNode.getName();
    final String tfAstRuleDecl = AST + name + "_" + type.getNameString() + " = ;";

    ASTASTRule tfAstRule = parseASTRule(tfAstRuleDecl);

    // create method _getTFElementType
    tfAstRule.getGrammarMethodList().add(createASTGrammarMethod("Class", "_getTFElementType", "return null;"));

    if (!type.equals(REPLACEMENT) && ! type.equals(PATTERN)) {
      // create method getLhs and getRhs
      tfAstRule.getGrammarMethodList()
          .add(createASTGrammarMethod(AST_PREFIX + "ITF" + name, "getTFElement", RETURN_GET + name + PARENTHESES));
    }

    return tfAstRule;
  }
  
  public ASTASTRule createAstExternalProd(ASTExternalProd srcNode){
    final String name = srcNode.getName();
    final String tfAstRuleDecl = AST + "ITF" + name + " astextends de.monticore.tf.ast.ITFElement" + " ;";
  
    ASTASTRule tfAstRule = parseASTRule(tfAstRuleDecl);
    return  tfAstRule;
  }


  protected ASTMCBasicGenericType parseGenericType(String type) {
    Grammar_WithConceptsParser ggtp = new Grammar_WithConceptsParser();
    try {
      return ggtp.parseMCBasicGenericType(type).get();
    }
    catch (IOException e) {
      throw new RuntimeException("0xF1006 Unable to create GenericType for " + type);
    }
  }

  protected ASTMCReturnType parseReturnType(String type) {
    Grammar_WithConceptsParser ggtp = new Grammar_WithConceptsParser();
    try {
      return ggtp.parse_StringMCReturnType(type).get();
    }
    catch (IOException e) {
      throw new RuntimeException("0xF1007 Unable to create GenericType for " + type);
    }
  }

  protected ASTASTRule parseASTRule(String tfAstElementProduction) {
    Grammar_WithConceptsParser p = new Grammar_WithConceptsParser();
    try {
      return p.parse_StringASTRule(tfAstElementProduction).get();
    }
    catch (IOException e) {
      throw new RuntimeException("0xF1000 Unable to create ASTRule for" + tfAstElementProduction);
    }
  }


  protected ASTAction parseAction(String methodBody) {
    Grammar_WithConceptsParser p = new Grammar_WithConceptsParser();
    try {
      return p.parse_StringAction(methodBody).get();
    }
    catch (IOException e) {
      throw new RuntimeException("0xF1003 Unable to create Action for " + methodBody);
    }
  }

}
