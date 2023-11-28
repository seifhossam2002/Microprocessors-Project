public class StoreBuffer {
    int Address;
    float value;
    String queue;
    int busy;

    public StoreBuffer(int Address, float value, String queue, int busy) {
        this.Address = Address;
        this.value = value;
        this.queue = queue;
        this.busy = busy;
    }

    public int getAddress() {
        return Address;
    }

    public float getValue() {
        return value;
    }

    public String getQueue() {
        return queue;
    }

    public int getBusy() {
        return busy;
    }

    public void setAddress(int Address) {
        this.Address = Address;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public void setBusy(int busy) {
        this.busy = busy;
    }

    
}
