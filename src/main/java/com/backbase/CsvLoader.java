package com.backbase;

import com.backbase.dto.BestPictureWinnerDto;
import com.backbase.entity.BestPictureWinner;
import com.backbase.repository.BestPictureWinnerRepository;
import jakarta.annotation.PostConstruct;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.*;
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

    private final BestPictureWinnerRepository awardRepository;

    @Value("classpath:academy_awards.csv")
    private Resource csvResource;

    public CsvLoader(BestPictureWinnerRepository awardRepository) {
        this.awardRepository = awardRepository;
    }

    private List<BestPictureWinnerDto> readCsv() {
        List<BestPictureWinnerDto> awardDtos = new ArrayList<>();
        CSVFormat csvFormat = CSVFormat.Builder.create().setHeader().setSkipHeaderRecord(true).setHeader(YEAR_FIELD, CATEGORY_FIELD, NOMINEE_FIELD,ADDITIONAL_INFO_FIELD,WON_FIELD).get();
        try {
            CSVParser parser = CSVParser.parse(csvResource.getInputStream(), StandardCharsets.UTF_8, csvFormat);
            for(CSVRecord csvRecord : parser) {
               String category = csvRecord.get(CATEGORY_FIELD);
               String nominee = csvRecord.get(NOMINEE_FIELD);
               String won = csvRecord.get(WON_FIELD);
               if(wonBestPicture(category, won)) {
                   BestPictureWinnerDto awardDto = new BestPictureWinnerDto(nominee);
                   awardDtos.add(awardDto);
               }
            }
        } catch (IOException e) {
            logger.error("Error accorded in reading academy_awards.csv file", e);
            throw new RuntimeException(e);
        }
        return awardDtos;
    }

    private boolean wonBestPicture(String category, String won) {
        return BEST_PICTURE_CATEGORY_NAME.equalsIgnoreCase(category) && BEST_PICTURE_WIN_VALUE.equalsIgnoreCase(won);
    }

    private void saveCsv(List<BestPictureWinnerDto> awardDtos) {
        List<String> existingAwards = awardRepository
                .findAll()
                .stream()
                .map(award -> award.getTitle().toLowerCase())
                .toList();
        List<BestPictureWinner> awards = awardDtos.stream()
                .filter(award -> ! existingAwards.contains(award.title().toLowerCase()))
                .map(awardDto -> new BestPictureWinner(awardDto.title()))
                .toList();
        awardRepository.saveAll(awards);
    }

    @PostConstruct
    void initializeDatabase() {
        List<BestPictureWinnerDto> awardDtos = readCsv();
        saveCsv(awardDtos);
        logger.info("Number of {} records added to database", awardDtos.size());
    }
}
