package net.cserna.bence.bulletzone.entity;

/**
 * @author Bence Cserna (bence@cserna.net)
 */

public class NumberField extends FieldEntity {

	@SuppressWarnings("unused")
	private static final String TAG = "NumberField";
	private final int value;

	public NumberField(int value) {
		this.value = value;
	}

	@Override
	public int getIntValue() {
		return 0;
	}

	@Override
	public FieldEntity copy() {
		return null;
	}

	@Override
	public String toString() {
		return Integer.toString(value == 1000 ? 1 : 2);
	}
	
}
