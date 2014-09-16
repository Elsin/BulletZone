package net.cserna.bence.bulletzone.entity;

public class Wall extends FieldEntity {

	@Override
	public FieldEntity copy() {
		return new Wall();
	}
	
	@Override
	public int getIntValue() {
		return 1000;
	}
	
	@Override
	public String toString() {
		return "W";
	}

}
