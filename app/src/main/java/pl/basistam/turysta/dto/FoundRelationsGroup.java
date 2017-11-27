package pl.basistam.turysta.dto;

public class FoundRelationsGroup extends RelationsGroup {

    private int totalNumber = 0;
    private int lastPage = 0;

    public FoundRelationsGroup(String name) {
        super(name);
    }

    public int getTotalNumber() {
        return totalNumber;
    }

    public void setTotalNumber(int totalNumber) {
        this.totalNumber = totalNumber;
    }

    public int getAndIncrementLastPage() {
        return ++lastPage;
    }

    public void setLastPage(int lastPage) {
        this.lastPage = lastPage;
    }

    public boolean canDownloadMore() {
        return totalNumber >= 15 && getChildren().size() < totalNumber;
    }
}
