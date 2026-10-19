package com.example.demo.mapper;

import com.example.demo.Entites.Message;
import com.example.demo.dto.MessageDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    MessageDTO toDto(Message message);

    Message toEntity(MessageDTO messageDTO);
}
