package reliquary.client.color.item;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColorAnalyzer {
	private static final double MAX_MERGE_DISTANCE = 30.0;

	public static int[] filterInteriorPixels(int[] pixels, int width, int height) {
		int[] result = new int[pixels.length];

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int idx = y * width + x;
				int argb = pixels[idx];
				int alpha = (argb >> 24) & 0xFF;

				if (alpha < 32)
					continue;

				boolean touchesTransparent = false;

				for (int dy = -1; dy <= 1 && !touchesTransparent; dy++) {
					for (int dx = -1; dx <= 1; dx++) {
						if (dx == 0 && dy == 0)
							continue;
						int nx = x + dx;
						int ny = y + dy;
						if (nx < 0 || ny < 0 || nx >= width || ny >= height)
							continue;

						int neighbor = pixels[ny * width + nx];
						int neighborAlpha = (neighbor >> 24) & 0xFF;
						if (neighborAlpha < 32) {
							touchesTransparent = true;
							break;
						}
					}
				}

				if (!touchesTransparent) {
					result[idx] = argb;
				}
			}
		}

		return result;
	}

	public static List<Color> getMainAndAccentColors(int[] pixels, int width, int height) {
		pixels = filterInteriorPixels(pixels, width, height);
		Map<Integer, Integer> colorCounts = new HashMap<>();

		for (int argb : pixels) {
			int alpha = (argb >> 24) & 0xFF;
			if (alpha < 32)
				continue;
			int rgb = argb & 0x00FFFFFF;
			colorCounts.merge(rgb, 1, Integer::sum);
		}

		List<ColorGroup> groups = new ArrayList<>();
		for (Map.Entry<Integer, Integer> entry : colorCounts.entrySet()) {
			Color color = new Color(entry.getKey());
			boolean added = false;
			for (ColorGroup group : groups) {
				if (group.distanceTo(color) <= MAX_MERGE_DISTANCE) {
					group.add(color, entry.getValue());
					added = true;
					break;
				}
			}
			if (!added) {
				groups.add(new ColorGroup(color, entry.getValue()));
			}
		}

		groups.sort((a, b) -> Integer.compare(b.totalWeight, a.totalWeight));
		ColorGroup mainGroup = groups.getFirst();
		Color mainColor = mainGroup.getRepresentativeColor();

		double weightExponent = 0.7;
		double distanceExponent = 1.3;

		ColorGroup accentGroup = mainGroup;
		double bestScore = -1;

		for (int i = 1; i < groups.size(); i++) {
			ColorGroup group = groups.get(i);
			double distance = mainGroup.distanceTo(group);
			double score = Math.pow(group.totalWeight, weightExponent) * Math.pow(distance, distanceExponent);

			if (score > bestScore) {
				bestScore = score;
				accentGroup = group;
			}
		}

		Color accentColor = accentGroup.getRepresentativeColor();

		return List.of(mainColor, accentColor);
	}

	private static class ColorGroup {
		int rSum = 0, gSum = 0, bSum = 0, totalWeight = 0;

		final List<Color> colors = new ArrayList<>();

		ColorGroup(Color color, int weight) {
			add(color, weight);
		}

		void add(Color color, int weight) {
			rSum += color.getRed() * weight;
			gSum += color.getGreen() * weight;
			bSum += color.getBlue() * weight;
			totalWeight += weight;
			colors.add(color);
		}

		Color getAverageColor() {
			if (totalWeight == 0)
				return Color.BLACK;
			return new Color(rSum / totalWeight, gSum / totalWeight, bSum / totalWeight);
		}

		Color getRepresentativeColor() {
			if (colors.isEmpty())
				return Color.BLACK;

			Color avg = getAverageColor();
			double minDistance = Double.MAX_VALUE;
			Color best = colors.getFirst();

			for (Color c : colors) {
				double dist = colorDistance(avg, c);
				if (dist < minDistance) {
					minDistance = dist;
					best = c;
				}
			}

			return best;
		}

		double distanceTo(ColorGroup other) {
			return colorDistance(getAverageColor(), other.getAverageColor());
		}

		double distanceTo(Color color) {
			return colorDistance(getAverageColor(), color);
		}

		private static double colorDistance(Color a, Color b) {
			int dr = a.getRed() - b.getRed();
			int dg = a.getGreen() - b.getGreen();
			int db = a.getBlue() - b.getBlue();
			return Math.sqrt(dr * dr + dg * dg + db * db);
		}
	}
}
