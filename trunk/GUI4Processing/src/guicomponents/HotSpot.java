package guicomponents;

import processing.core.PImage;

/**
 * Base class for different types of hot spot.
 * 
 * @author Peter Lager
 *
 */
abstract class HotSpot implements GConstants, Comparable<HotSpot> {

	public final Integer id;

	abstract public boolean contains(float px, float py);

	protected HotSpot(int id){
		this.id = Math.abs(id);
	}

	public void adjust(Object ... arguments){}

	public int compareTo(HotSpot spoto) {
		return id.compareTo(spoto.id);
	}

	/**
	 * Hit is based on being inside a rectangle.
	 * 
	 * @author Peter Lager
	 */
	static class HSrect extends HotSpot {
		public float x, y, w, h;

		public HSrect(int id, float x, float y, float w, float h) {
			super(id);
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
		}

		@Override
		public boolean contains(float px, float py) {
			return (px >= x && py >= y && px <= x + w && py <= y + h);
		}
	}

	/**
	 * Hit is based on being inside a rectangle.
	 * 
	 * @author Peter Lager
	 */
	static class HScircle extends HotSpot {
		public float x, y, r, r2;

		public HScircle(int id, float x, float y, float r) {
			super(id);
			this.x = x;
			this.y = y;
			this.r = r;
			this.r2 = r * r;
		}

		@Override
		public boolean contains(float px, float py) {
			return ((px-x)*(px-x) + (py-y)*(py-y) <= r2);
		}

		public void adjust(Object ... arguments){
			if(arguments.length > 0)
				x = Float.valueOf(arguments[0].toString());
		}

		
		public String toString(){
			return "HS circle ["+x+", "+y+"]  radius = "+r;
		}
	}

	/**
	 * Hit depends on the mask image. non-transparent areas are black and
	 * transparent areas are white. <br>
	 * 
	 * It is better this way because scaling the image can change the 
	 * colour white to very nearly white but black is unchanged so is 
	 * easier to test.
	 * 
	 * @author Peter Lager
	 *
	 */
	static class HSmask extends HotSpot {

		private PImage mask = null;

		protected HSmask(int id, PImage mask) {
			super(id);
			this.mask = mask;
		}

		@Override
		public boolean contains(float px, float py) {
			if(mask != null){
				int pixel = mask.get((int)px, (int)py);
				float alpha = (pixel >> 24) & 0xff;
				// A > 0 and RGB = 0 is transparent
				if(alpha > 0 && (pixel & 0x00ffffff) == 0){
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * Hit is determined by the alpha channel value.
	 * @author Peter
	 *
	 */
	static class HSalpha extends HotSpot {

		private PImage image = null;

		protected HSalpha(int id, PImage image) {
			super(id);
			this.image = image;
		}

		@Override
		public boolean contains(float px, float py) {
			if(image != null){
				float alpha = (image.get((int)px, (int)py) >> 24) & 0xff;
				if(alpha >  ALPHA_PICK)
					return true;
			}
			return false;
		}

	}
}
