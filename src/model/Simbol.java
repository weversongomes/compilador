package model;

public class Simbol {
	String name, value, type;
	
	boolean equals(String name) {
        if (name == this.name)
        	return true;
        return false;
    }
}
