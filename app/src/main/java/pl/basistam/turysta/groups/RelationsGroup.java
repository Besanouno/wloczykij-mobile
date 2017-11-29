package pl.basistam.turysta.groups;

import java.util.ArrayList;
import java.util.List;

public class RelationsGroup<T> {
    private String name;
    private List<T> children;

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

    public List<T> getChildren() {
        return children;
    }

    public void setChildren(List<T> children) {
        this.children = children;
    }
}
