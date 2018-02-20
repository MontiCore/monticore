/* (c)  https://github.com/MontiCore/monticore */package de.se_rwth.langeditor.texteditor.outline;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.google.inject.Inject;

import de.monticore.ast.ASTNode;
import de.se_rwth.langeditor.injection.TextEditorScoped;
import de.se_rwth.langeditor.language.OutlineElementSet;

@TextEditorScoped
public class LabelProviderImpl extends LabelProvider {
  
  private final OutlineElementSet outlineElements;
  
  @Inject
  public LabelProviderImpl(OutlineElementSet outlineElements) {
    this.outlineElements = outlineElements;
  }
  
  @Override
  public Image getImage(Object element) {
    return outlineElements.getImage(element.getClass()).orElse(null);
  }
  
  @Override
  public String getText(Object element) {
    return  outlineElements.getName((ASTNode) element);
  }
}
