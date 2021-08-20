package de.monticore.dstlgen;

import de.monticore.grammar.grammarfamily.GrammarFamilyMill;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

@Ignore // TODO: This test will fail until MC 7.2.0 is the previous grammar version
public class DSTLGenCLITest {

  @Before
  public void init() {
    GrammarFamilyMill.init();
    LogStub.initDEBUG();
  }


  @Test
  public void testSocial() {
    new DSTLGenCLI().run(new String[]{"-g", "../monticore-test/montitrans/test-dstl-gen/src/main/grammars/mc/testcases/social/SocialNetwork.mc4",
            "-hcg", "../monticore-test/montitrans/test-dstl-gen/src/main/grammarsHC/",
            "-o", "../monticore-test/montitrans/test-dstl-gen/target/generated-sources",
            "-mp", makeLocalPathForTesting()
    });
  }

  @Test
  public void testGeneric() {
    new DSTLGenCLI().run(new String[]{"-g", "../monticore-test/montitrans/test-dstl-gen/src/main/grammars/mc/testcases/GenericDSL.mc4",
            "-hcg", "../monticore-test/montitrans/test-dstl-gen/src/main/grammarsHC/",
            "-o", "../monticore-test/montitrans/test-dstl-gen/target/generated-sources",
            "-mp", makeLocalPathForTesting()
    });
  }

  @Test
  public void testHelp() {
    DSTLGenCLI.main(new String[]{"-h"});
  }


  private String makeLocalPathForTesting() {
    URL[] urls;
    // Use the getProperty for after java 9 or custom classloaders
    String[] entries = System.getProperty("java.class.path").split(File.pathSeparator);
    urls = new URL[entries.length];
    for (int i = 0; i < entries.length; i++) {
      try {
        // Ensure we have absolute paths
        urls[i] = Paths.get(entries[i]).toAbsolutePath().toUri().toURL();
      } catch (MalformedURLException e) {
        e.printStackTrace();
      }
    }
    System.err.println(Arrays.toString(urls));
    return Arrays.stream(urls).map(URL::getPath).collect(Collectors.joining(File.pathSeparator));
  }

}
