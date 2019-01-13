package pl.basistam.wloczykij.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import pl.basistam.wloczykij.items.EventUserItem;
import pl.basistam.wloczykij.enums.EventUserStatus;

public class EventUsers implements Serializable {

    private List<EventUserDtoWithPreviousState> participants = new ArrayList<>();


    private class EventUserDtoWithPreviousState {
        private EventUserItem eventUserItem;
        private String previousStatus;

        EventUserDtoWithPreviousState(EventUserItem eventUserItem) {
            this.eventUserItem = eventUserItem;
            this.previousStatus = eventUserItem.getStatus();
        }
    }

    public EventUsers(List<EventUserItem> participants) {
        for (EventUserItem e: participants) {
            this.participants.add(new EventUserDtoWithPreviousState(e));
        }
    }

    public void registerChange(EventUserItem eventUser) {
        for (EventUserDtoWithPreviousState e: participants) {
            if (eventUser.getLogin().equals(e.eventUserItem.getLogin())) {
                changePresentUserStatus(e);
                return;
            }
        }
        registerNewUser(eventUser);
    }

    private void changePresentUserStatus(EventUserDtoWithPreviousState e) {
        String status = e.eventUserItem.getStatus();
        if (EventUserStatus.NONE.name().equals(status)) {
            if (EventUserStatus.NONE.name().equals(e.previousStatus)) {
                e.eventUserItem.setStatus(EventUserStatus.INVITED.name());
            } else {
                e.eventUserItem.setStatus(e.previousStatus);
            }
        } else if (EventUserStatus.PARTICIPANT.name().equals(status)
                || EventUserStatus.INVITED.name().equals(status)) {
            e.eventUserItem.setStatus(EventUserStatus.NONE.name());
        } else if (EventUserStatus.WAITING.name().equals(status)) {
            e.eventUserItem.setStatus(EventUserStatus.PARTICIPANT.name());
        }
    }

    private void registerNewUser(EventUserItem e) {
        e.setStatus(EventUserStatus.INVITED.name());
        participants.add(new EventUserDtoWithPreviousState(e));
    }

    public List<EventUserItem> getParticipants() {
        List<EventUserItem> result = new ArrayList<>(participants.size());
        for (EventUserDtoWithPreviousState e: participants) {
            result.add(e.eventUserItem);
        }
        return result;    }

}
