package name.alno.rzd.api;

public class Train implements Comparable<Train> {

	public Train( String num, String fromTime, String toTime ) {
		this.num = num;
		this.fromTime = fromTime;
		this.toTime = toTime;
	}

	public final String fromTime, toTime;

	public final String num;

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();

		b.append( "{ " );
		b.append( num );
		b.append( " / " );
		b.append( fromTime );
		b.append( " - " );
		b.append( toTime );
		b.append( " }" );

		return b.toString();
	}

	public int compareTo( Train o ) {
		int res = fromTime.compareTo( o.fromTime );

		if ( res == 0 )
			res = toTime.compareTo( o.toTime );

		if ( res == 0 )
			res = num.compareTo( o.num );

		return res;
	}

}
