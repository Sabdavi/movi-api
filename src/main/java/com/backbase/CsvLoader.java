package com.backbase;

import com.backbase.entity.MovieAward;
import com.backbase.exception.CsvProcessingException;
import com.backbase.repository.MovieAwardRepository;
import jakarta.annotation.PostConstruct;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class CsvLoader {

    private static final Logger logger = LogManager.getLogger(CsvLoader.class);
    private static final String YEAR_FIELD = "Year";
    private static final String CATEGORY_FIELD = "Category";
    private static final String NOMINEE_FIELD = "Nominee";
    private static final String ADDITIONAL_INFO_FIELD = "AdditionalInfo";
    private static final String WON_FIELD = "Won";
    private static final String BEST_PICTURE_CATEGORY_NAME = "Best Picture";
    private static final String BEST_PICTURE_WIN_VALUE = "YES";

    private final MovieAwardRepository awardRepository;

    @Value("classpath:academy_awards.csv")
    private Resource csvResource;

    public CsvLoader(MovieAwardRepository awardRepository) {
        this.awardRepository = awardRepository;
    }

    private List<String> readCsv() {
        List<String> awardWinningTitles = new ArrayList<>();
        CSVFormat csvFormat = CSVFormat.Builder.
                create()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setHeader(YEAR_FIELD, CATEGORY_FIELD, NOMINEE_FIELD, ADDITIONAL_INFO_FIELD, WON_FIELD)
                .get();
        try {
            CSVParser parser = CSVParser.parse(csvResource.getInputStream(), StandardCharsets.UTF_8, csvFormat);
            for (CSVRecord csvRecord : parser) {
                String category = csvRecord.get(CATEGORY_FIELD);
                String nominee = csvRecord.get(NOMINEE_FIELD);
                String won = csvRecord.get(WON_FIELD);
                if (wonBestPicture(category, won)) {
                    awardWinningTitles.add(nominee);
                }
            }
        } catch (Exception e) {
            String message = "Error occurred in reading academy_awards.csv file";
            logger.error(message, e);
            throw new CsvProcessingException(message, e);
        }
        return awardWinningTitles;
    }

    private boolean wonBestPicture(String category, String won) {
        return BEST_PICTURE_CATEGORY_NAME.equalsIgnoreCase(category) && BEST_PICTURE_WIN_VALUE.equalsIgnoreCase(won);
    }

    private void saveCsv(List<String> awardWinningTitles) {
        List<String> existingAwards = awardRepository.findAllTitlesLowerCase();
        List<MovieAward> awards = awardWinningTitles.stream()
                .filter(award -> !existingAwards.contains(award.toLowerCase()))
                .map(title -> MovieAward.builder().title(title).build())
                .toList();
        awardRepository.saveAll(awards);
        logger.info("Number of {} records added to database", awards.size());
    }

    @PostConstruct
    void initializeDatabase() {
        List<String> awardWinningTitles = readCsv();
        saveCsv(awardWinningTitles);
    }
}
