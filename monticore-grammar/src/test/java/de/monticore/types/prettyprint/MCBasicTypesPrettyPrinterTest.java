package de.monticore.types.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTSimpleReferenceType;
import de.monticore.types.mcbasictypes._ast.ASTType;
import de.monticore.types.mcbasictypes._ast.ASTVoidType;
import de.monticore.types.mcbasictypestest._parser.MCBasicTypesTestParser;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MCBasicTypesPrettyPrinterTest {

  @Test
  public void primitivesTest(){
    Class foo = boolean.class;
    MCBasicTypesPrettyPrinterConcreteVisitor prettyprinter = new MCBasicTypesPrettyPrinterConcreteVisitor(new IndentPrinter());

    String[] primitives = new String[]{"boolean", "byte", "char", "short", "int", "long",
        "float", "double"};
    try {
      for (String primitive : primitives) {
        prettyprinter.getPrinter().clearBuffer();
        MCBasicTypesTestParser mcBasicTypesParser = new MCBasicTypesTestParser();
        // .parseType(primitive);

        Optional<? extends ASTType> type = mcBasicTypesParser.parse_StringPrimitiveType(primitive);
        type.get().accept(prettyprinter);
        assertTrue(type.isPresent());
        assertEquals(primitive,prettyprinter.getPrinter().getContent());
        assertTrue(type.get() instanceof ASTPrimitiveType);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  @Test
  public void simpleReferenceTest(){
    MCBasicTypesPrettyPrinterConcreteVisitor prettyprinter = new MCBasicTypesPrettyPrinterConcreteVisitor(new IndentPrinter());
    String simpleReference = "de.monticore.types.prettyprint";
    try{
      MCBasicTypesTestParser mcBasicTypesParser= new MCBasicTypesTestParser();
      Optional<? extends ASTType> type = mcBasicTypesParser.parse_StringSimpleReferenceType(simpleReference);
      type.get().accept(prettyprinter);
      assertTrue(type.isPresent());
      assertEquals(simpleReference,prettyprinter.getPrinter().getContent());
      assertTrue(type.get() instanceof ASTSimpleReferenceType);
    }catch(IOException e){
      e.printStackTrace();
    }
  }

}
