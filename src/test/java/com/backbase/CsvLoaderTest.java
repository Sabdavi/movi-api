package com.backbase;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CsvLoaderTest {

    @Autowired
    private CsvLoader csvLoader;

    @Test
    void shouldParseBestPictureWinnersFromCsv() throws Exception {

        Method readCsvMethod = CsvLoader.class.getDeclaredMethod("readCsv");
        readCsvMethod.setAccessible(true);

        List<String> result = (List<String>) readCsvMethod.invoke(csvLoader);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(83, result.size());
        assertTrue(result.stream().allMatch(awardWinnerTitle -> awardWinnerTitle != null && !awardWinnerTitle.isBlank()));
    }
}
