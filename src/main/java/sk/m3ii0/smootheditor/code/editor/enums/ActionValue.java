package sk.m3ii0.smootheditor.code.editor.enums;

public enum ActionValue {
	
	PLUS(1),
	MINUS(-1);
	
	ActionValue(double operator) {
		this.operator = operator;
	}
	
	private final double operator;
	
	public double parse(double var) {
		return var*operator;
	}
	
}
