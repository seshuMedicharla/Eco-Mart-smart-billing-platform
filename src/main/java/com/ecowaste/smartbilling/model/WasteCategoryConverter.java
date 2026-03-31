package com.ecowaste.smartbilling.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class WasteCategoryConverter implements AttributeConverter<WasteCategory, String> {

    @Override
    public String convertToDatabaseColumn(WasteCategory attribute) {
        return attribute == null ? null : attribute.name();
    }

    @Override
    public WasteCategory convertToEntityAttribute(String dbData) {
        return WasteCategory.fromDatabaseValue(dbData);
    }
}
