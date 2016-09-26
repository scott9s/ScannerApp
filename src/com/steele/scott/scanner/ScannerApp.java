package com.steele.scott.scanner;

import java.util.Queue;
/**
 * 
 * @author scottsteele
 *
 */
public class ScannerApp {
	
	/**
	 * A simple calculator language scanner
	 * Takes either input calculation either from prompt,
	 * scans characters into parsable tokens, 
	 * and prints tokens to screen.
	 */
	public static void main(String[] args) 
	{
		Scanner scanner = new Scanner();
		Queue<String> tokens = null;
		try 
		{
			tokens = scanner.scanForTokens();
			System.out.println("Thank you. Outputting Tokens:");
			while (!tokens.isEmpty())
			{
				System.out.println("\t" + tokens.poll());
			}
		}
		catch (Exception e)
		{
			System.out.println("[ERROR] Unexpected error scanning string: abort");
			e.printStackTrace();
		}
	}
}
