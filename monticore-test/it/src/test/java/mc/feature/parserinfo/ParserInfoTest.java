package mc.feature.parserinfo;

import mc.feature.parserinfo.parserinfotest._parser.ParserInfoTestParserInfo;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

/**
 * Test the generated ParserInfo classes.
 * Since the concrete antlr state numbers are not stable, we must always check a range of state numbers.
 */
public class ParserInfoTest {
    // The generated parser has around 125 states
    // => add some safety margin
    private final int MAX_STATE_NUMBER = 250;

    @Before
    public void init(){
        ParserInfoTestParserInfo.init();
    }

    @Test
    public void testNoRef() {
        List<Integer> states = IntStream.range(0, MAX_STATE_NUMBER)
                .filter(ParserInfoTestParserInfo::stateHasUsageNameUsageName1)
                .boxed()
                .collect(Collectors.toList());

        assertEquals(1, states.size());

        int s = states.get(0);
        assertFalse(ParserInfoTestParserInfo.stateReferencesElementASymbol(s));
        assertFalse(ParserInfoTestParserInfo.stateReferencesElementBSymbol(s));
    }

    @Test
    public void testSimpleReference() {
        List<Integer> states = IntStream.range(0, MAX_STATE_NUMBER)
                .filter(ParserInfoTestParserInfo::stateHasUsageNameUsageName2)
                .boxed()
                .collect(Collectors.toList());

        assertEquals(1, states.size());

        int s = states.get(0);
        assertTrue(ParserInfoTestParserInfo.stateReferencesElementASymbol(s));
        assertFalse(ParserInfoTestParserInfo.stateReferencesElementBSymbol(s));
    }

    @Test
    public void testMultipleReferencesA() {
        List<Integer> states = IntStream.range(0, MAX_STATE_NUMBER)
                .filter(ParserInfoTestParserInfo::stateHasUsageNameRefA)
                .boxed()
                .collect(Collectors.toList());

        assertEquals(1, states.size());

        int s = states.get(0);
        assertTrue(ParserInfoTestParserInfo.stateReferencesElementASymbol(s));
        assertFalse(ParserInfoTestParserInfo.stateReferencesElementBSymbol(s));
    }

    @Test
    public void testMultipleReferencesB() {
        List<Integer> states = IntStream.range(0, MAX_STATE_NUMBER)
                .filter(ParserInfoTestParserInfo::stateHasUsageNameRefB)
                .boxed()
                .collect(Collectors.toList());

        assertEquals(1, states.size());

        int s = states.get(0);
        assertFalse(ParserInfoTestParserInfo.stateReferencesElementASymbol(s));
        assertTrue(ParserInfoTestParserInfo.stateReferencesElementBSymbol(s));
    }

    @Test
    public void testSimpleListRef() {
        List<Integer> states = IntStream.range(0, MAX_STATE_NUMBER)
                .filter(ParserInfoTestParserInfo::stateHasUsageNameUsageNameForList1)
                .boxed()
                .collect(Collectors.toList());

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
                .collect(Collectors.toList());

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
                    .collect(Collectors.toList());

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
                    .collect(Collectors.toList());

            assertTrue(states.isEmpty());
        }
    }

    @Test
    public void testIsDefiningName(){
        List<Integer> states = IntStream.range(0, MAX_STATE_NUMBER)
            .filter(ParserInfoTestParserInfo::stateDefinesName)
            .boxed()
            .collect(Collectors.toList());

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
