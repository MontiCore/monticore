/* (c) https://github.com/MontiCore/monticore */
package de.monticore.dstlgen.grammartransformation;

import de.monticore.ast.Comment;
import de.monticore.dstlgen.util.DSTLUtil;
import de.monticore.grammar.grammar.GrammarMill;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._visitor.GrammarTraverser;
import de.monticore.grammar.grammar_withconcepts._parser.Grammar_WithConceptsParser;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static de.monticore.grammar.grammar._ast.ASTConstantsGrammar.DEFAULT;
import static de.se_rwth.commons.StringTransformations.capitalize;

/**
 * This singleton generates several lexical and syntactical
 * productions that are used for the generation of transformation
 * languages. This class provides some constant Symbols that are used
 * when creating a transformation language from a grammar. All constants
 * that start with LSYM are names of lexical tokens. All constants that
 * start with PSYM are names of parser productions.
 */
public class ProductionFactory {

  private static final String COLON = ":";
  private static final String OR = " | ";
  private static final String PATTERN_SUFFIX = "_Pat";
  private static final String NAME = "Name";
  public static final String PSYM_TFIDENTIFIER = "TfIdentifier";
  public static final String PSYM_TFOBJECTS = "TFRule";
  public static final String PSYM_SCHEMAVAR = "schemaVar";
  public static final String PSYM_PATTERN = "de.monticore.tf.ast.IPattern";


  private static ProductionFactory instance = null;

  /**
   * Getter to get the only instance of the
   * ProductionFactory (singleton)
   *
   * @return the instance of the factory
   */
  public static synchronized ProductionFactory getInstance() {
    if (instance == null) {
      instance = new ProductionFactory();
    }
    return instance;
  }


  /**
   * Creates an interface production corresponding to the
   * given production from the modeling language. Should
   * be called for every production of the source language.
   *
   * @param srcNode the production of the source language
   * @return the interface production as an ASTInterfaceProd object
   */
  public ASTInterfaceProd createInterfaceProd(ASTClassProd srcNode, int grammar_depth, boolean reduceParserAlts) {
    String name = srcNode.getName();
    String tfReplacementRule;

    String ITFPart = "I" + srcNode.getSymbol().getEnclosingScope().getName() + "TFPart";

    tfReplacementRule = "interface ITF" + name + " astextends de.monticore.tf.ast.ITFElement";
    boolean isEmpty = DSTLUtil.isEmptyProduction(srcNode.getSymbol());
    if (!isEmpty && !reduceParserAlts) {
      tfReplacementRule += "/* Skipping extends " + ITFPart + "<" + grammar_depth + "> due to being moved */";
    } else {
      tfReplacementRule += "/* Skipping "+ITFPart+" due to emptiness or reducing parser alts */";
    }
    tfReplacementRule += ";";

    ASTInterfaceProd result = parseInterfaceProd(tfReplacementRule);
    if (!isEmpty) { // Only copy super (interface) rules if we are not empty - antlr does not like empty optionals
      for (ASTRuleReference ref : srcNode
              .getSuperInterfaceRuleList()) {
        copyandAdaptNonterminal(result, ref);
      }

      for (ASTRuleReference ref : srcNode.getSuperRuleList()) {
        copyandAdaptNonterminal(result, ref);
      }
    } else {
      result.add_PreComment(new Comment("/* No super rule (interfaces) due to being empty */"));
    }

    return result;
  }

  public ASTInterfaceProd createInterfaceProd(ASTInterfaceProd srcNode, int grammar_depth) {
    return this.createInterfaceProd(srcNode, grammar_depth, false);
  }


