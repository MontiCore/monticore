package de.monticore.codegen.cd2java;

import de.monticore.cd.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.cd.cd4code._parser.CD4CodeParser;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CDTypeFactory {

  public static ASTMCType create(String type) {
    CD4CodeParser p = new CD4CodeParser();
    Optional<ASTMCType> ast = null;
    try {
      ast = p.parse_StringMCType(type);
    } catch (IOException e) {
      fail();
    }
    assertTrue(ast.isPresent());
    return ast.get();
  }
}
