package com.steele.scott.scanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * 
 * @author scottsteele
 *
 */
public class Scanner {

	private Stack<Character> buffer;
	private Queue<String> tokens;
	private Character c;
	
	Scanner()
	{
		this.buffer = new Stack<Character>();
		this.tokens = new LinkedList<String>();
		this.c = ' '; 
	}
	
	/**
	 * 
	 * @param buffer a stack loaded with a String
	 * @return tokens a Queue loaded with parsable tokens
	 */
	public Queue<String> scanForTokens()
	{
		loadBuffer();
		try 
		{
			// while there are still chars to process off the stack
			while (!buffer.isEmpty() && c != null)
			{
				// pop next char
				c = buffer.pop(); 
				if (c != null)
				{
					// ignore whitespaces
					if ( Character.isWhitespace(c))				
					{
						continue;
					}
					// identify parens and add to queue
					if (c == '(' )
					{
						tokens.add("lparen");
						continue;
					}
					if (c == ')')
					{
						tokens.add("rparen");
						continue;
					}
					// identify non-division operators and add to queue
					if (c == '+')
					{
						tokens.add("plus");
						continue;
					}
					if (c == '-')
					{
						tokens.add("minus");
						continue;
					}
					if (c == '*')
					{
						tokens.add("times");
						continue;
					}
					// identify assignments
					else if (c == ':')
					{ 
						// if next char will make ":=" then pop it and add assign to queue
						if (!buffer.isEmpty() && buffer.peek() == '=')
						{
							buffer.pop();
							tokens.add("assign");
						}
						// else something is wrong, throw a fit
						else
						{
							throw new Exception("[ERROR] token unknown for :" + c); 
						}
					}
					// strip out comments and identify division operator
					else if (c == '/')
					{
						// check if it is a // style comment
						if (!buffer.isEmpty() && buffer.peek() == '/' ) 				
						{
							// if so strip out comment chars
							do
							{
								buffer.pop();
							} while (!buffer.isEmpty() && buffer.peek() != '\n'); 
						}
						// check if it is a /**/ style comment
						else if (!buffer.isEmpty() && buffer.peek() == '*') 			
						{
							/* if so strip out comments */
							do
							{
								do
								{
									buffer.pop();
								} while (!buffer.isEmpty() && buffer.peek() != '*');
								if (!buffer.isEmpty())
								{
									buffer.pop();
								}
								if (!buffer.isEmpty() && buffer.peek() == '/')
								{
									buffer.pop();
									break;
								}
							} while (!buffer.isEmpty()); 
						}
						// otherwise, it's a division operator, add to queue
						else
						{
							tokens.add("div");
						}
					}
					// identify numbers leading with a decimal point
					else if (c == '.')
					{
						// if the next char is a digit
						if (!buffer.isEmpty() && Character.isDigit(buffer.peek()) )
						{
							// pop off any remaining digits
							do
							{
								// pop and check for second '.'
								if ('.' == buffer.pop())
								{
									throw new Exception("[ERROR] multiple decimal points in single number");
								}
							} while (!buffer.isEmpty() && Character.isDigit(buffer.peek())); 
							tokens.add("number");
						}
						// if the next char isn't a digit, then something is wrong: throw a fit
						else
						{
							throw new Exception("[ERROR] token unknown for ." + c); 
						}
					}
					// identify numbers leading with a digit
					else if (Character.isDigit(c))
					{
						// set hasDeci[mal point] flag to false
						Boolean hasDeci = false;
						do
						{
							// if the next char is a '.' and one had already been encountered, throw a fit
							if (!buffer.isEmpty() && buffer.peek() == '.' && hasDeci)
							{
								throw new Exception("[ERROR] multiple decimal points in single number"); 
							}
							// if the next char is a '.', set hasDeci flag to true and call pop
							else if (!buffer.isEmpty() && buffer.peek() == '.' && !hasDeci)
							{
								hasDeci = true;
								buffer.pop();
							}
							// pop the next char if it's a digit
							else if (!buffer.isEmpty() && Character.isDigit(buffer.peek()))
							{
								buffer.pop();
							}
							// do this while the next char is a digit or a '.'
						} while (!buffer.isEmpty() && (Character.isDigit(buffer.peek()) || buffer.peek() == '.' ));
						// push token onto queue
						tokens.add("number");
					}
					// identify read, write, or id's
					else if (Character.isAlphabetic(c))
					{
						// check for read
						if (isRead()) 
						{
							tokens.add("read");
							continue;
						}
						// check for write
						else if (isWrite())
						{
							tokens.add("write");
							continue;
						}
						// else remove any remaining alphabetic characters
						while (!buffer.isEmpty() && Character.isAlphabetic(buffer.peek()))
						{
							buffer.pop();
						} 
						tokens.add("id"); // must be an id
					}
					// if here unrecognizable token. Throw fit
					else
					{
						throw new Exception("[ERROR] token unknown. Multiple decimal points in single number"); 
					}
				}
			}
		}
		catch (Exception e)
		{
			e.getMessage();
			e.printStackTrace();
		}
		return tokens;
	}
	
