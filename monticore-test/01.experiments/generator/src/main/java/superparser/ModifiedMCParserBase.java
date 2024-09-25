/* (c) https://github.com/MontiCore/monticore */
package superparser;

import de.monticore.antlr4.MCParser;
import org.antlr.v4.runtime.TokenStream;

public abstract class ModifiedMCParserBase extends MCParser {
  public ModifiedMCParserBase(TokenStream input) {
    super(input);
  }

  public ModifiedMCParserBase() {
  }

  public static int customCalled;

  public boolean doCustomMethod() {
    customCalled++;
    return true;
  }

}
