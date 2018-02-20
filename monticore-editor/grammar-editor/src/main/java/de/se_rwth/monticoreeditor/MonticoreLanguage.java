/* (c)  https://github.com/MontiCore/monticore */

package de.se_rwth.monticoreeditor;

import static de.se_rwth.langeditor.util.Misc.loadImage;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.templates.TemplateProposal;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import de.monticore.ast.ASTNode;
import de.monticore.grammar.cocos.GrammarCoCos;
import de.monticore.grammar.grammar._ast.ASTASTRule;
import de.monticore.grammar.grammar._ast.ASTAbstractProd;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTExternalProd;
import de.monticore.grammar.grammar._ast.ASTInterfaceProd;
import de.monticore.grammar.grammar._ast.ASTLexProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.monticore.grammar.grammar_withconcepts._parser.Grammar_WithConceptsAntlrLexer;
import de.monticore.grammar.grammar_withconcepts._parser.Grammar_WithConceptsAntlrParser;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.monticore.symboltable.SymbolKind;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.langeditor.language.Language;
import de.se_rwth.langeditor.language.OutlineElementSet;
import de.se_rwth.langeditor.language.OutlineElementSet.Builder;
import de.se_rwth.langeditor.language.ParserConfig;
import de.se_rwth.langeditor.modelstates.ModelState;
import de.se_rwth.monticoreeditor.templates.ProdTemplate;

public final class MonticoreLanguage implements Language {
  
  private final Resolving resolving = new Resolving();
  
  private final Collection<? extends SymbolKind> completionKinds = Sets
      .newHashSet(MCProdSymbol.KIND);
      
  private final ImmutableList<String> keywords = ImmutableList.of("component", "package", "grammar",
      "options", "astimplements",
      "astextends",
      "interface", "enum", "implements", "external", "fragment",
      "extends", "returns", "ast", "token", "protected", "scope", "symbol");
      
  @Override
  public String getExtension() {
    return "mc4";
  }
  
  @Override
  public ParserConfig<?> getParserConfig() {
    return new ParserConfig<>(Grammar_WithConceptsAntlrLexer::new,
        Grammar_WithConceptsAntlrParser::new,
        Grammar_WithConceptsAntlrParser::mCGrammar);
  }
  
  @Override
  public void buildProject(IProject project, ImmutableSet<ModelState> modelStates,
      ImmutableList<Path> modelPath) {
    resolving.buildProject(project, modelStates, modelPath);
    modelStates.forEach(state -> {
      Log.getFindings().clear();
      checkContextConditions(state);
      addErrors(state);
    });
  }
  
  @Override
  public void buildModel(ModelState modelState) {
    Log.getFindings().clear();
    try {
      resolving.buildModel(modelState);
      checkContextConditions(modelState);
    }
    catch (Exception e) {
      // Do nothing
    }
    addErrors(modelState);
  }
  
  @Override
  public ImmutableList<String> getKeywords() {
    return keywords;
  }
  
  /**
   * @see de.se_rwth.langeditor.language.Language#getCompletionKinds()
   */
  @Override
  public Collection<? extends SymbolKind> getCompletionKinds() {
    return completionKinds;
  }
  
  @Override
  public OutlineElementSet getOutlineElementSet() {
    Builder builder = OutlineElementSet.builder();
    builder.add(ASTClassProd.class,
        ASTClassProd::getName,
        loadImage("icons/teamstrm_rep.gif"));
    builder.add(ASTInterfaceProd.class,
        ASTInterfaceProd::getName,
        loadImage("icons/intf_obj.gif"));
    builder.add(ASTAbstractProd.class,
        ASTAbstractProd::getName,
        loadImage("icons/class_abs_tsk.gif"));
    builder.add(ASTASTRule.class,
        ASTASTRule::getType,
        loadImage("icons/source_attach_attrib.gif"));
    builder.add(ASTExternalProd.class,
        ASTExternalProd::getName,
        loadImage("icons/element.gif"));
    builder.add(ASTLexProd.class,
        ASTLexProd::getName,
        loadImage("icons/tnames_co.gif"));
    return builder.build();
  }
  
  @Override
  public Optional<Supplier<Optional<ASTNode>>> createResolver(ASTNode astNode) {
    return resolving.createResolver(astNode);
  }
  
  private void checkContextConditions(ModelState modelState) {
    if (modelState.getRootNode() instanceof ASTMCGrammar) {
      Grammar_WithConceptsCoCoChecker cocoChecker = new GrammarCoCos().getCoCoChecker();
      cocoChecker.handle((ASTMCGrammar) modelState.getRootNode());
    }
  }
  
  private void addErrors(ModelState modelState) {
    Log.getFindings().stream().forEach(finding -> {
      modelState.addAdditionalError(finding);
    });
  }

  /**
   * @see de.se_rwth.langeditor.language.Language#getTemplateProposals(org.eclipse.jface.text.ITextViewer, int, java.lang.String)
   */
  @Override
  public List<TemplateProposal> getTemplateProposals(ITextViewer viewer, int offset,
      String prefix) {
    List<TemplateProposal> templates = new ArrayList<>();
    templates.addAll(new ProdTemplate().getTemplateProposals(viewer, offset, prefix));
    return templates;
  }

  
}
