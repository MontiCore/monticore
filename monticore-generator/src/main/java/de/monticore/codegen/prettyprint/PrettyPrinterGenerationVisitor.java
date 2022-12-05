// (c) https://github.com/MontiCore/monticore
package de.monticore.codegen.prettyprint;

import de.monticore.ast.ASTNode;
import de.monticore.cd.codegen.CD2JavaTemplates;
import de.monticore.cd.facade.CDMethodFacade;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.codegen.cd2java._ast.ast_class.ASTConstants;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.codegen.prettyprint.data.AltData;
import de.monticore.codegen.prettyprint.data.BlockData;
import de.monticore.codegen.prettyprint.data.PPGuardComponent;
import de.monticore.expressions.commonexpressions.CommonExpressionsMill;
import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._ast.ASTLogicalNotExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.grammar.LexNamer;
import de.monticore.grammar.Multiplicity;
import de.monticore.grammar.grammar.GrammarMill;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.grammar.grammar._symboltable.RuleComponentSymbol;
import de.monticore.grammar.grammar._visitor.GrammarVisitor2;
import de.monticore.grammar.prettyprint.Grammar_WithConceptsFullPrettyPrinter;
import de.monticore.literals.mccommonliterals.MCCommonLiteralsMill;
import de.monticore.literals.mccommonliterals._ast.ASTConstantsMCCommonLiterals;
import de.monticore.literals.mcliteralsbasis.MCLiteralsBasisMill;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;


public class PrettyPrinterGenerationVisitor implements GrammarVisitor2 {

  protected static final String HANDLE = "public void handle(%s.AST%s node);";
  protected static final String ITERATOR_PREFIX = "iter_";

  // data from the first phase
  protected final Map<String, NonTermAccessorVisitor.ClassProdNonTermPrettyPrintData> classProds;

  protected final ASTCDClass ppClass;

  protected final GlobalExtensionManagement glex;

  // In case generation is not possible
  protected String failureMessage;


  // Stacks
  protected final Stack<BlockData> blockDataStack = new Stack<>();

  protected final Stack<AltData> altDataStack = new Stack<>();

  // Changing attributes
  protected ASTClassProd currentClassProd;
  protected NonTermAccessorVisitor.ClassProdNonTermPrettyPrintData currentClassProdData;

  protected String grammarName;

  public PrettyPrinterGenerationVisitor(GlobalExtensionManagement glex, ASTCDClass ppClass, Map<String, NonTermAccessorVisitor.ClassProdNonTermPrettyPrintData> classProds) {
    this.glex = glex;
    this.ppClass = ppClass;
    this.classProds = classProds;
  }


  @Override
  public void visit(ASTMCGrammar node) {
    this.grammarName = node.getName();
  }

  @Override
  public void visit(ASTClassProd node) {
    // We handle ClassProds similar to Blocks
    BlockData classProd = new BlockData(true, ASTConstantsGrammar.DEFAULT, ASTConstantsGrammar.DEFAULT, null);
    blockDataStack.push(classProd);

    this.failureMessage = null;

    this.currentClassProd = node;
    this.currentClassProdData = this.classProds.get(node.getName());
  }

