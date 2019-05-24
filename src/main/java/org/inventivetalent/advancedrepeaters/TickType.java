package org.inventivetalent.advancedrepeaters;

public enum TickType {

	GAME("GT", "game ticks", 1),
	REDSTONE("RT", "redstone ticks", 2),
	SECOND("SC", "seconds", 20),
	MINUTE("MN", "minutes", 1200);

	public String shortName;
	public String name;
	public long   multiplier;

	TickType(String shortName, String name, long multiplier) {
		this.shortName = shortName;
		this.name = name;
		this.multiplier = multiplier;
	}

	public long getTicks(long ticks) {
		return ticks * multiplier;
	}

	public static TickType parseString(String string) throws InvalidTickTypeException {
		for (TickType type : values()) {
			if (type.shortName.equalsIgnoreCase(string)) { return type; }
		}
		throw new InvalidTickTypeException(string);
	}

	public static long parseTicks(String string) throws InvalidTickTypeException, NumberFormatException {
		String typeString = string.substring(string.length() - 2);
		TickType type = parseString(typeString);

		String tickString = string.substring(0, string.length() - 2);
		long ticks = Long.parseLong(tickString);

		return type.getTicks(ticks);
	}

	public static String makeTypeString() {
		StringBuilder stringBuilder = new StringBuilder();
		for (TickType type : values()) {
			stringBuilder.append(", ");
			stringBuilder.append(type.shortName);
			stringBuilder.append("(");
			stringBuilder.append(type.name);
			stringBuilder.append(")");
		}
		return stringBuilder.substring(2);
	}

	public static class InvalidTickTypeException extends RuntimeException {
		public InvalidTickTypeException(String type) {
			super(type + " is not a valid tick type");
		}
	}

}
