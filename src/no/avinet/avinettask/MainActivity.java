package no.avinet.avinettask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private Button button;
	private EditText editText;
	private TextView textView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		editText = (EditText) findViewById(R.id.welcomeTextInput);
		button = (Button) findViewById(R.id.welcomeButton);
		textView = (TextView) findViewById(R.id.welcomeText);
		
		//Home message Update
		Context context = getApplicationContext();
		String string = PreferenceManager.getDefaultSharedPreferences(context).getString("welcome", "Welcome to Avinet task!");
        textView.setText(string);
		
        //Home button listener 
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if (editText.getText().length() > 0){
            		PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("welcome", editText.getText().toString()).commit();
            	}
            	Intent i = new Intent(getApplicationContext(), Map.class);
            	startActivity(i); 
            }
        });
	}
}
