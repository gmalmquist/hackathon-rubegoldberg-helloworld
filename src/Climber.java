
public class Climber {
	private Instance current;
	private Instance best;
	
	public Climber(Instance start) {
		current = start.copy();
		best = current.copy();
		restart();
	}
	
	public void restart() {
		current = current.random();
	}
	
	public void iterate() {
		current = this.best;
		
		Instance best = current;
		// let's visit 100 neighbors and choose the best
		for (int i = 0; i < 1000; i++) {
			Instance n = current.neighbor();
			if (n.utility() > best.utility() || Math.random() < 0.0001 * i) {
				best = n;
			}
		}
		
		if (best.utility() > this.best.utility()) {
			this.best = best.copy();
		}
		
		this.current = best;
	}
	
	public Instance best() {
		return best;
	}
	
	public Instance current() {
		return current;
	}
}