  /**
   * Creates an interface production corresponding to the
   * given interface production from the modeling language.
   * Should be called for every interface production of the
   * source language.
   *
   * @param srcNode the interface production of the source language
   * @return the interface production as an ASTInterfaceProd object
   */
  public ASTInterfaceProd createInterfaceProd(ASTInterfaceProd srcNode, int grammar_depth, boolean reduceParserAlts) {
    reduceParserAlts = true; // TODO: remove it all together?
    String ITFPart = "I" + srcNode.getSymbol().getEnclosingScope().getName() + "TFPart";
    final String tfReplacementRule = "interface ITF" + srcNode.getName() + " astextends de.monticore.tf.ast.ITFElement " + (reduceParserAlts ? "" : " extends " +ITFPart+"<" + grammar_depth + ">") + ";";
    ASTInterfaceProd result = parseInterfaceProd(tfReplacementRule);
    for (ASTRuleReference r : srcNode.getSuperInterfaceRuleList()) {
      copyandAdaptNonterminal(result, r);
    }

    result.add_PreComment(new Comment("/* NO extends "+ITFPart+ "<?> */"));
    return result;
  }


  public ASTInterfaceProd createInterfaceProd(ASTAbstractProd srcNode) {
    final String tfReplacementRule = "interface ITF" + srcNode.getName() + " astextends de.monticore.tf.ast.ITFElement;";
    ASTInterfaceProd result = parseInterfaceProd(tfReplacementRule);
    for (ASTRuleReference r : srcNode.getSuperInterfaceRuleList()) {
      copyandAdaptNonterminal(result, r);
    }

    return result;
  }

  private void copyandAdaptNonterminal(ASTInterfaceProd result, ASTRuleReference r) {
    ASTRuleReference clone = r.deepClone();
    clone.setName("ITF" + clone.getTypeName());
    result.getSuperInterfaceRuleList().add(clone);
  }

  /**
   * Creates a production for replacing elements from the source language.
   * Should be called for every (interface) production of the source language.
   *
   * @param srcNode the production of the source language
   * @return the replacement production as an ASTClassProd object
   */
  public ASTClassProd createReplacementProd(ASTProd srcNode, boolean superExternal) {
    final String name = srcNode.getName();
    String tfReplacementRule =
            name + "_" + "Rep"
                    + helpRelation(superExternal, "ITF" + name,  "I" + srcNode.getSymbol().getEnclosingScope().getName() + "TFPart")
                    + "  astimplements de.monticore.tf.ast.IReplacement = "
                    + "(" + createPrefix(name, ProductionType.REPLACEMENT) + " \"[[\" lhs:" + "ITF" + name
                    + "? ReplacementOp "
                    + " rhs:" + "ITF" + name + "? \"]]\");";

    return parseClassProd(tfReplacementRule);
  }

  public ASTClassProd createPatternProd(ASTClassProd srcNode, MCGrammarSymbol grammarSymbol, boolean superExternal) {
    return this.createPatternProd(srcNode, grammarSymbol, superExternal, false, false);
  }


