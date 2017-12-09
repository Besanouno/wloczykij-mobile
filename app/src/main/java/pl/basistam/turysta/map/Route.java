package pl.basistam.turysta.map;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Route implements Serializable {
    private List<Integer> trailIds = new ArrayList<>();

    public Route(List<Integer> trailIds) {
        setTrailIds(trailIds);
    }

    public List<Integer> getTrailIds() {
        return trailIds;
    }

    public void setTrailIds(List<Integer> trailIds) {
        if (trailIds != null)
            this.trailIds = trailIds;
    }
}
