public class LoadBuffer {
    int Address;
    int busy;


    public LoadBuffer(int Address, int busy) {
        this.Address = Address;
        this.busy = busy;
    }

    public int getAddress() {
        return Address;
    }

    public int getBusy() {
        return busy;
    }

    public void setAddress(int Address) {
        this.Address = Address;
    }

    public void setBusy(int busy) {
        this.busy = busy;
    }

    
}
