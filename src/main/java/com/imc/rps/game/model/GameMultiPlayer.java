package com.imc.rps.game.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

import static com.imc.rps.common.utils.ClassUtils.DATE_FORMAT_PATTERN;

/**
 * Plain java class representing an game multiplayer resource.
 */
@Document
@Data
@Builder
public class GameMultiPlayer {
    @Id
    private String id;

    @Field
    @Indexed
    @NotNull
    private String uuid;

    @Field
    @Indexed
    @NotNull
    private List<String> players;

    @Field
    @Indexed
    @NotNull
    private String result;

    @Field
    @Indexed
    @NotNull
    @JsonFormat(pattern = DATE_FORMAT_PATTERN)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date date;
}
