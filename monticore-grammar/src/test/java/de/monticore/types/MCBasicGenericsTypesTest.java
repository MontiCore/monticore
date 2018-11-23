package de.monticore.types;


import de.monticore.types.mcbasicgenericstypes._ast.ASTListType;
import de.monticore.types.mcbasicgenericstypes._ast.ASTOptionalType;
import de.monticore.types.mcbasicgenericstypes._ast.ASTSetType;
import de.monticore.types.mcbasicgenericstypes._visitor.MCBasicGenericsTypesVisitor;
import de.monticore.types.mcbasicgenericstypestest._parser.MCBasicGenericsTypesTestParser;
import de.monticore.types.mcbasictypes._ast.*;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesVisitor;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MCBasicGenericsTypesTest {

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }

  @Test
  public void testBasicGenericsTypes() throws IOException {
    Class foo = boolean.class;
    String[] types = new String[]{"List<String>","Optional<String>"};

    for (String testType : types) {
      MCBasicGenericsTypesTestParser mcBasicTypesParser = new MCBasicGenericsTypesTestParser();
      // .parseType(primitive);

      Optional<ASTType> type = mcBasicTypesParser.parse_String(testType);

      assertNotNull(type);
      assertTrue(type.isPresent());
      assertTrue(type.get() instanceof ASTReferenceType);

      ASTReferenceType t = (ASTReferenceType) type.get();
      t.accept( new MCBasicGenericsTypesVisitor() {
        public void visit(ASTListType t) {
          assertTrue(true);
          t.getBuiltInType().accept(new MCBasicTypesVisitor() {
            @Override
            public void visit(ASTBuiltInType node) {
              if (!(node instanceof ASTStringRereferenceType)) {
                fail("Found not String");
              }
            }
          });
        }
      });
    }

  }


  private class CheckTypeVisitor implements MCBasicGenericsTypesVisitor {

  }

}