  /**
   * Creates the pattern production used to match elements defined in the
   * source language in the model. Should be called on every production
   * from the source language.
   *
   * @param srcNode       the production of the source language
   * @param grammarSymbol grammar which is currently dealt with
   * @return the pattern production as an ASTClassProd object
   */
  public ASTClassProd createPatternProd(ASTClassProd srcNode,
                                        MCGrammarSymbol grammarSymbol,
                                        boolean superExternal,
                                        boolean skipForSpecialRecursion,
                                        boolean isEmpty) {
    ASTClassProd result = GrammarMill.classProdBuilder().uncheckedBuild();

    // three notations for a pattern
    List<ASTAlt> patternAlts = new ArrayList<>();


    ASTAlt origAlt = GrammarMill.altBuilder().uncheckedBuild();
    ASTAlt abstractAlt = GrammarMill.altBuilder().uncheckedBuild();
    ASTAlt mixedAlt = GrammarMill.altBuilder().uncheckedBuild();

    // first notation: abstract syntax (schemavar only)
    patternAlts.add(abstractAlt);
    // NTName as keyword
    ASTTerminal terminal = GrammarMill.terminalBuilder().uncheckedBuild();
    terminal.setName(srcNode.getName());
    terminal.setIteration(DEFAULT);
    abstractAlt.getComponentList().add(terminal);
    // schema variable
    ASTNonTerminal schemaVarName = GrammarMill.nonTerminalBuilder().uncheckedBuild();
    schemaVarName.setUsageName(PSYM_SCHEMAVAR + NAME);
    schemaVarName.setName(NAME);
    abstractAlt.getComponentList().add(schemaVarName);

    // second notation: copy original production
    patternAlts.add(origAlt);

    ASTBlock origBlock = GrammarMill.blockBuilder().uncheckedBuild();
    //ANTRL does not accept left recursion in blocks
    if (skipForSpecialRecursion) {
      if (srcNode.getAltList().size() != 1) {
        Log.error("0xA5C05 pattern creation: More than one alt during detected left recursion - aborting");
      }
      ASTAlt aDeepClone = srcNode.getAltList().get(0).deepClone();
      aDeepClone.setRightAssoc(false); //TODO: Do i need the rightassoc?
      origAlt.getComponentList().addAll(aDeepClone.getComponentList());
      origBlock.getAltList().add(aDeepClone);
      origAlt.add_PreComment(new Comment(" /* Avoid an extra block here */ "));
    } else {
      origBlock.add_PreComment(new Comment(" /* Using an extra block here */ "));
      origAlt.getComponentList().add(origBlock);
      for (ASTAlt a : srcNode.getAltList()) {
        ASTAlt aDeepClone = a.deepClone();
        aDeepClone.setRightAssoc(false);
        origBlock.getAltList().add(aDeepClone);
      }
    }

    //third notation: variable and syntax
    patternAlts.add(mixedAlt);

    ASTBlock mixedBlock = GrammarMill.blockBuilder().uncheckedBuild();
    mixedAlt.getComponentList().add(mixedBlock);

    ASTAlt mixedBlockAlt = GrammarMill.altBuilder().uncheckedBuild();
    mixedBlock.getAltList().add(mixedBlockAlt);

    ASTBlock varBlock = GrammarMill.blockBuilder().uncheckedBuild();

    ASTAlt varOptionalAlt = GrammarMill.altBuilder().uncheckedBuild();
    mixedBlockAlt.getComponentList().add(varBlock);


    // nonterminal is optional but variable is mandatory
    ASTAlt ntOptionalAlt = GrammarMill.altBuilder().uncheckedBuild();
    terminal = GrammarMill.terminalBuilder().uncheckedBuild();
    terminal.setName(srcNode.getName());
    terminal.setIteration(ASTConstantsGrammar.QUESTION);
    ntOptionalAlt.getComponentList().add(terminal);
    ntOptionalAlt.getComponentList()
            .add(schemaVarName.deepClone());
    varBlock.getAltList().add(ntOptionalAlt);

    // nonterminal is mandatory but variable is optional
    terminal = GrammarMill.terminalBuilder().uncheckedBuild();
    terminal.setName(srcNode.getName());
    terminal.setIteration(DEFAULT);
    varOptionalAlt.getComponentList().add(terminal);
    ASTNonTerminal schemaVarNameOptional = schemaVarName.deepClone();
    schemaVarNameOptional.setIteration(ASTConstantsGrammar.QUESTION);
    varOptionalAlt.getComponentList().add(schemaVarNameOptional);
    varBlock.getAltList().add(varOptionalAlt);

    // syntax in parenthesis
    terminal = GrammarMill.terminalBuilder().uncheckedBuild();
    terminal.setName("[[");
    terminal.setIteration(DEFAULT);
    mixedBlockAlt.getComponentList().add(terminal);
    mixedBlockAlt.getComponentList().add(origBlock.deepClone());
    terminal = GrammarMill.terminalBuilder().uncheckedBuild();
    terminal.setName("]]");
    terminal.setIteration(DEFAULT);
    mixedBlockAlt.getComponentList().add(terminal);

    result.setAltList(patternAlts);

    // create "implements ... or extends ..."
    result.getSuperInterfaceRuleList().clear();

    ASTRuleReference superInterface = GrammarMill.ruleReferenceBuilder().uncheckedBuild();
    superInterface.setName("ITF" + srcNode.getName());
    if (!superExternal) {
      result.getSuperInterfaceRuleList().add(superInterface);
    } else {
      result.getSuperRuleList().add(superInterface);
    }

    addInterfaces(srcNode.getSuperRuleList(), result);
    addInterfaces(srcNode.getSuperInterfaceRuleList(),
                  result);
    if (!isEmpty) {
      superInterface = GrammarMill.ruleReferenceBuilder().uncheckedBuild();
      superInterface.setName("I" + srcNode.getSymbol().getEnclosingScope().getName() + "TFPart");
      result.getSuperInterfaceRuleList().add(superInterface);
    } else {
      result.add_PreComment(new Comment("/* No I?TFPart supers due to emptiness */"));
    }


    // create "astimplements ..."
    result.getASTSuperInterfaceList().add(parseGenericType(PSYM_PATTERN));

    // change name
    result.setName(srcNode.getName() + PATTERN_SUFFIX);
    // add action
    if (srcNode.isPresentAction()) {
      result.setAction(srcNode.getAction().deepClone());
    }
    GrammarTraverser grammarTraverser = GrammarMill.traverser();
    grammarTraverser.add4Grammar(new PostprocessPatternAttributesVisitor(grammarSymbol));
        result.accept(grammarTraverser);

    return result;
  }

