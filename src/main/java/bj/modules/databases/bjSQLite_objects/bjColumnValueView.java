package bj.modules.databases.bjSQLite_objects;

import android.content.Context;
import android.graphics.fonts.Font;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class bjColumnValueView extends View {
	private Integer viewKinds=0;
	private Font font;
	private String columnName;
	private LinearLayout linearLayoutTitle;


	public bjColumnValueView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes, Integer viewKinds, Font font, String columnName) {
		super(context, attrs, defStyleAttr, defStyleRes);
		this.viewKinds = viewKinds;
		this.font = font;
		this.columnName = columnName;
	}

	public @interface ViewKinds{
		public static Integer ViewKind_RowColumn=0;
		public static Integer ViewKind_Tick=1;
		public static Integer ViewKind_Title=2;
		public static Integer ViewKind_TitleDetail=3;
		public static Integer ViewKind_DateTime=4;
		public static Integer ViewKind_Date=5;
		public static Integer ViewKind_Time=6;
		public static Integer ViewKind_Direction_LeftRigth=7;
		public static Integer ViewKind_TextOnLine=8;
		public static Integer ViewKind_TextOnLineDetaile=9;
		public static Integer ViewKind_TextMultiLine=9;
		public static Integer ViewKind_TextOnLineNumber=9;
		public static Integer ViewKind_TextOnLineImageNumber=9;

	}
}
