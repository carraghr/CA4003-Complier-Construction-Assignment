import java.util.HashMap;

public class STC extends Object{
	private HashMap<String,Object> table;
	private Token id; /*name of variable*/ 
	private DataType kind; /*type of variable var, const, or function*/
	private String scope; /*scope*/
	private Token type; /* type of variable*/
	
	public STC(Token id, String scope, DataType kind){/* STC constructor for none variable type*/
		this.id = id;
		this.scope = scope;
		this.table = new HashMap<String,Object>();
		this.kind = kind;
	}
	
	public STC(Token id, String scope, DataType kind, Token type){
		this.id = id;
		this.scope = scope;
		this.table = new HashMap<String,Object>();
		this.kind = kind;
		this.type = type;
	}
	
	public void addData(String key, Object value){
		table.put(key, value);
	}
	
	public Object getData(String key){
		return table.get(key);
	}
	
	public HashMap<String,Object> getData(){
		return table;
	}
	
	Token getID(){
		return this.id;
	}
	
	void setID(Token id){
		this.id = id;
	}
	
	DataType getKind(){
		return this.kind;
	}
	
	String getScope(){
		return this.scope;
	}
	
	Token getType(){
		return this.type;
	}
	
	void setType(Token type){
		this.type = type;
	}
	
	public String toString(){
		return this.type + " " + this.id + " " + this.scope + " " + this.table;
	}
}
