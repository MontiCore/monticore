/* (c) https://github.com/MontiCore/monticore */
package mc.feature.parserinfo;

import de.monticore.runtime.junit.AbstractMCTest;
import mc.feature.parserinfo.parserinfosimpleinheritancetest._parser._auxiliary.ParserInfoSimpleInheritanceTestParserInfoForParserInfoTest;
import mc.feature.parserinfo.parserinfotest._parser.ParserInfoTestParserInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.Parameter;
import org.junit.jupiter.params.ParameterizedClass;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test the generated ParserInfo classes.
 * Since the concrete antlr state numbers are not stable, we must always check a range of state numbers.
 */
@ParameterizedClass
@ValueSource(booleans =  {true, false})
public class ParserInfoTest extends AbstractMCTest {
    @Parameter
    private boolean useSimpleInheritance;

    // The generated parser has around 125 states
    // => add some safety margin
    private final int MAX_STATE_NUMBER = 250;

    @BeforeEach
    public void init(){
        if(useSimpleInheritance){
            ParserInfoTestParserInfo.initMe(new ParserInfoSimpleInheritanceTestParserInfoForParserInfoTest());
        }else{
            ParserInfoTestParserInfo.init();
        }
    }
    
    @Test
    public void testNoRef() {
        List<Integer> states = IntStream.range(0, MAX_STATE_NUMBER)
                .filter(ParserInfoTestParserInfo::stateHasUsageNameUsageName1)
                .boxed()
                .toList();

        assertEquals(1, states.size());

        int s = states.getFirst();
        assertFalse(ParserInfoTestParserInfo.stateReferencesElementASymbol(s));
        assertFalse(ParserInfoTestParserInfo.stateReferencesElementBSymbol(s));
    }

    @Test
    public void testSimpleReference() {
        List<Integer> states = IntStream.range(0, MAX_STATE_NUMBER)
                .filter(ParserInfoTestParserInfo::stateHasUsageNameUsageName2)
                .boxed()
                .toList();

        assertEquals(1, states.size());

        int s = states.getFirst();
        assertTrue(ParserInfoTestParserInfo.stateReferencesElementASymbol(s));
        assertFalse(ParserInfoTestParserInfo.stateReferencesElementBSymbol(s));
    }

    @Test
    public void testMultipleReferencesA() {
        List<Integer> states = IntStream.range(0, MAX_STATE_NUMBER)
                .filter(ParserInfoTestParserInfo::stateHasUsageNameRefA)
                .boxed()
                .toList();

        assertEquals(1, states.size());

        int s = states.getFirst();
        assertTrue(ParserInfoTestParserInfo.stateReferencesElementASymbol(s));
        assertFalse(ParserInfoTestParserInfo.stateReferencesElementBSymbol(s));
    }

    @Test
    public void testMultipleReferencesB() {
        List<Integer> states = IntStream.range(0, MAX_STATE_NUMBER)
                .filter(ParserInfoTestParserInfo::stateHasUsageNameRefB)
                .boxed()
                .toList();

        assertEquals(1, states.size());

        int s = states.getFirst();
        assertFalse(ParserInfoTestParserInfo.stateReferencesElementASymbol(s));
        assertTrue(ParserInfoTestParserInfo.stateReferencesElementBSymbol(s));
    }

    @Test
    public void testSimpleListRef() {
        List<Integer> states = IntStream.range(0, MAX_STATE_NUMBER)
                .filter(ParserInfoTestParserInfo::stateHasUsageNameUsageNameForList1)
                .boxed()
                .toList();

        // '(a || ",")+' is replaced with 'a ("," a)*'
        assertEquals(2, states.size());

        for (Integer s : states) {
            assertTrue(ParserInfoTestParserInfo.stateReferencesElementASymbol(s));
            assertFalse(ParserInfoTestParserInfo.stateReferencesElementBSymbol(s));
        }
    }

    @Test
    public void testAltSimpleListRef() {
        List<Integer> states = IntStream.range(0, MAX_STATE_NUMBER)
                .filter(ParserInfoTestParserInfo::stateHasUsageNameUsageNameForList2)
                .boxed()
                .toList();

        assertEquals(2, states.size());

        for (Integer s : states) {
            assertTrue(ParserInfoTestParserInfo.stateReferencesElementASymbol(s));
            assertFalse(ParserInfoTestParserInfo.stateReferencesElementBSymbol(s));
        }
    }

    @Test
    public void testStaticDelegatePattern(){
        {
            List<Integer> states = IntStream.range(0, MAX_STATE_NUMBER)
                    .filter(ParserInfoTestParserInfo::stateReferencesElementASymbol)
                    .boxed()
                    .toList();

            assertFalse(states.isEmpty());
        }

        ParserInfoTestParserInfo.initMe(new ParserInfoTestParserInfo(){
            @Override
            protected boolean _stateReferencesElementASymbol(int state) {
                return false;
            }
        });

        {
            List<Integer> states = IntStream.range(0, MAX_STATE_NUMBER)
                    .filter(ParserInfoTestParserInfo::stateReferencesElementASymbol)
                    .boxed()
                    .toList();

            assertTrue(states.isEmpty());
        }
    }

    @Test
    public void testIsDefiningName(){
        List<Integer> states = IntStream.range(0, MAX_STATE_NUMBER)
            .filter(ParserInfoTestParserInfo::stateDefinesName)
            .boxed()
            .toList();

        // ElementA to D are symbols and therefore define names
        assertEquals(4, states.size());

        // All states need to have the usage name "name", otherwise they are not defining the symbols' name
        states.forEach(state -> {
            assertTrue(ParserInfoTestParserInfo.stateHasUsageNameName(state));
            assertFalse(ParserInfoTestParserInfo.stateHasUsageNameRefA(state));
            assertFalse(ParserInfoTestParserInfo.stateHasUsageNameRefB(state));
        });

        // All states can not reference any kind
        states.forEach(state -> {
            assertFalse(ParserInfoTestParserInfo.stateReferencesElementASymbol(state));
            assertFalse(ParserInfoTestParserInfo.stateReferencesElementBSymbol(state));
        });
    }

}