  @Override
  public void endVisit(ASTClassProd node) {
    BlockData blockData = blockDataStack.pop();

    blockData.getAltDataList().sort(Collections.reverseOrder());

    // Prepare iterators (used instead of direct lists access)
    Map<String, IteratorData> iterators = new HashMap<>();
    for (String refName : currentClassProdData.getNonTerminals().keySet()) {
      if (!currentClassProdData.isIteratorNeeded(refName)) continue;
      ASTNode itNode = currentClassProdData.getNonTerminalNodes().get(refName);

      Multiplicity multiplicity = currentClassProdData.getMultiplicity(StringTransformations.uncapitalize(refName));

      String getter = getPlainGetterSymbol(refName, multiplicity);

      // Resolve the production to derive the concrete type
      Optional<ProdSymbol> refProd = node.getSymbol().getEnclosingScope().resolveProd(((ASTNonTerminal) itNode).getSymbol().getReferencedType());

      if (refProd.isEmpty()) {
        Log.error("Unable to resolve referenced production during PPGen");
        return;
      }

      String type;
      if (refProd.get().isIsLexerProd()) {
        // Lexer types will be represented by Strings
        type = "String";
      } else {
        // Apply TypeCD2JavaVisitor-equivalent by introducing the _ast package

        List<String> sTypes = new ArrayList<>();
        if (!refProd.get().getPackageName().isEmpty())
          sTypes.add(refProd.get().getPackageName());

        sTypes.add(refProd.get().getEnclosingScope().getName().toLowerCase());

        sTypes.add(ASTConstants.AST_PACKAGE);
        String refProdName = StringTransformations.capitalize(refProd.get().getName());
        if (refProd.get().isIsExternal())
          refProdName += "Ext";
        sTypes.add(ASTConstants.AST_PREFIX + refProdName);
        type = Joiners.DOT.join(sTypes);
      }

      if (multiplicity == Multiplicity.LIST) {
        // Also use an iterator for List-types
        type = "java.util.Iterator<" + type + ">";
      }


      iterators.put(refName, new IteratorData(getter, type));
    }

    if (!currentClassProdData.getErroringNonTerminals().isEmpty())
      this.failureMessage = "The NonTerminal(s) " + currentClassProdData.getErroringNonTerminals() + " caused the automatic generation to fail";


    List<String> astPackage = getASTPackage(node.getSymbol());
    String signature = String.format(HANDLE, Joiners.DOT.join(astPackage), StringTransformations.capitalize(node.getName()));
    ASTCDMethod handle = CDMethodFacade.getInstance().createMethodByDefinition(signature);

    TemplateHookPoint hookPoint;
    if (this.failureMessage == null) {
      // Add the handle(node) method of the pretty printer with the collected BlockData
      hookPoint = new TemplateHookPoint("_prettyprinter.pp.HandleMethod", blockData,
              node.getName(), node.getEnclosingScope().getName(), Joiners.DOT.join(astPackage), iterators.entrySet());
    } else {
      // Add the handle(node) method of the pretty printer with the collected BlockData
      hookPoint = new TemplateHookPoint("_prettyprinter.pp.HandleMethodError", failureMessage, node.getName(), node, blockData);
    }
    ppClass.addCDMember(handle);
    glex.replaceTemplate(CD2JavaTemplates.EMPTY_BODY, handle, hookPoint);
    this.currentClassProd = null;
    this.currentClassProdData = null;
  }


  @Override
  public void visit(ASTAlt node) {
    if (blockDataStack.isEmpty()) return; // Only visit in CPs
    AltData altData = new AltData();
    blockDataStack.peek().getAltDataList().add(altData);
    altDataStack.push(altData);
  }

  @Override
  public void endVisit(ASTAlt node) {
    if (blockDataStack.isEmpty()) return; // Only visit in CPs
    altDataStack.pop();
  }

