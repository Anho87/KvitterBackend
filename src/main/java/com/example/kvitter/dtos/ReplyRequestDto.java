package com.example.kvitter.dtos;

import java.util.UUID;

public record ReplyRequestDto(String message, UUID kvitterId, UUID parentReplyId) {
}
