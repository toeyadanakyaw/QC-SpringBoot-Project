package com.announce.AcknowledgeHub_SpringBoot.model;

import lombok.Data;

import java.util.List;

@Data
public class GroupCreationDto {

    private String groupName;
    private List<Integer> staffIds;
}
