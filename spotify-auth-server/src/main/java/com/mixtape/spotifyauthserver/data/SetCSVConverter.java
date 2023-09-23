package com.mixtape.spotifyauthserver.data;

import jakarta.persistence.AttributeConverter;
import org.apache.logging.log4j.util.Strings;

import java.util.HashSet;
import java.util.Set;

public class SetCSVConverter implements AttributeConverter<Set<String>, String> {
    @Override
    public String convertToDatabaseColumn(Set<String> attribute) {
        return Strings.join(attribute, ',');
    }

    @Override
    public Set<String> convertToEntityAttribute(String dbData) {
        return new HashSet<>(Set.of(dbData.split(",")));
    }
}
