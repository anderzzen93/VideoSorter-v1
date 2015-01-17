package VideoSorter;

import java.util.List;
import java.util.Stack;

public class SearchStringInterpreter {

	/**
	 * Konstruktören
	 */
	public SearchStringInterpreter(){
		
	}
	/**
	 * Tolkar och utför den tillhandahållna strängen 
	 * @param terms Söksträngen 
	 * @param videos Listan med videos att utföra sökningen på 
	 * @param specificTerm Specifikt fält att utföra sökningen i. Gener, År etc. 
	 * @return Lista med utgallrade videos enligt sökningen.
	 */
	public List<Video> interpretString(String terms, List<Video> videos, String specificTerm){
		
		 Stack<String> lexed = stringLex(terms.toLowerCase());
		 Stack<String> postFixed = stringPostfix(lexed);
		 
		 for (String s : postFixed){
			 System.out.println(s);
		 }
		  
		 return evaluatePostfix(postFixed, videos, specificTerm);
	}
	/**
	 * Utför sökningan och gallringen av videos
	 * @param terms Termer att söka med 
	 * @param videos Videos att söka bland 
	 * @param specificTerm Fält i metadatan att söka i. Genre, År etc.
	 * @return Resultatet av sökningen som en List<Videos>
	 */
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
	/**
	 * Gör ett snitt av två stack med Video
	 * @param a Ena stacken med Video 
	 * @param b Andra stacken med Video
	 * @return Snittet av de två stackaren som en stack
	 */
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
	/**
	 * Skapar en union av två Stack<Video>
	 * @param a ena Stack<Video>
	 * @param b Andra Stack<Video>
	 * @return Unionen som stack
	 */
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
	/**
	 * Skapar en stack som komplement 
	 * @param a Stack att kompletera 
	 * @param universe Alla Videoobjekt att komplettera med.
	 * @return Komplementet till a som Stack<Video>
	 */
	private Stack<Video> complement(Stack<Video> a, List<Video> universe){
		Stack<Video> result = new Stack<Video>();
		
		for(Video v : universe){
			if (!a.contains(v)){
				result.add(v);
			}
		}
		
		return result;
	}
	/**
	 * Delar upp operander och operatorer
	 * @param terms Söksträng att dela 
	 * @return Operander och operatorer separerade i en Stack<String>
	 */
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
			else if (terms.charAt(i) == ' '){
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

		Stack<String> result2 = new Stack<String>();

		while (!result.empty()){
			result2.push(result.pop().replace('_', ' '));
		}

		return result2;
	}
	/**
	 * Postfixar söksträngen
	 * @param terms Den lexade söksträngen
	 * @return Stack<String> ordnad efter postfixnotation
	 */
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
	
	/**
	 * Styr prioriteten för operatorerna
	 * @param operator Operator att kolla prioritet för
	 * @return Int representerande prioriteten större=högre
	 */
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
	/**
	 * Kollar om termen är en operator
	 * @param term term att kontrollera
	 * @return true eller false
	 */
	private boolean isOperator(String term){
		term = term.toLowerCase();
		
		return term.equals("not") || term.equals("and") || term.equals("or");
	}
	/**
	 * Kollar om termen är parentes
	 * @param term Term att kolla
	 * @return true om sant annars false
	 */
	private boolean isParenthesis(String term){
		
		return term.equals("(") || term.equals(")");
	}
	/**
	 * Kollar om termen är operand
	 * @param term term att kontrollera
	 * @return true om sant annars false
	 */
	private boolean isOperand(String term){
		return !isOperator(term) && !isParenthesis(term);
	}
}
