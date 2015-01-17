package VideoSorter;

import java.util.List;
import java.util.Stack;

public class SearchStringInterpreter {

	public SearchStringInterpreter(){
		
	}
	
	public List<Video> interpretString(String terms, List<Video> videos, String specificTerm){
		
		 Stack<String> lexed = stringLex(terms.toLowerCase());
		 Stack<String> postFixed = stringPostfix(lexed);
		 
		 for (String s : postFixed){
			 System.out.println(s);
		 }
		  
		 return evaluatePostfix(postFixed, videos, specificTerm);
	}
	
	private List<Video> evaluatePostfix(Stack<String> terms, List<Video> videos, String specificTerm){
		
		Stack<String> wait = new Stack<String>();
		Stack<Stack<Video>> operandWait = new Stack<Stack<Video>>();
		
		String[] tokens = new String[terms.size()];
		
		int index = 0;
		for (String s : terms){
			tokens[index++] = s;
		}
		
		for (int i = 0; i < tokens.length; i++){
			if (isOperand(tokens[i])){
				wait.push(tokens[i]);
			} else if(isOperator(tokens[i])){
				if (tokens[i].equals("and")){
					if (operandWait.isEmpty()){
						String operand1 = wait.pop();
						String operand2 = wait.pop();
						operandWait.push(new Stack<Video>());
						for (Video v : videos){
							if (v.metaData.anyMatch(operand1, specificTerm) && v.metaData.anyMatch(operand2, specificTerm))
								operandWait.peek().push(v);
						}
					}else{
						String match = wait.pop();
						operandWait.push(new Stack<Video>());
						for (Video v : videos){
							if (v.metaData.anyMatch(match, specificTerm)){
								operandWait.peek().push(v);
							}
						}
						operandWait.push(intersect(operandWait.pop(), operandWait.pop()));
					}
				} else if(tokens[i].equals("or")){
					if (operandWait.isEmpty()){
						String operand1 = wait.pop();
						String operand2 = wait.pop();
						operandWait.push(new Stack<Video>());
						for (Video v : videos){
							if (v.metaData.anyMatch(operand1, specificTerm) || v.metaData.anyMatch(operand2, specificTerm))
								operandWait.peek().push(v);
						}
					} else{
						String match = wait.pop();
						operandWait.push(new Stack<Video>());
						for (Video v : videos){
							if (v.metaData.anyMatch(match, specificTerm)){
								operandWait.peek().push(v);
							}
						}
						operandWait.push(union(operandWait.pop(), operandWait.pop()));
					}
				} else if (tokens[i].equals("not")){
					if (operandWait.isEmpty()){
						String operand = wait.pop();
						operandWait.push(new Stack<Video>());
						for (Video v : videos){
							if (!v.metaData.anyMatch(operand, specificTerm)){
								operandWait.peek().push(v);
							}
						}
					} else{
						operandWait.push(complement(operandWait.pop(), videos));
					}
				}
			}
		}
		Stack <Video>  temp = new Stack <Video>();
		Stack <Video>  temp2 = new Stack <Video>();
		
		for (Stack<Video> v : operandWait){
			for (Video q : v){
				temp.push(q);
			}
		}
		
		for (String v : wait){
			for (Video q : videos){
				if (q.getMetaData().anyMatch(v, specificTerm)){
					temp2.push(q);
				}
			}
		}
		
		if (temp2.isEmpty()){
			return temp;
		} else if (temp.isEmpty()){
			return temp2;
		} else{
			intersect(temp, temp2);
		}
		
		return temp2.isEmpty() ? temp : intersect (temp, temp2); 
	}
	
	private Stack<Video> intersect(Stack<Video> a, Stack<Video> b){
		Stack<Video> result = new Stack<Video>();
		
		for (Video v : a){
			for (Video v2 : b){
				if (v.equals(v2))
					result.push(v);
			}
		}
		
		return result;
	}
	
