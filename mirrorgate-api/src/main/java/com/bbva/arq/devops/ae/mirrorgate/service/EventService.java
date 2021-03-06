package com.bbva.arq.devops.ae.mirrorgate.service;

import com.bbva.arq.devops.ae.mirrorgate.model.BaseModel;
import com.bbva.arq.devops.ae.mirrorgate.model.Event;
import com.bbva.arq.devops.ae.mirrorgate.model.EventType;

import java.util.List;

public interface EventService {

    void saveEvent(BaseModel model, EventType type);

    void saveEvents(Iterable<? extends BaseModel> models, EventType type);

    Event getLastEvent();

    List<Event> getEventsSinceTimestamp(Long timestamp);

}
