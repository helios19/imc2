package com.imc.rps.common.utils;

import com.google.common.base.Enums;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.imc.rps.game.dto.GameDto;
import com.imc.rps.game.dto.GameMultiPlayerDto;
import com.imc.rps.game.model.Game;
import com.imc.rps.game.model.GameMultiPlayer;
import com.imc.rps.game.model.GameSymbolEnum;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.ofPattern;

/**
 * Utils class providing convenient factory and helper methods for {@link Game} resources.
 */
public class ClassUtils {
    public static final String COUNTERS_COLLECTION_NAME = "counters";
    public static final String GAMES_COLLECTION_NAME = "games";
    public static final String GAME_MULTIPLAYERS_COLLECTION_NAME = "gameMultiPlayers";
    public static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter FORMATTER = ofPattern("d/MM/yyyy h:mm:ss a");
    public static final int DEFAULT_PAGE_SIZE = 50;
    public static final ModelMapper MODEL_MAPPER = new ModelMapper();

    private ClassUtils() {
    }

    /**
     * Validates game symbol.
     *
     * @param symbol to validate
     * @return Whether the symbol is valid
     */
    public static boolean isValidateSymbol(String symbol) {
        return Enums.getIfPresent(GameSymbolEnum.class, symbol).isPresent();
    }

    /**
     * Validates game symbol.
     *
     * @param symbols to validate
     * @return Whether the symbols are valid
     */
    public static boolean isInvalidateSymbols(List<String> symbols) {
        return symbols.stream().filter(s -> !isValidateSymbol(s)).findAny().isPresent();
    }

    /**
     * Converts {@code isoDate} argument to {@link Date}.
     *
     * @param isoDate character sequence to convert
     * @return Date instance
     */
    public static Date toDate(String isoDate) {
        return Date.from(
                LocalDateTime.parse(isoDate, FORMATTER)
                        .atZone(ZoneId.systemDefault())
                        .toInstant());
    }

    /**
     * Converts {@code localDate} argument to {@link Date}.
     *
     * @param localDateTime Local datetime to convert
     * @return Date instance
     */
    public static Date asDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Converts {@link Date} argument to {@code isoDate}.
     *
     * @param date Date to format
     * @return Date instance
     */
    public static String fromDate(Date date) {
        return FORMATTER.format(LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()));
    }

    /**
     * Converts a {@link Game} instance into a {@link GameDto} object.
     *
     * @param game Game to convert
     * @return GameDto
     */
    public static GameDto convertToDto(Game game) {
        GameDto gameDto = MODEL_MAPPER.map(game, GameDto.class);
        gameDto.setDate(fromDate(game.getDate()));
        return gameDto;
    }

    /**
     * Converts a {@link Game} instance into a {@link GameDto} object.
     *
     * @param game Game to convert
     * @return GameDto
     */
    public static GameMultiPlayerDto convertToDto(GameMultiPlayer game) {
        GameMultiPlayerDto gameDto = MODEL_MAPPER.map(game, GameMultiPlayerDto.class);
        gameDto.setDate(fromDate(game.getDate()));
        return gameDto;
    }

    /**
     * Returns a new UUID if {@code gameUUID} input parameter is null or empty.
     *
     * @param gameUUID Game UUID
     * @return New or existing game UUID
     */
    public static String generateUUID(String gameUUID) {
        return Strings.isNullOrEmpty(gameUUID) ? UUID.randomUUID().toString() : gameUUID;
    }

    /**
     * Generates random symbol.
     *
     * @return generated random symbol
     */
    public static GameSymbolEnum generateRandomSymbol() {
        Random rand = new Random();
        List<GameSymbolEnum> symbols = Lists.newArrayList(GameSymbolEnum.values());
        return symbols.get(rand.nextInt(symbols.size()));
    }

    /**
     * Converts a list of games to its Dto representation.
     *
     * @param games List of games to convert
     * @return List of GameDtos
     */
    public static List<GameDto> convertToGameDtos(List<Game> games) {
        return games.stream()
                .map(game -> ClassUtils.convertToDto(game))
                .collect(Collectors.toList());
    }

    /**
     * Converts a list of multiplayer games to its Dto representation.
     *
     * @param games List of multiplayer games to convert
     * @return List of GameDtos
     */
    public static List<GameMultiPlayerDto> convertToGameMultiPlayerDtos(List<GameMultiPlayer> games) {
        return games.stream()
                .map(game -> ClassUtils.convertToDto(game))
                .collect(Collectors.toList());
    }
}
