/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tagging;

import de.monticore.codegen.cd2java._tagging.TaggingConstants;
import de.monticore.grammar.grammar.GrammarMill;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTMCGrammarBuilder;
import de.monticore.grammar.grammar._ast.ASTTerminal;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbolSurrogate;
import de.monticore.grammar.grammar._visitor.GrammarVisitor2;
import de.se_rwth.commons.Splitters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TagSchemaDerivVisitor implements GrammarVisitor2 {

  protected ASTMCGrammar grammar;

  @Override
  public void visit(ASTMCGrammar node) {
    ASTMCGrammarBuilder builder = GrammarMill.mCGrammarBuilder().setName(node.getName() + TaggingConstants.TAGSCHEMA_SUFFIX);
    if (!node.getPackageList().isEmpty()) {
      builder.setPackageList(new ArrayList<>(node.getPackageList()));
    }

    // extends TagSchema
    builder.getSupergrammarList().add(GrammarMill.grammarReferenceBuilder().setNamesList(Arrays.asList("de", "monticore", "tagging", TaggingConstants.TAGSCHEMA_SUFFIX)).build());

    // extend the TagSchemas of all super languages
    for (MCGrammarSymbolSurrogate superSurrogate : node.getSymbol().getSuperGrammars()) {
      // We currently have to load the delegates, as the package name of a delegate might not be correct
      List<String> namesList = new ArrayList<>(Splitters.DOT.splitToList(superSurrogate.lazyLoadDelegate().getFullName()));
      namesList.set(namesList.size() - 1, namesList.get(namesList.size() - 1) + TaggingConstants.TAGSCHEMA_SUFFIX);

      builder.addSupergrammar(GrammarMill.grammarReferenceBuilder().setNamesList(namesList).build());
    }

    builder.setComponent(node.isComponent());

    this.grammar = builder.build();

    this.grammar.setStartRule(GrammarMill.startRuleBuilder().setName(TaggingConstants.TAGSCHEMA_SUFFIX).build());
  }

  @Override
  public void visit(ASTClassProd node) {
    ASTClassProd classProd = GrammarMill.classProdBuilder().setName(node.getName() + "SchemaIdentifier").addSuperInterfaceRule(GrammarMill.ruleReferenceBuilder().setName("ScopeIdentifier").build()).addAlt(GrammarMill.altBuilder().addComponent(terminal(node.getName())).build()).build();

    grammar.addClassProd(classProd);
  }

  // DISCUSS: IV-D.2.  via referenced symbols? or all Names?

  protected ASTTerminal terminal(String term) {
    return GrammarMill.terminalBuilder().setName(term).build();
  }

  public ASTMCGrammar getGrammar() {
    return grammar;
  }
}
