public class Register {
	private String name;
	private float value;
	private String queue;

	public Register(String name, float value, String queue) {
		super();
		this.name = name;
		this.value = value;
		this.queue = queue;
	}

	public String getName() {
		return name;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		if (!this.name.equals("R0"))
			this.value = value;
	}

	public String getQueue() {
		return queue;
	}

	public void setQueue(String queue) {
		this.queue = queue;
	}

	@Override
	public String toString() {
		return "Register [name=" + name + ", value=" + value + "queue"+queue+"]";
	}
}