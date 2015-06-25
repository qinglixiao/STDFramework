package com.std.framework.view;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.std.framework.R;

/**
 * 多形状图形,
 * 目前支持的形状有：圆形、矩形、圆角矩形、正多边形，svg图像，正方形
 * 
 */
public class ShapeImageView extends BaseImageView {
	public static class Shape {
		public static final int CIRCLE = 1;			//圆形
		public static final int RECTANGLE = 2;		//矩形
		public static final int SVG = 3;				//svg图像
		public static final int ROUNDRECTANGLE = 4;//圆角矩形
		public static final int POLYGON = 5;		//正多边形
		public static final int SQUARE = 6;			//正方形
	}

	public static final int POLYGONSIDES = 6; 		       //多边形默认边数
	public static final int ROUNDRECTANGLERADIUS = 30;  //圆角矩形默认圆角大小

	private int mShape = Shape.CIRCLE;	//默认为圆形
	private int mSvgRawResourceId;		//svg文件资源id
	private float rx;					//画圆角矩形时x轴的圆角半径
	private float ry;					//画圆角矩形时y的轴圆角半径
	private int sides;					//多边形边数
	private float offsetAngle;				//当为多边形时的起始偏移角度

	public ShapeImageView(Context context) {
		super(context);
	}

	/**
	    * @param context：上下文
	    * @param resourceId ：图像资源id
	    * @param shape ：图像形状
	    * @param rx ：x轴圆角半径（只在圆角矩形中起作用（Shape.ROUNDRECTANGLE)）
	    * @param ry ：y轴圆角半径（只在圆角矩形中起作用（Shape.ROUNDRECTANGLE)）
	    * @param sides ：多边形边数(只在正多边形中起作用（Shape.POLYGON）)
	    * @param offsetAngle ：多边形起始偏移角度(只在正多边形中起作用（Shape.POLYGON）)
	    * @param svgRawResourceId ：svg文件位置
	    */
	public ShapeImageView(Context context, int resourceId, int shape, float rx, float ry, int sides, float offsetAngle, int svgRawResourceId) {
		this(context);
		setImageResource(resourceId);
		mShape = shape;
		this.rx = rx;
		this.ry = ry;
		this.sides = sides;
		this.offsetAngle = offsetAngle;
		mSvgRawResourceId = svgRawResourceId;
	}

	public ShapeImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		sharedConstructor(context, attrs);
	}

	public ShapeImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		sharedConstructor(context, attrs);
	}

	private void sharedConstructor(Context context, AttributeSet attrs) {
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ShapeImageView);
		mShape = a.getInt(R.styleable.ShapeImageView_shape, Shape.CIRCLE);
		mSvgRawResourceId = a.getResourceId(R.styleable.ShapeImageView_svg_raw_resource, 0);
		rx = a.getFloat(R.styleable.ShapeImageView_radius_x, ROUNDRECTANGLERADIUS);
		ry = a.getFloat(R.styleable.ShapeImageView_radius_y, ROUNDRECTANGLERADIUS);
		sides = a.getInt(R.styleable.ShapeImageView_sides, POLYGONSIDES);
		offsetAngle = a.getFloat(R.styleable.ShapeImageView_offsetAngle, 0);
		rightBottomDrawable = a.getDrawable(R.styleable.ShapeImageView_right_bottom_src);
		a.recycle();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean b = super.onTouchEvent(event);
		this.event = event;
		invalidate();
		return b;
	}

	@Override
	public Bitmap getBitmap() {
		switch (mShape) {
			case Shape.CIRCLE:
				CircleImageView circleImageView = new CircleImageView(getContext());
				realImageView = circleImageView;
				realImageView.isFocus = isFocused();
				return circleImageView.getBitmap(getWidth(), getHeight());
			case Shape.RECTANGLE:
				RectangleImageView rectangleImageView = new RectangleImageView(getContext());
				realImageView = rectangleImageView;
				rectangleImageView.isFocus = isFocused();
				return rectangleImageView.getBitmap(getWidth(), getHeight());
			case Shape.SVG:
				SvgImageView svgImageView = new SvgImageView(getContext());
				realImageView = svgImageView;
				realImageView.isFocus = isFocused();
				return svgImageView.getBitmap(mContext, getWidth(), getHeight(), mSvgRawResourceId);
			case Shape.ROUNDRECTANGLE:
				RoundRectangleImageView roundRectangleImageView = new RoundRectangleImageView(getContext());
				realImageView = roundRectangleImageView;
				realImageView.isFocus = isFocused();
				return roundRectangleImageView.getBitmap(getWidth(), getHeight(), rx, ry);
			case Shape.POLYGON:
				PolygonImageView polygonImageView = new PolygonImageView(getContext());
				realImageView = polygonImageView;
				realImageView.isFocus = isFocused();
				return polygonImageView.getBitmap(getWidth(), getHeight(), sides, offsetAngle);
			case Shape.SQUARE:
				SquareImageView squareImageView = new SquareImageView(getContext());
				realImageView = squareImageView;
				realImageView.isFocus = isFocused();
				return squareImageView.getBitmap(getWidth(), getHeight());
		}
		return null;
	}

	/**
	 * 变换图像形状
	 */
	public void changeShapeType(int shape) {
		mShape = shape;
		invalidate();
	}

	public float getRx() {
		return rx;
	}

	public void setRx(float rx) {
		this.rx = rx;
	}

	public float getRy() {
		return ry;
	}

	public void setRy(float ry) {
		this.ry = ry;
	}

	public int getSides() {
		return sides;
	}

	public void setSides(int sides) {
		this.sides = sides;
	}

	public float getOffsetAngle() {
		return offsetAngle;
	}

	public void setOffsetAngle(float offsetAngle) {
		this.offsetAngle = offsetAngle;
	}

}

/**
 * 圆形实现
 */
class CircleImageView extends BaseImageView {

	public CircleImageView(Context context) {
		super(context);
	}

	public CircleImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public Bitmap getBitmap(int width, int height) {
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.BLACK);
		canvas.drawOval(new RectF(0.0f, 0.0f, width, height), paint);

		return bitmap;
	}

	@Override
	public Bitmap getBitmap() {
		return getBitmap(getWidth(), getHeight());
	}

	@Override
	protected void adorn(Canvas canvas, MotionEvent event) {
		super.adorn(canvas, event);
		if (isFocus) {
			Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			mPaint.setStrokeWidth(6);
			mPaint.setColor(Color.WHITE);
			mPaint.setStyle(Paint.Style.STROKE);
			canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, canvas.getWidth() / 2 - 2.5f, mPaint);
		}
	}

}

/**
 *矩形实现
 */
class RectangleImageView extends BaseImageView {

	public RectangleImageView(Context context) {
		super(context);
	}

	public RectangleImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RectangleImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public Bitmap getBitmap(int width, int height) {
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.BLACK);
		canvas.drawRect(new RectF(0.0f, 0.0f, width, height), paint);
		return bitmap;
	}

	@Override
	public Bitmap getBitmap() {
		return getBitmap(getWidth(), getHeight());
	}

}

/**
 *正方形实现
 */
class SquareImageView extends BaseImageView {

	public SquareImageView(Context context) {
		super(context);
	}

	public SquareImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SquareImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public Bitmap getBitmap(int width, int height) {
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.BLACK);

		int offset = Math.abs((width - height) / 2);
		if (width >= height)
			canvas.drawRect(new RectF(offset, 0.0f, offset + height, width), paint);
		else
			canvas.drawRect(new RectF(0.0f, offset, width, offset + width), paint);
		return bitmap;
	}

	@Override
	public Bitmap getBitmap() {
		return getBitmap(getWidth(), getHeight());
	}
}

/**
 * 圆角矩形实现 
 */
class RoundRectangleImageView extends BaseImageView {

