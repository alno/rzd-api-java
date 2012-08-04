package name.alno.rzd.api;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class StationSearchService {

	private static final String BASE_URL = "http://pass.rzd.ru/suggester?lang=ru&stationNamePart=";

	private final Map<String, Set<Station>> cache = new HashMap<String, Set<Station>>();

	private final HttpClient http;

	public StationSearchService( HttpClient http ) {
		this.http = http;
	}

	public List<Station> findStations( String prefix ) throws ApiException {
		if ( prefix.length() < 2 )
			return Collections.emptyList();

		prefix = prefix.toUpperCase();

		try {
			Set<Station> fromServer = findStationsOnServer( prefix.substring( 0, 2 ) );
			List<Station> results = new ArrayList<Station>();

			for ( Station st : fromServer )
				if ( st.name.startsWith( prefix ) )
					results.add( st );

			return results;
		} catch ( ClientProtocolException e ) {
			throw new ApiException( "Error fetching stations due to protocol error", e );
		} catch ( IOException e ) {
			throw new ApiException( "Error fetching stations due to IO error", e );
		} catch ( JSONException e ) {
			throw new ApiException( "Error fetching stations due to parse error", e );
		}
	}

	protected Set<Station> findStationsOnServer( String prefix ) throws IOException, ClientProtocolException, JSONException {
		if ( cache.containsKey( prefix ) )
			return cache.get( prefix );

		HttpResponse res = http.execute( new HttpGet( BASE_URL + URLEncoder.encode( prefix, "UTF-8" ) ) );

		String data = EntityUtils.toString( res.getEntity() );

		JSONArray arr = new JSONArray( data );
		Set<Station> results = new TreeSet<Station>();

		for ( int i = 0, l = arr.length(); i < l; ++i ) {
			JSONObject obj = arr.getJSONObject( i );

			results.add( new Station( obj.getString( "name" ), obj.getLong( "id" ) ) );
		}

		cache.put( prefix, results );

		return results;
	}

	public static void main( String[] args ) throws ApiException {
		System.out.println( new StationSearchService( new DefaultHttpClient() ).findStations( "ка" ) );
		System.out.println( new StationSearchService( new DefaultHttpClient() ).findStations( "калуга" ) );
		System.out.println( new StationSearchService( new DefaultHttpClient() ).findStations( "москва киевск" ) );
	}

}
