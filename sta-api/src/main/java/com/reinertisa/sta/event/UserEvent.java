package com.reinertisa.sta.event;

import com.reinertisa.sta.entity.UserEntity;
import com.reinertisa.sta.enumaration.EventType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class UserEvent {
    private UserEntity user;
    private EventType type;
    private Map<?, ?> data;
}