	public RoundRectangleImageView(Context context) {
		super(context);
	}

	public RoundRectangleImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RoundRectangleImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public Bitmap getBitmap(int width, int height, float rx, float ry) {
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.BLACK);
		canvas.drawRoundRect(new RectF(0.0f, 0.0f, width, height), rx, ry, paint);
		return bitmap;
	}

	@Override
	public Bitmap getBitmap() {
		return getBitmap(getWidth(), getHeight(), 0, 0);
	}
}

/**
 * 多边形实现 
 */
class PolygonImageView extends BaseImageView {

	public PolygonImageView(Context context) {
		super(context);
	}

	public PolygonImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PolygonImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public Bitmap getBitmap(int width, int height, int sides, float offsetAngle) {
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.BLACK);
		Path path = getPolygonPath(getPolygonPoints(width, height, sides, offsetAngle));
		if (path != null)
			canvas.drawPath(path, paint);
		return bitmap;
	}

	private static PointF[] getPolygonPoints(int width, int height, int sides, float offsetAngle) {
		PointF[] points = new PointF[sides];
		float centerX = width / 2;
		float centerY = height / 2;
		float radius = Math.min(width, height) / 2;
		sides = Math.max(0, Math.abs(sides));

		offsetAngle = (float) (Math.PI * offsetAngle / 180);
		for (int i = 0; i < sides; i++) {
			float x = (float) (centerX + radius * Math.cos(offsetAngle));
			float y = (float) (centerY + radius * Math.sin(offsetAngle));
			points[i] = new PointF(x, y);
			offsetAngle += 2 * Math.PI / sides;
		}
		return points;
	}

	private static Path getPolygonPath(PointF[] points) {
		if (points == null || points.length == 0)
			return null;
		Path path = new Path();
		path.moveTo(points[0].x, points[0].y);
		for (int i = 0; i < points.length; i++)
			path.lineTo(points[i].x, points[i].y);
		return path;
	}

	@Override
	public Bitmap getBitmap() {
		return getBitmap(getWidth(), getHeight(), 0, 0);
	}
}

/**
 * svg图像实现
 */
class SvgImageView extends BaseImageView {
	private static SvgImageView instance;
	private int mSvgRawResourceId;

	public static SvgImageView getInstance(Context context) {
		if (instance == null)
			instance = new SvgImageView(context);
		return instance;
	}

	public SvgImageView(Context context) {
		super(context);
	}

	public SvgImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		sharedConstructor(context, attrs);
	}

	public SvgImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		sharedConstructor(context, attrs);
	}

	private void sharedConstructor(Context context, AttributeSet attrs) {
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ShapeImageView);
		mSvgRawResourceId = a.getResourceId(R.styleable.ShapeImageView_svg_raw_resource, 0);
		a.recycle();
	}

	public Bitmap getBitmap(Context context, int width, int height, int svgRawResourceId) {
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.BLACK);

		if (svgRawResourceId > 0) {
			SVG svg = SVGParser.getSVGFromInputStream(context.getResources().openRawResource(svgRawResourceId), width, height);
			canvas.drawPicture(svg.getPicture());
		}
		else {
			canvas.drawRect(new RectF(0.0f, 0.0f, width, height), paint);
		}

		return bitmap;
	}

	@Override
	public Bitmap getBitmap() {
		return getBitmap(mContext, getWidth(), getHeight(), mSvgRawResourceId);
	}
}

abstract class BaseImageView extends ImageView {
	private static final String TAG = BaseImageView.class.getSimpleName();
	protected Context mContext;
	private static final Xfermode sXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
	private Bitmap mMaskBitmap;
	private Paint mPaint;
	private WeakReference<Bitmap> mWeakBitmap;
	protected BaseImageView realImageView;
	protected MotionEvent event;
	protected boolean isFocus;
	protected Drawable rightBottomDrawable;  //右下角图片

	public BaseImageView(Context context) {
		super(context);
		sharedConstructor(context);
	}

	public BaseImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		sharedConstructor(context);
	}

	public BaseImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		sharedConstructor(context);
	}

	private void sharedConstructor(Context context) {
		mContext = context;
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	}

	public void invalidate() {
		mWeakBitmap = null;
		if (mMaskBitmap != null) {
			mMaskBitmap.recycle();
		}
		super.invalidate();
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		if (!isInEditMode()) {
			int i = canvas.saveLayer(0.0f, 0.0f, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
			try {
				Bitmap bitmap = mWeakBitmap != null ? mWeakBitmap.get() : null;
				if (bitmap == null || bitmap.isRecycled()) {
					Drawable drawable = getDrawable();
					if (drawable != null) {
						bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
						Canvas bitmapCanvas = new Canvas(bitmap);
						drawable.setBounds(0, 0, getWidth(), getHeight());
						drawable.draw(bitmapCanvas);

						// 如果mask图像已经创建则直接取缓存中的图片
						if (mMaskBitmap == null || mMaskBitmap.isRecycled()) {
							mMaskBitmap = getBitmap();
						}

						mPaint.reset();
						mPaint.setFilterBitmap(false);
						mPaint.setXfermode(sXfermode);
						bitmapCanvas.drawBitmap(mMaskBitmap, 0.0f, 0.0f, mPaint);

						mWeakBitmap = new WeakReference<Bitmap>(bitmap);

						if (realImageView != null) {
							realImageView.rightBottomDrawable = rightBottomDrawable;
							realImageView.adorn(bitmapCanvas, event);
						}
					}
				}

				if (bitmap != null) {
					mPaint.setXfermode(null);
					canvas.drawBitmap(bitmap, 0.0f, 0.0f, mPaint);
					return;
				}
			}
			catch (Exception e) {
				System.gc();
				Log.e(TAG, String.format("Failed to draw, Id :: %s. Error occurred :: %s", getId(), e.toString()));
			}
			finally {
				canvas.restoreToCount(i);
			}
		}
		else {
			super.onDraw(canvas);
		}
	}

	protected void adorn(Canvas canvas, MotionEvent event) {
		if (rightBottomDrawable != null) {
			int left = canvas.getWidth() - rightBottomDrawable.getIntrinsicWidth();
			int top = canvas.getHeight() - rightBottomDrawable.getIntrinsicHeight();
			int width = rightBottomDrawable.getIntrinsicWidth();
			int height = rightBottomDrawable.getIntrinsicHeight();
			rightBottomDrawable.setBounds(left, top, left + width, top + height);
			rightBottomDrawable.draw(canvas);
		}

	}
	
	public void setRightBottomDrawable(Drawable drawable){
		rightBottomDrawable = drawable;
	}
	
	public abstract Bitmap getBitmap();
}

/*****************************************以下为SVG格式解析相关类********************************/

/**
 * Parses numbers from SVG text. Based on the Batik Number Parser (Apache 2 License).
 *
 * @author Apache Software Foundation, Larva Labs LLC
 */
class ParserHelper {

	private char current;
	private CharSequence s;
	public int pos;
	private int n;

	public ParserHelper(CharSequence s, int pos) {
		this.s = s;
		this.pos = pos;
		n = s.length();
		current = s.charAt(pos);
	}

	private char read() {
		if (pos < n) {
			pos++;
		}
		if (pos == n) {
			return '\0';
		}
		else {
			return s.charAt(pos);
		}
	}

	public void skipWhitespace() {
		while (pos < n) {
			if (Character.isWhitespace(s.charAt(pos))) {
				advance();
			}
			else {
				break;
			}
		}
	}

	public void skipNumberSeparator() {
		while (pos < n) {
			char c = s.charAt(pos);
			switch (c) {
				case ' ':
				case ',':
				case '\n':
				case '\t':
					advance();
					break;
				default:
					return;
			}
		}
	}

	public void advance() {
		current = read();
	}

