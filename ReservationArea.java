public class ReservationArea {
    int instructionId;
    String reservationAreaId;
    String opcode;
    float value_j;
    float value_k;
    String queue_j;
    String queue_k;
    int busy;

    public ReservationArea(int instructionId, String reservationAreaId, String opcode, float value_j, float value_k,
            String queue_j, String queue_k, int busy) {
        this.instructionId = instructionId;
        this.reservationAreaId = reservationAreaId;
        this.opcode = opcode;
        this.value_j = value_j;
        this.value_k = value_k;
        this.queue_j = queue_j;
        this.queue_k = queue_k;
        this.busy = busy;
    }

    public int getInstructionId() {
        return instructionId;
    }

    public String getReservationAreaId() {
        return reservationAreaId;
    }

    public String getOpcode() {
        return opcode;
    }

    public float getValue_j() {
        return value_j;
    }

    public float getValue_k() {
        return value_k;
    }

    public String getQueue_j() {
        return queue_j;
    }

    public String getQueue_k() {
        return queue_k;
    }

    public int getBusy() {
        return busy;
    }

    public void setInstructionId(int instructionId) {
        this.instructionId = instructionId;
    }

    public void setReservationAreaId(String reservationAreaId) {
        this.reservationAreaId = reservationAreaId;
    }

    public void setValue_j(float value_j) {
        this.value_j = value_j;
    }

    public void setValue_k(float value_k) {
        this.value_k = value_k;
    }

    public void setQueue_j(String queue_j) {
        this.queue_j = queue_j;
    }

    public void setQueue_k(String queue_k) {
        this.queue_k = queue_k;
    }

    public void setBusy(int busy) {
        this.busy = busy;
    }

    @Override
    public String toString() {
        return "ReservationArea [instructionId=" + instructionId + "reservationAreaId" + reservationAreaId + ", opcode="
                + opcode + ", value_j=" + value_j + ", value_k=" + value_k
                + ", queue_j=" + queue_j + ", queue_k=" + queue_k + ", busy=" + busy + "]";
    }

}
