package pl.basistam.turysta.dto;

public class FoundEventUsersGroup extends EventUsersGroup {

    private int totalNumber = 0;
    private int lastPage = 0;

    public FoundEventUsersGroup(String name) {
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
