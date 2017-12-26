package model;

public class Simbol {
	public String name, value, type;
	public boolean isConstant = false;
	
	public boolean equals(Object obj) {
		Simbol simbol = (Simbol) obj;
		String name = simbol.name;
        if (name == this.name)
        	return true;
        return false;
    }
}
