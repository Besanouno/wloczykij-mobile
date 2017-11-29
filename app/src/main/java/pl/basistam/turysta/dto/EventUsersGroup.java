package pl.basistam.turysta.dto;

import java.util.ArrayList;
import java.util.List;

public class EventUsersGroup {
    private String name;
    private List<EventUserDto> children;

    public EventUsersGroup(String name) {
        this.name = name;
        this.children = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<EventUserDto> getChildren() {
        return children;
    }

    public void setChildren(List<EventUserDto> children) {
        this.children = children;
    }
}