	/**
	 * loads input String into a Character stack
	 */
	public void loadBuffer()
	{
		StringBuilder input = new StringBuilder(getInput());
		input.reverse(); // load stack last char first
		for (int i = 0; i < input.length(); i++)
		{
			buffer.push(input.charAt(i));
		}
	}
	
	/**
	 * Prompts for an input string
	 * @return input 
	 */
	private String getInput()
	{
		BufferedReader reader = null;
		String input = "";
        try {
        	reader = new BufferedReader(new InputStreamReader(System.in));
        	System.out.print("Please enter an expression to calculate: ");
        	input = reader.readLine();
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        }
        finally
        {
        	try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        return input;
	}
	
	private Boolean isRead()
	{
		// is the starting char r?
		if (c == 'r' || c == 'R')
		{
			String read = c.toString();
			// while the following char seq is a substring of "read", continue popping chars off stack and adding them to read variable
			 while ("read".contains(read) && read.length() <= 4)
			{
				// if next char is alphabetic
					if (!buffer.isEmpty() && Character.isAlphabetic(buffer.peek()) )
					{
							read += buffer.pop();
					}
					// if next char isn't alphabetic or stack is empty, break to final comparison
					else
					{
						break;
					}
			}	
			// if sequence equals read and the next character is NOT alphabeticall (might be prefix to a longer id, ex. reader or readasdfadsf) 
			 if (read.contentEquals( "read") )
				{
					// and the next character is NOT alphabetical (might be prefix to a longer id, ex. writer or writerPaperBack) 
					if (!buffer.isEmpty() && Character.isAlphabetic(buffer.peek()))
					{
						return false; 
					}
					// else write is the last token to pull from stack, return true (regardless of syntax error)
					else
					{
						return true;
					}
				}
		}
		return false;		// all other outcomes are false
	}
	
	private Boolean isWrite()
	{
		// is the starting char w?
		if (c == 'w' || c == 'W')
		{
			String write = c.toString();
			// while the following char sequence is a substring of "write", continue popping chars off stack and adding them to read variable
			while ("write".contains(write) && write.length() <= 5 )
			{
				// if next char is alphabetic
				if (!buffer.isEmpty() && Character.isAlphabetic(buffer.peek()) )
				{
						write += buffer.pop();
				}
				// if next char isn't alphabetic or stack is empty, break to final comparison
				else
				{
					break;
				}
			}	
			// if sequence equals write 
			if (write.contentEquals( "write") )
			{
				// and the next character is NOT alphabetical (might be prefix to a longer id, ex. writer or writerPaperBack) 
				if (!buffer.isEmpty() && Character.isAlphabetic(buffer.peek()))
				{
					return false; 
				}
				// else write is the last token to pull from stack, return true (regardless of syntax error)
				else
				{
					return true;
				}
			}
		}
		return false; 		//if it ain't write it's wrong
	}
}


