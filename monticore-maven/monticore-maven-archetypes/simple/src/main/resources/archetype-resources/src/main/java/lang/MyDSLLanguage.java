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

package ${package}.lang;

import java.util.Optional;

import javax.annotation.Nullable;

import de.monticore.CommonModelingLanguage;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.resolving.CommonResolvingFilter;
import ${package}.mydsl._parser.MyDSLParser;
import ${package}.symboltable.MyDSLModelLoader;
import ${package}.symboltable.MyDSLSymbolTableCreator;
import ${package}.symboltable.MyElementSymbol;
import ${package}.symboltable.MyFieldSymbol;
import ${package}.symboltable.MyModelSymbol;

public class MyDSLLanguage extends CommonModelingLanguage {
  
  public static final String FILE_ENDING = "mydsl";
  
  public MyDSLLanguage() {
    super("MyDSL", FILE_ENDING);
    
    addResolver(CommonResolvingFilter.create(MyModelSymbol.class, MyModelSymbol.KIND));
    addResolver(CommonResolvingFilter.create(MyElementSymbol.class, MyElementSymbol.KIND));
    addResolver(CommonResolvingFilter.create(MyFieldSymbol.class, MyFieldSymbol.KIND));
  }
  
  @Override
  public MyDSLParser getParser() {
    return new MyDSLParser();
  }
  
  @Override
  public Optional<MyDSLSymbolTableCreator> getSymbolTableCreator(
      ResolvingConfiguration resolvingConfiguration, @Nullable MutableScope enclosingScope) {
    return Optional
        .of(new MyDSLSymbolTableCreator(resolvingConfiguration, enclosingScope));
  }
  
  @Override
  public MyDSLModelLoader getModelLoader() {
    return (MyDSLModelLoader) super.getModelLoader();
  }
  
  @Override
  protected MyDSLModelLoader provideModelLoader() {
    return new MyDSLModelLoader(this);
  }
}
