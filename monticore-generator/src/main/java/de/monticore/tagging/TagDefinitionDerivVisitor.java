/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tagging;

import de.monticore.ast.Comment;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.codegen.cd2java._tagging.TaggingConstants;
import de.monticore.grammar.grammar.GrammarMill;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbolSurrogate;
import de.monticore.grammar.grammar._symboltable.RuleComponentSymbol;
import de.monticore.grammar.grammar._visitor.GrammarVisitor2;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.monticore.grammar.grammar_withconcepts._ast.ASTAction;
import de.monticore.statements.mcreturnstatements.MCReturnStatementsMill;
import de.monticore.types.MCTypeFacade;
import de.se_rwth.commons.Splitters;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The following visitor derives a language specific TagDefinition grammar.
 * It mainly produces the ModelElementIdentifier productions
 */
public class TagDefinitionDerivVisitor implements GrammarVisitor2 {

  protected ASTMCGrammar grammar;
  protected final MCTypeFacade mcTypeFacade = MCTypeFacade.getInstance();

  @Override
  public void visit(ASTMCGrammar node) {
    ASTMCGrammarBuilder builder = GrammarMill.mCGrammarBuilder().setName(node.getName() + TaggingConstants.TAGDEFINITION_SUFFIX);
    if (!node.getPackageList().isEmpty()) {
      builder.setPackageList(new ArrayList<>(node.getPackageList()));
    }

    // extends node
    builder.addSupergrammar(GrammarMill.grammarReferenceBuilder().setNamesList(Splitters.DOT.splitToList(node.getSymbol().getFullName())).build());
    // extends Tags
    builder.addSupergrammar(GrammarMill.grammarReferenceBuilder().setNamesList(Arrays.asList("de", "monticore", "tagging", "Tags")).build());

    builder.setComponent(node.isComponent());

    for (MCGrammarSymbolSurrogate superSurrogate : node.getSymbol().getSuperGrammars()) {
      List<String> namesList = new ArrayList<>(Splitters.DOT.splitToList(superSurrogate.lazyLoadDelegate().getFullName()));
      namesList.set(namesList.size() - 1, namesList.get(namesList.size() - 1) + TaggingConstants.TAGDEFINITION_SUFFIX);

      builder.addSupergrammar(GrammarMill.grammarReferenceBuilder().setNamesList(namesList).build());
    }
    this.grammar = builder.build();
    this.grammar.setStartRule(GrammarMill.startRuleBuilder().setName("TagUnit").build());
  }

  @Override
  public void visit(ASTClassProd node) {
    if (node.getSymbol().isIsDirectLeftRecursive() || node.getSymbol().isIsIndirectLeftRecursive()) {
      grammar.add_PostComment(new Comment("/* Unable to create a TagDef Identifier for left-recursive production " + node.getName() + " */"));
      // Do not log a warning for this
      return;
    }
    createSpecificIdentifier(node);
  }

  @Override
  public void visit(ASTInterfaceProd node) {
    // We do not generate (specific) identifiers based on the CS for interfaces
  }

  protected void createSpecificIdentifier(ASTProd node) {
    boolean ignore = node.getSymbol().isIsSymbolDefinition() || hasName(node);
    ASTClassProd classProd = GrammarMill.classProdBuilder().setName(node.getName() + "Identifier")
            .addSuperInterfaceRule(GrammarMill.ruleReferenceBuilder().setName("ModelElementIdentifier").build())
            .addAlt(GrammarMill.altBuilder()
                    .addComponent(terminal("["))
                    .addComponent(GrammarMill.nonTerminalBuilder().setName(node.getName()).setUsageName("identifiesElement").build())
                    .addComponent(terminal(("]"))).build())
            .build();

    if (ignore) {
      // Add a comment to the ${prod}DeclarationIdentifier to annotate, that this production can also be referenced by its (symbol) name
      classProd.add_PreComment(new Comment("/* DefaultIdent (using the Name) works too */"));
    }

    grammar.addClassProd(classProd);
    // Add the getIdentifies method to the Identifier
    grammar.addASTRule(GrammarMill.aSTRuleBuilder()
            .setType(node.getName() + "Identifier")
            .addGrammarMethod(GrammarMill.grammarMethodBuilder()
                    .setPublic(true)
                    .setMCReturnType(GrammarMill.mCReturnTypeBuilder().setMCType(mcTypeFacade.createStringType()).build())
                    .setBody(returnStringLiteral(node.getName()))
                    .setName("getIdentifies")
                    .build())
            .build());
  }

  // Create a return "source"; method body
  protected ASTAction returnStringLiteral(String source) {
    return Grammar_WithConceptsMill.actionBuilder()
            .addMCBlockStatement(
                    MCReturnStatementsMill.returnStatementBuilder()
                            .setExpression(
                                    CD4CodeMill.literalExpressionBuilder()
                                            .setLiteral(CD4CodeMill.stringLiteralBuilder().setSource(source).build())
                                            .build()
                            )
                            .build()
            )
            .build();
  }

  protected boolean hasName(ASTProd node) {
    // If the production symbol has a single name production
    boolean isFirstName = true;
    for (RuleComponentSymbol comp : node.getSymbol().getProdComponents()) {
      if (comp.isIsNonterminal() && comp.isPresentReferencedType()) {
        if (comp.getName().equals("name") && comp.getReferencedType().equals("Name")) {
          if (comp.isIsList() || comp.isIsOptional() || !isFirstName) return false;
          isFirstName = false;
        }
      }
    }
    return true;
  }

  protected ASTTerminal terminal(String term) {
    return GrammarMill.terminalBuilder().setName(term).build();
  }

  public ASTMCGrammar getGrammar() {
    return grammar;
  }
}
