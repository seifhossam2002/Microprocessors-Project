public class LoadBuffer {
    private int instructionId;
    private String reservationAreaId;
    private String Address;
    private int busy;

    public LoadBuffer(int instructionId,String reservationAreaId,int busy, String Address) {
        this.instructionId = instructionId;
        this.reservationAreaId = reservationAreaId;
        this.Address = Address;
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

    public int getBusy() {
        return busy;
    }

    public void setAddress(String Address) {
        this.Address = Address;
    }

    public void setBusy(int busy) {
        this.busy = busy;
    }

    public void setInstructionId(int instructionId) {
        this.instructionId = instructionId;
    }

    public void setReservationAreaId(String reservationAreaId) {
        this.reservationAreaId = reservationAreaId;
    }

}