  protected void addInterfaces(List<ASTRuleReference> references, ASTClassProd result) {
    for (ASTRuleReference ref : references) {
      ASTRuleReference r = ref.deepClone();
      r.setName("ITF" + r.getName());
      result.getSuperInterfaceRuleList().add(r);
    }
  }

  public ASTClassProd createPatternProd(ASTInterfaceProd srcNode, MCGrammarSymbol grammarSymbol) {
    return doCreatePatternProd(srcNode, grammarSymbol);
  }

  public ASTClassProd createPatternProd(ASTAbstractProd srcNode, MCGrammarSymbol grammarSymbol) {
    return doCreatePatternProd(srcNode, grammarSymbol);
  }

  /**
   * Creates the pattern production used to match elements defined in the
   * source language in the model. Should be called on every interface production
   * from the source language.
   *
   * @param srcNode       the production of the source language
   * @param grammarSymbol grammar which is currently dealt with
   * @return the pattern production as an ASTClassProd object
   */
  protected ASTClassProd doCreatePatternProd(ASTProd srcNode, MCGrammarSymbol grammarSymbol) {
    // copy original production
    ASTClassProd result = GrammarMill.classProdBuilder().uncheckedBuild();

    if (srcNode.getSymbol().isIsInterface())
      result.add_PreComment(new Comment(" /* do not generate a builder for me */ "));

    // three notations for a pattern
    List<ASTAlt> patternAlts = new ArrayList<>();


    ASTAlt abstractAlt = GrammarMill.altBuilder().uncheckedBuild();
    ASTAlt mixedAlt = GrammarMill.altBuilder().uncheckedBuild();


    // first notation: abstract syntax (schemavar only)
    patternAlts.add(abstractAlt);
    // NTName as keyword
    ASTTerminal terminal = GrammarMill.terminalBuilder().uncheckedBuild();
    terminal.setName(srcNode.getName());
    terminal.setIteration(DEFAULT);
    abstractAlt.getComponentList().add(terminal);
    // schema variable
    ASTNonTerminal schemaVarName = GrammarMill.nonTerminalBuilder().uncheckedBuild();
    schemaVarName.setUsageName(PSYM_SCHEMAVAR + NAME);
    schemaVarName.setName(NAME);
    abstractAlt.getComponentList().add(schemaVarName);

    //third notation: variable and syntax
    patternAlts.add(mixedAlt);

    // syntax in parenthesis
    terminal = GrammarMill.terminalBuilder().uncheckedBuild();
    terminal.setName(srcNode.getName());
    terminal.setIteration(DEFAULT);
    mixedAlt.getComponentList().add(terminal);
    ASTNonTerminal schemaVarNameOptional = schemaVarName.deepClone();
    schemaVarNameOptional.setIteration(ASTConstantsGrammar.QUESTION);
    mixedAlt.getComponentList().add(schemaVarNameOptional);
    terminal = GrammarMill.terminalBuilder().uncheckedBuild();
    terminal.setName("[[");
    terminal.setIteration(DEFAULT);
    mixedAlt.getComponentList().add(terminal);
    ASTNonTerminal nonTerminal = GrammarMill.nonTerminalBuilder().uncheckedBuild();
    nonTerminal.setUsageName(srcNode.getName());
    nonTerminal.setName(srcNode.getName());
    nonTerminal.setGenSymbol(false);
    nonTerminal.setPlusKeywords(false);
    nonTerminal.setIteration(DEFAULT);
    mixedAlt.getComponentList().add(nonTerminal);
    terminal = GrammarMill.terminalBuilder().uncheckedBuild();
    terminal.setName("]]");
    terminal.setIteration(DEFAULT);
    mixedAlt.getComponentList().add(terminal);

    result.setAltList(patternAlts);

    // create "implements ..."
    result.getSuperInterfaceRuleList().clear();
    ASTRuleReference superInterface = GrammarMill.ruleReferenceBuilder().uncheckedBuild();
    superInterface.setName("ITF" + srcNode.getName());
    result.getSuperInterfaceRuleList().add(superInterface);
    superInterface = GrammarMill.ruleReferenceBuilder().uncheckedBuild();
    superInterface.setName("I" + srcNode.getSymbol().getEnclosingScope().getName() + "TFPart");
    result.getSuperInterfaceRuleList().add(superInterface);


    // create "astimplements ..."
    result.getASTSuperInterfaceList().add(parseGenericType(PSYM_PATTERN));

    // change name
    result.setName(srcNode.getName() + PATTERN_SUFFIX);

    GrammarTraverser grammarTraverser = GrammarMill.traverser();
    grammarTraverser.add4Grammar(new PostprocessPatternAttributesVisitor(grammarSymbol));
    result.accept(grammarTraverser);
    return result;
  }

