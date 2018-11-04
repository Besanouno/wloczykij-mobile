package pl.basistam.wloczykij.dto;

public class FoundPeopleGroup extends Group {

    private int totalNumber = 0;
    private int lastPage = 0;

    public FoundPeopleGroup(String name) {
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
}
