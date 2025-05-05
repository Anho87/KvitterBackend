package com.example.kvitter.dtos;

import java.util.List;


public record KvitterRequest(String message,List<String> hashtags ,Boolean isPrivate) {
   
}