	/**
	 * Parses the content of the buffer and converts it to a float.
	 */
	public float parseFloat() {
		int mant = 0;
		int mantDig = 0;
		boolean mantPos = true;
		boolean mantRead = false;

		int exp = 0;
		int expDig = 0;
		int expAdj = 0;
		boolean expPos = true;

		switch (current) {
			case '-':
				mantPos = false;
				// fallthrough
			case '+':
				current = read();
		}

		m1: switch (current) {
			default:
				return Float.NaN;

			case '.':
				break;

			case '0':
				mantRead = true;
				l: for (;;) {
					current = read();
					switch (current) {
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							break l;
						case '.':
						case 'e':
						case 'E':
							break m1;
						default:
							return 0.0f;
						case '0':
					}
				}

			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				mantRead = true;
				l: for (;;) {
					if (mantDig < 9) {
						mantDig++;
						mant = mant * 10 + (current - '0');
					}
					else {
						expAdj++;
					}
					current = read();
					switch (current) {
						default:
							break l;
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
					}
				}
		}

		if (current == '.') {
			current = read();
			m2: switch (current) {
				default:
				case 'e':
				case 'E':
					if (!mantRead) {
						reportUnexpectedCharacterError(current);
						return 0.0f;
					}
					break;

				case '0':
					if (mantDig == 0) {
						l: for (;;) {
							current = read();
							expAdj--;
							switch (current) {
								case '1':
								case '2':
								case '3':
								case '4':
								case '5':
								case '6':
								case '7':
								case '8':
								case '9':
									break l;
								default:
									if (!mantRead) {
										return 0.0f;
									}
									break m2;
								case '0':
							}
						}
					}
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
				case '8':
				case '9':
					l: for (;;) {
						if (mantDig < 9) {
							mantDig++;
							mant = mant * 10 + (current - '0');
							expAdj--;
						}
						current = read();
						switch (current) {
							default:
								break l;
							case '0':
							case '1':
							case '2':
							case '3':
							case '4':
							case '5':
							case '6':
							case '7':
							case '8':
							case '9':
						}
					}
			}
		}

		switch (current) {
			case 'e':
			case 'E':
				current = read();
				switch (current) {
					default:
						reportUnexpectedCharacterError(current);
						return 0f;
					case '-':
						expPos = false;
					case '+':
						current = read();
						switch (current) {
							default:
								reportUnexpectedCharacterError(current);
								return 0f;
							case '0':
							case '1':
							case '2':
							case '3':
							case '4':
							case '5':
							case '6':
							case '7':
							case '8':
							case '9':
						}
					case '0':
					case '1':
					case '2':
					case '3':
					case '4':
					case '5':
					case '6':
					case '7':
					case '8':
					case '9':
				}

				en: switch (current) {
					case '0':
						l: for (;;) {
							current = read();
							switch (current) {
								case '1':
								case '2':
								case '3':
								case '4':
								case '5':
								case '6':
								case '7':
								case '8':
								case '9':
									break l;
								default:
									break en;
								case '0':
							}
						}

					case '1':
					case '2':
					case '3':
					case '4':
					case '5':
					case '6':
					case '7':
					case '8':
					case '9':
						l: for (;;) {
							if (expDig < 3) {
								expDig++;
								exp = exp * 10 + (current - '0');
							}
							current = read();
							switch (current) {
								default:
									break l;
								case '0':
								case '1':
								case '2':
								case '3':
								case '4':
								case '5':
								case '6':
								case '7':
								case '8':
								case '9':
							}
						}
				}
			default:
		}

		if (!expPos) {
			exp = -exp;
		}
		exp += expAdj;
		if (!mantPos) {
			mant = -mant;
		}

		return buildFloat(mant, exp);
	}

	private void reportUnexpectedCharacterError(char c) {
		throw new RuntimeException("Unexpected char '" + c + "'.");
	}

	/**
	 * Computes a float from mantissa and exponent.
	 */
	public static float buildFloat(int mant, int exp) {
		if (exp < -125 || mant == 0) {
			return 0.0f;
		}

		if (exp >= 128) {
			return (mant > 0) ? Float.POSITIVE_INFINITY : Float.NEGATIVE_INFINITY;
		}

		if (exp == 0) {
			return mant;
		}

		if (mant >= (1 << 26)) {
			mant++;  // round up trailing bits if they will be dropped.
		}

		return (float) ((exp > 0) ? mant * pow10[exp] : mant / pow10[-exp]);
	}

	/**
	 * Array of powers of ten. Using double instead of float gives a tiny bit more precision.
	 */
	private static final double[] pow10 = new double[128];

	static {
		for (int i = 0; i < pow10.length; i++) {
			pow10[i] = Math.pow(10, i);
		}
	}

	public float nextFloat() {
		skipWhitespace();
		float f = parseFloat();
		skipNumberSeparator();
		return f;
	}
}

/**
 * Describes a vector Picture object, and optionally its bounds.
 *
 * @author Larva Labs, LLC
 */
class SVG {

	/**
	 * The parsed Picture object.
	 */
	private Picture picture;

	/**
	 * These are the bounds for the SVG specified as a hidden "bounds" layer in the SVG.
	 */
	private RectF bounds;

	/**
	 * These are the estimated bounds of the SVG computed from the SVG elements while parsing.
	 * Note that this could be null if there was a failure to compute limits (ie. an empty SVG).
	 */
	private RectF limits = null;

	/**
	 * Construct a new SVG.
	 * @param picture the parsed picture object.
	 * @param bounds the bounds computed from the "bounds" layer in the SVG.
	 */
	SVG(Picture picture, RectF bounds) {
		this.picture = picture;
		this.bounds = bounds;
	}

	/**
	 * Set the limits of the SVG, which are the estimated bounds computed by the parser.
	 * @param limits the bounds computed while parsing the SVG, may not be entirely accurate.
	 */
	void setLimits(RectF limits) {
		this.limits = limits;
	}

	/**
	 * Create a picture drawable from the SVG.
	 * @return the PictureDrawable.
	 */
	public PictureDrawable createPictureDrawable() {
		return new PictureDrawable(picture);
		//        return new PictureDrawable(picture) {
		//            @Override
		//            public int getIntrinsicWidth() {
		//                if (bounds != null) {
		//                    return (int) bounds.width();
		//                } else if (limits != null) {
		//                    return (int) limits.width();
		//                } else {
		//                    return -1;
		//                }
		//            }
		//
		//            @Override
		//            public int getIntrinsicHeight() {
		//                if (bounds != null) {
		//                    return (int) bounds.height();
		//                } else if (limits != null) {
		//                    return (int) limits.height();
		//                } else {
		//                    return -1;
		//                }
		//            }
		//        };
	}

	/**
	 * Get the parsed SVG picture data.
	 * @return the picture.
	 */
	public Picture getPicture() {
		return picture;
	}

	/**
	 * Gets the bounding rectangle for the SVG, if one was specified.
	 * @return rectangle representing the bounds.
	 */
	public RectF getBounds() {
		return bounds;
	}

	/**
	 * Gets the bounding rectangle for the SVG that was computed upon parsing. It may not be entirely accurate for certain curves or transformations, but is often better than nothing.
	 * @return rectangle representing the computed bounds.
	 */
	public RectF getLimits() {
		return limits;
	}
}

/**
 * Runtime exception thrown when there is a problem parsing an SVG.
 *
 * @author Larva Labs, LLC
 */
class SVGParseException extends RuntimeException {

	public SVGParseException(String s) {
		super(s);
	}

	public SVGParseException(String s, Throwable throwable) {
		super(s, throwable);
	}

	public SVGParseException(Throwable throwable) {
		super(throwable);
	}
}

