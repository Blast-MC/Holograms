package tech.blastmc.holograms.utils;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Helper methods for modifying Strings
 */
public class StringUtils {

	@Getter
	private static final String colorChar = "§";
	@Getter
	private static final String altColorChar = "&";
	@Getter
	private static final String colorCharsRegex = "[" + colorChar + altColorChar + "]";
	@Getter
	private static final Pattern colorPattern = Pattern.compile(colorCharsRegex + "[\\da-fA-F]");
	@Getter
	private static final Pattern formatPattern = Pattern.compile(colorCharsRegex + "[k-orK-OR]");
	@Getter
	private static final Pattern hexPattern = Pattern.compile(colorCharsRegex + "#[a-fA-F\\d]{6}");
	@Getter
	private static final Pattern hexColorizedPattern = Pattern.compile(colorCharsRegex + "x(" + colorCharsRegex + "[a-fA-F\\d]){6}");
	@Getter
	private static final Pattern colorGroupPattern = Pattern.compile("(" + colorPattern + "|(" + hexPattern + "|" + hexColorizedPattern + "))((" + formatPattern + ")+)?");

	public static String colorize(String input) {
		if (input == null)
			return null;

		while (true) {
			Matcher matcher = hexPattern.matcher(input);
			if (!matcher.find()) break;

			String color = matcher.group();
			input = input.replace(color, net.md_5.bungee.api.ChatColor.of(color.replaceFirst(colorCharsRegex, "")).toString());
		}

		return ChatColor.translateAlternateColorCodes(altColorChar.charAt(0), input);
	}

	public static String getPrefix(String prefix) {
		return colorize("&8&l[&e" + prefix + "&8&l]&3 ");
	}

	/**
	 * Removes any color from a message
	 *
	 * @param input The colored message
	 *
	 * @return The uncolored message
	 */
	public static String stripColor(String input) {
		return ChatColor.stripColor(colorize(input));
	}

	/**
	 * Removes the last character from the end of a string, leaving the rest
	 *
	 * @param string The string to trim the last character from
	 * @return The modified string
	 */
	public static String trimFirst(String string) {
		return right(string, string.length() - 1);
	}

	/**
	 * Gets the specified amount of characters from the right of the given string
	 *
	 * @param string The string to modify
	 * @param number The number of characters to get
	 *
	 * @return The given amount of characters from the right of the given string
	 */
	public static String right(String string, int number) {
		return string.substring(Math.max(string.length() - number, 0));
	}

	/**
	 * Gets the specified amount of characters from the left of the given string
	 *
	 * @param string The string to modify
	 * @param number The number of characters to get
	 *
	 * @return The given amount of characters from the left of the given string
	 */
	public static String left(String string, int number) {
		return string.substring(0, Math.min(number, string.length()));
	}

	/**
	 * Modify a string to be camel case, or every word capitalized
	 *
	 * @param text The string to camel case
	 *
	 * @return The camel cased string
	 */
	public static String camelCase(String text) {
		if (text == null || text.isEmpty()) {
			return text;
		}

		return Arrays.stream(text.replaceAll("_", " ").split(" "))
				.map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase())
				.collect(Collectors.joining(" "));
	}

	/**
	 * Modifies a string to get the string after the last instance of the given delimiter
	 *
	 * @param string The string to modify
	 * @param delimiter The character to look for
	 *
	 * @return The modified string
	 */
	public static String listLast(String string, String delimiter) {
		return string.substring(string.lastIndexOf(delimiter) + 1);
	}

	/**
	 * Get a string version of a location in simple forms
	 *
	 * @param loc The location to turn into a short string
	 *
	 * @return The string version of the given location
	 */
	public static String getShortLocationString(Location loc) {
		DecimalFormat nf = new DecimalFormat("0.00");
		return nf.format(loc.getX()) + ", " + nf.format(loc.getY()) + ", " +  nf.format(loc.getZ());
	}

	public static String getLastColor(String text) {
		Matcher matcher = colorGroupPattern.matcher(text);
		String last = "";
		while (matcher.find())
			last = matcher.group();
		return last.toLowerCase();
	}

	public static String loreize(String string) {
		int i = 0, lineLength = 0;
		boolean watchForNewLine = false, watchForColor = false;
		string = colorize(string);

		for (String character : string.split("")) {
			if (character.contains("\n")) {
				lineLength = 0;
				continue;
			}

			if (watchForNewLine) {
				if ("|".equalsIgnoreCase(character))
					lineLength = 0;
				watchForNewLine = false;
			} else if ("|".equalsIgnoreCase(character))
				watchForNewLine = true;

			if (watchForColor) {
				if (character.matches("[A-Fa-fK-Ok-oRr0-9]"))
					lineLength -= 2;
				watchForColor = false;
			} else if ("&".equalsIgnoreCase(character))
				watchForColor = true;

			++lineLength;

			if (lineLength > 28)
				if (" ".equalsIgnoreCase(character)) {
					String before = left(string, i);
					String excess = right(string, string.length() - i);
					if (excess.length() > 5) {
						excess = excess.trim();
						boolean doSplit = true;
						if (excess.contains("||") && excess.indexOf("||") <= 5)
							doSplit = false;
						if (excess.contains(" ") && excess.indexOf(" ") <= 5)
							doSplit = false;
						if (lineLength >= 38)
							doSplit = true;

						if (doSplit) {
							string = before + "||" + getLastColor(before) + excess.trim();
							lineLength = 0;
							i += 4;
						}
					}
				}

			++i;
		}

		return string;
	}

	private static String getColoredWords(String text) {
		if (text == null) return null;
		StringBuilder builder = new StringBuilder();
		for (String word : text.split(" "))
			builder.append(getLastColor(builder.toString())).append(word).append(" ");

		// Trim trailing whitespace
		String result = builder.toString().replaceFirst("\\s++$", "");
		if (text.endsWith(" ")) result += " ";
		return result;
	}

	public static boolean isNullOrEmpty(String id) {
		return (id == null || id.isEmpty());
	}
}
