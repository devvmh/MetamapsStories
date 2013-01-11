package cc.metamaps.stories;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class MainActivity extends Activity implements ActionBar.OnNavigationListener {

    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
    private Connection conn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        // Set up the dropdown list navigation in the action bar.
        actionBar.setListNavigationCallbacks(
                // Specify a SpinnerAdapter to populate the dropdown list.
                new ArrayAdapter(
                        actionBar.getThemedContext(),
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1,
                        new String[]{
                                getString(R.string.title_section1),
                        }),
                this);
        
        new ConnectToDatabase().execute();
        
        System.out.println("Hi");
    }

    private class ConnectToDatabase extends AsyncTask<Void, Void, Void> {
    	private Connection c;
    	private String username = "failed";
    	protected Void doInBackground(Void... args) {
            try {
    			Class.forName("org.postgresql.Driver");
    		} catch (ClassNotFoundException e) {
    			e.printStackTrace();
    		}
            String url = DatabaseInfo.url;
            String user = DatabaseInfo.user;
            String pass = DatabaseInfo.pass;
            try {
    			c = DriverManager.getConnection(url, user, pass);
        		conn = c;
        		String sql = "SELECT users.name FROM users LIMIT 1";
    			Statement st = conn.createStatement ();
    			ResultSet rs = st.executeQuery(sql);
    			while (rs.next ()) {
    				// Columns are can be referenced by name.
    				username = rs.getString("name");
    				break;
    			}
    			rs.close ();
    	    	st.close ();
    		} catch (SQLException e) {
    			e.printStackTrace();
    		}
            
            return null;
    	}
    	
    	protected void onPostExecute(Void args) {
    		//select userid from username
    		Toast.makeText(MainActivity.this, username, Toast.LENGTH_SHORT).show();
    	}

    }

    @Override
    public void onDestroy() {
    	try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getActionBar().setSelectedNavigationItem(
                    savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM,
                getActionBar().getSelectedNavigationIndex());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(int position, long id) {
        // When the given tab is selected, show the tab contents in the container
        Fragment fragment = new AddStoryFragment();
        Bundle args = new Bundle();
        args.putInt(AddStoryFragment.ARG_SECTION_NUMBER, position + 1);
        fragment.setArguments(args);
        getFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
        return true;
    }
}