	private Stack<Video> union(Stack<Video> a, Stack<Video> b){
		Stack<Video> result = new Stack<Video>();
		
		for (Video v : a){
			result.push(v);
		}
		
		for (Video v : b){
			if (!result.contains(v))
				result.push(v);
		}
		
		return result;
	}
	
	private Stack<Video> complement(Stack<Video> a, List<Video> universe){
		Stack<Video> result = new Stack<Video>();
		
		for(Video v : universe){
			if (!a.contains(v)){
				result.add(v);
			}
		}
		
		return result;
	}
 
	private Stack<String> stringLex(String terms){

		Stack<String> result = new Stack<String>();
		String buffer = "";
		
		for (int i = 0; i < terms.length(); i++){
			if (terms.charAt(i) == '(' || terms.charAt(i) == ')'){
				if (buffer.length() != 0){
					result.push(buffer);
					buffer = "";
				} result.push(new String( new char[]{terms.charAt(i)}));
			}
			else if (terms.charAt(i) == ' ' && isNextOperator(i, terms)){
				if (buffer.length() != 0){
					result.push(buffer);
					buffer = "";
				}
			}else{
				buffer += terms.charAt(i);
			}
		}
		if (buffer.length() != 0){
			result.push(buffer);
		}
		return result;
	}
	
	private boolean isNextOperator(int index, String terms){
		
		if (isLastOperand(index, terms))
			return true;
		
		if (index + 3 < terms.length()){
			if (terms.substring(index + 1, index + 4).equals("or ")){
				return true;
			}
		} else{
			return true;
		}
		
		if (index + 4 < terms.length()){
			String substr = terms.substring(index + 1, index + 5);
			if (substr.equals("and ") || substr.equals("not "))
				return true;
		} else{
			return true;
		}
		
		return false;
	}
	
	private boolean isLastOperand(int index, String terms){
		String substr = terms.substring(index, terms.length());
		return !substr.contains(" and ") && !substr.contains(" or ") && !substr.contains(" not ");
	}

	private Stack<String> stringPostfix(Stack<String> terms){
		
		Stack<String> out = new Stack<String>();
		Stack<String> wait = new Stack<String>();
		
		String[] tokens = new String[terms.size()];
		
		int index = 0;
		for (String s : terms){
			tokens[index++] = s;
		}

		for (int i = 0; i < tokens.length; i++){
			if (isOperand(tokens[i])){
				out.push(tokens[i]);
			} else if (isOperator(tokens[i])){
				if (!wait.isEmpty() && getPriority(tokens[i]) <= getPriority(wait.peek())){
					if (!tokens[i].equals("not") && !wait.peek().equals("not")){	
						while(!wait.isEmpty() && getPriority(tokens[i]) <= getPriority(wait.peek())){
							out.push(wait.pop());
						}
					}
					wait.push(tokens[i]);
				} else{
					wait.push(tokens[i]);
				}
			} else if (isParenthesis(tokens[i])){
				if (tokens[i].equals("(")){
					wait.push(tokens[i]);
				} else if (tokens[i].equals(")")){
					String token = "";
					while (!(token = wait.pop()).equals("(")){
						out.push(token);
					}
				}
			}
		}
		
		while(!wait.isEmpty()){
			out.push(wait.pop());
		}
		System.out.println(out);
		return out;
	}
	
	private int getPriority(String operator){
		operator = operator.toLowerCase();
		
		if(operator.equals("not")){
			return 3;
		} else if (operator.equals("and") || operator.equals("or")){
			return 1;
		} else{
			return 0; //Ska aldrig hända
		}
	}
	
	private boolean isOperator(String term){
		term = term.toLowerCase();
		
		return term.equals("not") || term.equals("and") || term.equals("or");
	}
	
	private boolean isParenthesis(String term){
		
		return term.equals("(") || term.equals(")");
	}
	
	private boolean isOperand(String term){
		return !isOperator(term) && !isParenthesis(term);
	}
}
