package flyu.comparetool;

import android.app.*;
import android.content.*;
import android.content.res.*;
import android.content.ClipboardManager;
import android.graphics.*;
import android.os.*;
import android.text.*;
import android.text.style.*;
import android.view.*;
import android.widget.*;
import android.widget.SeekBar.*;
import java.io.*;
import java.util.*;

public class FileCompareActivity extends Activity implements ScrollViewListener {
	@Override
	public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
		if (scrollView == mScrollView1) {
			mScrollView2.scrollTo(x, y);
		}
		if (scrollView == mScrollView2) {
			mScrollView1.scrollTo(x, y);
		}
	}

	static final int MESSAGE_UPDATETEXTVIEW=0;
	static final int MESSAGE_GETSPAN=1;
	static final int CODE_ADDORIGINFILE=0;
	static final int CODE_ADDTOCOMPAREFILE=1;
	static final int COLOR_WHITE=0;
	static final int COLOR_BLACK=1; 
	static final int COLOR_GRAY=2;
	static final int COLOR_RED=3; 
	static final int COLOR_GREEN=4; 
	static final int COLOR_BLUE=5; 
	static final int COLOR_LIGHTBLUE=6; 
	static final int COLOR_PINK=7; 
	static final int COLOR_YELLOW=8; 




	ObservableScrollView mScrollView1,mScrollView2;
	EditText mEditTextOrigin,mEditTextToCompare;
	TextView mTextViewTest;

	CheckBox CheckBoxHideTitle,CheckBoxHideStatusBar,CheckBoxScroll,CheckBoxIsPermitEdit;

	Spinner mSpinnerTextColor=null, mSpinnerHighLight=null,mSpinnerBackground=null;

	SharedPreferences mSharedPreferences;

	ProgressDialog mProgressDialog=null;

	SeekBar mSeekBar;

	Handler mHandler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MESSAGE_GETSPAN:
					setTitle("加载完成");
					mEditTextOrigin.setText(toTextViewOrigin);
					mEditTextToCompare.setText(toTextViewToCompare);
					mProgressDialog.setCancelable(true);

					mProgressDialog.cancel();
					break;
			}
			super.handleMessage(msg);
		}
	};
	Message mMessage=new Message();

	String toTextViewOrigin="";
	String toCopy="没有不同之处";

	SpannableStringBuilder toTextViewToCompare=new SpannableStringBuilder();



	Toast t;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		mSharedPreferences = getSharedPreferences("AutoSave", MODE_PRIVATE);

		if (mSharedPreferences.getBoolean("isHideTitle", false)) {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
		} else {
			getActionBar().setDisplayShowHomeEnabled(false);
		}

		if (mSharedPreferences.getBoolean("isHideStatusBar", false)) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
								 WindowManager.LayoutParams.FLAG_FULLSCREEN);

		}




		setContentView(R.layout.file_compare_result);
		setTitle("正在加载");



		mScrollView1 = (ObservableScrollView)findViewById(R.id.mainScrollView1);
		mScrollView2 = (ObservableScrollView)findViewById(R.id.mainScrollView2);
		if (mSharedPreferences.getBoolean("isScroll", false)) {
			mScrollView1.setScrollViewListener(this);
			mScrollView2.setScrollViewListener(this);
		}


		mEditTextOrigin = (EditText)findViewById(R.id.TextViewShowOrigin);
		mEditTextToCompare = (EditText)findViewById(R.id.TextViewShowToCompare);

		mEditTextOrigin.setFocusable(mSharedPreferences.getBoolean("isPermitEdit", false));
		mEditTextToCompare.setFocusable(mSharedPreferences.getBoolean("isPermitEdit", false));

		mEditTextOrigin.setTextSize(mSharedPreferences.getFloat("TextSize", 12));
		mEditTextToCompare.setTextSize(mSharedPreferences.getFloat("TextSize", 12)) ;
		mEditTextOrigin.setTextColor(getColor(mSharedPreferences.getInt("TextColor", 2)));
		mEditTextToCompare.setTextColor(getColor(mSharedPreferences.getInt("TextColor", 2)));

		LinearLayout mLinearLayout=(LinearLayout)findViewById(R.id.compareresultLinearLayout1);

		mLinearLayout.setBackgroundColor(getColor(mSharedPreferences.getInt("BackgroundColor", 1)));

		mProgressDialog = new ProgressDialog(FileCompareActivity.this);
		mProgressDialog.setProgress(0);
		mProgressDialog.setTitle("请稍后……");
		mProgressDialog.setMessage("正在比较中……");
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();

		t = new Toast(this);
		t = t.makeText(this, "", 1);

		new Thread(){
			public void run() {


				mMessage.what = MESSAGE_GETSPAN;
				//			switchForegroundColorSpan(mSharedPreferences.getInt("HighLightColor", 6));
				toTextViewOrigin = readText(getIntent().getStringExtra("FileOriginPath"));
				toTextViewToCompare = CompareAll(toTextViewOrigin, readText(getIntent().getStringExtra("FileToComparePath")));
				mHandler.sendMessage(mMessage);
			}
		}.start();





	}






	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "设置");
		menu.add(0, 1, 0, "快速复制");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case 0:
				View mView=LayoutInflater.from(this).inflate(R.layout.settings, null);
				new AlertDialog.Builder(this).setTitle("设置").setView(mView).setNegativeButton("保存", new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface p1, int p2) {
							mSharedPreferences.edit().putFloat("TextSize", mSeekBar.getProgress() + 5).commit();
							mSharedPreferences.edit().putInt("TextColor", mSpinnerTextColor.getSelectedItemPosition()).commit();

							mSharedPreferences.edit().putInt("HighLightColor", mSpinnerHighLight.getSelectedItemPosition()).commit();
							mSharedPreferences.edit().putInt("BackgroundColor", mSpinnerBackground.getSelectedItemPosition()).commit();
							mSharedPreferences.edit().putBoolean("isHideTitle", CheckBoxHideTitle.isChecked()).commit();
							mSharedPreferences.edit().putBoolean("isHideStatusBar", CheckBoxHideStatusBar.isChecked()).commit();
							mSharedPreferences.edit().putBoolean("isScroll", CheckBoxScroll.isChecked()).commit();
							mSharedPreferences.edit().putBoolean("isPermitEdit", CheckBoxIsPermitEdit.isChecked()).commit();
							t.setText("重新打开页面生效");
							t.show();
						}


					}).show();
				mTextViewTest = (TextView)mView.findViewById(R.id.TextViewTest); 
				mTextViewTest.setTextSize(mSharedPreferences.getFloat("TextSize", 12));
				mSeekBar = (SeekBar)mView.findViewById(R.id.SeekBarTextSize);
				mSeekBar.setProgress((int)(mSharedPreferences.getFloat("TextSize", 12) - 5));
				mSeekBar.setMax(20);
				mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

						@Override
						public void onProgressChanged(SeekBar p1, int p2, boolean p3) {
							mTextViewTest.setTextSize(p2 + 5);

						}
						@Override
						public void onStartTrackingTouch(SeekBar p1) {
						}

						@Override
						public void onStopTrackingTouch(SeekBar p1) {
						}
					});

				mSpinnerTextColor = (Spinner)mView.findViewById(R.id.SpinnerTextColor);
				mSpinnerTextColor.setAdapter(new ArrayAdapter(this, R.layout.spinnerlayout, R.id.TextViewColor, getResources().getStringArray(R.array.colors)));
				mSpinnerTextColor.setSelection(mSharedPreferences.getInt("TextColor", 2));
				mSpinnerTextColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

						@Override
						public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4) {
							mTextViewTest.setTextColor(getColor(p3));
						}

						@Override
						public void onNothingSelected(AdapterView<?> p1) {

						}


					});


				mSpinnerHighLight = (Spinner)mView.findViewById(R.id.SpinnerHighLight);
				mSpinnerHighLight.setAdapter(new ArrayAdapter(this, R.layout.spinnerlayout, R.id.TextViewColor, getResources().getStringArray(R.array.colors)));
				mSpinnerHighLight.setSelection(mSharedPreferences.getInt("HighLightColor", 6));
				mSpinnerHighLight.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

						@Override
						public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4) {
							SpannableStringBuilder mSpannableStringBuilder=new SpannableStringBuilder(mTextViewTest.getText().toString()); 

							mSpannableStringBuilder.setSpan(new ForegroundColorSpan(getColor(p3)), 2, 4, SpannableStringBuilder.SPAN_EXCLUSIVE_INCLUSIVE); 
							mTextViewTest.setText(mSpannableStringBuilder);
						}

						@Override
						public void onNothingSelected(AdapterView<?> p1) {
						}
					});
				mSpinnerBackground = (Spinner)mView.findViewById(R.id.SpinnerBackground);
				mSpinnerBackground.setAdapter(new ArrayAdapter(this, R.layout.spinnerlayout, R.id.TextViewColor, getResources().getStringArray(R.array.colors)));
				mSpinnerBackground.setSelection(mSharedPreferences.getInt("BackgroundColor", 1)); 
				mSpinnerBackground.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
						@Override
						public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4) {

							mTextViewTest.setBackgroundColor(getColor(p3));
						}

						@Override
						public void onNothingSelected(AdapterView<?> p1) {
						}
					}); 
				CheckBoxHideTitle = (CheckBox)mView.findViewById(R.id.CheckBoxHideTitle);
				CheckBoxHideTitle.setChecked(mSharedPreferences.getBoolean("isHideTitle", false));
				CheckBoxHideStatusBar = (CheckBox)mView.findViewById(R.id.CheckBoxHideStatusBar);
				CheckBoxHideStatusBar.setChecked(mSharedPreferences.getBoolean("isHideStatusBar", false));
				CheckBoxScroll = (CheckBox)mView.findViewById(R.id.CheckBoxScroll);
				CheckBoxScroll.setChecked(mSharedPreferences.getBoolean("isScroll", false));
				CheckBoxIsPermitEdit = (CheckBox)mView.findViewById(R.id.CheckBoxIsPermitEdit);
				CheckBoxIsPermitEdit.setChecked(mSharedPreferences.getBoolean("isPermitEdit", false));



				break;
			case 1:

				ClipboardManager mClipboardManager=(ClipboardManager)getSystemService(CLIPBOARD_SERVICE);

				mClipboardManager.setPrimaryClip(ClipData.newPlainText("differences", toCopy));
				t.setText("已复制");
				t.show();
				break;


		}
		return super.onOptionsItemSelected(item);
	}
	


	public int getColor(int colorPositionInList) {
		switch (colorPositionInList) {
			case COLOR_WHITE:
				return Color.WHITE;
			case COLOR_BLACK:
				return Color.BLACK;
			case COLOR_GRAY:
				return Color.GRAY;
			case COLOR_RED:
				return Color.RED;
			case COLOR_GREEN:
				return 	Color.GREEN;
			case COLOR_BLUE:
				return  Color.BLUE;
			case COLOR_LIGHTBLUE:
				return	Color.rgb(0, 255, 255);
			case COLOR_PINK:
				return	Color.rgb(255, 0, 255);
			case COLOR_YELLOW:
				return	Color.YELLOW;
		}


		return 1;
	}