  @Override
  public void visit(ASTNonTerminal node) {
    if (blockDataStack.isEmpty()) return; // Only visit in CPs
    AltData altData = altDataStack.peek();

    if (node.getSymbol().getReferencedProd().get().isIsEnum()) {
      this.failureMessage = "EnumProd references are not yet implemented";
      return;
    }
    String refName = node.isPresentUsageName() ? node.getUsageName() : node.getName();
    Multiplicity multiplicity = currentClassProdData.getMultiplicity(StringTransformations.uncapitalize(refName));


    Optional<ProdSymbol> prodSymbol = currentClassProd.getSpannedScope().resolveProd(node.getName());
    if (prodSymbol.isPresent() && prodSymbol.get().isIsLexerProd() && prodSymbol.get().isPresentAstNode()) {
      // Abort primitives (as we are unable to
      ASTLexProd lexProd = (ASTLexProd) prodSymbol.get().getAstNode();
      String lexType = TransformationHelper.createConvertType(lexProd);
      if ("int".equals(lexType) || "boolean".equals(lexType) || "char".equals(lexType)
              || "float".equals(lexType) || "double".equals(lexType)
              || "long".equals(lexType) || "byte".equals(lexType) || "short".equals(lexType)) {
        this.failureMessage = "Unable to derive guard condition for primitive type " + lexType + " for NoNTerm " + node.getName();
      }
    }
    boolean isIteratorUsed = currentClassProdData.isIteratorNeeded(refName);

    int iteration = node.getIteration();

    if (multiplicity == Multiplicity.STANDARD && (iteration == ASTConstantsGrammar.PLUS || iteration == ASTConstantsGrammar.STAR))
      iteration = ASTConstantsGrammar.DEFAULT; // Force overwrite in case of ASTRule shenanigans
    if (multiplicity == Multiplicity.OPTIONAL && (iteration == ASTConstantsGrammar.PLUS || iteration == ASTConstantsGrammar.STAR))
      iteration = ASTConstantsGrammar.QUESTION; // Force overwrite in case of ASTRule shenanigans

    if (multiplicity == Multiplicity.LIST && node.getIteration() == ASTConstantsGrammar.DEFAULT && !isIteratorUsed) {
      PPGuardComponent component = PPGuardComponent.forNTSingle(isLexType(node) ? "String" : node.getName(),
              refName,
              iteration
      );

      altData.getComponentList().add(component);
      return;
    }

    if (node.getIteration() == ASTConstantsGrammar.QUESTION || node.getIteration() == ASTConstantsGrammar.STAR) {
      altData.setOptional(altData.getOptional() + 1);
    } else if (node.getIteration() == ASTConstantsGrammar.PLUS) {
      altData.setRequired(altData.getRequired() + 1);
      altData.setOptional(altData.getOptional() + 1);

      ASTExpression exp = getExp(node, refName, multiplicity);
      if (exp != null) {
        altData.getExpressionList().add(exp);
      }
    } else if (node.getIteration() == ASTConstantsGrammar.DEFAULT) {
      altData.setRequired(altData.getRequired() + 1);

      ASTExpression exp = getExp(node, refName, multiplicity);
      if (exp != null) {
        altData.getExpressionList().add(exp);
      }
    }

    if (isIteratorUsed) {
      blockDataStack.peek().markListReady(); // Mark that an iterator was used => while can be used
      altData.markListReady();
    }

    PPGuardComponent component = PPGuardComponent.forNT(isLexType(node) ? "String" : node.getName(),
            refName,
            iteration,
            isIteratorUsed
    );

    altData.getComponentList().add(component);
  }

  @Override
  public void visit(ASTNonTerminalSeparator node) {
    Log.error("0xA1067 GrammarTransformer#removeNonTerminalSeparators should have removed NonTerminalSeparators");
  }


  @Override
  public void visit(ASTTerminal node) {
    if (blockDataStack.isEmpty()) return; // Only visit in CPs
    PPGuardComponent component = PPGuardComponent.forT(node.getName(), node.getIteration());

    altDataStack.peek().getComponentList().add(component);
    altDataStack.peek().getExpressionList().add(AltData.TRUE_EXPRESSION); // Push a true condition
  }


  @Override
  public void visit(ASTBlock node) {
    BlockData outerBlock = blockDataStack.peek();
    AltData altData = altDataStack.peek();
    BlockData blockData = new BlockData(false, node.getIteration(), getEffectiveIteration(outerBlock.getInheritedIteration(), node.getIteration()), node);
    blockDataStack.push(blockData);
    altData.getComponentList().add(PPGuardComponent.forBlock(blockData, node.getIteration()));
  }