  /**
   * Creates the TFObject production which is the top level production of the
   * transformation language. Should be called just once.
   *
   * @param grammarSymbol The generating grammar
   * @return the tfobjects production as an ASTClassProd object
   */
  public ASTClassProd createTFRuleProduction(MCGrammarSymbol grammarSymbol) {
    StringBuilder tfObjectsProduction = new StringBuilder(grammarSymbol.getName() + PSYM_TFOBJECTS + " = TFRule;");
    return parseClassProd(tfObjectsProduction.toString());
  }

  /**
   * Creates an interface production for a constant production of the source
   * language. Should be called only one for every constant of the source language.
   *
   * @param name the name of the constant
   * @return the interface production as an ASTInterface object
   */
  public ASTInterfaceProd createInterfaceProdForConstant(String name) {
    return GrammarMill.interfaceProdBuilder().setName(name).build();
  }

  /**
   * Creates Interface Productions for keywords
   *
   * @param srcNode the keyword(s) to create an interface for
   * @return the interface production as an ASTInterface object
   */
  public ASTInterfaceProd createInterfaceProd(ASTConstantGroup srcNode, MCGrammarSymbol grammarSymbol) {
    return createInterfaceProdForConstant("ITF" + grammarSymbol.getName() + "_" + DSTLUtil.getNameForConstant(srcNode) + "_Constant");
  }