/**
 * Entry point for parsing SVG files for Android.
 * Use one of the various static methods for parsing SVGs by resource, asset or input stream.
 * Optionally, a single color can be searched and replaced in the SVG while parsing.
 * You can also parse an svg path directly.
 *
 * @author Larva Labs, LLC
 * @see #getSVGFromResource(android.content.res.Resources, int)
 * @see #getSVGFromAsset(android.content.res.AssetManager, String)
 * @see #getSVGFromString(String)
 * @see #getSVGFromInputStream(java.io.InputStream)
 * @see #parsePath(String)
 */
class SVGParser {

	static final String TAG = "SVGAndroid";

	/**
	 * Parse SVG data from an input stream.
	 *
	 * @param svgData the input stream, with SVG XML data in UTF-8 character encoding.
	 * @return the parsed SVG.
	 * @throws SVGParseException if there is an error while parsing.
	 */
	public static SVG getSVGFromInputStream(InputStream svgData) throws SVGParseException {
		return SVGParser.parse(svgData, 0, 0, false);
	}

	/**
	 * Parse SVG data from an input stream and scale to the specific size.
	 * @param svgData
	 * @param targetWidth
	 * @param targetHeight
	 * @return
	 * @throws SVGParseException
	 */
	public static SVG getSVGFromInputStream(InputStream svgData, int targetWidth, int targetHeight) throws SVGParseException {
		return SVGParser.parse(svgData, 0, 0, false, targetWidth, targetHeight);
	}

	/**
	 * Parse SVG data from an Android application resource.
	 *
	 * @param resources the Android context resources.
	 * @param resId     the ID of the raw resource SVG.
	 * @return the parsed SVG.
	 * @throws SVGParseException if there is an error while parsing.
	 */
	public static SVG getSVGFromResource(Resources resources, int resId) throws SVGParseException {
		return SVGParser.parse(resources.openRawResource(resId), 0, 0, false);
	}

	/**
	 * Parse SVG data from an Android application asset.
	 *
	 * @param assetMngr the Android asset manager.
	 * @param svgPath   the path to the SVG file in the application's assets.
	 * @return the parsed SVG.
	 * @throws SVGParseException if there is an error while parsing.
	 * @throws java.io.IOException       if there was a problem reading the file.
	 */
	public static SVG getSVGFromAsset(AssetManager assetMngr, String svgPath) throws SVGParseException, IOException {
		InputStream inputStream = assetMngr.open(svgPath);
		SVG svg = getSVGFromInputStream(inputStream);
		inputStream.close();
		return svg;
	}

	/**
	 * Parse SVG data from an input stream, replacing a single color with another color.
	 *
	 * @param svgData      the input stream, with SVG XML data in UTF-8 character encoding.
	 * @param searchColor  the color in the SVG to replace.
	 * @param replaceColor the color with which to replace the search color.
	 * @return the parsed SVG.
	 * @throws SVGParseException if there is an error while parsing.
	 */
	public static SVG getSVGFromInputStream(InputStream svgData, int searchColor, int replaceColor, int targetWidth, int targetHeight)
			throws SVGParseException {
		return SVGParser.parse(svgData, searchColor, replaceColor, false, targetWidth, targetHeight);
	}

	/**
	 * Parse SVG data from a string.
	 *
	 * @param svgData      the string containing SVG XML data.
	 * @param searchColor  the color in the SVG to replace.
	 * @param replaceColor the color with which to replace the search color.
	 * @return the parsed SVG.
	 * @throws SVGParseException if there is an error while parsing.
	 */
	public static SVG getSVGFromString(String svgData, int searchColor, int replaceColor) throws SVGParseException {
		return SVGParser.parse(new ByteArrayInputStream(svgData.getBytes()), searchColor, replaceColor, false);
	}

	/**
	 * Parse SVG data from an Android application resource.
	 *
	 * @param resources    the Android context
	 * @param resId        the ID of the raw resource SVG.
	 * @param searchColor  the color in the SVG to replace.
	 * @param replaceColor the color with which to replace the search color.
	 * @return the parsed SVG.
	 * @throws SVGParseException if there is an error while parsing.
	 */
	public static SVG getSVGFromResource(Resources resources, int resId, int searchColor, int replaceColor) throws SVGParseException {
		return SVGParser.parse(resources.openRawResource(resId), searchColor, replaceColor, false);
	}

	/**
	 * Parse SVG data from an Android application asset.
	 *
	 * @param assetMngr    the Android asset manager.
	 * @param svgPath      the path to the SVG file in the application's assets.
	 * @param searchColor  the color in the SVG to replace.
	 * @param replaceColor the color with which to replace the search color.
	 * @return the parsed SVG.
	 * @throws SVGParseException if there is an error while parsing.
	 * @throws java.io.IOException       if there was a problem reading the file.
	 */
	public static SVG getSVGFromAsset(AssetManager assetMngr, String svgPath, int searchColor, int replaceColor) throws SVGParseException,
			IOException {
		InputStream inputStream = assetMngr.open(svgPath);
		SVG svg = getSVGFromInputStream(inputStream, searchColor, replaceColor);
		inputStream.close();
		return svg;
	}

	/**
	 * Parses a single SVG path and returns it as a <code>android.graphics.Path</code> object.
	 * An example path is <code>M250,150L150,350L350,350Z</code>, which draws a triangle.
	 *
	 * @param pathString the SVG path, see the specification <a href="http://www.w3.org/TR/SVG/paths.html">here</a>.
	 */
	public static Path parsePath(String pathString) {
		return doPath(pathString);
	}

	private static SVG parse(InputStream in, Integer searchColor, Integer replaceColor, boolean whiteMode, int targetWidth, int targetHeight)
			throws SVGParseException {
		//        Util.debug("Parsing SVG...");
		try {
			long start = System.currentTimeMillis();
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();
			final Picture picture = new Picture();
			SVGHandler handler = new SVGHandler(picture, targetWidth, targetHeight);
			handler.setColorSwap(searchColor, replaceColor);
			handler.setWhiteMode(whiteMode);
			xr.setContentHandler(handler);
			xr.parse(new InputSource(in));
			//        Util.debug("Parsing complete in " + (System.currentTimeMillis() - start) + " millis.");
			SVG result = new SVG(picture, handler.bounds);
			// Skip bounds if it was an empty pic
			if (!Float.isInfinite(handler.limits.top)) {
				result.setLimits(handler.limits);
			}
			return result;
		}
		catch (Exception e) {
			throw new SVGParseException(e);
		}
	}

	private static SVG parse(InputStream in, Integer searchColor, Integer replaceColor, boolean whiteMode) throws SVGParseException {
		return parse(in, searchColor, replaceColor, whiteMode, 0, 0);
	}

	private static NumberParse parseNumbers(String s) {
		//Util.debug("Parsing numbers from: '" + s + "'");
		int n = s.length();
		int p = 0;
		ArrayList<Float> numbers = new ArrayList<Float>();
		boolean skipChar = false;
		for (int i = 1; i < n; i++) {
			if (skipChar) {
				skipChar = false;
				continue;
			}
			char c = s.charAt(i);
			switch (c) {
			// This ends the parsing, as we are on the next element
				case 'M':
				case 'm':
				case 'Z':
				case 'z':
				case 'L':
				case 'l':
				case 'H':
				case 'h':
				case 'V':
				case 'v':
				case 'C':
				case 'c':
				case 'S':
				case 's':
				case 'Q':
				case 'q':
				case 'T':
				case 't':
				case 'a':
				case 'A':
				case ')': {
					String str = s.substring(p, i);
					if (str.trim().length() > 0) {
						//Util.debug("  Last: " + str);
						Float f = Float.parseFloat(str);
						numbers.add(f);
					}
					p = i;
					return new NumberParse(numbers, p);
				}
				case '\n':
				case '\t':
				case ' ':
				case ',':
				case '-': {
					String str = s.substring(p, i);
					// Just keep moving if multiple whitespace
					if (str.trim().length() > 0) {
						//Util.debug("  Next: " + str);
						Float f = Float.parseFloat(str);
						numbers.add(f);
						if (c == '-') {
							p = i;
						}
						else {
							p = i + 1;
							skipChar = true;
						}
					}
					else {
						p++;
					}
					break;
				}
			}
		}
		String last = s.substring(p);
		if (last.length() > 0) {
			//Util.debug("  Last: " + last);
			try {
				numbers.add(Float.parseFloat(last));
			}
			catch (NumberFormatException nfe) {
				// Just white-space, forget it
			}
			p = s.length();
		}
		return new NumberParse(numbers, p);
	}

