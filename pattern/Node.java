package scripts.api.pattern;

public abstract class Node {

    public static BaseScript script;

    public Node(BaseScript script) { this.script = script; }

    public abstract void execute();

    public abstract boolean validate();
	
}