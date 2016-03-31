package flyu.comparetool;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.widget.*;
import java.io.*;
import java.util.*;

public class DirectoryCompareActivity extends Activity
{
	ListView mListViewLeft,mListViewRight;
	MyAdapter mAdapterLeft,mAdapterRight;
	File OriginPath,ToComparePath;
	File[] OriginFiles,ToCompareFiles;
	ProgressDialog mProgressDialog;
	Handler mHandler=new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
			mProgressDialog.setCancelable(true);
			mProgressDialog.cancel();
			mListViewLeft.setAdapter(mAdapterLeft);
			mListViewRight.setAdapter(mAdapterRight);
			super.handleMessage(msg);
		}
	
};
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);

		setContentView(R.layout.directory_compare_result);

		mListViewLeft = (ListView)findViewById(R.id.directorycompareresultListView1);
		mListViewRight = (ListView)findViewById(R.id.directorycompareresultListView2);
		
		

		mProgressDialog = new ProgressDialog(DirectoryCompareActivity.this);
		mProgressDialog.setProgress(0);
		mProgressDialog.setTitle("请稍后……");
		mProgressDialog.setMessage("正在比较中……");
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();
		
		
		new Thread(){
			public void run(){
				Intent intent=getIntent();
				OriginPath = new File(intent.getStringExtra("DirectoryOriginPath"));
				ToComparePath = new File(intent.getStringExtra("DirectoryToComparePath"));
				OriginFiles = OriginPath.listFiles();
				Arrays.sort(OriginFiles, new MyComparator());
				ToCompareFiles = ToComparePath.listFiles();
				Arrays.sort(ToCompareFiles, new MyComparator());


				List<Integer> ms=new ArrayList<Integer>();
				List<Integer> ns=new ArrayList<Integer>();

				for (int m=0;m < OriginFiles.length;m++)
				{
					for (int n=0;n < ToCompareFiles.length;n++)
					{

						if (OriginFiles[m].getName().equals(ToCompareFiles[n].getName()))
						{
							ms.add(m);
							ns.add(n);

						}
					}

				}
				
				mAdapterLeft = new MyAdapter(DirectoryCompareActivity.this,listToPrimitive(ms), Color.RED, OriginFiles);
				mAdapterRight= new MyAdapter(DirectoryCompareActivity.this,listToPrimitive(ns), Color.RED, ToCompareFiles);
				
				mHandler.sendEmptyMessage(0);
				
			}
		}.start();
		
		}

	public int[] listToPrimitive(List<Integer> list)
	{ 
		if (list == null)
		{
			return null; 
		} 
		int[] result = new int[list.size()]; 
		for (int i = 0; i < list.size(); ++i)
		{
			result[i] = list.get(i).intValue(); 
		}
		return result; 
	}


}