  @Override
  public void endVisit(ASTBlock node) {
    BlockData blockData = blockDataStack.pop();

    if (blockData.isListReady()) {
      blockDataStack.peek().markListReady();
    }
    List<ASTExpression> allAltExpressions = new ArrayList<>();

    if (!altDataStack.isEmpty()) {
      AltData altData = altDataStack.peek();

      int maxOpt = altData.getOptional();
      int maxReq = altData.getRequired();

      boolean isOpt = node.getIteration() == ASTConstantsGrammar.STAR || node.getIteration() == ASTConstantsGrammar.QUESTION;

      boolean isAnyListReady = false;
      boolean areAllListReady = true;

      for (AltData innerAlt : blockData.getAltDataList()) {
        isAnyListReady |= innerAlt.isListReady();
        areAllListReady &= innerAlt.isListReady();
        if (isOpt) {
          maxOpt = Math.max(maxOpt, innerAlt.getOptional() + innerAlt.getRequired());
        } else {
          // Non-optional block
          maxOpt = Math.max(maxOpt, innerAlt.getOptional());
          maxReq = Math.max(maxReq, innerAlt.getRequired());
          if (!innerAlt.getExpressionList().isEmpty())
            allAltExpressions.add(AltData.reduceToAnd(innerAlt.getExpressionList()));
          else
            allAltExpressions.add(AltData.TRUE_EXPRESSION);
        }
      }
      altData.setOptional(maxOpt);
      altData.setRequired(maxReq);

      if (!allAltExpressions.isEmpty())
        altData.getExpressionList().add(AltData.reduceToOr(allAltExpressions));

      // Prevent e.g. (Decimal | ",")* => while (hasDecimal() || true) { ... } endless loops
      if (node.getIteration() == ASTConstantsGrammar.STAR || node.getIteration() == ASTConstantsGrammar.PLUS) {
        if ((isAnyListReady && !areAllListReady)) // cases such as (["a"] | NT)*;
          this.failureMessage = "Contains a list of Alts where one is not iterator ready (leading to endless while loops)!";

        if (blockData.getAltDataList().stream().anyMatch(a -> a.getExpressionList().isEmpty())) {
          String pp = new Grammar_WithConceptsFullPrettyPrinter(new IndentPrinter()).prettyprint(node);
          pp = pp.replace("\"", "\\\"").replace("\n", " ");
          this.failureMessage = "Contains a block without condition which is looped: " + pp;
        }
      }

    }

    blockData.getAltDataList().sort(Collections.reverseOrder());

  }

  @Override
  public void visit(ASTKeyTerminal node) {
    if (blockDataStack.isEmpty()) return; // Only visit in CPs
    PPGuardComponent component = PPGuardComponent.forT(node.getName(), node.getIteration());

    altDataStack.peek().getComponentList().add(component);
  }

