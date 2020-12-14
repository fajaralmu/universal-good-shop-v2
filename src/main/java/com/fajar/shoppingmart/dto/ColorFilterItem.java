package com.fajar.shoppingmart.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder.Default;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class ColorFilterItem implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = 3437405319868427774L;

	private int min;
	private int max;
	@Default
	private boolean withFilter = true;
	
	public boolean matchRange(int value) {
		
		if (!withFilter) {
			return true;
		}
		if (min > max) { return false; }
//		if (value == 0) {
//			System.out.println(value +" >= "+ min +"&&"+ value +"<="+ max);
//		}
		if (value >= min && value <= max) {
			return true;
		}
		
//		System.out.println("FALSE");
		return false;
	}
	
	public static void main(String[] args) {
		ColorFilterItem filter = new ColorFilterItem(30, 40, true);
		boolean res = filter.matchRange(100);
		System.out.println(res);
	}
	
	public static ColorFilterItem empty() {
		return ColorFilterItem.builder().withFilter(false).build();
	}

	public static ColorFilterItem create(int min, int max) {
		ColorFilterItem colorFilterItem = ColorFilterItem.builder().min(min).max(max).build();
		return colorFilterItem;
	}
}
