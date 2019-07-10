/* (c) https://github.com/MontiCore/monticore */

package mc.embedding.composite._symboltable;

import mc.embedding.host._symboltable.HostLanguage;

public class CompositeLanguage extends CompositeLanguageTOP {

  public static final String FILE_ENDING = HostLanguage.FILE_ENDING;

  public CompositeLanguage() {
    super("Composite Language", FILE_ENDING);

  }


  @Override protected CompositeModelLoader provideModelLoader() {
    return new CompositeModelLoader(this);
  }
}
