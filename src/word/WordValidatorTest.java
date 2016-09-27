/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package word;

import java.io.IOException;

import org.junit.Test;

public class WordValidatorTest {

    @Test
    public void testWordValidator() throws IOException {
        final WordValidator validator = new WordValidator(5, "wordsforproblem.txt");
        validator.printResult();
    }
}
