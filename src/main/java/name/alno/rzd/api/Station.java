package name.alno.rzd.api;

import java.io.Serializable;

public class Station implements Comparable<Station>, Serializable {

	private static final long serialVersionUID = 1L;

	public final String name;
	public final long id;

	public Station( String name, long id ) {
		if ( name == null )
			throw new IllegalArgumentException( "Name can't be null" );

		this.name = name;
		this.id = id;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		return 11 + (int) (id ^ (id >>> 32)) * 17 + name.hashCode();
	}

	@Override
	public boolean equals( Object obj ) {
		if ( this == obj )
			return true;
		if ( obj == null || getClass() != obj.getClass() )
			return false;

		Station other = (Station) obj;

		return other.id == id && other.name.equals( name );
	}

	public int compareTo( Station o ) {
		int r = name.compareToIgnoreCase( o.name );

		if ( r == 0 )
			r = Long.compare( id, o.id );

		return r;
	}

}
