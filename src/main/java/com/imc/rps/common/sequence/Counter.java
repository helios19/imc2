package com.imc.rps.common.sequence;

import com.imc.rps.common.service.CounterServiceImpl;
import com.imc.rps.common.utils.ClassUtils;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Counter document class holding up the sequence value of a given collection.
 *
 * @see CounterServiceImpl
 */
@Data
@Builder
@Document(collection = ClassUtils.COUNTERS_COLLECTION_NAME)
public class Counter {
    @Id
    private String id;

    @Field
    private int seq;
}