	private static Matrix parseTransform(String s) {
		if (s.startsWith("matrix(")) {
			NumberParse np = parseNumbers(s.substring("matrix(".length()));
			if (np.numbers.size() == 6) {
				Matrix matrix = new Matrix();
				matrix.setValues(new float[] {
						// Row 1
						np.numbers.get(0), np.numbers.get(2), np.numbers.get(4),
						// Row 2
						np.numbers.get(1), np.numbers.get(3), np.numbers.get(5),
						// Row 3
						0, 0, 1, });
				return matrix;
			}
		}
		else if (s.startsWith("translate(")) {
			NumberParse np = parseNumbers(s.substring("translate(".length()));
			if (np.numbers.size() > 0) {
				float tx = np.numbers.get(0);
				float ty = 0;
				if (np.numbers.size() > 1) {
					ty = np.numbers.get(1);
				}
				Matrix matrix = new Matrix();
				matrix.postTranslate(tx, ty);
				return matrix;
			}
		}
		else if (s.startsWith("scale(")) {
			NumberParse np = parseNumbers(s.substring("scale(".length()));
			if (np.numbers.size() > 0) {
				float sx = np.numbers.get(0);
				float sy = 0;
				if (np.numbers.size() > 1) {
					sy = np.numbers.get(1);
				}
				Matrix matrix = new Matrix();
				matrix.postScale(sx, sy);
				return matrix;
			}
		}
		else if (s.startsWith("skewX(")) {
			NumberParse np = parseNumbers(s.substring("skewX(".length()));
			if (np.numbers.size() > 0) {
				float angle = np.numbers.get(0);
				Matrix matrix = new Matrix();
				matrix.postSkew((float) Math.tan(angle), 0);
				return matrix;
			}
		}
		else if (s.startsWith("skewY(")) {
			NumberParse np = parseNumbers(s.substring("skewY(".length()));
			if (np.numbers.size() > 0) {
				float angle = np.numbers.get(0);
				Matrix matrix = new Matrix();
				matrix.postSkew(0, (float) Math.tan(angle));
				return matrix;
			}
		}
		else if (s.startsWith("rotate(")) {
			NumberParse np = parseNumbers(s.substring("rotate(".length()));
			if (np.numbers.size() > 0) {
				float angle = np.numbers.get(0);
				float cx = 0;
				float cy = 0;
				if (np.numbers.size() > 2) {
					cx = np.numbers.get(1);
					cy = np.numbers.get(2);
				}
				Matrix matrix = new Matrix();
				matrix.postTranslate(cx, cy);
				matrix.postRotate(angle);
				matrix.postTranslate(-cx, -cy);
				return matrix;
			}
		}
		return null;
	}

	/**
	 * This is where the hard-to-parse paths are handled.
	 * Uppercase rules are absolute positions, lowercase are relative.
	 * Types of path rules:
	 * <p/>
	 * <ol>
	 * <li>M/m - (x y)+ - Move to (without drawing)
	 * <li>Z/z - (no params) - Close path (back to starting point)
	 * <li>L/l - (x y)+ - Line to
	 * <li>H/h - x+ - Horizontal ine to
	 * <li>V/v - y+ - Vertical line to
	 * <li>C/c - (x1 y1 x2 y2 x y)+ - Cubic bezier to
	 * <li>S/s - (x2 y2 x y)+ - Smooth cubic bezier to (shorthand that assumes the x2, y2 from previous C/S is the x1, y1 of this bezier)
	 * <li>Q/q - (x1 y1 x y)+ - Quadratic bezier to
	 * <li>T/t - (x y)+ - Smooth quadratic bezier to (assumes previous control point is "reflection" of last one w.r.t. to current point)
	 * </ol>
	 * <p/>
	 * Numbers are separate by whitespace, comma or nothing at all (!) if they are self-delimiting, (ie. begin with a - sign)
	 *
	 * @param s the path string from the XML
	 */
	private static Path doPath(String s) {
		int n = s.length();
		ParserHelper ph = new ParserHelper(s, 0);
		ph.skipWhitespace();
		Path p = new Path();
		float lastX = 0;
		float lastY = 0;
		float lastX1 = 0;
		float lastY1 = 0;
		float subPathStartX = 0;
		float subPathStartY = 0;
		char prevCmd = 0;
		while (ph.pos < n) {
			char cmd = s.charAt(ph.pos);
			switch (cmd) {
				case '-':
				case '+':
				case '0':
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
				case '8':
				case '9':
					if (prevCmd == 'm' || prevCmd == 'M') {
						cmd = (char) (((int) prevCmd) - 1);
						break;
					}
					else if (prevCmd == 'c' || prevCmd == 'C') {
						cmd = prevCmd;
						break;
					}
					else if (prevCmd == 'l' || prevCmd == 'L') {
						cmd = prevCmd;
						break;
					}
				default: {
					ph.advance();
					prevCmd = cmd;
				}
			}

			boolean wasCurve = false;
			switch (cmd) {
				case 'M':
				case 'm': {
					float x = ph.nextFloat();
					float y = ph.nextFloat();
					if (cmd == 'm') {
						subPathStartX += x;
						subPathStartY += y;
						p.rMoveTo(x, y);
						lastX += x;
						lastY += y;
					}
					else {
						subPathStartX = x;
						subPathStartY = y;
						p.moveTo(x, y);
						lastX = x;
						lastY = y;
					}
					break;
				}
				case 'Z':
				case 'z': {
					p.close();
					p.moveTo(subPathStartX, subPathStartY);
					lastX = subPathStartX;
					lastY = subPathStartY;
					lastX1 = subPathStartX;
					lastY1 = subPathStartY;
					wasCurve = true;
					break;
				}
				case 'L':
				case 'l': {
					float x = ph.nextFloat();
					float y = ph.nextFloat();
					if (cmd == 'l') {
						p.rLineTo(x, y);
						lastX += x;
						lastY += y;
					}
					else {
						p.lineTo(x, y);
						lastX = x;
						lastY = y;
					}
					break;
				}
				case 'H':
				case 'h': {
					float x = ph.nextFloat();
					if (cmd == 'h') {
						p.rLineTo(x, 0);
						lastX += x;
					}
					else {
						p.lineTo(x, lastY);
						lastX = x;
					}
					break;
				}
				case 'V':
				case 'v': {
					float y = ph.nextFloat();
					if (cmd == 'v') {
						p.rLineTo(0, y);
						lastY += y;
					}
					else {
						p.lineTo(lastX, y);
						lastY = y;
					}
					break;
				}
				case 'C':
				case 'c': {
					wasCurve = true;
					float x1 = ph.nextFloat();
					float y1 = ph.nextFloat();
					float x2 = ph.nextFloat();
					float y2 = ph.nextFloat();
					float x = ph.nextFloat();
					float y = ph.nextFloat();
					if (cmd == 'c') {
						x1 += lastX;
						x2 += lastX;
						x += lastX;
						y1 += lastY;
						y2 += lastY;
						y += lastY;
					}
					p.cubicTo(x1, y1, x2, y2, x, y);
					lastX1 = x2;
					lastY1 = y2;
					lastX = x;
					lastY = y;
					break;
				}
				case 'S':
				case 's': {
					wasCurve = true;
					float x2 = ph.nextFloat();
					float y2 = ph.nextFloat();
					float x = ph.nextFloat();
					float y = ph.nextFloat();
					if (cmd == 's') {
						x2 += lastX;
						x += lastX;
						y2 += lastY;
						y += lastY;
					}
					float x1 = 2 * lastX - lastX1;
					float y1 = 2 * lastY - lastY1;
					p.cubicTo(x1, y1, x2, y2, x, y);
					lastX1 = x2;
					lastY1 = y2;
					lastX = x;
					lastY = y;
					break;
				}
				case 'A':
				case 'a': {
					float rx = ph.nextFloat();
					float ry = ph.nextFloat();
					float theta = ph.nextFloat();
					int largeArc = (int) ph.nextFloat();
					int sweepArc = (int) ph.nextFloat();
					float x = ph.nextFloat();
					float y = ph.nextFloat();
					drawArc(p, lastX, lastY, x, y, rx, ry, theta, largeArc, sweepArc);
					lastX = x;
					lastY = y;
					break;
				}
			}
			if (!wasCurve) {
				lastX1 = lastX;
				lastY1 = lastY;
			}
			ph.skipWhitespace();
		}
		return p;
	}

