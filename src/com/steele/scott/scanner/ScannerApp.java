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
		String print;
		try 
		{
			tokens = scanner.scanForTokens();
			while (!tokens.isEmpty())
			{
				print = tokens.poll();
				if (print != null)
				{
					System.out.println(print);
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("[ERROR] Unexpected error scanning string: abort");
			e.printStackTrace();
		}
	}
}
