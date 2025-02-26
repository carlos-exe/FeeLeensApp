package data;

public class Classification {
	private String name;
	private Integer value;
	
	public Classification(String name, Integer value) {
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}
	
	public Integer getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return "Label: " + name + ", " + "value: " + value;
	}
}
