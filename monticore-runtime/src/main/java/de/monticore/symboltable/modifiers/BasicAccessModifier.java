/*
 * ******************************************************************************
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
 * ******************************************************************************
 */

package de.monticore.symboltable.modifiers;

/**
 * Contains constants for basic access modifiers that exist in Java, i.e., public, protected,
 * package-local and private. Additional, the constant {@link #ABSENT} represents the absent of
 * modifiers.
 *
 * @author Pedram Mir Seyed Nazari
 */
public enum BasicAccessModifier implements AccessModifier {
  
  PUBLIC {
    
    @Override
    public boolean includes(AccessModifier modifier) {
      return modifier.equals(PUBLIC);
    }
    
    @Override
    public String toString() {
      return "public";
    }
  },
  
  PROTECTED {
    
    @Override
    public boolean includes(AccessModifier modifier) {
      return (modifier.equals(PUBLIC) || modifier.equals(PROTECTED));
    }
    
    @Override
    public String toString() {
      return "protected";
    }
  },
  
  PACKAGE_LOCAL {
    
    @Override
    public boolean includes(AccessModifier modifier) {
      return (modifier.equals(PUBLIC)
          || modifier.equals(PROTECTED)
          || modifier.equals(PACKAGE_LOCAL));
    }
    
    @Override
    public String toString() {
      return "package_local";
    }   
  },
  
  PRIVATE {
    
    public boolean includes(AccessModifier modifier) {
      return (modifier.equals(PUBLIC)
          || modifier.equals(PROTECTED)
          || modifier.equals(PACKAGE_LOCAL)
          || modifier.equals(PRIVATE));
    }

    @Override
    public String toString() {
      return "private";
    }    
 },
}
