package com.example.kvitter.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


public record KvitterRequest(String message,List<String> hashtags ,Boolean isPrivate) {
   
}
