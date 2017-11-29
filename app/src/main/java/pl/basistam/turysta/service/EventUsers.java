package pl.basistam.turysta.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import pl.basistam.turysta.dto.EventUserDto;
import pl.basistam.turysta.enums.EventUserStatus;

public class EventUsers implements Serializable {

    private List<EventUserDtoWithPreviousState> participants = new ArrayList<>();


    private class EventUserDtoWithPreviousState {
        private EventUserDto eventUserDto;
        private String previousStatus;

        EventUserDtoWithPreviousState(EventUserDto eventUserDto) {
            this.eventUserDto = eventUserDto;
            this.previousStatus = eventUserDto.getStatus();
        }
    }

    public EventUsers(List<EventUserDto> participants) {
        for (EventUserDto e: participants) {
            this.participants.add(new EventUserDtoWithPreviousState(e));
        }
    }

    public void registerChange(EventUserDto eventUser) {
        for (EventUserDtoWithPreviousState e: participants) {
            if (eventUser.getLogin().equals(e.eventUserDto.getLogin())) {
                changePresentUserStatus(e);
                return;
            }
        }
        registerNewUser(eventUser);
    }

    private void changePresentUserStatus(EventUserDtoWithPreviousState e) {
        String status = e.eventUserDto.getStatus();
        if (EventUserStatus.NONE.name().equals(status)) {
            e.eventUserDto.setStatus(e.previousStatus);
        } else if (EventUserStatus.PARTICIPANT.name().equals(status)
                || EventUserStatus.INVITED.name().equals(status)) {
            e.eventUserDto.setStatus(EventUserStatus.NONE.name());
        } else if (EventUserStatus.WAITING.name().equals(status)) {
            e.eventUserDto.setStatus(EventUserStatus.PARTICIPANT.name());
        }
    }

    private void registerNewUser(EventUserDto e) {
        e.setStatus(EventUserStatus.INVITED.name());
        participants.add(new EventUserDtoWithPreviousState(e));
    }

    public List<EventUserDto> getParticipants() {
        List<EventUserDto> result = new ArrayList<>(participants.size());
        for (EventUserDtoWithPreviousState e: participants) {
            result.add(e.eventUserDto);
        }
        return result;    }

}
