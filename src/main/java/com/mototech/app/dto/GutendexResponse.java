package com.mototech.app.dto;

import lombok.Data;

import java.util.List;

@Data
public class GutendexResponse {


    private long count;

    private String next;

    private Object previous;

    private List<BookDto> results;

}
