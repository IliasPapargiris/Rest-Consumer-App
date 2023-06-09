package com.mototech.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AuthorDto {

    private String name;
    @JsonProperty("birth_year")
    private Integer birthYear;
    @JsonProperty("death_year")
    private Integer deathYear;
}
