
public interface Instance {
	public Instance copy();
	public Instance neighbor();
	public Instance random();
	public double utility();
	public String name();
}
