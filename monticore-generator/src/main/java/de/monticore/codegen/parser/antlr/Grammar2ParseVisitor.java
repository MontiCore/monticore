/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.parser.antlr;

import de.monticore.ast.ASTNode;
import de.monticore.cd.facade.*;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.codegen.cd2java._ast.ast_class.ASTConstants;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.codegen.parser.MCGrammarInfo;
import de.monticore.codegen.parser.ParserGeneratorHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.grammar.MCGrammarSymbolTableHelper;
import de.monticore.grammar.PredicatePair;
import de.monticore.grammar.grammar.GrammarMill;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.grammar.grammar._symboltable.RuleComponentSymbol;
import de.monticore.grammar.grammar._visitor.GrammarHandler;
import de.monticore.grammar.grammar._visitor.GrammarTraverser;
import de.monticore.grammar.grammar._visitor.GrammarVisitor2;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;

/**
 * Generates a class, which transforms the ANTLR ParseTree into an AST
 * For moe information, see de.monticore.antlr4.MCBuildVisitor
 * <p>
 * Parser-Preconditions, actions and concepts will still be present in the parser.
 *  (Although without access to the AST-builders)
 */
public class Grammar2ParseVisitor implements GrammarVisitor2, GrammarHandler {

  protected GrammarTraverser grammarTraverser;

  protected final GlobalExtensionManagement glex;
  protected final ParserGeneratorHelper parserGeneratorHelper;
  protected final MCGrammarInfo grammarInfo;

  protected final Stack<ParseVisitorEntry> stack = new Stack<>();
  protected final Map<ASTProd, Map<ASTNode, String>> tmpNameDict;


  protected CDMethodFacade cdMethodFacade = CDMethodFacade.getInstance();
  protected CDConstructorFacade cdConstructorFacade = CDConstructorFacade.getInstance();
  protected CDAttributeFacade cdAttributeFacade = CDAttributeFacade.getInstance();
  protected CDParameterFacade cdParameterFacade = CDParameterFacade.getInstance();
  protected MCTypeFacade mcTypeFacade = MCTypeFacade.getInstance();

  protected ASTCDClass visitorClass;
  protected String antlrParserName;
  protected String millName;

  protected Map<ASTNode, String> tmpNames;

  protected ASTMCType astNodeType = mcTypeFacade.createQualifiedType(ASTNode.class.getName());
  protected ASTMCType objectType = mcTypeFacade.createQualifiedType(Object.class.getName());

  protected Stack<Boolean> ruleIteratedStack = new Stack<>();

  public Grammar2ParseVisitor(GlobalExtensionManagement glex, ParserGeneratorHelper parserGeneratorHelper, MCGrammarInfo grammarInfo, Map<ASTProd, Map<ASTNode, String>> tmpNameDict) {
    this.glex = glex;
    this.parserGeneratorHelper = parserGeneratorHelper;
    this.grammarInfo = grammarInfo;

    this.tmpNameDict = tmpNameDict;
  }

  public ASTCDClass getVisitorClass() {
    return visitorClass;
  }

  @Override
  public GrammarTraverser getTraverser() {
    return grammarTraverser;
  }

  @Override
  public void setTraverser(GrammarTraverser traverser) {
    this.grammarTraverser = traverser;
  }