  @Override
  public void visit(ASTConstantGroup node) {
    String humanName = node.isPresentUsageName() ? node.getUsageName() : node.getName();
    String getter = getPlainGetterSymbol(humanName, Multiplicity.STANDARD);

    boolean onlyOneConstant = node.getConstantList().size() == 1;
    if (onlyOneConstant) {
      // catch (op:["*"]|op:["/"]) and ASTRule shenanigans
      String nodeAttrName = node.isPresentUsageName() ? node.getUsageName() : node.getName();
      if (nodeAttrName.isEmpty())
        nodeAttrName = node.getConstant(0).getHumanName();
      // The getter for booleans is using the "is"-prefix
      getter = "is" + StringTransformations.capitalize(nodeAttrName);
      if (node.getEnclosingScope().resolveRuleComponentMany(humanName).size() > 1)
        this.failureMessage = "Unable to handle ConstantGroup with size of 1, but multiple elements named " + humanName + " present";
    }

    Set<Map.Entry<String, String>> constants = new HashSet<>();
    for (ASTConstant constant : node.getConstantList()) {
      constants.add(new AbstractMap.SimpleEntry<>(constant.getHumanName(), constant.getName()));
      if (!onlyOneConstant && LexNamer.createGoodName(constant.getHumanName()).isEmpty()) // The constant will be named CONSTANT{num} instead
        this.failureMessage = "Unable to find good Constant name for " + getter + " and value " + constant.getHumanName();
    }

    PPGuardComponent component = PPGuardComponent.forCG(getter, constants);

    AltData altData;
    Optional<BlockData> blockDataOpt = Optional.empty();
    if (node.getIteration() == ASTConstantsGrammar.QUESTION || node.getIteration() == ASTConstantsGrammar.STAR) {
      // Add a new block with alt for this ConstantGroup
      BlockData outerBlock = blockDataStack.peek();
      blockDataOpt = Optional.of(new BlockData(false, node.getIteration(), getEffectiveIteration(outerBlock.getInheritedIteration(), node.getIteration()), null));
      altDataStack.peek().getComponentList().add(PPGuardComponent.forBlock(blockDataOpt.get(), node.getIteration()));
      // And add one alt (without using the stack, as we will only use it in this method)
      altData = new AltData();
      blockDataOpt.get().getAltDataList().add(altData);
    }else {
      // If this is not an optional CG, skip the extra block
      altData = altDataStack.peek();
    }
    altData.getComponentList().add(component);

    // As exactly one constant occurs, a conditional-expression is necessary
    if (onlyOneConstant) {
      ASTExpression getConstant = getNodeCallExp(getter);
      altData.getExpressionList().add(getConstant);
    } else {
      //  Add disjunctive form of all constant conditions
      //  name:(["a" | "b"])+ is an (almost never used) example
      List<ASTExpression> expressionList = new ArrayList<>();
      ASTExpression getCG = getNodeCallExp(getter); // CG getter

      List<String> astPackage = getASTPackage(node.getSymbol());
      ASTNameExpression constantsName = getNameExpression(Joiners.DOT.join(astPackage) + "." + ASTConstants.AST_CONSTANTS + StringTransformations.capitalize(grammarName));
      for (ASTConstant constant : node.getConstantList()) {
        // equals respective ASTConstants constant
        expressionList.add(
                CommonExpressionsMill.equalsExpressionBuilder()
                        .setLeft(getCG)
                        .setOperator("==")
                        .setRight(getFieldExp(constantsName, constant.getHumanName().toUpperCase())).build());
      }
      // Reduce the list to a bunch of logical ORs
      altData.getExpressionList().add(AltData.reduceToOr(expressionList));
    }

    if (blockDataOpt.isPresent()) {
      // Close the (opt) block
      AltData outerAltData = altDataStack.peek();
      int maxOpt = outerAltData.getOptional();
      int maxReq = outerAltData.getRequired();

      boolean isOpt = node.getIteration() == ASTConstantsGrammar.STAR || node.getIteration() == ASTConstantsGrammar.QUESTION;

      List<ASTExpression> allAltExpressions = new ArrayList<>();

      for (AltData innerAlt : blockDataOpt.get().getAltDataList()) {
        if (isOpt) {
          maxOpt = Math.max(maxOpt, innerAlt.getOptional() + innerAlt.getRequired());
        } else {
          // Non-optional block
          maxOpt = Math.max(maxOpt, innerAlt.getOptional());
          maxReq = Math.max(maxReq, innerAlt.getRequired());
          if (!innerAlt.getExpressionList().isEmpty())
            allAltExpressions.add(AltData.reduceToAnd(innerAlt.getExpressionList()));
          else
            allAltExpressions.add(AltData.TRUE_EXPRESSION);
        }
      }
      outerAltData.setOptional(maxOpt);
      outerAltData.setRequired(maxReq);

      if (!allAltExpressions.isEmpty())
        outerAltData.getExpressionList().add(AltData.reduceToOr(allAltExpressions));

    }
  }

  public List<String> getASTPackage(RuleComponentSymbol symbol) {
    List<String> astPackage = new ArrayList<>();
    if (!symbol.getPackageName().isEmpty())
      astPackage.add(symbol.getPackageName());
    astPackage.add(grammarName.toLowerCase());
    astPackage.add("_ast");
    return astPackage;
  }

  public List<String> getASTPackage(ProdSymbol symbol) {
    List<String> astPackage = new ArrayList<>();
    if (!symbol.getPackageName().isEmpty())
      astPackage.add(symbol.getPackageName());
    astPackage.add(grammarName.toLowerCase());
    astPackage.add(ASTConstants.AST_PACKAGE);
    return astPackage;
  }

  public boolean isLexType(ASTNonTerminal node) {
    Optional<ProdSymbol> prodSymbol = currentClassProd.getSpannedScope().resolveProd(node.getName());
    return prodSymbol.get().isIsLexerProd();
  }

