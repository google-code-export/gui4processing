package guicomponents;

import processing.core.PApplet;

/**
 * Base class for all slider and knob type controls.
 * 
 * @author Peter Lager
 *
 */
public class FValueControl extends FAbstractControl {

	static protected float HINSET = 2;

	protected float startLimit = 0, endLimit = 1;
	protected boolean showLimits = true;
	
	protected StyledString ssStartLimit, ssEndLimit, ssValue;

	protected int valueType = DECIMAL;
	protected int precision = 2;
	protected String unit = "";
	protected boolean showValue = true;
	
	protected float epsilon = 0.01f;
	
	protected float valuePos = 0.5f, valueTarget = 0.5f;
	protected boolean isValueChanging  = false;
	protected float easing  = 1.0f; // must be >= 1.0
	

	protected int nbrTicks = 2;
	protected boolean stickToTicks = false;
	protected boolean showTicks = true;					//  make false for final release
	
	protected boolean limitsInvalid = true;
	
	// Offset to between mouse and thumb centre
	protected float offset;
	
	public FValueControl(PApplet theApplet, float p0, float p1, float p2, float p3) {
		super(theApplet, p0, p1, p2, p3);
	}
	
	public void pre(){
		if(Math.abs(valueTarget - valuePos) > epsilon){
			valuePos += (valueTarget - valuePos) / easing;
			isValueChanging = bufferInvalid = true;
			fireEventX(this, isValueChanging);
		}
		else {
			valuePos = valueTarget;
			if(isValueChanging){
				bufferInvalid = true;
				isValueChanging = false;
				fireEventX(this, isValueChanging);
			}
		}
	}
	
	public String getNumericDisplayString(float number){
		String s = "";
		switch(valueType){
		case INTEGER:
			s = String.format("%d %s", Math.round(number), unit);
			break;
		case DECIMAL:
			s = String.format("%." + precision + "f %s", number, unit);
			break;
		case EXPONENT:
			s = String.format("%." + precision + "f %s", number, unit);
			break;
		}
		return s.trim();
	}
	
	
	/**
	 * Sets the range of values to be returned. If you pass two integer values it
	 * will assume that you want to set the valueType to INTEGER;
	 * @param start
	 * @param end
	 */
	public void setLimits(int start, int end){
		startLimit = start;
		endLimit = end;
		valueType = INTEGER;
		limitsInvalid = true;
		bufferInvalid = true;
	}
	
	/**
	 * Sets the range of values to be returned. If you pass two integer values it
	 * will assume that you want to set the valueType to INTEGER;
	 * @param start
	 * @param end
	 */
	public void setLimits(float start, float end){
		startLimit = start;
		endLimit = end;
		if(valueType == INTEGER){
			valueType = DECIMAL;
			if(precision == 0)
				precision = 1;
		}
		limitsInvalid = true;
		bufferInvalid = true;
	}
	
	public void setValue(float v){
		if(valueType == INTEGER)
			v = Math.round(v);
		float p = (v - startLimit) / (endLimit - startLimit);
		if(p < 0)
			p = 0;
		else if(p > 1)
			p = 1;
		if(stickToTicks)
			p = findNearestTickValueTo(p);
		valueTarget = p;
	}
	
	public void setPrecision(int p){
		if(p < 1)
			p = 1;
		if(p != precision){
			precision = p;
			limitsInvalid = true;
			bufferInvalid = true;
		}
	}
	
	public void setUnits(String units){
		if(units == null)
			units = "";
		if(!unit.equals(units)){
			unit = units;
			limitsInvalid = true;
			bufferInvalid = true;			
		}
	}
	
	public void setNumberFormat(int displayFormat, int precision, String unit){
		this.unit = (unit == null) ? "" : unit;
		setNumberFormat(displayFormat, precision);
	}

	/**
	 * 
	 * @param numberFormat
	 * @param precision
	 */
	public void setNumberFormat(int numberFormat, int precision){
		switch(numberFormat){
		case INTEGER:
		case DECIMAL:
		case EXPONENT:
			this.valueType = numberFormat;
			break;
		default:
			valueType = DECIMAL;
		}
		this.precision = precision;
		bufferInvalid = true;
	}
	
	/**
	 * Get the current value as a float
	 */
	public float getValueF(){
		return startLimit + (endLimit - startLimit) * valuePos;
	}

	/**
	 * Get the current value as an integer. <br>
	 * DECIMAL and EXPONENT value types will be rounded to the nearest integer.
	 */
	public float getValueI(){
		return Math.round(startLimit + (endLimit - startLimit) * valuePos);
	}
	
	/**
	 * @return the easing
	 */
	public float getEasing() {
		return easing;
	}

	/**
	 * @param easeBy the easing to set
	 */
	public void setEasing(float easeBy) {
		if(easeBy < 1)
			easing = 1;
		else
			easing = easeBy;
	}

	/**
	 * @return the nbrTicks
	 */
	public int getNbrTicks() {
		return nbrTicks;
	}

	/**
	 * @param noOfTicks the nbrTicks to set
	 */
	public void setNbrTicks(int noOfTicks) {
		if(noOfTicks < 2)
			noOfTicks = 2;
		if(nbrTicks != noOfTicks){
			nbrTicks = noOfTicks;
			bufferInvalid = true;
		}
	}

	/**
	 * @return the stickToTicks
	 */
	public boolean isStickToTicks() {
		return stickToTicks;
	}

	/**
	 * @param stickToTicks the stickToTicks to set
	 */
	public void setStickToTicks(boolean stickToTicks) {
		this.stickToTicks = stickToTicks;
		if(stickToTicks)
			valueTarget = findNearestTickValueTo(valuePos);
	}

	/**
	 * These are normalised values i.e. between 0.0 and 1.0 inclusive
	 * @param p
	 * @return
	 */
	protected float findNearestTickValueTo(float p){
		float tickSpace = 1.0f / (nbrTicks - 1);
		int tn =  (int) (p / tickSpace + 0.5f);
		return tickSpace * tn;
	}
	
	/**
	 * @return the showTicks
	 */
	public boolean isShowTicks() {
		return showTicks;
	}

	/**
	 * @param showTicks the showTicks to set
	 */
	public void setShowTicks(boolean showTicks) {
		this.showTicks = showTicks;
	}

	/**
	 * @return the startLimit
	 */
	public float getStartLimit() {
		return startLimit;
	}

	/**
	 * @return the endLimit
	 */
	public float getEndLimit() {
		return endLimit;
	}

	/**
	 * @return the valueType
	 */
	public int getValueType() {
		return valueType;
	}

	/**
	 * @return the precision
	 */
	public int getPrecision() {
		return precision;
	}

	/**
	 * @return the unit
	 */
	public String getUnit() {
		return unit;
	}

	/**
	 * @return the isValueChanging
	 */
	public boolean isValueChanging() {
		return isValueChanging;
	}

	
}
