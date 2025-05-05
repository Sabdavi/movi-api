package com.backbase;

import com.backbase.entity.MovieAward;
import com.backbase.exception.CsvProcessingException;
import com.backbase.repository.MovieAwardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class CsvLoaderTest {

    private CsvLoader csvLoader;
    private MovieAwardRepository awardRepository;

    @BeforeEach
    void setup() {
        awardRepository = mock(MovieAwardRepository.class);
        csvLoader = new CsvLoader(awardRepository);

        Resource resource = new ClassPathResource("academy_awards.csv");
        ReflectionTestUtils.setField(csvLoader, "csvResource", resource);
    }

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

    @Test
    void shouldSaveOnlyNewTitlesToRepository() {
        List<String> csvTitles = List.of("Movie A", "Movie B");
        List<String> existingTitles = List.of("movie a");

        when(awardRepository.findAllTitlesLowerCase()).thenReturn(existingTitles);

        ReflectionTestUtils.invokeMethod(csvLoader, "saveCsv", csvTitles);

        verify(awardRepository).saveAll(argThat(awards -> {
            List<MovieAward> list = StreamSupport.stream(awards.spliterator(), false).toList();
            return list.size() == 1 && list.get(0).getTitle().equals("Movie B");
        }));
    }

    @Test
    void shouldThrowException_whenCsvIsUnreadable() {
        Resource faultyResource = mock(Resource.class);
        try {
            when(faultyResource.getInputStream()).thenThrow(new RuntimeException("Read failed"));
        } catch (Exception e) {
        }

        ReflectionTestUtils.setField(csvLoader, "csvResource", faultyResource);

        CsvProcessingException exception = assertThrows(
                CsvProcessingException.class,
                () -> ReflectionTestUtils.invokeMethod(csvLoader, "readCsv")
        );

        assertTrue(exception.getMessage().contains("Error occurred in reading"));
    }
}
