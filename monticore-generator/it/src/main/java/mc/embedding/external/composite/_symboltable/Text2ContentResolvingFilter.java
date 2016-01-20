/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
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
 * ******************************************************************************
 */

package mc.embedding.external.composite._symboltable;

import static com.google.common.base.Preconditions.checkArgument;

import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.resolving.CommonAdaptedResolvingFilter;
import mc.embedding.external.embedded._symboltable.TextSymbol;
import mc.embedding.external.host._symboltable.ContentSymbol;

public class Text2ContentResolvingFilter extends CommonAdaptedResolvingFilter<ContentSymbol>{

  public Text2ContentResolvingFilter() {
    super(TextSymbol.KIND, ContentSymbol.class, ContentSymbol.KIND);
  }

  @Override
  protected ContentSymbol createAdapter(Symbol textSymbol) {
    checkArgument(textSymbol instanceof TextSymbol);

    return new Text2ContentAdapter((TextSymbol) textSymbol);
  }
}
