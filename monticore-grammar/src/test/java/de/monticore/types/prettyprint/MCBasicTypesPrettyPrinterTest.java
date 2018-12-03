package de.monticore.types.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedReferenceType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypestest._parser.MCBasicTypesTestParser;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MCBasicTypesPrettyPrinterTest {

  @Test
  public void primitivesTest(){
    Class foo = boolean.class;
    MCBasicTypesPrettyPrinter prettyprinter = new MCBasicTypesPrettyPrinter(new IndentPrinter());

    String[] primitives = new String[]{"boolean", "byte", "char", "short", "int", "long",
        "float", "double"};
    try {
      for (String primitive : primitives) {
        prettyprinter.getPrinter().clearBuffer();
        MCBasicTypesTestParser mcBasicTypesParser = new MCBasicTypesTestParser();
        // .parseType(primitive);

        Optional<? extends ASTMCType> type = mcBasicTypesParser.parse_StringMCPrimitiveType(primitive);
        type.get().accept(prettyprinter);
        assertTrue(type.isPresent());
        assertEquals(primitive,prettyprinter.getPrinter().getContent());
        assertTrue(type.get() instanceof ASTMCPrimitiveType);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  @Test
  public void simpleReferenceTest(){
    MCBasicTypesPrettyPrinter prettyprinter = new MCBasicTypesPrettyPrinter(new IndentPrinter());
    String simpleReference = "de.monticore.types.prettyprint";
    try{
      MCBasicTypesTestParser mcBasicTypesParser= new MCBasicTypesTestParser();
      Optional<? extends ASTMCType> type = mcBasicTypesParser.parse_StringMCQualifiedReferenceType(simpleReference);
      type.get().accept(prettyprinter);
      assertTrue(type.isPresent());
      assertEquals(simpleReference,prettyprinter.getPrinter().getContent());
      assertTrue(type.get() instanceof ASTMCQualifiedReferenceType);
    }catch(IOException e){
      e.printStackTrace();
    }
  }

}
