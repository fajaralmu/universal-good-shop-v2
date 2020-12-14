package com.fajar.shoppingmart.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ColorComponent implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4496719903665647525L;
	private int red;
	private int green;
	private int blue;

	public static ColorComponent create(int r, int g, int b) {

		ColorComponent colorComponent = new ColorComponent();
		colorComponent.setBlue(b);
		colorComponent.setGreen(g);
		colorComponent.setRed(r);
		return colorComponent;
	}

	public static ColorComponent adjust(int pixel, List<ColorComponent> colorComponents) {
		
		int pxRed = (pixel >> 16) & 0xff;
		int pxGreen = (pixel >> 8) & 0xff;
		int pxBlue = (pixel) & 0xff;

		if (null == colorComponents || colorComponents.isEmpty()) {
			return create(pxRed, pxGreen, pxBlue);
		}
		Map<Integer, Integer> colorGapSummaries = new HashMap<>();
		for (int i = 0; i < colorComponents.size(); i++) {
			Integer gap = calculateGaps(colorComponents.get(i), pxRed, pxGreen, pxBlue);
			colorGapSummaries.put(i, gap );
		}
		int minGapIndex = getMinGapIndex(colorGapSummaries);
		return colorComponents.get(minGapIndex);
	}

	static Integer getMinGapIndex(Map<Integer, Integer> colorGaps) {

		Integer key = 0;
		int gap = 255;
		for (Entry<Integer, Integer> entry : colorGaps.entrySet()) {
			if (entry.getValue() < gap) {
				gap = entry.getValue();
				key = entry.getKey();
			}
		}
		return key;
	}

	private static int calculateGaps(ColorComponent colorComponent, int pxRed, int pxGreen, int pxBlue) {

		int gapSummary = 0;
		int redGap = Math.abs(colorComponent.red - pxRed);
		int greenGap = Math.abs(colorComponent.green - pxGreen);
		int blueGap = Math.abs(colorComponent.blue - pxBlue);

		gapSummary = redGap + greenGap + blueGap;
		return gapSummary;
	}
}
