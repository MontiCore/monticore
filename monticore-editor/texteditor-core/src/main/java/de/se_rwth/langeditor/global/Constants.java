/*******************************************************************************
 * MontiCore Language Workbench
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
package de.se_rwth.langeditor.global;

import org.eclipse.core.runtime.Path;

public final class Constants {
  
  private Constants() {
    // non-instantiable
  }
  
  public static final String LANGUAGE_EXTENSION_POINT = "texteditor-core.language";
  
  public static final String LANGUAGE_EXTENSION_POINT_IMPLEMENTATION = "implementation";
  
  public static final String EDITOR_ID = "de.se_rwth.langeditor";
  
  public static final String MODELPATH_ENTRY = "texteditor-core.modelpathentry";
  
  public static final Path MODELPATH = new Path("texteditor-core.modelpath");
}
