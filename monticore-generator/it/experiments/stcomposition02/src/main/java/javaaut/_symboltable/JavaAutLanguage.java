/* (c) https://github.com/MontiCore/monticore */

package javaaut._symboltable;

import automata5._symboltable.Automata5LanguageTOP;
import automata5._symboltable.Automata5ModelLoader;

@Deprecated //will be removed soon
public class JavaAutLanguage extends JavaAutLanguageTOP {

  public JavaAutLanguage(){
    super("JavaAutLanguage","javaaut");
  }

  @Override protected JavaAutModelLoader provideModelLoader() {
    return new JavaAutModelLoader(this);
  }
}
