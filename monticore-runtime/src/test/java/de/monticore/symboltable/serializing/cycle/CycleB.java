/*
 * ******************************************************************************
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
 * ******************************************************************************
 */

package de.monticore.symboltable.serializing.cycle;

import java.io.Serializable;

/**
 * TODO: Write me!
 *
 * @author  Pedram Mir Seyed Nazari
 *
 */
public class CycleB implements Serializable {

  private static final long serialVersionUID = 5687822153183482920L;
  
  private CycleC c;

  public CycleC getC() {
    return c;
  }

  public void setC(CycleC c) {
    this.c = c;
  }
  
}
