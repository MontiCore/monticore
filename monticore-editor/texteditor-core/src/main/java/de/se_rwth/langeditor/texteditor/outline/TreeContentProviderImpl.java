/*******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, 2016, MontiCore, All rights reserved.
 *  
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.se_rwth.langeditor.texteditor.outline;

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
    // TODO Auto-generated method stub
    
  }
  
  @Override
  public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    // TODO Auto-generated method stub
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
