/* (c) https://github.com/MontiCore/monticore */
package de.monticore.dstlgen.grammartransformation;

import de.monticore.ast.Comment;
import de.monticore.dstlgen.ruletranslation.DSTLGenInheritanceHelper;
import de.monticore.dstlgen.util.DSTLUtil;
import de.monticore.grammar.grammar.GrammarMill;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._visitor.GrammarTraverser;
import de.monticore.types.MCTypeFacade;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.monticore.grammar.grammar._ast.ASTConstantsGrammar.DEFAULT;
import static de.monticore.grammar.grammar._ast.ASTConstantsGrammar.QUESTION;
import static de.se_rwth.commons.StringTransformations.capitalize;
import static de.se_rwth.commons.StringTransformations.uncapitalize;

/**
 * This singleton generates several lexical and syntactical
 * productions that are used for the generation of transformation
 * languages. This class provides some constant Symbols that are used
 * when creating a transformation language from a grammar. All constants
 * that start with LSYM are names of lexical tokens. All constants that
 * start with PSYM are names of parser productions.
 */
public class ProductionFactory {

  private static final String PATTERN_SUFFIX = "_Pat";
  private static final String NAME = "Name";
  public static final String PSYM_TFIDENTIFIER = "TfIdentifier";
  public static final String PSYM_TFTFSchema = "TFSchema";
  public static final String PSYM_TFOBJECTS = "TFRule";
  public static final String PSYM_SCHEMAVAR = "schemaVar";
  public static final String PSYM_PATTERN = "de.monticore.tf.ast.IPattern";

  public static final String NONTERM_PREFIX = "ITF";
  public static final String LEXPROD_PREFIX = "LexTF";

  protected final MCTypeFacade mcTypeFacade = MCTypeFacade.getInstance();

  protected static ProductionFactory instance = null;

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

    ASTInterfaceProdBuilder builder = GrammarMill.interfaceProdBuilder()
            .setName(NONTERM_PREFIX + name) // interface ITF${name}
            // astextends de.monticore.tf.ast.ITFElement
            .addASTSuperInterface(mcTypeFacade.createQualifiedType("de.monticore.tf.ast.ITFElement"));

    boolean isEmpty = DSTLUtil.isEmptyProduction(srcNode.getSymbol());

    ASTInterfaceProd result = builder.build();
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
    return this.createInterfaceProd(srcNode, grammar_depth, true);
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
    String ITFPart = "I" + srcNode.getSymbol().getEnclosingScope().getName() + "TFPart";
    ASTInterfaceProdBuilder builder = GrammarMill.interfaceProdBuilder()
            .setName(NONTERM_PREFIX + srcNode.getName()) // interface ITF${name}
            // astextends de.monticore.tf.ast.ITFElement
            .addASTSuperInterface(mcTypeFacade.createQualifiedType("de.monticore.tf.ast.ITFElement"));
    if (!reduceParserAlts) {
      // extends ITFPart<grammar_depth>
      builder.addSuperInterfaceRule(GrammarMill.ruleReferenceBuilder().setName(ITFPart).setPrio(Integer.toString(grammar_depth)).build());
    }

    ASTInterfaceProd result = builder.build();
    for (ASTRuleReference r : srcNode.getSuperInterfaceRuleList()) {
      copyandAdaptNonterminal(result, r);
    }

