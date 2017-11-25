package pl.basistam.turysta.dto;

import java.util.List;

public class EventFullDto extends EventDto {
    List<UserItem> participants;
    List<UserItem> invited;

    public List<UserItem> getParticipants() {
        return participants;
    }

    public void setParticipants(List<UserItem> participants) {
        this.participants = participants;
    }

    public List<UserItem> getInvited() {
        return invited;
    }

    public void setInvited(List<UserItem> invited) {
        this.invited = invited;
    }
}
