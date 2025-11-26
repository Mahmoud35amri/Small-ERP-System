package com.minierp.util;

import com.minierp.model.Identifiable;
import java.util.Collection;

public class IdGenerator {
    public static int generate(Collection<? extends Identifiable> items) {
        return items.stream()
                .mapToInt(Identifiable::getId)
                .max()
                .orElse(0) + 1;
    }
}