    result.add_PreComment(new Comment("/* NO extends "+ITFPart+ "<?> */"));
    return result;
  }


  public ASTInterfaceProd createInterfaceProd(ASTAbstractProd srcNode) {
    //interface ITF${srcNode.getName()} astextends de.monticore.tf.ast.ITFElement
    ASTInterfaceProd result = GrammarMill.interfaceProdBuilder()
            .setName(NONTERM_PREFIX + srcNode.getName())
            .addASTSuperInterface(mcTypeFacade.createQualifiedType("de.monticore.tf.ast.ITFElement"))
            .build();
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
    ASTClassProdBuilder builder = GrammarMill.classProdBuilder()
            .setName(name + "_" + "Rep");

    if (superExternal)   // ${name_Rep} extends ITF${name}
      builder.addSuperRule(GrammarMill.ruleReferenceBuilder().setName(NONTERM_PREFIX + name).build());
    else                // ${name_Rep} implements ITF${name}
      builder.addSuperInterfaceRule(GrammarMill.ruleReferenceBuilder().setName(NONTERM_PREFIX + name).build());
    // implements I${grammar}TFPart
    builder.addSuperInterfaceRule(GrammarMill.ruleReferenceBuilder().setName("I" + srcNode.getSymbol().getEnclosingScope().getName() + "TFPart").build());
    // astimplements de.monticore.tf.ast.IReplacement
    builder.addASTSuperInterface(mcTypeFacade.createQualifiedType("de.monticore.tf.ast.IReplacement"));

    // "(" + createPrefix(name, ProductionType.REPLACEMENT) + " \"[[\" lhs:" + "ITF" + name + "?"
    // ReplacementOp
    //  "rhs:" + "ITF" + name + "? \"]]\" );";
    builder.addAlt(
            GrammarMill.altBuilder()
                    .addComponent(GrammarMill.blockBuilder().addAlt( // overkill block?
                                    GrammarMill.altBuilder()
                                            .addComponent(GrammarMill.terminalBuilder().setName("[[").build())
                                            .addComponent(GrammarMill.nonTerminalBuilder().setName(NONTERM_PREFIX + name).setUsageName("lhs").setIteration(ASTConstantsGrammar.QUESTION).build())
                                            .addComponent(GrammarMill.nonTerminalBuilder().setName("ReplacementOp").build())
                                            .addComponent(GrammarMill.nonTerminalBuilder().setName(NONTERM_PREFIX + name).setUsageName("rhs").setIteration(ASTConstantsGrammar.QUESTION).build())
                                            .addComponent(GrammarMill.terminalBuilder().setName("]]").build())
                                            .build())
                            .build()
                    ).build());

    return builder.build();
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

    // prepare post-processing of the (cloned) original rules parts to modify them
    GrammarTraverser postProcessingTraverser = GrammarMill.traverser();
    postProcessingTraverser.add4Grammar(new PostprocessPatternAttributesVisitor(grammarSymbol));

    // create "astimplements ..."
    result.getASTSuperInterfaceList().add(MCTypeFacade.getInstance().createQualifiedType(PSYM_PATTERN));

    // change name
    result.setName(srcNode.getName() + PATTERN_SUFFIX);
    // add action
    if (srcNode.isPresentAction()) {
      result.setAction(srcNode.getAction().deepClone());
      result.getAction().accept(postProcessingTraverser);
      postProcessingTraverser.clearTraversedElements();
    }

    // And the alternates for the concrete pattern
    result.setAltList(createPatternAlternates(srcNode, postProcessingTraverser, skipForSpecialRecursion));

    return result;
  }

  protected static List<ASTAlt> createPatternAlternates(ASTClassProd srcNode, GrammarTraverser postProcessingTraverser,  boolean skipForSpecialRecursion) {
    // three notations for a pattern (see Rule 3c of 6.2.3 [Hoe18])
    List<ASTAlt> patternAlts = new ArrayList<>();

    // originalAlt may be multiple
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

    // second notation: modified copy of original production
    List<ASTAlt> secondNotationAlts = new ArrayList<>();
    //Note: ANTRL does not accept left recursion in blocks
    // We thus add the alts for the 2nd notation to the _pat production
    // and store a copy in the secondNotationAlts list
    // - which is sandwiched in [[ ( $alts ) ]], thus not being left recursive
    for (ASTAlt a : srcNode.getAltList()) {
      ASTAlt aDeepClone = a.deepClone();
      postProcessingTraverser.clearTraversedElements();
      aDeepClone.accept(postProcessingTraverser);
      aDeepClone.setRightAssoc(false);
      secondNotationAlts.add(aDeepClone);
      patternAlts.add(aDeepClone);
    }

    //third notation: combination of schema variable and concrete syntax
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

    // include the (modified) concrete syntax/body again (similiar to notation 2),
    // but wrapped within a block to avoid escaping alts
    terminal = GrammarMill.terminalBuilder().uncheckedBuild();
    terminal.setName("[[");
    terminal.setIteration(DEFAULT);
    mixedBlockAlt.getComponentList().add(terminal);
    ASTBlock copyOfSecondBlock = GrammarMill.blockBuilder().addAllAlt(secondNotationAlts).uncheckedBuild();
    mixedBlockAlt.getComponentList().add(copyOfSecondBlock.deepClone());
    terminal = GrammarMill.terminalBuilder().uncheckedBuild();
    terminal.setName("]]");
    terminal.setIteration(DEFAULT);
    mixedBlockAlt.getComponentList().add(terminal);

    return patternAlts;
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

    if (srcNode.getSymbol().isIsLexerProd())
      result.add_PreComment(new Comment(" /* I am a lexer prod */ "));

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

    // syntax in parentheses
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
    nonTerminal.setUsageName(uncapitalize(srcNode.getName()));
    nonTerminal.setName(NONTERM_PREFIX + srcNode.getName());
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
    result.getASTSuperInterfaceList().add(MCTypeFacade.getInstance().createQualifiedType(PSYM_PATTERN));

    // change name
    result.setName(srcNode.getName() + PATTERN_SUFFIX);

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
    // ${grammarname}TFRule = TFRule;
    return GrammarMill.classProdBuilder()
            .setName(grammarSymbol.getName() + PSYM_TFOBJECTS)
            .addAlt(GrammarMill.altBuilder()
                    .addComponent(GrammarMill.nonTerminalBuilder().setName(PSYM_TFOBJECTS).build()).build())
            .build();
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
    // ${nameWithPrefix}_Constant_Neg
    return GrammarMill.classProdBuilder()
            .setName(nameWithPrefix + "_Constant_Neg")
            // implements ITF${nameWithPrefix}
            .addSuperInterfaceRule(GrammarMill.ruleReferenceBuilder().setName("ITF" + nameWithPrefix + "_Constant").build())
            // astimplements de.monticore.tf.ast.IAttributeNegation
            .addASTSuperInterface(mcTypeFacade.createQualifiedType("de.monticore.tf.ast.IAttributeNegation"))
            // = "not" "[[" ${nameWithPrefix}_Constant_Pat "]]";
            .addAlt(
                    GrammarMill.altBuilder()
                            .addComponent(GrammarMill.terminalBuilder().setName("not").build())
                            .addComponent(GrammarMill.terminalBuilder().setName("[[").build())
                            .addComponent(GrammarMill.nonTerminalBuilder().setName(nameWithPrefix + "_Constant_Pat").build())
                            .addComponent(GrammarMill.terminalBuilder().setName("]]").build())
                            .build()
            )
            .build();
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
    // ${nameWithPrefix}_Constant_Opt
    return GrammarMill.classProdBuilder()
            .setName(nameWithPrefix + "_Constant_Opt")
            // implements ITF${nameWithPrefix}_Constant
            .addSuperInterfaceRule(GrammarMill.ruleReferenceBuilder().setName("ITF" + nameWithPrefix + "_Constant").build())
            // astimplements de.monticore.tf.ast.IAttributeOptional
            .addASTSuperInterface(mcTypeFacade.createQualifiedType("de.monticore.tf.ast.IAttributeOptional"))
            // = "opt" "[[" ${nameWithPrefix}_Constant_Pat "]]"
            .addAlt(
                    GrammarMill.altBuilder()
                            .addComponent(GrammarMill.terminalBuilder().setName("opt").build())
                            .addComponent(GrammarMill.terminalBuilder().setName("[[").build())
                            .addComponent(GrammarMill.nonTerminalBuilder().setName(nameWithPrefix + "_Constant_Pat").build())
                            .addComponent(GrammarMill.terminalBuilder().setName("]]").build())
                            .build()
            )
            .build();
  }

  public ASTClassProd createProd(ASTProd srcNode, ProductionType type, boolean superExternal) {
    return this.createProd(srcNode, type, superExternal, false);
  }


  /**
   * creates a class prod for a given production and type, e.g. replacement to be created
   *
   * @param srcNode node to create a production for
   * @param type    type of production to be created
   * @param superExternal if one of the super productions is an external one
   * @param specialRecursion if the srcNode is left recursive
   * @return production fo the given type
   */
  public ASTClassProd createProd(ASTProd srcNode,
                                 ProductionType type,
                                 boolean superExternal,
                                 boolean specialRecursion) {
    final String name = srcNode.getName();
    String nonterminal = "ITF" + name;

    // ${name}_{type}
    ASTClassProdBuilder builder = GrammarMill.classProdBuilder()
            .setName(name + "_" + type.getNameString());
    ASTAltBuilder altBuilder = GrammarMill.altBuilder();
    if (!specialRecursion || (type != ProductionType.OPTIONAL && type != ProductionType.NEGATION)) {
      if (superExternal) {
        // extends ITF${name}
        builder.addSuperRule(GrammarMill.ruleReferenceBuilder().setName("ITF" + name).build());
      } else {
        // implements ITF${name}
        builder.addSuperInterfaceRule(GrammarMill.ruleReferenceBuilder().setName("ITF" + name).build());
      }
      // implements I${grammarname}TFPart
      builder.addSuperInterfaceRule(GrammarMill.ruleReferenceBuilder().setName("I" + srcNode.getSymbol().getEnclosingScope().getName() + "TFPart").build());
    }

    // Use the prod-pattern-interface, in case of non-left-recursiveness
    String nonTermNameOrPattern = nonterminal;

    if (type == ProductionType.OPTIONAL) {
      // "opt" "[[" ${name}:ITF${name} ]]
      builder.addASTSuperInterface(mcTypeFacade.createQualifiedType("de.monticore.tf.ast.IOptional"));
      altBuilder.addComponent(GrammarMill.terminalBuilder().setName("opt").build());
    } else if (type == ProductionType.NEGATION) {
      // "not" "[[" ${name}:ITF${name} ]]
      builder.addASTSuperInterface(mcTypeFacade.createQualifiedType("de.monticore.tf.ast.INegation"));
      altBuilder.addComponent(GrammarMill.terminalBuilder().setName("not").build());
    } else if (type == ProductionType.LIST) {
      // "list "<${name}>"? schemaVarName:Name? "[[" ${name}:ITF${name} ]]
      builder.addASTSuperInterface(mcTypeFacade.createQualifiedType("de.monticore.tf.ast.I" + type.getNameString()));
      altBuilder.addComponent(GrammarMill.terminalBuilder().setName("list").build());
      altBuilder.addComponent(GrammarMill.terminalBuilder().setName("<" + name + ">").setIteration(QUESTION).build());
      // left recursive prods currently only support pattern matching (instead of using the interface)
      // DISCUSS: evaluate left-rec support
      nonTermNameOrPattern = specialRecursion ? nonterminal : (name + "_Pat");
      altBuilder.addComponent(GrammarMill.nonTerminalBuilder().setUsageName(PSYM_SCHEMAVAR + NAME).setName(NAME).setIteration(QUESTION).build());
    } else {
      // schemaVarName:Name? "[[" ${name}:ITF${name} ]]
      builder.addASTSuperInterface(mcTypeFacade.createQualifiedType("de.monticore.tf.ast.I" + type.getNameString()));

      altBuilder.addComponent(GrammarMill.nonTerminalBuilder().setUsageName(PSYM_SCHEMAVAR + NAME).setName(NAME).setIteration(QUESTION).build());

      nonTermNameOrPattern = specialRecursion ? nonterminal : (name + "_Pat");
    }
    // "[[" ${name?uncap_first}:${nonTermNameOrPattern} "]]"
    altBuilder.addComponent(GrammarMill.terminalBuilder().setName("[[").build());
    altBuilder.addComponent(GrammarMill.nonTerminalBuilder().setUsageName(StringTransformations.uncapitalize(name)).setName(nonTermNameOrPattern).build());
    altBuilder.addComponent(GrammarMill.terminalBuilder().setName("]]").build());

    builder.addAlt(altBuilder.build());

    return builder.build();

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

    // ${nameWithPrefix}_Constant_Rep
    return GrammarMill.classProdBuilder()
            .setName(nameWithPrefix + "_Constant_Rep")
            // implements ITF${name}_Constant
            .addSuperInterfaceRule(GrammarMill.ruleReferenceBuilder().setName("ITF" + nameWithPrefix + "_Constant").build())
            // astimplements de.monticore.tf.ast.IAttributeReplacement
            .addASTSuperInterface(mcTypeFacade.createQualifiedType("de.monticore.tf.ast.IAttributeReplacement"))
            .addAlt( // = "[[" lhs:${nameWithPrefix}_Constant_Pat :- "]]"
                    GrammarMill.altBuilder()
                            .addComponent(GrammarMill.terminalBuilder().setName("[[").build())
                            .addComponent(GrammarMill.nonTerminalBuilder().setUsageName("lhs").setName(nameWithPrefix + "_Constant_Pat").build())
                            .addComponent(GrammarMill.terminalBuilder().setName(":-").build())
                            .addComponent(GrammarMill.terminalBuilder().setName("]]").build())
                            .build()
            )
            .addAlt( // | "[[" :- rhs:${nameWithPrefix}_Constant_Pat "]]"
                    GrammarMill.altBuilder()
                            .addComponent(GrammarMill.terminalBuilder().setName("[[").build())
                            .addComponent(GrammarMill.terminalBuilder().setName(":-").build())
                            .addComponent(GrammarMill.nonTerminalBuilder().setUsageName("rhs").setName(nameWithPrefix + "_Constant_Pat").build())
                            .addComponent(GrammarMill.terminalBuilder().setName("]]").build())
                            .build()
            )
            .build();
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
    List<ASTConstant> constants = new ArrayList<>();
    if (srcNode.getConstant(0).isPresentUsageName() && srcNode
            .getConstant(0).getUsageName() != null) {
      constants.add(GrammarMill.constantBuilder()
              .setUsageName(srcNode.getConstant(0).getUsageName())
              .setString(srcNode.getConstant(0).getName())
              .uncheckedBuild()); // name missing
    } else if (srcNode.getConstant(0).isPresentUsageName()) {
      constants.add(GrammarMill.constantBuilder()
              .setUsageName(capitalize(
                      srcNode.getConstant(0).getUsageName()))
              .setString(srcNode.getConstant(0).getName())
              .uncheckedBuild()); // name missing
    } else {
      constants.add(GrammarMill.constantBuilder()
              .setString(srcNode.getConstant(0).getName())
              .uncheckedBuild()); // name missing
    }
    for (int i = 1; i < srcNode.getConstantList().size(); i++) {
      ASTConstantBuilder constantBuilder = GrammarMill.constantBuilder();
      if (srcNode.getConstant(i).isPresentUsageName()) {
        constantBuilder.setUsageName(srcNode.getConstant(i).getUsageName());
      }
      constantBuilder.setString(srcNode.getConstant(i).getName());
      constants.add(constantBuilder.uncheckedBuild()); // name missing
    }
    // Sanitize the constant name (so special characters such as *,- are handled correctly)
    String constantName = DSTLUtil.getNameForConstant(srcNode);
    String nameWithPrefix = grammarSymbol
            .getName() + "_" + constantName;
    // ${nameWithPrefix}_Constant_Pat
    return GrammarMill.classProdBuilder()
            .setName(nameWithPrefix  + "_Constant_Pat")
            // implements ITF${nameWithPrefix}_Constant
            .addSuperInterfaceRule(GrammarMill.ruleReferenceBuilder().setName("ITF" + nameWithPrefix + "_Constant").build())
            // astimplements de.monticore.tf.ast.IAttributePattern
            .addASTSuperInterface(mcTypeFacade.createQualifiedType("de.monticore.tf.ast.IAttributePattern"))
            // = constantName:[ $constants ]
            .addAlt(GrammarMill.altBuilder()
                    .addComponent(GrammarMill.constantGroupBuilder()
                            .setUsageName(constantName)
                            .setConstantsList(constants).build())
                    .build()
            ).build();
  }

  /**
   * creates interface productions for external productions
   *
   * @param srcNode the external production
   * @return the created interface production
   */
  public ASTAbstractProd createAbstractProd(
          ASTExternalProd srcNode) {
    return GrammarMill.abstractProdBuilder()
            .setName(NONTERM_PREFIX + srcNode.getName()) // abstract ITF${name}
            // astimplements de.monticore.tf.ast.ITFElement
            .addASTSuperInterface(mcTypeFacade.createQualifiedType("de.monticore.tf.ast.ITFElement"))
            .build();
  }



  protected ASTLexProd createLexProd(ASTLexProd srcNode, MCGrammarSymbol grammarSymbol) {
    Log.debug("Creating lex prod for " + srcNode, DSL2TransformationLanguageVisitor.LOG);
    ASTLexProd result = srcNode.deepClone();
    if (DSTLGenInheritanceHelper.getInstance().isLexicalProdKnownInTFCommons(srcNode.getName())) {
      result.add_PreComment(new Comment("/* Leaving this lexical productions name intact , as it (most likely) is overriding another one (or this grammar is a supergrammar of TFCommons) */"));
    }else {
      result.setName(LEXPROD_PREFIX + result.getName());
    }

    // post processing of the class prod
    GrammarTraverser postProcessingTraverser = GrammarMill.traverser();
    postProcessingTraverser.add4Grammar(new PostprocessPatternAttributesVisitor(grammarSymbol));
    result.accept(postProcessingTraverser);

    return result;
  }

  /**
   * Creates an TFIdentifier for a lexical production with a specific structure to not clash with the
   * TFidentifier for Names
   */
  protected Optional<ASTClassProd> createLexIdentifier(ASTLexProd srcNode, MCGrammarSymbol grammarSymbol, ASTMCGrammar tflang) {
    Log.debug("Creating identifier prod for " + srcNode, DSL2TransformationLanguageVisitor.LOG);
    String name = PSYM_TFIDENTIFIER + srcNode.getName();
    String tokenName = DSTLGenInheritanceHelper.getInstance().isLexicalProdKnownInTFCommons(srcNode.getName()) ? srcNode.getName() : (LEXPROD_PREFIX + srcNode.getName());
    if (DSTLGenInheritanceHelper.getInstance().isLexicalProdKnownInTFCommons(name)) {
      // We still add a classprod, but mark it
      tflang.add_PostComment(new Comment("/* Skipping (already known) identifier for lexprod " + srcNode.getName() + " */"));
      return Optional.empty();
    }else if (grammarSymbol.getName().equals("MCBasics")){
      tflang.add_PostComment(new Comment("/* Skipping identifier for lexprod " + srcNode.getName() + " due to being declared in MCBasics */"));
      return Optional.empty();
    }else {

      boolean isOverriding = grammarSymbol.getSuperGrammarSymbols().stream().anyMatch(x->x.getProd(srcNode.getName()).isPresent());
      if (isOverriding) {
        // This is a workaround for CoCo 0xA4025
        tflang.add_PostComment(new Comment("/* Skipping identifier for lexprod " + srcNode.getName() + " as it overrides another one and CoCo 0xA4025 */"));
        return Optional.empty();
      }

      // (identifier:X)
      ASTAlt first = GrammarMill.altBuilder().addComponent(GrammarMill.nonTerminalBuilder().setUsageName("identifierToken").setName(tokenName).build()).build();
      // "[[" identifier:X :- (newIdentifier:TFSchema | newIdentifier:X)? "]]"
      ASTAlt second = GrammarMill.altBuilder()
              .addComponent(GrammarMill.terminalBuilder().setName("[[").build())
              .addComponent(GrammarMill.nonTerminalBuilder().setUsageName("identifierToken").setName(tokenName).build())
              .addComponent(GrammarMill.terminalBuilder().setName(":-").build())
              .addComponent(GrammarMill.blockBuilder()
                      .addAlt(GrammarMill.altBuilder().addComponent(GrammarMill.nonTerminalBuilder().setUsageName("newIdentifierSchema").setName(PSYM_TFTFSchema).build()).build())
                      .addAlt(GrammarMill.altBuilder().addComponent(GrammarMill.nonTerminalBuilder().setUsageName("newIdentifierToken").setName(tokenName).build()).build())
                      .setIteration(ASTConstantsGrammar.QUESTION)
                      .build())
              .addComponent(GrammarMill.terminalBuilder().setName("]]").build())
              .build();
      // "[[" (identifier:TFSchema | identifier:X)? :-   newIdentifier:X "]]"
      ASTAlt third = GrammarMill.altBuilder()
              .addComponent(GrammarMill.terminalBuilder().setName("[[").build())
              .addComponent(GrammarMill.blockBuilder()
                      .addAlt(GrammarMill.altBuilder().addComponent(GrammarMill.nonTerminalBuilder().setUsageName("identifierSchema").setName(PSYM_TFTFSchema).build()).build())
                      .addAlt(GrammarMill.altBuilder().addComponent(GrammarMill.nonTerminalBuilder().setUsageName("identifierToken").setName(tokenName).build()).build())
                      .setIteration(ASTConstantsGrammar.QUESTION)
                      .build())
              .addComponent(GrammarMill.terminalBuilder().setName(":-").build())
              .addComponent(GrammarMill.nonTerminalBuilder().setUsageName("newIdentifierToken").setName(tokenName).build())
              .addComponent(GrammarMill.terminalBuilder().setName("]]").build())
              .build();
      //  "X" identifier:TFSchema ";"
      ASTAlt fourth = GrammarMill.altBuilder()
              .addComponent(GrammarMill.terminalBuilder().setName(srcNode.getName()).build())
              .addComponent(GrammarMill.nonTerminalBuilder().setUsageName("identifierSchema").setName(PSYM_TFTFSchema).build())
              .addComponent(GrammarMill.terminalBuilder().setName(";").build())
              .build();
      return Optional.of(GrammarMill.classProdBuilder().setName(name).addAlt(first).addAlt(second).addAlt(third).addAlt(fourth)
              .addSuperInterfaceRule(GrammarMill.ruleReferenceBuilder().setName(PSYM_TFIDENTIFIER).build())
              .build());
    }
  }

}
