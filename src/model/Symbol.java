package model;

public class Symbol {
	public String name, value, type;
	public boolean isConstant = false;
	
	public boolean equals(Object obj) {
		Symbol symbol = (Symbol) obj;
		String name = symbol.name;
        if (name.equals(this.name))
        	return true;
        return false;
    }
}