  @Override
  public void visit(ASTMCGrammar node) {
    antlrParserName = StringTransformations.capitalize(node.getName()) + "AntlrParser";
    millName = parserGeneratorHelper.getQualifiedGrammarName().toLowerCase() + "." + StringTransformations.capitalize(node.getName()) + "Mill";
    visitorClass = CDBasisMill.cDClassBuilder()
            .setName(node.getName() + "ASTBuildVisitor")
            .setModifier(CDModifier.PUBLIC.build())
            .build();
    visitorClass.setCDInterfaceUsage(
            CD4CodeMill.cDInterfaceUsageBuilder()
                    .addInterface(
                            mcTypeFacade.createBasicGenericTypeOf(node.getName() + "AntlrParserVisitor", "Object"))
                    .build());
    visitorClass.setCDExtendUsage(CDBasisMill.cDExtendUsageBuilder().addSuperclass(mcTypeFacade.createQualifiedType("de.monticore.antlr4.MCBuildVisitor")).build());


    ASTCDConstructor constructor = cdConstructorFacade.createConstructor(CDModifier.PUBLIC.build(), visitorClass.getName(),
            cdParameterFacade.createParameter(String.class, "filename"),
            cdParameterFacade.createParameter(BufferedTokenStream.class.getName(), "tokenStream")
    );
    visitorClass.addCDMember(constructor);
    glex.replaceTemplate(EMPTY_BODY, constructor, new TemplateHookPoint("_parser.visitor.Constructor"));

    CD4C.getInstance().addImport(visitorClass, "de.monticore.ast.ASTNode");
    CD4C.getInstance().addImport(visitorClass, String.join(".", parserGeneratorHelper.getQualifiedGrammarName().toLowerCase(), "_ast.*"));
    CD4C.getInstance().addImport(visitorClass, millName);
    CD4C.getInstance().addImport(visitorClass, "org.antlr.v4.runtime.tree.*");
    CD4C.getInstance().addImport(visitorClass, "org.antlr.v4.runtime.Token");
    grammarInfo.getGrammarSymbol().getAllSuperGrammars().forEach(superg ->
            CD4C.getInstance().addImport(visitorClass, String.join(".", superg.getFullName().toLowerCase(), "_ast.*")));

    visitorClass.addCDMember(cdAttributeFacade.createAttribute(CDModifier.PUBLIC.build(), mcTypeFacade.createIntType(), "depth"));

    // add visit(ParseTree) -> tree.accept
    ASTCDMethod m;
    visitorClass.addCDMember((m = cdMethodFacade.createMethod(CDModifier.PUBLIC.build(), objectType, "visit", cdParameterFacade.createParameter(ParseTree.class, "tree"))));
    glex.replaceTemplate(EMPTY_BODY, m, new TemplateHookPoint("_parser.visitor.VisitTree"));
    // add visitChildren(RuleNode) -> EXC
    visitorClass.addCDMember((m = cdMethodFacade.createMethod(CDModifier.PUBLIC.build(), astNodeType, "visitChildren", cdParameterFacade.createParameter(RuleNode.class, "node"))));
    glex.replaceTemplate(EMPTY_BODY, m, new TemplateHookPoint("_parser.visitor.NotImplemented"));
    // add visitErrorNode
    visitorClass.addCDMember((m = cdMethodFacade.createMethod(CDModifier.PUBLIC.build(), astNodeType, "visitErrorNode", cdParameterFacade.createParameter(ErrorNode.class, "node"))));
    glex.replaceTemplate(EMPTY_BODY, m, new TemplateHookPoint("_parser.visitor.NotImplemented"));
    // add visitTerminal
    visitorClass.addCDMember((m = cdMethodFacade.createMethod(CDModifier.PUBLIC.build(), astNodeType, "visitTerminal", cdParameterFacade.createParameter(TerminalNode.class, "node"))));
    glex.replaceTemplate(EMPTY_BODY, m, new TemplateHookPoint("_parser.visitor.NotImplemented"));

    // noKeyword and splittoken introduces terminals, which require a visit method
    List<String> pseudoProductions = new ArrayList<>(grammarInfo.getSplitRules().values());
    for (String s : grammarInfo.getGrammarSymbol().getKeywordRulesWithInherited()) {
      pseudoProductions.add(parserGeneratorHelper.getKeyRuleName(s));
    }
    for (String s : pseudoProductions) {

      String ruleNameCap = StringTransformations.capitalize(s);

      ASTCDMethod method = cdMethodFacade.createMethod(CDModifier.PUBLIC.build(),
              astNodeType,
              "visit" + ruleNameCap,
              cdParameterFacade.createParameter(antlrParserName + "." + ruleNameCap + "Context", "ctx"));
      glex.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_parser.visitor.NotImplemented"));

      visitorClass.addCDMember(method);
    }

  }


  @Override
  public void traverse(ASTMCGrammar node) {
    // Modified traversal: We traverse over all class and interface productions of this grammar and its super grammars
    node.getSymbol().getProdsWithInherited().values().forEach(symbol -> symbol.getAstNode().accept(getTraverser()));
  }

  @Override
  public void handle(ASTExternalProd node) {
    // nothing
  }

