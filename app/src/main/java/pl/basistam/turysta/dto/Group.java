package pl.basistam.turysta.dto;


import java.util.ArrayList;
import java.util.List;

public class Group {
    private String name;
    private List<Relation> children;

    public Group(String name) {
        this.name = name;
        this.children = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Relation> getChildren() {
        return children;
    }

    public void setChildren(List<Relation> children) {
        this.children = children;
    }
}
