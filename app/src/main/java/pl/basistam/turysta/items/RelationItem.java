package pl.basistam.turysta.items;

public class RelationItem {
    private String name;
    private String login;
    private boolean related;

    public RelationItem(String name, String login, boolean related) {
        this.name = name;
        this.login = login;
        this.related = related;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }
    public void setLogin(String login) {
        this.login = login;
    }

    public boolean isRelated() {
        return related;
    }
    public void setRelated(boolean related) {
        this.related = related;
    }

    public String getFullName() {
        return this.name + " (" + this.login + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RelationItem)) {
            return false;
        }
        RelationItem another = (RelationItem) obj;
        return login.equals(another.login);
    }
}
