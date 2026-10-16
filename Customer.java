public class Customer {
    private String name;
    private int partySize;

    public Customer(String name, int partySize) {
        this.name = name;
        this.partySize = partySize;
    }

    public String getName() {
        return name;
    }

    public int getPartySize() {
        return partySize;
    }

    @Override
    public String toString() {
        return name + " (Party of " + partySize + ")";
    }
}