  @Override
  public void handle(ASTClassProd ast) {
    if (ast.getSymbol().isIsIndirectLeftRecursive()) {
      // No CodeGen
      return;
    }
    String rulename = Grammar2Antlr.getRuleNameForAntlr(ast.getName());
    String rulenameCap = StringTransformations.capitalize(rulename);

    tmpNames = tmpNameDict.get(ast);
    if (tmpNames == null) {
      // No parser generation for this node (it is most likely overriden)
      return;
    }
    // Rules extending, etc.
    List<PredicatePair> subRules = grammarInfo.getSubRulesForParsing(ast.getName());

    ASTCDMethod method = cdMethodFacade.createMethod(CDModifier.PUBLIC.build(),
            astNodeType,
            "visit" + rulenameCap,
            cdParameterFacade.createParameter(antlrParserName + "." + rulenameCap + "Context", "ctx"));

    ParseVisitorEntry root = new ParseVisitorEntry();
    stack.push(root);

    List<ASTAlt> alts = parserGeneratorHelper.getAlternatives(ast);

    if (subRules != null && !subRules.isEmpty()) {
      int i = 0;
      for (PredicatePair ignored : subRules) {
        ParseVisitorEntry subruleAlt = new ParseVisitorEntry();

        root.alternatives.add(subruleAlt);

        ParseVisitorEntry e = new ParseVisitorEntry();
        e.subrule = "subRuleVar" + i++;

        subruleAlt.blockComponents.add(e);
        subruleAlt.condition = "ctx." + e.subrule + " != null";
      }
    }

    alts.forEach(alt -> alt.accept(getTraverser()));

    root.isRoot = true;

    this.glex.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_parser.visitor.VisitClassProd",
            ast.getName(), millName, root,
            ast.isPresentAction() ? Optional.of(GrammarMill.prettyPrint(ast.getAction(), false)) : Optional.empty()));
    visitorClass.addCDMember(method);


    stack.pop();

  }

  @Override
  public void handle(ASTEnumProd ast) {
    String rulename = Grammar2Antlr.getRuleNameForAntlr(ast.getName());
    String rulenameCap = StringTransformations.capitalize(rulename);
    String enumClass = MCGrammarSymbolTableHelper.getQualifiedName(ast, ast.getSymbol(), "AST", "");
    ASTCDMethod method = cdMethodFacade.createMethod(CDModifier.PUBLIC.build(),
            mcTypeFacade.createQualifiedType(enumClass),
            "visit" + rulenameCap,
            cdParameterFacade.createParameter(antlrParserName + "." + rulenameCap + "Context", "ctx"));

    visitorClass.addCDMember(method);

    List<String> enumConstants = ast.getConstantList().stream().map(parserGeneratorHelper::getConstantNameForConstant)
            .map(cn -> enumClass + "." + cn).collect(Collectors.toList());

    glex.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_parser.visitor.VisitEnum", enumConstants));
  }

  @Override
  public void handle(ASTInterfaceProd ast) {
    handleInterfaceOrAbstract(ast, ast.getSymbol(), ast.getName());
  }

  @Override
  public void handle(ASTAbstractProd ast) {
    handleInterfaceOrAbstract(ast, ast.getSymbol(), ast.getName());
  }

  public void handleInterfaceOrAbstract(ASTProd ast, ProdSymbol symbol, String name) {
    String rulename = Grammar2Antlr.getRuleNameForAntlr(name);
    String rulenameCap = StringTransformations.capitalize(rulename);

    tmpNames = tmpNameDict.get(ast);

    List<AltEntry> alts = new ArrayList<>();
    boolean isLeft = collectAlts(symbol, alts);
    alts.sort(AltEntry::compareTo);

    for (AltEntry alt : alts) {

      ParseVisitorEntry e = new ParseVisitorEntry();
      if (alt.getRuleNode() instanceof ASTAlt) {
        // Left recursive rule
        stack.push(e);
        alt.getRuleNode().accept(getTraverser());
        stack.pop();
        alt.setParseVisitorEntry(e.getAlternatives().get(0));
      } else if (isLeft && alt.getRuleNode() instanceof ASTClassProd && ((ASTClassProd) alt.getRuleNode()).getAltList().size() == 1) {
        stack.push(e);
        ((ASTClassProd) alt.getRuleNode()).getAlt(0).accept(getTraverser());
        stack.pop();
        if (e.getAlternatives().size() != 1) {
          Log.error("0xA0393 Unspected count of alternatives. Expected 1, butfound " + e.getAlternatives().size(),
                  alt.getRuleNode().get_SourcePositionStart());
          return;
        }
        alt.setParseVisitorEntry(e.getAlternatives().get(0));
      } else {
        // normal rule - like a NonTerminal
        String tmpVar = tmpNames.get(alt.getRuleNode());

        alt.setParseVisitorEntry(e);
        e.tmpName = tmpVar;
        e.condition = "ctx." + tmpVar + " != null";
        alt.setSimpleReference(true);
      }
    }


    ASTCDMethod method = cdMethodFacade.createMethod(CDModifier.PUBLIC.build(),
            astNodeType,
            "visit" + rulenameCap,
            cdParameterFacade.createParameter(antlrParserName + "." + rulenameCap + "Context", "ctx"));
    glex.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_parser.visitor.VisitInterface", name, millName, alts));

    visitorClass.addCDMember(method);
  }

  protected boolean collectAlts(ProdSymbol prodSymbol, List<AltEntry> alts) {
    boolean isLeft = false;
    List<PredicatePair> interfaces = grammarInfo.getSubRulesForParsing(prodSymbol.getName());
    for (PredicatePair interf : interfaces) {
      Optional<ProdSymbol> symbol = parserGeneratorHelper.getGrammarSymbol().getSpannedScope().resolveProd(interf.getClassname());
      if (symbol.isEmpty()) {
        continue;
      }
      ProdSymbol superSymbol = symbol.get();
      if (!prodSymbol.isPresentAstNode()) {
        continue;
      }
      if (superSymbol.isIsIndirectLeftRecursive()) {
        isLeft = true;
        if (superSymbol.isClass()) {
          List<ASTAlt> localAlts = ((ASTClassProd) superSymbol.getAstNode()).getAltList();
          for (ASTAlt alt : localAlts) {
            alts.add(new AltEntry(alt, (ASTParserProd) superSymbol.getAstNode(), interf));
          }
        } else if (prodSymbol.isIsInterface()) {
          collectAlts(superSymbol, alts);
        }
      } else {
        alts.add(new AltEntry(superSymbol.getAstNode(), superSymbol.getAstNode(), interf));
      }
    }
    return isLeft;
  }

  public static class AltEntry implements Comparable<AltEntry> {
    protected final ASTNode ruleNode;
    protected final ASTProd builderNode;
    protected final PredicatePair pair;

    protected ParseVisitorEntry parseVisitorEntry;

    protected final int prio;

    protected boolean simpleReference;

    public AltEntry(ASTNode ruleNode, ASTProd builderNode, PredicatePair pair) {
      this.ruleNode = ruleNode;
      this.builderNode = builderNode;
      this.pair = pair;
      this.prio = pair.getRuleReference().isPresentPrio() ? Integer.parseInt(pair.getRuleReference().getPrio()) : 0;
    }

    @Override
    public int compareTo(AltEntry o) {
      return Integer.compare(o.prio, this.prio); // Highest priority first
    }

    public ASTNode getRuleNode() {
      return ruleNode;
    }

    public ASTProd getBuilderNode() {
      return builderNode;
    }

    public PredicatePair getPair() {
      return pair;
    }

    public ParseVisitorEntry getParseVisitorEntry() {
      return parseVisitorEntry;
    }

    public void setParseVisitorEntry(ParseVisitorEntry parseVisitorEntry) {
      this.parseVisitorEntry = parseVisitorEntry;
    }

    public void setSimpleReference(boolean simpleReference) {
      this.simpleReference = simpleReference;
    }

    public boolean isSimpleReference() {
      return simpleReference;
    }

    public String getBuilderNodeName() {
      return this.getBuilderNode().getName();
    }
  }

  protected Set<String> convertMethods = new HashSet<>();

  @Override
  public void handle(ASTLexProd node) {
    if (node.getSymbol().isIsExternal()) return;
    // add convert
    if (convertMethods.contains(node.getName())) return;
    convertMethods.add(node.getName());

    ASTMCType retType = mcTypeFacade.createStringType();
    HookPoint hookPoint = new StringHookPoint("return t.getText();" + "/*" + node.getSymbol().getFullName() + "*/");
    String tokenParamName = "t";
    if (node.isPresentVariable()) {
      if (node.getTypeList() == null || node.getTypeList().isEmpty()) {
        switch (node.getVariable()) {
          case "int":
            retType = mcTypeFacade.createIntType();
            hookPoint = new StringHookPoint("return Integer.parseInt(t.getText());");
            break;
          case "boolean":
            retType = mcTypeFacade.createBooleanType();
            hookPoint = new StringHookPoint("return (t.getText().equals(\"1\")||t.getText().equals(\"start\")||t.getText().equals(\"on\")||t.getText().equals(\"true\"));");
            break;
          case "byte":
            retType = mcTypeFacade.createQualifiedType("byte");
            hookPoint = new StringHookPoint("return Byte.parseByte(t.getText());");
            break;
          case "char":
            retType = mcTypeFacade.createQualifiedType("char");
            hookPoint = new StringHookPoint("return t.getText().charAt(0);");
            break;
          case "float":
            retType = mcTypeFacade.createQualifiedType("float");
            hookPoint = new StringHookPoint("return Float.parseFloat(t.getText());");
            break;
          case "double":
            retType = mcTypeFacade.createQualifiedType("double");
            hookPoint = new StringHookPoint("return Double.parseDouble(t.getText());");
            break;
          case "long":
            retType = mcTypeFacade.createQualifiedType("long");
            hookPoint = new StringHookPoint("return Long.parseLong(t.getText());");
            break;
          case "short":
            retType = mcTypeFacade.createQualifiedType("short");
            hookPoint = new StringHookPoint("return Short.parseShort(t.getText());");
            break;
          case "card":
            retType = mcTypeFacade.createQualifiedType("int");
            hookPoint = new StringHookPoint("return t.getText().equals(\"*\")?-1 : Integer.parseInt(t.getText());");
            break;
          default:
            Log.warn(
                    "0xA1061 No function for " + node.getVariable() + " registered, will treat it as string!");
        }
      } else if (node.isPresentBlock()) { // specific function
        retType = mcTypeFacade.createQualifiedType(Names.constructQualifiedName(node.getTypeList()));
        hookPoint = new StringHookPoint(Grammar_WithConceptsMill.prettyPrint(node.getBlock(), true));
        tokenParamName = node.getVariable();
      }
    }
    ASTCDMethod method = cdMethodFacade.createMethod(CDModifier.PROTECTED.build(),
            retType,
            "convert" + StringTransformations.capitalize(node.getName()),
            cdParameterFacade.createParameter("Token", tokenParamName)
    );
    glex.replaceTemplate(EMPTY_BODY, method, hookPoint);
    visitorClass.addCDMember(method);

  }


  @Override
  public void handle(ASTAlt node) {
    ParseVisitorEntry prev = stack.peek();
    ParseVisitorEntry alt = new ParseVisitorEntry();
    prev.alternatives.add(alt);
    stack.push(alt);
    GrammarHandler.super.handle(node);
    stack.pop();

    alt.blockComponents.forEach(c -> alt.allOptConditions.addAll(c.allOptConditions));

    List<ParseVisitorEntry> nonOptEntries = alt.blockComponents.stream().filter(e -> e.condition != null).filter(f -> !f.ruleOptional).collect(Collectors.toList());
    if (nonOptEntries.isEmpty()) {
      if (alt.allOptConditions.stream().noneMatch(Objects::nonNull))
        alt.condition = null;
      else
        alt.condition = ("/* alt all opt */ (" + String.join("||", alt.allOptConditions) + ")");
    } else {
      alt.condition = "(" + nonOptEntries.stream().map(e -> e.condition).filter(Objects::nonNull).collect(Collectors.joining(" && ")) + ")";
    }

    // remove conditions, as it has been moved upwards
    nonOptEntries.forEach(e -> e.condition = null);

    if (nonOptEntries.isEmpty() && alt.blockComponents.size() == 1)
      alt.blockComponents.forEach(e -> e.condition = null); // remove sub ones only iff one non-default is there


    if (alt.condition != null)
      alt.condition += " /*from alt*/";

    if (alt.blockComponents.isEmpty()) {
      // Add an empty alt for the generation
      prev.alternatives.remove(alt);
    }

  }


  @Override
  public void handle(ASTBlock node) {
    ParseVisitorEntry blockEntry = new ParseVisitorEntry();

    blockEntry.ruleOptional = node.getIteration() == ASTConstantsGrammar.QUESTION || node.getIteration() == ASTConstantsGrammar.STAR;
    ruleIteratedStack.push((!ruleIteratedStack.isEmpty() && ruleIteratedStack.peek()) || node.getIteration() == ASTConstantsGrammar.STAR || node.getIteration() == ASTConstantsGrammar.PLUS);

    ParseVisitorEntry prev = stack.peek();
    prev.blockComponents.add(blockEntry);
    stack.push(blockEntry);

    GrammarHandler.super.handle(node);

    stack.pop();

    ruleIteratedStack.pop();

    blockEntry.alternatives.forEach(c -> blockEntry.allOptConditions.addAll(c.allOptConditions));

    // no else for (A | B)*
    if (node.getIteration() == ASTConstantsGrammar.PLUS || node.getIteration() == ASTConstantsGrammar.STAR) {
      blockEntry.alternatives.forEach(c -> c.setNoAltElseRec(true));
    }

    // A | B => A || B
    List<ParseVisitorEntry> nonOptEntries = blockEntry.alternatives.stream().filter(e -> e.condition != null).filter(f -> !f.ruleOptional).collect(Collectors.toList());
    if (nonOptEntries.isEmpty()) {
      if (blockEntry.allOptConditions.stream().anyMatch(Objects::nonNull))
        blockEntry.condition = "/* block all opt */ (" + String.join("||", blockEntry.allOptConditions) + ")";
      else
        blockEntry.condition = null;
    } else {
      blockEntry.condition = nonOptEntries.stream().map(e -> e.condition).filter(Objects::nonNull).collect(Collectors.joining("||"));
      blockEntry.condition += " /*from block*/";
    }

    if (blockEntry.alternatives.isEmpty()) {
      prev.blockComponents.remove(blockEntry);
    }

  }

  @Override
  public void handle(ASTNonTerminal node) {
    ParseVisitorEntry e = new ParseVisitorEntry();

    ProdSymbol ps = grammarInfo.getGrammarSymbol().getProdWithInherited(node.getName()).get();

    // Only handle Lexer, Parser, interface, abstract and enum rules, but no external, ... rules
    if (!ps.isIsLexerProd() && !ps.isParserProd()
            &&
            !ps.isIsInterface()
            &&
            !ps.isIsAbstract()
            &&
            !ps.isIsEnum()) {
      // External -> skip
      // external rule called (first+second version)
      return;
    }
    // In case the AST has a list of this rule (*/+), we use += instead of = in the g4 rule
    String usageName = ParserGeneratorHelper.getUsageName(node);
    Optional<Integer> max = node.getEnclosingScope().getLocalAdditionalAttributeSymbols()
            .stream()
            .filter(astrule -> astrule.getName().equals(usageName))
            .map(TransformationHelper::getMax)
            .map(o -> o.orElse(1))
            .findFirst();

    boolean isRuleIterated = (!ruleIteratedStack.isEmpty() && ruleIteratedStack.peek()) || node.getIteration() == ASTConstantsGrammar.STAR || node.getIteration() == ASTConstantsGrammar.PLUS;

    if (max.isPresent() && max.get() == 1)
      isRuleIterated = false; // an astrule forcefully set the cardinality


    e.usageName = ParserGeneratorHelper.getUsageName(node);
    // Use the FQN when casting
    MCGrammarSymbol pdGrammar = ((ASTMCGrammar) ps.getEnclosingScope().getAstNode()).getSymbol();
    e.cast = Joiners.DOT.join(pdGrammar.getFullName().toLowerCase(),
            ASTConstants.AST_PACKAGE) + ".AST" + StringTransformations.capitalize(node.getName());
    if (ps.isIsExternal())
      e.cast += "Ext";
    e.tmpName = tmpNames.get(node) != null ? tmpNames.get(node) : "TMPNAME_IS_NULL";
    e.astList = node.getSymbol().isIsList();
    e.astOptional = node.getSymbol().isIsOptional();
    e.ruleOptional = node.getIteration() == ASTConstantsGrammar.STAR || node.getIteration() == ASTConstantsGrammar.QUESTION;
    e.ruleList = isRuleIterated;
    e.isLexNT = ps.isIsLexerProd();

    if (e.isLexNT) {
      e.convert = "convert" + StringTransformations.capitalize(node.getName());
    }

    if (e.ruleList)
      e.condition = "!ctx." + e.tmpName + ".isEmpty()";
    else
      e.condition = "ctx." + e.tmpName + " != null";

    e.allOptConditions.add(e.condition);

    stack.peek().blockComponents.add(e);
  }


  @Override
  public void handle(ASTKeyTerminal node) {
    this.handleTerminal(node, node::getSymbol, node.getIteration());
  }

  @Override
  public void handle(ASTTokenTerminal node) {
    this.handleTerminal(node, node::getSymbol, node.getIteration());
  }

  @Override
  public void handle(ASTTerminal node) {
    this.handleTerminal(node, node::getSymbol, node.getIteration());
  }

  protected String getRuleName(String name) {
    if (name.isEmpty()) {
      return "";
    } else if (grammarInfo.isKeyword(name, parserGeneratorHelper.getGrammarSymbol()) && grammarInfo.getKeywordRules().contains(name)) {
      return parserGeneratorHelper.getKeyRuleName(name);
    } else {
      return parserGeneratorHelper.getLexSymbolName(name.intern());
    }
  }

  protected void handleTerminal(ASTITerminal node, Supplier<RuleComponentSymbol> symbolSup, int iteration) {
    String rulename = getRuleName(node.getName());

    ParseVisitorEntry e = new ParseVisitorEntry();
    e.constantLexName = rulename;
    stack.peek().blockComponents.add(e);
    @Nullable String tmpName = tmpNames.get(node);
    Map<String, Collection<String>> replacedKeywords = grammarInfo.getGrammarSymbol().getReplacedKeywordsWithInherited();
    if (tmpName == null) {
      Log.error("0xA0392 Missing tmpname " + visitorClass.getName(), node.get_SourcePositionStart());
      throw new IllegalStateException("Missing tmpname " + node.getEnclosingScope().getName());
    } else if (replacedKeywords.containsKey(node.getName())) {
      int nokeywordindex = 0;
      Map<String, ParseVisitorEntry> entries = new HashMap<>();

      for (String ignored : replacedKeywords.get(node.getName())) {
        // Make a new alternative out of each possible keyword
        String prodname = tmpName + "_nk" + nokeywordindex++;
        ParseVisitorEntry alte = new ParseVisitorEntry();
        alte.constantLexName = prodname;
        alte.tmpName = prodname;
        alte.condition = "ctx." + prodname + " != null /*renk*/";
        entries.put(prodname, alte);
        // we might be able to combine multiple conditions together here
      }

      e.alternatives.addAll(entries.values());

      // Turn this an alternative
      e.condition = "(" +  entries.values().stream().map(ParseVisitorEntry::getCondition)
              .collect(Collectors.joining("||")) + ")";
    } else if (node instanceof ASTKeyTerminal && ((ASTKeyTerminal) node).getKeyConstant().sizeStrings() == 1) {
      e.condition = "ctx." + tmpName + " != null /*cc*/";
    } else if (node instanceof ASTKeyTerminal) {
      int keyIndex = 0;
      for (String ignored : ((ASTKeyTerminal) node).getKeyConstant().getStringList()) {
        ParseVisitorEntry alte = new ParseVisitorEntry();
        String prodname = tmpName + "_key" + keyIndex++;
        alte.constantLexName = prodname;
        alte.tmpName = prodname;
        alte.condition = "ctx." + prodname + " != null /*key1+*/";
        e.alternatives.add(alte);
      }
      e.condition = "(" + e.alternatives.stream().map(ParseVisitorEntry::getCondition).collect(Collectors.joining("||")) + ") /* key-constant */";
    } else {
      e.condition = "ctx." + tmpName + " != null /*kk*/";
    }
    e.allOptConditions.add(e.condition);
    e.ruleOptional = iteration == ASTConstantsGrammar.STAR || iteration == ASTConstantsGrammar.QUESTION;

    // we can ignore Terminals without a usage name for the 2nd part here
    if (node.isPresentUsageName()) {
      RuleComponentSymbol symbol = symbolSup.get();
      e.astList = symbol.isIsList();
      e.ruleList = symbol.isIsList();

      e.astOptional = symbol.isIsOptional();
      e.usageName = node.getUsageName();
      e.tmpName = tmpName;
      if (symbol.isIsList()) {
        e.astList = true;

        String usageName = node.getUsageName();
        Optional<Integer> max = node.getEnclosingScope().getLocalAdditionalAttributeSymbols()
                .stream()
                .filter(astrule -> astrule.getName().equals(usageName))
                .map(TransformationHelper::getMax)
                .map(o -> o.orElse(1))
                .findFirst();

        if (max.isPresent() && max.get() == 1)
          e.astList = false; // an astrule forcefully set the cardinality
      }

      e.alternatives.forEach(ea -> {
        ea.astList = e.astList;
        ea.ruleList = e.ruleList;
        ea.usageName = e.usageName;
      });
    }

    // Note: If Actions within  epeated blocks should be considered,
    // a separate transformation would be necessary
    // (and at least one (non)terminal within the repetition would require an implicit usage-name)
  }

  @Override
  public void handle(ASTConstantGroup node) {
    boolean iterated = TransformationHelper
            .isConstGroupIterated(node.getSymbol());
    if (!iterated) {
      ASTConstant x = node.getConstantList().get(0);

      ParseVisitorEntry e = new ParseVisitorEntry();
      stack.peek().blockComponents.add(e);
      String tmpName = tmpNames.get(node);
      e.condition = "ctx." + tmpName + " != null";
      e.usageName = (node.isPresentUsageName()) ? node.getUsageName() : x.getHumanName();
      e.constantValue = "true";
      e.ruleOptional = node.getIteration() == ASTConstantsGrammar.STAR || node.getIteration() == ASTConstantsGrammar.QUESTION;

      e.allOptConditions.add(e.condition);
    } else {
      Optional<MCGrammarSymbol> ruleGrammar = MCGrammarSymbolTableHelper
              .getMCGrammarSymbol(node.getEnclosingScope());
      String constfile = getConstantClassName(ruleGrammar.get());

      ParseVisitorEntry e = new ParseVisitorEntry();
      stack.peek().blockComponents.add(e);
      e.usageName = node.getUsageName();
      e.ruleOptional = node.getIteration() == ASTConstantsGrammar.STAR || node.getIteration() == ASTConstantsGrammar.QUESTION;
      e.ruleList = node.getIteration() == ASTConstantsGrammar.STAR || node.getIteration() == ASTConstantsGrammar.PLUS;

      List<String[]> values = new ArrayList<>();
      for (ASTConstant x : node.getConstantList()) {
        String constantName = parserGeneratorHelper.getConstantNameForConstant(x);
        String tmpName = tmpNames.get(x);
        String ruleName = tmpName == null ? (getRuleName(x) + "()") : tmpName;
        values.add(new String[]{"ctx." + ruleName + " != null", constfile + "." + constantName});
        e.allOptConditions.add("ctx." + ruleName + " != null");

      }
      e.condition = "(" + String.join("||", e.allOptConditions) + ")";
      e.constantValues = values;
    }

  }

  protected String getRuleName(ASTConstant constant) {
    if (constant.isPresentKeyConstant()) {
      if (constant.getKeyConstant().getStringList().size() > 1) {
        Log.error("0xA0394 Unexpected key constant string list: " + constant.getKeyConstant().getStringList(), constant.get_SourcePositionStart());
        return "invalid getruleName";
      }
      return parserGeneratorHelper.getKeyRuleName(constant.getKeyConstant().getString(0));
    } else if (constant.isPresentTokenConstant()) {
      return parserGeneratorHelper.getLexSymbolName(constant.getTokenConstant().getString());
    } else if (!grammarInfo.isKeyword(constant.getName(), parserGeneratorHelper.getGrammarSymbol())) {
      return parserGeneratorHelper.getLexSymbolName(constant.getName());
    } else if (grammarInfo.getKeywordRules().contains(constant.getName())) {
      return parserGeneratorHelper.getKeyRuleName(constant.getName());
    } else {
      return parserGeneratorHelper.getLexSymbolName(constant.getName());
    }
  }


  protected String getASTPackage(MCGrammarSymbol symbol) {
    return Joiners.DOT.join(symbol.getFullName().toLowerCase(),
            ASTConstants.AST_PACKAGE);
  }

  protected String getConstantClassName(MCGrammarSymbol symbol) {
    return Joiners.DOT.join(getASTPackage(symbol), ASTConstants.AST_CONSTANTS + symbol.getName());
  }
}
