/* (c) https://github.com/MontiCore/monticore */

package automata6._symboltable;

@Deprecated //will be removed soon
public class Automata6Language extends Automata6LanguageTOP {

  public Automata6Language(){
    super("Automata6","aut");
  }

  @Override protected Automata6ModelLoader provideModelLoader() {
    return new Automata6ModelLoader(this);
  }
}
