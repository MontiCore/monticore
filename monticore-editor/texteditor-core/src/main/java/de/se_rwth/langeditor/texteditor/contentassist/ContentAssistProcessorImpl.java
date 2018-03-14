/* (c)  https://github.com/MontiCore/monticore */package de.se_rwth.langeditor.texteditor.contentassist;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.core.resources.IStorage;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

import com.google.inject.Inject;

import de.monticore.ast.ASTNode;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Scopes;
import de.monticore.symboltable.Symbol;
import de.se_rwth.langeditor.injection.TextEditorScoped;
import de.se_rwth.langeditor.language.Language;
import de.se_rwth.langeditor.modelstates.ModelState;
import de.se_rwth.langeditor.modelstates.Nodes;
import de.se_rwth.langeditor.modelstates.ObservableModelStates;
import de.se_rwth.langeditor.util.antlr.ParseTrees;

@TextEditorScoped
public class ContentAssistProcessorImpl implements IContentAssistProcessor {
  
  private Language language;
  
  private Optional<ModelState> currentModelState = Optional.empty();
  
  @Inject
  public ContentAssistProcessorImpl(
      IStorage storage,
      ObservableModelStates observableModelStates,
      Language language) {
    this.language = language;
    observableModelStates.getModelStates().stream()
        .filter(modelState -> modelState.getStorage().equals(storage))
        .forEach(this::acceptModelState);
    observableModelStates.addStorageObserver(storage, this::acceptModelState);
  }
  
  @Override
  public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
    String incomplete = getIncomplete(viewer.getDocument(), offset);
    int start = offset - incomplete.length();
    List<ICompletionProposal> proposalList = new ArrayList<>();
    
    // Add types from symbol table
    if (currentModelState.isPresent()) {
      Optional<ASTNode> actNode = Nodes.getAstNode(ParseTrees.getRootOfSubtreeEnclosingRegion(
          currentModelState.get().getRootContext(), start, offset));
      if (currentModelState.get().getLastLegalRootNode()
          .isPresent() && actNode.isPresent()) {
        ASTNode legalRoot = currentModelState.get().getLastLegalRootNode().get();
        Optional<? extends Scope> rootScope = language.getScope(legalRoot);
        if (rootScope.isPresent()) {
          // Get all symbols in global scope
          GlobalScope globalScope = (GlobalScope) rootScope.get().getEnclosingScope().get();
          Optional<? extends Scope> enclosingScope;
          if (actNode.get().getEnclosingScope().isPresent()) {
            enclosingScope = actNode.get().getEnclosingScope();
          } else {
            enclosingScope = rootScope;
          }
          for (Symbol symbol : getSymbols(globalScope)) {
            if (findMatch(incomplete, symbol, enclosingScope)) {
              proposalList
                  .add(new CompletionProposal(symbol.getName(), start, incomplete.length(),
                      symbol.getName().length()));
            }
          }
        }
      }      
    }
    
    // Add templates
    proposalList.addAll(language.getTemplateProposals(viewer, offset, incomplete));

    // Add keywords
    for (String keyword : language.getKeywords()) {
      if (findMatch(incomplete, keyword)) {
        proposalList
            .add(new CompletionProposal(keyword, start, incomplete.length(), keyword.length()));
      }
    }
    return proposalList.toArray(new ICompletionProposal[0]);
  }
  
  /**
   * @param rootNode
   * @return
   */
  private List<Symbol> getSymbols(Scope rootScope) {
    List<Symbol> names = new ArrayList<>();
    if (rootScope.exportsSymbols()) {
      for (Symbol symbol : Scopes.getLocalSymbolsAsCollection(rootScope)) {
        if (language.getCompletionKinds().contains(symbol.getKind())) {
          names.add(symbol);
        }
      }
    }
    List<? extends Scope> subScopes = rootScope.getSubScopes();
    for (Scope scope : subScopes) {
      names.addAll(getSymbols(scope));
    }
    return names;
  }
  
  private void acceptModelState(ModelState modelState) {
    currentModelState = Optional.of(modelState);
  }
  
  private boolean findMatch(String incomplete, Symbol proposal, Optional<? extends Scope> scope) {
    if (proposal.getName().startsWith(incomplete)) {
      if (scope.isPresent()) {
        return scope.get().resolve(proposal.getName(), proposal.getKind()).isPresent();
      }
      else {
        return true;
      }
    }
    return false;
  }
  
  private boolean findMatch(String incomplete, String proposal) {
    return proposal.startsWith(incomplete);
  }
  
  @Override
  public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
    return null;
  }
  
  @Override
  public char[] getCompletionProposalAutoActivationCharacters() {
    return null;
  }
  
  @Override
  public char[] getContextInformationAutoActivationCharacters() {
    return null;
  }
  
  @Override
  public String getErrorMessage() {
    return "error during content assist";
  }
  
  @Override
  public IContextInformationValidator getContextInformationValidator() {
    return null;
  }
  
  private String getIncomplete(IDocument document, int offset) {
    try {
      String incompleted = "";
      boolean done = false;
      while (0 < offset && !done) {
        char precedingChar = document.getChar(--offset);
        if ('a' <= precedingChar && precedingChar <= 'z'
            || 'A' <= precedingChar && precedingChar <= 'Z') {
          incompleted = precedingChar + incompleted;
        }
        else {
          done = true;
        }
      }
      return incompleted;
    }
    catch (BadLocationException e) {
      throw new RuntimeException(e);
    }
  }
  
}
