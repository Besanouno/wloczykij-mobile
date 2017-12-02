package pl.basistam.turysta.dto;


import java.util.Date;
import java.util.List;

import pl.basistam.turysta.items.EventUserItem;

public class EventDto {
    private String name;
    private String description;
    private Integer participantsLimit;
    private String placeOfMeeting;
    private String initiator;
    private Date startDate;
    private Date endDate;
    private boolean publicAccess;
    private List<EventUserItem> eventUsers;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getParticipantsLimit() {
        return participantsLimit;
    }

    public void setParticipantsLimit(Integer participantsLimit) {
        this.participantsLimit = participantsLimit;
    }

    public String getPlaceOfMeeting() {
        return placeOfMeeting;
    }

    public void setPlaceOfMeeting(String placeOfMeeting) {
        this.placeOfMeeting = placeOfMeeting;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public boolean isPublicAccess() {
        return publicAccess;
    }

    public void setPublicAccess(boolean publicAccess) {
        this.publicAccess = publicAccess;
    }

    public String getInitiator() {
        return initiator;
    }

    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }

    public List<EventUserItem> getEventUsers() {
        return eventUsers;
    }

    public void setEventUsers(List<EventUserItem> eventUsers) {
        this.eventUsers = eventUsers;
    }
}
