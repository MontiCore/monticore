/* (c)  https://github.com/MontiCore/monticore */package de.se_rwth.langeditor.texteditor.hyperlinks;

import java.util.Collections;
import java.util.Optional;
import java.util.function.Supplier;

import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.Trees;
import org.eclipse.core.resources.IStorage;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

import de.monticore.ast.ASTNode;
import de.se_rwth.langeditor.injection.TextEditorScoped;
import de.se_rwth.langeditor.language.Language;
import de.se_rwth.langeditor.modelstates.Nodes;
import de.se_rwth.langeditor.modelstates.ModelState;
import de.se_rwth.langeditor.modelstates.ObservableModelStates;
import de.se_rwth.langeditor.util.Misc;
import de.se_rwth.langeditor.util.antlr.ParseTrees;

@TextEditorScoped
public final class HyperlinkDetectorImpl extends AbstractHyperlinkDetector {
  
  private final Language language;
  
  private final Nodes nodes;
  
  private final ObservableModelStates observableModelStates;
  
  private final Supplier<Optional<ModelState>> currentModelState;
  
  @Inject
  HyperlinkDetectorImpl(Language language, Nodes nodes, IStorage storage,
      ObservableModelStates observableModelStates) {
    this.language = language;
    this.nodes = nodes;
    this.observableModelStates = observableModelStates;
    this.currentModelState = () -> observableModelStates.findModelState(storage);
  }
  
  @Override
  public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region,
      boolean canShowMultipleHyperlinks) {
    Optional<ASTNode> enclosingASTNode = getEnclosingASTNode(region);
    Optional<Supplier<Optional<ASTNode>>> resolver = enclosingASTNode
        .flatMap(language::createResolver);
    if (enclosingASTNode.isPresent() && resolver.isPresent()) {
      IHyperlink hyperlink = new ParseTreeHyperlinkAdapter(
          enclosingASTNode.flatMap(nodes::getParseTree).get(), resolver.get());
      return new IHyperlink[] { hyperlink };
    }
    return null;
  }
  
  private Optional<ASTNode> getEnclosingASTNode(IRegion region) {
    return currentModelState.get()
        .flatMap(currentModelState -> ParseTrees.getTerminalBySourceCharIndex(
            currentModelState.getRootContext(), region.getOffset()))
        .map(Trees::getAncestors)
        .map(ancestors -> Lists.reverse(ancestors))
        .orElse(Collections.emptyList())
        .stream()
        .filter(ParseTree.class::isInstance)
        .map(ParseTree.class::cast)
        .map(Nodes::getAstNode)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .findFirst();
  }
  
  private class ParseTreeHyperlinkAdapter implements IHyperlink {
    
    private final ParseTree parseTree;
    
    private final Supplier<Optional<ASTNode>> resolver;
    
    private ParseTreeHyperlinkAdapter(ParseTree parseTree, Supplier<Optional<ASTNode>> resolver) {
      this.parseTree = parseTree;
      this.resolver = resolver;
    }
    
    @Override
    public IRegion getHyperlinkRegion() {
      Interval interval = ParseTrees.tokenInterval(parseTree);
      return new Region(interval.a, interval.length() - 1);
    }
    
    @Override
    public String getTypeLabel() {
      return null;
    }
    
    @Override
    public String getHyperlinkText() {
      return null;
    }
    
    @Override
    public void open() {
      resolver.get()
          .flatMap(nodes::getParseTree)
          .ifPresent(resolved -> {
            findStorage(resolved).ifPresent(storage -> openEditor(resolved, storage));
          });
    }
    
    private Optional<IStorage> findStorage(ParseTree parseTree) {
      return observableModelStates.getModelStates().stream()
          .filter(modelState -> Trees.descendants(modelState.getRootContext()).stream()
              .anyMatch(parseTree::equals))
          .findFirst()
          .map(ModelState::getStorage);
    }
  }
  
  private void openEditor(ParseTree parseTree, IStorage storage) {
    try {
      IEditorDescriptor editorDescriptor = IDE.getEditorDescriptor(storage.getName());
      ITextEditor textEditor = (ITextEditor) IDE.openEditor(
          PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(),
          Misc.getEditorInput(storage), editorDescriptor.getId());
      int startIndex = ParseTrees.getFirstToken(parseTree).get().getStartIndex();
      textEditor.selectAndReveal(startIndex, 0);
    }
    catch (PartInitException e) {
      throw new RuntimeException(e);
    }
  }
}
