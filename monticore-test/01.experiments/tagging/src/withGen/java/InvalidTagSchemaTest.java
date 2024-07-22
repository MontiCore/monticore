/* (c) https://github.com/MontiCore/monticore */

import automatatagschema.AutomataTagSchemaMill;
import de.monticore.tagging.tagschema._ast.ASTTagSchema;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

public class InvalidTagSchemaTest {

  @BeforeClass
  public static void prepare()  {
    LogStub.init();
    Log.enableFailQuick(false);

    AutomataTagSchemaMill.init();
  }

  @Before
  public void beforeEach() {
    Log.clearFindings();
  }

  @Test
  public void test() throws IOException {
    Optional<ASTTagSchema> astOpt = AutomataTagSchemaMill.parser().parse("src/test/resources/schema/invalid/InvalidTagSchema.tagschema");
    Assert.assertTrue(astOpt.isPresent());
    new de.monticore.tagging.tagschema.TagSchemaAfterParseTrafo().transform(astOpt.get());
    Assert.assertEquals(1, Log.getErrorCount());
  }

}
