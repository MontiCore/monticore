/* (c) https://github.com/MontiCore/monticore */
import automata.AutomataMill;
import automata._symboltable.IAutomataGlobalScope;
import automata._symboltable.MyStateDeSer;
import automata._symboltable.MyStateResolver;
import de.monticore.io.paths.MCPath;
import org.junit.Test;
import java.nio.file.Paths;

public class GlobalScopeTest {

  @Test
  public void testGS() {

    // tests availability of the configuration methods
    IAutomataGlobalScope gs = AutomataMill.globalScope();
    gs.setSymbolPath(new MCPath(Paths.get("src/models")));
    gs.setFileExt("autsym");
    gs.addAdaptedStateSymbolResolver(new MyStateResolver());
    gs.putSymbolDeSer("automata._symboltable.StateSymbol", 
        new MyStateDeSer());

  }
}
