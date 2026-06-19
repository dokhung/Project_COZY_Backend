package com.ohgiraffers.COZYbe.domain.task.entity;

import com.ohgiraffers.COZYbe.domain.task.enums.TaskStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class TaskStatusConverter implements AttributeConverter<TaskStatus, String> {

    @Override
    public String convertToDatabaseColumn(TaskStatus attribute) {
        return (attribute == null ? TaskStatus.TODO : attribute).name();
    }

    @Override
    public TaskStatus convertToEntityAttribute(String dbData) {
        return TaskStatus.fromStoredValue(dbData);
    }
}
