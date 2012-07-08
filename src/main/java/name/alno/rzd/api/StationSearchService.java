package name.alno.rzd.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

	private final HttpClient http;

	public StationSearchService( HttpClient http ) {
		this.http = http;
	}

	public List<Station> findStations( String prefix ) throws ClientProtocolException, IOException, JSONException {
		HttpResponse res = http.execute( new HttpGet( BASE_URL + prefix ) );

		String data = EntityUtils.toString( res.getEntity() );

		JSONArray arr = new JSONArray( data );
		ArrayList<Station> results = new ArrayList<Station>();

		for ( int i = 0, l = arr.length(); i < l; ++i ) {
			JSONObject obj = arr.getJSONObject( i );

			results.add( new Station( obj.getString( "name" ), obj.getLong( "id" ) ) );
		}

		return results;
	}

	public static void main( String[] args ) throws ClientProtocolException, IOException, JSONException {
		System.out.println( new StationSearchService( new DefaultHttpClient() ).findStations( "ка" ) );
	}

}
