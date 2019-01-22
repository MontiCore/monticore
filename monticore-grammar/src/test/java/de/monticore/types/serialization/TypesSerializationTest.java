/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.types.serialization;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Optional;

import org.junit.Test;

import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypestest._parser.MCBasicTypesTestParser;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 * @since   TODO: add version number
 *
 */
public class TypesSerializationTest {
  

  
  @Test
  public void testSimpleType() throws IOException {
    TypesSerializer typesSerializer = new TypesSerializer();
    
    MCBasicTypesTestParser mcBasicTypesParser = new MCBasicTypesTestParser();
    Optional<ASTMCQualifiedType> boolOpt = mcBasicTypesParser.parse_StringMCQualifiedType("java.lang.String");
    assertTrue(boolOpt.isPresent());
    
    Optional<String> serialized = typesSerializer.serialize(boolOpt.get());
    assertTrue(serialized.isPresent());
    assertTrue(serialized.get().length()>0);
    System.out.println(serialized.get());
    
    Optional<ASTMCType> deserialize = typesSerializer.deserialize(serialized.get());
  }
}
