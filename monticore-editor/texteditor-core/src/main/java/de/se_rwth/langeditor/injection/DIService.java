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
package de.se_rwth.langeditor.injection;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.texteditor.ITextEditor;

import com.google.inject.Guice;
import com.google.inject.Injector;

public final class DIService {
  
  private DIService() {
    throw new AssertionError(getClass().getSimpleName() + " can't be instantiated");
  }
  
  private static final GuiceConfig guiceConfig = new GuiceConfig();
  
  private static final Injector injector = Guice.createInjector(guiceConfig);
  
  public static void injectMembers(Object object) {
    injector.injectMembers(object);
  }
  
  public static void injectMembers(IProject project, Object object) {
    guiceConfig.enter(project);
    injector.injectMembers(object);
    guiceConfig.leave();
  }
  
  public static void injectMembers(ITextEditor textEditor, Object object) {
    guiceConfig.enter(textEditor);
    injector.injectMembers(object);
    guiceConfig.leave();
  }
  
  public static <T> T getInstance(Class<T> type) {
    return injector.getInstance(type);
  }
  
  public static <T> T getInstance(IProject project, Class<T> type) {
    guiceConfig.enter(project);
    T instance = injector.getInstance(type);
    guiceConfig.leave();
    return instance;
  }
  
  public static <T> T getInstance(ITextEditor textEditor, Class<T> type) {
    guiceConfig.enter(textEditor);
    T instance = injector.getInstance(type);
    guiceConfig.leave();
    return instance;
  }
  
}
