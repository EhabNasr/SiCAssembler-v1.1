/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ehab
 */
public class Literal {
    protected String name;
    protected int value;
    protected char[] charsValue;
    protected int size;
    protected Address address;
    protected boolean charFlag;
    public Literal(String name){
        this.name = name;
        if(this.name.indexOf("X") != -1){
            this.charFlag = false;
            String temp =  this.name.substring(this.name.indexOf("'")+1, this.name.indexOf("'", this.name.indexOf("'")+1));
            this.value = Integer.decode("0x" + temp);
            this.size = temp.length()/2;
            if((temp.length() % 2) == 1) this.size++;
        }else if(this.name.charAt(1) == 'C'){
            this.charFlag = true;
            String temp =  this.name.substring(this.name.indexOf("'")+1, this.name.indexOf("'", this.name.indexOf("'")+1));
            this.charsValue = temp.toCharArray();
            this.size = temp.length();
        }
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the value
     */
    public int getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(int value) {
        this.value = value;
    }

    /**
     * @return the size
     */
    public int getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * @return the address
     */
    public Address getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(Address address) {
        this.address = address;
    }
}
