/* (c) https://github.com/MontiCore/monticore */

import automatatagschema.AutomataTagSchemaMill;
import de.monticore.tagging.tagschema._ast.ASTTagSchema;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InvalidTagSchemaTest {

  @BeforeAll
  public static void prepare()  {
    LogStub.init();
    Log.enableFailQuick(false);

    AutomataTagSchemaMill.init();
  }

  @BeforeEach
  public void beforeEach() {
    Log.clearFindings();
  }

  @Test
  public void test() throws IOException {
    Optional<ASTTagSchema> astOpt = AutomataTagSchemaMill.parser().parse("src/test/resources/schema/invalid/InvalidTagSchema.tagschema");
    assertTrue(astOpt.isPresent());
    new de.monticore.tagging.tagschema.TagSchemaAfterParseTrafo().transform(astOpt.get());
    assertEquals(1, Log.getErrorCount());
  }

}
