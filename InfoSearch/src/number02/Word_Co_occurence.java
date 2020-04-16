package number02;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Word_Co_occurence {
	private static List<String[]> docArrays = new ArrayList<>();

	public static void main(String[] args) {
		Word_Co_occurence word = new Word_Co_occurence();

		String path = System.getProperty("user.dir");
		Scanner scan = new Scanner(System.in);
		int windowSize = 2; // 윈도우 크기
		
		System.out.print("알파벳을 입력하세요 : ");
		String findTerm = scan.next();

		word.parseFiles(path + "/test");

		Map<String, Integer> result = word.calculateWordCo(docArrays, findTerm, windowSize);

		List<String> keySetList = new ArrayList<>(result.keySet());
		Collections.sort(keySetList, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return result.get(o2).compareTo(result.get(o1));
			}
		});
		
		System.out.print(findTerm + " -> ");
		for (String key : keySetList) {
			int value = result.get(key);
			System.out.print(key + ":" + value + ", ");
		}
		scan.close();
	}

	public void parseFiles(String filePath) {
		File[] files = new File(filePath).listFiles();
		BufferedReader br = null;
		try {
			for (File file : files) {
				if (file.getName().endsWith(".data")) {
					br = new BufferedReader(new FileReader(file));
					StringBuilder sb = new StringBuilder();
					String s = null;
					while ((s = br.readLine()) != null) {
						sb.append(s);
					}
					String[] tokenizedTerms = sb.toString().split(" ");
					docArrays.add(tokenizedTerms);
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Map<String, Integer> calculateWordCo(List<String[]> docArrays, String findTerm, int window_size) {
		Map<String, Integer> result = new HashMap<>();
		for (String[] array : docArrays) {
			for (int i = 0; i < array.length; i++) {
				// 일단 해당 문자의 위치 i 찾음
				if (array[i].equals(findTerm)) {
					// 윈도우 크기보다 작을 때
					if (i < window_size) {
						for (int j = 0; j < i + window_size; j++) {
							if (array[j].equals(findTerm))
								continue;
							else if (!result.containsKey(array[j])) {
								result.put(array[j], 1);
							} else {
								result.put(array[j], result.get(array[j]) + 1);
							}
						}
					} // 윈도우 크기보다 클때
					else if (window_size > array.length - i - 1) {
						for (int j = i - window_size; j < array.length; j++) {
							if (array[j].equals(findTerm))
								continue;
							else if (!result.containsKey(array[j])) {
								result.put(array[j], 1);
							} else {
								result.put(array[j], result.get(array[j]) + 1);
							}
						}
					} // 둘다 아닐때
					else {
						for (int j = i - window_size; j <= i + window_size; j++) {
							if (array[j].equals(findTerm))
								continue;
							else if (!result.containsKey(array[j])) {
								result.put(array[j], 1);
							} else {
								result.put(array[j], result.get(array[j]) + 1);
							}
						}
					}
				}
			}
		}
		return result;
	}
}
