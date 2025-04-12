package tech.blastmc.holograms.utils;

public record Percentage(byte value) {

	public Percentage(byte value) {
		this.value = (byte) Math.min(100, Math.max(0, value));
	}

	public byte getSignedValue() {
		return (byte) (int) Math.round(25 + (value / 100.0) * (255 - 25));
	}

}
