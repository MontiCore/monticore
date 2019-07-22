/* (c) https://github.com/MontiCore/monticore */

package mc.embedding.external.composite._symboltable;

import mc.embedding.external.composite._parser.CompositeParser;
import mc.embedding.external.host._symboltable.HostLanguage;

public class CompositeLanguage extends CompositeLanguageTOP {

  public static final String FILE_ENDING = HostLanguage.FILE_ENDING;

  public CompositeLanguage() {
    super("Composite Language", FILE_ENDING);

  }

  @Override public CompositeParser getParser() {
    return new CompositeParser();
  }


  @Override protected CompositeModelLoader provideModelLoader() {
    return new CompositeModelLoader(this);
  }
}
