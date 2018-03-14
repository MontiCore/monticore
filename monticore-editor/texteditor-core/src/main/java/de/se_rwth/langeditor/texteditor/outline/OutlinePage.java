/* (c)  https://github.com/MontiCore/monticore */package de.se_rwth.langeditor.texteditor.outline;

import static de.se_rwth.langeditor.util.antlr.ParseTrees.getTokenLength;

import java.util.Optional;

import org.eclipse.core.resources.IStorage;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import com.google.inject.Inject;

import de.monticore.ast.ASTNode;
import de.se_rwth.langeditor.injection.TextEditorScoped;
import de.se_rwth.langeditor.modelstates.Nodes;
import de.se_rwth.langeditor.modelstates.ModelState;
import de.se_rwth.langeditor.modelstates.ObservableModelStates;
import de.se_rwth.langeditor.util.Misc;
import de.se_rwth.langeditor.util.antlr.ParseTrees;

@TextEditorScoped
public class OutlinePage extends ContentOutlinePage {
  
  private final ITextEditor textEditor;
  
  private final IStorage storage;
  
  private final ObservableModelStates observableModelStates;
  
  private final Nodes nodes;
  
  private final TreeContentProviderImpl treeContentProviderImpl;
  
  private final ILabelProvider labelProvider;
  
  private Optional<ModelState> currentModelState;
  
  @Inject
  public OutlinePage(
      ITextEditor textEditor,
      IStorage storage,
      ObservableModelStates observableModelStates,
      Nodes nodes,
      TreeContentProviderImpl treeContentProviderImpl,
      ILabelProvider labelProvider) {
    this.textEditor = textEditor;
    this.storage = storage;
    this.observableModelStates = observableModelStates;
    this.nodes = nodes;
    this.treeContentProviderImpl = treeContentProviderImpl;
    this.labelProvider = labelProvider;
  }
  
  @Override
  public void createControl(Composite composite) {
    super.createControl(composite);
    TreeViewer treeViewer = getTreeViewer();
    treeViewer.setContentProvider(treeContentProviderImpl);
    treeViewer.setLabelProvider(labelProvider);
    observableModelStates.getModelStates().stream()
        .filter(modelState -> modelState.getStorage().equals(storage))
        .forEach(this::acceptModelState);
    observableModelStates.addStorageObserver(storage, this::acceptModelState);
  }
  
  @Override
  public void selectionChanged(SelectionChangedEvent event) {
    IStructuredSelection selection = (IStructuredSelection) event.getSelection();
    Object element = selection.getFirstElement();
    if (element != null) {
      nodes.getParseTree((ASTNode) element)
          .flatMap(ParseTrees::getFirstToken)
          .ifPresent(
              token -> textEditor.selectAndReveal(token.getStartIndex(), getTokenLength(token)));
    }
  }
  
  private void acceptModelState(ModelState modelState) {
    if (modelState.isLegal()) {
      currentModelState = Optional.of(modelState);
      Display.getDefault().asyncExec(() -> {
        getTreeViewer().setInput(modelState.getRootNode());
      });
    }
  }
  
  @Override
  public void setActionBars(IActionBars actionBars) {
    IToolBarManager toolBarManager = actionBars.getToolBarManager();
    toolBarManager.add(new SortOutline());
    toolBarManager.update(false);
    actionBars.updateActionBars();
  }
  
  public TreeViewer getTree() {
    return getTreeViewer();
  }
  
  private class SortOutline extends Action {
    
    @Override
    public int getStyle() {
      return AS_CHECK_BOX;
    }
    
    @Override
    public ImageDescriptor getImageDescriptor() {
      return Misc.loadImage("icons/alpha_mode.gif")
          .map(ImageDescriptor::createFromImage)
          .orElse(super.getImageDescriptor());
    }
    
    @Override
    public void run() {
      treeContentProviderImpl.setAlphabeticallySorted(isChecked());
      currentModelState.ifPresent(modelState -> {
        Display.getDefault().asyncExec(() -> {
          getTreeViewer().setInput(modelState.getRootNode());
        });
      });
    }
  }
}