	private static void drawArc(Path p, float lastX, float lastY, float x, float y, float rx, float ry, float theta, int largeArc, int sweepArc) {
		// todo - not implemented yet, may be very hard to do using Android drawing facilities.
	}

	private static NumberParse getNumberParseAttr(String name, Attributes attributes) {
		int n = attributes.getLength();
		for (int i = 0; i < n; i++) {
			if (attributes.getLocalName(i).equals(name)) {
				return parseNumbers(attributes.getValue(i));
			}
		}
		return null;
	}

	private static String getStringAttr(String name, Attributes attributes) {
		int n = attributes.getLength();
		for (int i = 0; i < n; i++) {
			if (attributes.getLocalName(i).equals(name)) {
				return attributes.getValue(i);
			}
		}
		return null;
	}

	private static Float getFloatAttr(String name, Attributes attributes) {
		return getFloatAttr(name, attributes, null);
	}

	private static Float getFloatAttr(String name, Attributes attributes, Float defaultValue) {
		String v = getStringAttr(name, attributes);
		if (v == null) {
			return defaultValue;
		}
		else {
			if (v.endsWith("px")) {
				v = v.substring(0, v.length() - 2);
			}
			//            Log.d(TAG, "Float parsing '" + name + "=" + v + "'");
			return Float.parseFloat(v);
		}
	}

	private static Integer getHexAttr(String name, Attributes attributes) {
		String v = getStringAttr(name, attributes);
		//Util.debug("Hex parsing '" + name + "=" + v + "'");
		if (v == null) {
			return null;
		}
		else {
			try {
				return Integer.parseInt(v.substring(1), 16);
			}
			catch (NumberFormatException nfe) {
				// todo - parse word-based color here
				return null;
			}
		}
	}

	private static class NumberParse {
		private ArrayList<Float> numbers;
		private int nextCmd;

		public NumberParse(ArrayList<Float> numbers, int nextCmd) {
			this.numbers = numbers;
			this.nextCmd = nextCmd;
		}

		public int getNextCmd() {
			return nextCmd;
		}

		public float getNumber(int index) {
			return numbers.get(index);
		}

	}

	private static class Gradient {
		String id;
		String xlink;
		boolean isLinear;
		float x1, y1, x2, y2;
		float x, y, radius;
		ArrayList<Float> positions = new ArrayList<Float>();
		ArrayList<Integer> colors = new ArrayList<Integer>();
		Matrix matrix = null;

		public Gradient createChild(Gradient g) {
			Gradient child = new Gradient();
			child.id = g.id;
			child.xlink = id;
			child.isLinear = g.isLinear;
			child.x1 = g.x1;
			child.x2 = g.x2;
			child.y1 = g.y1;
			child.y2 = g.y2;
			child.x = g.x;
			child.y = g.y;
			child.radius = g.radius;
			child.positions = positions;
			child.colors = colors;
			child.matrix = matrix;
			if (g.matrix != null) {
				if (matrix == null) {
					child.matrix = g.matrix;
				}
				else {
					Matrix m = new Matrix(matrix);
					m.preConcat(g.matrix);
					child.matrix = m;
				}
			}
			return child;
		}
	}

	private static class StyleSet {
		HashMap<String, String> styleMap = new HashMap<String, String>();

		private StyleSet(String string) {
			String[] styles = string.split(";");
			for (String s : styles) {
				String[] style = s.split(":");
				if (style.length == 2) {
					styleMap.put(style[0], style[1]);
				}
			}
		}

		public String getStyle(String name) {
			return styleMap.get(name);
		}
	}

	private static class Properties {
		StyleSet styles = null;
		Attributes atts;

		private Properties(Attributes atts) {
			this.atts = atts;
			String styleAttr = getStringAttr("style", atts);
			if (styleAttr != null) {
				styles = new StyleSet(styleAttr);
			}
		}

		public String getAttr(String name) {
			String v = null;
			if (styles != null) {
				v = styles.getStyle(name);
			}
			if (v == null) {
				v = getStringAttr(name, atts);
			}
			return v;
		}

		public String getString(String name) {
			return getAttr(name);
		}

		public Integer getHex(String name) {
			String v = getAttr(name);
			if (v == null || !v.startsWith("#")) {
				return null;
			}
			else {
				try {
					return Integer.parseInt(v.substring(1), 16);
				}
				catch (NumberFormatException nfe) {
					// todo - parse word-based color here
					return null;
				}
			}
		}

		public Float getFloat(String name, float defaultValue) {
			Float v = getFloat(name);
			if (v == null) {
				return defaultValue;
			}
			else {
				return v;
			}
		}

		public Float getFloat(String name) {
			String v = getAttr(name);
			if (v == null) {
				return null;
			}
			else {
				try {
					return Float.parseFloat(v);
				}
				catch (NumberFormatException nfe) {
					return null;
				}
			}
		}
	}

	private static class SVGHandler extends DefaultHandler {

		Picture picture;
		Canvas canvas;
		Paint paint;
		// Scratch rect (so we aren't constantly making new ones)
		RectF rect = new RectF();
		RectF bounds = null;
		RectF limits = new RectF(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);

		Integer searchColor = null;
		Integer replaceColor = null;
		int targetWidth;
		int targetHeight;

		boolean whiteMode = false;

		boolean pushed = false;

		HashMap<String, Shader> gradientMap = new HashMap<String, Shader>();
		HashMap<String, Gradient> gradientRefMap = new HashMap<String, Gradient>();
		Gradient gradient = null;

		private SVGHandler(Picture picture) {
			this.picture = picture;
			paint = new Paint();
			paint.setAntiAlias(true);
		}

		private SVGHandler(Picture picture, int targetWidth, int targetHeight) {
			this(picture);
			this.targetWidth = targetWidth;
			this.targetHeight = targetHeight;
		}

		public void setColorSwap(Integer searchColor, Integer replaceColor) {
			this.searchColor = searchColor;
			this.replaceColor = replaceColor;
		}

		public void setWhiteMode(boolean whiteMode) {
			this.whiteMode = whiteMode;
		}

		@Override
		public void startDocument() throws SAXException {
			// Set up prior to parsing a doc
		}

		@Override
		public void endDocument() throws SAXException {
			// Clean up after parsing a doc
		}