  /**
   * creates negative elements for keywords
   *
   * @param name          name of the keyword
   * @param grammarSymbol grammar which is currently dealt with
   * @return the production for the negative element
   */
  public ASTClassProd createNegationProdForConstant(MCGrammarSymbol grammarSymbol, String name) {
    String nameWithPrefix = grammarSymbol.getName() + "_" + name;
    return parseClassProd(nameWithPrefix + "_Constant_Neg implements ITF" + nameWithPrefix
                    + "_Constant astimplements de.monticore.tf.ast.IAttributeNegation = "
                    + "\"not\"  \"[[\" " + nameWithPrefix + "_Constant_Pat \"]]\";");
  }

  /**
   * creates optional elements for keywords
   *
   * @param name          name of the keyword
   * @param grammarSymbol grammar which is currently dealt with
   * @return the production for the optional element
   */
  public ASTClassProd createOptionalProdForConstant(MCGrammarSymbol grammarSymbol, String name) {
    String nameWithPrefix = grammarSymbol.getName() + "_" + name;
    return parseClassProd(nameWithPrefix + "_Constant_Opt implements ITF" + nameWithPrefix
        + "_Constant astimplements de.monticore.tf.ast.IAttributeOptional = "
        + "\"opt\"  \"[[\" " + nameWithPrefix + "_Constant_Pat \"]]\";");
  }

  public ASTClassProd createProd(ASTProd srcNode, ProductionType type, boolean superExternal) {
    return this.createProd(srcNode, type, superExternal, false);
  }


  /**
   * creates a class prod for a given production and type, e.g. replacement to be created
   *
   * @param srcNode node to create a production for
   * @param type    type of production to be created
   * @return production fo the given type
   */
  public ASTClassProd createProd(ASTProd srcNode,
                                 ProductionType type,
                                 boolean superExternal,
                                 boolean specialRecursion) {
    final String name = srcNode.getName();
    String relation = superExternal ? " extends " : " implements ";
    String nonterminal = "ITF" + name;

    //TODO: When to use special name vs
    String specialname = specialRecursion ? nonterminal : (name + "_Pat");

    if (type.equals(ProductionType.OPTIONAL)) {
      String classProdAsString =
              name + "_" + type
                      .getNameString() + (specialRecursion ? "" : helpRelation(
                      superExternal,
                      "ITF" + name, "I" + srcNode.getSymbol().getEnclosingScope().getName() + "TFPart")) + " astimplements de.monticore.tf.ast.I" + "Optional" + " = "
                      + createPrefix(name, type) + " \"[[\" "
                      + StringTransformations.uncapitalize(
                      name) + ":" + nonterminal + " \"]]\";";

      return parseClassProd(classProdAsString);
    } else if (type.equals(ProductionType.NEGATION)) {
      String classProdAsString =
              name + "_" + type
                      .getNameString() + (specialRecursion ? "" : helpRelation(
                      superExternal,
                      "ITF" + name, "I" + srcNode.getSymbol().getEnclosingScope().getName() + "TFPart")) + " astimplements de.monticore.tf.ast.I" + "Negation" + " = "
                      + createPrefix(name, type) + " \"[[\" "
                      + StringTransformations.uncapitalize(
                      name) + ":" + (nonterminal /* specialName */) + " \"]]\";";

      return parseClassProd(classProdAsString);
    } else {
      String classProdAsString =
              name + "_" + type.getNameString() + helpRelation(
                      superExternal,
                      "ITF" + name, "I" + srcNode.getSymbol().getEnclosingScope().getName() + "TFPart") + " astimplements de.monticore.tf.ast.I" + type
                      .getNameString() + " = "
                      + createPrefix(name,
                                     type) + PSYM_SCHEMAVAR + NAME + COLON + NAME + "? \"[[\" "
                      + StringTransformations.uncapitalize(
                      name) + ":" + specialname + " \"]]\";";

      return parseClassProd(classProdAsString);
    }
  }

  private static String helpRelation(boolean doExtend,
                                     String fullName, String tfPartName) {
    return doExtend ? " extends " + fullName + " implements "+tfPartName+" " : " implements " + fullName + ", "+tfPartName+" ";
  }