  public boolean isLexType(ASTNonTerminalSeparator node) {
    Optional<ProdSymbol> prodSymbol = currentClassProd.getSpannedScope().resolveProd(node.getName());
    return prodSymbol.get().isIsLexerProd();
  }


  /**
   * Get the expression to ensure a NonTerminal is present
   * Uses the Link from the {@link de.monticore.codegen.mc2cd.transl.MC2CDTranslation} to decide between types
   *
   * @param node         the ASTNode of the NonTerminal, etc.
   * @param refName      the usage name (if present), otherwise the name
   * @param multiplicity the multiplicity of the Node
   * @return the expression for optionals or lists, or null otherwise
   */
  @Nullable
  public ASTExpression getExp(@Nonnull ASTNonTerminal node, String refName, Multiplicity multiplicity) {
    if (multiplicity == Multiplicity.OPTIONAL) {
      return getNodeCallExp("isPresent" + StringTransformations.capitalize(node.getSymbol().getName()));
    } else if (currentClassProdData.isIteratorNeeded(refName)) {
      ASTExpression it = getNameExpression(ITERATOR_PREFIX + StringTransformations.uncapitalize(refName));
      return getCallExp(getFieldExp(it, "hasNext"), "hasNext");
    } else if (multiplicity == Multiplicity.LIST) {
      String getter = getPlainGetterSymbol(refName, multiplicity);
      ASTExpression getList = getNodeCallExp(getter);
      return negate(getCallExp(getFieldExp(getList, "isEmpty"), "isEmpty")); //s
    }

    return null;
  }

  public String getPlainGetterSymbol(String refName, Multiplicity multiplicity) {
    String g1 = "get" + StringTransformations.capitalize(refName);
    switch (multiplicity) {
      case LIST:
        g1 += "List";
        break;
      case OPTIONAL:
        g1 += "Opt";
        break;
      default:
        break;
    }
    return g1;
  }

  public ASTLogicalNotExpression negate(ASTExpression expression) {
    return CommonExpressionsMill.logicalNotExpressionBuilder().setExpression(expression).build();
  }

  public ASTFieldAccessExpression getFieldExp(ASTExpression nodeExp, String name) {
    return CommonExpressionsMill.fieldAccessExpressionBuilder().setExpression(nodeExp)
            .setName(name).build();
  }

  public ASTFieldAccessExpression getNodeFieldExp(String name) {
    ASTNameExpression nodeExp = CommonExpressionsMill.nameExpressionBuilder().setName("node").build();
    return CommonExpressionsMill.fieldAccessExpressionBuilder().setExpression(nodeExp)
            .setName(name).build();
  }

  public ASTCallExpression getNodeCallExp(String name) {
    return CommonExpressionsMill.callExpressionBuilder().setExpression(getNodeFieldExp(name))
            .setArguments(CommonExpressionsMill.argumentsBuilder().build())
            .build();
  }

  public ASTNameExpression getNameExpression(String name) {
    return CommonExpressionsMill.nameExpressionBuilder()
            .setName(name).build();
  }

  public ASTCallExpression getCallExp(ASTExpression n, String name) {
    return CommonExpressionsMill.callExpressionBuilder().setExpression(n)
            .setArguments(CommonExpressionsMill.argumentsBuilder().build())
            .build();
  }


  protected int getEffectiveIteration(int outer, int self) {
    if (outer == ASTConstantsGrammar.STAR)
      return ASTConstantsGrammar.STAR;
    if (outer == ASTConstantsGrammar.PLUS)
      return ASTConstantsGrammar.PLUS;
    if (outer == ASTConstantsGrammar.QUESTION) {
      if (self == ASTConstantsGrammar.STAR || self == ASTConstantsGrammar.PLUS)
        return self;
      return outer;
    }
    return self;
  }


  /**
   * Data/Record class for Iterators
   */
  public static class IteratorData {
    protected final String getter;
    protected final String type;

    public IteratorData(String getter, String type) {
      this.getter = getter;
      this.type = type;
    }

    public String getGetter() {
      return getter;
    }

    public String getType() {
      return type;
    }
  }
}
