import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class SemanticChecks implements BasicLVisitor{

	private final static String GLOBAL_SCOPE = "GLOBAL_SCOPE";
	private static final LinkedHashSet<String> FunctionsCalled = new LinkedHashSet<String>();
	private static final LinkedHashSet<String> FunctionsDeclared = new LinkedHashSet<String>();
	/*Create HashMap for storing scope and STC for each scope*/
	private static HashMap<String, HashMap<String, STC>> ST = new HashMap<String, HashMap<String, STC>>(); 
    private static String oldScope = GLOBAL_SCOPE; /* oldscope and scope global at start of program*/
    private static String scope = GLOBAL_SCOPE;
    private static int scopeCount = 0;//keep track of what scope program is in.
	
	@Override
	public Object visit(SimpleNode node, Object data) {
		node.childrenAccept(this, data);
		return null;
	}

	@Override
	public Object visit(ASTProgram node, Object data) {
		
		ST.put(scope, new HashMap<String,STC>());
		
		node.childrenAccept(this, data);
		
		
		
		Set<String> keys = ST.keySet();
		System.out.println();
		System.out.printf("%-15s%-15s%-15s%-15s%-15s%n", "Kind", "Type", "Identifier", "Scope", "Data");

		/* Go through each scope and test each semantic checks*/
		for(String key:keys){
			
			/*get the symbol table within the scope and go through each of the entries */
			HashMap <String, STC> scopeST = ST.get(key);
			Set<String> scopeKeys = scopeST.keySet();
			
			for(String scopeKey:scopeKeys){
				STC stChild = scopeST.get(scopeKey);
				System.out.printf("%-15s%-15s%-15s%-15s%-15s%n", stChild.getKind(), stChild.getType(), stChild.getID(), scopeKey, stChild.getData());
			}
		}
		
		/*Check if all functions declared are actually used*/
		if(! (FunctionsDeclared.size() == FunctionsCalled.size())){
			System.out.println("All declared functions were not called!");
		}else{
			System.out.println("All declared functions were called!");
		}

		/* Go through each scope and test each semantic checks*/
		for(String key:keys){
			
			/*get the symbol table within the scope and go through each of the entries */
			HashMap <String, STC> scopeST = ST.get(key);
			Set<String> scopeKeys = scopeST.keySet();
			
			for(String scopeKey:scopeKeys){
				STC stChild = scopeST.get(scopeKey);
				if(stChild.getKind() != DataType.FUNCTION){
					if(stChild.getData("writtenTO") == null && stChild.getData("readedFrom") == null){
						System.out.println(stChild.getID() + " in scope " + stChild.getScope() + "not written to and not read from");
					}else if(stChild.getData("writtenTO") == null && stChild.getData("readedFrom") == true){
						System.out.println(stChild.getID() + " in scope " + stChild.getScope() + "not written to");
					}else if(stChild.getData("writtenTO") == true && stChild.getData("readedFrom") == null){
						System.out.println(stChild.getID() + " in scope " + stChild.getScope() + "not read from");
					}else if(stChild.getData("writtenTO") == true && stChild.getData("readedFrom") == true){
						System.out.println(stChild.getID() + " in scope " + stChild.getScope() + "is written to and read from");
					}
				}
			}
		}
		
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object visit(ASTvar_decl node, Object data) {
		
		/*Get all the names for each of the variables of the same type*/
		List<Token> idList = (List<Token>) node.jjtGetChild(0).jjtAccept(this, data);
		
		/*get the type of var been declared*/
		Token type = (Token) node.jjtGetChild(1).jjtAccept(this, data);
		
		/*get each id in list*/
		for(Token id :idList){
			/*get the scope map that var needs to be added to*/
			HashMap<String, STC> scopeST = ST.get(scope);
			/* if this is the first scope of it add a new HashMap entry for it*/
			if(scopeST == null){
				scopeST = new HashMap<String, STC>();
			}
			/* create var to be placed in scope*/
			STC var = new STC(id,scope,DataType.VAR,type);
			if(scopeST.get(id.image) == null){
				scopeST.put(id.image, var);
				ST.put(scope, scopeST);
			}else{/*Error message if the scope already has this var declared*/
				System.out.println("Error: id "+id.image+" already declared in " + scope);
			}
		}
		
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object visit(ASTconst_decl node, Object data) {
		
		/*Get the scope that const_decl needs to be added to.*/
		HashMap<String,STC> scopeST = ST.get(scope);
		
		if(scopeST == null){
			scopeST = new HashMap<String, STC>();
		}
		/*go through each grouping of const decls*/
		for(int i = 0; i<node.jjtGetNumChildren(); i+=3){
			/*Get id of const var been created*/
			Token id = (Token) node.jjtGetChild(i).jjtAccept(this, data);
			/*Get type of the const var been created*/
			Token type = (Token) node.jjtGetChild(i+2).jjtAccept(this, data);
			/*Get the value been assigned to the const var*/
			List<Token> assign;
			
			if(node.jjtGetChild(i).jjtAccept(this, data) instanceof Token){
				assign = new ArrayList<Token>();
				assign.add((Token) node.jjtGetChild(i).jjtAccept(this, data));/**/
			}else{
				assign = ((List<Token>) node.jjtGetChild(i).jjtAccept(this, data)); /**/
			}
			
			STC constVar = new STC(id,scope,DataType.CONST,type);
			
			if(scopeST.get(id.image) == null){
				scopeST.put(id.image, constVar);
				ST.put(scope, scopeST);
			}else{/*Error message if the scope already has this var declared*/
				System.out.println("Error: id "+id.image+" already declared in " + scope);
			}
		}
		
		return null;
	}

	@Override
	public Object visit(ASTfunctionDecl node, Object data) {
		
		/*Get return type of function*/
		Token type = (Token) node.jjtGetChild(0).jjtAccept(this, data);
		
		
		return null;
	}

	@Override
	public Object visit(ASTfunctionBody node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTParamList node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTType node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTMain node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTfunctionCall node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	

	@Override
	public Object visit(ASTaddFragment node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTsubFragment node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTcondition node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTIdent_list node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTArgument_List node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTId node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTNum node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTBool node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTPlusExpression node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTMinusExpression node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTMultExpression node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTDivExpression node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

}
