package name.alno.rzd.api;

public class Train implements Comparable<Train> {

	public Train( String num, String source, String destination, String departure, String arrival ) {
		this.num = num;
		this.source = source;
		this.destination = destination;
		this.departure = departure;
		this.arrival = arrival;
	}

	public final String source, destination;
	public final String departure, arrival;

	public final String num;

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();

		b.append( "{ " );
		b.append( num );
		b.append( ' ' );
		b.append( source );
		b.append( " - " );
		b.append( destination );
		b.append( " / " );
		b.append( departure );
		b.append( " - " );
		b.append( arrival );
		b.append( " }" );

		return b.toString();
	}

	public int compareTo( Train o ) {
		int res = departure.compareTo( o.departure );

		if ( res == 0 )
			res = arrival.compareTo( o.arrival );

		if ( res == 0 )
			res = num.compareTo( o.num );

		return res;
	}

}
