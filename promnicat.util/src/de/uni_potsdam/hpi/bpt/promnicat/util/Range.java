/**
 * PromniCAT - Collection and Analysis of Business Process Models
 * Copyright (C) 2012 Cindy Fähnrich, Tobias Hoppe, Andrina Mascher
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.uni_potsdam.hpi.bpt.promnicat.util;

/**
 * Range class representing an inclusive range of doubles.
 * 
 * @author Cindy Fähnrich
 *
 */
public class Range {
	
	private double maxValue = 0;
	private double minValue = 0;
	
	
	public Range(double min, double max){
		super();
		maxValue = max;
		minValue = min;
	}
	
	public Range(double number){
		super();
		maxValue = number;
		minValue = number;
	}
	public double getMaxValue(){
		return maxValue;
	}
	
	public double getMinValue(){
		return minValue;
	}
	
	public void setMaxValue(double max){
		maxValue = max;
	}
	
	public void setMinValue(double min){
		minValue = min;
	}

	public boolean isInRange(double number){
		return ((minValue <= number) && (number <= maxValue)); 
	}
	
	public void updateRange(double number){
		if (!isInRange(number)){
			setNewRange(number);
		}
	}
	
	public void updateRange(Range otherRange){
		if (!isInRange(otherRange.getMinValue())){
			setNewRange(otherRange.getMinValue());
		}
		if (!isInRange(otherRange.getMaxValue())){
			setNewRange(otherRange.getMaxValue());
		}
	}
	public void setNewRange(double number){
		if (minValue > number){
			minValue = number;
		} 
		if (maxValue < number){
			maxValue = number;
		}
	}
	
	public Range copy(){
		Range newRange = new Range(minValue, maxValue);
		return newRange;
	}
}
