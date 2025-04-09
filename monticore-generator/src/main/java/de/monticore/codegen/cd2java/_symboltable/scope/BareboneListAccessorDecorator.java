package de.monticore.codegen.cd2java._symboltable.scope;

import de.monticore.codegen.cd2java.methods.accessor.ListAccessorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

import java.util.List;

public class BareboneListAccessorDecorator extends ListAccessorDecorator {
  public BareboneListAccessorDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  protected List<String> getMethodSignatures() {
    return List.of();
//    return Arrays.asList(
//            String.format(CONTAINS, capitalizedAttributeNameWithOutS),
//            String.format(CONTAINS_ALL, capitalizedAttributeNameWithS),
//            String.format(IS_EMPTY, capitalizedAttributeNameWithS),
//            String.format(ITERATOR, attributeType, capitalizedAttributeNameWithS),
//            String.format(SIZE, capitalizedAttributeNameWithS),
//            String.format(TO_ARRAY, attributeType, capitalizedAttributeNameWithS, attributeType),
//            String.format(TO_ARRAY_, capitalizedAttributeNameWithS),
//            String.format(SPLITERATOR, attributeType, capitalizedAttributeNameWithS),
//            String.format(STREAM, attributeType, capitalizedAttributeNameWithS),
//            String.format(PARALLEL_STREAM, attributeType, capitalizedAttributeNameWithS),
//            String.format(GET, attributeType, capitalizedAttributeNameWithOutS),
//            String.format(INDEX_OF, capitalizedAttributeNameWithOutS),
//            String.format(LAST_INDEX_OF, capitalizedAttributeNameWithOutS),
//            String.format(EQUALS, capitalizedAttributeNameWithS),
//            String.format(HASHCODE, capitalizedAttributeNameWithS),
//            String.format(LIST_ITERATOR, attributeType, capitalizedAttributeNameWithS),
//            String.format(LIST_ITERATOR_, attributeType, capitalizedAttributeNameWithS),
//            String.format(SUBLIST, attributeType, capitalizedAttributeNameWithS)
//                        );
  }
}
