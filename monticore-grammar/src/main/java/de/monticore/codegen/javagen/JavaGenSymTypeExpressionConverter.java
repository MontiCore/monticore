/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.javagen;

import de.monticore.codegen.CodeGenSymTypeExpressionConverter;
import de.monticore.codegen.javagen.typeconverter.JavaBooleanConversionHandler;
import de.monticore.codegen.javagen.typeconverter.JavaFunctionConversionHandler;
import de.monticore.codegen.javagen.typeconverter.JavaNumericConversionHandler;
import de.monticore.codegen.javagen.typeconverter.JavaObjectConversionHandler;
import de.monticore.codegen.javagen.typeconverter.JavaStringConversionHandler;
import de.monticore.codegen.javagen.typeconverter.JavaTupleConversionHandler;
import de.se_rwth.commons.logging.Log;

import java.util.List;

public class JavaGenSymTypeExpressionConverter
    extends CodeGenSymTypeExpressionConverter {

  protected JavaGenSymTypeExpressionConverter() {
    setConversionHandlers(List.of(
        new JavaBooleanConversionHandler(),
        new JavaFunctionConversionHandler(),
        new JavaNumericConversionHandler(),
        new JavaTupleConversionHandler(),
        new JavaStringConversionHandler(),
        new JavaObjectConversionHandler()
    ));
  }

  // static delegate
  public static void init() {
    Log.trace("init JavaGenSymTypeExpressionConverter", "CodeGen setup");
    JavaGenSymTypeExpressionConverter converter =
        new JavaGenSymTypeExpressionConverter();
    CodeGenSymTypeExpressionConverter.setDelegate(converter);
  }

}
