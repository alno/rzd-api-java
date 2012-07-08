package name.alno.rzd.api;

public class TicketGroup {

	public final String type;

	public double price;

	public int count;

	public TicketGroup( String type, double price, int count ) {
		this.type = type;
		this.price = price;
		this.count = count;
	}

	@Override
	public String toString() {
		return "{ " + type + ", " + price + " руб, " + count + " }";
	}

}
