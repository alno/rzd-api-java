package name.alno.rzd.api;

public class Station {

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
		return name + "/" + id;
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

}
