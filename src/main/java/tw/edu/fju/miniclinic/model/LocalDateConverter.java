package tw.edu.fju.miniclinic.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.LocalDate;

@Converter
public class LocalDateConverter implements AttributeConverter<LocalDate, String> {

    @Override
    public String convertToDatabaseColumn(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.toString();
    }

    @Override
    public LocalDate convertToEntityAttribute(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        return LocalDate.parse(s);
    }
}