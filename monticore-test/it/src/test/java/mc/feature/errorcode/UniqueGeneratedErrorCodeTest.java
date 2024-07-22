/* (c) https://github.com/MontiCore/monticore */
package mc.feature.errorcode;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * checks that generated error codes are unique
 * tests this with the generated files from automaton
 * a test for more files would take to long
 */
public class UniqueGeneratedErrorCodeTest {

  private List<String> errorCodes = new ArrayList<>();

  private final Pattern pattern = Pattern.compile(".*0xA(\\d{4})x(\\d{10}).*");
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void testOnlyUniqueGeneratedErrorCodes() {
    File[] files = new File(
        "target/generated-sources/monticore/sourcecode/mc/examples/automaton/automaton").listFiles();
    if (files == null) {
      files = new File(
              "target-emf/generated-sources/monticore/sourcecode/mc/examples/automaton/automaton").listFiles();
    }
    Assertions.assertNotNull(files);
    checkFileList(files);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  protected void checkFileList(File[] files) {
    for (final File file : files) {
      if (file.listFiles() != null) {
        checkFileList(file.listFiles());
      } else {
        try {
          final BufferedReader reader = new BufferedReader(new FileReader(file));
          final StringBuilder contents = new StringBuilder();
          while (reader.ready()) {
            contents.append(reader.readLine());
          }
          reader.close();
          final String stringContents = contents.toString();
          Matcher m = pattern.matcher(stringContents);
          if (m.find()) {
            for (int i = 1; i < m.groupCount(); i = i + 2) {
              String code = m.group(i) + m.group(i + 1);
              Assertions.assertFalse(errorCodes.contains(code));
              errorCodes.add(code);
            }
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