  /**
   * creates a class production for replacements of keywords
   *
   * @param name          the name of the keyword
   * @param grammarSymbol grammar which is currently dealt with
   * @return the production for replacements
   */
  public ASTClassProd createReplacementProdForConstant(
          MCGrammarSymbol grammarSymbol, String name) {
    String nameWithPrefix = grammarSymbol
            .getName() + "_" + name;
    String classProdAsString = nameWithPrefix + "_Constant_Rep implements ITF" + nameWithPrefix
            + "_Constant astimplements de.monticore.tf.ast.IAttributeReplacement = "
            + " ((\"[[\" lhs:" + nameWithPrefix + "_Constant_Pat \":-\" \"]]\")"
            + "| (\"[[\" \":-\" rhs:" + nameWithPrefix + "_Constant_Pat \"]]\"));";

    return parseClassProd(classProdAsString);
  }

  /**
   * creates a class production representing the pattern for keywords
   *
   * @param srcNode       node for the keywords
   * @param grammarSymbol grammar which is currently dealt with
   * @param name          name of interface to be implemented
   * @return the production for patterns
   */
  public ASTClassProd createPatternProd(
          ASTConstantGroup srcNode,
          MCGrammarSymbol grammarSymbol, String name) {
    StringBuilder constant = new StringBuilder();
    if (srcNode.getConstant(0).isPresentUsageName() && srcNode
            .getConstant(0).getUsageName() != null) {
      constant.append(srcNode.getConstant(0).getUsageName())
              .append(":\"")
              .append(srcNode.getConstant(0).getName())
              .append("\"");
    } else if (srcNode.getConstant(0).isPresentUsageName()) {
      constant.append("\"")
              .append(capitalize(
                      srcNode.getConstant(0).getUsageName()))
              .append("\"").append(":\"")
              .append(srcNode.getConstant(0).getName())
              .append("\"");
    } else {
      constant.append("\"")
              .append(srcNode.getConstant(0).getName())
              .append("\"");
    }
    for (int i = 1; i < srcNode.getConstantList().size(); i++) {
      constant.append(OR);
      if (srcNode.getConstant(i)
              .isPresentUsageName() && !srcNode.getConstant(i)
              .isPresentUsageName()) {
        constant.append(srcNode.getConstant(i).getUsageName())
                .append(COLON);
      }
      constant.append("\"")
              .append(srcNode.getConstant(i).getName())
              .append("\"");
    }
    String nameWithPrefix = grammarSymbol
            .getName() + "_" + name;
    String rule = nameWithPrefix + "_Constant_Pat implements ITF" + nameWithPrefix
            + "_Constant astimplements de.monticore.tf.ast.IAttributePattern = "
            + name + ":[" + constant.toString() + "];";

    return parseClassProd(rule);
  }

  /**
   * creates interface productions for external productions
   *
   * @param srcNode the external production
   * @return the created interface production
   */
  public ASTAbstractProd createAbstractProd(
          ASTExternalProd srcNode) {
    final String name = srcNode.getName();
    final String tfReplacementRule = "abstract ITF" + name + " astimplements de.monticore.tf.ast.ITFElement;";

    return parseAbstractProd(tfReplacementRule);
  }

  public ASTClassProd createPatternProd(
          ASTExternalProd srcNode) {
    final String name = srcNode.getName();
    final String tfPatternRule =
            name + "_Pat implements ITF" + name + " astimplements de.monticore.tf.ast.IPattern ="
                    + "(" + "\"" + name + "\"" + "((" + PSYM_SCHEMAVAR + NAME + COLON + NAME
                    + "\";\") |(" + PSYM_SCHEMAVAR + NAME + COLON + NAME + "? \"[[\" " + name
                    + "Content" + "\"]]\" )  ) );";

    return parseClassProd(tfPatternRule);
  }

