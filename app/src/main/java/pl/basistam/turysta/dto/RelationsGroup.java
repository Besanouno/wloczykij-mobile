package pl.basistam.turysta.dto;


import java.util.ArrayList;
import java.util.List;

public class RelationsGroup {
    private String name;
    private List<RelationItem> children;

    public RelationsGroup(String name) {
        this.name = name;
        this.children = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RelationItem> getChildren() {
        return children;
    }

    public void setChildren(List<RelationItem> children) {
        this.children = children;
    }
}
