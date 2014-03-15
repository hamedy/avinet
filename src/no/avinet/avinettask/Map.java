package no.avinet.avinettask;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.SphericalUtil;

public class Map extends Activity {
	
	private final LatLng ORIGIN = new LatLng(59.885938, 10.523377);
	private final int ZOOM_LEVEL = 25;
	private final double LENGTH = 0.297;
	private final double WIDTH = 0.21;
	private int SCALE = 1;
	private GoogleMap map;
	private Polygon polygon = null;
	private TextView textView;
	private TextView textView2;
	private ImageButton rotate_c;
	private ImageButton rotate_cc;
	private Context context;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.acticity_map);
	    
	    context = getApplicationContext();
	    map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
	    map.moveCamera( CameraUpdateFactory.newLatLngZoom(ORIGIN , ZOOM_LEVEL) );  
	    textView = (TextView) findViewById(R.id.offset_points);
	    textView2 = (TextView) findViewById(R.id.offset_points2);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.map_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.add_polygon:
	        	drawPolygon();
	            return true;
	        case R.id.remove_polygon:
	            map.clear();
	            rotate_c.setVisibility(View.GONE);
	    	    rotate_cc.setVisibility(View.GONE);
	    	    textView.setVisibility(View.INVISIBLE);
	    	    textView2.setVisibility(View.INVISIBLE);
	    	    polygon = null;
	            return true;
	        case R.id.enlarge:
	        	if (0<SCALE && SCALE<1001){
		        	Log.d("avinet", "enlarge");
	        		SCALE *= 10;
		        	map.clear();
		        	polygon = null;
		        	drawPolygon();
	        	}
	        	else{
	        		CharSequence text = "For bigger sizes buy the pro version!";
	        		int duration = Toast.LENGTH_SHORT;
	        		Toast toast = Toast.makeText(context, text, duration);
	        		toast.show();
	        	}
	        	return true;
	        case R.id.shrink:
	        	if (SCALE>9){
		        	SCALE = SCALE/10;
		        	map.clear();
		        	polygon = null;
		        	drawPolygon();
	        	}
	        	else{
	        		CharSequence text = "Smaller sizes are not allowed!";
	        		int duration = Toast.LENGTH_SHORT;
	        		Toast toast = Toast.makeText(context, text, duration);
	        		toast.show();
	        	}
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private void drawPolygon()
	{
		if (polygon != null) return;
		else{
			double SCALED_LENGTH = LENGTH * SCALE;
			double SCALED_WIDTH = WIDTH * SCALE;
			
			LatLng temp = SphericalUtil.computeOffset(ORIGIN, SCALED_WIDTH/2, 90);
			LatLng p1 = SphericalUtil.computeOffset(temp, SCALED_LENGTH/2, 0);
			LatLng p2 = SphericalUtil.computeOffset(p1, SCALED_LENGTH, 180);
			LatLng p3 = SphericalUtil.computeOffset(p2, SCALED_WIDTH, 270);
			LatLng p4 = SphericalUtil.computeOffset(p3, SCALED_LENGTH, 0);
			
			PolygonOptions rectOptions = new PolygonOptions()
	        .add(p1,p2,p3,p4,p1).fillColor(0x7F59ACFF);
	
	    	//Get back the mutable Polygon
		    polygon = map.addPolygon(rectOptions);
		    Log.d("avinet", "first point" + String.valueOf(polygon.getPoints()));
		    showPolygonControls();
		    
		    map.addMarker(new MarkerOptions().position(ORIGIN));
		}
	}
	
	private void showPolygonControls()
	{
		rotate_c = (ImageButton) findViewById(R.id.rotate_clockwise);
	    rotate_cc = (ImageButton) findViewById(R.id.rotate_counter_clockwise);
	    rotate_c.setVisibility(View.VISIBLE);
	    rotate_cc.setVisibility(View.VISIBLE);
	    textView.setVisibility(View.VISIBLE);
	    textView2.setVisibility(View.VISIBLE);
	    setOffsetPointsText();
	    rotate_c.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	            
	        	rotatePolygon(-10.0);
	        	setOffsetPointsText();
	        }
	    });
	    rotate_cc.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	        	rotatePolygon(10.0);
	        	setOffsetPointsText();
	        }
	    });
	}
	
	
	private void rotatePolygon(double degree){
		List<LatLng> points = new ArrayList<LatLng>();
        points = polygon.getPoints();
    	for (int i = 0; i < points.size(); i++) {
    		points.set(i, rotatePoint(points.get(i), degree));
    	}
    	polygon.setPoints(points);
	}
	
	private LatLng rotatePoint(LatLng point, double degree)
	{
		Log.d("avinet", "inside rotate: " + String.valueOf(point));
        double x =  ORIGIN.longitude   + (Math.cos(Math.toRadians(degree)) * (point.longitude - ORIGIN.longitude) - Math.sin(Math.toRadians(degree))  * (point.latitude - ORIGIN.latitude) / Math.abs(Math.cos(Math.toRadians(ORIGIN.latitude))));
        double y = ORIGIN.latitude + (Math.sin(Math.toRadians(degree)) * (point.longitude - ORIGIN.longitude) * Math.abs(Math.cos(Math.toRadians(ORIGIN.latitude))) + Math.cos(Math.toRadians(degree))   * (point.latitude - ORIGIN.latitude));
        
        LatLng newPoint = new LatLng(y, x);
        
        return newPoint;
	}
	
	private void setOffsetPointsText()
	{	
		List<LatLng> points = new ArrayList<LatLng>();
		points = polygon.getPoints();
		double heading = SphericalUtil.computeHeading(points.get(1), ORIGIN);
		LatLng p2_offset = SphericalUtil.computeOffset(points.get(1), 0.01, heading);
		double heading2 = SphericalUtil.computeHeading(points.get(3), ORIGIN);
		LatLng p4_offset = SphericalUtil.computeOffset(points.get(3), 0.01, heading2);
		
		textView.setText(String.valueOf(p2_offset.latitude)+" "+String.valueOf(p2_offset.longitude));
		textView2.setText(String.valueOf(p4_offset.latitude)+" "+String.valueOf(p4_offset.longitude));
	}
	
}
