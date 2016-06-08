package de.se_rwth.langeditor.texteditor.contentassist;

import java.util.Optional;
import java.util.function.Supplier;

import org.antlr.v4.runtime.tree.ParseTree;
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
import de.se_rwth.langeditor.injection.TextEditorScoped;
import de.se_rwth.langeditor.modelstates.ModelState;
import de.se_rwth.langeditor.modelstates.Nodes;
import de.se_rwth.langeditor.modelstates.ObservableModelStates;
import de.se_rwth.langeditor.util.antlr.ParseTrees;

@TextEditorScoped
public class ContentAssistProcessorImpl implements IContentAssistProcessor {
  
  private final Supplier<Optional<ModelState>> currentModelState;
  
  @Inject
  public ContentAssistProcessorImpl(IStorage storage,
      ObservableModelStates observableModelStates) {
    this.currentModelState = () -> observableModelStates.findModelState(storage);
  }
  
  @Override
  public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
    String incomplete = getIncomplete(viewer.getDocument(), offset);
    CompletionProposal newProposal = newProposal(findMatch(incomplete, offset), offset,
        incomplete.length());
    return new CompletionProposal[] { newProposal };
  }
  
  private String findMatch(String incomplete, int offset) {
    return "";
//    return currentModelState.get().flatMap(ModelState::getLastLegalState)
//        .flatMap(modelState ->
//            ParseTrees.getTerminalBySourceCharIndex(modelState.getRootContext(), offset))
//        .flatMap(this::getEnclosingAstNode)
//        .flatMap(enclosingAstNode -> enclosingAstNode.getEnclosingScope().map(scope ->
//            scope.resolve(symbol -> {
//              System.err.println("checking " + symbol.getName() + " against " + incomplete);
//              return symbol.getName().startsWith(incomplete);
//            })))
//        .map(symbol -> ((Symbol) symbol).getName()).orElse("");
  }
  
  private Optional<ASTNode> getEnclosingAstNode(ParseTree parseTree) {
    return ParseTrees.bottomUpAncestors(parseTree).stream()
        .map(Nodes::getAstNode)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .findFirst();
  }
  
  private CompletionProposal newProposal(String complete, int offset, int prefixLength) {
    return new CompletionProposal(complete, offset - prefixLength, prefixLength,
        complete.length());
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
