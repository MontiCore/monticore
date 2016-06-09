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
package de.se_rwth.langeditor.language;

import java.util.Optional;
import java.util.function.Function;

import org.eclipse.swt.graphics.Image;

import de.monticore.ast.ASTNode;

class OutlineElement<T extends ASTNode> {
  
  private final Class<T> ruleType;
  
  private final Function<T, String> nameGetter;
  
  private final Optional<Image> image;
  
  OutlineElement(
      Class<T> ruleType,
      Function<T, String> nameGetter,
      Optional<Image> image) {
    this.ruleType = ruleType;
    this.nameGetter = nameGetter;
    this.image = image;
  }
  
  Class<T> getRuleType() {
    return ruleType;
  }
  
  Optional<String> getName(Object context) {
    if (ruleType.isInstance(context)) {
      return Optional.of(nameGetter.apply(ruleType.cast(context)));
    }
    return Optional.empty();
  }
  
  Optional<Image> getImage() {
    return image;
  }
}
