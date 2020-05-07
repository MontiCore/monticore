/* (c) https://github.com/MontiCore/monticore */
package mc.feature.errorcode;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * checks that generated error codes are unique
 * tests this with the generated files from automaton
 * a test for more files would take to long
 */
public class UniqueGeneratedErrorCodeTest {

  private List<String> errorCodes = new ArrayList<>();

  private final Pattern pattern = Pattern.compile(".*0xA(\\d{4})x(\\d{10}).*");

  @Test
  public void testOnlyUniqueGeneratedErrorCodes() {
    final File[] files = new File(
        "target/generated-sources/monticore/sourcecode/mc/examples/automaton/automaton").listFiles();
    assertNotNull(files);
    checkFileList(files);
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
              assertFalse(errorCodes.contains(code));
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