/*
	public ForegroundColorSpan switchForegroundColorSpan(int a) {
		ForegroundColorSpan mForegroundColorSpan=null;
		switch (a) {
			case COLOR_WHITE:
				mForegroundColorSpan = new ForegroundColorSpan(Color.WHITE); 
				break;
			case COLOR_BLACK:
				mForegroundColorSpan = new ForegroundColorSpan(Color.BLACK);
				break; 
			case COLOR_GRAY:
				mForegroundColorSpan = new ForegroundColorSpan(Color.GRAY);
				break;
			case COLOR_RED:
				mForegroundColorSpan = new ForegroundColorSpan(Color.RED);
				break; 
			case COLOR_GREEN:
				mForegroundColorSpan = new ForegroundColorSpan(Color.GREEN);
				break; 
			case COLOR_BLUE:
				mForegroundColorSpan = new ForegroundColorSpan(Color.BLUE);
				break; 
			case COLOR_LIGHTBLUE:
				mForegroundColorSpan = new ForegroundColorSpan(Color.rgb(0, 255, 255));
				break; 
			case COLOR_PINK:
				mForegroundColorSpan = new ForegroundColorSpan(Color.rgb(255, 0, 255));
				break; 
			case COLOR_YELLOW:
				mForegroundColorSpan = new ForegroundColorSpan(Color.YELLOW);
				break; 

		}
		return mForegroundColorSpan;
	}
*/



	public String readText(String filepath) {
		String result="";
		String str="";
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			fis = new FileInputStream(filepath);
			isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);
			while ((str = br.readLine()) != null) {
				result += str + "\n";
			}
		} catch (Exception e) {
			result = "文件不存在";
		} finally {
			try {
				br.close();
				isr.close();
				fis.close();
			} catch (Exception e) {
				result = "文件不存在";
			}
		}
		return result;
	}



	public int getMin(int a, int b) {
		if (a < b) {
			return a;
		} else {
			return b;
		}

	}

	public SpannableStringBuilder  CompareAll(String origin, String toCompare) {
		String[] a= origin.split("\n");
		String[] b=toCompare.split("\n");
		String BuildString="";
		int index=0;
		toCopy = "";
		SpannableStringBuilder mSpannableStringBuilderA=new SpannableStringBuilder(toCompare);

		for (int i=0;;i++) {

			if (i == getMin(a.length, b.length)) {

				break;
			}

			if (a[i].equals(b[i])) {
				BuildString += b[i] + "\n";

			} else {
				toCopy += "第" + (i + 1) + "行\n" + "源文件\n" + a[i] + "\n被比较文件\n" + b[i] + "\n\n";
				index = BuildString.length();
				mSpannableStringBuilderA.setSpan(new ForegroundColorSpan(getColor(mSharedPreferences.getInt("HighLightColor",6))), index, index + b[i].length(), SpannableStringBuilder.SPAN_EXCLUSIVE_INCLUSIVE);
				BuildString += b[i] + "\n";
			}
		}
		return mSpannableStringBuilderA;

	}
}

