package openlab.radiohealthdialoguespush;

/**
 * Created by nkk27 on 02/06/15.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class FirstPage extends ActionBarActivity implements OnClickListener {


    EditText e1,e2; //,e3;
    Button b1, b2;
    String user,password,domain;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);


        e1 = (EditText)findViewById(R.id.editText1);
        e2 = (EditText)findViewById(R.id.editText2);
        //e3 = (EditText)findViewById(R.id.editText3);

        b1 = (Button)findViewById(R.id.button1);
        b2 = (Button)findViewById(R.id.button_register);


        user = e1.getText().toString();
        password = e2.getText().toString();
        //domain = e3.getText().toString();

        b1.setOnClickListener(this);
        b2.setOnClickListener(this);


    }
    @Override
    protected void onDestroy() {
       // stopService(new Intent(FirstPage.this, TCPService.class)) ;

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.first_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if(v == b1){
            Intent in=new Intent(this,Main.class);
            in.putExtra("user", user);
            in.putExtra("password", password);
            in.putExtra("domain", domain);
           // startService(new Intent(FirstPage.this, TCPService.class));
            startActivity(in);

        }
        if (v == b2){
            Intent in1 = new Intent(this, Register.class);
            startActivity(in1);
        }

    }
}