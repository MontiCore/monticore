/* (c)  https://github.com/MontiCore/monticore */package de.se_rwth.langeditor.injection;

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
