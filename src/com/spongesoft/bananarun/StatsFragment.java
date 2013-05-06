package com.spongesoft.bananarun;

import java.util.ArrayList;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.spongesoft.bananarun.R;

public class StatsFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	public static final String ARG_SECTION_NUMBER = "section_number";

	int[] firstData={23,145,67,78,86,190,46,78,167,164};
	int[] secondData={83,45,168,138,67,150,64,87,144,188};
	/**
	 * Buttons and whatnot
	 */
	DBManagement entry;
	

	/**
	 * Methods
	 */
	public StatsFragment() {
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		entry = new DBManagement(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Create a new TextView and set its text to the fragment's section
		// number argument value.
		final View StatsView = inflater
				.inflate(R.layout.stats, container, false);
		
		Button genStats = (Button) StatsView.findViewById(R.id.generalStats);
		ListView lv = (ListView) StatsView.findViewById(R.id.statsListview);

		genStats.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				 getBarChart();
			}
		 });
		
		entry.open();
		int numSessions = entry.getRaceCount();
		entry.close();
		
		String[] list = new String[] {"Session 1", "Session 2", "Session 3",
				"Session 4", "Session 5"};
				/*new ArrayList<String>();
	    for (int j = 0; j < numSessions; j++) {
	      list.add("Session"+j);
	    }
	    */
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
		        android.R.layout.simple_list_item_1, list);
		    lv.setAdapter(adapter);

	        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

	          @Override
	          public void onItemClick(AdapterView<?> parent, final View view,
	              int position, long id) {
	        	  final String item = (String) parent.getItemAtPosition(position);
		            Toast.makeText(getActivity().getApplicationContext(), "Selected item: "+item, Toast.LENGTH_SHORT).show();         
	          }

	        });
	        
	    

		/*TextView tv = (TextView) HomeView.findViewById(R.id.tvSQLinfo);
		entry.open();
		entry.setRace();
		String result = entry.getRaceAvgSpeed();
		tv.setText(result);

		
		
		//
		TextView pref = (TextView) HomeView.findViewById(R.id.pref);
		
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
		String height = settings.getString("prefUserHeight", "No val");
		String weight = settings.getString("prefUserWeight", "0");
		pref.setText("vals: " + height + ", " + weight);
		
		*/
		
		return StatsView;
	}
	
	public void getBarChart(){
	    XYMultipleSeriesRenderer barChartRenderer = getBarChartRenderer();
	    setBarChartSettings(barChartRenderer);
	    Intent intent = ChartFactory.getBarChartIntent(getActivity().getBaseContext(), getBarDemoDataset(), barChartRenderer, Type.DEFAULT);
	    startActivity(intent);
	    }
	
	 private XYMultipleSeriesDataset getBarDemoDataset() {
	        XYMultipleSeriesDataset barChartDataset = new XYMultipleSeriesDataset();
	                CategorySeries firstSeries = new CategorySeries("General 1");
	                for(int i=0;i<firstData.length;i++)
	                    firstSeries.add(firstData[i]);
	                barChartDataset.addSeries(firstSeries.toXYSeries());
	         
	             CategorySeries secondSeries = new CategorySeries("General 2");
	                for(int j=0;j<secondData.length;j++)
	                    secondSeries.add(secondData[j]);
	                barChartDataset.addSeries(secondSeries.toXYSeries());
	        return barChartDataset;
	      }
	
	 public XYMultipleSeriesRenderer getBarChartRenderer() {
	        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
	        renderer.setAxisTitleTextSize(20);
	        renderer.setChartTitleTextSize(18);
	        renderer.setLabelsTextSize(18);
	        renderer.setLegendTextSize(18);
	        renderer.setMargins(new int[] {20, 30, 15, 0});
	        SimpleSeriesRenderer r = new SimpleSeriesRenderer();
	        r.setColor(Color.BLUE);
	        renderer.addSeriesRenderer(r);
	        r = new SimpleSeriesRenderer();
	        r.setColor(Color.GREEN);
	        renderer.addSeriesRenderer(r);
	        return renderer;
	      }
	 private void setBarChartSettings(XYMultipleSeriesRenderer renderer) {
	        renderer.setChartTitle("run1 vs run2");
	        renderer.setXTitle("Time");
	        renderer.setYTitle("Kilometers");
	        renderer.setXAxisMin(0.5);
	        renderer.setXAxisMax(10.5);
	        renderer.setYAxisMin(0);
	        renderer.setYAxisMax(210);
	      }

}
