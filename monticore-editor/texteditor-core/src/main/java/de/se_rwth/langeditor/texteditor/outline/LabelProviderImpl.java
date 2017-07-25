/*******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
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