		private boolean doFill(Properties atts, HashMap<String, Shader> gradients) {
			if ("none".equals(atts.getString("display"))) {
				return false;
			}
			if (whiteMode) {
				paint.setStyle(Paint.Style.FILL);
				paint.setColor(0xFFFFFFFF);
				return true;
			}
			String fillString = atts.getString("fill");
			if (fillString != null && fillString.startsWith("url(#")) {
				// It's a gradient fill, look it up in our map
				String id = fillString.substring("url(#".length(), fillString.length() - 1);
				Shader shader = gradients.get(id);
				if (shader != null) {
					//Util.debug("Found shader!");
					paint.setShader(shader);
					paint.setStyle(Paint.Style.FILL);
					return true;
				}
				else {
					//Util.debug("Didn't find shader!");
					return false;
				}
			}
			else {
				paint.setShader(null);
				Integer color = atts.getHex("fill");
				if (color != null) {
					doColor(atts, color, true);
					paint.setStyle(Paint.Style.FILL);
					return true;
				}
				else if (atts.getString("fill") == null && atts.getString("stroke") == null) {
					// Default is black fill
					paint.setStyle(Paint.Style.FILL);
					paint.setColor(0xFF000000);
					return true;
				}
			}
			return false;
		}

		private boolean doStroke(Properties atts) {
			if (whiteMode) {
				// Never stroke in white mode
				return false;
			}
			if ("none".equals(atts.getString("display"))) {
				return false;
			}
			Integer color = atts.getHex("stroke");
			if (color != null) {
				doColor(atts, color, false);
				// Check for other stroke attributes
				Float width = atts.getFloat("stroke-width");
				// Set defaults

				if (width != null) {
					paint.setStrokeWidth(width);
				}
				String linecap = atts.getString("stroke-linecap");
				if ("round".equals(linecap)) {
					paint.setStrokeCap(Paint.Cap.ROUND);
				}
				else if ("square".equals(linecap)) {
					paint.setStrokeCap(Paint.Cap.SQUARE);
				}
				else if ("butt".equals(linecap)) {
					paint.setStrokeCap(Paint.Cap.BUTT);
				}
				String linejoin = atts.getString("stroke-linejoin");
				if ("miter".equals(linejoin)) {
					paint.setStrokeJoin(Paint.Join.MITER);
				}
				else if ("round".equals(linejoin)) {
					paint.setStrokeJoin(Paint.Join.ROUND);
				}
				else if ("bevel".equals(linejoin)) {
					paint.setStrokeJoin(Paint.Join.BEVEL);
				}
				paint.setStyle(Paint.Style.STROKE);
				return true;
			}
			return false;
		}

		private Gradient doGradient(boolean isLinear, Attributes atts) {
			Gradient gradient = new Gradient();
			gradient.id = getStringAttr("id", atts);
			gradient.isLinear = isLinear;
			if (isLinear) {
				gradient.x1 = getFloatAttr("x1", atts, 0f);
				gradient.x2 = getFloatAttr("x2", atts, 0f);
				gradient.y1 = getFloatAttr("y1", atts, 0f);
				gradient.y2 = getFloatAttr("y2", atts, 0f);
			}
			else {
				gradient.x = getFloatAttr("cx", atts, 0f);
				gradient.y = getFloatAttr("cy", atts, 0f);
				gradient.radius = getFloatAttr("r", atts, 0f);
			}
			String transform = getStringAttr("gradientTransform", atts);
			if (transform != null) {
				gradient.matrix = parseTransform(transform);
			}
			String xlink = getStringAttr("href", atts);
			if (xlink != null) {
				if (xlink.startsWith("#")) {
					xlink = xlink.substring(1);
				}
				gradient.xlink = xlink;
			}
			return gradient;
		}

		private void doColor(Properties atts, Integer color, boolean fillMode) {
			int c = (0xFFFFFF & color) | 0xFF000000;
			if (searchColor != null && searchColor.intValue() == c) {
				c = replaceColor;
			}
			paint.setColor(c);
			Float opacity = atts.getFloat("opacity");
			if (opacity == null) {
				opacity = atts.getFloat(fillMode ? "fill-opacity" : "stroke-opacity");
			}
			if (opacity == null) {
				paint.setAlpha(255);
			}
			else {
				paint.setAlpha((int) (255 * opacity));
			}
		}

		private boolean hidden = false;
		private int hiddenLevel = 0;
		private boolean boundsMode = false;

		private void doLimits(float x, float y) {
			if (x < limits.left) {
				limits.left = x;
			}
			if (x > limits.right) {
				limits.right = x;
			}
			if (y < limits.top) {
				limits.top = y;
			}
			if (y > limits.bottom) {
				limits.bottom = y;
			}
		}

		private void doLimits(float x, float y, float width, float height) {
			doLimits(x, y);
			doLimits(x + width, y + height);
		}

		private void doLimits(Path path) {
			path.computeBounds(rect, false);
			doLimits(rect.left, rect.top);
			doLimits(rect.right, rect.bottom);
		}

		private void pushTransform(Attributes atts) {
			final String transform = getStringAttr("transform", atts);
			pushed = transform != null;
			if (pushed) {
				final Matrix matrix = parseTransform(transform);
				canvas.save();
				canvas.concat(matrix);
			}
		}

		private void popTransform() {
			if (pushed) {
				canvas.restore();
			}
		}

		/**
		 * Start recording picture on the canvas.
		 * If target width and height are set for the canvas
		 * scale output picture uniformally using by the smallest
		 * dimention.
		 * @param imageWidth Width of the SVG image.
		 * @param imageHeight Height of the SVG image.
		 * @return
		 */
		private Canvas beginRecordingPicture(int imageWidth, int imageHeight) {
			if (targetWidth == 0 || targetHeight == 0) {
				return picture.beginRecording(imageWidth, imageHeight);
			}
			else {
				Canvas canvas = picture.beginRecording(targetWidth, targetHeight);
				prepareScaledCanvas(canvas, imageWidth, imageHeight);
				return canvas;
			}
		}

		private static final void prepareScaledCanvas(Canvas canvas, float imageWidth, float imageHeight) {
			final float scaleX = canvas.getWidth() / imageWidth;
			final float scaleY = canvas.getHeight() / imageHeight;

			if (scaleX > scaleY) {
				final float dx = ((scaleX - scaleY) * imageWidth) / 2;
				canvas.translate(dx, 0);
				canvas.scale(scaleY, scaleY);
			}
			else {
				final float dy = ((scaleY - scaleX) * imageHeight) / 2;
				canvas.translate(0, dy);
				canvas.scale(scaleX, scaleX);
			}
		}