  /**
   * creates class productions for replacements of external productions
   *
   * @param srcNode the external production
   * @return the created class production
   */
  public ASTClassProd createReplacementProd(
          ASTExternalProd srcNode) {
    final String name = srcNode.getName();
    final String tfReplacementRule =
            name + "_Rep implements ITF" + name + "ABC astimplements de.monticore.tf.ast.IReplacement = "
                    + "(" + createPrefix(name,
                                         ProductionType.REPLACEMENT) + "\"[[\" lhs:" + "ITF" + name
                    + "? \":-\" "
                    + " rhs:" + "ITF" + name + "? \"]]\");";

    return parseClassProd(tfReplacementRule);
  }

  /**
   * creates a class production corresponding to the production type for external productions
   *
   * @param srcNode the external production
   * @param type    the type to be created e.g. list
   * @return the created class production
   */
  public ASTClassProd createProd(ASTExternalProd srcNode,
                                 ProductionType type) {
    final String name = srcNode.getName();
    String nonterminal = "ITF" + name;
    if (type.equals(ProductionType.OPTIONAL)) {
      final String classProdAsString =
              name + "_" + type
                      .getNameString() + " implements ITF" + name + " astimplements de.monticore.tf.ast.I"
                      + "Optional" + " =  \"[[\" "
                      + StringTransformations.uncapitalize(
                      name) + ":" + nonterminal + " \"]]\";";
      return parseClassProd(classProdAsString);
    } else if (type.equals(ProductionType.NEGATION)) {
      final String classProdAsString =
              name + "_" + type
                      .getNameString() + " implements ITF" + name + " astimplements de.monticore.tf.ast.I"
                      + "Negation" + " =  \"[[\" "
                      + StringTransformations.uncapitalize(
                      name) + ":" + name + "_Pat \"]]\";";
      return parseClassProd(classProdAsString);
    } else {
      final String classProdAsString =
              name + "_" + type
                      .getNameString() + " implements ITF" + name + " astimplements de.monticore.tf.ast.I"
                      + type.getNameString() + " = "
                      + createPrefix(name,
                                     type) + PSYM_SCHEMAVAR + NAME + COLON + NAME + "? \"[[\" "
                      + StringTransformations.uncapitalize(
                      name) + ":" + name + "_Pat \"]]\";";
      return parseClassProd(classProdAsString);
    }
  }

  protected ASTMCType parseGenericType(String type) {
    Grammar_WithConceptsParser ggtp = new Grammar_WithConceptsParser();
    try {
      return ggtp.parse_StringMCType(type).get();
    } catch (IOException e) {
      throw new RuntimeException(
              "0xF1002 Unable to create GenericType for " + type);
    }
  }

  protected ASTInterfaceProd parseInterfaceProd(
          String tfReplacementRule) {
    Grammar_WithConceptsParser p = new Grammar_WithConceptsParser();
    try {
      return p.parse_StringInterfaceProd(tfReplacementRule)
              .get();
    } catch (IOException e) {
      throw new RuntimeException(
              "0xF1005 Unable to create Interface prod for" + tfReplacementRule);
    }
  }

  protected ASTAbstractProd parseAbstractProd(
          String tfReplacementRule) {
    Grammar_WithConceptsParser p = new Grammar_WithConceptsParser();
    try {
      return p.parse_StringAbstractProd(tfReplacementRule)
              .get();
    } catch (IOException e) {
      throw new RuntimeException(
              "0xF1001 Unable to create Interface prod for" + tfReplacementRule);
    }
  }

  protected ASTClassProd parseClassProd(String input) {
    Grammar_WithConceptsParser p = new Grammar_WithConceptsParser();
    try {
      return p.parse_StringClassProd(input).get();
    } catch (IOException e) {
      throw new RuntimeException(
              "0xF1004 Unable to create ClassProd for " + input);
    }
  }

  private String createPrefix(String name,
                              ProductionType type) {
    switch (type) {
      case LIST:
        return "\"" + "list" + "\" (\"<" + name + ">\")? ";
      case NEGATION:
        return "\"not\"";
      case OPTIONAL:
        return "\"opt\"";
      default:
        return "";
    }
  }

}
