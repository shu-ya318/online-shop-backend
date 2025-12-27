package com.project.demo.converter;

import com.project.demo.enumeration.Modifier;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;

@Component
@Converter(autoApply = true)
public class ModifierConverter implements AttributeConverter<Modifier, String> {

    @Override
    public String convertToDatabaseColumn(Modifier modifier) {
        if (modifier == null) {
            return null;
        }
    
        return modifier.name().toLowerCase();
    }

    @Override
    public Modifier convertToEntityAttribute(String code) {
        if (code == null) {
            return null;
        }

        return Stream.of(Modifier.values())
                .filter(c -> c.name().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
