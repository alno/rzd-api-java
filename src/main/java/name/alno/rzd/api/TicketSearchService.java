package name.alno.rzd.api;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TicketSearchService {

	private static final String BASE_URL = "http://pass.rzd.ru/pass/public/pass?STRUCTURE_ID=735&layer_id=5179&dir=0&tfl=3&checkSeats=1";

	private final HttpClient http;

	public TicketSearchService( HttpClient http ) {
		this.http = http;
	}

	public SortedMap<Train, List<TicketGroup>> findTickets( String date, Station from, Station to ) throws ApiException {
		return findTickets( date, from.name, from.id, to.name, to.id );
	}

	public SortedMap<Train, List<TicketGroup>> findTickets( String date, String fromName, long fromId, String toName, long toId ) throws ApiException {
		try {
			String requestUrl = getTicketSearchUrl( date, fromName, fromId, toName, toId );

			if ( requestUrl == null )
				return null;

			JSONObject data = waitForResult( requestUrl );

			return parseTrainsWithTickets( data.getJSONArray( "tp" ).getJSONObject( 0 ).getJSONArray( "list" ) );
		} catch ( ClientProtocolException e ) {
			throw new ApiException( "Error fetching tickets due to protocol error", e );
		} catch ( IOException e ) {
			throw new ApiException( "Error fetching tickets due to IO error", e );
		} catch ( JSONException e ) {
			throw new ApiException( "Error fetching tickets due to parse error", e );
		} catch ( InterruptedException e ) {
			throw new ApiException( "Error fetching tickets due to interruption", e );
		}
	}

	protected JSONObject waitForResult( String requestUrl ) throws InterruptedException, IOException, ClientProtocolException, JSONException {
		Thread.sleep( 500 );

		JSONObject data = new JSONObject( EntityUtils.toString( http.execute( new HttpGet( requestUrl ) ).getEntity() ) );

		while ( !data.getString( "result" ).equals( "OK" ) ) {
			Thread.sleep( 1000 );

			data = new JSONObject( EntityUtils.toString( http.execute( new HttpGet( requestUrl ) ).getEntity() ) );
		}

		return data;
	}

	protected String getTicketSearchUrl( String date, String fromName, long fromId, String toName, long toId ) throws IOException, ClientProtocolException, JSONException {
		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append( BASE_URL );
		urlBuilder.append( "&dt0=" );
		urlBuilder.append( URLEncoder.encode( date, "UTF-8" ) );
		urlBuilder.append( "&st0=" );
		urlBuilder.append( URLEncoder.encode( fromName, "UTF-8" ) );
		urlBuilder.append( "&code0=" );
		urlBuilder.append( fromId );
		urlBuilder.append( "&st1=" );
		urlBuilder.append( URLEncoder.encode( toName, "UTF-8" ) );
		urlBuilder.append( "&code1=" );
		urlBuilder.append( toId );

		HttpResponse headerResponse = http.execute( new HttpGet( urlBuilder.toString() ) );
		JSONObject header = new JSONObject( EntityUtils.toString( headerResponse.getEntity() ) );

		if ( !header.has( "rid" ) )
			return null;

		urlBuilder.append( "&rid=" );
		urlBuilder.append( header.getLong( "rid" ) );
		urlBuilder.append( "&SESSION_ID=" );
		urlBuilder.append( header.getLong( "SESSION_ID" ) );

		return urlBuilder.toString();
	}

	protected SortedMap<Train, List<TicketGroup>> parseTrainsWithTickets( JSONArray trainsJson ) throws JSONException {
		TreeMap<Train, List<TicketGroup>> trains = new TreeMap<Train, List<TicketGroup>>();

		for ( int i = 0, l = trainsJson.length(); i < l; ++i )
			if ( trainsJson.getJSONObject( i ).getJSONArray( "cars" ).length() > 0 )
				trains.put( parseTrain( trainsJson.getJSONObject( i ) ), parseTicketGroups( trainsJson.getJSONObject( i ).getJSONArray( "cars" ) ) );

		return trains;
	}

	protected Train parseTrain( JSONObject json ) throws JSONException {
		return new Train( json.getString( "number" ), json.getString( "route0" ), json.getString( "route1" ), json.getString( "time0" ), json.getString( "time1" ) );
	}

	protected List<TicketGroup> parseTicketGroups( JSONArray seatsJson ) throws JSONException {
		ArrayList<TicketGroup> seats = new ArrayList<TicketGroup>( seatsJson.length() );

		for ( int i = 0, l = seatsJson.length(); i < l; ++i )
			seats.add( parseTicketGroup( seatsJson.getJSONObject( i ) ) );

		return seats;
	}

	protected TicketGroup parseTicketGroup( JSONObject json ) throws JSONException {
		return new TicketGroup( json.getString( "type" ), json.getDouble( "tariff" ), json.getInt( "freeSeats" ) );
	}

	public static void main( String[] args ) throws ApiException {
		System.out.println( new TicketSearchService( new DefaultHttpClient() ).findTickets( "03.08.2012", new Station( "Москва", 2000123 ), new Station( "Калуга", 2000351 ) ) );
	}

}
