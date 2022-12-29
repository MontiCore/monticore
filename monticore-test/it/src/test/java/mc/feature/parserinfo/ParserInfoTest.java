/* (c) https://github.com/MontiCore/monticore */
package mc.feature.parserinfo;

import de.se_rwth.commons.logging.LogStub;
import mc.feature.parserinfo.parserinfosimpleinheritancetest._parser._auxiliary.ParserInfoSimpleInheritanceTestParserInfoForParserInfoTest;
import mc.feature.parserinfo.parserinfotest._parser.ParserInfoTestParserInfo;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;
import de.se_rwth.commons.logging.Log;

/**
 * Test the generated ParserInfo classes.
 * Since the concrete antlr state numbers are not stable, we must always check a range of state numbers.
 */
@RunWith(Parameterized.class)
public class ParserInfoTest {
    private boolean useSimpleInheritance;

    @Parameterized.Parameters
    public static Collection<Object[]> data(){
        return Arrays.asList(new Boolean[]{false}, new Boolean[]{true});
    }

    public ParserInfoTest(boolean useSimpleInheritance){
        this.useSimpleInheritance = useSimpleInheritance;
    }

    // The generated parser has around 125 states
    // => add some safety margin
    private final int MAX_STATE_NUMBER = 250;

    @Before
    public void init(){
        if(useSimpleInheritance){
            ParserInfoTestParserInfo.initMe(new ParserInfoSimpleInheritanceTestParserInfoForParserInfoTest());
        }else{
            ParserInfoTestParserInfo.init();
        }
    }
    
    @Before
    public void before() {
        LogStub.init();
        Log.enableFailQuick(false);
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
        assertTrue(Log.getFindings().isEmpty());
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
        assertTrue(Log.getFindings().isEmpty());
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
        assertTrue(Log.getFindings().isEmpty());
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
        assertTrue(Log.getFindings().isEmpty());
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
        assertTrue(Log.getFindings().isEmpty());
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
        assertTrue(Log.getFindings().isEmpty());
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
        assertTrue(Log.getFindings().isEmpty());
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
        assertTrue(Log.getFindings().isEmpty());
    }

}
