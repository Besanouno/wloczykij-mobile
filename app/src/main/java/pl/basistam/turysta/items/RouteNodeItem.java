package pl.basistam.turysta.items;


public class RouteNodeItem {
    private String name;
    private Double heightDifference;
    private Integer time;

    public RouteNodeItem(String name) {
        this.name = name;
    }

    public RouteNodeItem(String name, Double heightDifference, Integer time) {
        this.name = name;
        this.heightDifference = heightDifference;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getHeightDifference() {
        return heightDifference;
    }

    public void setHeightDifference(Double heightDifference) {
        this.heightDifference = heightDifference;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public String getPrintableHeightDifference() {
        return Double.toString(Math.abs(heightDifference)) + "m";
    }

    public String getPrintableTime() {
        return Integer.toString(time / 60) + ":" + Integer.toString(time % 60) + "h ";
    }
}

