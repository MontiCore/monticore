/* (c)  https://github.com/MontiCore/monticore */package de.se_rwth.langeditor.texteditor.outline;

import java.util.List;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.tree.ParseTree;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.google.inject.Inject;

import de.monticore.ast.ASTNode;
import de.monticore.utils.ASTNodes;
import de.se_rwth.langeditor.injection.TextEditorScoped;
import de.se_rwth.langeditor.language.OutlineElementSet;
import de.se_rwth.langeditor.modelstates.Nodes;

@TextEditorScoped
public class TreeContentProviderImpl implements ITreeContentProvider {
  
  private final Nodes nodes;
  
  private final OutlineElementSet outlineElements;
  
  private boolean isAlphabeticallySorted;
  
  @Inject
  public TreeContentProviderImpl(Nodes nodes, OutlineElementSet outlineElements) {
    this.nodes = nodes;
    this.outlineElements = outlineElements;
  }
  
  @Override
  public void dispose() {
    
  }
  
  @Override
  public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
  }
  
  @Override
  public Object[] getElements(Object inputElement) {
    if (inputElement instanceof ASTNode) {
      List<ASTNode> elements = getOutlineChildren((ASTNode) inputElement);
      if (isAlphabeticallySorted) {
        elements.sort((first, second) -> compareParseTrees((ASTNode) first, (ASTNode) second));
      }
      return elements.toArray();
    }
    throw new IllegalArgumentException("Unexpected input " + inputElement.getClass().getName());
  }
  
  @Override
  public Object[] getChildren(Object parentElement) {
    if (parentElement instanceof ASTNode) {
      List<ASTNode> children = getOutlineChildren((ASTNode) parentElement);
      if (isAlphabeticallySorted) {
        children.sort((first, second) -> compareParseTrees((ASTNode) first, (ASTNode) second));
      }
      return children.toArray();
    }
    return null;
  }
  
  private int compareParseTrees(ASTNode first, ASTNode second) {
    return outlineElements.getName(first).compareTo(outlineElements.getName(second));
  }
  
  @Override
  public Object getParent(Object element) {
    if (element instanceof ASTNode) {
      return nodes.getParent((ASTNode) element).orElse(null);
    }
    return null;
  }
  
  @Override
  public boolean hasChildren(Object element) {
    if (element instanceof ParseTree) {
      return !getOutlineChildren((ASTNode) element).isEmpty();
    }
    return false;
  }
  
  void setAlphabeticallySorted(boolean isAlphabeticallySorted) {
    this.isAlphabeticallySorted = isAlphabeticallySorted;
  }
  
  private List<ASTNode> getOutlineChildren(ASTNode astNode) {
    return ASTNodes.getSuccessors(astNode, ASTNode.class).stream()
        .skip(1)
        .filter(successor -> outlineElements.contains(successor.getClass()))
        .collect(Collectors.toList());
  }
}
