package com.fajar.shoppingmart.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ColorFilter implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2972042514127731364L;
	private ColorFilterItem red;
	private ColorFilterItem green;
	private ColorFilterItem blue;

	@Default
	private boolean useTemplateCharacter = true;
	@Default
	private String character = "o";

	public boolean matchFilter(ColorComponent colorComponent) {
		return matchFilter(colorComponent.getRed(), colorComponent.getGreen(), colorComponent.getBlue());
	}

	public boolean matchFilter(int r, int g, int b) {

		boolean matchRed = null != this.red && this.red.matchRange(r);
//		System.out.println("red: "+ matchRed);
		boolean matchGreen = null != this.green && this.green.matchRange(g);
//		System.out.println("green: "+ matchGreen);
		boolean matchBlue = null != this.blue && this.blue.matchRange(b);
//		System.out.println("blue: "+ matchBlue);

		return matchRed && matchGreen && matchBlue;
	}

	public static ColorFilter createExact(Integer red, Integer green, Integer blue, String character) {
		return createRanged(red, red, green, green, blue, blue, character);
	}

	public static ColorFilter createExactSame(Integer color, String character) {
		return createExact(color, color, color, character);
	}

	public static ColorFilter createRanged(Integer redMin, Integer redMax, Integer greenMin, Integer greenMax,
			Integer blueMin, Integer blueMax, String character) {

		ColorFilter colorFilter = new ColorFilter();
		if (null != character) {
			colorFilter.setUseTemplateCharacter(false);
			colorFilter.setCharacter(character);
		}
		if (null != redMin && null != redMax) {
			colorFilter.setRed(ColorFilterItem.create(redMin, redMax));
		}
		if (null != greenMin && null != greenMax) {
			colorFilter.setGreen(ColorFilterItem.create(greenMin, greenMax));
		}
		if (null != blueMin && null != blueMax) {
			colorFilter.setBlue(ColorFilterItem.create(blueMin, blueMax));
		}
		return colorFilter;
	}

}