		@Override
		public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
			// Reset paint opacity
			paint.setAlpha(255);
			// Ignore everything but rectangles in bounds mode
			if (boundsMode) {
				if (localName.equals("rect")) {
					Float x = getFloatAttr("x", atts);
					if (x == null) {
						x = 0f;
					}
					Float y = getFloatAttr("y", atts);
					if (y == null) {
						y = 0f;
					}
					Float width = getFloatAttr("width", atts);
					Float height = getFloatAttr("height", atts);
					bounds = new RectF(x, y, x + width, y + width);
				}
				return;
			}
			if (localName.equals("svg")) {
				int imageWidth = (int) Math.ceil(getFloatAttr("width", atts));
				int imageHeight = (int) Math.ceil(getFloatAttr("height", atts));
				canvas = beginRecordingPicture(imageWidth, imageHeight);
			}
			else if (localName.equals("defs")) {
				// Ignore
			}
			else if (localName.equals("linearGradient")) {
				gradient = doGradient(true, atts);
			}
			else if (localName.equals("radialGradient")) {
				gradient = doGradient(false, atts);
			}
			else if (localName.equals("stop")) {
				if (gradient != null) {
					float offset = getFloatAttr("offset", atts);
					String styles = getStringAttr("style", atts);
					StyleSet styleSet = new StyleSet(styles);
					String colorStyle = styleSet.getStyle("stop-color");
					int color = Color.BLACK;
					if (colorStyle != null) {
						if (colorStyle.startsWith("#")) {
							color = Integer.parseInt(colorStyle.substring(1), 16);
						}
						else {
							color = Integer.parseInt(colorStyle, 16);
						}
					}
					String opacityStyle = styleSet.getStyle("stop-opacity");
					if (opacityStyle != null) {
						float alpha = Float.parseFloat(opacityStyle);
						int alphaInt = Math.round(255 * alpha);
						color |= (alphaInt << 24);
					}
					else {
						color |= 0xFF000000;
					}
					gradient.positions.add(offset);
					gradient.colors.add(color);
				}
			}
			else if (localName.equals("g")) {
				// Check to see if this is the "bounds" layer
				if ("bounds".equalsIgnoreCase(getStringAttr("id", atts))) {
					boundsMode = true;
				}
				if (hidden) {
					hiddenLevel++;
					//Util.debug("Hidden up: " + hiddenLevel);
				}
				// Go in to hidden mode if display is "none"
				if ("none".equals(getStringAttr("display", atts))) {
					if (!hidden) {
						hidden = true;
						hiddenLevel = 1;
						//Util.debug("Hidden up: " + hiddenLevel);
					}
				}
			}
			else if (!hidden && localName.equals("rect")) {
				Float x = getFloatAttr("x", atts);
				if (x == null) {
					x = 0f;
				}
				Float y = getFloatAttr("y", atts);
				if (y == null) {
					y = 0f;
				}
				Float width = getFloatAttr("width", atts);
				Float height = getFloatAttr("height", atts);
				pushTransform(atts);
				Properties props = new Properties(atts);
				if (doFill(props, gradientMap)) {
					doLimits(x, y, width, height);
					canvas.drawRect(x, y, x + width, y + height, paint);
				}
				if (doStroke(props)) {
					canvas.drawRect(x, y, x + width, y + height, paint);
				}
				popTransform();
			}
			else if (!hidden && localName.equals("line")) {
				Float x1 = getFloatAttr("x1", atts);
				Float x2 = getFloatAttr("x2", atts);
				Float y1 = getFloatAttr("y1", atts);
				Float y2 = getFloatAttr("y2", atts);
				Properties props = new Properties(atts);
				if (doStroke(props)) {
					pushTransform(atts);
					doLimits(x1, y1);
					doLimits(x2, y2);
					canvas.drawLine(x1, y1, x2, y2, paint);
					popTransform();
				}
			}
			else if (!hidden && localName.equals("circle")) {
				Float centerX = getFloatAttr("cx", atts);
				Float centerY = getFloatAttr("cy", atts);
				Float radius = getFloatAttr("r", atts);
				if (centerX != null && centerY != null && radius != null) {
					pushTransform(atts);
					Properties props = new Properties(atts);
					if (doFill(props, gradientMap)) {
						doLimits(centerX - radius, centerY - radius);
						doLimits(centerX + radius, centerY + radius);
						canvas.drawCircle(centerX, centerY, radius, paint);
					}
					if (doStroke(props)) {
						canvas.drawCircle(centerX, centerY, radius, paint);
					}
					popTransform();
				}
			}
			else if (!hidden && localName.equals("ellipse")) {
				Float centerX = getFloatAttr("cx", atts);
				Float centerY = getFloatAttr("cy", atts);
				Float radiusX = getFloatAttr("rx", atts);
				Float radiusY = getFloatAttr("ry", atts);
				if (centerX != null && centerY != null && radiusX != null && radiusY != null) {
					pushTransform(atts);
					Properties props = new Properties(atts);
					rect.set(centerX - radiusX, centerY - radiusY, centerX + radiusX, centerY + radiusY);
					if (doFill(props, gradientMap)) {
						doLimits(centerX - radiusX, centerY - radiusY);
						doLimits(centerX + radiusX, centerY + radiusY);
						canvas.drawOval(rect, paint);
					}
					if (doStroke(props)) {
						canvas.drawOval(rect, paint);
					}
					popTransform();
				}
			}
			else if (!hidden && (localName.equals("polygon") || localName.equals("polyline"))) {
				NumberParse numbers = getNumberParseAttr("points", atts);
				if (numbers != null) {
					Path p = new Path();
					ArrayList<Float> points = numbers.numbers;
					if (points.size() > 1) {
						pushTransform(atts);
						Properties props = new Properties(atts);
						p.moveTo(points.get(0), points.get(1));
						for (int i = 2; i < points.size(); i += 2) {
							float x = points.get(i);
							float y = points.get(i + 1);
							p.lineTo(x, y);
						}
						// Don't close a polyline
						if (localName.equals("polygon")) {
							p.close();
						}
						if (doFill(props, gradientMap)) {
							doLimits(p);
							canvas.drawPath(p, paint);
						}
						if (doStroke(props)) {
							canvas.drawPath(p, paint);
						}
						popTransform();
					}
				}
			}
			else if (!hidden && localName.equals("path")) {
				Path p = doPath(getStringAttr("d", atts));
				pushTransform(atts);
				Properties props = new Properties(atts);
				if (doFill(props, gradientMap)) {
					doLimits(p);
					canvas.drawPath(p, paint);
				}
				if (doStroke(props)) {
					canvas.drawPath(p, paint);
				}
				popTransform();
			}
			else if (!hidden) {
				Log.d(TAG, "UNRECOGNIZED SVG COMMAND: " + localName);
			}
		}

		@Override
		public void characters(char ch[], int start, int length) {
			// no-op
		}

		@Override
		public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
			if (localName.equals("svg")) {
				picture.endRecording();
			}
			else if (localName.equals("linearGradient")) {
				if (gradient.id != null) {
					if (gradient.xlink != null) {
						Gradient parent = gradientRefMap.get(gradient.xlink);
						if (parent != null) {
							gradient = parent.createChild(gradient);
						}
					}
					int[] colors = new int[gradient.colors.size()];
					for (int i = 0; i < colors.length; i++) {
						colors[i] = gradient.colors.get(i);
					}
					float[] positions = new float[gradient.positions.size()];
					for (int i = 0; i < positions.length; i++) {
						positions[i] = gradient.positions.get(i);
					}
					if (colors.length == 0) {
						Log.d("BAD", "BAD");
					}
					LinearGradient g = new LinearGradient(gradient.x1, gradient.y1, gradient.x2, gradient.y2, colors, positions,
							Shader.TileMode.CLAMP);
					if (gradient.matrix != null) {
						g.setLocalMatrix(gradient.matrix);
					}
					gradientMap.put(gradient.id, g);
					gradientRefMap.put(gradient.id, gradient);
				}
			}
			else if (localName.equals("radialGradient")) {
				if (gradient.id != null) {
					if (gradient.xlink != null) {
						Gradient parent = gradientRefMap.get(gradient.xlink);
						if (parent != null) {
							gradient = parent.createChild(gradient);
						}
					}
					int[] colors = new int[gradient.colors.size()];
					for (int i = 0; i < colors.length; i++) {
						colors[i] = gradient.colors.get(i);
					}
					float[] positions = new float[gradient.positions.size()];
					for (int i = 0; i < positions.length; i++) {
						positions[i] = gradient.positions.get(i);
					}
					if (gradient.xlink != null) {
						Gradient parent = gradientRefMap.get(gradient.xlink);
						if (parent != null) {
							gradient = parent.createChild(gradient);
						}
					}
					RadialGradient g = new RadialGradient(gradient.x, gradient.y, gradient.radius, colors, positions,
							Shader.TileMode.CLAMP);
					if (gradient.matrix != null) {
						g.setLocalMatrix(gradient.matrix);
					}
					gradientMap.put(gradient.id, g);
					gradientRefMap.put(gradient.id, gradient);
				}
			}
			else if (localName.equals("g")) {
				if (boundsMode) {
					boundsMode = false;
				}
				// Break out of hidden mode
				if (hidden) {
					hiddenLevel--;
					//Util.debug("Hidden down: " + hiddenLevel);
					if (hiddenLevel == 0) {
						hidden = false;
					}
				}
				// Clear gradient map
				gradientMap.clear();
			}
		}
	}
}
