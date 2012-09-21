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

	
	public int compareTo(HotSpot spoto) {
		return id.compareTo(spoto.id);
	}
	
	/**
	 * Hit is based on being inside a rectangle.
	 * 
	 * @author Peter Lager
	 */
	static class HSrect extends HotSpot {
		private final float x, y, w, h;

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

		PImage mask = null;

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

		PImage image = null;

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
