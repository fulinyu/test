package flyu.comparetool;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.util.*;
import android.view.View.*;
import android.widget.AdapterView.*;
import java.net.*;
import android.content.*;
import java.text.*;
import android.net.*;

public class MainActivity extends Activity
{
	File currentFile;
	File[] currentFiles;
	ListView mListView;
	SimpleAdapter mSimpleAdapter;
	TextView mTextView;
	int index;
	Intent intent=new Intent();
	ProgressDialog mProgressDialog;

	Handler mHandler=new Handler(){

		@Override
		public void handleMessage(Message msg)
		{


			mTextView.setText(currentFile.toString());
			mListView.setAdapter(mSimpleAdapter);
			mProgressDialog.setCancelable(true);
			mProgressDialog.cancel();
			super.handleMessage(msg);
		}
		
	};
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);

		currentFile = Environment.getExternalStorageDirectory();
	
        setContentView(R.layout.main);

		mTextView = (TextView)findViewById(R.id.mainTextView1);
		mListView = (ListView)findViewById(R.id.mainListView1);

		refreshListView();

		mListView.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
				{
					if (currentFiles[p3].isDirectory())
					{
						currentFile = new File(currentFile, currentFiles[p3].getName());

						refreshListView();

					}
					else
					{
						Intent i=new Intent();
						i.setAction(Intent.ACTION_VIEW);
						i.setDataAndType(Uri.fromFile(currentFiles[p3]),
						URLConnection.guessContentTypeFromName(currentFiles[p3].getName()));
try{startActivity(i);}catch(Exception e){}
					}
				}
			});



		mListView.setOnItemLongClickListener(new OnItemLongClickListener(){

				@Override
				public boolean onItemLongClick(AdapterView<?> p1, View p2, int p3, long p4)
				{
					index = p3;
					new AlertDialog.Builder(MainActivity.this)  
						.setTitle("请选择")  
						.setIcon(android.R.drawable.ic_dialog_info)                  
						.setItems(new String[] {"添加至被比较对象","添加至比较对象","删除"}, 
						new DialogInterface.OnClickListener() {  

							public void onClick(DialogInterface dialog, int which)
							{  
								switch (which)
								{
									case 0:
										if (currentFiles[index].isFile())
										{
											intent.putExtra("FileOriginPath", currentFiles[index].getPath());
										}
										else
										{
											intent.putExtra("DirectoryOriginPath", currentFiles[index].getPath());
										}
										break;
									case 1:
										if (currentFiles[index].isFile())
										{

											intent.putExtra("FileToComparePath", currentFiles[index].getPath());
										}
										else
										{
											intent.putExtra("DirectoryToComparePath", currentFiles[index].getPath());
										}
										break;
									case 2:
										currentFiles[index].delete();
										break;
								}
							}  
						}  
					)  
						.setNegativeButton("取消", null)  
						.show();  

					return false;
				}
			});
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(Menu.NONE, 0, 0, "开始比较文件");
		menu.add(Menu.NONE, 1, 1, "开始比较文件夹");
		menu.add(Menu.NONE, 2, 2, "查看文件列表");
		menu.add(Menu.NONE, 3, 3, "查看文件夹列表");
		menu.add(Menu.NONE, 4, 4, "帮助和关于");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case 0:
			
intent.setClass(this,FileCompareActivity.class);
startActivity(intent);
				break;
			case 1:
				intent.setClass(this,DirectoryCompareActivity.class);
				startActivity(intent);
				break;
			case 2:
				new AlertDialog.Builder(MainActivity.this).setMessage(
				intent.getStringExtra("FileOriginPath")
				+"\n"+
				intent.getStringExtra("FileToComparePath")
			
				).show();
				break;
			case 3:
				new AlertDialog.Builder(MainActivity.this).setMessage(
					intent.getStringExtra("DirectoryOriginPath")
					+"\n"+
					intent.getStringExtra("DirectoryToComparePath")

				).show();
				break;
			case 4:
showAbout();
				break;


		}
		return super.onOptionsItemSelected(item);
	}

	public void showAbout() {
		new AlertDialog.Builder(this).setTitle("关于").setMessage(String.format(getFromAssets("updatelog"), "百度ID：炸了天中 \n\bEmail：807377534@qq.com")).show();
	}


	public String getFromAssets(String fileName) {
		try {
			InputStreamReader inputReader = new InputStreamReader(getResources().getAssets().open(fileName));
			BufferedReader bufReader = new BufferedReader(inputReader);
			String line="";
			String Result="";
			while ((line = bufReader.readLine()) != null)
				Result += line + "\n";
			return Result;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}

	}


	void refreshListView()
	{
		
		
				mProgressDialog = new ProgressDialog(MainActivity.this);
		mProgressDialog.setProgress(0);
		mProgressDialog.setTitle("请稍后……");
		mProgressDialog.setMessage("正在处理……");
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();

		

		new Thread(){
			public void run() {

				currentFiles = currentFile.listFiles();
				Arrays.sort(currentFiles,new MyComparator());
				
				
				List<Map<String,Object>> a=new ArrayList<Map<String,Object>>();
				Map<String,Object>b=new HashMap<String,Object>();

				for (int i=0;i < currentFiles.length;i++)
				{
					b = new HashMap<String,Object>();
					String str=currentFiles[i].getName();
					if (currentFiles[i].isDirectory())
					{
						b.put("icon", R.drawable.folder);
					}
					else
					{
						b.put("icon",R.drawable.file);
						b.put("size", android.text.format.Formatter.formatFileSize(MainActivity.this, currentFiles[i].length()));
					}
					b.put("name", str);
					a.add(b);
				}
			
				mSimpleAdapter = new SimpleAdapter(MainActivity.this, a, R.layout.list_item, new String[]{"icon","name","size"}, new int[]{R.id.img,R.id.txt_name,R.id.txt_size});
				
				mHandler.sendEmptyMessage(0);
			}
		}.start();
		
		
		
		
	}


	@Override
	public void onBackPressed()
	{
		if (currentFile.equals(Environment.getExternalStorageDirectory()))
		{
			super.onBackPressed();
		}
		else
		{
			currentFile = new File(currentFile.getParent());
			refreshListView();
		}
	}
}
