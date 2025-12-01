package mc.feature.inheritance.mulitpleOverrides;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.GeneratorIntegrationsTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MultipleOverridesTest extends GeneratorIntegrationsTest {

    @BeforeEach
    public void before() {
        LogStub.init();
        Log.enableFailQuick(false);
    }

    @Test
    public void testMultipleOverrides() {

    }

}
