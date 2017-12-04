package pl.basistam.turysta.map;

import java.io.Serializable;
import java.util.List;

public class Route implements Serializable {
    private List<Integer> trailIds;

    public Route(List<Integer> trailIds) {
        this.trailIds = trailIds;
    }

    public List<Integer> getTrailIds() {
        return trailIds;
    }

    public void setTrailIds(List<Integer> trailIds) {
        this.trailIds = trailIds;
    }
}
