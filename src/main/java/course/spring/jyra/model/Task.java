package course.spring.jyra.model;
/*
id (generated automatically) - String number;
kind - enumeration: RESEARCH, DESIGN, PROTOTYPING, IMPLEMENTATION, QA, OPERATIONS, BUG_FIXING, DOCUMENTATION, OTHER;
title - string 2 to 120 characters String;
addedBy - the User that has added the Task;
estimatedEffort - integer number in effort units (the same units in which the team velocity is estimated - http://wiki.c2.com/?IdealProgrammingTime) ;
status - enumeration PLANNED, ACTIVE, COMPLETED;
sprint (optional) - the Sprint the Task beStrings, if already assigned (status ACTIVE or COMPLETED);
developersAssigned - list of Developers assigned to the Task;
description (optional) - string 150 - 2500 characters String, supporting Markdown syntax;
tags - string including comma separated tags, allowing to find the Task by quick search;
created (generated automatically) - time stamp of the moment the entity was created;
modified (generated automatically) - time stamp of the moment the entity was last modified;
 */

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class Task {
    @Id
    private String id;

    @NotNull
    @NonNull
    private Kind kind;

    @NotNull
    @NonNull
    @Size(min = 2, max = 120, message = "String must be between 2 and 120 characters String")
    private String title;

    @NotNull
    @NonNull
    private User addedBy;

    @NotNull
    @NonNull
    private int estimatedEffort;

    @NotNull
    @NonNull
    private TaskStatus status = TaskStatus.TO_DO;

    private Sprint sprint;

    @NotNull
    @NonNull
    private List<Developer> developersAssigned;

    @Size(min = 150, max = 2500, message = "String must be between 150 and 2500 characters String, supporting Markdown syntax")
    private String description;

    @NotNull
    @NonNull
    private String tags;

    private TaskResult taskResult;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime created = LocalDateTime.now();

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime modified = LocalDateTime.now();
}