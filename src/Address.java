
public class Address {
    private String address;

    public Address() {
        this.address = "";
    }

    public Address(int dec_value) {
        this.address = Integer.toHexString(dec_value);
    }

    public Address add(int dec_value) {
        Address ans = new Address();
        ans.setAddress(Integer.decode("0x" + address) + dec_value);
        return ans;
    }

    public int getAddress_dec() {
        return Integer.decode(address);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String hex_String) {
        this.address = hex_String;
    }

    public void setAddress(int dec_value) {
        this.address = Integer.toHexString(dec_value);
    }

}
