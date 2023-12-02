public class StoreBuffer {
    private int instructionId;
    private String reservationAreaId;
    private String Address;
    private float value;
    private String queue;
    private int busy;

    public StoreBuffer(int instructionId,String reservationAreaId, int busy, String Address, float value, String queue) {
        this.instructionId = instructionId;
        this.reservationAreaId = reservationAreaId;
        this.Address = Address;
        this.value = value;
        this.queue = queue;
        this.busy = busy;
    }

    public int getInstructionId() {
        return instructionId;
    }

    public String getReservationAreaId() {
        return reservationAreaId;
    }

    public String getAddress() {
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

    public void setAddress(String Address) {
        this.Address = Address;
    }

    public void setInstructionId(int instructionId) {
        this.instructionId = instructionId;
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

    public void setReservationAreaId(String reservationAreaId) {
        this.reservationAreaId = reservationAreaId;
    }
}
